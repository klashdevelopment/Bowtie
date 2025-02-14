package dev.klash.bowtie.feature;

import dev.klash.bowtie.Bowtie;
import dev.klash.bowtie.chat.ChatListener;
import dev.klash.caramel.CaramelUtility;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BowtiePlaceholder extends PlaceholderExpansion {

    @Override
    @NotNull
    public String getAuthor() {
        return "GavinGoGaming";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "bowtie";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.contains("name")) {
            Component userDName;
            if(player.getPlayer() != null) {
                userDName = ChatListener.fix(player.getPlayer().displayName());
            } else {
                userDName = ChatListener.fix(Component.text(player.getName() == null ? "Unknown" : player.getName()));
            }

            if(Bowtie.nicksConfig.getData().contains(player.getUniqueId().toString())
                    && !(Bowtie.nicksConfig.getData().getString(player.getUniqueId().toString()) == null)
                    && !Bowtie.nicksConfig.getData().getString(player.getUniqueId().toString()).trim().isEmpty()) {
                userDName = CaramelUtility.colorcomp(Bowtie.nicksConfig.getData().getString(player.getUniqueId().toString()));
            }
            return LegacyComponentSerializer.legacy('&')
                    .serialize(userDName);
        }
        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        return onRequest(player, params);
    }
}