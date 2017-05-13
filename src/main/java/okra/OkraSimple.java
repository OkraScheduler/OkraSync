package okra;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import okra.base.AbstractOkra;
import okra.base.OkraItem;
import okra.base.OkraStatus;
import okra.exception.OkraItemNotFoundException;
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
        Document doc = new Document();
        doc.append("status", OkraStatus.PENDING.name());
        doc.append("runDate", DateUtils.localDateTimeToDate(item.getRunDate()));

        mongo
                .getDatabase(getDatabase())
                .getCollection(getCollection())
                .insertOne(doc);
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
