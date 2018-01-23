package org.black_ixx.playerpoints;

import com.avaje.ebean.EbeanServer;
import com.kuai8.mc.kuai8point.Kuai8Point;
import com.mengcraft.economy.sub.SubPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created on 17-6-26.
 */
public class PlayerPoints extends SubPlugin {

    private EbeanServer database;

    @Override
    public EbeanServer getDatabase() {
        return database;
    }

    public void setDatabase(EbeanServer database) {
        this.database = database;
    }

    public PlayerPointsAPI getAPI() {
        return PlayerPointsAPI.inst;
    }

    @Override
    public void onEnable() {
        PlayerPointsAPI.init(this, getDatabase());
        try {
            Kuai8Point plugin = JavaPlugin.getPlugin(Kuai8Point.class);
            Bukkit.getPluginManager().registerEvents(new K8(plugin), getParent());
            getLogger().info("Hook kuaiba point plugin");
        } catch (Exception ignore) {
            getLogger().info("Kuaiba point plugin NOT found");
        }
    }

    @Override
    public boolean onCommand(CommandSender who, Command command, String label, String[] input) {
        return CommandExec.exec(this, who, input);
    }

}
