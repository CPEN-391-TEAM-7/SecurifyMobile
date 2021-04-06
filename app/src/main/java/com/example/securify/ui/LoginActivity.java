package com.example.securify.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.securify.R;
import com.example.securify.model.User;
import com.example.securify.ui.volley.VolleyRequest;
import com.example.securify.ui.volley.VolleyResponseListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 100;

    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestId()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut();
                signInButton.setVisibility(View.VISIBLE);
                signOutButton.setVisibility(View.GONE);
            }
        });

        Log.d(TAG, "Login Activity is created.");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personId = acct.getId();
                User.getInstance().setName(personName);
                User.getInstance().setUserID(personId);
                User.getInstance().setProfilePicture(acct.getPhotoUrl() == null ? "": acct.getPhotoUrl().toString());
                User.getInstance().setEmail(acct.getEmail());

                Log.i(TAG, "POST REQUEST starting...");
                Log.d(TAG, personId);
                Log.d(TAG, personName);

                JSONObject postData = new JSONObject();
                try {
                    postData.put("userID", personId);
                    postData.put("name", personName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Send request here.
                VolleyRequest.addRequest(getBaseContext(), VolleyRequest.POST_REGISTER_USER, "", "", postData, new VolleyResponseListener() {
                    @Override
                    public void onError(Object response) {
                        VolleyError error = (VolleyError) response;
                        String statusCode = String.valueOf(error.networkResponse.statusCode);

                        if (statusCode.equals("409")) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                JSONObject data = new JSONObject(responseBody);
                                JSONObject userObject= (JSONObject) data.get("user");
                                Log.i(TAG, "userID: " + userObject.get("userID"));
                                Log.i(TAG, "name: " + userObject.get("name"));
                                signInButton.setVisibility(View.GONE);
                                signOutButton.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "Welcome to Securify", Toast.LENGTH_SHORT).show();
                                Log.i("LOGIN", "Successful");
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            } catch (JSONException | UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e(TAG, response.toString());
                        }
                    }

                    @Override
                    public void onResponse(Object response) {
                        Log.i(TAG, "Response is " + response.toString());
                        try {
                            JSONObject json = new JSONObject(response.toString());
                            Log.i(TAG, "userID: " + json.getString("userID"));
                            Log.i(TAG, "name: " + json.getString("name"));
                            signInButton.setVisibility(View.GONE);
                            signOutButton.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Welcome to Securify", Toast.LENGTH_SHORT).show();
                            Log.i("REGISTER", "Successful");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } catch (JSONException e){
                            Log.e(TAG, "JSON Error");
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
            // updateUI(null);
        }
    }
}