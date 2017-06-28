package org.black_ixx.playerpoints;

import com.mengcraft.economy.sub.SubPlugin;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.logging.Level;

import static com.mengcraft.economy.$.nil;

/**
 * Created on 17-6-26.
 */
public class PlayerPoints extends SubPlugin {

    static PlayerPointsAPI api;

    public PlayerPointsAPI getAPI() {
        return api;
    }

    public PlayerPoints(PlayerPointsAPI api) {
        PlayerPoints.api = api;
    }

    enum _$ {

        GIVE("PlayerPoints.give", (who, i) -> {
            Player p = Bukkit.getPlayerExact(i.next());
            if (!nil(p)) {
                int value = Integer.parseInt(i.next());
                exec(() -> {
                    if (api.give(p.getUniqueId(), value)) {
                        p.sendMessage(ChatColor.GREEN + String.format("你收到%d点券", value));
                        who.sendMessage(ChatColor.GREEN + "操作已完成");
                    } else {
                        who.sendMessage(ChatColor.RED + "操作未完成");
                    }
                });
            } else {
                who.sendMessage(ChatColor.RED + "玩家不在线");
            }
        }),

        GIVEALL("PlayerPoints.giveall", (who, i) -> {
            int value = Integer.parseInt(i.next());
            exec(() -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    api.give(p.getUniqueId(), value);
                    p.sendMessage(ChatColor.GREEN + String.format("你收到%d点券", value));
                }
                who.sendMessage(ChatColor.GREEN + "操作已完成");
            });
        }),

        TAKE("PlayerPoints.take", (who, i) -> {
            Player p = Bukkit.getPlayerExact(i.next());
            if (!nil(p)) {
                int value = Integer.parseInt(i.next());
                exec(() -> {
                    if (api.take(p.getUniqueId(), value)) {
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
            Player p = Bukkit.getPlayerExact(i.next());
            if (!nil(p)) {
                exec(() -> {
                    int look = api.look(p.getUniqueId());
                    who.sendMessage(ChatColor.GREEN.toString() + look);
                });
            } else {
                who.sendMessage(ChatColor.RED + "玩家不在线");
            }
        }),

        PAY("PlayerPoints.pay", (who, i) -> {
            if (!(who instanceof Player)) throw new IllegalStateException("限玩家使用");
            Player p = Bukkit.getPlayerExact(i.next());
            if (!nil(p)) {
                int value = Integer.parseInt(i.next());
                exec(() -> {
                    if (api.pay(((Player) who).getUniqueId(), p.getUniqueId(), value)) {
                        who.sendMessage(ChatColor.GREEN + "操作已完成");
                        p.sendMessage(ChatColor.GREEN + String.format("你收到%d点券", value));
                    } else {
                        who.sendMessage(ChatColor.RED + "操作未完成");
                    }
                });
            } else {
                who.sendMessage(ChatColor.RED + "玩家不在线");
            }
        }),

        SET("PlayerPoints.set", (who, i) -> {
            Player p = Bukkit.getPlayerExact(i.next());
            if (!nil(p)) {
                int value = i.hasNext() ? Integer.parseInt(i.next()) : 0;
                exec(() -> {
                    if (api.set(p.getUniqueId(), value)) {
                        who.sendMessage(ChatColor.GREEN + "操作已完成");
                        p.sendMessage(ChatColor.GREEN + String.format("点券被设置成%d点", value));
                    } else {
                        who.sendMessage(ChatColor.RED + "操作未完成");
                    }
                });
            } else {
                who.sendMessage(ChatColor.RED + "玩家不在线");
            }
        }),

        RESET("PlayerPoints.reset", SET.func);

        private final String permission;
        private final BiConsumer<CommandSender, Iterator<String>> func;

        _$(String permission, BiConsumer<CommandSender, Iterator<String>> func) {
            this.permission = permission;
            this.func = func;
        }
    }

    @Override
    public boolean onCommand(CommandSender who, Command command, String label, String[] input) {
        if (input.length == 0) return false;
        val i = Arrays.asList(input).iterator();
        try {
            _$ lab = _$.valueOf(i.next().toUpperCase());
            if (!who.hasPermission(lab.permission)) return false;
            lab.func.accept(who, i);
            return true;
        } catch (IllegalArgumentException ignore) {
            who.sendMessage(ChatColor.RED + "输入不正确|" + Arrays.toString(input));
        } catch (Exception e) {
            who.sendMessage(ChatColor.RED + e.toString());
            getLogger().log(Level.SEVERE, who + "|" + Arrays.toString(input), e);
        }
        return false;
    }

    public static void exec(Runnable r) {
        CompletableFuture.runAsync(r);
    }

}
