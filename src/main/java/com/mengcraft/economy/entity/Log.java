package com.mengcraft.economy.entity;

import com.avaje.ebean.annotation.CreatedTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created on 17-2-27.
 */
@Entity
@Table(name = "m_economy_log")
public class Log {

    @Id
    private int id;

    private String name;
    private double value;

    @CreatedTimestamp
    private Timestamp time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

}
