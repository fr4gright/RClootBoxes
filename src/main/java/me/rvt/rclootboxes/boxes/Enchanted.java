package me.rvt.rclootboxes.boxes;

import me.rvt.rclootboxes.sql.CountBoxes;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enchanted extends Default {

    public Enchanted(Block storedBlock, FileConfiguration config, Plugin plugin) {
        super(storedBlock, config, plugin);
    }

    @Override
    public void openBox(Player p, Block clicked, ItemStack keyInHand) {
        int dropped = 0;
        Random rand = new Random();
        Color fireWorkColors = getRandomColor(rand);

        clicked.setType(Material.AIR);
        keyInHand.setAmount(keyInHand.getAmount() - 1);

        InstantFirework(FireworkEffect.builder().withColor(
                fireWorkColors).flicker(true).build(), clicked.getLocation());

        (clicked.getWorld().spawn(clicked.getLocation(),
                ExperienceOrb.class)).setExperience(config.getInt("lootbox.exp"));

        while (dropped < 1) {
            List < Enchantment > enchantments = generateEnchant();
            List < ItemStack > items = generateItems(rand);

            for (ItemStack i: items) {
                for (Enchantment e: enchantments) {
                    if (e.canEnchantItem(i)) {
                        i.addEnchantment(e, Math.min(Math.max(rand.nextInt(6), 1), e.getMaxLevel()));
                    }
                }
                if (i.getEnchantments().size() > 0) {
                    storedBlock.getWorld().dropItemNaturally(storedBlock.getLocation(), i);
                    dropped++;
                }
            }
        }

        new CountBoxes(p);
    }

    private List < Enchantment > generateEnchant() {
        List < Enchantment > temp = new ArrayList < > ();

        for (int i = 0; i < config.getInt("lootbox.maxEnchantAttempts"); i++)
            temp.add(Enchantment.values()[(int)(Math.random() * Enchantment.values().length)]);

        return temp;
    }

    private List < ItemStack > generateItems(Random rand) {
        List < ItemStack > temp = new ArrayList < > ();

        for (int i = 0, max = config.getInt("lootbox.maxEnchantDrops"); i < max; i++) {
            int material = rand.nextInt(Material.values().length);
            ItemStack drop = new ItemStack(Material.values()[material],
                    Math.min(Material.values()[material].getMaxStackSize(), 1));

            if (drop.getType().isItem())
                temp.add(drop);
            else
                max++;
        }
        return temp;
    }

    @Override
    public void dropBox() {
        storedBlock.setType(Material.AIR);

        ItemStack drop = new ItemStack(config.getItemStack("lootbox.enchanted"));
        drop.setAmount(1);

        storedBlock.getWorld().dropItemNaturally(storedBlock.getLocation(), drop);
    }
}