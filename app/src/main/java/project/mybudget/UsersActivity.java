package project.mybudget;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.*;
import java.util.ArrayList;

import project.mybudget.Authentication.Login;
import project.mybudget.adapters.UsersAdapter;
import project.mybudget.models.item;
import project.mybudget.models.person;

public class UsersActivity extends AppCompatActivity {

    UsersAdapter adapter;
    SearchView searchView;
    ArrayList<person> persons = new ArrayList<person>();
    private RecyclerView itemList;
    Spinner itemSpinner;

    ArrayList itemsTitles = new ArrayList();
    ArrayList<item> items = new ArrayList<item>();
    ArrayAdapter<String> itemsAdapter;


    String type;// either "add" or "current"
    int bid; // gives the bid that we will do tasks on

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        type = getIntent().getStringExtra("type");
        bid = getIntent().getIntExtra("bid", 0);

        if (type.equals("current"))
            getSupportActionBar().setTitle(R.string.CurrentUsers);
        else if (type.equals("add"))
            getSupportActionBar().setTitle(R.string.AddnewUser);
        else {
            //admin is entered
            getSupportActionBar().setTitle(R.string.UsersList);
        }

        itemList = (RecyclerView) findViewById(R.id.recycler_view);
        searchView = (SearchView) findViewById(R.id.searchView);
        SharedPreferences sharedPreferences = getSharedPreferences(Login.SHARED_PREF_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString(Login.KEY_USERNAME, null);
        searchView.setQueryHint(username);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        itemList.setLayoutManager(linearLayoutManager);

        itemList.setHasFixedSize(true);
        String query = "SELECT * FROM person WHERE pid != " + Login.person.getPid() + " AND pid != 0";

        if (type.equalsIgnoreCase("current")) {
            query += " AND pid IN (SELECT pid FROM person_budgets WHERE bid = " + bid + ")";
        } else {
            query += " AND pid NOT IN (SELECT pid FROM person_budgets WHERE bid = " + bid + ")";
        }

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute(query);

        adapter = new UsersAdapter(persons, type, bid);
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemsTitles);
        UsersAdapter temp = new UsersAdapter(adapter.getFilter(""), type, bid);
        itemList.setAdapter(temp);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                UsersAdapter temp = new UsersAdapter(adapter.getFilter(query.trim()), type, bid);
                itemList.setAdapter(temp);
                return false;
            }
        });
    }

    public void updateList(ArrayList<person> people) {
        adapter = new UsersAdapter(people, "current", bid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bases, menu);
        try {
            if (type.equals("current")) {
                getMenuInflater().inflate(R.menu.add_menu, menu);
//                getMenuInflater().inflate(R.menu.filter_menu, menu);
                getMenuInflater().inflate(R.menu.report_menu, menu);
            }
        } catch (Exception e) {

        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            searchView.setVisibility(View.VISIBLE);
            searchView.setIconified(false);
        } else if (item.getItemId() == R.id.add_button) {
            Intent intent = new Intent(this, UsersActivity.class);
            intent.putExtra("type", "add");
            intent.putExtra("bid", bid);
            startActivity(intent);
        } else if (item.getItemId() == R.id.filter_button) {
            showFilterUsersDialog();
        } else if (item.getItemId() == R.id.report_button) {
            Intent intent = new Intent(this, GeneratedReportActivity.class);
            intent.putExtra("bid", bid);
            startActivity(intent);
        } else
            finish();
        return super.onOptionsItemSelected(item);
    }


    public boolean showFilterUsersDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_users_filter);


        // set the custom dialog components - text, image and button

        Button submit = (Button) dialog.findViewById(R.id.submit);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        // if button is clicked, close the custom dialog
        itemSpinner = dialog.findViewById(R.id.item_spinner);
        final EditText numOfitems = dialog.findViewById(R.id.num_items_purchased);


        itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setAdapter(itemsAdapter);
        AsyncTaskRunner2 runner = new AsyncTaskRunner2();
        if (Login.person.getPid() == 0){
            runner.execute("SELECT * FROM item group by name");
        }else {
            runner.execute("SELECT * FROM item WHERE catid in (SELECT catid FROM category WHERE pid = " + Login.person.getPid() + ")");
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = itemSpinner.getSelectedItemPosition();
//                int itemId = items.get(index).getItemid();

                String query = "SELECT * FROM person WHERE pid != " + Login.person.getPid() + " AND pid IN (SELECT pid FROM person_budgets WHERE bid =" + bid + ")";
                if (index != 0) {
                    query += " AND pid IN (SELECT pid FROM receipt WHERE itemid IN ( SELECT itemid FROM item WHERE name = \"" + items.get(index - 1).getName() + "\" ) AND bid= " + bid + ")";
                }
                if (!numOfitems.getText().toString().equals("")) {

                    int numberOfItems = Integer.parseInt(numOfitems.getText().toString());
                    query += "AND pid IN (SELECT pid FROM receipt where bid = " + bid + " GROUP BY pid HAVING count(*) >= " + numberOfItems +")";

                }

                AsyncTaskRunner filter = new AsyncTaskRunner();
                filter.execute(query);
                //after that
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        return true;
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, ArrayList<person>> {


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<person> doInBackground(String... params) {

            try {
                Statement stmt = Login.stmt;
                ResultSet rs = stmt.executeQuery(params[0]);
                persons.clear();
                while (rs.next()) {
                    persons.add(new person(rs.getInt(1), rs.getString(2), rs.getString(2), rs.getString(4), rs.getString(5), rs.getString(6), "", "", rs.getTimestamp(7)));
                }
            } catch (Exception e) { // handle to connect to the database server
                e.printStackTrace();
            }
            return persons;
        }

        @Override
        protected void onPostExecute(ArrayList<person> persons) {
            super.onPostExecute(persons);
            if (type.equals("current"))
                itemList.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

    private class AsyncTaskRunner2 extends AsyncTask<String, String, ArrayList<item>> {


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<item> doInBackground(String... params) {

            try {
                Statement stmt = Login.stmt;
                ResultSet rs = stmt.executeQuery(params[0]);
                items.clear();
                while (rs.next()) {
                    items.add(new item(rs.getInt(1), rs.getString(2), rs.getInt(3)));
                }

            } catch (SQLException e) { // handle to connect to the database server
                e.printStackTrace();

            }
            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<item> items) {
            super.onPostExecute(items);
            itemsTitles.clear();
            itemsTitles.add(getString(R.string.ChooseItem));
            for (item item : items) {
                itemsTitles.add(item.getName());
            }

            itemsAdapter = new ArrayAdapter<String>(Login.context, android.R.layout.simple_spinner_item, itemsTitles);
            itemSpinner.setAdapter(itemsAdapter);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }
}
