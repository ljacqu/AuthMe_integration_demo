package ch.jalu.authme.integrationdemo;

import ch.jalu.authme.integrationdemo.listener.AuthMeListener;
import ch.jalu.authme.integrationdemo.service.FireSwordService;
import ch.jalu.authme.integrationdemo.command.CommandException;
import ch.jalu.authme.integrationdemo.command.CommandImplementation;
import ch.jalu.authme.integrationdemo.command.FireSwordCommand;
import ch.jalu.authme.integrationdemo.command.ToggleBoatCommand;
import ch.jalu.authme.integrationdemo.listener.SampleListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Sample plugin.
 */
public class SamplePlugin extends JavaPlugin {

    private Map<String, CommandImplementation> commands;

    @Override
    public void onEnable() {
        final Logger logger = getLogger();
        final PluginManager pluginManager = getServer().getPluginManager();

        PluginDescriptionFile description = getDescription();
        logger.info(String.format("Initializing %s v%s", description.getVersion(), description.getVersion()));

        FireSwordService fireSwordService = new FireSwordService();

        commands = new HashMap<>();
        registerCommand(new ToggleBoatCommand());
        registerCommand(new FireSwordCommand(fireSwordService));

        SampleListener listener = new SampleListener(fireSwordService);
        pluginManager.registerEvents(listener, this);
        if (pluginManager.isPluginEnabled("AuthMe")) {
            logger.info("Hooking into AuthMe");
            pluginManager.registerEvents(new AuthMeListener(), this);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling plugin");
        commands = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

    private void registerCommand(CommandImplementation command) {
        commands.put(command.getLabel(), command);
    }
}
