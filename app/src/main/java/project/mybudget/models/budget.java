package project.mybudget.models;


import java.sql.*;
import java.util.Date;

public class budget {

    private int bid;
    private double balance;
    private java.sql.Date sdate;
    private String description;
    private int btid;

    public budget(int bid, double balance, java.sql.Date sdate, String description, int btid) {
        this.bid = bid;
        this.balance = balance;
        this.description = description;
        this.sdate = sdate;
        this.btid = btid;
    }

    public void addToDatabase(int pid, Statement stmt) throws SQLException {
        if (btid == 1 || btid == 2) {
            sdate = new java.sql.Date(System.currentTimeMillis());


            stmt.executeUpdate("INSERT INTO budget (balance, sdate, description, btid) VALUES ('" + balance + "', '" + sdate + "', '" + description + "', '" + btid + "');");

            String query = "SELECT bid FROM budget WHERE balance = '" + balance + "' AND description = '" + description.toLowerCase() + "' AND btid = '" + btid + "' AND sdate = '" + sdate + "';";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                bid = rs.getInt(1);
            }
            stmt.executeUpdate("INSERT INTO person_budgets (pid, bid, sdate) VALUES ('" + pid + "', '" + bid + "', '" + sdate + "')");
        }
    }

    public static void removeFromDatabase (Statement stmt, String id) throws SQLException {

        String query = ("DELETE FROM receipt WHERE bid = "+id);
        stmt.executeUpdate(query);

        query = ("DELETE FROM person_budgets WHERE bid = "+id);
        stmt.executeUpdate(query);

        query = ("DELETE FROM budget WHERE bid = "+id);
        stmt.executeUpdate(query);
    }


    public int getBtid() {
        return btid;
    }

    public int getBid() {
        return bid;
    }

    public double getBalance() {
        return balance;
    }

    public Date getSdate() {
        return sdate;
    }

    public String getDescription() {
        return description;
    }
}