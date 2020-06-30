package me.rvt.rclootboxes;

import me.rvt.rclootboxes.boxes.Default;
import me.rvt.rclootboxes.boxes.Enchanted;
import me.rvt.rclootboxes.yaml.ConfigInit;
import me.rvt.rclootboxes.sql.Unopened;
import me.rvt.rclootboxes.keys.Key;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class RClootBoxes extends JavaPlugin implements Listener, CommandExecutor {
    FileConfiguration config;
    List < Object > placedBox = new ArrayList < > ();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("lootbox").setExecutor(this);
        this.getCommand("key").setExecutor(this);

        config = new ConfigInit(this).getConfig();
        Unopened u = new Unopened();
        List < Block > loaded = u.getUnopened();

        for (Block b: loaded) {
            if (b.getType() == config.getItemStack("lootbox.default").getType())
                placedBox.add(new Default(b, config, this));
            else
                placedBox.add(new Enchanted(b, config, this));
        }
    }

    @Override
    public void onDisable() {
        List < Block > toSave = new ArrayList < > ();

        for (Object o: placedBox) {
            toSave.add(((Default) o).getBlock());
        }

        new Unopened().saveUnopened(toSave);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 1 && (!(sender instanceof Player) || sender.isOp())) {
            Player p = Bukkit.getPlayer(args[0]);
            if (p != null) {
                switch (cmd.getName()) {
                    case "lootbox":
                        if (args.length > 2 && args[2].toLowerCase().equals("-e")) {
                            ItemStack giveItem = config.getItemStack("lootbox.enchanted");
                            giveItem.setAmount(Integer.parseInt(args[1]));
                            p.getInventory().addItem(giveItem);
                        } else {
                            ItemStack giveItem = config.getItemStack("lootbox.default");
                            giveItem.setAmount(Integer.parseInt(args[1]));
                            p.getInventory().addItem(giveItem);
                        }
                        break;
                    case "key":
                        Key k = new Key(p, config, this);
                        k.getKey(Integer.parseInt(args[1]));
                        break;
                }
            }
        }
        return true;
    }

    @EventHandler
    private void onChestOpen(InventoryOpenEvent e) {
        if (e.getInventory().getType() == InventoryType.SHULKER_BOX) {
            Player p = (Player) e.getPlayer();
            Block clicked = e.getInventory().getLocation().getBlock();
            ItemStack keyInHand = p.getInventory().getItemInMainHand();

            for (Object o: placedBox) {
                if (o instanceof Default && ((Default) o).getBlock().equals(clicked)) {
                    if (keyInHand.getItemMeta() != null && keyInHand.getItemMeta().equals(
                            config.getItemStack("lootbox.key").getItemMeta())) {
                        ((Default) o).openBox(p, clicked, keyInHand);
                        placedBox.remove(o);
                        e.setCancelled(true);
                    } else {
                        e.setCancelled(true);
                        p.sendMessage(config.getString("message.prefix") +
                                config.getString("message.nokey"));
                    }
                    return;
                }

                if (o instanceof Enchanted && ((Enchanted) o).getBlock().equals(clicked)) {
                    if (keyInHand.getItemMeta() != null && keyInHand.getItemMeta().equals(
                            config.getItemStack("lootbox.key").getItemMeta())) {
                        ((Enchanted) o).openBox(p, clicked, keyInHand);
                        placedBox.remove(o);
                        e.setCancelled(true);
                    } else {
                        e.setCancelled(true);
                        p.sendMessage(config.getString("message.prefix") +
                                config.getString("message.nokey"));
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent e) {
        if (e.getBlock().getType().toString().contains("SHULKER_BOX")) {
            if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null) {
                if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().equals(
                        config.getItemStack("lootbox.default").getItemMeta())) {
                    placedBox.add(new Default(e.getBlockPlaced(), config, this));
                    return;
                }

                if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().equals(
                        config.getItemStack("lootbox.enchanted").getItemMeta())) {
                    placedBox.add(new Enchanted(e.getBlockPlaced(), config, this));
                    return;
                }
            }
        }

        if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null &&
                e.getPlayer().getInventory().getItemInMainHand().getItemMeta().equals(
                        config.getItemStack("lootbox.key").getItemMeta())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(config.getString("message.prefix") +
                    config.getString("message.keyplace"));
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType().toString().contains("SHULKER_BOX")) {
            for (Object o: placedBox) {
                if (o instanceof Default && ((Default) o).getBlock().equals(e.getBlock())) {
                    e.setCancelled(true);
                    ((Default) o).dropBox();
                    placedBox.remove(o);
                    return;
                }
            }
            for (Object o: placedBox) {
                if (o instanceof Enchanted && ((Enchanted) o).getBlock().equals(e.getBlock())) {
                    e.setCancelled(true);
                    ((Enchanted) o).dropBox();
                    placedBox.remove(o);
                    return;
                }
            }
        }
    }

    @EventHandler
    private void fireWorkDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Firework && e.getDamager().hasMetadata("lootboxeffect")) {
            e.setCancelled(true);
        }
    }
}