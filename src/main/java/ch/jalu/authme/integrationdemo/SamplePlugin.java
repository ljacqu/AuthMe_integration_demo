package ch.jalu.authme.integrationdemo;

import ch.jalu.authme.integrationdemo.command.CommandException;
import ch.jalu.authme.integrationdemo.command.CommandImplementation;
import ch.jalu.authme.integrationdemo.command.ExistsCommand;
import ch.jalu.authme.integrationdemo.command.FireSwordCommand;
import ch.jalu.authme.integrationdemo.listener.AuthMeListener;
import ch.jalu.authme.integrationdemo.listener.BukkitListener;
import ch.jalu.authme.integrationdemo.service.AuthMeHook;
import ch.jalu.authme.integrationdemo.service.FireSwordService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Sample plugin.
 */
public class SamplePlugin extends JavaPlugin {

    // List of commands (label -> command)
    private Map<String, CommandImplementation> commands;

    // AuthMe integration
    private Listener authMeListener;
    private AuthMeHook authMeHook;

    @Override
    public void onEnable() {
        SampleLogger.setLogger(getLogger());
        final PluginManager pluginManager = getServer().getPluginManager();

        // Initialize services
        FireSwordService fireSwordService = new FireSwordService();
        authMeHook = new AuthMeHook();

        // Initialize commands
        registerCommands(
            new FireSwordCommand(fireSwordService),
            new ExistsCommand(authMeHook));

        // Register the regular listener
        BukkitListener listener = new BukkitListener(this, fireSwordService);
        pluginManager.registerEvents(listener, this);

        // Register AuthMe components if it is available
        if (pluginManager.isPluginEnabled("AuthMe")) {
            registerAuthMeComponents();
        }
    }

    @Override
    public void onDisable() {
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

    /**
     * Activates the AuthMe hook and registers the AuthMe listener if not yet registered.
     * Call this method only when certain that AuthMe is enabled.
     */
    public void registerAuthMeComponents() {
        SampleLogger.info("Hooking into AuthMe");
        authMeHook.initializeAuthMeHook();
        if (authMeListener == null) {
            authMeListener = new AuthMeListener();
            getServer().getPluginManager().registerEvents(authMeListener, this);
        }
    }

    /**
     * Deactivates the AuthMe hook. Call when AuthMe has been disabled.
     */
    public void removeAuthMeHook() {
        SampleLogger.info("Unhooking from AuthMe");
        authMeHook.removeAuthMeHook();
    }

    /**
     * Registers the given commands.
     *
     * @param givenCommands the commands to register
     */
    private void registerCommands(CommandImplementation... givenCommands) {
        commands = new HashMap<>();
        for (CommandImplementation command : givenCommands) {
            commands.put(command.getLabel(), command);
        }
    }
}
