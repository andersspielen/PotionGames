package com.tw0far.potiongames.commands;

import org.bukkit.command.CommandSender;
import com.tw0far.potiongames.PotionGamesX;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * /pg debug - Toggle verbose debug logging for troubleshooting
 */
public class DebugCommand implements ICommand {
    private boolean debugMode = false;

    public DebugCommand(PotionGamesX plugin) {
    }

    @Override
    public String getName() {
        return "debug";
    }

    @Override
    public String getPermission() {
        return "pg.debug";
    }


    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof org.bukkit.entity.Player player)) {
            sender.sendMessage(net.kyori.adventure.text.Component.text("This command can only be used by players!").color(net.kyori.adventure.text.format.NamedTextColor.RED));
            return true;
        }
        debugMode = !debugMode;

        Component message = Component.text("Debug Mode: ").color(NamedTextColor.GOLD)
            .append(Component.text(debugMode ? "ENABLED" : "DISABLED")
                .color(debugMode ? NamedTextColor.GREEN : NamedTextColor.RED));

        player.sendMessage(message);

        if (debugMode) {
            player.sendMessage(Component.text("Verbose logging enabled. Check server console for debug output.")
                .color(NamedTextColor.GRAY));
        }

        return true;
    }

    @Override
    public String getUsage() {
        return "/pg debug";
    }
}
