package org.black_ixx.playerpoints.event;

import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerPointsResetEvent extends PlayerPointsEvent {

    /**
     * Handler list.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public PlayerPointsResetEvent(UUID who) {
        super(who, -1);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public int getChange() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChange(int change) {
        throw new UnsupportedOperationException();
    }

}
