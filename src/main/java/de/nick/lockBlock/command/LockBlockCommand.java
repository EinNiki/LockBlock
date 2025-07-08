package de.nick.lockBlock.command;

import de.nick.lockBlock.LockBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

public class LockBlockCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }
        Player player = (Player) sender;


        if (player.hasMetadata("LockBlock.True")) {
            player.sendMessage(LockBlock.getInstance().getPrefix() + " deaktiviert.");
            player.removeMetadata("LockBlock.True", LockBlock.getInstance());
        } else {
            player.sendMessage(LockBlock.getInstance().getPrefix() + " Klicke ein Block ein um den Status zu ändern.");
            player.setMetadata("LockBlock.True", new FixedMetadataValue(LockBlock.getInstance(), true));
        }

        return false;
    }

}
