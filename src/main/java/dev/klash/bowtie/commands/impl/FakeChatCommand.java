package dev.klash.bowtie.commands.impl;

import dev.klash.bowtie.Bowtie;
import dev.klash.bowtie.utility.MiniMessageUtility;
import dev.klash.caramel.CaramelUtility;
import dev.klash.caramel.commands.CaramelCommand;
import dev.klash.caramel.commands.CaramelCommandDetail;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static dev.klash.bowtie.chat.ChatListener.addPlaceholdersWithoutVault;
import static dev.klash.bowtie.chat.ChatListener.fix;

public class FakeChatCommand implements CaramelCommand {
    public static HashMap<Player, String> fakePlayers = new HashMap<>();

    @Override
    public CaramelCommandDetail getDetails() {
        return new CaramelCommandDetail("fakechat", "bowtie.troll", Bowtie.tie(), "fc");
    }

    public String combineArgs(int start, List<String> list) {
        StringBuilder sb = new StringBuilder();
        for(int i = start; i < list.size(); i++) {
            sb.append(list.get(i)).append(" ");
        }
        return sb.toString().trim();
    }

    void sendRed(Player p, String m) {
        p.sendMessage(CaramelUtility.colorcomp("<red>" + m));
    }

    @Override
    public void onPlayer(Player player, List<String> list) {
        if(list.size() == 0) {
            sendRed(player, "Usage: /fakechat help");
            return;
        }
        if(list.get(0).equalsIgnoreCase("login")) {
            if(list.size() < 2) {
                sendRed(player, "Usage: /fakechat login <name>");
                return;
            }
            fakePlayers.put(player, list.get(1));
            Bukkit.broadcast(addPlaceholdersWithoutVault(Bowtie.tie().getConfig().getString("chat.join"), CaramelUtility.colorcomp(fakePlayers.get(player)), CaramelUtility.colorcomp(fakePlayers.get(player))));
            player.sendMessage(CaramelUtility.colorcomp("<green>Logged in as <aqua>" + list.get(1)));
        } else if(list.get(0).equalsIgnoreCase("say")) {
            if(list.size() < 2) {
                sendRed(player, "Usage: /fakechat say <message>");
                return;
            }
            if(!fakePlayers.containsKey(player)) {
                sendRed(player, "You need to set a name (/fakechat login) first!");
                return;
            }
            String message = combineArgs(1, list);
            String format = Bowtie.tie().getConfig().getString("chat.format");
            TagResolver messageResolver = Placeholder.parsed("message", message);
            Bukkit.broadcast(addPlaceholdersWithoutVault(format, CaramelUtility.colorcomp(fakePlayers.get(player)), CaramelUtility.colorcomp(fakePlayers.get(player)), messageResolver));
        } else if(list.get(0).equalsIgnoreCase("logout")) {
            fakePlayers.remove(player);
            Bukkit.broadcast(addPlaceholdersWithoutVault(Bowtie.tie().getConfig().getString("chat.quit"), CaramelUtility.colorcomp(fakePlayers.get(player)), CaramelUtility.colorcomp(fakePlayers.get(player))));
            sendRed(player, "Logged out");
        } else if(list.get(0).equalsIgnoreCase("help")) {
            sendRed(player, "(/fc ...) /fakechat login <player>, /fakechat say <message>, /fakechat logout");
        } else {
            sendRed(player, "Usage: /fakechat help");
        }
    }

    @Override
    public void onConsole(CommandSender commandSender, List<String> list) {
        commandSender.sendMessage("Cant use this command from console!");
    }
}
