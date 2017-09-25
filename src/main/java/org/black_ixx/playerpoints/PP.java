package org.black_ixx.playerpoints;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

import static org.black_ixx.playerpoints.PP.TABLE_NAME;

/**
 * Created on 17-6-26.
 */
@Data
@Entity
@EqualsAndHashCode(of = "who")
@Table(name = TABLE_NAME)
public class PP {

    public static final String TABLE_NAME = "playerpoints";

    @Id
    @Column(name = "playername")
    private UUID who;

    @Column(name = "points", nullable = false)
    private int value;

    @Column(nullable = false)
    private int extra;

    public int getAll() {
        return getValue() + getExtra();
    }

}
