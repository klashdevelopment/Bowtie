package dev.klash.bowtie.feature.discord;

import dev.klash.caramel.CaramelConfig;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.webhook.IncomingWebhook;

import java.awt.*;

public class DiscordMessagesConfig {
    public String
    playerJoin, playerQuit, chat, die, advancement;

    public DiscordMessagesConfig(CaramelConfig conf) {
        this.playerJoin = conf.getData().getString("messages.player-join");
        this.playerQuit = conf.getData().getString("messages.player-quit");
        this.chat = conf.getData().getString("messages.chat");
    }

    /*
        * Converts a string to an embed builder
        * Uses "[EMBED]Title|Content|Color|ImageUrl" format
     */
    public static EmbedBuilder toEmbed(String message) throws IllegalStateException {
        if(!message.startsWith("[EMBED]")) throw new IllegalStateException("Message is not an embed!");
        message = message.substring(7);
        String[] parts = message.split("\\|");
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(parts[0]);
        embed.setDescription(parts[1]);
        embed.setColor(Color.decode(parts[2]));
        embed.setImage(parts[3]);
        return embed;
    }

    /*
        * Checks if a message is an embed
     */
    public static boolean isEmbed(String message) {
        return message.startsWith("[EMBED]");
    }

    protected void send(String message, IncomingWebhook webhook) {
        if(isEmbed(message)) {
            webhook.sendMessage(toEmbed(message));
        } else {
            webhook.sendMessage(message);
        }
    }
}
