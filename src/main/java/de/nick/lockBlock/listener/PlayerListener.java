package de.nick.lockBlock.listener;

import de.nick.lockBlock.LockBlock;
import de.nick.lockBlock.manager.CacheManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;


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

    /*

        Totem OF Undying

     */

    @EventHandler
    public void onPlayerPickupItem(PlayerAttemptPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        setTotemName(item, event.getPlayer().getName());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        resetTotemName(item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (event.getWhoClicked() instanceof Player player) {
            if (event.getClickedInventory() != null && event.getCurrentItem().getType() == Material.TOTEM_OF_UNDYING) {
                setTotemName(event.getCurrentItem(), player.getName());
            }
        }
    }

    public void setTotemName(ItemStack item, String playerName) {
        if (item.getType() == Material.TOTEM_OF_UNDYING) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text("Totem von " + playerName));
                item.setItemMeta(meta);
            }
        }
    }

    private void resetTotemName(ItemStack item) {
        if (item.getType() == Material.TOTEM_OF_UNDYING) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(null);
                item.setItemMeta(meta);
            }
        }
    }


    /*

    Ole Kick

     */

    private long nextKickTime = 0L;
    private final Random random = new Random();

    // Zufällige Zeit in Millisekunden (3 bis 10 Minuten)
    private long getRandomDelayMillis() {
        int min = 3 * 60; // 3 Minuten in Sekunden
        int max = 10 * 60; // 10 Minuten in Sekunden
        int randomSeconds = random.nextInt(max - min + 1) + min;
        return randomSeconds * 1000L;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage().toLowerCase();
        Player sender = event.getPlayer();

        if (message.contains("oleee")) {

            if (sender.getName().equalsIgnoreCase("olepz")) {
                return;
            }

            Player ole = Bukkit.getPlayerExact("olepz");
            if (ole == null || !ole.isOnline()) {
                sender.sendMessage(ChatColor.YELLOW + "olepz ist nicht online!");
                return;
            }


            long now = System.currentTimeMillis();

            if (now < nextKickTime) {
                long remaining = (nextKickTime - now) / 1000L;
                long min = remaining / 60;
                long sec = remaining % 60;
                sender.sendMessage(ChatColor.RED + "Du musst noch " + min + " Minuten und " + sec + " Sekunden warten, bevor ole wieder gekickt werden kann!");
                return;
            }


            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("LockBlock"), () -> {
                ole.kickPlayer(ChatColor.RED + "Your dick is too small for the server");
                sender.sendMessage(ChatColor.YELLOW + "olepz wurde gekickt!");
            });

            nextKickTime = now + getRandomDelayMillis();
        }
    }

}
