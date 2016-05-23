package com.mengcraft.economy;

import com.avaje.ebean.EbeanServer;
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
    private final EbeanServer db;

    public MoneyManager(Main main) {
        this.main = main;
        db = main.getDatabase();
    }

    private Cache<User> getUserCache(OfflinePlayer p) {
        Cache<User> cached;
        if (cache.containsKey(p.getUniqueId())) {
            cached = cache.get(p.getUniqueId());
        } else {
            cached = new Cache<>(() -> {
                User user;
                User fetched = db.find(User.class, p.getUniqueId());
                if (fetched == null) {
                    user = db.createEntityBean(User.class);
                    user.setName(p.getName());
                    user.setId(p.getUniqueId());
                } else {
                    user = fetched;
                }
                return user;
            });
            cached.setExpire(300000);
            cache.put(p.getUniqueId(), cached);
        }
        return cached;
    }

    public void set(OfflinePlayer p, double v) {
        Cache<User> cache = getUserCache(p);
        User user = cache.get(true);
        user.setValue(round(v));
        db.save(user);
    }

    public double get(OfflinePlayer p) {
        Cache<User> cached = getUserCache(p);
        User user = cached.get();
        return user == null ? 0 : user.getValue();
    }

    public boolean has(OfflinePlayer p, double value) {
        Cache<User> cached = getUserCache(p);
        User user = cached.get(true);
        return user != null && user.getValue() >= value;
    }

    public void give(OfflinePlayer p, double value) {
        db.beginTransaction();
        try {
            Cache<User> cached = getUserCache(p);
            User user = cached.get(true);
            user.setValue(user.getValue() + round(value));
            db.save(user);
            db.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public boolean give(OfflinePlayer f, OfflinePlayer t, double v) {
        db.beginTransaction();
        try {
            Cache<User> cached = getUserCache(f);
            User from = cached.get(true);
            if (from.getValue() >= v) {
                Cache<User> cached1 = getUserCache(t);
                User to = cached1.get(true);
                from.setValue(from.getValue() - v);
                to.setValue(to.getValue() + v);

                db.save(from);
                db.save(to);
                db.commitTransaction();
                return true;
            }
        } finally {
            db.endTransaction();
        }
        return false;
    }

    public boolean take(OfflinePlayer p, double value) {
        db.beginTransaction();
        try {
            Cache<User> cached = getUserCache(p);
            User user = cached.get(true);
            double rounded = round(value);
            if (user.getValue() >= rounded) {
                user.setValue(user.getValue() - rounded);
                db.save(user);
                db.commitTransaction();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return false;
    }

    public double round(double in) {
        return new BigDecimal(in).setScale(main.getScale(), RoundingMode.DOWN).doubleValue();
    }

}
