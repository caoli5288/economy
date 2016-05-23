package com.mengcraft.economy.lib;

import com.mengcraft.economy.MoneyManager;
import com.mengcraft.economy.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;

/**
 * Created on 16-5-23.
 */
public class VaultHook {

    public static void hook(Main main, MoneyManager manager) {
        main.getServer().getServicesManager().register(
                Economy.class,
                new VaultEconomy(main, manager),
                main,
                ServicePriority.Highest
        );
        main.getLogger().info("Hook to vault!!!");
    }

}
