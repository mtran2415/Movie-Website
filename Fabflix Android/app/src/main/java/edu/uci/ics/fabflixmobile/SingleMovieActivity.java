package edu.uci.ics.fabflixmobile;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SingleMovieActivity extends ActionBarActivity{

    private String ip = "10.0.2.2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_movie);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Log.d("id", bundle.getString("id"));
            getMovieInfo(bundle.getString("id"));

        }
    }

    public void getMovieInfo(String id) {

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;


        Log.d("mid", id);

        String url = "https://" + ip + ":8443/Fablix/api/single-movie?id=" + id;

        final JsonArrayRequest jsonRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("response2", response.toString());
                        displayInfo(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                    }
                }
        );

        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonRequest);
        Log.d("after", "after search req");
    }

    public void displayInfo(JSONArray results) {

        for (int i = 0 ; i < results.length(); i++) {
            try {
                JSONObject mov = results.getJSONObject(i);
                ((TextView) findViewById(R.id.title)).setText(mov.getString("movie_title"));
                ((TextView) findViewById(R.id.year)).setText(mov.getString("movie_year"));
                ((TextView) findViewById(R.id.director)).setText(mov.getString("movie_director"));
                ((TextView) findViewById(R.id.stars)).setText(mov.getString("movie_stars"));
                ((TextView) findViewById(R.id.genres)).setText(mov.getString("movie_genres"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
