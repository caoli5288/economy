package org.black_ixx.playerpoints;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.logging.Level;

import static com.mengcraft.economy.$.nil;
import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * Created by on 2017/7/8.
 */
public class CommandExec {

    private enum Sub {

        GIVE("PlayerPoints.give", (who, i) -> {
            OfflinePlayer p = getOfflinePlayer(i.next());
            if (!nil(p)) {
                int value = Integer.parseInt(i.next());
                runAsync(() -> {
                    if (PlayerPointsAPI.inst.give(p.getUniqueId(), value)) {
                        if(p.isOnline())
                            ((Player)(p)).sendMessage(ChatColor.GREEN + String.format("你收到%d点券", value));
                        who.sendMessage(ChatColor.GREEN + "操作已完成");
                    } else {
                        who.sendMessage(ChatColor.RED + "操作未完成");
                    }
                });
            } else {
                who.sendMessage(ChatColor.RED + "玩家不在线");
            }
        }),

        GIVE_EXTRA("PlayerPoints.give-extra", (who, i) -> {
            OfflinePlayer p = getOfflinePlayer(i.next());
            if (!nil(p)) {
                int value = Integer.parseInt(i.next());
                runAsync(() -> {
                    if (PlayerPointsAPI.inst.giveExtra(p.getUniqueId(), value)) {
                        if(p.isOnline())
                            ((Player)(p)).sendMessage(ChatColor.GREEN + String.format("你收到%d代券", value));
                        who.sendMessage(ChatColor.GREEN + "操作已完成");
                    } else {
                        who.sendMessage(ChatColor.RED + "操作未完成");
                    }
                });
            } else {
                who.sendMessage(ChatColor.RED + "玩家不存在");
            }
        }),

