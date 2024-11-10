package dev.klash.bowtie.commands.impl;

import dev.klash.bowtie.Bowtie;
import dev.klash.caramel.CaramelUtility;
import dev.klash.caramel.commands.CaramelCommand;
import dev.klash.caramel.commands.CaramelCommandDetail;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.intellij.lang.annotations.Subst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EffectCommand implements CaramelCommand {
    @Override
    public CaramelCommandDetail getDetails() {
        return new CaramelCommandDetail("effect", "bowtie.admin", Bowtie.tie());
    }

    void sendFormatted(Player p, String message) {
        p.sendMessage(CaramelUtility.colorcomp(message));
    }

    @Override
    public void onPlayer(Player player, List<String> list) {
        if(list.size() < 2) {
            sendFormatted(player, "<red>Usage: /effect <give/clear> <user/@a/@s> <effect> [duration] [amplifier] [hideParticles]");
            return;
        }
        try {
            String action = list.get(0);
            if(!Arrays.asList("give", "clear").contains(action)) {
                sendFormatted(player, "<red>Usage: /effect <give/clear> <user/@a/@s> <effect> [duration] [amplifier] [hideParticles]");
                return;
            }
            List<Player> targets = new ArrayList<>();
            if (list.get(1).equalsIgnoreCase("@a")) {
                targets.addAll(Bukkit.getOnlinePlayers());
            } else if (list.get(1).equalsIgnoreCase("@s")) {
                targets.add(player);
            } else {
                Player target = Bukkit.getPlayer(list.get(1));
                if(target == null) {
                    sendFormatted(player, "<red>Player not found.");
                    return;
                }
                targets.add(target);
            }
            PotionEffectType effect = null;
            if(list.size() > 2) {
                String key, namespace;
                if(list.get(2).contains(":")) {
                    key = list.get(2).split(":")[1];
                    namespace = list.get(2).split(":")[0];
                }else {
                    key = list.get(2);
                    namespace = "minecraft";
                }
                effect = Registry.POTION_EFFECT_TYPE.get(Key.key(namespace, key));
            }

            if(action.equalsIgnoreCase("give")) {
                if (effect == null) {
                    sendFormatted(player, "<red>Effect not found.");
                    return;
                }
                if(list.size() > 3) {
                    int duration = Integer.parseInt(list.get(3));
                    if(list.size() > 4) {
                        int amplifier = Integer.parseInt(list.get(4));
                        if(list.size() > 5) {
                            boolean hideParticles = Boolean.parseBoolean(list.get(5));
                            for (Player target : targets) {
                                target.addPotionEffect(new PotionEffect(effect, duration, amplifier, false,  hideParticles));
                            }sendFormatted(player, "<green>Added effect <aqua>" + effect.key().value() + "<green> to <aqua>" + list.get(1) + ".");
                            return;
                        }
                        for (Player target : targets) {
                            target.addPotionEffect(new PotionEffect(effect, duration, amplifier));
                        }sendFormatted(player, "<green>Added effect <aqua>" + effect.key().value() + "<green> to <aqua>" + list.get(1) + ".");
                        return;
                    }
                    for (Player target : targets) {
                        target.addPotionEffect(new PotionEffect(effect, duration, 0));
                    }sendFormatted(player, "<green>Added effect <aqua>" + effect.key().value() + "<green> to <aqua>" + list.get(1) + ".");
                }else {
                    for (Player target : targets) {
                        target.addPotionEffect(new PotionEffect(effect, 30*20, 0));
                    }sendFormatted(player, "<green>Added effect <aqua>" + effect.key().value() + "<green> to <aqua>" + list.get(1) + ".");
                }
            }else {
                for (Player target : targets) {
                    if(effect == null) {
                        target.getActivePotionEffects().clear();
                    } else {
                        target.removePotionEffect(effect);
                    }
                }
                sendFormatted(player, "<green>Removed effect <aqua>" + (effect != null ? effect.getKey().asString() : "all") + "<green> from " + list.get(1) + ".");
                return;
            }

        } catch(Exception e) {
            Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).forEach(player::sendMessage);
            return;
        }

    }

    @Override
    public List<String> complete(String[] args) {
        if(args.length == 1) {
            return CaramelUtility.tabComplete(args[0], Arrays.asList("give", "clear"));
        }
        else if (args.length == 2) {
            List<String> thing = new ArrayList<>(Arrays.asList("@a", "@s"));
            thing.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
            return CaramelUtility.tabComplete(args[1], thing);
        }
        else if (args.length == 3) {
            List<String> thing = new ArrayList<>(Registry.POTION_EFFECT_TYPE.stream().map(PotionEffectType::getKey).map(Key::asString).toList());
            Registry.POTION_EFFECT_TYPE.forEach(effectType -> {
                if(effectType.key().namespace().equalsIgnoreCase("minecraft")) {
                    thing.add(effectType.key().value());
                }
            });
            return CaramelUtility.tabComplete(args[2], thing);
        }
        else if (args.length == 6) {
            return CaramelUtility.tabComplete(args[5], Arrays.asList("true", "false"));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void onConsole(CommandSender commandSender, List<String> list) {

    }
}
