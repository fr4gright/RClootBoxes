package me.rvt.rclootboxes.sql;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Unopened {
    Plugin plugin;

    ResultSet rSet;
    Statement stmt;
    Connection c;

    public Unopened() {
        this.plugin = Bukkit.getPluginManager().getPlugin("RClootBoxes");

        rSet = null;
        stmt = null;
        c = null;
    }

    public List < Block > getUnopened() {
        List<Block> loaded = new ArrayList<>();

        try {
            sqlConnect();

            rSet = stmt.executeQuery("SELECT x, y, z, world FROM Unopened");

            while (rSet.next()){
                loaded.add(new Location(
                        Bukkit.getWorld(rSet.getString("world")),
                        rSet.getInt("x"),
                        rSet.getInt("y"),
                        rSet.getInt("z")
                ).getBlock());
            }

            stmt.executeUpdate("DELETE FROM Unopened");
            c.commit();

        } catch (Exception e) {
            System.out.println("[RClootBoxes]" + e.getMessage());
        }
        finally {
            sqlClose();
        }

        return loaded;
    }

    public void saveUnopened(List < Block > placedBlock) {
        try {
            sqlConnect();

            for(Block b: placedBlock) {
                stmt.executeUpdate(
                        String.format("INSERT INTO Unopened VALUES (%d, %d, %d, '%s')",
                            b.getX(), b.getY(), b.getZ(), b.getWorld().getName())
                );
            }
            c.commit();

        } catch (Exception e) {
            System.out.println("[RClootBoxes]" + e.getMessage());
        }
        finally {
            sqlClose();
        }
    }

    private void sqlConnect(){
        try{
        c = DriverManager.getConnection("jdbc:sqlite:data/Data.db");
        c.setAutoCommit(false);

        stmt = c.createStatement();
        }
        catch (Exception ignored) {}
    }

    private void sqlClose(){
        try{
            if(rSet != null && !rSet.isClosed())
                rSet.close();
            if(stmt != null && !stmt.isClosed())
                stmt.close();
            if(c != null && !c.isClosed())
                c.close();
        }
        catch (Exception ignored) {}
    }
}