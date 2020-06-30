package me.rvt.rclootboxes.sql;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CountBoxes {

    public CountBoxes(Player p){
        Statement stmt = null;
        Connection c = null;

        try {
            c = DriverManager.getConnection("jdbc:sqlite:data/Data.db");
            c.setAutoCommit(false);

            stmt = c.createStatement();

            stmt.executeUpdate(
                    String.format("UPDATE Players SET lootboxes = lootboxes + 1 WHERE username = '%s'", p.getName())
            );

            c.commit();
        } catch (Exception e) {
            System.out.println("[RClootBoxes]" + e.getMessage());
        }
        finally {
            try{
                if(stmt != null && !stmt.isClosed())
                    stmt.close();
                if(c != null && !c.isClosed())
                    c.close();
            }
            catch (Exception ignored) {}
        }
    }
}
