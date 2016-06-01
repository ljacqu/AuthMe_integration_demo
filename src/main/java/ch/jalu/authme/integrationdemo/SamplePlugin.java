package ch.jalu.authme.integrationdemo;

import ch.jalu.authme.integrationdemo.command.CommandException;
import ch.jalu.authme.integrationdemo.command.CommandImplementation;
import ch.jalu.authme.integrationdemo.command.ExistsCommand;
import ch.jalu.authme.integrationdemo.command.FireSwordCommand;
import ch.jalu.authme.integrationdemo.listener.AuthMeListener;
import ch.jalu.authme.integrationdemo.listener.SampleListener;
import ch.jalu.authme.integrationdemo.service.AuthMeHook;
import ch.jalu.authme.integrationdemo.service.FireSwordService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Sample plugin.
 */
public class SamplePlugin extends JavaPlugin {

    private Map<String, CommandImplementation> commands;

    @Override
    public void onEnable() {
        SampleLogger.setLogger(getLogger());
        final PluginManager pluginManager = getServer().getPluginManager();

        PluginDescriptionFile description = getDescription();
        SampleLogger.info(String.format("Initializing %s v%s", description.getName(), description.getVersion()));

        FireSwordService fireSwordService = new FireSwordService();
        AuthMeHook authMeHook = new AuthMeHook(pluginManager);

        commands = new HashMap<>();
        registerCommand(new FireSwordCommand(fireSwordService));
        registerCommand(new ExistsCommand(authMeHook));

        SampleListener listener = new SampleListener(fireSwordService, authMeHook);
        pluginManager.registerEvents(listener, this);
        if (pluginManager.isPluginEnabled("AuthMe")) {
            SampleLogger.info("Hooking into AuthMe");
            pluginManager.registerEvents(new AuthMeListener(), this);
        }
    }

    @Override
    public void onDisable() {
        SampleLogger.info("Disabling plugin");
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
