package project.mybudget.Authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;

import java.sql.*;
import java.util.Locale;

import project.mybudget.Admin.AdminDashboard;
import project.mybudget.MainActivity;
import project.mybudget.R;
import project.mybudget.SettingsActivity;
import project.mybudget.models.person;


public class Login extends AppCompatActivity implements View.OnClickListener {
    static Button bLogin, bRegister;
    EditText etUsername, etPassword;
    public static Context context;
    View loadingView;
    ProgressBar loadingProgress;
    TextView signinText;
    public static String CLASS_LIBRARY = "com.mysql.jdbc.Driver";
    public static String DRIVER_DATA = "jdbc:mysql://ijj1btjwrd3b7932.cbetxkdyhwsb.us-east-1.rds.amazonaws.com/zgfg4nmv2k2zlecx";
    public static String USER_DATA = "kxxiem2tuf4ixx1h";
    public static String PASSWORD_DATA = "rx9u7xxhef73pd2v";
    private String unicode = "?useUnicode=yes&characterEncoding=UTF-8";
    public static final String SHARED_PREF_NAME = "authentication";
    public static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    public static String USERNAME;
    public static person person;
    public static ResultSet rs;
    public static Statement stmt;
    SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    public static Resources resources;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Locale.setDefault(getResources().getConfiguration().locale);//set new locale as default

        bLogin = (Button)

                findViewById(R.id.bLogin);

        etUsername = (EditText)

                findViewById(R.id.etUsername);

        etPassword = (EditText)

                findViewById(R.id.etPassword);

        bRegister = (Button)

                findViewById(R.id.bRegister);


        loadingView = (View)

                findViewById(R.id.loading_view);

        loadingProgress = (ProgressBar)
                findViewById(R.id.loading_progressBar);

        context = getBaseContext();

        signinText = (TextView)

                findViewById(R.id.loading_signin);
        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context);
        builder.addApi(Auth.GOOGLE_SIGN_IN_API, gso);
        googleApiClient= builder
                .build();


        bLogin.setOnClickListener(this);
        bRegister.setOnClickListener(this);
        context = this;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Login.context);

        AsyncTaskRunner2 runner = new AsyncTaskRunner2();
        runner.execute();

        loginSavedUser();

        signInButton=(SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,1);
            }
        });


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            gotoProfile(result.getSignInAccount().getAccount().name);
        }else{
            Toast.makeText(getApplicationContext(),"Sign in cancel", Toast.LENGTH_LONG).show();
        }
    }
    private void gotoProfile(String name){
        person = new person(0, name,name,"",name.hashCode()+"","","","",null);
        Register.AsyncTaskRunner runner = new Register.AsyncTaskRunner();
                runner.execute(String.valueOf(person.getPid()), person.getUsername(),"",name.hashCode()+"","","",null);
        etUsername.setText(name);
        etPassword.setText(name.hashCode()+"");
        bLogin.callOnClick();
    }



    private void loginSavedUser() {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString(KEY_USERNAME, null);
        String password = sharedPreferences.getString(KEY_PASSWORD, null);

        if (username != null) {
            etUsername.setText(username);
            etPassword.setText(password);
            if (person == null) {
                bLogin.callOnClick();
            }
        }
    }

    @Override
    public void onClick(View view) {


        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
        USERNAME = username;

        switch (view.getId()) {
            case R.id.bLogin:

                if (username.length() == 0 || password.length() == 0) {
                    Snackbar.make(view, R.string.completeall, Snackbar.LENGTH_LONG).show();
                } else {
                    try {


                    AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
                    asyncTaskRunner.execute(username, password);
                    loadingProgress.setVisibility(View.VISIBLE);
                    loadingView.setVisibility(View.VISIBLE);
                    signinText.setVisibility(View.VISIBLE);
                    }catch (Exception e){
                        Snackbar.make(bLogin, getString(R.string.failedtoCONNECT), Snackbar.LENGTH_SHORT);
                    }
                }

                break;
            case R.id.bRegister:
                Intent registerIntent = new Intent(Login.this, Register.class);
                startActivity(registerIntent);
                break;
        }
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        private String resp = "";

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 2) {
                try {

                    //here project is database name, root is username and password is ics324
                    rs = stmt.executeQuery("SELECT * FROM person");

                    boolean found = false;
                    while (rs.next() && !found) {
                        if (rs.getString(2).equalsIgnoreCase(params[0])) {
                            found = true;
                            if (rs.getString(3).equals(params[1])) {
                                resp = getString(R.string.loggedinSuccessfully);
                                person = new person(rs.getInt(1), "", rs.getString(2), "", rs.getString(3), "", "", "", null);
                            } else {
                                resp = getString(R.string.Thepasswordisincorrectforthis);

                                if (rs.getString(1).equalsIgnoreCase(params[0])) {
                                    resp += getString(R.string.usernamePleasetryagain);
                                } else {
                                    resp += getString(R.string.usernamePleasetryagain);
                                }
                            }
                        }
                    }

                    if (!found) {
                        if (params[0].contains("@")) {
                            resp = getString(R.string.notregistered);
                        } else {
                            resp = getString(R.string.nousername);
                        }
                    }


                } catch (SQLException e) { // handle to connect to the database server
                    if (e.getErrorCode() == 0) {
                        e.printStackTrace();
                        resp = getString(R.string.failedtoCONNECT);

                        try {
                            Class.forName(CLASS_LIBRARY);
                            Connection con = DriverManager.getConnection(DRIVER_DATA + unicode, USER_DATA, PASSWORD_DATA);
                            stmt = con.createStatement();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    } else {
                        e.printStackTrace();
                        resp = getString(R.string.mistakeindata);
                        System.out.println(e.getMessage());
                    }
                    e.printStackTrace();
                }catch (NullPointerException e){
                    resp = getString(R.string.failedtoCONNECT);
                    try {
                        Class.forName(CLASS_LIBRARY);
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    Connection con = null;
                    try {
                        con = DriverManager.getConnection(DRIVER_DATA + unicode, USER_DATA, PASSWORD_DATA);

                    stmt = con.createStatement();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            } else { // if some input missing
                resp = getString(R.string.missingdata);
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {

            Snackbar.make(bLogin, resp, Snackbar.LENGTH_LONG).show();

            if (resp.contains(getString(R.string.Successfully))) {
                if (person.getUsername().equalsIgnoreCase(USER_DATA) && person.getPassword().equals(PASSWORD_DATA)) {
                    startActivity(new Intent(context, AdminDashboard.class));
                } else {
                    startActivity(new Intent(context, MainActivity.class));
                }
                finish();
            } else {
                loadingProgress.setVisibility(View.GONE);
                loadingView.setVisibility(View.GONE);
                signinText.setVisibility(View.GONE);
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }

    private class AsyncTaskRunner2 extends AsyncTask<String, String, Void> {


        protected Void doInBackground(String... params) {

            try {
                Class.forName(CLASS_LIBRARY);
                Connection con = DriverManager.getConnection(DRIVER_DATA + unicode, USER_DATA, PASSWORD_DATA);
                stmt = con.createStatement();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
