package de.nick.lockBlock.listener;

import de.nick.lockBlock.LockBlock;
import de.nick.lockBlock.manager.CacheManager;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.*;

public class PlayerListener implements Listener {

    // === KONSTANTEN ===
    private static final String META_LOCK_TRUE = "LockBlock.True";
    private static final String NAMESPACE_ITEMFRAME = "itemframe";
    private static final String NAMESPACE_ELYTRA = "elytraLauncher";
    private static final String NAMESPACE_ELEVATOR = "elevator";
    private static final String SPAM_PLAYER = "olepz";
    private static final String SPAM_PLAYER2 = "rechterschuh39";
    private static final int OLE_MIN_SECONDS = 180;
    private static final int OLE_MAX_SECONDS = 600;

    // === KICK COOLDOWNS ===
    private long nextKickTime = 0L;
    private long nextKickTime2 = 0L;
    private final Random random = new Random();

    // === BLOCK-PROTECTION ===
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        CacheManager cm = LockBlock.getInstance().getCacheManager();

        if (player.hasMetadata(META_LOCK_TRUE)) {
            event.setCancelled(true);

            if (cm.containsLocation(loc)) {
                if (cm.removeFromCache(loc)) {
                    send(player, "§aBlock-Schutz entfernt: " + event.getBlock().getBlockData().getMaterial());
                } else {
                    send(player, "§cFehler beim Entfernen.");
                }
            } else {
                if (cm.addToCache(loc)) {
                    send(player, "§aBlock geschützt: " + event.getBlock().getBlockData().getMaterial());
                } else {
                    send(player, "§cFehler beim Hinzufügen.");
                }
            }
            send(player, "Modus deaktiviert.");
            player.removeMetadata(META_LOCK_TRUE, LockBlock.getInstance());
            return;
        }

        if (cm.containsLocation(loc)) {
            event.setCancelled(true);
            send(player, "§cDieser Block ist geschützt!");
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
        if (event.getPlayer().hasMetadata(META_LOCK_TRUE)) return;
        if (event.getClickedBlock() == null) return;

        Location loc = event.getClickedBlock().getLocation();
        if (LockBlock.getInstance().getCacheManager().containsLocation(loc)) {
            event.setCancelled(true);
            send(event.getPlayer(), "§cDieser Block ist geschützt und kann nicht benutzt werden!");
        }
    }

    // === TOTEM-NAMING ===
    @EventHandler
    public void onPlayerPickupItem(PlayerAttemptPickupItemEvent event) {
        setTotemName(event.getItem().getItemStack(), event.getPlayer().getName());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        resetTotemName(event.getItemDrop().getItemStack());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (event.getWhoClicked() instanceof Player player &&
                event.getClickedInventory() != null &&
                event.getCurrentItem().getType() == Material.TOTEM_OF_UNDYING) {
            setTotemName(event.getCurrentItem(), player.getName());
        }
    }

