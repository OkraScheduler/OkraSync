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

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import okra.base.OkraStatus;

import java.time.LocalDateTime;
import java.util.Date;

public class QueryUtils {

    public static BasicDBObject generateRunDateQueryPart() {
        BasicDBObject runDateQuery = new BasicDBObject();
        runDateQuery.put("status", OkraStatus.PENDING.name());
        runDateQuery.put("runDate", new BasicDBObject("$lt", DateUtils.localDateTimeToDate(LocalDateTime.now())));
        return runDateQuery;
    }

    public static BasicDBObject generateHeartbeatExpiredOrNullQueryPart(long secondsToGetExpired) {
        BasicDBList heartBeatOrNullValues = new BasicDBList();
        heartBeatOrNullValues.add(new BasicDBObject("heartbeat", getExpiredHeartbeatDate(secondsToGetExpired)));
        heartBeatOrNullValues.add(new BasicDBObject("heartbeat", null));
        return new BasicDBObject("$or", heartBeatOrNullValues);
    }

    public static BasicDBObject generateStatusProcessingAndExpiredHeartbeatQuery(long secondsToGetExpired) {
        BasicDBList heartbeatQueryAndValues = new BasicDBList();
        heartbeatQueryAndValues.add(new BasicDBObject("status", OkraStatus.PROCESSING.name()));
        heartbeatQueryAndValues.add(QueryUtils.generateHeartbeatExpiredOrNullQueryPart(secondsToGetExpired));
        return new BasicDBObject("$and", heartbeatQueryAndValues);
    }

    public static BasicDBObject generatePeekQuery(long secondsToGetExpired) {
        BasicDBList orValues = new BasicDBList();
        orValues.add(QueryUtils.generateRunDateQueryPart());
        orValues.add(QueryUtils.generateStatusProcessingAndExpiredHeartbeatQuery(secondsToGetExpired));
        return new BasicDBObject("$or", orValues);
    }

    private static Date getExpiredHeartbeatDate(long secondsToGetExpired) {
        return DateUtils.localDateTimeToDate(LocalDateTime.now().minusSeconds(secondsToGetExpired));
    }

}
