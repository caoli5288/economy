package com.mengcraft.economy;

import org.bukkit.OfflinePlayer;

/**
 * Created on 16-9-20.
 */
public interface MyEconomy {

    void set(OfflinePlayer p, double value);

    double get(OfflinePlayer p);

    boolean has(OfflinePlayer p, double value);

    void give(OfflinePlayer p, double value);

    boolean take(OfflinePlayer p, double value);

    boolean take(OfflinePlayer from, OfflinePlayer to, double value);
}
