package org.black_ixx.playerpoints.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Called when a player's points is to be changed.
 */
public class PlayerPointsChangeEvent extends PlayerPointsEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private int change;

    public PlayerPointsChangeEvent(UUID who, int change) {
        super(who, change);
        this.change = change;
    }

    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
