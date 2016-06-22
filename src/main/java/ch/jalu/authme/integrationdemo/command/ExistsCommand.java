package ch.jalu.authme.integrationdemo.command;

import ch.jalu.authme.integrationdemo.service.AuthMeHook;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;
import java.util.List;

/**
 * Command checking whether or not a given username is registered in AuthMe.
 */
public class ExistsCommand implements CommandImplementation {

    @Inject
    private AuthMeHook authMeHook;

    ExistsCommand() { }

    @Override
    public String getLabel() {
        return "exists";
    }

    @Override
    public void performCommand(CommandSender sender, List<String> arguments) throws CommandException {
        if (arguments.isEmpty()) {
            throw new CommandException("Please enter a name, e.g. /exists username");
        } else if (!authMeHook.isHookActive()) {
            throw new CommandException("Not hooked into AuthMe");
        }

        boolean doesUserExist = authMeHook.isNameRegistered(arguments.get(0));
        if (doesUserExist) {
            sender.sendMessage("Name '" + arguments.get(0) + "' is registered");
        } else {
            sender.sendMessage("Name '" + arguments.get(0) + "' is not yet registered");
        }
    }
}
