package me.rvt.rclootboxes.keys;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Key {
    Player p;
    Plugin plugin;
    FileConfiguration config;

    public Key(Player p, FileConfiguration config, Plugin plugin) {
        this.p = p;
        this.plugin = plugin;
        this.config = config;
    }

    public void getKey(int amount){
        ItemStack giveItem = config.getItemStack("lootbox.key");
        giveItem.setAmount(amount);
        p.getInventory().addItem(giveItem);
    }
}