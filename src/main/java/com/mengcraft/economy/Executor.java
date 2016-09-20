package com.mengcraft.economy;

import com.mengcraft.economy.entity.User;
import com.mengcraft.economy.lib.Cache;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;

import static com.mengcraft.economy.Main.eq;
import static java.util.Arrays.asList;

/**
 * Created on 16-3-21.
 */
public class Executor implements CommandExecutor {

    private final Cache<List<User>> top;
    private final Main main;
    private final Manager manager;

    public Executor(Main main, Manager manager) {
        this.main = main;
        this.manager = manager;
        top = new Cache<>(() -> main.getDatabase().find(User.class)
                .orderBy("value desc")
                .setMaxRows(20)
                .findList());
        top.setExpire(3000000);
    }


    @Override
    public boolean onCommand(CommandSender p, Command cmd, String s, String[] args) {
        return execute(p, asList(args).iterator());
    }

    private boolean execute(CommandSender p, Iterator<String> it) {
        if (it.hasNext()) {
            String s1 = it.next();
            if (eq(s1, "look")) {
                return look(p, it);
            } else if (eq(s1, "give")) {
                return give(p, it);
            } else if (eq(s1, "take")) {
                return take(p, it);
            } else if (eq(s1, "set")) {
                return set(p, it);
            } else if (eq(s1, "top")) {
                return top(p);
            } else {
                sendMessage(p);
            }
        } else if (p instanceof Player) {
            main.execute(() -> {
                double i = manager.get(((Player) p));
                p.sendMessage(ChatColor.BLUE + "你拥有" + i + main.getPlural());
            });
            return true;
        } else {
            sendMessage(p);
        }
        return false;
    }

    private boolean top(CommandSender p) {
        if (p.hasPermission("money.top")) {
            main.execute(() -> {
                List<User> list = top.get();
                int i = 1;
                p.sendMessage(ChatColor.GOLD + ">>> 富豪排行榜");
                for (User user : list) {
                    p.sendMessage("§6 " + i++ + "> " + user.getName() + " - " + user.getValue() + main.getPlural());
                }
                p.sendMessage(ChatColor.GOLD + "<<<");
            });
            return true;
        }
        return false;
    }

    private boolean set(CommandSender p, Iterator<String> it) {
        if (p.hasPermission("money.set") && it.hasNext()) {
            String next = it.next();
            if (it.hasNext()) {
                double i = Double.parseDouble(it.next());
                if (i > 0) {
                    set(p, main.getServer().getOfflinePlayer(next), i);
                } else {
                    p.sendMessage(ChatColor.RED + "请不要使用负数");
                }
                return true;
            }
        }
        return false;
    }

    private void set(CommandSender p, OfflinePlayer j, double i) {
        main.execute(() -> {
            manager.set(j, i);
        });
        p.sendMessage(ChatColor.GREEN + "操作成功");
    }

    private boolean take(CommandSender p, Iterator<String> it) {
        if (p.hasPermission("money.admin") && it.hasNext()) {
            String next = it.next();
            if (it.hasNext()) {
                double i = Double.parseDouble(it.next());
                if (i > 0) {
                    take(p, main.getServer().getOfflinePlayer(next), i);
                } else {
                    p.sendMessage(ChatColor.RED + "请不要使用负数");
                }
                return true;
            }
        }
        return false;
    }

    private void take(CommandSender p, OfflinePlayer j, double i) {
        main.execute(() -> {
            if (manager.take(j, i)) {
                p.sendMessage(ChatColor.GREEN + "操作成功");
            } else {
                p.sendMessage(ChatColor.RED + "操作失败");
            }
        });
    }

    private boolean give(CommandSender p, Iterator<String> it) {
        if (p.hasPermission("money.give") && it.hasNext()) {
            String next = it.next();
            if (it.hasNext()) {
                double i = Double.parseDouble(it.next());
                if (i > 0) {
                    give(p, main.getServer().getOfflinePlayer(next), i);
                } else {
                    p.sendMessage(ChatColor.RED + "请不要使用负数");
                }
                return true;
            }
        } else {
            sendMessage(p);
        }
        return false;
    }

    private void give(CommandSender p, OfflinePlayer j, double i) {
        main.execute(() -> {
            main.getDatabase().beginTransaction();
            try {// Console always has permission so never thr cast exception
                if (p.hasPermission("money.admin") || manager.take(Player.class.cast(p), i)) {
                    manager.give(j, i);
                    main.getDatabase().commitTransaction();
                    p.sendMessage(ChatColor.GREEN + "操作成功");
                }
            } catch (Exception e) {
                main.getLogger().throwing("", "", e);
                p.sendMessage(ChatColor.RED + "操作失败");
            } finally {
                main.getDatabase().endTransaction();
            }
        });
    }

    private boolean look(CommandSender p, Iterator<String> it) {
        if (it.hasNext() && p.hasPermission("money.look")) {
            OfflinePlayer j = main.getServer().getOfflinePlayer(it.next());
            main.execute(() -> {
                double i = manager.get(j);
                p.sendMessage(ChatColor.BLUE + "玩家" + j.getName() + "拥有" + i + main.getPlural());
            });
            return true;
        } else {
            sendMessage(p);
        }
        return false;
    }

    private void sendMessage(CommandSender p) {
        if (p.hasPermission("money.look")) {
            p.sendMessage("/money look <p>");
        }
        if (p.hasPermission("money.give")) {
            p.sendMessage("/money give <p> <money>");
        }
        if (p.hasPermission("money.take")) {
            p.sendMessage("/money take <p> <money>");
        }
        if (p.hasPermission("money.set")) {
            p.sendMessage("/money set <p> <money>");
        }
        if (p.hasPermission("money.top")) {
            p.sendMessage("/money top");
        }
    }

}
