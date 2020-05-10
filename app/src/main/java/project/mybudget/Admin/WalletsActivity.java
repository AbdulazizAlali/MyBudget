package project.mybudget.Admin;


import android.os.Bundle;
import android.view.MenuItem;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import project.mybudget.R;
import project.mybudget.ReportsActivity;
import project.mybudget.WalletsListFragment;


public class WalletsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);


        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.sliding_tabs);
        tabs.setupWithViewPager(viewPager);


    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {


        ReportsActivity.Adapter adapter = new ReportsActivity.Adapter(getSupportFragmentManager());

        if (getIntent().getIntExtra("id", 0) != 0) {
            adapter.addFragment(new WalletsListFragment("personal", getIntent().getIntExtra("id", 0)), getString(R.string.Personal));
            adapter.addFragment(new WalletsListFragment("groups", getIntent().getIntExtra("id", 0)), getString(R.string.Groups));
            getSupportActionBar().setTitle(R.string.ChooseBudget);
        } else {
            adapter.addFragment(new WalletsListFragment("personalAdmin", 0), getString(R.string.Personal));
            adapter.addFragment(new WalletsListFragment("groupsAdmin", 0), getString(R.string.Groups));
            getSupportActionBar().setTitle(R.string.BudgetsList);
        }
        viewPager.setAdapter(adapter);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        //Add the menu to the Action bar
//        getMenuInflater().inflate(R.menu.back_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
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


}
