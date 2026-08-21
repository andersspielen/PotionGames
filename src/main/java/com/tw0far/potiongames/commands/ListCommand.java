package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.GameStates;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Lobby;
import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.models.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * /pg list - Open GUI with all lobbies
 */
public class ListCommand implements ICommand {
    private final PotionGamesX plugin;

    public ListCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getPermission() {
        return "pg.join";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(net.kyori.adventure.text.Component.text("This command can only be used by players!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return true;
        }
        // Build an inventory GUI listing all lobbies with basic info
        List<Lobby> lobbies = plugin.getGame().getLobbies();
        if (lobbies == null || lobbies.isEmpty()) {
            player.sendMessage(Messages.ListNoLobbiesText());
            return true;
        }

        int size = ((lobbies.size() + 8) / 9) * 9; // round up to multiple of 9
        Inventory inv = Bukkit.createInventory(null, Math.max(9, size), Messages.LobbyListTitle());

        for (Lobby lobby : lobbies) {
            int id = lobby.getId();
            int active = lobby.getActivePlayers().size();
            int max = lobby.getMaxPlayers();

            ItemStack item = new ItemStack(Material.CHEST);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                // Display name MUST be the numeric lobby id so existing click handler can parse it
                meta.displayName(Component.text(Integer.toString(id)).color(NamedTextColor.GOLD));
                List<Component> lore = new ArrayList<>();
                NamedTextColor stateColor = lobby.getState() == GameStates.WAITING || lobby.getState() == GameStates.PREPARING ? NamedTextColor.GREEN : NamedTextColor.RED;
                lore.add(Component.text(lobby.getState().toString()).color(stateColor));
                if (lobby.getCurrentArena() != null) {
                    lore.add(Component.text(lobby.getCurrentArena().getName()).color(NamedTextColor.GOLD));
                } else {
                    lore.add(Component.text("VOTING").color(NamedTextColor.AQUA));
                }
                lore.add(Component.text("[" + active + "/" + max + "]").color(NamedTextColor.GRAY));
                meta.lore(lore);
                item.setItemMeta(meta);
            }
            inv.addItem(item);
        }

        if (inv != null) {
            player.openInventory(inv);
        }
        return true;
    }

    @Override
    public String getUsage() {
        return Messages.HelpListUsageText();
    }
}

