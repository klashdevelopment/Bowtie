package dev.klash.bowtie.utility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;

import java.util.function.UnaryOperator;

public class ChatUtility {

    public static Component itemToComponent(ItemStack item, String append, String prepend) {
        Component component =
                MiniMessage.miniMessage().deserialize(prepend)
                .append(Component.translatable(item.getType().translationKey()))
                .append(MiniMessage.miniMessage().deserialize(append));
        return component.hoverEvent(item.asHoverEvent(UnaryOperator.identity()));
    }
    public static Component itemsToComponent(ItemStack[] items, String color, String color2) {
        Component component = Component.empty();
        for (int i = 0; i < items.length; i++) {
            component = component.append(itemToComponent(items[i], i == items.length - 1 ? "" : "<"+color2+">, ", "<"+color+">"));
        }
        return component;
    }
}
