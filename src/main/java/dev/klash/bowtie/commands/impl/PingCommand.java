package dev.klash.bowtie.commands.impl;

import dev.klash.bowtie.Bowtie;
import dev.klash.bowtie.utility.MiniMessageUtility;
import dev.klash.caramel.CaramelUtility;
import dev.klash.caramel.commands.CaramelCommand;
import dev.klash.caramel.commands.CaramelCommandDetail;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PingCommand implements CaramelCommand {
    @Override
    public CaramelCommandDetail getDetails() {
        return new CaramelCommandDetail("parsemm", "bowtie.admin", Bowtie.tie(), "ping");
    }

    void sendFormatted(Player p, String message) {
        p.sendMessage(CaramelUtility.colorcomp(message));
    }

    @Override
    public void onPlayer(Player player, List<String> list) {
        if(list.isEmpty()) {
            sendFormatted(player, "<red>Usage: /parsemm <message>");
            return;
        }
        sendFormatted(player, String.join(" ", list));
    }

    @Override
    public List<String> complete(String[] args) {
        String joined = String.join(" ", args);
        if(MiniMessageUtility.findLatestOpenTag(joined).isEmpty()) {
            List<String> comps = (MiniMessageUtility.getSuggestionsForFullString(joined));
            List<String> comp = new ArrayList<>();
            for (String c : comps) {
                comp.add("<" + c);
            }
            return CaramelUtility.tabComplete("<" + MiniMessageUtility.findLatestOpenTag(joined), comp);
        }else {
            List<String> comps = MiniMessageUtility.VALID_TAGS;
            List<String> comp = new ArrayList<>();
            for (String c : comps) {
                comp.add("<" + c);
            }
            return CaramelUtility.tabComplete("<", comp);
        }
    }

    @Override
    public void onConsole(CommandSender commandSender, List<String> list) {

    }
}
