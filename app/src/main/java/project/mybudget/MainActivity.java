package project.mybudget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;


import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;


import project.mybudget.Admin.WalletsActivity;
import project.mybudget.Authentication.Login;
import project.mybudget.fragments.BottomBarAdapter;
import project.mybudget.fragments.NoSwipePager;
import project.mybudget.models.Receipt;
import project.mybudget.models.budget;
import project.mybudget.models.category;
import project.mybudget.models.item;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private DrawerLayout mainDrawer;
    private ActionBarDrawerToggle drawerToggle;
    int x = 5;
    private NavigationView mainNavigator;
    static NoSwipePager viewPager;
    private BottomBarAdapter pagerAdapter;
    private FloatingActionButton fab;
    BottomNavigationView bottomNavigator;
    public static Context context;
    ArrayList walletsTitles = new ArrayList();
    ArrayList<budget> wallets = new ArrayList<budget>();
    Spinner walletSpinner;
    ArrayAdapter<String> walletsAdapter;
    ArrayAdapter<String> categoriesAdapter;
    ArrayAdapter<String> itemsAdapter;

    ArrayList categoriesTitles = new ArrayList();
    ArrayList<category> categories = new ArrayList<category>();


    Spinner categorySpinner;
    Spinner itemSpinner;

    ArrayList itemsTitles = new ArrayList();
    ArrayList<item> items = new ArrayList<item>();

    Login loginFragment = new Login();
    HomeActivity homeFragment = new HomeActivity();
    ReportsActivity reportsFragment = new ReportsActivity();
    WalletsFragment walletsFragment = new WalletsFragment();
    SettingsActivity settingsFragment = new SettingsActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Locale.setDefault(getResources().getConfiguration().locale);//set new locale as default

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.Home);

        mainDrawer = findViewById(R.id.drawer_main);
        drawerToggle = new ActionBarDrawerToggle(this, mainDrawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerToggle.syncState();


        //this viewPager is the area where fragments will be shown
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setPagingEnabled(false);

        //this pagerAdapter take care of arranging fragments

        setViewPager();

        // this drawerToggle to keep syncing if main drawer is opened or closed

        mainDrawer.addDrawerListener(drawerToggle);

        mainNavigator = findViewById(R.id.navigator_main);
        mainNavigator.setNavigationItemSelectedListener(navigationListener);

        bottomNavigator = findViewById(R.id.nav_view_bottom);
        bottomNavigator.setOnNavigationItemSelectedListener(bottomNavigationListener);

        fab = findViewById(R.id.add_receipt_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddReceiptDialog();
            }
        });
    }

    public void setViewPager() {
        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());
        pagerAdapter.addFragments(homeFragment);
        pagerAdapter.addFragments(reportsFragment);
        pagerAdapter.addFragments(walletsFragment);
        pagerAdapter.addFragments(settingsFragment);

        viewPager.setAdapter(pagerAdapter);
    }

    public void setActionBarTitle(String title) {
        // set the title of action as the fragment title
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        //Todo: add items to prefrences
        String Language = "en_US";

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.create_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.create_item) {
//            showCreateItemDialog();
            return true;
        }
        // show the selected option .. open the drawer
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // show the page and change the title
            Configuration config = new Configuration();//get Configuration
            String languageToLoad = getSharedPreferences(Login.SHARED_PREF_NAME, Login.MODE_PRIVATE).getString("lang", context.getResources().getConfiguration().locale.getLanguage());;
            Locale locale = new Locale(languageToLoad);//Set Selected             context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            config.locale = locale;//set config locale as selected locale
            StartupActivity.context.getResources().updateConfiguration(config,
                    StartupActivity.context.getResources().getDisplayMetrics());
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    setActionBarTitle(getString(R.string.Home));
                    return true;
                case R.id.navigation_reports:
                    viewPager.setCurrentItem(1);
                    setActionBarTitle(getString(R.string.Reports));
                    return true;
                case R.id.navigation_wallets:
                    viewPager.setCurrentItem(2);
                    setActionBarTitle(getString(R.string.Wallets));
                    return true;
                case R.id.navigation_setting:
                    viewPager.setCurrentItem(3);
                    setActionBarTitle(getString(R.string.Settings));
                    return true;
            }

            return false;
        }
    };

    NavigationView.OnNavigationItemSelectedListener navigationListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // show the page and change the title then close the drawer
            Intent intent;
            switch (item.getItemId()) {
                case R.id.nav_main:
                    viewPager.setCurrentItem(0);
                    setActionBarTitle(getString(R.string.Home));
                    break;
                case R.id.invite_friend:
                    intent = new Intent(context, WalletsActivity.class);
                    intent.putExtra("id", Login.person.getPid());
                    startActivity(intent);
                    break;
                case R.id.nav_categories:
                    intent = new Intent(context, CategoriesActivity.class);
                    intent.putExtra("type", "user");
                    startActivity(intent);
                    break;
            }
            mainDrawer.closeDrawer(GravityCompat.START);
            return true;
        }
    };

    public static void setPage() {
        viewPager.setCurrentItem(0);
    }

    int catid = 0;

    public boolean showAddReceiptDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_receipt);

        dialog.setTitle(R.string.AddReceipt);


        final Button submit = (Button) dialog.findViewById(R.id.submit);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        final EditText receiptName = dialog.findViewById(R.id.receipt_name);
        final RadioGroup receiptType = dialog.findViewById(R.id.receipt_type);
        final EditText receiptAmount = dialog.findViewById(R.id.receipt_amount);
        RadioButton expenseButton = dialog.findViewById(R.id.expense);


        receiptType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                AsyncTaskRunner runner = new AsyncTaskRunner();
                if (receiptType.getCheckedRadioButtonId() == R.id.expense)
                    runner.execute("categories", "expenses");
                else
                    runner.execute("categories", "income");
            }
        });
        expenseButton.setChecked(true);
        walletSpinner = dialog.findViewById(R.id.wallet_spinner);
        categorySpinner = dialog.findViewById(R.id.category_spinner);
        itemSpinner = dialog.findViewById(R.id.item_spinner);

        AsyncTaskRunner budgetsRunner = new AsyncTaskRunner();
        budgetsRunner.execute("wallets");

        walletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                int bid = wallets.get(index).getBid();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

