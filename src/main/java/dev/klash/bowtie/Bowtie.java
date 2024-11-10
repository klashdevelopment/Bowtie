package dev.klash.bowtie;

import dev.klash.bowtie.chat.AfkListener;
import dev.klash.bowtie.chat.ChatListener;
import dev.klash.bowtie.commands.BowtieCommands;
import dev.klash.bowtie.utility.JSONUtility;
import dev.klash.caramel.Caramel;
import dev.klash.caramel.CaramelConfig;
import dev.klash.caramel.commands.CaramelCommand;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Bowtie extends JavaPlugin {

    public static Chat vaultChat = null;
    public static Permission vaultPerms = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        tie = this;

        try {
            if(Bukkit.getPluginManager().getPlugin("Vault") != null) {
                RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
                if (rsp == null) {
                    getLogger().warning("Vault (Chat) not found, prefixes/suffixes won't work.");
                }else {
                    vaultChat = rsp.getProvider();
                }

                RegisteredServiceProvider<Permission> rspPerms = getServer().getServicesManager().getRegistration(Permission.class);
                if (rspPerms == null) {
                    getLogger().warning("Vault (Perms) not found, prefixes/suffixes won't work.");
                }else {
                    vaultPerms = rspPerms.getProvider();
                }
            } else {
                getLogger().warning("Vault not found, colors/prefixes/suffixes won't work.");
            }
        } catch (Exception exception) {
            getLogger().warning("Vault (Chat/Perms) had issues, colors/prefixes/suffixes won't work.");
            return;
        }

        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new AfkListener(), this);

        spawnConfig = new CaramelConfig(this, "spawn.yml");
        spawnConfig.saveDefaultConfig();

        nicksConfig = new CaramelConfig(this, "nicks.yml");
        nicksConfig.saveDefaultConfig();

        homesConfig = new CaramelConfig(this, "homes.yml");
        homesConfig.saveDefaultConfig();

        infoCmdConfig = new CaramelConfig(this, "info-commands.yml");
        infoCmdConfig.saveDefaultConfig();

        if(getConfig().getBoolean("items.use-essentials-item-names")) {
            getLogger().warning("Using experimental essentials item names, items with metadata will not be added.");
            try {
                File itemsJsonFile = new File(getDataFolder(), "essentialsitems.json");
                if(!itemsJsonFile.exists()) {
                    saveResource("essentialsitems.json", false);
                }
                Map<String, String> itemsJSON = JSONUtility.parseSSJsonFile(itemsJsonFile);
                List<String> failed = new ArrayList<>();
                for (Map.Entry<String, String> entry : itemsJSON.entrySet()) {
                    Material material = Material.getMaterial(entry.getValue().toUpperCase());
                    if(material != null) {
                        materialNames.put(entry.getKey().toLowerCase(), material);
                    } else {
                        failed.add(entry.getKey());
                    }
                }
                if(!failed.isEmpty()) {
                    getLogger().warning("(EssentialsItemNames) Failed to add many essentials-items, probably due to invalid materials like \"blazespawner\".");
                }
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        }else {
            for (Material material : Material.values()) {
                if(material.isLegacy() || material.isAir()) continue;
                materialNames.put(material.name().toLowerCase(), material);
                if(!material.name().replaceAll("_","").equalsIgnoreCase(material.name())) {
                    materialNames.put(material.name().replaceAll("_","").toLowerCase(), material);
                }
            }
        }


        for(Class<? extends CaramelCommand> clazz : BowtieCommands.advancedCommands) {
            try {
                CaramelCommand cmd = clazz.getConstructor().newInstance();
                registeredCommands.add(cmd);
                Caramel.getInstance().commands.register(cmd);
            } catch(Exception e) {}
        }
        for(CaramelCommand cmd : BowtieCommands.commands) {
            registeredCommands.add(cmd);
            Caramel.getInstance().commands.register(cmd);
        }
    }

    List<CaramelCommand> registeredCommands = new ArrayList<>();

    public static HashMap<String, Material> materialNames = new HashMap<>();

    public static CaramelConfig spawnConfig, nicksConfig, homesConfig, infoCmdConfig;

    public static Bowtie tie;
    public static Bowtie tie() {
        return tie!=null ? tie : (Bowtie)Bukkit.getServer().getPluginManager().getPlugin("Bowtie");
    }

    @Override
    public void onDisable() {
        for(CaramelCommand cmd : registeredCommands) {
            Caramel.getInstance().commands.getCommandList().remove(cmd);
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return Caramel.getInstance().onCommand(sender, command, label, args);
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Caramel.getInstance().onTabComplete(sender, command, alias, args);
    }
}
