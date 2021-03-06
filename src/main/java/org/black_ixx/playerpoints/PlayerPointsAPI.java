package org.black_ixx.playerpoints;

import com.avaje.ebean.EbeanServer;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mengcraft.economy.$;
import com.mengcraft.economy.entity.Log;
import lombok.SneakyThrows;
import lombok.val;
import org.black_ixx.playerpoints.event.PlayerPointsChangeEvent;
import org.black_ixx.playerpoints.event.PlayerPointsResetEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.mengcraft.economy.$.nil;

/**
 * API hook.
 */
public final class PlayerPointsAPI {

    private final EbeanServer db;
    static PlayerPointsAPI inst;

    private final Cache<UUID, PointRanking> rc = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .initialCapacity(Bukkit.getMaxPlayers() << 1)
            .build();

    private PlayerPointsAPI(EbeanServer db) {
        this.db = db;
    }

    public static void init(Plugin plugin, EbeanServer db) {
        $.thr(!$.nil(inst), "init");
        inst = new PlayerPointsAPI(db);
        if (!nil(Bukkit.getPluginManager().getPlugin("PlaceholderAPI"))) {
            val placeholder = new PPlaceholder(plugin);
            placeholder.hook();
            plugin.getLogger().info("Hook to PlaceholderAPI okay");
        }
    }

    public boolean giveExtra(UUID who, int value) {
        $.thr(nil(who), "nil");
        val event = new PlayerPointsChangeEvent(who, value, true);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        val update = db.createUpdate(PP.class, "update " + PP.TABLE_NAME + " set extra = extra + :value where who = :who")
                .set("who", who)
                .set("value", event.getChange());
        int result = update.execute();
        if (result == 0) {
            PP p = new PP();
            p.setWho(who);
            p.setExtra(event.getChange());
            db.save(p);
            // Thr exception here if op not okay
        }

        log(Bukkit.getOfflinePlayer(who), event.getChange(), "point_ext", Log.Op.ADD_EXTRA);

        return true;
    }

    public boolean give(UUID who, int value) {
        $.thr(nil(who), "nil");
        val event = new PlayerPointsChangeEvent(who, value);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        val update = db.createUpdate(PP.class, "update " + PP.TABLE_NAME + " set value = value + :value where who = :who")
                .set("who", who)
                .set("value", event.getChange());
        int result = update.execute();
        if (result == 0) {
            PP p = new PP();
            p.setWho(who);
            p.setValue(event.getChange());
            db.insert(p);
            // Thr exception here if op not okay
        }

        log(Bukkit.getOfflinePlayer(who), event.getChange(), "point", Log.Op.ADD);

        return true;
    }

    @Deprecated
    public boolean give(String name, int amount) {
        val p = Bukkit.getPlayerExact(name);
        $.thr(nil(p), "offline");
        return give(p.getUniqueId(), amount);
    }

    public boolean take(UUID who, int amount) {
        return take(who, amount, true);
    }

    public boolean take(UUID who, int amount, boolean b) {
        $.thr(nil(who), "nil");

        val event = new PlayerPointsChangeEvent(who, -amount, b);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled() || event.getChange() == 0) {
            return false;
        }

        val point = db.find(PP.class, who);
        if (nil(point)) return false;

        val value = new ValueBox(-event.getChange());

        if (b && point.getExtra() > 0) {
            int i = point.getExtra() - value.i;
            if (i > -1) {
                point.setExtra(i);
                db.update(point);
                log(Bukkit.getOfflinePlayer(who), -value.i, "point_ext", Log.Op.ADD_EXTRA);
                return true;
            }
            value.ext = point.getExtra();
            value.i -= point.getExtra();
            point.setExtra(0);
        }

        int i = point.getValue() - value.i;
        if (i > -1) {
            point.setValue(i);
            db.update(point);
            log(Bukkit.getOfflinePlayer(who), -value.i, "point", Log.Op.ADD);
            if (!(value.ext == 0)) {
                log(Bukkit.getOfflinePlayer(who), -value.ext, "point_ext", Log.Op.ADD_EXTRA);
            }
            return true;
        }