        GIVEALL("PlayerPoints.giveall", (who, i) -> {
            int value = Integer.parseInt(i.next());
            runAsync(() -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    PlayerPointsAPI.inst.give(p.getUniqueId(), value);
                    p.sendMessage(ChatColor.GREEN + String.format("你收到%d点券", value));
                }
                who.sendMessage(ChatColor.GREEN + "操作已完成");
            });
        }),

        TAKE("PlayerPoints.take", (who, i) -> {
            OfflinePlayer p = getOfflinePlayer(i.next());
            if (!nil(p)) {
                int value = Integer.parseInt(i.next());
                runAsync(() -> {
                    if (PlayerPointsAPI.inst.take(p.getUniqueId(), value)) {
                        if(p.isOnline())
                            ((Player)(p)).sendMessage(ChatColor.GREEN + String.format("你减少%d点券", value));
                        who.sendMessage(ChatColor.GREEN + "操作已完成");
                    } else {
                        who.sendMessage(ChatColor.RED + "操作未完成");
                    }
                });
            } else {
                who.sendMessage(ChatColor.RED + "玩家不存在");
            }
        }),

        TAKE_POINT("PlayerPoints.take", (who, i) -> {
            Player p = Bukkit.getPlayerExact(i.next());
            if (!nil(p)) {
                int value = Integer.parseInt(i.next());
                runAsync(() -> {
                    if (PlayerPointsAPI.inst.take(p.getUniqueId(), value, false)) {
                        p.sendMessage(ChatColor.GREEN + String.format("你减少%d点券", value));
                        who.sendMessage(ChatColor.GREEN + "操作已完成");
                    } else {
                        who.sendMessage(ChatColor.RED + "操作未完成");
                    }
                });
            } else {
                who.sendMessage(ChatColor.RED + "玩家不在线");
            }
        }),

        LOOK("PlayerPoints.look", (who, i) -> {
            OfflinePlayer p = getOfflinePlayer(i.next());
            if (!nil(p)) {
                runAsync(() -> {
                    int look = PlayerPointsAPI.inst.look(p.getUniqueId());
                    who.sendMessage(ChatColor.GREEN.toString() + look);
                });
            } else {
                who.sendMessage(ChatColor.RED + "玩家不存在");
            }
        }),

        PAY("PlayerPoints.pay", (who, i) -> {
            if (!(who instanceof Player)) throw new IllegalStateException("限玩家使用");
            OfflinePlayer p = getOfflinePlayer(i.next());
            if (!nil(p)) {
                int value = Integer.parseInt(i.next());
                runAsync(() -> {
                    if (PlayerPointsAPI.inst.pay(((Player) who).getUniqueId(), p.getUniqueId(), value)) {
                        who.sendMessage(ChatColor.GREEN + "操作已完成");
                        if(p.isOnline())
                            ((Player)(p)).sendMessage(ChatColor.GREEN + String.format("你收到%d点券", value));
                    } else {
                        who.sendMessage(ChatColor.RED + "操作未完成");
                    }
                });
            } else {
                who.sendMessage(ChatColor.RED + "玩家不存在");
            }
        }),

        SET("PlayerPoints.set", (who, i) -> {
            OfflinePlayer p = getOfflinePlayer(i.next());
            if (!nil(p)) {
                int value = i.hasNext() ? Integer.parseInt(i.next()) : 0;
                runAsync(() -> {
                    if (PlayerPointsAPI.inst.set(p.getUniqueId(), value)) {
                        who.sendMessage(ChatColor.GREEN + "操作已完成");
                        if(p.isOnline())
                            ((Player)(p)).sendMessage(ChatColor.GREEN + String.format("点券被设置成%d点", value));
                    } else {
                        who.sendMessage(ChatColor.RED + "操作未完成");
                    }
                });
            } else {
                who.sendMessage(ChatColor.RED + "玩家不存在");
            }
        }),

        RESET("PlayerPoints.reset", SET.func);

        private final String permission;
        private final BiConsumer<CommandSender, Iterator<String>> func;

        Sub(String permission, BiConsumer<CommandSender, Iterator<String>> func) {
            this.permission = permission;
            this.func = func;
        }
    }
    public static boolean exec(Plugin plugin, CommandSender who, String[] input) {
        if (input.length == 0){
            sendMessage(who);
            return false;
        }
        val i = Arrays.asList(input).iterator();
        try {
            Sub lab = Sub.valueOf(i.next().toUpperCase().replace('-', '_'));
            if (!who.hasPermission(lab.permission)) return false;
            lab.func.accept(who, i);
            return true;
        } catch (IllegalArgumentException ignore) {
            who.sendMessage(ChatColor.RED + "输入不正确|" + Arrays.toString(input));
        } catch (Exception e) {
            who.sendMessage(ChatColor.RED + e.toString());
            plugin.getLogger().log(Level.SEVERE, who + "|" + Arrays.toString(input), e);
        }
        sendMessage(who);
        return false;
    }

    private static OfflinePlayer getOfflinePlayer(String name){
        for(OfflinePlayer player:Bukkit.getOfflinePlayers()){
            if(player.getName().equalsIgnoreCase(name))return player;
        }
        return null;
    }
    private static void sendMessage(CommandSender p) {
        if (p.hasPermission("PlayerPoints.give")) {
            p.sendMessage("/points give <p> <points>");
        }
        if (p.hasPermission("PlayerPoints.give-extra")) {
            p.sendMessage("/points give-extra <p> <points>");
        }
        if (p.hasPermission("PlayerPoints.giveall")) {
            p.sendMessage("/points giveall <p> <points>");
        }
        if (p.hasPermission("PlayerPoints.take")) {
            p.sendMessage("/points take <p> <points>");
        }
        if (p.hasPermission("PlayerPoints.take")) {
            p.sendMessage("/points take-point <p> <points>");
        }
        if (p.hasPermission("PlayerPoints.look")) {
            p.sendMessage("/points look <p>");
        }
        if (p.hasPermission("PlayerPoints.set")) {
            p.sendMessage("/points take <p> <points>");
        }
        if (p.hasPermission("PlayerPoints.reset")) {
            p.sendMessage("/points reset <p>");
        }
    }
}
