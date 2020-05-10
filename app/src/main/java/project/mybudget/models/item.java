package project.mybudget.models;

import java.sql.*;

public class item {
    private int itemid;
    private String name;
    private int catid;

    public item(int itemid, String name, int catid) {
        this.itemid = itemid;
        this.name = name;
        this.catid = catid;
    }

    public int getCatid() {
        return catid;
    }

    public void addToDatabase(Statement stmt) throws SQLException {
        stmt.executeUpdate("INSERT INTO item (name, catid) VALUES ('" + name + "', '" + catid + "');");
        ResultSet rs = stmt.executeQuery("SELECT itemid FROM item WHERE name = '" + name + "' AND catid = '" + catid + "';");
        while (rs.next()) {
            itemid = rs.getInt(1);
        }
    }

    public int getItemid() {
        return itemid;
    }

    public String getName() {
        return name;
    }


}
