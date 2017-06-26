package com.mengcraft.economy;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.economy.entity.Log;
import com.mengcraft.economy.entity.User;
import com.mengcraft.economy.lib.VaultEconomy;
import com.mengcraft.economy.sub.SubPluginLoader;
import com.mengcraft.simpleorm.EbeanHandler;
import com.mengcraft.simpleorm.EbeanManager;
import lombok.SneakyThrows;
import lombok.val;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PP;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created on 16-3-21.
 */
public class Main extends JavaPlugin {

    private ExecutorService pool;
    private Manager manager;
    private String plural;
    private String singular;
    private EbeanServer database;

    @Override
    public EbeanServer getDatabase() {
        return database;
    }

    @Override
    public void onLoad() {
        pool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        manager = new Manager(this);

        getServer().getServicesManager().register(
                MyEconomy.class,
                manager,
                this,
                ServicePriority.Normal
        );

        Plugin vault = getServer().getPluginManager().getPlugin("Vault");
        if (vault != null) {
            getServer().getServicesManager().register(
                    Economy.class,
                    new VaultEconomy(this, manager),
                    this,
                    ServicePriority.Highest
            );
            getLogger().info("Hook to vault!!!");
        }
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        boolean pp = getConfig().getBoolean("pp.hook");
        if (pp) {
            $.thr(!$.nil(Bukkit.getPluginManager().getPlugin("PlayerPoints")), "!!! PlayerPoints already loaded");
        }

        EbeanHandler db = EbeanManager.DEFAULT.getHandler(this);
        if (!db.isInitialized()) {
            if (pp) {
                db.define(PP.class);
            }
            db.define(Log.class);
            db.define(User.class);
            try {
                db.initialize();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        db.install();
        db.reflect();
        database = db.getServer();

        if (pp) {
            val description = new PluginDescriptionFile(getResource("p.yml"));
            val p = new PlayerPoints();
            SubPluginLoader.of(this).loadPlugin(p, description);
        }

        plural = getConfig().getString("vault.unit.plural");
        singular = getConfig().getString("vault.unit.singular");

        getCommand("money").setExecutor(new Executor(this, manager));

        getServer().getConsoleSender().sendMessage(new String[]{
                ChatColor.GREEN + "梦梦家高性能服务器出租店",
                ChatColor.GREEN + "shop105595113.taobao.com"
        });

        manager.hookQuit();
    }

    @Override
    public void onDisable() {
        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getScale() {
        return getConfig().getInt("vault.scale", 2);
    }

    public void exec(Runnable j) {
        pool.submit(j);
    }

    public String getPlural() {
        return plural;
    }

    public String getSingular() {
        return singular;
    }

    public <T> Future<T> submit(Callable<T> call) {
        return pool.submit(call);
    }

    public void log(Exception e) {
        getLogger().log(Level.SEVERE, e.toString(), e);
    }

}
