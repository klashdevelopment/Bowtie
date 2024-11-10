package dev.klash.bowtie.chat;

import dev.klash.bowtie.Bowtie;
import dev.klash.caramel.Caramel;
import dev.klash.caramel.CaramelUtility;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    public static String convert(String legacy, boolean concise, char charPrefix, boolean rgb) {
        String miniMessage = legacy
                .replace(charPrefix + "0", "<black>")
                .replace(charPrefix + "1", "<dark_blue>")
                .replace(charPrefix + "2", "<dark_green>")
                .replace(charPrefix + "3", "<dark_aqua>")
                .replace(charPrefix + "4", "<dark_red>")
                .replace(charPrefix + "5", "<dark_purple>")
                .replace(charPrefix + "6", "<gold>")
                .replace(charPrefix + "7", "<gray>")
                .replace(charPrefix + "8", "<dark_gray>")
                .replace(charPrefix + "9", "<blue>")
                .replace(charPrefix + "a", "<green>")
                .replace(charPrefix + "b", "<aqua>")
                .replace(charPrefix + "c", "<red>")
                .replace(charPrefix + "d", "<light_purple>")
                .replace(charPrefix + "e", "<yellow>")
                .replace(charPrefix + "f", "<white>");

        if (concise) {
            miniMessage = miniMessage
                    .replace(charPrefix + "n", "<u>")
                    .replace(charPrefix + "m", "<st>")
                    .replace(charPrefix + "k", "<obf>")
                    .replace(charPrefix + "o", "<i>")
                    .replace(charPrefix + "l", "<b>")
                    .replace(charPrefix + "r", "<r>");
        } else {
            miniMessage = miniMessage
                    .replace(charPrefix + "n", "<underlined>")
                    .replace(charPrefix + "m", "<strikethrough>")
                    .replace(charPrefix + "k", "<obfuscated>")
                    .replace(charPrefix + "o", "<italic>")
                    .replace(charPrefix + "l", "<bold>")
                    .replace(charPrefix + "r", "<reset>");
        }

        if (rgb) {
            Pattern pattern = Pattern.compile(charPrefix + "#([0-9a-fA-F]{6})");
            Matcher matcher = pattern.matcher(miniMessage);
            miniMessage = matcher.replaceAll("<#$1>");
        }

        return miniMessage;
    }

    public static String convert(String legacy) {
        return convert(legacy == null ? "" : legacy, false, '&', true);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.renderer(new ChatRenderer() {
            @Override
            public @NotNull Component render(@NotNull Player player, @NotNull Component component, @NotNull Component component1, @NotNull Audience audience) {
                String format = Bowtie.tie().getConfig().getString("chat.format");

                TagResolver prefix, suffix, displayname, name, message;

                if(Bowtie.vaultChat != null) {
                    prefix = Placeholder.parsed("prefix", convert(Bowtie.vaultChat.getPlayerPrefix(player)));
                    suffix = Placeholder.parsed("suffix", convert(Bowtie.vaultChat.getPlayerSuffix(player)));
                } else {
                    prefix = Placeholder.parsed("prefix", "");
                    suffix = Placeholder.parsed("suffix", "");
                }

                Component userDName = player.displayName();
                if(Bowtie.nicksConfig.getData().contains(player.getUniqueId().toString())
                        && !(Bowtie.nicksConfig.getData().getString(player.getUniqueId().toString()) == null)
                        && !Bowtie.nicksConfig.getData().getString(player.getUniqueId().toString()).trim().isEmpty()) {
                    userDName = CaramelUtility.colorcomp(Bowtie.nicksConfig.getData().getString(player.getUniqueId().toString()));
                }

                displayname = Placeholder.component("displayname", userDName);
                name = Placeholder.component("name", player.name());

                if(Bowtie.vaultPerms == null ? player.hasPermission("bowtie.chat.minimessage") : Bowtie.vaultPerms.has(player, "bowtie.chat.minimessage")) {
                    String msgString = PlainTextComponentSerializer.plainText().serialize(event.message());
                    message = Placeholder.parsed("message", msgString);
                }else {
                    message = Placeholder.component("message", event.message());
                }

                return MiniMessage.miniMessage().deserialize(format, prefix, suffix, displayname, name, message);
            }
        });
    }

}