    private void setTotemName(ItemStack item, String playerName) {
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

    // === OLE & SAFARI KICK-FUNKTION ===
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage().toLowerCase();
        Player sender = event.getPlayer();

        // --- OLE ---
        if (message.contains("oleee") && !sender.getName().equalsIgnoreCase(SPAM_PLAYER)) {
            Player ole = Bukkit.getPlayerExact(SPAM_PLAYER);
            if (ole == null || !ole.isOnline()) {
                sender.sendMessage(ChatColor.YELLOW + "olepz ist nicht online!");
                return;
            }
            long now = System.currentTimeMillis();
            if (now < nextKickTime) {
                long time = (nextKickTime - now) / 1000L;
                sender.sendMessage(ChatColor.RED + "Du musst noch " + (time/60) + " Minuten und " + (time%60) + " Sekunden warten, bevor ole wieder gekickt werden kann!");
                return;
            }
            Bukkit.getScheduler().runTask(LockBlock.getInstance(), () -> {
                ole.kickPlayer(ChatColor.RED + "Your dick is too small for the server");
                sender.sendMessage(ChatColor.YELLOW + "olepz wurde gekickt!");
            });
            nextKickTime = now + (random.nextInt(OLE_MAX_SECONDS - OLE_MIN_SECONDS + 1) + OLE_MIN_SECONDS) * 1000L;
        }

        // --- SCHUH (safariii) ---
        if (message.contains("safariii") && !sender.getName().equalsIgnoreCase(SPAM_PLAYER2)) {
            Player schuh = Bukkit.getPlayerExact(SPAM_PLAYER2);
            if (schuh == null || !schuh.isOnline()) {
                sender.sendMessage(ChatColor.YELLOW + "rechterschuh39 ist nicht online!");
                return;
            }
            long now = System.currentTimeMillis();
            if (now < nextKickTime2) {
                long time = (nextKickTime2 - now) / 1000L;
                sender.sendMessage(ChatColor.RED + "Du musst noch " + (time/60) + " Minuten und " + (time%60) + " Sekunden warten, bevor rechterschuh39 wieder gekickt werden kann!");
                return;
            }
            Bukkit.getScheduler().runTask(LockBlock.getInstance(), () -> {
                schuh.kickPlayer(ChatColor.RED + "nerv nicht");
                sender.sendMessage(ChatColor.YELLOW + "rechterschuh39 wurde gekickt!");
            });
            nextKickTime2 = now + (random.nextInt(OLE_MAX_SECONDS - OLE_MIN_SECONDS + 1) + OLE_MIN_SECONDS) * 1000L;
        }
    }

    // === AUFZUG & ELYTRA LAUNCHER ===
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        // Elevator Hoch
        if (loc.getBlock().getType() == Material.DAYLIGHT_DETECTOR && player.getVelocity().getY() > 0) {
            handleElevator(player, loc);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        AsyncScheduler scheduler = Bukkit.getAsyncScheduler();

        // Elytra Launcher
        if (loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.DISPENSER && player.isSneaking()) {
            handleElytraLauncher(player, loc, scheduler);
        }
        // Elevator Runter
        if (loc.getBlock().getType() == Material.DAYLIGHT_DETECTOR && player.isSneaking()) {
            handleElevatorDown(player, loc, scheduler);
        }
    }

    private void handleElytraLauncher(Player player, Location location, AsyncScheduler scheduler) {
        Location dispenserLoc = location.clone().add(BlockFace.DOWN.getDirection());
        Block dispenserBlock = dispenserLoc.getBlock();
        if (!(dispenserBlock.getState() instanceof Dispenser dispenser)) return;

        NamespacedKey elytraKey = new NamespacedKey(LockBlock.getInstance(), NAMESPACE_ELYTRA);
        boolean isLauncher = dispenser.getPersistentDataContainer().getOrDefault(elytraKey, PersistentDataType.BOOLEAN, false);

        if (!isLauncher ||
                !(((Directional) dispenserBlock.getBlockData()).getFacing() == BlockFace.UP)) return;

        Inventory inv = dispenser.getInventory();
        boolean foundGunpowder = false;
        for (ItemStack stack : inv.getContents()) {
            if (stack != null && stack.getType() == Material.GUNPOWDER) {
                stack.subtract(1);
                foundGunpowder = true;
                break;
            }
        }
        if (!foundGunpowder) {
            player.sendActionBar("Denke daran, ohne Treibstoff funktioniert der Elytra Launcher nicht.");
            return;
        }

        // Boost je nach Netherite-Block
        int boost = (dispenserLoc.clone().subtract(0, 2, 0).getBlock().getType() == Material.NETHERITE_BLOCK)
                ? 10
                : (dispenserLoc.clone().subtract(0, 1, 0).getBlock().getType() == Material.NETHERITE_BLOCK)
                ? 4
                : 2;
        triggerElytraBoost(player, scheduler, 10, boost);
    }

