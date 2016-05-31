package ch.jalu.authme.integrationdemo.listener;

import ch.jalu.authme.integrationdemo.service.AuthMeHook;
import ch.jalu.authme.integrationdemo.service.FireSwordService;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import static org.bukkit.inventory.EquipmentSlot.HAND;

/**
 * Listener for standard events fired by Bukkit.
 * <p>
 * This class detects when AuthMe is enabled or disabled and initializes or removes
 * {@link AuthMeHook the hook} correspondingly.
 */
public class SampleListener implements Listener {

    private final FireSwordService fireSwordService;
    private final AuthMeHook authMeHook;

    public SampleListener(FireSwordService fireSwordService, AuthMeHook authMeHook) {
        this.fireSwordService = fireSwordService;
        this.authMeHook = authMeHook;
    }

    /**
     * Checks if AuthMe has been enabled and hooks into it in such a case.
     *
     * @param event the plugin enable event to process
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPluginEnable(PluginEnableEvent event) {
        if ("AuthMe".equals(event.getPlugin().getName())) {
            authMeHook.initializeAuthMeHook();
        }
    }

    /**
     * Checks if AuthMe has been disabled and unhooks from it in such a case.
     *
     * @param event the plugin disable event to process
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPluginDisable(PluginDisableEvent event) {
        if ("AuthMe".equals(event.getPlugin().getName())) {
            authMeHook.removeAuthMeHook();
        }
    }

    /**
     * Listens for "sword hits" (left-click action with fire sword in hand) and launches a fireball
     * if the fire sword event is enabled for the player.
     *
     * @param event the interact event to process
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void onSwordHit(PlayerInteractEvent event) {
        if (isSwordHitEvent(event) && fireSwordService.isEnabledFor(event.getPlayer().getName())) {
            final Player player = event.getPlayer();
            player.launchProjectile(Fireball.class, player.getLocation().getDirection());
            event.setCancelled(true);
        }
    }

    /**
     * Returns whether the given event is a "sword hit" event (left-click action with fire sword in hand).
     *
     * @param event the event to verify
     * @return true if the event is a "sword hit" event, false otherwise
     */
    private static boolean isSwordHitEvent(PlayerInteractEvent event) {
        return FireSwordService.FIRE_SWORD.equals(event.getMaterial()) && HAND.equals(event.getHand())
            && (Action.LEFT_CLICK_AIR.equals(event.getAction()) || Action.LEFT_CLICK_BLOCK.equals(event.getAction()));
    }

}
