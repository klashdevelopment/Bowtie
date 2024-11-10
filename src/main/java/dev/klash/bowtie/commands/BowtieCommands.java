package dev.klash.bowtie.commands;

import dev.klash.bowtie.Bowtie;
import dev.klash.bowtie.commands.impl.*;
import dev.klash.bowtie.utility.*;
import dev.klash.caramel.CaramelUtility;
import dev.klash.caramel.commands.CaramelCommand;
import dev.klash.caramel.commands.CaramelCommandDetail;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class BowtieCommands {

    public static List<Class<? extends CaramelCommand>> advancedCommands
            = new ArrayList<>(Arrays.asList(
                    ItemCommand.class,
                    TeleportCommand.class,
                    XPCommand.class,
                    EffectCommand.class,
                    LocateCommand.class,
                    PingCommand.class
    ));
    public static List<CaramelCommand> commands = new ArrayList<>(Arrays.asList(
            createPlayerCompleted(new String[]{"gmc"}, "bowtie.player", (player, args) -> {
                Player target = player;
                if(args.length > 0) {
                    target = Bowtie.tie().getServer().getPlayer(args[0]);
                    if(target == null) return "<red>Invalid player";
                }
                target.setGameMode(GameMode.CREATIVE);
                return "<green>Game mode changed to <aqua>creative";
            }),
            createPlayerCompleted(new String[]{"gms"}, "bowtie.player", (player, args) -> {
                Player target = player;
                if(args.length > 0) {
                    target = Bowtie.tie().getServer().getPlayer(args[0]);
                    if(target == null) return "<red>Invalid player";
                }
                target.setGameMode(GameMode.SURVIVAL);
                return "<green>Game mode changed to <aqua>survival";
            }),
            createPlayerCompleted(new String[]{"gma"}, "bowtie.player", (player, args) -> {
                Player target = player;
                if(args.length > 0) {
                    target = Bowtie.tie().getServer().getPlayer(args[0]);
                    if(target == null) return "<red>Invalid player";
                }
                target.setGameMode(GameMode.ADVENTURE);
                return "<green>Game mode changed to <aqua>adventure";
            }),
            createPlayerCompleted(new String[]{"gmsp"}, "bowtie.player", (player, args) -> {
                Player target = player;
                if(args.length > 0) {
                    target = Bowtie.tie().getServer().getPlayer(args[0]);
                    if(target == null) return "<red>Invalid player";
                }
                target.setGameMode(GameMode.SPECTATOR);
                return "<green>Game mode changed to <aqua>spectator";
            }),
            createPlayerCompleted(new String[]{"fly"}, "bowtie.player", (player, args) -> {
                Player target = player;
                if(args.length > 0) {
                    target = Bowtie.tie().getServer().getPlayer(args[0]);
                    if(target == null) return "<red>Invalid player";
                }
                target.setAllowFlight(!target.getAllowFlight());
                return "<green>Flight mode <aqua>"+(target.getAllowFlight() ? "enabled" : "disabled")+" <green>for <aqua>"+target.getName();
            }),
            createSimple(new String[]{"maxenchant"}, "bowtie.items", (player, args) -> {
                for(Enchantment ench : Enchantment.values()) {
                    if(!ench.canEnchantItem(player.getInventory().getItemInMainHand()) || ench.isCursed()) continue;
                    player.getInventory().getItemInMainHand().addUnsafeEnchantment(ench, ench.getMaxLevel());
                }
                return "<green>Maxed out enchantments on your held item.";
            }),
            createSimple(new String[]{"disenchant"}, "bowtie.items", (player, args) -> {
                ItemStack item = player.getInventory().getItemInMainHand();
                item.removeEnchantments();
                return "<green>Disenchanted item.";
            }),
            createSimple(new String[]{"maxinventory"}, "bowtie.items", (player, args) -> {
                Consumer<ItemStack> max = (item) -> {
                    for(Enchantment ench : Enchantment.values()) {
                        if(!ench.canEnchantItem(item) || ench.isCursed()) continue;
                        item.addUnsafeEnchantment(ench, ench.getMaxLevel());
                    }
                };
                for(ItemStack item : player.getInventory().getContents()) {
                    if(item == null || item.getType().isAir()) continue;
                    max.accept(item);
                }
                return "<green>Maxed out enchantments on your inventory items.";
            }),
            createPlayerCompleted(new String[]{"clear"}, "bowtie.player", (player, args) -> {
                Player target = player;
                if(args.length > 0) {
                    target = Bowtie.tie().getServer().getPlayer(args[0]);
                    if(target == null) return "<red>Invalid player";
                }
                target.getInventory().clear();
                return "<green>Cleared inventory.";
            }),
            createCompleted(new String[]{"gamemode", "gm"}, "bowtie.player", (playerRunning, args) -> {
                Player player = playerRunning;
                if(args.length > 1) {
                    if(!args[0].equalsIgnoreCase("@s")) {
                        player = Bowtie.tie().getServer().getPlayer(args[0]);
                        if (player == null) return "<red>Invalid player";
                    }
                }
                if(args.length < 1) return "<red>Usage: /gamemode <mode>";
                switch(args[0].toLowerCase()) {
                    case "0":
                    case "s":
                    case "survival":
                        player.setGameMode(GameMode.SURVIVAL);
                        return "<green>Game mode changed to <aqua>survival";
                    case "1":
                    case "c":
                    case "creative":
                        player.setGameMode(GameMode.CREATIVE);
                        return "<green>Game mode changed to <aqua>creative";
                    case "2":
                    case "a":
                    case "adventure":
                        player.setGameMode(GameMode.ADVENTURE);
                        return "<green>Game mode changed to <aqua>adventure";
                    case "3":
                    case "sp":
                    case "spectator":
                        player.setGameMode(GameMode.SPECTATOR);
                        return "<green>Game mode changed to <aqua>spectator";
                    default:
                        return "<red>Invalid game mode";
                }
            }, list -> {
                list.addAll(Arrays.asList("survival", "creative", "adventure", "spectator"));
                return 0;
            }),
            createCompleted(new String[]{"enchant"}, "bowtie.items", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /enchant <enchantment> [level]";
                Enchantment ench = Enchantment.getByName(args[0].toUpperCase());
                if(ench == null) return "<red>Invalid enchantment";
                int level = ench.getMaxLevel();
                if(args.length > 1) {
                    try {
                        level = Integer.parseInt(args[1]);
                    } catch(Exception e) {
                        return "<red>Invalid level";
                    }
                }
                player.getInventory().getItemInMainHand().addUnsafeEnchantment(ench, level);
                return "<green>Enchanted your held item with <aqua>"+ench.getKey().getKey()+" <green>at level <aqua>"+level;
            }, list -> {
                for(Enchantment ench : Enchantment.values()) {
                    list.add(ench.getKey().getKey());
                }
                return 0;
            }),
            createSimple(new String[]{"repair"}, "bowtie.items", (player, args) -> {
                ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                if(meta == null) return "<red>Invalid item";
                Damageable dama = (Damageable) meta;
                dama.setDamage(0);
                player.getInventory().getItemInMainHand().setItemMeta(meta);
                return "<green>Repaired your held item.";
            }),
            createPlayerCompleted(new String[]{"god"}, "bowtie.player", (player, args) -> {
                if(args.length > 0) {
                    Player target = Bowtie.tie().getServer().getPlayer(args[0]);
                    if(target == null) return "<red>Invalid player";
                    target.setInvulnerable(!target.isInvulnerable());
                    return "<green>God mode <aqua>"+(target.isInvulnerable() ? "enabled" : "disabled")+" <green>for <aqua>"+target.getName();
                }
                player.setInvulnerable(!player.isInvulnerable());
                return "<green>God mode <aqua>"+(player.isInvulnerable() ? "enabled" : "disabled");
            }),
            createSimple(new String[]{"speed"}, "bowtie.player", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /speed <amount>";
                try {
                    float speed = Float.parseFloat(args[0]);
                    player.setWalkSpeed(speed);
                    return "<green>Set your speed to <aqua>"+speed;
                } catch(Exception e) {
                    return "<red>Invalid speed";
                }
            }),
            createSimple(new String[]{"flyspeed"}, "bowtie.player", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /flyspeed <amount>";
                try {
                    float speed = Float.parseFloat(args[0]);
                    player.setFlySpeed(speed);
                    return "<green>Set your fly speed to <aqua>"+speed;
                } catch(Exception e) {
                    return "<red>Invalid speed";
                }
            }),
            createPlayerCompleted(new String[]{"kill"}, "bowtie.admin", (player, args) -> {
                if(args.length > 0) {
                    Player target = Bowtie.tie().getServer().getPlayer(args[0]);
                    if(target == null) return "<red>Invalid player";
                    target.setHealth(0);
                    return "<green>Killed <aqua>"+target.getName();
                }
                player.setHealth(0);
                return "<green>Killed you.";
            }),
            createPlayerCompleted(new String[]{"heal"}, "bowtie.player", (player, args) -> {
                if(args.length > 0) {
                    Player target = Bowtie.tie().getServer().getPlayer(args[0]);
                    if(target == null) return "<red>Invalid player";
                    target.setHealth(20);
                    return "<green>Healed <aqua>"+target.getName();
                }
                player.setHealth(20);
                return "<green>Healed you.";
            }),
            createPlayerCompleted(new String[]{"feed"}, "bowtie.player", (player, args) -> {
                if(args.length > 0) {
                    Player target = Bowtie.tie().getServer().getPlayer(args[0]);
                    if(target == null) return "<red>Invalid player";
                    target.setFoodLevel(20);
                    return "<green>Fed <aqua>"+target.getName();
                }
                player.setFoodLevel(20);
                return "<green>Fed you.";
            }),
            createSimple(new String[]{"time"}, "bowtie.world", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /time <time>";
                try {
                    long time = Long.parseLong(args[0]);
                    player.getWorld().setTime(time);
                    return "<green>Set time to <aqua>"+time;
                } catch(Exception e) {
                    return "<red>Invalid time";
                }
            }),
            createSimple(new String[]{"rename"}, "bowtie.items", (player, args) -> {
                ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                if(meta == null) return "<red>Invalid item";
                meta.displayName(CaramelUtility.colorcomp(String.join(" ", args)));
                player.getInventory().getItemInMainHand().setItemMeta(meta);
                return "<green>Renamed your held item.";
            }),
            createSimple(new String[]{"clearname"}, "bowtie.items", (player, args) -> {
                ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                if(meta == null) return "<red>Invalid item";
                meta.displayName(null);
                player.getInventory().getItemInMainHand().setItemMeta(meta);
                return "<green>Cleared the name of your held item.";
            }),
            createCompleted(new String[]{"lore"}, "bowtie.items", (player, args) -> {
                ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                if(meta == null) return "<red>Invalid item";
                if(args.length < 1) return "<red>Usage: /lore <set/add/clear/remove> [text]";
                switch(args[0].toLowerCase()) {
                    case "set":
                        if(args.length < 2) return "<red>Usage: /lore set <text>";
                        meta.lore(Arrays.asList(CaramelUtility.colorcomp(String.join(" ", Arrays.copyOfRange(args, 1, args.length)))));
                        player.getInventory().getItemInMainHand().setItemMeta(meta);
                        return "<green>Set the lore of your held item.";
                    case "add":
                        if(args.length < 2) return "<red>Usage: /lore add <text>";
                        List<Component> lore = meta.lore();
                        if(lore == null) lore = new ArrayList<>();
                        lore.add(CaramelUtility.colorcomp(String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
                        meta.lore(lore);
                        player.getInventory().getItemInMainHand().setItemMeta(meta);
                        return "<green>Added to the lore of your held item.";
                    case "clear":
                        meta.lore(null);
                        player.getInventory().getItemInMainHand().setItemMeta(meta);
                        return "<green>Cleared the lore of your held item.";
                    case "remove":
                        if(args.length < 2) return "<red>Usage: /lore remove <index>";
                        try {
                            int index = Integer.parseInt(args[1]);
                            List<Component> lore2 = meta.lore();
                            if(lore2 == null) return "<red>No lore to remove";
                            if(index < 0 || index >= lore2.size()) return "<red>Invalid index";
                            lore2.remove(index);
                            meta.lore(lore2);
                            player.getInventory().getItemInMainHand().setItemMeta(meta);
                            return "<green>Removed from the lore of your held item.";
                        } catch(Exception e) {
                            return "<red>Invalid index";
                        }
                    default:
                        return "<red>Invalid action";
                }
            }, list -> {
                list.addAll(Arrays.asList("set", "add", "clear", "remove"));
                return 0;
            }),
            createSimple(new String[]{"repairall"}, "bowtie.items", (player, args) -> {
                for(ItemStack stack : player.getInventory().getContents()) {
                    if(stack == null) continue;
                    ItemMeta meta = stack.getItemMeta();
                    if(meta == null) continue;
                    Damageable dama = (Damageable) meta;
                    dama.setDamage(0);
                    stack.setItemMeta(meta);
                }
                return "<green>Repaired all items in your inventory.";
            }),
            createSimple(new String[]{"renamemob"}, "bowtie.world", (player, args) -> {
                Entity target = player.getTargetEntity(10);
                if(target == null) return "<red>No entity found";
                target.customName(CaramelUtility.colorcomp(String.join(" ", args)));
                return "<green>Renamed the <aqua>" + PlainTextComponentSerializer.plainText().serialize(Component.translatable(target.getType().translationKey())) + " <green>to <aqua>" + String.join(" ", args);
            }),
            createSimple(new String[]{"nickname", "nick"}, "bowtie.nickname", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /nickname <name>";
                player.displayName(CaramelUtility.colorcomp(String.join(" ", args)));
                Bowtie.nicksConfig.getData().set(String.valueOf(player.getUniqueId()), String.join(" ", args));
                Bowtie.nicksConfig.saveData();
                return "<green>Set your nickname to <aqua>"+String.join(" ", args);
            }),
            createPlayerCompleted(new String[]{"nickother"}, "bowtie.nickname", (player, args) -> {
                if(args.length < 2) return "<red>Usage: /nickother <player> <name>";
                Player target = Bowtie.tie().getServer().getPlayer(args[0]);
                if(target == null) return "<red>Invalid player";
                target.displayName(CaramelUtility.colorcomp(String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
                Bowtie.nicksConfig.getData().set(String.valueOf(target.getUniqueId()), String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                Bowtie.nicksConfig.saveData();
                return "<green>Set the nickname of <aqua>"+target.getName()+" <green>to <aqua>"+String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            }),
            createSimple(new String[]{"clearnick"}, "bowtie.nickname", (player, args) -> {
                player.displayName(null);
                player.playerListName(null);
                Bowtie.nicksConfig.getData().set(String.valueOf(player.getUniqueId()), null);
                Bowtie.nicksConfig.saveData();
                return "<green>Cleared your nickname.";
            }),
            createPlayerCompleted(new String[]{"clearnickother"}, "bowtie.nickname", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /clearnickother <player>";
                Player target = Bowtie.tie().getServer().getPlayer(args[0]);
                if(target == null) return "<red>Invalid player";
                target.displayName(null);
                target.playerListName(null);
                Bowtie.nicksConfig.getData().set(String.valueOf(target.getUniqueId()), null);
                Bowtie.nicksConfig.saveData();
                return "<green>Cleared the nickname of <aqua>"+target.getName();
            }),
            createSimple(new String[]{"list"}, "bowtie.common", (player, args) -> {
                StringBuilder sb = new StringBuilder();
                for(Player p : Bowtie.tie().getServer().getOnlinePlayers()) {
                    sb.append(p.getName()).append(", ");
                }
                if(sb.length() > 0) sb.setLength(sb.length()-2);
                if(sb.length() == 0) sb.append("None");
                return "<green>Online players: <aqua>"+ sb;
            }, true),
            createSimple(new String[]{"day"}, "bowtie.world", (player, args) -> {
                player.getWorld().setTime(0);
                return "<green>Set time to <aqua>day";
            }),
            createSimple(new String[]{"night"}, "bowtie.world", (player, args) -> {
                player.getWorld().setTime(13000);
                return "<green>Set time to <aqua>night";
            }),
            createSimple(new String[]{"sun"}, "bowtie.world", (player, args) -> {
                player.getWorld().setStorm(false);
                return "<green>Set weather to <aqua>clear";
            }),
            createSimple(new String[]{"rain"}, "bowtie.world", (player, args) -> {
                player.getWorld().setStorm(true);
                return "<green>Set weather to <aqua>rain";
            }),
            createSimple(new String[]{"thunder"}, "bowtie.world", (player, args) -> {
                player.getWorld().setThundering(true);
                return "<green>Set weather to <aqua>thunder";
            }),
            createSimple(new String[]{"clearweather"}, "bowtie.world", (player, args) -> {
                player.getWorld().setStorm(false);
                player.getWorld().setThundering(false);
                return "<green>Cleared the weather.";
            }),
            createPlayerCompleted(new String[]{"tpto", "tp2p"}, "bowtie.teleport", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /tpto <player>";
                Player target = Bowtie.tie().getServer().getPlayer(args[0]);
                if(target == null) return "<red>Invalid player";
                player.teleport(target);
                return "<green>Teleported to <aqua>"+target.getName();
            }),
            createPlayerCompleted(new String[]{"tphere"}, "bowtie.teleport", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /tphere <player>";
                Player target = Bowtie.tie().getServer().getPlayer(args[0]);
                if(target == null) return "<red>Invalid player";
                target.teleport(player);
                return "<green>Teleported <aqua>"+target.getName()+" <green>to you.";
            }),
            createSimple(new String[]{"tpall"}, "bowtie.teleport", (player, args) -> {
                for(Player p : Bowtie.tie().getServer().getOnlinePlayers()) {
                    p.teleport(player);
                }
                return "<green>Teleported all players to you.";
            }),
            createPlayerCompleted(new String[]{"locateplayer"}, "bowtie.locate", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /locateplayer <player>";
                Player target = Bowtie.tie().getServer().getPlayer(args[0]);
                if(target == null) return "<red>Invalid player";
                int x = target.getLocation().getBlockX();
                int y = target.getLocation().getBlockY();
                int z = target.getLocation().getBlockZ();
                return "<green>Location of <aqua>"+target.getName() + ": "+x+", " + y+", "+z+" <green>in<aqua> " + target.getLocation().getWorld().getName();
            }),
            createSimple(new String[]{"broadcast", "bc"}, "bowtie.admin", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /broadcast <message>";
                Bowtie.tie().getServer().broadcast(CaramelUtility.colorcomp("<white>"+String.join(" ", args)));
                return "<green>Broadcasted.";
            }, true),
            createSimple(new String[]{"spawn"}, "bowtie.spawn", (player, args) -> {
                double[] coords = Bowtie.spawnConfig.getData().getDoubleList("spawn.coordinates").stream().mapToDouble(d -> d).toArray();
                String worldName = Bowtie.spawnConfig.getData().getString("spawn.world");
                if(Bukkit.getWorld(worldName) == null) return "<red>Error: Invalid spawn data. World \""+worldName+"\" does not exist";
                player.teleport(new Location(Bukkit.getWorld(worldName), coords[0], coords[1], coords[2], (float) coords[3], (float) coords[4]));
                return "<green>Teleported to <aqua>spawn";
            }),
            createSimple(new String[]{"setspawn"}, "bowtie.spawn", (player, args) -> {
                Bowtie.spawnConfig.getData().set("spawn.coordinates", Arrays.asList(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch()));
                Bowtie.spawnConfig.getData().set("spawn.world", player.getWorld().getName());
                Bowtie.spawnConfig.saveData();
                return "<green>Set spawn to your location.";
            }),
            createSimple(new String[]{"bowtiereload"}, "bowtie.admin", (player, args) -> {
                Bowtie.spawnConfig.reload();
                Bowtie.nicksConfig.reload();
                Bowtie.homesConfig.reload();
                Bowtie.infoCmdConfig.reload();
                Bowtie.tie().reloadConfig();
                return "<green>Reloaded Bowtie configurations.";
            }, true),
            createSimple(new String[]{"wtp", "tpworld"}, "bowtie.admin", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /wtp <world>";
                if(Bukkit.getWorld(args[0]) == null) return "<red>Invalid world";
                player.teleport(Bukkit.getWorld(args[0]).getSpawnLocation());
                return "<green>Teleported to <aqua>"+args[0];
            }),
            createCompleted(new String[]{"tpinworld"}, "bowtie.admin", (player, args) -> {
                if(args.length < 4) return "<red>Usage: /tpinworld <world> <x> <y> <z><gold> or use <red>/tpworld <world>";
                if(Bukkit.getWorld(args[0]) == null) return "<red>Invalid world";
                try {
                    double x = Double.parseDouble(args[1]);
                    double y = Double.parseDouble(args[2]);
                    double z = Double.parseDouble(args[3]);
                    player.teleport(new Location(Bukkit.getWorld(args[0]), x, y, z));
                    return "<green>Teleported to <aqua>"+args[0];
                } catch(Exception e) {
                    return "<red>Invalid coordinates";
                }
            }, (list, arg) -> {
                if(arg == 0) {
                    for(org.bukkit.World world : Bukkit.getWorlds()) {
                        list.add(world.getName());
                    }
                }
                return arg;
            }),
            createSimple(new String[]{"worlds"}, "bowtie.admin", (player, args) -> {
                StringBuilder sb = new StringBuilder();
                for(org.bukkit.World world : Bukkit.getWorlds()) {
                    sb.append(world.getName()).append(", ");
                }
                if(!sb.isEmpty()) sb.setLength(sb.length()-2);
                if(sb.isEmpty()) sb.append("None");
                return "<green>Worlds: <aqua>"+ sb;
            }, true),
            createSimple(new String[]{"setworldspawn"}, "bowtie.admin", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /setworldspawn <world>";
                if(Bukkit.getWorld(args[0]) == null) return "<red>Invalid world";
                Bukkit.getWorld(args[0]).setSpawnLocation(player.getLocation());
                return "<green>Set spawn of <aqua>"+args[0]+" <green>to your location.";
            }),
            createCompleted(new String[]{"share"}, "bowtie.share", (cmdPlayer, args) -> {
                if(args.length < 2) return "<red>Usage: /share <player/@a> <armor/helditem/offhand/coordinates/coordinates+xaero>";

                BiConsumer<Player, ItemStack[]> shareItems = (player, items) -> {
                    Component start = CaramelUtility.colorcomp("<aqua>"+cmdPlayer.getName() + "<green> is sharing their items: ");
                    start = start.append(ChatUtility.itemsToComponent(items, "aqua", "green"));
                    player.sendMessage(start);
                };

                BiConsumer<Player, String> shareWith = (player, type) -> {
                    switch(type.toLowerCase()) {
                        case "armor":
                            shareItems.accept(player, cmdPlayer.getInventory().getArmorContents());
                            break;
                        case "helditem":
                            shareItems.accept(player, new ItemStack[]{cmdPlayer.getInventory().getItemInMainHand()});
                            break;
                        case "offhand":
                            shareItems.accept(player, new ItemStack[]{cmdPlayer.getInventory().getItemInOffHand()});
                            break;
                        case "coordinates":
                            player.sendMessage(CaramelUtility.colorcomp("<aqua>"+cmdPlayer.getName() + "<green> is sharing their location at: <aqua>"+cmdPlayer.getLocation().getBlockX()+", "+cmdPlayer.getLocation().getBlockY()+", "+cmdPlayer.getLocation().getBlockZ()+", <green>in world<aqua> " + cmdPlayer.getWorld().getName()));
                            break;
                        case "coordinates+xaero":
                            int x = cmdPlayer.getLocation().getBlockX(), y = cmdPlayer.getLocation().getBlockY(), z = cmdPlayer.getLocation().getBlockZ();
                            player.sendMessage(CaramelUtility.colorcomp("<aqua>"+cmdPlayer.getName() + "<green> is sharing their location at: <aqua>"+x+", "+y+", "+z+", <green>in world<aqua> " + cmdPlayer.getWorld().getName()));
                            player.sendMessage(CaramelUtility.colorcomp("<aqua>They have also shared a Xaero waypoint, this may look odd if you do not have Xaero's World Map."));
                            player.sendMessage("xaero-waypoint:Shared Location:S:"+x+":"+y+":"+z+":11:false:0:Internal-dim%minecraft$"+ cmdPlayer.getWorld().getName()+"-waypoints");
                            break;
                        default:
                            cmdPlayer.sendMessage(CaramelUtility.colorcomp("<red>Unable to share with <dark_red>" + player.getName()+"<red> - invalid share type \""+type+"\""));
                            break;
                    }
                };

                if(args[0].equalsIgnoreCase("@a")) {
                    for(Player target : Bukkit.getOnlinePlayers()) {
                        shareWith.accept(target, args[1]);
                    }
                }else {
                    Player target = Bowtie.tie().getServer().getPlayer(args[0]);
                    if(target == null) return "<red>Invalid player";

                    shareWith.accept(target, args[1]);
                }
                return "<green>Shared your <aqua>"+args[1]+" <green>with <aqua>"+args[0];
            }, (list, arg) -> {
                if(arg == 1) {
                    list.add("@a");
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                }else {
                    list.addAll(Arrays.asList("armor", "helditem", "offhand", "coordinates", "coordinates+xaero"));
                }
                return arg-1;
            }),
            createCompleted(new String[]{"sudo"}, "bowtie.sudo", (player, args) -> {
                if(args.length < 2) return "<red>Usage: /sudo <player> <chat/command> [args]";
                Player target = Bowtie.tie().getServer().getPlayer(args[0]);
                if(target == null) return "<red>Invalid player";
                if(args[1].equalsIgnoreCase("chat")) {
                    target.chat(String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                }else if(args[1].equalsIgnoreCase("command")) {
                    Bowtie.tie().getServer().dispatchCommand(target, String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                }else {
                    return "<red>Invalid action";
                }
                return "<green>Sudo'd <aqua>"+target.getName();
            }, (list, arg) -> {
                if(arg == 1) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                }
                if(arg == 2) {
                    list.add("chat");
                    list.add("command");
                }
                return arg-1;
            }),
            createSimple(new String[]{"logininfo"}, "bowtie.admin", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /logininfo <player>";
                OfflinePlayer target = Bowtie.tie().getServer().getOfflinePlayer(args[0]);
                String online = target.isOnline() ? "<green>Online" : "<red>Offline";
                return "<green>Info for <aqua>"+target.getName()+"<green>: <aqua>"+target.getUniqueId()+"<green>, Last Login: <aqua>"+target.getLastLogin()+"<green> at <aqua>"+target.getLocation().getBlockX()+", "+target.getLocation().getBlockY()+", "+target.getLocation().getBlockZ()+"<green>, Status: "+online;
            }, true),
            createSimple(new String[]{"sethome"}, "bowtie.home", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /sethome <name>";
                HomeUtility.Home home = HomeUtility.locationToHome(player.getLocation(), args[0]);
                home.saveTo(player.getUniqueId());
                return "<green>Created home <aqua>"+args[0]+" <green>at your location.";
            }),
            createSimple(new String[]{"home"}, "bowtie.home", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /home <name>";
                HomeUtility.Home home = HomeUtility.getHome(player.getUniqueId(), args[0]);
                if(home == null) return "<red>Invalid home";
                if(!Bowtie.tie().getConfig().getBoolean("homes.tell-dont-teleport")) {
                    player.teleport(home.toLocation());
                    return "<green>Teleported to home <aqua>"+args[0];
                }else {
                    String coords = "<aqua>"+home.rx()+"<green>, <aqua>"+home.ry()+"<green>, <aqua>"+home.rz()+"<green> in world <aqua>"+home.world();

                    Component message = CaramelUtility.colorcomp("<green>[Waypoint]").clickEvent(ClickEvent.callback((player1) -> {
                        player.sendMessage("xaero-waypoint:"+home.name()+":S:"+home.x()+":"+home.y()+":"+home.z()+":11:false:0:Internal-dim%minecraft$"+home.world()+"-waypoints");
                    })).hoverEvent(HoverEvent.showText(CaramelUtility.colorcomp("<green>Click to create a Xaero Waypoint.")));
                    player.sendMessage(message);
                    return "<green>Your home <aqua>"+args[0]+" <green>is at "+coords;
                }
            }),
            createSimple(new String[]{"delhome"}, "bowtie.home", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /delhome <name>";
                HomeUtility.Home home = HomeUtility.getHome(player.getUniqueId(), args[0]);
                if(home == null) return "<red>Invalid home";
                HomeUtility.deleteHome(player.getUniqueId(), args[0]);
                return "<green>Deleted home <aqua>"+args[0];
            }),
            createSimple(new String[]{"homes"}, "bowtie.home", (player, args) -> {
                List<HomeUtility.Home> homes = HomeUtility.getHomes(player.getUniqueId());
                if(homes.isEmpty()) return "<green>You have no homes.";
                return "<green>Your homes: <aqua>"+String.join(", ", homes.stream().map(HomeUtility.Home::name).toList());
            }),
            createCompleted(new String[]{"info"}, "bowtie.common", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /info <category>";
                String category = args[0].toLowerCase();
                if(!Bowtie.infoCmdConfig.getData().contains(category)) return "<red>Invalid category";
                List<String> info = Bowtie.infoCmdConfig.getData().getStringList(category);
                // send all lines
                for(String line : info) {
                    player.sendMessage(CaramelUtility.colorcomp(line));
                }
                return "";
            }, (list) -> {
                list.addAll(Bowtie.infoCmdConfig.getData().getKeys(false));
                return 0;
            }),
            createPlayerCompleted(new String[]{"tpoff"}, "bowtie.teleport", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /tpoff <player>";
                OfflinePlayer target = Bowtie.tie().getServer().getOfflinePlayer(args[0]);
                if(target.getLocation() == null) return "<red>Invalid player";
                if(target.isOnline()) return "<red>Player is online, use /tp";
                player.teleport(target.getLocation());
                return "<green>Teleported to <aqua>"+target.getName();
            }),
            createSimple(new String[]{"bowtiecommands"}, "bowtie.common", (player, args) -> {
                StringBuilder sb = new StringBuilder();
                Bowtie.tie().getDescription().getCommands().keySet().forEach(cmd -> sb.append(cmd).append(", "));
                if(!sb.isEmpty()) sb.setLength(sb.length()-2);
                if(sb.isEmpty()) sb.append("None");
                return "<green>Bowtie commands: <aqua>"+ sb;
            }, true),
            createSimple(new String[]{"up"}, "bowtie.teleport", (player, args) -> {
                Location loc = player.getLocation();
                // Find closest 2 block gap above player
                for(int i = loc.getBlockY(); i < 256; i++) {
                    if(loc.getWorld().getBlockAt(loc.getBlockX(), i, loc.getBlockZ()).isPassable() && loc.getWorld().getBlockAt(loc.getBlockX(), i+1, loc.getBlockZ()).isPassable()) {
                        loc.setY(i);
                        player.teleport(loc);
                        return "<green>Teleported up.";
                    }
                }
                return "<red>Unable to find a 2 block gap above you.";
            }),
            createSimple(new String[]{"down"}, "bowtie.teleport", (player, args) -> {
                Location loc = player.getLocation();
                // Find closest 2 block gap below player
                for(int i = loc.getBlockY(); i > 0; i--) {
                    if(loc.getWorld().getBlockAt(loc.getBlockX(), i, loc.getBlockZ()).isPassable() && loc.getWorld().getBlockAt(loc.getBlockX(), i+1, loc.getBlockZ()).isPassable()) {
                        loc.setY(i);
                        player.teleport(loc);
                        return "<green>Teleported down.";
                    }
                }
                return "<red>Unable to find a 2 block gap below you.";
            }),
            createSimple(new String[]{"biome"}, "bowtie.common", (player, args) -> "<green>You are standing in the biome: <aqua><lang:"+player.getLocation().getBlock().getBiome().translationKey()+">"),
            createSimple(new String[]{"afk"}, "bowtie.common", (player, args) -> {
                boolean afk = AFKManager.toggle(player.getUniqueId());
                return "<green>You are now "+(afk ? "AFK" : "no longer AFK");
            }),
            createSimple(new String[]{"afkother"}, "bowtie.admin", (player, args) -> {
                if(args.length < 1) return "<red>Usage: /afkother <player>";
                Player target = Bowtie.tie().getServer().getPlayer(args[0]);
                if(target == null) return "<red>Invalid player";
                boolean afk = AFKManager.toggle(target.getUniqueId());
                target.sendMessage(CaramelUtility.colorcomp("<green>You are now "+(afk ? "AFK" : "no longer AFK")));
                return "<green>"+target.getName()+" is now "+(afk ? "AFK" : "no longer AFK");
            }, true)
    ));

    public static CaramelCommand createSimple(String[] names, String permission, BiFunction<Player, String[], String> run, boolean consoleSupported) {
        return new CaramelCommand() {
            @Override
            public CaramelCommandDetail getDetails() {
                String add = "bowtie:"+names[0];
                String[] newNames = append(names, add);
                return new CaramelCommandDetail(names[0], permission, Bowtie.tie(), newNames);
            }

            @Override
            public void onPlayer(Player player, List<String> list) {
                player.sendMessage(CaramelUtility.colorcomp(run.apply(player, list.toArray(new String[0]))));
            }

            @Override
            public void onConsole(CommandSender commandSender, List<String> list) {
                if(consoleSupported) {
                    commandSender.sendMessage(CaramelUtility.colorcomp(run.apply(null, list.toArray(new String[0]))));
                }else {
                    commandSender.sendMessage("Bowtie does not support console senders yet.");
                }
            }
        };
    }
    public static CaramelCommand createSimple(String[] names, String permission, BiFunction<Player, String[], String> run) {
        return createSimple(names, permission, run, false);
    }
    public static CaramelCommand createCompleted(String[] names, String permission, BiFunction<Player, String[], String> run, Function<List<String>, Integer> complete) {
        return new CaramelCommand() {
            @Override
            public CaramelCommandDetail getDetails() {
                String add = "bowtie:"+names[0];
                String[] newNames = append(names, add);
                return new CaramelCommandDetail(names[0], permission, Bowtie.tie(), newNames);
            }

            @Override
            public void onPlayer(Player player, List<String> list) {
                player.sendMessage(CaramelUtility.colorcomp(run.apply(player, list.toArray(new String[0]))));
            }

            @Override
            public List<String> complete(String[] args) {
                List<String> list = new ArrayList<>();
                int i = complete.apply(list);
                return CaramelUtility.tabComplete(args[i], list);
            }

            @Override
            public void onConsole(CommandSender commandSender, List<String> list) {
                commandSender.sendMessage("Bowtie does not support console senders for tabcompleted commands");
            }
        };
    }
    public static String[] append(String[] array, String value) {
        String[] result = Arrays.copyOf(array, array.length + 1);
        result[result.length - 1] = value;
        return result;
    }
    public static CaramelCommand createCompleted(String[] names, String permission, BiFunction<Player, String[], String> run, BiFunction<List<String>, Integer, Integer> complete) {
        return new CaramelCommand() {
            @Override
            public CaramelCommandDetail getDetails() {
                String add = "bowtie:"+names[0];
                String[] newNames = append(names, add);
                return new CaramelCommandDetail(names[0], permission, Bowtie.tie(), newNames);
            }

            @Override
            public void onPlayer(Player player, List<String> list) {
                player.sendMessage(CaramelUtility.colorcomp(run.apply(player, list.toArray(new String[0]))));
            }

            @Override
            public List<String> complete(String[] args) {
                List<String> list = new ArrayList<>();
                int i = complete.apply(list, args.length);
                return CaramelUtility.tabComplete(args[i], list);
            }

            @Override
            public void onConsole(CommandSender commandSender, List<String> list) {
                commandSender.sendMessage("Bowtie does not support console senders yet for tabcompleted commands.");
            }
        };
    }

    public static CaramelCommand createPlayerCompleted(String[] names, String permission, BiFunction<Player, String[], String> run) {
        return createCompleted(names, permission, run, (list) -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                list.add(player.getName());
            }
            return 0;
        });
    }
}
