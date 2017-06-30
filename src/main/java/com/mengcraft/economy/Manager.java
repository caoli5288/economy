package com.mengcraft.economy;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Transaction;
import com.mengcraft.economy.entity.Log;
import com.mengcraft.economy.entity.User;
import com.mengcraft.economy.lib.Cache;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 16-3-21.
 */
public class Manager implements MyEconomy, Listener {

    private final Map<UUID, Cache<User>> handle = new ConcurrentHashMap<>();
    private final Main main;

    Manager(Main main) {
        this.main = main;
    }

    private Cache<User> fetch(OfflinePlayer p) {
        Cache<User> cached = handle.get(p.getUniqueId());
        if ($.nil(cached)) {
            EbeanServer db = main.getDatabase();
            cached = new Cache<>(() -> {
                User out = db.find(User.class, p.getUniqueId());
                if ($.nil(out)) {
                    out = db.createEntityBean(User.class);
                    out.setId(p.getUniqueId());
                    out.setName(p.getName());
                }
                return out;
            });
            cached.setExpire(300_000);
            handle.put(p.getUniqueId(), cached);
        }
        return cached;
    }

    @Override
    public double get(OfflinePlayer p) {
        return fetch(p).get().getValue();
    }

    @Override
    public void set(OfflinePlayer p, double v) {
        Cache<User> cache = fetch(p);
        User user = cache.get(true);
        double value = round(v);
        user.setValue(value);
        main.log(p, value, Log.Op.SET);
        main.getDatabase().save(user);
    }

    @Override
    public boolean has(OfflinePlayer p, double value) {
        Cache<User> cached = fetch(p);
        User user = cached.get(true);
        return !(value - user.getValue() > 0);
    }

    @Override
    public void give(OfflinePlayer p, double v) {
        if (round(v) <= 0) {
            throw new IllegalArgumentException(String.valueOf(v));
        }
        User user = fetch(p).get(true);
        double value = round(user.getValue() + v);
        user.setValue(value);
        main.log(p, value, Log.Op.ADD);
        main.getDatabase().save(user);
    }

    @Override
    public boolean take(OfflinePlayer p, double v) {
        if (!(round(v) > 0)) {
            throw new IllegalArgumentException(String.valueOf(v));
        }
        User user = fetch(p).get(true);
        double value = round(user.getValue() - v);
        boolean result = !(value < 0);
        if (result) {
            user.setValue(value);
            main.log(p, -value, Log.Op.ADD);
            main.getDatabase().save(user);
        }
        return result;
    }

    @Override
    public boolean take(OfflinePlayer from, OfflinePlayer to, double value) {
        Transaction transaction = main.getDatabase().beginTransaction();
        boolean result;
        try {
            if (result = take(from, value)) {
                give(to, value);
                transaction.commit();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            transaction.end();
        }
        return result;
    }

    public double round(double in) {
        return new BigDecimal(in).setScale(main.getScale(), RoundingMode.HALF_UP).doubleValue();
    }

    void hookQuit() {
        EventExecutor e = (l, event) -> {
            Player p = PlayerQuitEvent.class.cast(event).getPlayer();
            handle.remove(p.getUniqueId());
        };
        RegisteredListener l = new RegisteredListener(this, e, EventPriority.NORMAL, main, false);
        PlayerQuitEvent.getHandlerList().register(l);
    }

}
