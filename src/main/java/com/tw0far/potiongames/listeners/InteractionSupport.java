package com.tw0far.potiongames.listeners;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/** Small shared helpers for interaction listeners. */
final class InteractionSupport {
    private InteractionSupport() {
    }

    static String plainName(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return null;
        }
        Component displayName = meta.displayName();
        return displayName == null ? null : PlainTextComponentSerializer.plainText().serialize(displayName);
    }

    static int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
