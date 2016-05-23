package com.mengcraft.economy.lib;

import net.milkbowl.vault.economy.EconomyResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created on 16-3-21.
 */
public class VaultResponse extends EconomyResponse {

    private final Future<Boolean> future;

    public VaultResponse(Future<Boolean> future) {
        super(0, 0, null, null);
        this.future = future;
    }

    @Override
    public boolean transactionSuccess() {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Future<Boolean> getFuture() {
        return future;
    }

}
