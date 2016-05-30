package ch.jalu.authme.integrationdemo.command;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Common interface for commands in this plugin.
 */
public interface CommandImplementation {

    /**
     * Executes the command.
     *
     * @param sender the sender of the command
     * @param arguments the command arguments
     */
    void performCommand(CommandSender sender, List<String> arguments) throws CommandException;

    /**
     * @return the command label (all lower-case)
     */
    String getLabel();
}
