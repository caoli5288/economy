package com.mengcraft.economy;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.economy.entity.User;
import org.bukkit.OfflinePlayer;

import java.util.function.Supplier;

/**
 * Created on 16-5-24.
 */
public class CacheProvider implements Supplier<User> {

    private final OfflinePlayer p;
    private final EbeanServer db;

    public CacheProvider(Main main, OfflinePlayer p) {
        this.p = p;
        db = main.getDatabase();
    }

    @Override
    public User get() {
        User user;
        User fetched = db.find(User.class, p.getUniqueId());
        if (fetched == null) {
            user = db.createEntityBean(User.class);
            user.setId(p.getUniqueId());
            user.setName(p.getName());
        } else {
            user = fetched;
        }
        return user;
    }

}
