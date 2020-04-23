package com.summit.summitproject.prebuilt.login;

import android.os.AsyncTask;

import com.summit.summitproject.prebuilt.model.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

import static android.os.SystemClock.sleep;

/**
 * Handles making a single network call to a fake login API to retrieve user details (their
 * name, a single credit card number (the last 4), and a list of transactions).
 * <br>
 * Returns the result of the network call via a {@link LoginListener}.
 * <p>
 * Since it's fake API call, we don't "really" send the username / password and it always
 * returns the same response, but it is "real" in the sense that a real network call is made.
 * <p>
 * Networking is done in a background thread via extending an {@link AsyncTask}:
 * <br> https://developer.android.com/reference/android/os/AsyncTask
 * <br> and is specifically done using the Okhttp library:
 * <br> http://square.github.io/okhttp/
 */
public class LoginManager extends AsyncTask<Void, Void, String> {

    private final String username;

    private final String password;

    private final LoginListener listener;

    /**
     * Takes in the user credentials and the listener to deliver the result to.
     */
    public LoginManager(String username, String password, LoginListener listener) {
        this.username = username;
        this.password = password;
        this.listener = listener;
    }

    /**
     * Runs in a background thread, waits two seconds (to simulate a network delay), and makes
     * a network call to retrieve user details.
     * <p>
     * Uses https://www.mocky.io/ to retrieve a pre-defined JSON response.
     * <p>
     * The data returned from this method is delivered to {@link LoginManager#onPostExecute(String)}.
     */
    @Override
    protected String doInBackground(Void... voids) {
        // Prepare logging for the request / response so you can see them in Logcat
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);

        // Prepare the networking client (used to actually make the connection & network call)
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        // Build a GET request for the URL we want to hit
        Request request = new Request.Builder()
                .get()
                .url("http://www.mocky.io/v2/5b0074643100006f0076df40?mocky-delay=2500ms")
                .build();
        try {
            // Execute the network call and assign the response
            Response response = client.newCall(request).execute();

            // If response body is not empty, return it
            ResponseBody body = response.body();
            return body != null ? body.string() : null;
        } catch (Exception e) {
            // Return null if there were any network problems
            return null;
        }
    }

    /**
     * Receives the raw response body String from the network call (or null if there was a problem).
     * Parses the JSON response for the user data. It looks something like:
     * {
     *    "name":"Nick C.",
     *    "cardLastFour":"7890",
     *    "transactions":
     *    [
     *        {
     *          "merchant":"Starbucks",
     *          "amount":"$1.40"
     *        },
     *        {
     *          "merchant":"Macy's",
     *          "amount":"$35.00"
     *        },
     *
     *        // ...
     *
     *    ]
     * }
     */
    @Override
    protected void onPostExecute(String response) {
        // If there was a problem with the network (response was null), then invoke the error
        // listener
        if (response == null) {
            listener.onLoginError(new Exception("Failed to get a response from the server."));
            return;
        }

        try {
            // Attempt to parse the name & card number (last 4) from the top of the JSON object
            JSONObject rootObject = new JSONObject(response);
            String name = rootObject.getString("name");
            String cardNum = rootObject.getString("cardLastFour");

            // Get the JSON array of transactions
            JSONArray transactionsObject = rootObject.getJSONArray("transactions");

            ArrayList<Transaction> transactions = new ArrayList<>();

            // For each JSON object in the array, parse out and add a new Transaction
            for (int i = 0; i < transactionsObject.length(); i++) {
                JSONObject currentTransaction = transactionsObject.getJSONObject(i);
                String merchant = currentTransaction.getString("merchant");
                String amount = currentTransaction.getString("amount");
                transactions.add(new Transaction(merchant, amount));
            }

            // Invoke the success listener with the user data that was parsed out
            listener.onLoginSuccess(name, cardNum, transactions);
        } catch (JSONException e) {
            // If there was a problem parsing the JSON, then invoke the error listener
            listener.onLoginError(new Exception("Failed to parse response from server."));
        }
    }
}
