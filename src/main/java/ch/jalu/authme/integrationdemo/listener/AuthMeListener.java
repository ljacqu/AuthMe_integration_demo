package ch.jalu.authme.integrationdemo.listener;

import fr.xephi.authme.events.AuthMeAsyncPreLoginEvent;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Random;

/**
 * Listener for AuthMe events.
 */
public class AuthMeListener implements Listener {

    private static final String[] JOIN_MESSAGES = new String[]{
        "Hi everyone!",
        "Hey guys",
        "Hi, how is everyone? :)",
        "/me Hello all"
    };
    private Random random = new Random();

    /**
     * Catches AuthMe's {@link AuthMeAsyncPreLoginEvent} and refuses to log in the player
     * with a 10% chance.
     *
     * @param event the event to process
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPrelogin(AuthMeAsyncPreLoginEvent event) {
        // Randomly decide that a player may not log in
        if (random.nextInt(10) == 0) {
            event.setCanLogin(false);
            event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "[Server] I don't like you! No log in for you.");
        }
    }

    /**
     * Catches AuthMe's {@link LoginEvent} (thrown after player successfully logged in)
     * and makes the player write some greeting to chat.
     *
     * @param event the event to process
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onLogin(LoginEvent event) {
        event.getPlayer().chat(JOIN_MESSAGES[random.nextInt(JOIN_MESSAGES.length)]);
    }
}
