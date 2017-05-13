package okra.model;

import lombok.Data;
import okra.base.OkraItem;
import okra.base.OkraStatus;

import java.time.LocalDateTime;

//@CompoundIndexes(
//        {
//                @CompoundIndex(def = "{'runDate':1,'status':1}", name = "i_runDate_status"),
//                @CompoundIndex(def = "{'status':1,'heartbeat':1}", name = "i_status_heartbeat"),
//                @CompoundIndex(def = "{'_id':-1,'status':1,'heartbeat':1}", name = "i_id_heartbeat_status")
//        }
//)
@Data
public class DefaultOkraItem implements OkraItem {

    private String id;

    private LocalDateTime heartbeat;

    private LocalDateTime runDate;

    private OkraStatus status;
}