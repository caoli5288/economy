package com.mengcraft.economy.entity;

import com.avaje.ebean.annotation.CreatedTimestamp;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created on 17-2-27.
 */
@Data
@Entity
@Table(name = "m_economy_log")
public class Log {

    public enum Op {

        SET,
        ADD,
    }

    @Id
    private int id;

    private String name;
    private String label;

    @Column(nullable = false)
    private Op operator;

    @Column(nullable = false)
    private double value;

    @CreatedTimestamp
    private Timestamp time;
}
