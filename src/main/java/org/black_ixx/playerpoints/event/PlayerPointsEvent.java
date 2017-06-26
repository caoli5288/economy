package org.black_ixx.playerpoints.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.UUID;

public abstract class PlayerPointsEvent extends Event implements Cancellable {

    private final UUID who;
    private boolean cancelled;

    public PlayerPointsEvent(UUID who, int change) {
        this.who = who;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public UUID getPlayerId() {
        return who;
    }

    public abstract int getChange();

    public abstract void setChange(int change);
}
