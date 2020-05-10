/*
 Privacy Friendly Finance Manager is licensed under the GPLv3.
 Copyright (C) 2019 Leonard Otto, Felix Hofmann

 This program is free software: you can redistribute it and/or modify it under the terms of the GNU
 General Public License as published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with this program.
 If not, see http://www.gnu.org/licenses/.

 Additionally icons from Google Design Material Icons are used that are licensed under Apache
 License Version 2.0.
 */

package project.mybudget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import project.mybudget.Authentication.Login;

public class StartupActivity extends AppCompatActivity {
    public static Context context;
    @Override
    protected void onStart() {
        super.onStart();
        context = this;
        SharedPreferences sharedPreferences = getSharedPreferences(Login.SHARED_PREF_NAME , Login.MODE_PRIVATE);
        String languageToLoad = sharedPreferences.getString("lang", context.getResources().getConfiguration().locale.getLanguage());;
        Locale locale = new Locale(languageToLoad);//Set Selected Locale
        Locale.setDefault(locale);//set new locale as default
        Configuration config = new Configuration();//get Configuration
        config.locale = locale;//set config locale as selected locale
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        new Thread(new Runnable() {
            public void run() {
                Intent i = new Intent(StartupActivity.this, Login.class);

                try {
                    // Sleep for 200 milliseconds.
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    Log.d(String.valueOf(this), e.getMessage());
                }
                startActivity(i);
                finish();
            }

        }).start();
    }
}