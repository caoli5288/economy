package org.black_ixx.playerpoints;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.economy.entity.Log;
import com.mengcraft.simpleorm.DatabaseException;
import com.mengcraft.simpleorm.EbeanManager;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by on 2017/7/8.
 */
public class PlayerPoints extends JavaPlugin {

    private EbeanServer database;

    @Override
    public void onEnable() {
        val db = EbeanManager.DEFAULT.getHandler(this);
        if (db.isNotInitialized()) {
            db.define(Log.class);
            db.define(PP.class);
            db.define(PointRanking.class);
            try {
                db.initialize();
            } catch (DatabaseException e) {
                throw new RuntimeException(e);
            }
            db.install();
            db.reflect();
        }
        database = db.getServer();
        PlayerPointsAPI.init(this, database);

        Plugin plugin = Bukkit.getPluginManager().getPlugin("KPointx");
        if (!(plugin == null)) {
            Bukkit.getPluginManager().registerEvents(new K8(plugin), this);
            getLogger().info("Hook kuaiba point plugin");
        }

    }

    @Override
    public boolean onCommand(CommandSender who, Command command, String label, String[] input) {
        return CommandExec.exec(this, who, input);
    }

    @Override
    public EbeanServer getDatabase() {
        return database;
    }

    public PlayerPointsAPI getAPI() {
        return PlayerPointsAPI.inst;
    }

}
