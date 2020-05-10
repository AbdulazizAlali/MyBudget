package project.mybudget.models;

import android.graphics.Color;

import androidx.core.content.ContextCompat;

import java.util.Date;

import project.mybudget.Authentication.Login;
import project.mybudget.R;

public class receiptView {

    private String description;
    private double value;
    private Date rectime;
    private String userName;
    private int catColor;
    private String catName;
    private String budgetName;
    private int recId;

    public receiptView(String description, double value, Date rectime, String userName, String budgetName, String catName, int catColor, int recId) {
        this.description = description;
        this.value = value;
        this.rectime = rectime;
        this.catColor = catColor;
        this.userName = userName;
        this.catName = catName;
        this.budgetName = budgetName;
        this.recId = recId;
    }

    public String getDescription() {
        return description;
    }

    public double getValue() {
        return value;
    }

    public Date getRectime() {
        return rectime;
    }

    public int getCatColor() {
        return catColor;
    }

    public String getUserName() {
        return userName;
    }

    public String getCatName() {
        return catName;
    }

    public String getBudgetName() {
        return budgetName;
    }

    public int getColor() {
        if (value > 0)
            return ContextCompat.getColor(Login.context, R.color.colorPrimary);
        else return Color.RED;
    }

    public int getRecId() {
        return recId;
    }

}
