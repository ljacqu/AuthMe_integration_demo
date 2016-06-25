package ch.jalu.authme.integrationdemo.command;

import ch.jalu.injector.AllInstances;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Command handler.
 */
public class CommandHandler {

    // List of commands (label -> command)
    private Map<String, CommandImplementation> commands;

    @Inject
    CommandHandler(@AllInstances CommandImplementation[] sampleCommands) {
        commands = new HashMap<>();
        for (CommandImplementation command : sampleCommands) {
            commands.put(command.getLabel(), command);
        }
    }

    public boolean onCommand(CommandSender sender, String label, String[] args) {
        CommandImplementation cmd = commands.get(label.toLowerCase());
        if (cmd == null) {
            return false;
        }
        try {
            cmd.performCommand(sender, Arrays.asList(args));
        } catch (CommandException e) {
            sender.sendMessage("Error: " + e.getMessage());
        }
        return true;
    }
}
