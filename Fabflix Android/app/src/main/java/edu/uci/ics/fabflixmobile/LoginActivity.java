package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends ActionBarActivity{

    private String ip = "10.0.2.2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {

        // Post request form data
        final Map<String, String> params = new HashMap<String, String>();

        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();

        params.put("username", username);
        params.put("password", password);

        Log.d("user", username);
        Log.d("pass", password);


        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        // 10.0.2.2 is the host machine when running the android emulator
        final StringRequest loginRequest = new StringRequest(Request.Method.POST, "https://" + ip + ":8443/Fablix/api/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response", response);

                        if(response.contains("fail")) {
                            Log.d("security.error", "Incorrect username/password combination");
                            ((TextView) findViewById(R.id.errorResponse)).setText("Incorrect username/password combination");
                        }
                        else
                            goToSearch();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        SafetyNet.getClient(this).verifyWithRecaptcha("6LcSHlsUAAAAAB2UuGMn1ZjkEMBsRkyjr5eAlqml")
                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        if (!response.getTokenResult().isEmpty()) {
                            // Add the request to the RequestQueue.
                            params.put("g-recaptcha-response", response.getTokenResult());
                            queue.add(loginRequest);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            Log.d("Login", "Error message: " +
                                    CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                        } else {
                            Log.d("Login", "Unknown type of error: " + e.getMessage());
                        }
                    }
                });

    }

    public void goToSearch() {
        Intent goToIntent = new Intent(this, SearchActivity.class);
        startActivity(goToIntent);
    }
}
