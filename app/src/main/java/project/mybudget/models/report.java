package project.mybudget.models;

import java.sql.*;

public class report {
    private int reportid;
    private Timestamp report_time;
    private int bid;
    private double balance;
    private int budget_type;
    private int number_users;
    private budget budget;


    public report(int reportid, Timestamp report_time, budget budget, int number_users) {
        this.reportid = reportid;
        this.report_time = report_time;
        this.budget = budget;
        this.bid = budget.getBid();
        this.balance = budget.getBalance();
        this.budget_type = budget.getBtid();
        this.number_users = number_users;
    }

    public boolean addToDatabase(Statement stmt) throws SQLException {
        if (budget_type == 1 || budget_type == 2) {
            ResultSet rs = stmt.executeQuery("SELECT bid, pid FROM person_budgets where bid = '" + budget.getBid() + "' AND sdate = '" + budget.getSdate() + "';");
            int usrs = 0;
            while (rs.next()) {
                usrs++;
            }

            stmt.executeUpdate("INSERT INTO report (report_time, bid, balance, budget_type, number_of_users) VALUES ('" + report_time + "'" + budget.getBid() + "', '" + budget.getBalance() + "', '" + budget_type + "', '" + usrs + "');");
            rs = stmt.executeQuery("SELECT reportid FROM report where report_time = '" + report_time + "' AND bid = '" + bid + "';");
            if (rs.next()) {
                reportid = rs.getInt(1);
                number_users = usrs;
            }
            return true;
        }
        return false;
    }

    public int getReportid() {
        return reportid;
    }

    public Timestamp getReport_time() {
        return report_time;
    }

    public int getNumber_users() {
        return number_users;
    }
}
