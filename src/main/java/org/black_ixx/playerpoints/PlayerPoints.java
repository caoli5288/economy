package org.black_ixx.playerpoints;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.economy.sub.SubPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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
    }

    @Override
    public boolean onCommand(CommandSender who, Command command, String label, String[] input) {
        return CommandExec.exec(this, who, input);
    }

}
