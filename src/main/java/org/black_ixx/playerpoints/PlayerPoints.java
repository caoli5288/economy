package org.black_ixx.playerpoints;

import com.mengcraft.economy.sub.SubPlugin;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static com.mengcraft.economy.$.nil;

/**
 * Created on 17-6-26.
 */
public class PlayerPoints extends SubPlugin {

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
