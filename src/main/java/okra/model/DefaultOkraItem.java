package okra.model;

import lombok.Data;
import okra.base.OkraItem;
import okra.base.OkraStatus;

import java.time.LocalDateTime;

@Data
public class DefaultOkraItem implements OkraItem {

    private String id;

    private LocalDateTime heartbeat;

    private LocalDateTime runDate;

    private OkraStatus status;
}