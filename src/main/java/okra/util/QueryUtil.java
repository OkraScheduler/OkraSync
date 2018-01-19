/*
 * Copyright (c) 2018 Okra Scheduler
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
package okra.util;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import okra.base.model.OkraStatus;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;

public final class QueryUtil {

    private QueryUtil() {
    }

    public static Document generateRunDateQueryPart() {
        final Document query = new Document();
        query.put("status", OkraStatus.PENDING.name());
        query.put("runDate", new BasicDBObject("$lt", DateUtil.toDate(LocalDateTime.now())));
        return query;
    }

    public static Document generateStatusProcessingAndHeartbeatExpiredQuery(final long secondsToGetExpired) {
        final Document query = new Document();
        query.put("status", OkraStatus.PROCESSING.name());
        query.put("heartbeat", DateUtil.nowMinusSeconds(secondsToGetExpired));
        return query;
    }

    private static Document generateStatusProcessingAndHeartbeatNullQuery() {
        final Document query = new Document();
        query.put("status", OkraStatus.PROCESSING.name());
        query.put("heartbeat", null);
        return query;
    }

    public static Bson generatePeekQuery(final long secondsToGetExpired) {
        return Filters.or(
                QueryUtil.generateRunDateQueryPart(),
                QueryUtil.generateStatusProcessingAndHeartbeatExpiredQuery(secondsToGetExpired),
                QueryUtil.generateStatusProcessingAndHeartbeatNullQuery());
    }
}