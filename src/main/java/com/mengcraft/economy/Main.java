package com.mengcraft.economy;

import com.mengcraft.economy.db.EbeanHandler;
import com.mengcraft.economy.db.EbeanManager;
import com.mengcraft.economy.entity.User;
import com.mengcraft.economy.lib.VaultHook;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created on 16-3-21.
 */
public class Main extends JavaPlugin {

    private String plural;
    private String singular;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        EbeanHandler db = EbeanManager.DEFAULT.getHandler(this);
        if (!db.isInitialized()) {
            db.define(User.class);
            try {
                db.initialize();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        db.install();
        db.reflect();

        plural = getConfig().getString("vault.unit.plural");
        singular = getConfig().getString("vault.unit.singular");

        MoneyManager manager = new MoneyManager(this);
        getServer().getServicesManager().register(
                MoneyManager.class,
                manager,
                this,
                ServicePriority.Normal
        );
        Plugin vault = getServer().getPluginManager().getPlugin("Vault");
        if (vault != null) {
            VaultHook.hook(this, manager);
        }

        getCommand("money").setExecutor(new Executor(this, manager));
    }

    public int getScale() {
        return getConfig().getInt("vault.scale");
    }

    public static boolean eq(Object i, Object j) {
        return i == j || (i != null && i.equals(j));
    }

    public void execute(Runnable j) {
        getServer().getScheduler().runTaskAsynchronously(this, j);
    }

    public String getPlural() {
        return plural;
    }

    public String getSingular() {
        return singular;
    }

}
