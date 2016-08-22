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
        User user = db.find(User.class)
                .where()
                .eq("name", p.getName())
                .findUnique();
        if (Main.eq(user, null)) {
            user = db.createEntityBean(User.class);
            user.setName(p.getName());
        }
        return user;
    }

}
