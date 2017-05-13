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
