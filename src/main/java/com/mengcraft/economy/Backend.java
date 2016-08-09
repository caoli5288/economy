package com.mengcraft.economy;

import org.bukkit.Bukkit;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * Created on 16-7-10.
 */
public class Backend extends Thread {
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private boolean shutdown;

    public Backend() {
        super("economy-backend-thread");
    }

    @Override
    public void run() {
        while (!(shutdown && queue.isEmpty())) {
            try {
                Runnable take = queue.take();
                if (!Main.eq(take, null)) {
                    take.run();
                }
            } catch (InterruptedException ignored) {
            } catch (Exception e) {// Log exception except interrupt.
                Bukkit.getLogger().throwing("", "", e);
            }
        }
    }

    public void submit(Runnable runnable) {
        queue.offer(runnable);
    }

    public void shutdown() {
        shutdown = true;
        interrupt();
    }
}
