package com.mengcraft.economy.lib;

import java.util.function.Supplier;

/**
 * Created on 16-5-23.
 */
public class Cache<T> {

    private final Supplier<T> fetcher;
    private long expire;
    private long last;
    private volatile T obj;

    public Cache(Supplier<T> fetcher) {
        this.fetcher = fetcher;
    }

    public T get() {
        return get(false);
    }

    public T get(boolean outdated) {
        if (outdated || hasOutdated()) {
            obj = fetcher.get();
            last = System.currentTimeMillis();
        }
        return obj;
    }

    public boolean hasOutdated() {
        return last + expire < System.currentTimeMillis();
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

}
