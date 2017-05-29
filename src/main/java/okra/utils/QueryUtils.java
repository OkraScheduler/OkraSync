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

package okra.utils;

import com.mongodb.BasicDBObject;
import okra.base.model.OkraStatus;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.util.Date;

import static com.mongodb.client.model.Filters.or;

public final class QueryUtils {

    public static Document generateRunDateQueryPart() {
        Document runDateQuery = new Document();
        runDateQuery.put("status", OkraStatus.PENDING.name());
        runDateQuery.put("runDate", new BasicDBObject("$lt", DateUtils.localDateTimeToDate(LocalDateTime.now())));
        return runDateQuery;
    }

    public static Document generateStatusProcessingAndHeartbeatExpiredQuery(final long secondsToGetExpired) {
        Document statusProcessingAndHeartbeatExpired = new Document();
        statusProcessingAndHeartbeatExpired.put("status", OkraStatus.PROCESSING.name());
        statusProcessingAndHeartbeatExpired.put("heartbeat", getExpiredHeartbeatDate(secondsToGetExpired));
        return statusProcessingAndHeartbeatExpired;
    }

    private static Document generateStatusProcessingAndHeartbeatNullQuery() {
        Document statusProcessingAndHeartbeatNull = new Document();
        statusProcessingAndHeartbeatNull.put("status", OkraStatus.PROCESSING.name());
        statusProcessingAndHeartbeatNull.put("heartbeat", null);
        return statusProcessingAndHeartbeatNull;
    }

    public static Bson generatePeekQuery(final long secondsToGetExpired) {
        return or(
                QueryUtils.generateRunDateQueryPart(),
                QueryUtils.generateStatusProcessingAndHeartbeatExpiredQuery(secondsToGetExpired),
                QueryUtils.generateStatusProcessingAndHeartbeatNullQuery());
    }

    private static Date getExpiredHeartbeatDate(final long secondsToGetExpired) {
        return DateUtils.localDateTimeToDate(LocalDateTime.now().minusSeconds(secondsToGetExpired));
    }

}
