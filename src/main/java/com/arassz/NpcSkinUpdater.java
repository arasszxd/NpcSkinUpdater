package com.arassz;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public final class NpcSkinUpdater extends JavaPlugin implements Listener {

    private BukkitTask updaterTask;
    private boolean updateAvailable = false;
    private String latestVersion = "";
    private File langFile;
    private FileConfiguration langConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createLangConfig();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("PlaceholderAPI not found! Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        startSkinUpdaterTask();
        Bukkit.getPluginManager().registerEvents(this, this);
        checkForUpdates();
    }

    @Override
    public void onDisable() {
        if (updaterTask != null) {
            updaterTask.cancel();
            updaterTask = null;
        }
    }

    public void createLangConfig() {
        langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            saveResource("lang.yml", false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public FileConfiguration getLangConfig() {
        if (langConfig == null) {
            createLangConfig();
        }
        return langConfig;
    }

    public void reloadLangConfig() {
        if (langFile == null) {
            langFile = new File(getDataFolder(), "lang.yml");
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public String getMessage(String path, String... placeholders) {
        String message = getLangConfig().getString(path);
        if (message == null) return "";
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace(placeholders[i], placeholders[i + 1]);
            }
        }
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }

    private void checkForUpdates() {
        if (!getConfig().getBoolean("update-checker.enabled", true)) {
            return;
        }

        int resourceId = getConfig().getInt("update-checker.resource-id", 0);
        if (resourceId <= 0) {
            getLogger().info("SpigotMC Resource ID (update-checker.resource-id) not configured, skipping update check.");
            return;
        }

        new UpdateChecker(this, resourceId).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                getLogger().info("Plugin is up-to-date! Version: " + version);
            } else {
                updateAvailable = true;
                latestVersion = version;
                getLogger().warning("A new update is available! Current version: " + getDescription().getVersion() + " -> New version: " + version);
                getLogger().warning("Please visit the SpigotMC page to update.");
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (updateAvailable && event.getPlayer().hasPermission("npcskinupdater.admin")) {
            event.getPlayer().sendMessage(getMessage("prefix") + getMessage("update-available", "{current}", getDescription().getVersion(), "{new}", latestVersion));
            event.getPlayer().sendMessage(getMessage("prefix") + getMessage("update-link"));
        }
    }

    private void startSkinUpdaterTask() {
        if (updaterTask != null) {
            updaterTask.cancel();
            updaterTask = null;
        }

        long intervalSeconds = getConfig().getLong("update-interval", 300L);
        long periodTicks = intervalSeconds * 20L;

        updaterTask = new BukkitRunnable() {
            @Override
            public void run() {
                ConfigurationSection npcsSection = getConfig().getConfigurationSection("npcs");
                if (npcsSection == null) return;

                for (String npcName : npcsSection.getKeys(false)) {
                    String placeholder = npcsSection.getString(npcName);
                    if (placeholder != null) {
                        updateNpc(npcName, placeholder);
                    }
                }
            }
        }.runTaskTimer(this, 100L, periodTicks);
    }

    private void updateNpc(String npcName, String placeholder) {
        String playerName = PlaceholderAPI.setPlaceholders(null, placeholder);

        if (playerName != null && !playerName.isEmpty() && !playerName.contains("%")) {
            if (playerName.length() < 3 || playerName.contains(" ")) return;

            String commandTemplate = getConfig().getString("update-command", "npc skin {npc} {player}");
            String command = commandTemplate
                    .replace("{npc}", npcName)
                    .replace("{player}", playerName);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

            if (getConfig().getBoolean("log-updates", true)) {
                getLogger().info("NPC updated -> " + npcName + ": " + playerName);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("npcskinupdater")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("npcskinupdater.admin")) {
                    sender.sendMessage(getMessage("prefix") + getMessage("no-permission"));
                    return true;
                }

                reloadConfig();
                reloadLangConfig();
                startSkinUpdaterTask();
                checkForUpdates();
                sender.sendMessage(getMessage("prefix") + getMessage("reload-success"));
                return true;
            }
            sender.sendMessage(getMessage("prefix") + getMessage("usage"));
            return true;
        }
        return false;
    }
}