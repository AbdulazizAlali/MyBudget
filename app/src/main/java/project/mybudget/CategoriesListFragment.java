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

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

import project.mybudget.Authentication.Login;
import project.mybudget.adapters.CategoriesAdapter;
import project.mybudget.models.budget;
import project.mybudget.models.category;

public class CategoriesListFragment extends Fragment {

    private CategoriesAdapter adapter;
    private RecyclerView itemList;
    private String type;//
    ArrayList<category> categories = new ArrayList<category>();
    String query = null;

    public CategoriesListFragment(String type) {
        this.type = type; // either "incomes" , "expenses", "adminincomes", "adminexpences"
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_recycler, container, false);
        itemList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        itemList.setLayoutManager(linearLayoutManager);

        itemList.setHasFixedSize(true);



        query = "SELECT * FROM category WHERE mtid = (SELECT mtid FROM money_type where type = ";
        String income = "income";
        String expence = "expenses";

        if (type.equalsIgnoreCase("income")) {
            query += "\"" + income + "\") AND pid =" + Login.person.getPid();
        } else if (type.equalsIgnoreCase("expenses")) {
            query += "\"" + expence + "\") AND pid =" + Login.person.getPid();
        } else if (type.equalsIgnoreCase("adminincomes")) {
            query += "\"" + income + "\") group by name";
        } else if (type.equalsIgnoreCase("adminexpenses")) {
            query += "\"" + expence + "\") group by name";
        }


        adapter = new CategoriesAdapter(categories);
        itemList.setAdapter(adapter);

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
                Dialog dialog = new AlertDialog.Builder(CategoriesActivity.context)
                        .setTitle(R.string.RemoveCategory)
                        .setMessage(R.string.suredeletecategory)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(R.string.Yesremovecategory, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                AsyncTaskRunner2 runner = new AsyncTaskRunner2();
                                runner.execute( String.valueOf(categories.get(position).getCatid()));
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

    private class AsyncTaskRunner extends AsyncTask<String, String, ArrayList<category>> {
        private String resp = "";

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<category> doInBackground(String... params) {

            try {

                Statement stmt = Login.stmt;

                String username = Login.USERNAME;
                ResultSet rs = stmt.executeQuery(params[0]);
                categories.clear();
                while (rs.next()) {
                    categories.add(new category(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5)));
                }
            } catch (SQLException e) { // handle to connect to the database server
                e.printStackTrace();
            }
            return categories;
        }

        @Override
        protected void onPostExecute(ArrayList<category> receipts) {
            super.onPostExecute(receipts);
            adapter = new CategoriesAdapter(categories);
            itemList.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

    public class AsyncTaskRunner2 extends AsyncTask<String, String, ArrayList<category>> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<category> doInBackground(String... params) {

            try {
                Statement stmt = Login.stmt;
                category.removeFromDatabase( stmt, params[0]);

            } catch (Exception e) { // handle to connect to the database server
                e.printStackTrace();
            }
            return categories;
        }

        @Override
        protected void onPostExecute(ArrayList<category> categories) {
            super.onPostExecute(categories);
            updateUi();
            HomeActivity.updateUi();
            Configuration config = new Configuration();//get Configuration
            String languageToLoad = Login.context.getSharedPreferences(Login.SHARED_PREF_NAME, Login.MODE_PRIVATE).getString("lang", Login.context.getResources().getConfiguration().locale.getLanguage());;
            Locale locale = new Locale(languageToLoad);//Set Selected             context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            config.locale = locale;//set config locale as selected locale
            StartupActivity.context.getResources().updateConfiguration(config,
                    StartupActivity.context.getResources().getDisplayMetrics());
        }
    }
}