package project.mybudget;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import project.mybudget.Authentication.Login;
import project.mybudget.adapters.ReceiptsAdapter;
import project.mybudget.models.receiptView;


public class ReportsFragment extends Fragment {

    int bid;
    int days;

    public ReportsFragment(int bid, int days) {
        this.bid = bid;
        this.days = days;
    }

    public ArrayList<receiptView> receipts = new ArrayList<receiptView>();
    ReceiptsAdapter adapter;
    private RecyclerView itemList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reports, container, false);

        itemList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        itemList.setLayoutManager(linearLayoutManager);

        itemList.setHasFixedSize(true);

        AnyChartView anyChartView = rootView.findViewById(R.id.any_chart_view);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);

        Cartesian cartesian = AnyChart.column();


        List<DataEntry> data = new ArrayList<>();
        AsyncTaskRunner gettingData = new AsyncTaskRunner();
        try {
            if (bid != 0) {
                receipts = gettingData.execute(" And budget.bid = " + bid).get();
            } else
                receipts = gettingData.execute(" And person.pid = " + Login.person.getPid()).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar cal;

        for (int k = 0; k < days; k++) {
            double value = 0;
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -k);
            int currentDay = cal.get(Calendar.DAY_OF_MONTH);
            for (receiptView receipt : receipts) {
                cal.setTime(receipt.getRectime());
                if (cal.get(Calendar.DAY_OF_MONTH) == currentDay) {
                    value += receipt.getValue();
                    data.add(new ValueDataEntry(currentDay, value));
                }
            }
            if(value == 0) {
                data.add(new ValueDataEntry(currentDay, 0));
            }
        }


        cartesian.data(data);
        cartesian.title(getString(R.string.GeneralReport));
        anyChartView.setChart(cartesian);


        return rootView;
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, ArrayList<receiptView>> {
        private String resp = "";

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<receiptView> doInBackground(String... params) {

            try {
                Statement stmt = Login.stmt;
                String query = "\n" +
                        "SELECT receipt.description, receipt.value, receipt.rectime, person.username , budget.description AS budgetName, category.name, category.color, receipt.recid\n" +
                        "FROM (((receipt\n" +
                        "INNER JOIN person ON person.pid = receipt.pid )\n" +
                        "INNER JOIN budget ON budget.bid = receipt.bid)\n" +
                        "INNER JOIN category ON category.catid = receipt.catid)\n WHERE rectime BETWEEN DATE_SUB(NOW(), INTERVAL "+days+" DAY) AND NOW()" ;
                query += params[0];


                ResultSet rs = stmt.executeQuery(query);
                receipts.clear();
                while (rs.next()) {
                    receipts.add(new receiptView(rs.getString(1), rs.getDouble(2), rs.getDate(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7), rs.getInt(8)));
                }

            } catch (SQLException e) { // handle to connect to the database server
                e.printStackTrace();
            }
            return receipts;
        }

        @Override
        protected void onPostExecute(ArrayList<receiptView> receipts) {
            super.onPostExecute(receipts);
            Collections.reverse(receipts);
            adapter = new ReceiptsAdapter(receipts);
            itemList.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

}
