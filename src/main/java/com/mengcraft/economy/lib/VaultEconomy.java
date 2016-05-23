package com.mengcraft.economy.lib;

import com.mengcraft.economy.Main;
import com.mengcraft.economy.MoneyManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created on 16-3-21.
 */
public class VaultEconomy implements Economy {

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            0,
            2147483647,
            60L,
            TimeUnit.SECONDS,
            new SynchronousQueue<>()
    );
    private final Main main;
    private final MoneyManager manager;

    public VaultEconomy(Main main, MoneyManager manager) {
        this.main = main;
        this.manager = manager;
    }

    @Override
    public boolean isEnabled() {
        return main.getConfig().getBoolean("vault.enable");
    }

    @Override
    public String getName() {
        return "Economy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return main.getScale();
    }

    @Override
    public String format(double v) {
        return new BigDecimal(v).setScale(fractionalDigits(), RoundingMode.DOWN).toString();
    }

    @Override
    public String currencyNamePlural() {
        return main.getConfig().getString("vault.unit.plural");
    }

    @Override
    public String currencyNameSingular() {
        return main.getConfig().getString("vault.unit.singular");
    }

    @Override
    public boolean hasAccount(String s) {
        return true;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return true;
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return true;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return true;
    }

    @Override
    public double getBalance(String s) {
        return getBalance(main.getServer().getOfflinePlayer(s));
    }

    @Override
    public double getBalance(OfflinePlayer p) {
        return manager.get(p);
    }

    @Override
    public double getBalance(String s, String s1) {
        return getBalance(main.getServer().getOfflinePlayer(s));
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(main.getServer().getOfflinePlayer(s));
    }

    @Override
    public boolean has(String s, double v) {
        return has(main.getServer().getOfflinePlayer(s), v);
    }

    @Override
    public boolean has(OfflinePlayer p, double v) {
        return manager.has(p, v);
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return has(main.getServer().getOfflinePlayer(s), v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return has(main.getServer().getOfflinePlayer(s), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        return withdrawPlayer(main.getServer().getOfflinePlayer(s), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer p, double v) {
        return new VaultResponse(executor.submit(() -> manager.take(p, v)));
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return withdrawPlayer(main.getServer().getOfflinePlayer(s), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(main.getServer().getOfflinePlayer(s), v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        return depositPlayer(main.getServer().getOfflinePlayer(s), v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer p, double v) {
        manager.give(p, v);
        return new EconomyResponse(0, 0, null, null) {
            @Override
            public boolean transactionSuccess() {
                return true;
            }
        };
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return depositPlayer(main.getServer().getOfflinePlayer(s), v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(main.getServer().getOfflinePlayer(s), v);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getBanks() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return true;
    }

}
