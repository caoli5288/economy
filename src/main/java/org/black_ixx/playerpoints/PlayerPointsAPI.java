package org.black_ixx.playerpoints;

import lombok.val;
import org.black_ixx.playerpoints.event.PlayerPointsChangeEvent;
import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * API hook.
 */
public class PlayerPointsAPI {

    public boolean give(UUID who, int amount) {
        if (who == null) {
            return false;
        }
        val event = new PlayerPointsChangeEvent(who, amount);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            // todo
        }
        return false;
    }

    @Deprecated
    public boolean give(String name, int amount) {
        throw new UnsupportedOperationException();
    }

    public boolean take(UUID who, int amount) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public boolean take(String name, int amount) {
        throw new UnsupportedOperationException();
    }

    public int look(UUID who) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public int look(String name) {
        throw new UnsupportedOperationException();
    }

    public boolean pay(UUID who, UUID to, int amount) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public boolean pay(String name, String to, int amount) {
        throw new UnsupportedOperationException();
    }

    public boolean set(UUID who, int amount) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public boolean set(String name, int amount) {
        throw new UnsupportedOperationException();
    }

    public boolean reset(UUID who) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public boolean reset(String name, int amount) {
        throw new UnsupportedOperationException();
    }
}
