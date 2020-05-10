package project.mybudget;


import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.flask.colorpicker.ColorPickerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import project.mybudget.Authentication.Login;
import project.mybudget.models.category;


public class CategoriesActivity extends AppCompatActivity {

    FloatingActionButton bAddCategory;
    ArrayList<category> categories = new ArrayList();
    ViewPager viewPager;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        context = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(R.string.categories);

        // Setting ViewPager for each Tabs
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.sliding_tabs);
        tabs.setupWithViewPager(viewPager);
        bAddCategory = findViewById(R.id.addCategoryButton);
        bAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCategoryDialog();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    // Add Fragments to Tabs
    private void setupViewPager() {


        ReportsActivity.Adapter adapter = new ReportsActivity.Adapter(getSupportFragmentManager());

        if (getIntent().getStringExtra("type").equalsIgnoreCase("admin")) {
            adapter.addFragment(new CategoriesListFragment("adminincomes"), getString(R.string.Incomes));
            adapter.addFragment(new CategoriesListFragment("adminexpenses"), getString(R.string.Expenses));
        } else {
            adapter.addFragment(new CategoriesListFragment("income"), getString(R.string.Incomes));
            adapter.addFragment(new CategoriesListFragment("expenses"), getString(R.string.Expenses));
        }

        viewPager.setAdapter(adapter);

    }


    public boolean showAddCategoryDialog() {



        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_category);
        Button submit = (Button) dialog.findViewById(R.id.submit);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);

        dialog.setTitle(R.string.AddCategory);

        // if button is clicked, close the custom dialog
        final ColorPickerView colorPickerView = dialog.findViewById(R.id.color_picker_view);
        final EditText categoryName = dialog.findViewById(R.id.category_name);
        final RadioGroup categoryType = dialog.findViewById(R.id.category_type);




        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    String color = colorPickerView.getSelectedColor() + "";
                    String name = categoryName.getText().toString();
                    String type;
                    if (categoryType.getCheckedRadioButtonId() == R.id.expencesRadio) {
                        type = "1";
                    } else {
                        type = "2";
                    }
                    AsyncTaskRunner createCategoryRunner = new AsyncTaskRunner();
                    createCategoryRunner.execute(name, color, type);

                }catch (Exception e){
                Snackbar.make(bAddCategory, getString(R.string.failedtoCONNECT), Snackbar.LENGTH_SHORT);
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

    private class AsyncTaskRunner extends AsyncTask<String, String, ArrayList<category>> {
        private String resp = "";

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ArrayList<category> doInBackground(String... params) {

            try {
                Statement stmt = Login.stmt;
                category category = new category(0, params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]), Login.person.getPid());
                category.addToDatabase(stmt);
                categories.add(category);
            } catch (SQLException ex) {
                ex.printStackTrace();
                Snackbar.make(bAddCategory, getString(R.string.failedtoCONNECT), Snackbar.LENGTH_SHORT);
            }
            return categories;
        }

        @Override
        protected void onPostExecute(ArrayList<category> wallets) {
            super.onPostExecute(categories);
            setupViewPager();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

}