package org.black_ixx.playerpoints;

import com.kuai8.mc.kuai8point.Kuai8Point;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class K8 implements Listener {

    private final Plugin upstream;

    public int look(String name) {
        return ((Kuai8Point) upstream).k().a(name);
    }

    public boolean take(String name, int value) {
        return ((Kuai8Point) upstream).k().b(name, value);
    }

    public boolean give(String name, int value) {
        return ((Kuai8Point) upstream).k().a(name, value);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handle(AsyncPlayerPreLoginEvent event) {
        AsyncPlayerPreLoginEvent.Result loginResult = event.getLoginResult();
        if (loginResult == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            String player = event.getName();
            int value = look(player);
            if (!(value < 1)) {
                take(player, value);
                PlayerPointsAPI.inst.give(event.getUniqueId(), value);
            }
        }
    }

}
