package me.rvt.rclootboxes.boxes;

import me.rvt.rclootboxes.sql.CountBoxes;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Default {
    Plugin plugin;
    FileConfiguration config;
    Block storedBlock;

    public Default(Block storedBlock, FileConfiguration config, Plugin plugin) {
        this.plugin = plugin;
        this.config = config;
        this.storedBlock = storedBlock;

        blockPlaceEffect();
    }

    public Block getBlock() {
        return storedBlock;
    }

    public void openBox(Player p, Block clicked, ItemStack keyInHand) {
        Random rand = new Random();
        Color fireWorkColors = getRandomColor(rand);

        clicked.setType(Material.AIR);
        keyInHand.setAmount(keyInHand.getAmount() - 1);

        InstantFirework(FireworkEffect.builder().withColor(
                fireWorkColors).flicker(true).build(), clicked.getLocation());

        (clicked.getWorld().spawn(clicked.getLocation(),
                ExperienceOrb.class)).setExperience(config.getInt("lootbox.exp"));

        for (int i = 0,
             max = Math.max(rand.nextInt(config.getInt("lootbox.maxDrop")), 1); i < max; i++) {

            int material = rand.nextInt(Material.values().length);
            ItemStack drop = new ItemStack(Material.values()[material],
                    Math.min(Material.values()[material].getMaxStackSize(), 1));

            if (config.getStringList("item.blacklist").contains(drop.getType().toString())) {
                max++;
                continue;
            }

            drop.setAmount(Math.max(rand.nextInt(Math.max(drop.getMaxStackSize(), 1)),
                    config.getInt("lootBox.stack.min")));

            if (drop.getType().isItem())
                clicked.getWorld().dropItemNaturally(clicked.getLocation(), drop);
            else
                max++;
        }

        new CountBoxes(p);
    }

    protected void InstantFirework(FireworkEffect fe, Location loc) {
        Firework f = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(fe);
        fm.setPower(1);
        f.setFireworkMeta(fm);
        f.setMetadata("lootboxeffect", new FixedMetadataValue(plugin, true));
        try {
            Class < ? > entityFireworkClass = getClass("net.minecraft.server.", "EntityFireworks");
            Class < ? > craftFireworkClass = getClass("org.bukkit.craftbukkit.", "entity.CraftFirework");
            Object firework = craftFireworkClass.cast(f);
            Method handle = firework.getClass().getMethod("getHandle");
            Object entityFirework = handle.invoke(firework);
            Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
            Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
            ticksFlown.setAccessible(true);
            ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
            ticksFlown.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        loc.getWorld().playEffect(loc, Effect.SMOKE, 4);
    }

    private Class < ? > getClass(String prefix, String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(
                ".", ",").split(",")[3] + ".";
        String name = prefix + version + nmsClassString;
        Class < ? > nmsClass = Class.forName(name);
        return nmsClass;
    }

    private void blockPlaceEffect() {
        Location loc = storedBlock.getLocation();
        World world = loc.getWorld();

        for (int i = 0; i < config.getInt("lootBox.effectsCount"); i++) {
            world.playEffect(loc, Effect.ENDER_SIGNAL, 0);
        }

        world.spawnParticle(Particle.PORTAL,
                loc.toCenterLocation(),
                config.getInt("lootBox.particlesCount")
        );
    }

    public void dropBox() {
        storedBlock.setType(Material.AIR);

        ItemStack drop = new ItemStack(config.getItemStack("lootbox.default"));
        drop.setAmount(1);

        storedBlock.getWorld().dropItemNaturally(storedBlock.getLocation(), drop);
    }

    protected Color getRandomColor(Random rand) {
        int colorMax = 255;

        return Color.fromBGR(
                rand.nextInt(colorMax),
                rand.nextInt(colorMax),
                rand.nextInt(colorMax)
        );
    }
}