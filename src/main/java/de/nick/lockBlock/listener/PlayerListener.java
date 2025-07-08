package de.nick.lockBlock.listener;

import de.nick.lockBlock.LockBlock;
import de.nick.lockBlock.manager.CacheManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public class PlayerListener implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        CacheManager cacheManager = LockBlock.getInstance().getCacheManager();

        if (player.hasMetadata("LockBlock.True")) {
            event.setCancelled(true);

            if (cacheManager.containsLocation(loc)) {
                if (cacheManager.removeFromCache(loc)) {
                    player.sendMessage(LockBlock.getInstance().getPrefix() + " §aBlock-Schutz entfernt: " + event.getBlock().getBlockData().getMaterial());
                } else {
                    player.sendMessage(LockBlock.getInstance().getPrefix() + " §cFehler beim Entfernen.");
                }
            } else {
                if (cacheManager.addToCache(loc)) {
                    player.sendMessage(LockBlock.getInstance().getPrefix() + " §aBlock geschützt: " + event.getBlock().getBlockData().getMaterial());
                } else {
                    player.sendMessage(LockBlock.getInstance().getPrefix() + " §cFehler beim Hinzufügen.");
                }
            }

            player.sendMessage(LockBlock.getInstance().getPrefix() + " Modus deaktiviert.");
            player.removeMetadata("LockBlock.True", LockBlock.getInstance());
            return;
        }

        if (cacheManager.containsLocation(loc)) {
            event.setCancelled(true);
            player.sendMessage(LockBlock.getInstance().getPrefix() + " §cDieser Block ist geschützt!");
        }
    }


    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Location loc = event.getBlock().getLocation();
        if (LockBlock.getInstance().getCacheManager().containsLocation(loc)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Location loc = event.getBlock().getLocation();
        if (LockBlock.getInstance().getCacheManager().containsLocation(loc)) {
            event.setNewCurrent(event.getOldCurrent());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (event.getPlayer().hasMetadata("LockBlock.True")) {
            return;
        }

        if (event.getClickedBlock() == null) return;

        Location loc = event.getClickedBlock().getLocation();
        CacheManager cacheManager = LockBlock.getInstance().getCacheManager();

        if (cacheManager.containsLocation(loc)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(LockBlock.getInstance().getPrefix() + " §cDieser Block ist geschützt und kann nicht benutzt werden!");
        }
    }


}
