package com.tw0far.potiongames.commands;

/**
 * Interface for individual command handlers.
 */
public interface ICommand {
    /**
     * Get the command name
     */
    String getName();

    /**
     * Get the required permission node
     */
    String getPermission();

    /**
     * Execute the command
     */
    boolean execute(org.bukkit.command.CommandSender sender, String[] args);

    /**
     * Get usage information
     */
    String getUsage();
}
