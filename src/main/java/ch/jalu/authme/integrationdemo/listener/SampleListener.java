package ch.jalu.authme.integrationdemo.listener;

import ch.jalu.authme.integrationdemo.service.FireSwordService;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static org.bukkit.inventory.EquipmentSlot.HAND;

/**
 * Event listener.
 */
public class SampleListener implements Listener {

    private final FireSwordService fireSwordService;

    public SampleListener(FireSwordService fireSwordService) {
        this.fireSwordService = fireSwordService;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false)
    public void onSwordHit(PlayerInteractEvent event) {
        if (isRelevantEvent(event) && fireSwordService.isEnabledFor(event.getPlayer().getName())) {
            final Player player = event.getPlayer();
            player.launchProjectile(Fireball.class, player.getLocation().getDirection());
            event.setCancelled(true);
        }
    }

    private static boolean isRelevantEvent(PlayerInteractEvent event) {
        return FireSwordService.FIRE_SWORD.equals(event.getMaterial()) && HAND.equals(event.getHand())
            && (Action.LEFT_CLICK_AIR.equals(event.getAction()) || Action.LEFT_CLICK_BLOCK.equals(event.getAction()));
    }

}
