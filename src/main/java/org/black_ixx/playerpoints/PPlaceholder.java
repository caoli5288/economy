package org.black_ixx.playerpoints;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

/**
 * Created by on 2017/7/8.
 */
public class PPlaceholder extends EZPlaceholderHook {

    private final Plugin plugin;

    interface IExec {

        String apply(PlayerPointsAPI api, Player p);
    }

    enum Sub {

        ALL((api, p) -> "" + api.lookAll(p.getUniqueId())),
        EXTRA((api, p) -> "" + api.lookExtra(p.getUniqueId()));

        private final IExec exec;

        Sub(IExec exec) {
            this.exec = exec;
        }

        String apply(PlayerPointsAPI api, Player p) {
            return exec.apply(api, p);
        }
    }

    public PPlaceholder(Plugin plugin) {
        super(plugin, "pp");
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player p, String label) {
        try {
            return Sub.valueOf(label.toUpperCase()).apply(PlayerPointsAPI.inst, p);
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, e.toString(), e);
        }
        return null;
    }

}
