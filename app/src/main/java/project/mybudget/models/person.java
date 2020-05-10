package project.mybudget.models;

import android.content.Intent;

import java.sql.*;
import java.util.ArrayList;

import project.mybudget.Authentication.Login;
import project.mybudget.R;

public class person {
    private int pid;
    private String name;
    private String username;
    private String email;
    private String password;
    private String dob;
    private String gender;
    private String phone;
    private Timestamp create_time;

    public person(int pid, String name, String username, String email, String password, String dob, String gender, String phone, Timestamp create_time) {
        this.pid = pid;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.gender = gender;
        this.phone = phone;
        this.create_time = null;
    }

    public int getPid() {
        return pid;
    }

    public void addToDatabase(Statement stmt) throws SQLException {

        create_time = new Timestamp(System.currentTimeMillis());
        stmt.executeUpdate("INSERT INTO person ( username, password) VALUES ( '" + username + "', '" + password + "');");
        ResultSet rs = stmt.executeQuery("SELECT pid FROM person WHERE username = '" + username + "';");
        if (rs.next()) {
            pid = rs.getInt(1);
        }
        Login.context.startActivity(new Intent(Login.context , Login.class));
        addCurrency("USD", 1, stmt);
        budget budget = new budget(0, 0.00, null, Login.context.getString(R.string.DefaultBudget), 1);
        budget.addToDatabase(pid, stmt);


        ArrayList<category> categories = new ArrayList<>(9);

        categories.add(new category(0, Login.context.getString(R.string.NonCategorized), -22912, 1, pid).addToDatabase(stmt));
        
        categories.add(new category(0, Login.context.getString(R.string.Restaurant), -16711911, 1, pid).addToDatabase(stmt));
//        new item(0, "Breakfast", categories.get(0).getCatID()).addToDatabase(stmt);
//        new item(0, "Lunch", categories.get(0).getCatID()).addToDatabase(stmt);
//        new item(0, "Dinner", categories.get(0).getCatID()).addToDatabase(stmt);

        categories.add(new category(0, Login.context.getString(R.string.Personal), -2539265, 1, pid).addToDatabase(stmt));
//        new item(0, "Grocery", categories.get(1).getCatID()).addToDatabase(stmt);
//        new item(0, "Shopping", categories.get(1).getCatID()).addToDatabase(stmt);

//        categories.add(new category(0, "Subscription", -16770561, 1, pid).addToDatabase(stmt));
//        new item(0, "Bills", categories.get(2).getCatID()).addToDatabase(stmt);
//        new item(0, "Television", categories.get(2).getCatID()).addToDatabase(stmt);
//        new item(0, "Internet", categories.get(2).getCatID()).addToDatabase(stmt);

        categories.add(new category(0, Login.context.getString(R.string.Travel), -16731137, 1, pid).addToDatabase(stmt));
//        new item(0, "Tickets", categories.get(3).getCatID()).addToDatabase(stmt);
//        new item(0, "Taxi", categories.get(3).getCatID()).addToDatabase(stmt);
//        new item(0, "Visa", categories.get(3).getCatID()).addToDatabase(stmt);
//        new item(0, "Rent Aar", categories.get(3).getCatID()).addToDatabase(stmt);
//        new item(0, "Shopping", categories.get(3).getCatID()).addToDatabase(stmt);
//        new item(0, "Restaurant", categories.get(3).getCatID()).addToDatabase(stmt);
//        new item(0, "Passport", categories.get(3).getCatID()).addToDatabase(stmt);
//        new item(0, "Cinema", categories.get(3).getCatID()).addToDatabase(stmt);

        categories.add(new category(0, Login.context.getString(R.string.House), -45824, 1, pid).addToDatabase(stmt));
//        new item(0, "Electricity Bill", categories.get(4).getCatID()).addToDatabase(stmt);
//        new item(0, "Water Bill", categories.get(4).getCatID()).addToDatabase(stmt);
//        new item(0, "Telephone bill", categories.get(4).getCatID()).addToDatabase(stmt);
//        new item(0, "Family", categories.get(4).getCatID()).addToDatabase(stmt);

        categories.add(new category(0, Login.context.getString(R.string.Salary), -12517377, 2, pid).addToDatabase(stmt));
//        new item(0, "Work", categories.get(5).getCatID()).addToDatabase(stmt);
//        new item(0, "Bonus", categories.get(5).getCatID()).addToDatabase(stmt);

        categories.add(new category(0, Login.context.getString(R.string.PersonalBusiness), -22912, 2, pid).addToDatabase(stmt));
//        new item(0, "Extra Income", categories.get(6).getCatID()).addToDatabase(stmt);
//        new item(0, "Benefits", categories.get(6).getCatID()).addToDatabase(stmt);

//        categories.add(new category(0, Login.context.getString(R.string.Bank), -10075905, 2, pid).addToDatabase(stmt));
//        new item(0, "Loan", categories.get(7).getCatID()).addToDatabase(stmt);

        categories.add(new category(0, Login.context.getString(R.string.Family), -16711911, 2, pid).addToDatabase(stmt));
//        new item(0, "Family Expenses", categories.get(8).getCatID()).addToDatabase(stmt);
    }

    public void addCurrency(String curName, double dolValue, Statement stmt) throws SQLException {
//        stmt.executeUpdate("INSERT INTO currency (name, dolvalue, pid) VALUES ('" + curName + "', '" + dolValue + "', '" + pid + "');");
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }
}