        return false;
    }

    @Deprecated
    public boolean take(String name, int amount) {
        val p = Bukkit.getPlayerExact(name);
        $.thr(nil(p), "offline");
        return take(p.getUniqueId(), amount);
    }

    public int lookAll(UUID who) {
        val p = db.find(PP.class, who);
        if (nil(p)) return 0;
        return p.getAll();
    }

    public int look(UUID who) {
        $.thr(nil(who), "nil");
        val find = db.find(PP.class, who);
        if (!nil(find)) {
            return find.getValue();
        }
        return 0;
    }

    public int lookExtra(UUID who) {
        $.thr(nil(who), "nil");
        val find = db.find(PP.class, who);
        if (!nil(find)) {
            return find.getExtra();
        }
        return 0;
    }

    @Deprecated
    public int look(String name) {
        val p = Bukkit.getPlayerExact(name);
        $.thr(nil(p), "offline");
        return look(p.getUniqueId());
    }

    public boolean pay(UUID who, UUID to, int amount) {
        return take(who, amount, false) && give(to, amount);
    }

    @Deprecated
    public boolean pay(String name, String to, int amount) {
        val p = Bukkit.getPlayerExact(name);
        val i = Bukkit.getPlayerExact(to);
        $.thr(nil(p) || nil(i), "offline");
        return pay(p.getUniqueId(), i.getUniqueId(), amount);
    }

    public boolean set(UUID who, int amount) {
        $.thr(nil(who), "nil");
        val event = new PlayerPointsResetEvent(who);
        event.setChange(amount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        val update = db.createUpdate(PP.class, "update " + PP.TABLE_NAME + " set value = :value where who = :who")
                .set("who", who)
                .set("value", event.getChange());
        int result = update.execute();
        if (result == 0) {
            PP p = new PP();
            p.setWho(who);
            p.setValue(event.getChange());
            db.insert(p);
        }

        log(Bukkit.getOfflinePlayer(who), event.getChange(), "point", Log.Op.SET);

        return true;
    }

    @Deprecated
    public boolean set(String name, int amount) {
        val p = Bukkit.getPlayerExact(name);
        $.thr(nil(p), "offline");
        return set(p.getUniqueId(), amount);
    }

    public boolean reset(UUID who) {
        return set(who, 0);
    }

    @Deprecated
    public boolean reset(String name, int amount) {
        val p = Bukkit.getPlayerExact(name);
        $.thr(nil(p), "offline");
        return set(p.getUniqueId(), amount);
    }

    public void log(OfflinePlayer p, int value, String label, Log.Op operator) {
        if (operator == Log.Op.ADD) {
            int d = Math.abs(value);
            logRank(p, d == value, d);
        }
        val inst = new Log();
        inst.setName(p.getName());
        inst.setValue(value);
        inst.setOperator(operator);
        inst.setLabel(label);
        db.insert(inst);
    }

    public void logRank(OfflinePlayer p, boolean b, int value) {
        val column = b ? "income" : "consume";
        val id = p.getUniqueId();
        val sql = db.createUpdate(PointRanking.class, "update point_ranking set " + column + " = " + column + " + :value , latest_update = now() where id = :id");
        sql.setParameter("value", value);
        sql.setParameter("id", id);
        int row = sql.execute();
        if (row == 0) {
            val inst = new PointRanking();
            inst.setId(id);
            inst.setName(p.getName());
            if (b) {
                inst.setIncome(value);
            } else {
                inst.setConsume(value);
            }
            db.insert(inst);
        }
        rc.invalidate(id);
    }

    /**
     * @param p the player's id
     * @return {@link PointRanking} object in cache pool
     */
    @SneakyThrows
    public PointRanking getRank(OfflinePlayer p) {
        return rc.get(p.getUniqueId(), () -> {
            val ranking = db.find(PointRanking.class, p.getUniqueId());
            return ranking == null ? PointRanking.EMPTY : ranking;
        });
    }

}
