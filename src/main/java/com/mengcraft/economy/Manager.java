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
public class Manager implements MyEconomy {

    private final Map<UUID, Cache<User>> cache = new ConcurrentHashMap<>();
    private final Main main;

    public Manager(Main main) {
        this.main = main;
    }

    private Cache<User> fetch(OfflinePlayer p) {
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
        Cache<User> cache = fetch(p);
        User user = cache.get(true);
        user.setValue(round(v));
        main.getDatabase().save(user);
    }

    public double get(OfflinePlayer p) {
        return fetch(p).get().getValue();
    }

    public boolean has(OfflinePlayer p, double value) {
        Cache<User> cached = fetch(p);
        User user = cached.get(true);
        return !(value - user.getValue() > 0);
    }

    public void give(OfflinePlayer p, double value) {
        if (!(round(value) > 0)) {
            throw new IllegalArgumentException(String.valueOf(value));
        }
        User user = fetch(p).get(true);
        user.setValue(round(user.getValue() + value));
        main.getDatabase().save(user);
    }

    public boolean take(OfflinePlayer p, double value) {
        if (!(round(value) > 0)) {
            throw new IllegalArgumentException(String.valueOf(value));
        }
        User user = fetch(p).get(true);
        double newValue = user.getValue() - value;
        boolean result = !(newValue < 0);
        if (result) {
            user.setValue(round(newValue));
            main.getDatabase().save(user);
        }
        return result;
    }

    public double round(double in) {
        return new BigDecimal(in).setScale(main.getScale(), RoundingMode.HALF_DOWN).doubleValue();
    }

}
