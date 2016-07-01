package com.mengcraft.economy;

import com.mengcraft.economy.entity.User;
import com.mengcraft.economy.lib.Cache;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 16-3-21.
 */
public class MoneyManager {

    private final Map<UUID, Cache<User>> cache = new ConcurrentHashMap<>();
    private final Main main;

    public MoneyManager(Main main) {
        this.main = main;
    }

    private Cache<User> getUserCache(OfflinePlayer p) {
        Cache<User> cached;
        if (cache.containsKey(p.getUniqueId())) {
            cached = cache.get(p.getUniqueId());
        } else {
            cached = new Cache<>(new CacheProvider(main, p));
            cached.setExpire(300000);
            cache.put(p.getUniqueId(), cached);
        }
        return cached;
    }

    public void set(OfflinePlayer p, double v) {
        Cache<User> cache = getUserCache(p);
        User user = cache.get(true);
        user.setValue(round(v));
        main.getDatabase().save(user);
    }

    public double get(OfflinePlayer p) {
        User user = getUserCache(p).get();
        return user == null ? 0 : user.getValue();
    }

    public boolean has(OfflinePlayer p, double value) {
        Cache<User> cached = getUserCache(p);
        User user = cached.get(true);
        return user != null && user.getValue() >= value;
    }

    public void give(OfflinePlayer p, double value) {
        main.getDatabase().beginTransaction();
        try {
            User user = getUserCache(p).get(true);
            user.setValue(new BigDecimal(user.getValue()).add(new BigDecimal(round(value))).setScale(main.getScale(), RoundingMode.HALF_DOWN).doubleValue());
            main.getDatabase().save(user);
            main.getDatabase().commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            main.getDatabase().endTransaction();
        }
    }

    public boolean give(OfflinePlayer f, OfflinePlayer t, double v) {
        main.getDatabase().beginTransaction();
        double rounded = round(v);
        try {
            User from = getUserCache(f).get(true);
            boolean b = from.getValue() >= v;
            if (b) {
                from.setValue(new BigDecimal(from.getValue()).subtract(new BigDecimal(rounded)).setScale(main.getScale(), RoundingMode.HALF_DOWN).doubleValue());
                main.getDatabase().save(from);

                User to = getUserCache(t).get(true);
                to.setValue(new BigDecimal(to.getValue()).add(new BigDecimal(rounded)).setScale(main.getScale(), RoundingMode.HALF_DOWN).doubleValue());

                main.getDatabase().save(to);
                main.getDatabase().commitTransaction();
            }
            return b;
        } finally {
            main.getDatabase().endTransaction();
        }
    }

    public boolean take(OfflinePlayer p, double value) {
        main.getDatabase().beginTransaction();
        try {
            Cache<User> cached = getUserCache(p);
            User user = cached.get(true);
            double rounded = round(value);
            boolean b = user.getValue() >= rounded;
            if (b) {
                user.setValue(new BigDecimal(user.getValue()).subtract(new BigDecimal(rounded)).setScale(main.getScale(), RoundingMode.HALF_DOWN).doubleValue());
                main.getDatabase().save(user);
                main.getDatabase().commitTransaction();
            }
            return b;
        } finally {
            main.getDatabase().endTransaction();
        }
    }

    public double round(double in) {
        return new BigDecimal(in).setScale(main.getScale(), RoundingMode.HALF_DOWN).doubleValue();
    }

}