    private void triggerElytraBoost(Player player, AsyncScheduler scheduler, double initialYVel, double forwardVel) {
        player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 180, 0.1, 0.1, 0.1, 0.1);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 50, 10);
        player.setVelocity(new Vector(0, initialYVel, 0));
        sendTitleOnElytraBoost(player);

        scheduler.runDelayed(LockBlock.getInstance(), delay -> {
            player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 180, 0.1, 0.1, 0.1, 0.1);
            player.sendTitle("Jetzt!", "");
            player.setVelocity(player.getEyeLocation().getDirection().multiply(forwardVel));
        }, 1800, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    private void sendTitleOnElytraBoost(Player player) {
        AsyncScheduler scheduler = Bukkit.getAsyncScheduler();
        scheduler.runDelayed(LockBlock.getInstance(), d -> player.sendTitle("Boost in", "3"), 500, java.util.concurrent.TimeUnit.MILLISECONDS);
        scheduler.runDelayed(LockBlock.getInstance(), d -> player.sendTitle("Boost in", "2"), 800, java.util.concurrent.TimeUnit.MILLISECONDS);
        scheduler.runDelayed(LockBlock.getInstance(), d -> player.sendTitle("Boost in", "1"), 1400, java.util.concurrent.TimeUnit.MILLISECONDS);
        scheduler.runDelayed(LockBlock.getInstance(), d -> player.sendTitle("", ""), 2000, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    // Aufzug hoch (beim Hochspringen)
    private void handleElevator(Player player, Location loc) {
        NamespacedKey elevatorKey = new NamespacedKey(LockBlock.getInstance(), NAMESPACE_ELEVATOR);
        for (int y = 2; y <= 200; y++) {
            Block block = loc.clone().add(0, y, 0).getBlock();
            if (block.getType() == Material.DAYLIGHT_DETECTOR &&
                    ((DaylightDetector) block.getState()).getPersistentDataContainer().getOrDefault(elevatorKey, PersistentDataType.BOOLEAN, false)) {
                teleportWithEffect(player, block.getLocation(), ChatColor.GREEN + "▲ Up ▲");
                return;
            }
        }
    }

    // Aufzug runter (Sneaken)
    private void handleElevatorDown(Player player, Location loc, AsyncScheduler scheduler) {
        NamespacedKey elevatorKey = new NamespacedKey(LockBlock.getInstance(), NAMESPACE_ELEVATOR);
        for (int y = -2; y >= -200; y--) {
            Block block = loc.clone().add(0, y, 0).getBlock();
            if (block.getType() == Material.DAYLIGHT_DETECTOR &&
                    ((DaylightDetector) block.getState()).getPersistentDataContainer().getOrDefault(elevatorKey, PersistentDataType.BOOLEAN, false)) {
                teleportWithEffect(player, block.getLocation(), ChatColor.RED + "▼ Down ▼");
                return;
            }
        }
    }

    private void teleportWithEffect(Player player, Location blockLoc, String title) {
        Location target = blockLoc.clone().add(0.5, 0.5, 0.5);
        target.setYaw(player.getYaw());
        target.setPitch(player.getPitch());
        player.teleportAsync(target);
        player.sendTitle(title, "");
        player.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 2, 2);
    }

    // === UTILITY ===
    private void send(Player p, String msg) {
        p.sendMessage(LockBlock.getInstance().getPrefix() + " " + msg);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() == Material.DAYLIGHT_DETECTOR) {
            if (block.getLocation().equals(new Location(Bukkit.getWorld("world"), 0, 63, 0)) || block.getLocation().equals(new Location(Bukkit.getWorld("world"), 0, 200, 0))) {
                if (!player.hasPermission("EV1System.ByPass.SpawnProtection")) {
                    event.setCancelled(true);
                    return;
                }
            }
            DaylightDetector daylightDetector = (DaylightDetector) block.getState();
            boolean isElevator = daylightDetector.getPersistentDataContainer().getOrDefault(new NamespacedKey(LockBlock.getInstance(), "elevator"), PersistentDataType.BOOLEAN, false);
            if (!isElevator) {
                return;
            }

            event.setCancelled(true);
            block.setType(Material.AIR);
            ItemStack elevator = new ItemStack(Material.DAYLIGHT_DETECTOR, 1);
            ItemMeta elevatorMeta = elevator.getItemMeta();
            elevatorMeta.setDisplayName("§l§2Elevator");
            elevatorMeta.setLore(Arrays.asList("§l§3Platziere zwei Elevators übereinander."));
            elevator.setItemMeta(elevatorMeta);
            elevator.editMeta(meta -> {
                meta.getPersistentDataContainer().set(new NamespacedKey(LockBlock.getInstance(), "elevator"), PersistentDataType.BOOLEAN, true);
            });
            block.getLocation().getWorld().dropItemNaturally(block.getLocation().toCenterLocation(), elevator);
        }

        if (block.getType() == Material.DISPENSER) {

            Dispenser dispenser = (Dispenser) block.getState();
            boolean isDispenser = dispenser.getPersistentDataContainer().getOrDefault(new NamespacedKey(LockBlock.getInstance(), "elytraLauncher"), PersistentDataType.BOOLEAN, false);
            if (!isDispenser) {
                return;
            }
            event.setCancelled(true);
            block.setType(Material.AIR);

            ItemStack elytraLauncherStack = new ItemStack(Material.DISPENSER, 1);
            ItemMeta elytraLauncherMeta = elytraLauncherStack.getItemMeta();

            elytraLauncherMeta.setDisplayName("§l§2Elytra Launcher");
            elytraLauncherMeta.setLore(Arrays.asList("§3Platziere den Elytra Launcher mit dem Loch nach oben und befülle ihn mit Treibstoff", "§3Anschließend kannst du dich boosten lassen, indem du sneakst."));

            elytraLauncherStack.setItemMeta(elytraLauncherMeta);

            elytraLauncherStack.editMeta(meta -> meta.getPersistentDataContainer().set(new NamespacedKey(LockBlock.getInstance(), "elytraLauncher"), PersistentDataType.BOOLEAN, true));

            block.getLocation().getWorld().dropItemNaturally(block.getLocation().toCenterLocation(), elytraLauncherStack);

            for (int i = 0; i < dispenser.getInventory().getSize(); i++) {
                if (dispenser.getInventory().getItem(i) != null) {
                    block.getLocation().getWorld().dropItemNaturally(block.getLocation().toCenterLocation(), Objects.requireNonNull(dispenser.getInventory().getItem(i)));
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = event.getItemInHand();

        // Prüfen, ob der Artikel ein Elevator ist
        if (isElevator(itemInHand)) {
            handleElevatorPlacement(event);
        }

        // Prüfen, ob der Artikel ein Elytra Launcher ist
        if (isElytraLauncher(itemInHand)) {
            handleElytraLauncherPlacement(event);
        }
    }

    // Prüft, ob das Item ein Elevator ist
    private boolean isElevator(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(
                new NamespacedKey(LockBlock.getInstance(), "elevator"), PersistentDataType.BOOLEAN, false
        );
    }

    private void handleElevatorPlacement(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.DAYLIGHT_DETECTOR) {
            DaylightDetector daylightDetector = (DaylightDetector) event.getBlockPlaced().getState();
            daylightDetector.getPersistentDataContainer().set(new NamespacedKey(LockBlock.getInstance(), "elevator"), PersistentDataType.BOOLEAN, true);
            daylightDetector.update();
        }
    }

    // Prüft, ob das Item ein Elytra Launcher ist
    private boolean isElytraLauncher(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(
                new NamespacedKey(LockBlock.getInstance(), "elytraLauncher"), PersistentDataType.BOOLEAN, false
        );
    }

    private void handleElytraLauncherPlacement(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.DISPENSER) {
            Dispenser dispenser = (Dispenser) event.getBlockPlaced().getState();
            dispenser.getPersistentDataContainer().set(new NamespacedKey(LockBlock.getInstance(), "elytraLauncher"), PersistentDataType.BOOLEAN, true);
            dispenser.update();
        }
    }
}
