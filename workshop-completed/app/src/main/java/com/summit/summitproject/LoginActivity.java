package com.summit.summitproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.summit.summitproject.prebuilt.login.LoginListener;
import com.summit.summitproject.prebuilt.login.LoginManager;
import com.summit.summitproject.prebuilt.model.Transaction;

import java.util.ArrayList;

/**
 * The first screen of our app. Takes in a username and password and interacts with the
 * {@link LoginManager} to retrieve user details. Also allows the user to check "Remember Me"
 * to locally store and recall credentials.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * The key under which the <b>username</b> will be stored in {@link SharedPreferences}.
     */
    private static final String PREF_USERNAME = "USERNAME";

    /**
     * The key under which the <b>password</b> will be stored in {@link SharedPreferences}.
     */
    private static final String PREF_PASSWORD = "PASSWORD";

    /**
     * Used to persist user credentials if "Remember Me" is checked.
     */
    private SharedPreferences sharedPreferences;

    // UI Widgets

    private EditText username;

    private EditText password;

    private Button signIn;

    private CheckBox rememberMe;

    private ProgressBar progress;

    /**
     * Called the first time an Activity is created, but before any UI is shown to the user.
     * Prepares the layout and assigns UI widget variables.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signIn = findViewById(R.id.sign_in);
        rememberMe = findViewById(R.id.remember_me);
        progress = findViewById(R.id.progress);

        setupWidgets();

        // If user credentials were previously stored, pre-fill the username / password
        sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        rememberMe.setChecked(sharedPreferences.contains(PREF_USERNAME));
        username.setText(sharedPreferences.getString(PREF_USERNAME, ""));
        password.setText(sharedPreferences.getString(PREF_PASSWORD, ""));
    }

    /**
     * Set up listeners for various user interactions.
     */
    private void setupWidgets() {
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);

        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputtedUsername = username.getText().toString();
                String inputtedPassword = password.getText().toString();

                // Don't allow user input while logging in & show the progress bar
                setAllEnabled(false);
                progress.setVisibility(View.VISIBLE);

                // Instantiate the login manager, passing the username, password, and result listener
                LoginManager loginManager = new LoginManager(inputtedUsername, inputtedPassword, loginListener);

                // Kick off the login network call
                loginManager.execute();
            }
        });

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If "Remember Me" was previously checked and the user no longer wants their
                // credentials remembered, clear the credentials from storage.
                if (!isChecked) {
                    clearUserCredentials();
                }
            }
        });
    }

    /**
     * Save the username nad password to local storage.
     */
    private void saveUserCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_USERNAME, username.getText().toString());
        editor.putString(PREF_PASSWORD, password.getText().toString());
        editor.apply();
    }

    /**
     * Clear the username and password from local storage.
     */
    private void clearUserCredentials() {
        username.setText("");
        password.setText("");
        sharedPreferences.edit().clear().apply();
    }

    /**
     * Used with {@link android.widget.TextView#addTextChangedListener(TextWatcher)} to get callbacks
     * when the user interacts with the text input widgets.
     */
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            // Enable the "Sign In" button if something has been inputted in both the username
            // and password.
            String usernameText = username.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            signIn.setEnabled(usernameText.length() > 0 && passwordText.length() > 0);
        }
    };

    private LoginListener loginListener = new LoginListener() {
        @Override
        public void onLoginSuccess(String name, String cardNum, ArrayList<Transaction> transactions) {
            // Allow user input (e.g. if the user returns to this screen) and
            // hide the progress bar again
            setAllEnabled(true);
            progress.setVisibility(View.INVISIBLE);

            // Store user credentials in private storage if "Remember Me" was checked
            if (rememberMe.isChecked()) {
                saveUserCredentials();
            }

            // Start the SummaryActivity and also pass the user's name,
            // card number, and list of transactions in the launch intent.
            Intent intent = new Intent(LoginActivity.this, SummaryActivity.class);
            intent.putExtra(SummaryActivity.KEY_NAME, name);
            intent.putExtra(SummaryActivity.KEY_CARD_NUM, cardNum);
            intent.putExtra(SummaryActivity.KEY_TRANSACTIONS, transactions);
            startActivity(intent);

        }

        @Override
        public void onLoginError(Exception exception) {
            // Allow user input (eg. if the user returns to this screen) and
            // hide the progress bar again
            setAllEnabled(true);
            progress.setVisibility(View.INVISIBLE);

            Toast.makeText(LoginActivity.this, "Failed to login", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * Enables or disables user interactions with the text input widgets & sign in button.
     */
    private void setAllEnabled(boolean enabled) {
        username.setEnabled(enabled);
        password.setEnabled(enabled);
        signIn.setEnabled(enabled);
    }
}

