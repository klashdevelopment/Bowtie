package dev.klash.bowtie.feature.discord;

import dev.klash.bowtie.utility.MiniMessageUtility;
import dev.klash.caramel.CaramelConfig;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.PlayerJoinEvent;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.webhook.IncomingWebhook;
import org.javacord.api.entity.webhook.Webhook;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Consumer;

public class DiscordCT {
    private DiscordApi api;
    private String token, channelID, serverID;
    private boolean enabled;
    private IncomingWebhook webhook;

    private DiscordMessagesConfig messages;

    public DiscordCT(CaramelConfig config) {
        FileConfiguration data = config.getData();
        this.token = data.getString("discord.token");
        this.channelID = data.getString("discord.channelID");
        this.serverID = data.getString("discord.serverID");
        this.enabled = data.getBoolean("discord.enabled");
        this.messages = new DiscordMessagesConfig(config);
    }
    public boolean init() {
        if (!enabled) return false;
        this.api = new DiscordApiBuilder().setToken(token).login().join();
        interact(c-> this.webhook = c.createWebhookBuilder().setName("Bowtie Chat Handler").create().join());
        SlashCommand.with("list", "Check who is online").createGlobal(api).join();
        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
            if (slashCommandInteraction.getCommandName().equals("list")) {
                slashCommandInteraction.createImmediateResponder()
                        .setContent("Pong!")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
            }
        });
        return true;
    }
    public void shutdown() {
        if(!enabled) return;
        if (api != null) {
            webhook.delete();
            api.disconnect();
        }
    }
    private void interact(Consumer<ServerTextChannel> run) {
        api.getServerById(serverID).flatMap(server -> server.getTextChannelById(channelID)).ifPresent(run);
    }
    public void onChatMessage(AsyncChatEvent message) throws URISyntaxException, MalformedURLException {
        if(!enabled) return;
        webhook.updateAvatar(new URI("https://cravatar.eu/avatar/"+message.getPlayer().getName()+".png").toURL());
        messages.send(MiniMessageUtility.plain(message.getPlayer().displayName()) + ": " + MiniMessageUtility.plain(message.message()), webhook);
    }
    public void onJoinPlayer(PlayerJoinEvent event) {
        if(!enabled) return;
        messages.send(MiniMessageUtility.plain(event.getPlayer().displayName()) + " joined the server", webhook);
    }
    public void onQuitPlayer(PlayerJoinEvent event) {
        if(!enabled) return;
        messages.send(MiniMessageUtility.plain(event.getPlayer().displayName()) + " left the server", webhook);
    }
}