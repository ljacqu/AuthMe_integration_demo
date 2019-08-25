package ch.jalu.authme.integrationdemo.service;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

/**
 * Manages the fire sword feature.
 */
public class FireSwordService {

    /**
     * The item that acts as a fire sword: if held in the hand and the player clicks,
     * it will launch fireballs.
     */
    public static final Material FIRE_SWORD = Material.GOLDEN_SWORD;

    private Set<String> hasFireSword = new HashSet<>();

    /**
     * Checks if the fire sword feature is enabled for the given name (case-sensitive).
     *
     * @param name the name to verify
     * @return true if the name has fire sword activate, false otherwise
     */
    public boolean isEnabledFor(String name) {
        return hasFireSword.contains(name);
    }

    /**
     * Toggles the fire sword feature for the given name (case-sensitive).
     *
     * @param name the name to toggle the feature for
     * @return the fire sword state for the player: true if now enabled, false if now disabled
     */
    public boolean toggleFor(String name) {
        if (isEnabledFor(name)) {
            hasFireSword.remove(name);
            return false;
        }
        hasFireSword.add(name);
        return true;
    }

}
