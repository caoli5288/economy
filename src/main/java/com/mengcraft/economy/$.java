package com.mengcraft.economy;

/**
 * Created on 17-1-24.
 */
public final class $ {

    private $() {
        throw new IllegalStateException("$");
    }

    public static boolean nil(Object i) {
        return i == null;
    }

    public static boolean eq(Object i, Object j) {
        return i == j || (!nil(i) && i.equals(j));
    }

}
