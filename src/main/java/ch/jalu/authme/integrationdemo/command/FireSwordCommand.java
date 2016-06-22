package ch.jalu.authme.integrationdemo.command;

import ch.jalu.authme.integrationdemo.service.FireSwordService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.List;

/**
 * Command to toggle a golden sword as a fire sword.
 */
public class FireSwordCommand implements CommandImplementation {

    @Inject
    private FireSwordService service;

    FireSwordCommand() { }

    @Override
    public String getLabel() {
        return "firesword";
    }

    @Override
    public void performCommand(CommandSender sender, List<String> arguments) throws CommandException {
        Player player = arguments.isEmpty()
                ? toPlayer(sender)
                : getPlayerOrFail(arguments.get(0));
        boolean newState = service.toggleFor(player.getName());
        if (newState) {
            sender.sendMessage("Fire sword enabled for " + player.getName());
        } else {
            sender.sendMessage("Fire sword disabled for " + player.getName());
        }
    }

    private static Player getPlayerOrFail(String name) throws CommandException {
        Player player = Bukkit.getPlayerExact(name);
        if (player == null) {
            throw new CommandException("Cannot find player '" + name + "'");
        }
        return player;
    }

    private static Player toPlayer(CommandSender sender) throws CommandException {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        throw new CommandException("Please specify a player");
    }
}
