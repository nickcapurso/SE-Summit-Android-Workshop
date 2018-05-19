package com.summit.summitproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.summit.summitproject.prebuilt.login.LoginManager;

/**
 * The first screen of our app. Takes in a username and password and interacts with the
 * {@link LoginManager} to retrieve user details. Also allows the user to check "Remember Me"
 * to locally store and recall credentials.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Called the first time an Activity is created, but before any UI is shown to the user.
     * Prepares the layout and assigns UI widget variables.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

}
