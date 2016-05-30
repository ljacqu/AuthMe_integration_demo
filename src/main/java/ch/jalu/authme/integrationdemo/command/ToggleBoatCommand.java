package ch.jalu.authme.integrationdemo.command;

import org.bukkit.TreeSpecies;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

/**
 * Toggles the boat wood type the player is sitting in.
 */
public class ToggleBoatCommand implements CommandImplementation {

    private Random random = new Random();

    @Override
    public String getLabel() {
        return "toggleboat";
    }

    @Override
    public void performCommand(CommandSender sender, List<String> args) throws CommandException {
        Boat boat = getBoatOfPlayer(sender);
        TreeSpecies woodType = getWoodTypeFromArgs(args);
        boat.setWoodType(woodType);
    }

    private TreeSpecies getWoodTypeFromArgs(List<String> args) throws CommandException {
        if (args.isEmpty()) {
            return getRandomWoodType();
        }
        Byte number = toByte(args.get(0));
        if (number == null) {
            // arg is not a number, try to match as name
            return mapToTreeSpecies(args.get(0));
        } else {
            TreeSpecies woodType = TreeSpecies.getByData(number);
            if (woodType != null) {
                return woodType;
            }
            throw new CommandException("Invalid wood number, use 0-5 or the wood name");
        }
    }

    private TreeSpecies getRandomWoodType() {
        TreeSpecies[] values = TreeSpecies.values();
        return values[random.nextInt(values.length)];
    }

    private Boat getBoatOfPlayer(CommandSender sender) throws CommandException {
        Entity vehicle = toPlayer(sender).getVehicle();
        if (vehicle instanceof Boat) {
            return (Boat) vehicle;
        }
        throw new CommandException("You are not in a boat");
    }

    private static TreeSpecies mapToTreeSpecies(String text) throws CommandException {
        try {
            return TreeSpecies.valueOf(text.toUpperCase());
        } catch (RuntimeException e) {
            throw new CommandException("Wood type '" + text + "' does not exist");
        }
    }

    private static Player toPlayer(CommandSender sender) throws CommandException {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        throw new CommandException("Only players may run this command");
    }

    private static Byte toByte(String s) {
        try {
            return Byte.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
