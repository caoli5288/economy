package com.mengcraft.economy;

import com.avaje.ebean.EbeanServer;
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

    public Manager(Main main) {
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
        user.setValue(round(v));
        main.getDatabase().save(user);
    }

    @Override
    public boolean has(OfflinePlayer p, double value) {
        Cache<User> cached = fetch(p);
        User user = cached.get(true);
        return !(value - user.getValue() > 0);
    }

    @Override
    public void give(OfflinePlayer p, double value) {
        if (!(round(value) > 0)) {
            throw new IllegalArgumentException(String.valueOf(value));
        }
        User user = fetch(p).get(true);
        user.setValue(round(user.getValue() + value));
        main.getDatabase().save(user);
    }

    @Override
    public boolean take(OfflinePlayer p, double value) {
        if (!(round(value) > 0)) {
            throw new IllegalArgumentException(String.valueOf(value));
        }
        User user = fetch(p).get(true);
        double newValue = round(user.getValue() - value);
        boolean result = !(newValue < 0);
        if (result) {
            user.setValue(newValue);
            main.getDatabase().save(user);
        }
        return result;
    }

    @Override
    public boolean take(OfflinePlayer from, OfflinePlayer to, double value) {
        EbeanServer db = main.getDatabase();
        db.beginTransaction();
        try {
            if (take(from, value)) {
                give(to, value);
                db.commitTransaction();
                return true;// hope this transaction work
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            db.endTransaction();
        }
        return false;
    }

    public double round(double in) {
        return new BigDecimal(in).setScale(main.getScale(), RoundingMode.HALF_UP).doubleValue();
    }

    public void hookQuit() {
        EventExecutor e = (l, event) -> {
            Player p = PlayerQuitEvent.class.cast(event).getPlayer();
            handle.remove(p.getUniqueId());
        };
        RegisteredListener l = new RegisteredListener(this, e, EventPriority.NORMAL, main, false);
        PlayerQuitEvent.getHandlerList().register(l);
    }

}
