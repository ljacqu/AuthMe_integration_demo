package ch.jalu.authme.integrationdemo;

import ch.jalu.authme.integrationdemo.command.CommandHandler;
import ch.jalu.authme.integrationdemo.listener.AuthMeListener;
import ch.jalu.authme.integrationdemo.listener.BukkitListener;
import ch.jalu.authme.integrationdemo.service.AuthMeHook;
import ch.jalu.injector.Injector;
import ch.jalu.injector.InjectorBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Sample plugin.
 */
public class SamplePlugin extends JavaPlugin {

    private CommandHandler commandHandler;
    // AuthMe integration
    private Listener authMeListener;
    private AuthMeHook authMeHook;

    @Override
    public void onEnable() {
        Injector injector = new InjectorBuilder()
                .addDefaultHandlers("ch.jalu.authme.integrationdemo")
                .create();
        injector.register(SamplePlugin.class, this);

        SampleLogger.setLogger(getLogger());
        authMeHook = injector.getSingleton(AuthMeHook.class);
        commandHandler = injector.getSingleton(CommandHandler.class);

        // Register the regular listener
        final PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(injector.getSingleton(BukkitListener.class), this);

        // Register AuthMe components if it is available
        if (pluginManager.isPluginEnabled("AuthMe")) {
            registerAuthMeComponents();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandHandler.onCommand(sender, label, args);
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
}