//        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
//                catid = categories.get(index).getCatID();
//
//                AsyncTaskRunner categoriesRunner = new AsyncTaskRunner();
//                categoriesRunner.execute("items");
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//
//        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    int budgetIndex = walletSpinner.getSelectedItemPosition();
                    int bid = wallets.get(budgetIndex).getBid();

                    int catIndex = categorySpinner.getSelectedItemPosition();
                    catid = categories.get(catIndex).getCatID();

//                int itemIndex = itemSpinner.getSelectedItemPosition();
//                int itemid = items.get(itemIndex).getItemid();

                    final EditText receiptAmount = dialog.findViewById(R.id.receipt_amount);

                    if (receiptAmount.getText().toString().equals("")) {
                        Snackbar.make(bottomNavigator, R.string.filltheamount, Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    }
                    double value = Double.parseDouble(receiptAmount.getText().toString());
                    if (receiptType.getCheckedRadioButtonId() == R.id.expense)
                        value *= -1;
                    AsyncTaskRunner2 receiptRunner = new AsyncTaskRunner2(1, value, bid, catid);
                    receiptRunner.execute("receipt", receiptName.getText().toString());
                }
                catch (Exception e){
                    Snackbar.make(bottomNavigator, getString(R.string.failedtoCONNECT), Snackbar.LENGTH_SHORT);
                }
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

    public boolean showCreateItemDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_item);
        dialog.setTitle(R.string.CreateItem);

        // set the custom dialog components - text, image and button
        Button submit = (Button) dialog.findViewById(R.id.submit);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        categorySpinner = (Spinner) dialog.findViewById(R.id.category_spinner);
        final EditText itemName = (EditText) dialog.findViewById(R.id.item_name);

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute("categories");

        // if button is clicked, close the custom dialog
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = categorySpinner.getSelectedItemPosition();
                catid = categories.get(index).getCatID();
                AsyncTaskRunner2 runner = new AsyncTaskRunner2(catid);
                runner.execute(itemName.getText().toString());
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

    private class AsyncTaskRunner extends AsyncTask<String, String, String[]> {
        private String resp = "";

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected String[] doInBackground(String... params) {

            try {
                Class.forName(Login.CLASS_LIBRARY);
                Statement stmt = Login.stmt;

                ResultSet rs;
                if (params[0].equalsIgnoreCase("wallets")) {
                    rs = stmt.executeQuery("SELECT * FROM budget where bid IN (select bid from person_budgets where pid = " + Login.person.getPid() + " )");
                    wallets.clear();
                    while (rs.next()) {
                        wallets.add(new budget(rs.getInt(1), rs.getDouble(2), rs.getDate(3), rs.getString(4), rs.getInt(5)));
                    }
                } else if (params[0].equalsIgnoreCase("categories")) {

                    String query = "SELECT * FROM category where pid = " + Login.person.getPid();
                    if (params.length == 2)
                        query += " AND mtid = (SELECT mtid FROM money_type WHERE type = \"" + params[1] + "\")";
                    rs = stmt.executeQuery(query);
                    categories.clear();
                    while (rs.next()) {
                        categories.add(new category(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5)));
                    }
                } else {
                    int index = categorySpinner.getSelectedItemPosition();
                    rs = stmt.executeQuery("SELECT * FROM item where catid = " + catid);
                    items.clear();
                    while (rs.next()) {
                        items.add(new item(rs.getInt(1), rs.getString(2), rs.getInt(3)));
                    }
                }
            } catch (SQLException e) { // handle to connect to the database server
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return params;
        }

        @Override
        protected void onPostExecute(String[] params) {
            try {

                if (params[0].equalsIgnoreCase("wallets")) {
                    walletsTitles.clear();
                    for (budget budget : wallets) {
                        walletsTitles.add(budget.getDescription());
                    }
                    walletsAdapter = new ArrayAdapter<String>(Login.context, android.R.layout.simple_spinner_item, walletsTitles);
                    walletsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    walletSpinner.setAdapter(walletsAdapter);
                } else if (params[0].equalsIgnoreCase("categories")) {
                    categoriesTitles.clear();
                    for (category category : categories) {
                        categoriesTitles.add(category.getName());
                    }
                    categoriesAdapter = new ArrayAdapter<String>(Login.context, android.R.layout.simple_spinner_item, categoriesTitles);
                    categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(categoriesAdapter);
                } else {
                    itemsTitles.clear();
                    for (item item : items) {
                        itemsTitles.add(item.getName());
                    }
                    itemsAdapter = new ArrayAdapter<String>(Login.context, android.R.layout.simple_spinner_item, itemsTitles);
                    itemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    itemSpinner.setAdapter(itemsAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

    class AsyncTaskRunner2 extends AsyncTask<String, String, ArrayList<item>> {

        private int catid;
        private int itemid;
        private double value;
        private int bid;


        public AsyncTaskRunner2(int catid) {
            this.catid = catid;
        }

        public AsyncTaskRunner2(int itemid, double value, int bid, int catid) {
            this.itemid = itemid;
            this.value = value;
            this.bid = bid;
            this.catid = catid;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<item> doInBackground(String... params) {
            try {
                Statement stmt = Login.stmt;
                if (params[0].equalsIgnoreCase("receipt")) {
                    Receipt receipt = new Receipt(0, params[1], itemid, value, new Timestamp(System.currentTimeMillis()), bid, Login.person.getPid(), 4, catid);
                    receipt.addToDatabase(stmt);

                } else {
                    item item = new item(0, params[0], catid);
                    item.addToDatabase(stmt);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                Snackbar.make(bottomNavigator, getString(R.string.failedtoCONNECT), Snackbar.LENGTH_SHORT);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<item> items) {
            super.onPostExecute(items);
            setViewPager();
            Configuration config = new Configuration();//get Configuration
            String languageToLoad = getSharedPreferences(Login.SHARED_PREF_NAME, Login.MODE_PRIVATE).getString("lang", context.getResources().getConfiguration().locale.getLanguage());;
            Locale locale = new Locale(languageToLoad);//Set Selected             context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            config.locale = locale;//set config locale as selected locale
            StartupActivity.context.getResources().updateConfiguration(config,
                    StartupActivity.context.getResources().getDisplayMetrics());
        }
    }
}