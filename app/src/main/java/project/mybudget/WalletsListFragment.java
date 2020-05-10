package project.mybudget;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.sql.*;
import java.util.ArrayList;

import project.mybudget.Authentication.Login;
import project.mybudget.adapters.WalletsAdapter;
import project.mybudget.models.Receipt;
import project.mybudget.models.budget;
import project.mybudget.models.receiptView;

public class WalletsListFragment extends Fragment {
    private WalletsAdapter adapter;
    private RecyclerView itemList;
    ArrayList<budget> budgets = new ArrayList<>();

    private String type;  //personal or groups
    private int id;
    private String query;
    public WalletsListFragment(String type, int id) {
        //parameter is either "personal", "group", "personalAdmin", "groupAdmin"
        this.type = type;
        this.id = id;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.content_recycler, container, false);
        String typeList = type.replace("Admin", "");

        query = "SELECT * FROM budget WHERE btid = (SELECT btid FROM budget_type WHERE type = \"" + typeList + "\")";
        if (type.equalsIgnoreCase("personal") || (type.equalsIgnoreCase("groups"))) {
            query += " AND bid in( SELECT bid from person_budgets where pid = " + id + ")";
        }
        itemList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        itemList.setLayoutManager(linearLayoutManager);
        itemList.setHasFixedSize(true);

        updateUi();

        return rootView;
    }

    private void updateUi() {
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute(query);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                final int position = viewHolder.getAdapterPosition();
                Dialog dialog = new AlertDialog.Builder(MainActivity.context)
                        .setTitle(R.string.Removebudget)
                        .setMessage(R.string.Suredeletebudget)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(R.string.Yesremovebudget, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                try {


                                AsyncTaskRunner2 runner = new AsyncTaskRunner2();
                                runner.execute( String.valueOf(budgets.get(position).getBid()));
                                return;
                                }catch (Exception e){
                                    Snackbar.make(getView(), getString(R.string.failedtoCONNECT), Snackbar.LENGTH_SHORT);
                                }
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

    private class AsyncTaskRunner extends AsyncTask<String, String, ArrayList<budget>> {
        private String resp = "";

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<budget> doInBackground(String... params) {

            try {
                Statement stmt = Login.stmt;
                budgets.clear();
                ResultSet rs = stmt.executeQuery(params[0]);
                while (rs.next()) {
                    budgets.add(new budget(rs.getInt(1), rs.getDouble(2), rs.getDate(3), rs.getString(4), rs.getInt(5)));
                }

            } catch (Exception e) { // handle to connect to the database server
                e.printStackTrace();
            }
            return budgets;
        }

        @Override
        protected void onPostExecute(ArrayList<budget> budgets) {
            adapter = new WalletsAdapter(budgets);
            itemList.setAdapter(adapter);
        }
    }

    public class AsyncTaskRunner2 extends AsyncTask<String, String, ArrayList<budget>> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<budget> doInBackground(String... params) {

            try {
                Statement stmt = Login.stmt;
                budget.removeFromDatabase( stmt, params[0]);

            } catch (SQLException e) { // handle to connect to the database server
                e.printStackTrace();
            }
            return budgets;
        }

        @Override
        protected void onPostExecute(ArrayList<budget> budgets) {
            super.onPostExecute(budgets);
            updateUi();
            HomeActivity.updateUi();
        }
    }
}
