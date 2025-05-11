package dev.klash.bowtie.chat;

import dev.klash.bowtie.Bowtie;
import dev.klash.caramel.CaramelUtility;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
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

    public static Component fix(Component input) {
        for(String replacement : Bowtie.tie().getConfig().getStringList("chat.replacements")) {
            @RegExp String from = replacement.split("\\|")[0];
            String to = replacement.split("\\|")[1];
            input = input.replaceText(TextReplacementConfig.builder()
                    .match(from)
                    .replacement(to)
                    .build());
        }
        return input;
    }
    public static @NotNull Component addPlaceholders(String s, Player player, TagResolver... res) {
        TagResolver prefix, suffix, displayname, name;

        if(Bowtie.vaultChat != null) {
            prefix = Placeholder.parsed("prefix", convert(Bowtie.vaultChat.getPlayerPrefix(player)));
            suffix = Placeholder.parsed("suffix", convert(Bowtie.vaultChat.getPlayerSuffix(player)));
        } else {
            prefix = Placeholder.parsed("prefix", "");
            suffix = Placeholder.parsed("suffix", "");
        }

        Component userDName = fix(player.displayName());

        if(Bowtie.nicksConfig.getData().contains(player.getUniqueId().toString())
                && !(Bowtie.nicksConfig.getData().getString(player.getUniqueId().toString()) == null)
                && !Bowtie.nicksConfig.getData().getString(player.getUniqueId().toString()).trim().isEmpty()) {
            userDName = CaramelUtility.colorcomp(Bowtie.nicksConfig.getData().getString(player.getUniqueId().toString()));
        }

        displayname = Placeholder.component("displayname", userDName);
        name = Placeholder.component("name", fix(player.name()));

        TagResolver[] resolvers = {prefix, suffix, displayname, name};
        resolvers = Arrays.copyOf(resolvers, resolvers.length + res.length);
        System.arraycopy(res, 0, resolvers, resolvers.length - res.length, res.length);

        return MiniMessage.miniMessage().deserialize(s, resolvers);
    }
    public static @NotNull Component addPlaceholdersWithoutVault(String s, Component displayName, Component userName, TagResolver... res) {
        TagResolver displayname, name;

        Component userDName = fix(displayName);

        displayname = Placeholder.component("displayname", userDName);
        name = Placeholder.component("name", fix(userName));

        TagResolver[] resolvers = {displayname, name};
        resolvers = Arrays.copyOf(resolvers, resolvers.length + res.length);
        System.arraycopy(res, 0, resolvers, resolvers.length - res.length, res.length);

        return MiniMessage.miniMessage().deserialize(s, resolvers);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(Bowtie.discordCT != null) Bowtie.discordCT.onJoinPlayer(event);
        event.joinMessage(addPlaceholders(Bowtie.tie().getConfig().getString("chat.join"), event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(Bowtie.discordCT != null) Bowtie.discordCT.onQuitPlayer(event);
        event.quitMessage(addPlaceholders(Bowtie.tie().getConfig().getString("chat.quit"), event.getPlayer()));
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) throws MalformedURLException, URISyntaxException {
        if(Bowtie.discordCT != null) Bowtie.discordCT.onChatMessage(event);
        event.renderer(new ChatRenderer() {
            @Override
            public @NotNull Component render(@NotNull Player player, @NotNull Component component, @NotNull Component component1, @NotNull Audience audience) {
                String format = Bowtie.tie().getConfig().getString("chat.format");

                TagResolver message;

                if(Bowtie.vaultPerms == null ? player.hasPermission("bowtie.chat.minimessage") : Bowtie.vaultPerms.has(player, "bowtie.chat.minimessage")) {
                    String msgString = PlainTextComponentSerializer.plainText().serialize(event.message());
                    message = Placeholder.parsed("message", msgString);
                }else {
                    message = Placeholder.component("message", fix(event.message()));
                }

                return addPlaceholders(format, player, message);
            }
        });
    }

}
