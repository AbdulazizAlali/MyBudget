package project.mybudget;


import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import project.mybudget.Authentication.Login;
import project.mybudget.models.budget;


public class WalletsFragment extends Fragment {

    ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tabs, container, false);
        // Setting ViewPager for each Tabs
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabs.setupWithViewPager(viewPager);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {


        ReportsActivity.Adapter adapter = new ReportsActivity.Adapter(getChildFragmentManager());
        adapter.addFragment(new WalletsListFragment("personal", Login.person.getPid()), getString(R.string.Personal));
        adapter.addFragment(new WalletsListFragment("groups", Login.person.getPid()), getString(R.string.Groups));

        viewPager.setAdapter(adapter);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        createBudgetDialog();

        return true;
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);

        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public boolean createBudgetDialog() {
        final Dialog dialog = new Dialog(MainActivity.context);
        dialog.setContentView(R.layout.dialog_budget);


        // set the custom dialog components - text, image and button

        Button submit = (Button) dialog.findViewById(R.id.submit);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        // if button is clicked, close the custom dialog
        final EditText budgetName = dialog.findViewById(R.id.budget_name);
        final EditText budgetBalance = dialog.findViewById(R.id.budget_balance);
        final RadioGroup budgetType = dialog.findViewById(R.id.budget_type);
        final RadioButton personalButton = dialog.findViewById(R.id.personalRadio);
        personalButton.setChecked(true);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {


                    String name = budgetName.getText().toString();
                    double balance = Double.parseDouble(budgetBalance.getText().toString());
                    int type;
                    if (budgetType.getCheckedRadioButtonId() == R.id.personalRadio) {
                        type = 1;
                    } else {
                        type = 2;
                    }
                    AsyncTaskRunner runner = new AsyncTaskRunner(balance, name, type);
                    runner.execute();
                }
                catch (Exception e){
                    Snackbar.make(getView(), getString(R.string.failedtoCONNECT), Snackbar.LENGTH_SHORT);
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

    class AsyncTaskRunner extends AsyncTask<String, String, Void> {

        private double balance;
        private String description;
        private int type;

        public AsyncTaskRunner(double balance, String description, int type) {
            this.balance = balance;
            this.description = description;
            this.type = type;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(String... params) {
            try {
                Statement stmt = Login.stmt;

                budget budget = new budget(0, balance, null, description, type);
                budget.addToDatabase(Login.person.getPid(), stmt);


            } catch (SQLException e) {
                e.printStackTrace();
                Snackbar.make(getView(), getString(R.string.failedtoCONNECT), Snackbar.LENGTH_SHORT);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setupViewPager(viewPager);

        }
    }


}
