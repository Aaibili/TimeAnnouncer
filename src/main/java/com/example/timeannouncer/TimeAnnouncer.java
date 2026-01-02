package com.example.timeannouncer;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.time.ZoneId;

public class TimeAnnouncer extends JavaPlugin {

    private TimeCheckTask timeCheckTask;
    private boolean enabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        enabled = getConfig().getBoolean("settings.enabled", true);

        int checkInterval = getConfig().getInt("settings.check-interval", 60) * 20;
        timeCheckTask = new TimeCheckTask(this);
        timeCheckTask.runTaskTimer(this, checkInterval, checkInterval);

        getLogger().info("TimeAnnouncer 插件已启用！ | TimeAnnouncer plugin enabled!");
    }

    @Override
    public void onDisable() {
        if (timeCheckTask != null) {
            timeCheckTask.cancel();
        }
        getLogger().info("TimeAnnouncer 插件已禁用！ | TimeAnnouncer plugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("timeannouncer")) {
            if (!sender.hasPermission("timeannouncer.use")) {
                sender.sendMessage(ChatColor.RED + "你没有权限使用此命令！ | You don't have permission to use this command!");
                return true;
            }

            if (args.length == 0) {
                enabled = !enabled;
                getConfig().set("settings.enabled", enabled);
                saveConfig();

                String status = enabled ? ChatColor.GREEN + "已启用 | Enabled" : ChatColor.RED + "已禁用 | Disabled";
                sender.sendMessage(ChatColor.GOLD + "时间公告功能 | Time announcement feature " + status);
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("timeannouncer.reload")) {
                    sender.sendMessage(ChatColor.RED + "你没有权限使用此命令！ | You don't have permission to use this command!");
                    return true;
                }
                reloadConfig();
                enabled = getConfig().getBoolean("settings.enabled", true);
                timeCheckTask.resetLastAnnounced();
                sender.sendMessage(ChatColor.GREEN + "配置文件已重载！ | Configuration file reloaded!");
                return true;
            }

            sender.sendMessage(ChatColor.RED + "用法: /" + label + " [reload]");
            return true;
        }
        return false;
    }

    public boolean isAnnouncerEnabled() {
        return enabled;
    }

    public String getMessage(String timeType) {
        return getConfig().getString("messages." + timeType, "");
    }

    public int getMorningHour() {
        return getConfig().getInt("time-settings.morning.hour", 6);
    }

    public int getMorningMinute() {
        return getConfig().getInt("time-settings.morning.minute", 0);
    }

    public int getNoonHour() {
        return getConfig().getInt("time-settings.noon.hour", 12);
    }

    public int getNoonMinute() {
        return getConfig().getInt("time-settings.noon.minute", 0);
    }

    public int getEveningHour() {
        return getConfig().getInt("time-settings.evening.hour", 18);
    }

    public int getEveningMinute() {
        return getConfig().getInt("time-settings.evening.minute", 0);
    }

    public int getMidnightHour() {
        return getConfig().getInt("time-settings.midnight.hour", 0);
    }

    public int getMidnightMinute() {
        return getConfig().getInt("time-settings.midnight.minute", 0);
    }

    public ZoneId getTimezone() {
        String timezoneId = getConfig().getString("settings.timezone", "Asia/Shanghai");
        return ZoneId.of(timezoneId);
    }
}
