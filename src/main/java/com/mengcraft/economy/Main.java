package com.mengcraft.economy;

import com.mengcraft.economy.db.EbeanHandler;
import com.mengcraft.economy.db.EbeanManager;
import com.mengcraft.economy.entity.User;
import com.mengcraft.economy.lib.VaultHook;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created on 16-3-21.
 */
public class Main extends JavaPlugin {

    private MoneyManager manager;
    private String plural;
    private String singular;


    @Override
    public void onLoad() {
        manager = new MoneyManager(this);
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
    }

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

        getCommand("money").setExecutor(new Executor(this, manager));

        getServer().getConsoleSender().sendMessage(new String[]{
                ChatColor.GREEN + "梦梦家高性能服务器出租店",
                ChatColor.GREEN + "shop105595113.taobao.com"
        });
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
