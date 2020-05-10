package project.mybudget.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import project.mybudget.Authentication.Login;
import project.mybudget.CategoriesActivity;
import project.mybudget.R;
import project.mybudget.UsersActivity;

public class AdminDashboard extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);


        getSupportActionBar().setTitle("ADMIN");

        findViewById(R.id.list_category_button).setOnClickListener(this);
        findViewById(R.id.list_wallets_button).setOnClickListener(this);
        findViewById(R.id.list_receipts_button).setOnClickListener(this);
        findViewById(R.id.list_users_button).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.list_category_button:
                intent = new Intent(this, CategoriesActivity.class);

                break;
            case R.id.list_wallets_button:
                intent = new Intent(this, WalletsActivity.class);

                break;

            case R.id.list_receipts_button:
                intent = new Intent(this, ReceiptsActivity.class);

                break;
            case R.id.list_users_button:
                intent = new Intent(this, UsersActivity.class);

                break;
        }
        intent.putExtra("type", "admin");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_out_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, Login.class));
        finish();
        return super.onOptionsItemSelected(item);
    }
}
