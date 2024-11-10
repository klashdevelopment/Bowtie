package dev.klash.bowtie.commands.impl;

import dev.klash.bowtie.Bowtie;
import dev.klash.caramel.CaramelUtility;
import dev.klash.caramel.commands.CaramelCommand;
import dev.klash.caramel.commands.CaramelCommandDetail;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemCommand implements CaramelCommand {
    @Override
    public CaramelCommandDetail getDetails() {
        return new CaramelCommandDetail("item", "bowtie.admin", Bowtie.tie(), "i", "get");
    }

    void sendFormatted(Player p, String message) {
        p.sendMessage(CaramelUtility.colorcomp(message));
    }

    @Override
    public void onPlayer(Player player, List<String> list) {
//        /item <id> <amount>
        if(list.isEmpty()) {
            sendFormatted(player, "<red>Usage: /item <id> [amount]");
            return;
        }
        try {
            int amount = 1;
            if(list.size() > 1) {
                amount = Integer.parseInt(list.get(1));
            }

            if(!Bowtie.materialNames.containsKey(list.get(0).toLowerCase())) {
                sendFormatted(player, "<red>Invalid item id");
                return;
            }

            Material mat = Bowtie.materialNames.get(list.get(0).toLowerCase());
            if(mat == null) {
                sendFormatted(player, "<red>Couldn't find vanilla item matching \""+list.get(0).toLowerCase()+"\". Are we using essentials item names?");
                return;
            }
            player.getInventory().addItem(new ItemStack(mat, amount));
            sendFormatted(player, "<green>Giving <aqua>"+amount+"x "+mat.name());
        } catch(Exception e) {
            sendFormatted(player, "<red>Invalid arguments");
        }
    }

    @Override
    public List<String> complete(String[] args) {
//        first arg should be all the material names
        if(args.length == 1) {
            return CaramelUtility.tabComplete(args[0], Arrays.asList(Bowtie.materialNames.keySet().toArray(new String[]{})));
        }
        else /*if(args.length == 0 || args.length == 2)*/ {
            return Collections.emptyList();
        }
    }

    @Override
    public void onConsole(CommandSender commandSender, List<String> list) {

    }
}
