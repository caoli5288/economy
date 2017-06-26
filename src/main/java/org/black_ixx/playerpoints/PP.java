package org.black_ixx.playerpoints;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

import static org.black_ixx.playerpoints.PP.TABLE_NAME;

/**
 * Created on 17-6-26.
 */
@Data
@Entity
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PP)) return false;
        PP pp = (PP) o;
        return Objects.equals(getWho(), pp.getWho());
    }

    @Override
    public int hashCode() {
        return getWho().hashCode();
    }

    public int getAll() {
        return getValue() + getExtra();
    }

}
