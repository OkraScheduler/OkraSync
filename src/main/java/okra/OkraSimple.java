/*
 * Copyright (c) 2017 Okra Scheduler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package okra;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import okra.base.AbstractOkra;
import okra.base.OkraItem;
import okra.base.OkraStatus;
import okra.exception.InvalidOkraItemException;
import okra.exception.OkraItemNotFoundException;
import okra.index.IndexCreator;
import okra.utils.DateUtils;
import okra.utils.QueryUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class OkraSimple<T extends OkraItem> extends AbstractOkra<T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(OkraSimple.class);

    private final MongoClient mongo;
    private final Class<T> scheduleItemClass;
    private final long defaultHeartbeatExpirationMillis;

    public OkraSimple(MongoClient mongo,
                      String database,
                      String collection,
                      Class<T> scheduleItemClass,
                      final long defaultHeartbeatExpiration,
                      final TimeUnit defaultHeartbeatExpirationUnit) {
        super(database, collection);
        this.mongo = mongo;
        this.scheduleItemClass = scheduleItemClass;
        this.defaultHeartbeatExpirationMillis = defaultHeartbeatExpirationUnit.toMillis(defaultHeartbeatExpiration);
        setup();
    }

    @Override
    public void setup() {
        IndexCreator.ensureIndexes(this, mongo, getDatabase(), getCollection());
    }

    @Override
    public Optional<T> poll() {
        Optional<T> result = peek();

        result.ifPresent(this::delete);

        return result;
    }

    @Override
    public Optional<T> peek() {
        BasicDBObject peekQuery = QueryUtils.generatePeekQuery(defaultHeartbeatExpirationMillis);

        BasicDBObject update = new BasicDBObject();
        update.put("heartbeat", new Date());
        update.put("status", OkraStatus.PROCESSING.name());

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);

        Document result = mongo
                .getDatabase(getDatabase())
                .getCollection(getCollection())
                .findOneAndUpdate(peekQuery, new BasicDBObject("$set", update), options);

        if (result == null) {
            return Optional.empty();
        }

        try {
            ObjectId objId = result.getObjectId("_id");
            T okraItem = scheduleItemClass.newInstance();
            okraItem.setId(objId.toString());

            Date heartBeat = result.getDate("heartbeat");
            okraItem.setHeartbeat(dateToLocalDateTime(heartBeat));

            Date runDate = result.getDate("runDate");
            okraItem.setRunDate(dateToLocalDateTime(runDate));

            String status = result.getString("status");
            okraItem.setStatus(OkraStatus.valueOf(status));

            return Optional.of(okraItem);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("Error initializing Okra Item instance", e);
        }

        return Optional.empty();
    }

    private LocalDateTime dateToLocalDateTime(Date heartBeat) {
        return LocalDateTime.ofInstant(heartBeat.toInstant(), ZoneId.systemDefault());
    }

    @Override
    public T retrieve() throws OkraItemNotFoundException {
        Optional<T> result = peek();
        return null;
    }

    @Override
    public Optional<T> reschedule(T item) {
        return null;
    }

    @Override
    public Optional<T> heartbeat(T item) {
        return null;
    }

    @Override
    public Optional<T> heartbeatAndUpdateCustomAttrs(T item, Map<String, Object> attrs) {
        return null;
    }

    @Override
    public void delete(T item) {
        ObjectId id = new ObjectId(item.getId());
        BasicDBObject idQuery = new BasicDBObject("_id", id);
        mongo.getDatabase(getDatabase()).getCollection(getCollection()).deleteOne(idQuery);
    }

    @Override
    public void schedule(T item) {

        validateSchedule(item);

        Document doc = new Document();
        doc.append("status", OkraStatus.PENDING.name());
        doc.append("runDate", DateUtils.localDateTimeToDate(item.getRunDate()));

        mongo
                .getDatabase(getDatabase())
                .getCollection(getCollection())
                .insertOne(doc);
    }

    private void validateSchedule(T item) {
        if (item == null
                || item.getRunDate() == null
                || item.getId() != null) {
            throw new InvalidOkraItemException();
        }
    }

    @Override
    public long countByStatus(OkraStatus status) {
        BasicDBObject query = new BasicDBObject("status", status.name());

        return mongo
                .getDatabase(getDatabase())
                .getCollection(getCollection())
                .count(query);
    }

}
