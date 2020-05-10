package project.mybudget.models;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.sql.*;
import java.util.Calendar;
import java.util.Date;

import project.mybudget.Authentication.Login;
import project.mybudget.R;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Receipt {

    private int RecID;
    private String description;
    private int itemid;
    private double value;
    private Date rectime;
    private int PID;
    private int Bid;
    private int RFID;
    private int catid;


    public Receipt(int recID, String description, int itemid, double value, Date rectime, int bid, int pid, int RFID, int catid) {
        RecID = recID;
        this.description = description;
        this.itemid = itemid;
        this.value = value;
        this.rectime = rectime;
        this.PID = pid;
        this.Bid = bid;
        this.RFID = 1;
        this.catid = catid;
    }


    public void addToDatabase(Statement stmt) throws SQLException {

        stmt.executeUpdate("INSERT INTO receipt (description, value, rectime, bid, pid, rfid, catid) VALUES ('" + description + "', '" + value + "', '" + rectime + "','" + Bid + "', '" + PID + "','" + RFID + "','" + catid + "');");

        String query2 = " UPDATE budget SET balance = balance + " + value + "WHERE bid= " + Bid;
        stmt.executeUpdate(query2);

    }

    public static void removeFromDatabase(Statement stmt, String id, String value) throws SQLException {


        ResultSet rs = stmt.executeQuery("SELECT bid from receipt WHERE recid = " + id);
        int bid = 0;
        if (rs.next())
            bid = rs.getInt(1);
        String query2 = " UPDATE budget SET balance = balance - " + value + "WHERE bid = " + bid;
        stmt.executeUpdate(query2);

        String query = ("DELETE FROM receipt WHERE recid = " + id);
        stmt.executeUpdate(query);

    }


    public int getItemid() {
        return itemid;
    }

    public int getRecID() {
        return RecID;
    }

    public String getDescription() {
        return description;
    }

    public double getValue() {
        return value;
    }

    public Date getDate() {
        return rectime;
    }

    public int getPID() {
        return PID;
    }

    public int getBid() {
        return Bid;
    }

    public int getRFID() {
        return RFID;
    }

    public int getCatid() {
        return catid;
    }


    public int getColor() {
        if (value > 0)
            return ContextCompat.getColor(Login.context, R.color.colorPrimary);
        else return Color.RED;
    }

    public int getDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(rectime.getTime()));
        return cal.get(Calendar.DAY_OF_MONTH);
    }

}