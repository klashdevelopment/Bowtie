package dev.klash.bowtie.commands.impl;

import dev.klash.bowtie.Bowtie;
import dev.klash.caramel.CaramelUtility;
import dev.klash.caramel.commands.CaramelCommand;
import dev.klash.caramel.commands.CaramelCommandDetail;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.util.BiomeSearchResult;
import org.bukkit.util.StructureSearchResult;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class LocateCommand implements CaramelCommand {
    @Override
    public CaramelCommandDetail getDetails() {
        return new CaramelCommandDetail("locate", "bowtie.admin", Bowtie.tie(), "bowtie:locate");
    }

    void sendFormatted(Player p, String message) {
        p.sendMessage(CaramelUtility.colorcomp(message));
    }

    public int distance(Location a, Location b) {
        return (int) Math.floor(a.distance(b));
    }

    @Override
    public void onPlayer(Player player, List<String> list) {
        // /locate <biome/poi/structure> <(minecraft:)name>
        BiFunction<String, List<String>, Boolean> usage = (thing, things) -> {
            boolean x = things.contains(thing);
            if(!x) sendFormatted(player, "<red>Usage: /locate <biome/structure/raid> <(minecraft:)name>");
            return x;
        };

        if(list.isEmpty()) {
            sendFormatted(player, "<red>No args. /locate <biome/structure/raid> <(minecraft:)name>");
            return;
        }
        String type = list.get(0);
        if(!usage.apply(type, Arrays.asList("biome", "structure", "raid"))) return;
        String name = list.size() < 2 ? "" : list.get(1);

        int x, y, z, d;

        switch(type) {
            case "biome":
                if(list.size() < 2) {
                    sendFormatted(player, "<red>Usage: /locate biome <biome>");
                    return;
                }
                Biome biome = Registry.BIOME.get(NamespacedKey.minecraft(name));
                if(biome == null) {
                    sendFormatted(player, "<red>Biome not found.");
                    return;
                }
                BiomeSearchResult result = player.getWorld().locateNearestBiome(player.getLocation(), 3500, biome);
                if(result == null) {
                    sendFormatted(player, "<red>None found.");
                    return;
                }
                Location location = result.getLocation();
                x = location.getBlockX(); y = location.getBlockY(); z = location.getBlockZ(); d = distance(location, player.getLocation());
                sendFormatted(player, "<green>Nearest " + biome.name() + " is at <aqua><click:suggest_command:/tp @s "+x+" "+y+" "+z+">["+x+", "+y+", "+z+"]</click><green> ("+d+" blocks away)");
                break;
            case "structure":
                if(list.size() < 2) {
                    sendFormatted(player, "<red>Usage: /locate structure <structure>");
                    return;
                }
                StructureType structure = Registry.STRUCTURE_TYPE.get(NamespacedKey.minecraft(name));
                if(structure == null) {
                    sendFormatted(player, "<red>Structure not found.");
                    return;
                }
                StructureSearchResult searchRes = player.getWorld().locateNearestStructure(player.getLocation(), structure, 3500, false);
                if(searchRes == null) {
                    sendFormatted(player, "<red>None found.");
                    return;
                }
                Location loc = searchRes.getLocation();
                x = loc.getBlockX(); y = loc.getBlockY(); z = loc.getBlockZ(); d = distance(loc, player.getLocation());
                sendFormatted(player, "<green>Nearest " + structure.getKey().value() + " is at <aqua><click:suggest_command:/tp @s "+x+" "+y+" "+z+">["+x+", "+y+", "+z+"]</click><green> ("+d+" blocks away)");
                break;
            case "raid":
                Raid raid = player.getWorld().locateNearestRaid(player.getLocation(), 3500);
                if(raid == null) {
                    sendFormatted(player, "<red>None found.");
                    return;
                }
                Location loca = raid.getLocation();
                x = loca.getBlockX(); y = loca.getBlockY(); z = loca.getBlockZ(); d = distance(loca, player.getLocation());
                sendFormatted(player, "<green>Nearest raid is at <aqua><click:suggest_command:/tp @s "+x+" "+y+" "+z+">["+x+", "+y+", "+z+"]</click><green> ("+d+" blocks away)");
                break;
            default:
                sendFormatted(player, "<red>Type not found: /locate <biome/structure/raid> <(minecraft:)name>");
                break;
        }
    }

    @Override
    public List<String> complete(String[] args) {
        if(args.length == 1) {
            return CaramelUtility.tabComplete(args[0], Arrays.asList("biome", "structure", "raid"));
        } else if(args.length == 2) {
            List<String> options = new ArrayList<>();
            switch(args[0]) {
                case "biome":
                    options.clear();
                    options.addAll(Registry.BIOME.stream().map(Biome::getKey).map(Key::value).toList());
                    break;
                case "structure":
                    options.clear();
                    options.addAll(Registry.STRUCTURE_TYPE.stream().map(StructureType::getKey).map(Key::value).toList());
                    break;
            }
            return CaramelUtility.tabComplete(args[1], options);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void onConsole(CommandSender commandSender, List<String> list) {

    }
}
