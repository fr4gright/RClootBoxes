package me.rvt.rclootboxes.yaml;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigInit {
    private FileConfiguration config;

    public ConfigInit(Plugin plugin) {
        loadConfig(plugin);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void loadConfig(Plugin plugin) {
        File conf = new File(plugin.getDataFolder(), "config.yml");

        config = YamlConfiguration.loadConfiguration(conf);

        if (!config.contains("lootbox")) {

            init();

            try {
                config.save(conf);
            } catch (IOException var3) {
                System.out.println("[RClootBoxes] Unable to save config!");
            }
        }
    }

    private void init() {
        ItemStack lootBox = new ItemStack(Material.LIME_SHULKER_BOX, 1);
        ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        ItemStack enchantedLootBox = new ItemStack(Material.PURPLE_SHULKER_BOX, 1);

        ItemMeta lootBoxMeta = lootBox.getItemMeta();
        ItemMeta keyMeta = key.getItemMeta();
        ItemMeta enchBoxMeta = enchantedLootBox.getItemMeta();

        List < String > boxLore = new ArrayList < > ();
        boxLore.add(ChatColor.AQUA + "Drops completely randomized item stacks.");
        boxLore.add(ChatColor.AQUA + "Place it on the ground and open with a" +
                ChatColor.GOLD + ChatColor.BOLD + " key" + ChatColor.AQUA + ".");
        boxLore.add(" ");
        boxLore.add(ChatColor.AQUA + "Can be received by " + ChatColor.GREEN + ChatColor.BOLD + "voting" + ChatColor.AQUA + ",");
        boxLore.add(ChatColor.AQUA + "buy a " + ChatColor.GOLD + ChatColor.BOLD + "key" + ChatColor.AQUA + " from " +
                ChatColor.GREEN + ChatColor.BOLD + "/voteshop" + ChatColor.AQUA + ".");

        List < String > enchBoxLore = new ArrayList < > ();
        enchBoxLore.add(ChatColor.AQUA + "Drops random enchanted item.");
        enchBoxLore.add(ChatColor.AQUA + "Place it on the ground and open with a" +
                ChatColor.GOLD + ChatColor.BOLD + " key" + ChatColor.AQUA + ".");
        enchBoxLore.add(" ");
        enchBoxLore.add(ChatColor.AQUA + "Can be received by " + ChatColor.GREEN + ChatColor.BOLD + "voting" + ChatColor.AQUA + ",");
        enchBoxLore.add(ChatColor.AQUA + "buy a " + ChatColor.GOLD + ChatColor.BOLD + "key" + ChatColor.AQUA + " from " +
                ChatColor.GREEN + ChatColor.BOLD + "/voteshop" + ChatColor.AQUA + ".");

        List < String > keyLore = new ArrayList < > ();
        keyLore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Right Click" +
                ChatColor.AQUA + " to open a" +
                ChatColor.GREEN + ChatColor.BOLD + " Loot Box" + ChatColor.AQUA+".");

        lootBoxMeta.setLore(boxLore);
        enchBoxMeta.setLore(enchBoxLore);
        keyMeta.setLore(keyLore);
        
        enchBoxMeta.addEnchant(Enchantment.LUCK, 1, true);
        keyMeta.addEnchant(Enchantment.LUCK, 1, true);

        lootBoxMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Loot Box");
        enchBoxMeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Enchanted Loot Box");
        keyMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Key");

        lootBox.setItemMeta(lootBoxMeta);
        enchantedLootBox.setItemMeta(enchBoxMeta);
        key.setItemMeta(keyMeta);

        List < String > blaclisted = new ArrayList < > ();

        blaclisted.add("AIR");
        blaclisted.add("BEDROCK");
        blaclisted.add("BARRIER");
        blaclisted.add("COMMAND_BLOCK");
        blaclisted.add("COMMAND_BLOCK_MINECART");
        blaclisted.add("STRUCTURE_BLOCK");
        blaclisted.add("STRUCTURE_VOID");
        blaclisted.add("CHAIN_COMMAND_BLOCK");
        blaclisted.add("REPEATING_COMMAND_BLOCK");
        blaclisted.add("JIGSAW");

        config.set("lootbox.default", lootBox);
        config.set("lootbox.enchanted", enchantedLootBox);
        config.set("lootbox.key", key);
        config.set("lootbox.exp", 25);
        config.set("lootbox.maxDrop", 7);
        config.set("lootBox.stack.min", 1);
        config.set("lootBox.particlesCount", 100);
        config.set("lootBox.effectsCount", 3);
        config.set("item.blacklist", blaclisted);
        config.set("lootbox.maxEnchantAttempts", 10);
        config.set("lootbox.maxEnchantDrops", 3);

        config.set("message.prefix", ChatColor.WHITE + "" + ChatColor.BOLD + "[" +
                ChatColor.AQUA + ChatColor.BOLD + "RC" + ChatColor.WHITE + ChatColor.BOLD + "][" +
                ChatColor.YELLOW + ChatColor.BOLD + "LootBoxes" + ChatColor.WHITE + ChatColor.BOLD + "]" +
                ChatColor.RESET + " ");
        config.set("message.nokey", ChatColor.RED + "You need a " + ChatColor.GOLD + ChatColor.BOLD + "KEY" +
                ChatColor.RED + " to open this " + ChatColor.GREEN + ChatColor.BOLD + "Loot Box" +
                ChatColor.RED + "!");
        config.set("message.keyplace", ChatColor.GOLD + "" + ChatColor.BOLD + "KEYS" +
                ChatColor.RED + " cannot be placed!");
    }
}