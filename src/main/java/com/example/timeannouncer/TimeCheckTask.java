package com.example.timeannouncer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class TimeCheckTask extends BukkitRunnable {

    private final TimeAnnouncer plugin;
    private final Map<String, String> lastAnnouncedTime;

    public TimeCheckTask(TimeAnnouncer plugin) {
        this.plugin = plugin;
        this.lastAnnouncedTime = new HashMap<>();
    }

    @Override
    public void run() {
        if (!plugin.isAnnouncerEnabled()) {
            return;
        }

        ZoneId timezone = plugin.getTimezone();
        LocalTime currentTime = LocalTime.now(timezone);
        int currentHour = currentTime.getHour();
        int currentMinute = currentTime.getMinute();

        checkAndAnnounce("morning", plugin.getMorningHour(), plugin.getMorningMinute(), currentHour, currentMinute);
        checkAndAnnounce("noon", plugin.getNoonHour(), plugin.getNoonMinute(), currentHour, currentMinute);
        checkAndAnnounce("evening", plugin.getEveningHour(), plugin.getEveningMinute(), currentHour, currentMinute);
        checkAndAnnounce("midnight", plugin.getMidnightHour(), plugin.getMidnightMinute(), currentHour, currentMinute);
    }

    private void checkAndAnnounce(String timeType, int targetHour, int targetMinute, int currentHour, int currentMinute) {
        if (currentHour == targetHour && currentMinute == targetMinute) {
            String currentTimeKey = currentHour + ":" + currentMinute;
            String lastAnnounced = lastAnnouncedTime.get(timeType);
            if (lastAnnounced == null || !lastAnnounced.equals(currentTimeKey)) {
                String message = plugin.getMessage(timeType);
                if (!message.isEmpty()) {
                    announceMessage(message);
                    lastAnnouncedTime.put(timeType, currentTimeKey);
                }
            }
        }
    }

    private void announceMessage(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("timeannouncer.receive")) {
                player.sendMessage(message);
            }
        }
        plugin.getLogger().info("已发送时间公告: " + message);
    }

    public void resetLastAnnounced() {
        lastAnnouncedTime.clear();
    }
}
