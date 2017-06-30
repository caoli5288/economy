package com.mengcraft.economy.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Created on 16-3-21.
 */
@Data
@EqualsAndHashCode(exclude = {"name", "value"})
@Entity
@Table(name = "m_economy")
public class User {

    @Id
    private UUID id;
    private String name;
    private double value;
}
