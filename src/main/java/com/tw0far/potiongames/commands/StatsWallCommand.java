package com.tw0far.potiongames.commands;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;

/**
 * /pg headp1(2;3) and /pg signp1(2;3) - Set the head/sign for a
 * top-3 place on the stats wall.
 */
public class StatsWallCommand implements ICommand {
    private final PotionGamesX plugin;
    private final boolean head;
    private final int place;
    private final String name;

    public StatsWallCommand(PotionGamesX plugin, boolean head, int place) {
        this.plugin = plugin;
        this.head = head;
        this.place = place;
        this.name = (head ? "headp" : "signp") + place;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPermission() {
        return "pg.setup";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(CommandDispatcher.createError("This command can only be used by players!"));
            return true;
        }
        Block target = player.getTargetBlockExact(5);
        if (target == null) {
            player.sendMessage(Messages.HeadLookBlockText(place));
            return false;
        }
        plugin.getConfig().set("pg.RankWall." + (head ? "headp" : "signp") + place, target.getLocation());
        plugin.saveConfig();
        player.sendMessage(head ? Messages.HeadSetText(place) : Messages.SignSetText(place));
        return true;
    }

    @Override
    public String getUsage() {
        return head ? Messages.HelpHeadpUsageText(place) : Messages.HelpSignpUsageText(place);
    }
}
