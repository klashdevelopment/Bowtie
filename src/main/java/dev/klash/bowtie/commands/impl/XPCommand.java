package dev.klash.bowtie.commands.impl;

import dev.klash.bowtie.Bowtie;
import dev.klash.caramel.CaramelUtility;
import dev.klash.caramel.commands.CaramelCommand;
import dev.klash.caramel.commands.CaramelCommandDetail;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class XPCommand implements CaramelCommand {
    @Override
    public CaramelCommandDetail getDetails() {
        return new CaramelCommandDetail("xp", "bowtie.admin", Bowtie.tie(), "exp", "experience");
    }

    void sendFormatted(Player p, String message) {
        p.sendMessage(CaramelUtility.colorcomp(message));
    }

    @Override
    public void onPlayer(Player player, List<String> list) {
        if(list.size() < 3) {
            sendFormatted(player, "<red>Usage: /xp <add/set/remove> <amount> <points/levels> [player]");
            return;
        }
        try {
            String action = list.get(0);
            int amount = Integer.parseInt(list.get(1));
            String type = list.get(2);

            if(!Arrays.asList("add", "set", "remove").contains(action) ||
               !Arrays.asList("points", "levels").contains(type)) {
                sendFormatted(player, "<red>Usage: /xp <add/set/remove> <amount> <points/levels> [player]");
                return;
            }

            Player target = player;
            if(list.size() == 4) {
                target = Bukkit.getPlayer(list.get(3));
                if(target == null) {
                    sendFormatted(player, "<red>Player not found.");
                    return;
                }
            }

            switch(action) {
                case "add":
                    if(type.equals("points")) {
                        target.giveExp(amount);
                    } else {
                        target.giveExpLevels(amount);
                    }
                    sendFormatted(player, "<green>Added " + amount + " " + type + " to " + target.getName() + ".");
                    break;
                case "set":
                    if(type.equals("points")) {
                        target.setExp(amount);
                    } else {
                        target.setLevel(amount);
                    }
                    sendFormatted(player, "<green>Set " + target.getName() + "'s " + type + " to " + amount + ".");
                    break;
                case "remove":
                    if(type.equals("points")) {
                        target.giveExp(-amount);
                    } else {
                        target.giveExpLevels(-amount);
                    }
                    sendFormatted(player, "<green>Removed " + amount + " " + type + " from " + target.getName() + ".");
                    break;
            }

        } catch(Exception e) {
            sendFormatted(player, "<red>Amount is not a number.");
            return;
        }

    }

    @Override
    public List<String> complete(String[] args) {
        if(args.length == 1) {
            return CaramelUtility.tabComplete(args[0], Arrays.asList("add", "set", "remove"));
        }
        else if (args.length == 2) {
            return CaramelUtility.tabComplete(args[1], Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
        }
        else if (args.length == 3) {
            return CaramelUtility.tabComplete(args[2], Arrays.asList("points", "levels"));
        }
        else if (args.length == 4) {
            return CaramelUtility.tabCompletePlayers(args[3], Bukkit.getOnlinePlayers());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void onConsole(CommandSender commandSender, List<String> list) {

    }
}
