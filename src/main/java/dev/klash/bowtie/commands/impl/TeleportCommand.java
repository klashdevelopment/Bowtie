package dev.klash.bowtie.commands.impl;

import dev.klash.bowtie.Bowtie;
import dev.klash.caramel.CaramelUtility;
import dev.klash.caramel.commands.CaramelCommand;
import dev.klash.caramel.commands.CaramelCommandDetail;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TeleportCommand implements CaramelCommand {
    @Override
    public CaramelCommandDetail getDetails() {
        return new CaramelCommandDetail("tp", "bowtie.teleport", Bowtie.tie(), "teleport");
    }

    void sendFormatted(Player p, String message) {
        p.sendMessage(CaramelUtility.colorcomp(message));
    }

    public List<Double> vectorToDoubleList(Location location) {
        return Arrays.asList(location.getX(), location.getY(), location.getZ());
    }
    public double[] parseCoords(String[] args, Location currentLocation) {
//        use currentLocation to parse ~
        double[] coords = new double[3];
        for(int i = 0; i < 3; i++) {
            if(args[i].equals("~")) {
                coords[i] = vectorToDoubleList(currentLocation).get(i);
            } else if(args[i].startsWith("~")) {
                coords[i] = vectorToDoubleList(currentLocation).get(i) + Double.parseDouble(args[i].substring(1));
            } else {
                coords[i] = Double.parseDouble(args[i]);
            }
        }
        return coords;
    }

    @Override
    public void onPlayer(Player player, List<String> list) {
// /tp [player] [x] [y] [z]
        // All use cases: /tp player x y z, /tp x y z, /tp player.
        if(list.isEmpty()) {
            sendFormatted(player, "<red>Usage: /tp [player] [x] [y] [z]");
            return;
        }
        if(list.size() == 1) {
            if(player.hasPermission("bowtie.admin")) {
                Player target = player.getServer().getPlayer(list.get(0));
                if(target != null) {
                    player.teleport(target);
                    sendFormatted(player, "<green>Teleported to <aqua>"+target.getName());
                } else {
                    sendFormatted(player, "<red>Player not found");
                }
            } else {
                sendFormatted(player, "<red>Insufficient permissions");
            }
        } else if(list.size() == 3) {
            try {
                double[] coords = parseCoords(list.toArray(new String[0]), player.getLocation());
                double x = coords[0];
                double y = coords[1];
                double z = coords[2];
                player.teleport(player.getWorld().getBlockAt((int)x, (int)y, (int)z).getLocation());
                sendFormatted(player, "<green>Teleported to <aqua>"+x+" "+y+" "+z);
            } catch(Exception e) {
                sendFormatted(player, "<red>Invalid arguments");
            }
        } else if(list.size() == 4) {
            if(player.hasPermission("bowtie.admin")) {
                Player target = list.get(0).equalsIgnoreCase("@s") ? player : player.getServer().getPlayer(list.get(0));
                if(target != null) {
                    try {
                        double[] coords = parseCoords(list.subList(1, 4).toArray(new String[0]), target.getLocation());
                        double x = coords[0];
                        double y = coords[1];
                        double z = coords[2];
                        target.teleport(target.getWorld().getBlockAt((int)x, (int)y, (int)z).getLocation());
                        sendFormatted(player, "<green>Teleported <aqua>"+target.getName()+" <green>to <aqua>"+x+" "+y+" "+z);
                    } catch(Exception e) {
                        sendFormatted(player, "<red>Invalid arguments");
                    }
                } else {
                    sendFormatted(player, "<red>Player not found");
                }
            } else {
                sendFormatted(player, "<red>Insufficient permissions");
            }
        } else {
            sendFormatted(player, "<red>Invalid arguments");
        }
    }

    @Override
    public List<String> complete(String[] args) {
//        first arg should be player names unless first arg is already a number, in which case nothing
        if(args.length == 1) {
            if(args[0].matches("[0-9]+")) {
                return Collections.emptyList();
            } else {
                return CaramelUtility.tabCompletePlayers(args[0], Bowtie.tie().getServer().getOnlinePlayers());
            }
        }
        else {
            return Collections.emptyList();
        }
    }

    @Override
    public void onConsole(CommandSender commandSender, List<String> list) {

    }
}
