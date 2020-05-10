package project.mybudget.Admin;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

import project.mybudget.Authentication.Login;
import project.mybudget.R;
import project.mybudget.adapters.ReceiptsAdapter;
import project.mybudget.models.receiptView;

public class ReceiptsActivity extends AppCompatActivity {

    ReceiptsAdapter adapter;
    private RecyclerView itemList;
    ArrayList<receiptView> receipts = new ArrayList<receiptView>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_recycler);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(R.string.ReceiptsList);

        itemList = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        itemList.setLayoutManager(linearLayoutManager);

        itemList.setHasFixedSize(true);
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, ArrayList<receiptView>> {

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
                        "INNER JOIN category ON category.catid = receipt.catid) order by rectime\n";

                ResultSet rs = stmt.executeQuery(query);

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
