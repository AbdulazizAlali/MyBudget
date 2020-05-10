package project.mybudget;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import project.mybudget.Authentication.Login;
import project.mybudget.adapters.ReceiptsAdapter;
import project.mybudget.models.Receipt;
import project.mybudget.models.receiptView;

public class HomeActivity extends Fragment {
    static ReceiptsAdapter adapter;
    private static RecyclerView itemList;
    public static ArrayList<receiptView> receipts = new ArrayList<receiptView>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_recycler, container, false);
        ((MainActivity) getActivity())
                .setActionBarTitle(getString(R.string.Home));

        itemList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        itemList.setLayoutManager(linearLayoutManager);
        itemList.setHasFixedSize(true);

        updateUi();


        return rootView;

    }

    public static void updateUi() {
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                updateUi();
                return true;
            }

            // Called when a user swipes left or right on a ViewHolder
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                final int position = viewHolder.getAdapterPosition();
                Dialog dialog = new AlertDialog.Builder(MainActivity.context)
                        .setTitle(R.string.RemoveReceipt)
                        .setMessage(R.string.suredeleteRceipt)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(R.string.Yesremovereceipt, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                AsyncTaskRunner2 runner = new AsyncTaskRunner2();
                                runner.execute( String.valueOf(receipts.get(position).getRecId()), String.valueOf(receipts.get(position).getValue()));
                                return;
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateUi();
                                return;
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert).create();
                        dialog.show();
                updateUi();
            }
        }).attachToRecyclerView(itemList);
    }

    public static class AsyncTaskRunner extends AsyncTask<String, String, ArrayList<receiptView>> {
        private String resp = "";

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<receiptView> doInBackground(String... params) {

            try {
                Statement stmt = Login.stmt;
                String query = "\n" +
                        "SELECT  receipt.description, receipt.value, receipt.rectime, person.username , budget.description AS budgetName, category.name, category.color, receipt.recid\n " +
                        "FROM (((receipt\n" +
                        "INNER JOIN person ON person.pid = receipt.pid )\n" +
                        "INNER JOIN budget ON budget.bid = receipt.bid)\n" +
                        "INNER JOIN category ON category.catid = receipt.catid)\n" +
                        "WHERE budget.bid IN (SELECT bid FROM person_budgets where pid = " + Login.person.getPid() + ")";

                ResultSet rs = stmt.executeQuery(query);
                receipts.clear();
                while (rs.next()) {
                    receipts.add(new receiptView(rs.getString(1), rs.getDouble(2), rs.getDate(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7), rs.getInt(8)));
                }

            } catch (SQLException e) { // handle to connect to the database server
                e.printStackTrace();
//            } catch ()
            }
            return receipts;
        }

        @Override
        protected void onPostExecute(ArrayList<receiptView> receipts) {
            super.onPostExecute(receipts);
            Collections.reverse(receipts);
            adapter = new ReceiptsAdapter(receipts);
            itemList.setAdapter(adapter);
            Configuration config = new Configuration();//get Configuration
            String languageToLoad = Login.context.getSharedPreferences(Login.SHARED_PREF_NAME, Login.MODE_PRIVATE).getString("lang", Login.context.getResources().getConfiguration().locale.getLanguage());;
            Locale locale = new Locale(languageToLoad);//Set Selected             context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            config.locale = locale;//set config locale as selected locale
            StartupActivity.context.getResources().updateConfiguration(config,
                    StartupActivity.context.getResources().getDisplayMetrics());
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

    public static class AsyncTaskRunner2 extends AsyncTask<String, String, ArrayList<receiptView>> {
        private String resp = "";

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<receiptView> doInBackground(String... params) {

            try {
                Statement stmt = Login.stmt;
                Receipt.removeFromDatabase( stmt, params[0], params[1]);

            } catch (SQLException e) { // handle to connect to the database server
                e.printStackTrace();
            }
            return receipts;
        }

        @Override
        protected void onPostExecute(ArrayList<receiptView> receiptViews) {
            super.onPostExecute(receiptViews);
            updateUi();
        }
    }



}
