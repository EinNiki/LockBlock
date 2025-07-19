package de.nick.lockBlock;

import de.nick.lockBlock.command.LockBlockCommand;
import de.nick.lockBlock.listener.PlayerListener;
import de.nick.lockBlock.manager.CacheManager;
import de.nick.lockBlock.manager.FileManager;
import de.nick.lockBlock.utils.RecipeLoader;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class LockBlock extends JavaPlugin {

    private static LockBlock instance;
    private FileManager lockBlocks;
    private CacheManager cacheManager;
    private String prefix = "LockBlock§8:§7";

    @Override
    public void onEnable() {

        instance = this;
        cacheManager = new CacheManager();
        this.lockBlocks = new FileManager("lockBlocks.yml", this.getDataFolder());
        getCommand("lock").setExecutor(new LockBlockCommand());
        cacheManager.loadFromYML();

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        new RecipeLoader().registerRecipes();

    }

    @Override
    public void onDisable() {
        cacheManager.saveCacheToYML();
        instance = null;
    }

    public static LockBlock getInstance() {
        return instance;
    }

    public String getPrefix() {
        return prefix;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public @NotNull FileConfiguration getLockBlocks() {return this.lockBlocks.getFileConfiguration();}

    public void saveDefaultConfigs() {
        this.lockBlocks.save();
    }
}
