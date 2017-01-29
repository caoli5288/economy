package com.mengcraft.economy.lib;

import com.mengcraft.economy.Main;
import com.mengcraft.economy.Manager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.List;

/**
 * Created on 16-3-21.
 */
public class VaultEconomy implements Economy {

    private final Main main;
    private final Manager manager;

    public VaultEconomy(Main main, Manager manager) {
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
    public String format(double value) {
        return String.valueOf(manager.round(value));
    }

    @Override
    public String currencyNamePlural() {
        return main.getPlural();
    }

    @Override
    public String currencyNameSingular() {
        return main.getSingular();
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
    public double getBalance(String name) {
        return getBalance(main.getServer().getOfflinePlayer(name));
    }

    @Override
    public double getBalance(OfflinePlayer p) {
        return manager.get(p);
    }

    @Override
    public double getBalance(String name, String s1) {
        return getBalance(name);
    }

    @Override
    public double getBalance(OfflinePlayer p, String s) {
        return getBalance(p);
    }

    @Override
    public boolean has(OfflinePlayer p, double v) {
        return manager.has(p, v);
    }

    @Override
    public boolean has(String name, double v) {
        return has(main.getServer().getOfflinePlayer(name), v);
    }

    @Override
    public boolean has(String name, String s1, double value) {
        return has(name, value);
    }

    @Override
    public boolean has(OfflinePlayer p, String s, double v) {
        return has(p, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer p, double v) {
        return new VaultResponse(main.submit(() -> manager.take(p, v)));
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, double v) {
        return withdrawPlayer(main.getServer().getOfflinePlayer(name), v);
    }

    @Override
    public EconomyResponse withdrawPlayer(String name, String s1, double v) {
        return withdrawPlayer(name, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer p, String s, double v) {
        return withdrawPlayer(p, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer p, double v) {
        main.exec(() -> manager.give(p, v));
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String name, double v) {
        return depositPlayer(main.getServer().getOfflinePlayer(name), v);
    }

    @Override
    public EconomyResponse depositPlayer(String name, String s1, double v) {
        return depositPlayer(name, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer p, String s, double v) {
        return depositPlayer(p, v);
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
