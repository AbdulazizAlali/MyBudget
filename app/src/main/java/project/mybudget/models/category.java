package project.mybudget.models;

import java.sql.*;

public class category {

    private int catid;
    private String name;
    private int color;
    private int mtid;
    private int pid;

    public category(int catid, String name, int color, int mtid, int pid) {
        this.catid = catid;
        this.name = name;
        this.color = color;
        this.mtid = mtid;
        this.pid = pid;
    }

    public static void removeFromDatabase(Statement stmt, String id) throws SQLException {

        String query = " UPDATE receipt SET catid = 0 WHERE catid = " + id;
        stmt.executeUpdate(query);

        query = ("DELETE FROM category WHERE catid = "+id);
        stmt.executeUpdate(query);
    }

    public category addToDatabase(Statement stmt) throws SQLException {

        stmt.executeUpdate("INSERT INTO category (name, color, mtid, pid) VALUES ('" + name + "', '" + color + "', '" + mtid + "', '" + pid + "');");
        ResultSet rs = stmt.executeQuery("SELECT catid FROM category WHERE name = '" + name + "' AND color = '" + color + "' AND mtid = '" + mtid + "' AND pid = '" + pid + "';");
        while (rs.next()) {
            catid = rs.getInt(1);
        }
        return this;
    }


    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getMtid() {
        return mtid;
    }

    public void setMtid(int mtid) {
        this.mtid = mtid;
    }

    public int getCatID() {
        return catid;
    }
}