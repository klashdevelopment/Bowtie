package dev.klash.bowtie.feature.discord;

import dev.klash.bowtie.Bowtie;
import dev.klash.bowtie.utility.MiniMessageUtility;
import dev.klash.caramel.CaramelConfig;
import dev.klash.caramel.CaramelUtility;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.webhook.IncomingWebhook;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

public class DiscordCT {
    private DiscordApi api;
    private String token, channelID, serverID;
    private boolean enabled;
    private IncomingWebhook webhook;

    private DiscordMessagesConfig messages;

    public DiscordCT(CaramelConfig config) {
        FileConfiguration data = config.getData();
        this.token = data.getString("token");
        this.channelID = data.getString("channel-id");
        this.serverID = data.getString("server-id");
        this.enabled = data.getBoolean("enabled");
        this.messages = new DiscordMessagesConfig(config);
    }
    public boolean init() {
        if (!enabled) return false;
        Bowtie.tie().getLogger().info("Starting Discord handler");
        this.api = new DiscordApiBuilder().setToken(token).addIntents(Intent.MESSAGE_CONTENT).login().join();
        interact(c-> this.webhook = c.createWebhookBuilder().setName("Bowtie Chat Handler").create().join());
        interact(c-> c.sendMessage("Server is online"));
        SlashCommand.with("list", "Check who is online").createGlobal(api).join();
        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
            if (slashCommandInteraction.getCommandName().equals("list")) {
                slashCommandInteraction.createImmediateResponder()
                        .setContent("Online players: " + String.join(", ", Bukkit.getOnlinePlayers()
                                .stream()
                                .map(Player::getName)
                                .toList()))
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
            }
        });
        api.addMessageCreateListener(event -> {
            if(event.getChannel().getId() == Long.parseLong(this.channelID)){
                if (event.getMessageAuthor().isUser() && !event.getMessageAuthor().isWebhook() && !event.getMessageAuthor().isBotOwner()) {
                    String message = event.getMessageContent();
                    String playerName = event.getMessageAuthor().asUser().get().getName();
                    if(!message.trim().isEmpty()) {
                        Bukkit.getScheduler().runTask(Bowtie.tie(), () -> {
                            Bukkit.broadcast(CaramelUtility.colorcomp("<#5865F2>[Discord]<white> " + playerName + " » " + message));
                        });
                    }
                    for(int i = 0; i < event.getMessageAttachments().size(); i++) {
                        MessageAttachment att = event.getMessageAttachments().get(i);
                        if(!att.isImage()) continue;
                        int finalI = i;
                        Bukkit.getScheduler().runTask(Bowtie.tie(), () -> {
                            Bukkit.broadcast(CaramelUtility.colorcomp("<#5865F2>[Discord]<white> " + playerName + " » <aqua>("+(finalI +1)+"/"+(event.getMessageAttachments().size())+") <light_purple><hover:show_text:\"<aqua>Open cdn.discordapp.com link\"><click:open_url:\""+att.getUrl()+"\">View Image"));
                        });
                    }
                }
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
    public void onChatMessage(AsyncChatEvent message) {
        if(!enabled) return;
        Bukkit.getScheduler().runTask(Bowtie.tie(), () -> {
            try {
                webhook.updateAvatar(new URI("https://cravatar.eu/avatar/"+message.getPlayer().getName()+".png").toURL()).join();
            } catch (MalformedURLException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
            webhook.updateName(MiniMessageUtility.plain(message.getPlayer().displayName())).join();
            messages.send(MiniMessageUtility.plain(message.message()), webhook);
        });
    }
    public void onJoinPlayer(PlayerJoinEvent event) {
        if(!enabled) return;
        Bukkit.getScheduler().runTask(Bowtie.tie(), () -> {
            messages.send(MiniMessageUtility.plain(event.getPlayer().displayName()) + " joined the server", webhook);
        });
    }
    public void onQuitPlayer(PlayerQuitEvent event) {
        if(!enabled) return;
        Bukkit.getScheduler().runTask(Bowtie.tie(), () -> {
            messages.send(MiniMessageUtility.plain(event.getPlayer().displayName()) + " left the server", webhook);
        });
    }
}