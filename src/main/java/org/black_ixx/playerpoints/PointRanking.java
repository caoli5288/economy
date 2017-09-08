package org.black_ixx.playerpoints;

import com.avaje.ebean.annotation.CreatedTimestamp;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by on 2017/9/8.
 */
@Data
@Entity
@EqualsAndHashCode(of = "id")
public class PointRanking {

    @Id
    private UUID id;
    private String name;
    private int income;
    private int consume;

    @CreatedTimestamp
    private Timestamp latestUpdate;
}
