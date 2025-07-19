package de.nick.lockBlock.utils;

import de.nick.lockBlock.LockBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class RecipeLoader {

    public void registerRecipes() {
        // Elevator
        ItemStack elevator = new ItemStack(Material.DAYLIGHT_DETECTOR, 1);
        ItemMeta elevatorMeta = elevator.getItemMeta();
        elevatorMeta.setDisplayName("§2Elevator");
        elevatorMeta.setLore(Arrays.asList("§l§3Platziere zwei Elevators übereinander."));
        elevator.setItemMeta(elevatorMeta);
        elevator.editMeta(meta -> {
            meta.getPersistentDataContainer().set(new NamespacedKey(LockBlock.getInstance(), "elevator"), PersistentDataType.BOOLEAN, true);
        });

        ShapedRecipe elevatorRecipe = new ShapedRecipe(new NamespacedKey(LockBlock.getInstance(), "elevator_recipe"), elevator);
        elevatorRecipe.shape("*D*", "*E*", "*N*");
        elevatorRecipe.setIngredient('D', Material.DIAMOND);
        elevatorRecipe.setIngredient('E', Material.DAYLIGHT_DETECTOR);
        elevatorRecipe.setIngredient('N', Material.ENDER_PEARL);
        Bukkit.getServer().addRecipe(elevatorRecipe);


        // Elytra Launcher
        ItemStack elytraLauncher = new ItemStack(Material.DISPENSER, 1);
        ItemMeta elytraLauncherMeta = elytraLauncher.getItemMeta();
        elytraLauncherMeta.setDisplayName("§l§2Elytra Launcher");
        elytraLauncherMeta.setLore(Arrays.asList("§3Platziere den Elytra Launcher mit dem Loch nach oben und befülle ihn mit Treibstoff", "§3Anschließend kannst du dich boosten lassen, indem du sneakst."));
        elytraLauncher.setItemMeta(elytraLauncherMeta);
        elytraLauncher.editMeta(meta -> {
            meta.getPersistentDataContainer().set(new NamespacedKey(LockBlock.getInstance(), "elytraLauncher"), PersistentDataType.BOOLEAN, true);
        });

        ShapedRecipe elytraLauncherRecipe = new ShapedRecipe(new NamespacedKey(LockBlock.getInstance(), "elytraLauncher_recipe"), elytraLauncher);
        elytraLauncherRecipe.shape("***", "*A*", "*B*");
        elytraLauncherRecipe.setIngredient('*', Material.FIREWORK_ROCKET);
        elytraLauncherRecipe.setIngredient('A', Material.DISPENSER);
        elytraLauncherRecipe.setIngredient('B', Material.FIRE_CHARGE);
        Bukkit.getServer().addRecipe(elytraLauncherRecipe);


        // LightBlock
        ItemStack lightBlock = new ItemStack(Material.LIGHT, 1);
        ItemMeta lightBlockMeta = lightBlock.getItemMeta();
        lightBlockMeta.setDisplayName("§5Light");
        lightBlockMeta.setLore(Arrays.asList(""));
        lightBlock.setItemMeta(lightBlockMeta);

        ShapedRecipe lightBlockRecipe = new ShapedRecipe(new NamespacedKey(LockBlock.getInstance(), "lightBlock_recipe"), lightBlock);
        lightBlockRecipe.shape("*A*", "ABA", "*A*");
        lightBlockRecipe.setIngredient('A', Material.GLOWSTONE_DUST);
        lightBlockRecipe.setIngredient('B', Material.COAL);
        Bukkit.getServer().addRecipe(lightBlockRecipe);
    }

}
