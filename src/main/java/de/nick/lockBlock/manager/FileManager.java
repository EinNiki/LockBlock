package de.nick.lockBlock.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FileManager {

    private FileConfiguration fileConfiguration;
    private File file;

    public FileManager(String name, File path) {
        this.file = new File(path, name);
        if (!this.file.exists()) {
            path.mkdirs();

            try {
                this.file.createNewFile();
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

        this.fileConfiguration = new YamlConfiguration();

        try {
            this.fileConfiguration.load(this.file);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        if (this.fileConfiguration.getKeys(true).isEmpty() && name.equals("lockBlock.yml")) {
            this.save();
        }

    }

    public File getFile() {
        return this.file;
    }

    public FileConfiguration getFileConfiguration() {
        return this.fileConfiguration;
    }

    public void reload() {
        try {
            this.fileConfiguration.load(this.file);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void save() {
        try {
            this.fileConfiguration.save(this.file);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }
}

