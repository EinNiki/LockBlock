package de.nick.lockBlock.manager;

import de.nick.lockBlock.LockBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CacheManager {

    private final ArrayList<Location> lockBlocks = new ArrayList<>();

    public void loadFromYML() {
        lockBlocks.clear();
        FileConfiguration config = LockBlock.getInstance().getLockBlocks();
        List<String> list = config.getStringList("lockBlocks");
        for (String locString : list) {
            Location loc = deserializeLocation(locString);
            if (loc != null) {
                lockBlocks.add(loc);
            }
        }
    }

    public boolean addToCache(Location loc) {
        if (!containsLocation(loc)) {
            lockBlocks.add(loc);
            return true;
        }
        return false;
    }

    public boolean removeFromCache(Location loc) {
        for (Location l : lockBlocks) {
            if (l.getWorld().getName().equals(loc.getWorld().getName()) &&
                    l.getBlockX() == loc.getBlockX() &&
                    l.getBlockY() == loc.getBlockY() &&
                    l.getBlockZ() == loc.getBlockZ()) {
                lockBlocks.remove(l);
                return true;
            }
        }
        return false;
    }


    public void saveCacheToYML() {
        FileConfiguration config = LockBlock.getInstance().getLockBlocks();
        List<String> serialized = new ArrayList<>();
        for (Location loc : lockBlocks) {
            serialized.add(serializeLocation(loc));
        }
        config.set("lockBlocks", serialized);
        LockBlock.getInstance().saveDefaultConfigs();
    }

    private String serializeLocation(Location loc) {
        return loc.getWorld().getName() + ";" +
                loc.getBlockX() + ";" +
                loc.getBlockY() + ";" +
                loc.getBlockZ();
    }

    private Location deserializeLocation(String s) {
        try {
            String[] parts = s.split(";");
            if (parts.length != 4) return null;
            World world = Bukkit.getWorld(parts[0]);
            if (world == null) return null;
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);
            return new Location(world, x, y, z);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean containsLocation(Location loc) {
        for (Location l : lockBlocks) {
            if (l.getWorld().getName().equals(loc.getWorld().getName()) &&
                    l.getBlockX() == loc.getBlockX() &&
                    l.getBlockY() == loc.getBlockY() &&
                    l.getBlockZ() == loc.getBlockZ()) {
                return true;
            }
        }
        return false;
    }
}
