package project.mybudget.Authentication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.sql.*;
import java.util.Calendar;

import project.mybudget.R;
import project.mybudget.models.person;


public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText etName, etAge, etUsername, etPassword, etEmail, etPhone;
    static Button bRegister, bLogin;
    DatePickerDialog picker;
//    RadioGroup rGender;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;

//        etName = (EditText) findViewById(R.id.etName);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
//        etAge = (EditText) findViewById(R.id.etAge);
        bRegister = (Button) findViewById(R.id.bRegister);
        bLogin = (Button) findViewById(R.id.bLogin);
//        rGender = (RadioGroup) findViewById(R.id.gender_group);
//        etEmail = (EditText) findViewById(R.id.etEmail);
//        etPhone = (EditText) findViewById(R.id.etPhone);


        bRegister.setOnClickListener(this);
        bLogin.setOnClickListener(this);
//        etAge.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Calendar cldr = Calendar.getInstance();
//                int day = cldr.get(Calendar.DAY_OF_MONTH);
//                int month = cldr.get(Calendar.MONTH);
//                int year = cldr.get(Calendar.YEAR);
//                // date picker dialog
//                picker = new DatePickerDialog(Register.this,
//                        new DatePickerDialog.OnDateSetListener() {
//                            @Override
//                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                                etAge.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
//                            }
//                        }, year, month, day);
//                picker.show();
//            }
//        });
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.bLogin) {
            finish();
            return;
        }

//        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
//        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
//        String dob = etAge.getText().toString().trim();
//        String gender = "";
//        String phone = etPhone.getText().toString().trim();


//        switch (rGender.getCheckedRadioButtonId()) {
//            case R.id.maleRad:
//                gender = "M";
//                break;
//            case R.id.femaleRad:
//                gender = "F";
//                break;
//
//        }

        if (username.length() == 0 || password.length() == 0) {
            Snackbar.make(view, R.string.completeall, Snackbar.LENGTH_LONG).show();
        } else {
            try {

                    AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
                    asyncTaskRunner.execute("name", username, "", password, "2020-4-14", "gender", "phone");

            } catch (IndexOutOfBoundsException e) {
                Snackbar.make(view, R.string.formatemail, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public static class AsyncTaskRunner extends AsyncTask<String, String, String> {
        private String resp = "";

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 7) {
                try {
                    Statement stmt = Login.stmt;
                    person person = new person(0, params[0], params[1], params[2], params[3], params[4], params[5], params[6], null);
                    person.addToDatabase(stmt);
                    resp = Login.context.getString(R.string.RegisteredSuccessfully);
                } catch (SQLException e) { // handle to connect to the database server
                    if (e.getErrorCode() == 1062) {
                        String eMessage = e.getMessage();
//                        String att = eMessage.substring(eMessage.lastIndexOf("key") + 5, eMessage.indexOf("_", eMessage.lastIndexOf("key") + 5));
                        resp = Login.context.getString(R.string.username)+" " + Login.context.getString(R.string.isreserved);
                    } else if (e.getErrorCode() == 0) {
                        resp = Login.context.getString(R.string.failedtoCONNECT);
                    } else {
                        resp = Login.context.getString(R.string.mistakeindata);
                        System.out.println(e.getMessage());
                    }
                    e.printStackTrace();
                }catch (Exception e){
                    resp = Login.context.getString(R.string.failedtoCONNECT);
                }
            } else { // if some input missing
                resp = Login.context.getString(R.string.missingdata);
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Snackbar.make(bRegister, resp, Snackbar.LENGTH_LONG).show();
            }catch (Exception e){
                if (resp.equals(Login.context.getString(R.string.failedtoCONNECT)))
                    Snackbar.make(Login.bRegister, resp, Snackbar.LENGTH_LONG).show();
            }

            if (resp.contains(Login.context.getString(R.string.Successfully))) {

            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }
}