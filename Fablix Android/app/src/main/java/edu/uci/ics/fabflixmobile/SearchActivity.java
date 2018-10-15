package edu.uci.ics.fabflixmobile;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends ActionBarActivity{

    private int offset = 0;
    private String ip = "10.0.2.2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ((Button) findViewById(R.id.prev)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.next)).setVisibility(View.GONE);

    }

    public void submitSearch(View view) {


        offset = 0;

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        String title = ((EditText) findViewById(R.id.title_search)).getText().toString();
        ((TextView) findViewById(R.id.query)).setText(title);
        ((TextView) findViewById(R.id.query)).setVisibility(View.GONE);

        Log.d("title", title);
        Log.d("query", ((TextView) findViewById(R.id.query)).getText().toString());

        String off = Integer.toString(offset);
        String url = "https://" + ip + ":8443/Fablix/search?title=" + title + "&year=&director=&star=&limit=10&offset=" + off + "&header=title&sort=asc";

        final JsonArrayRequest jsonRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("response2", response.toString());
                        try {



                            displayMovies(response);
                        }catch (JSONException e) {
                            System.out.println(e.toString());
                        }

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

    public void displayMovies(JSONArray results) throws JSONException {

        ((Button) findViewById(R.id.prev)).setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.next)).setVisibility(View.VISIBLE);

        if(offset <= 0)
            ((Button) findViewById(R.id.prev)).setVisibility(View.GONE);

        if(results.length() < 10)
            ((Button) findViewById(R.id.next)).setVisibility(View.GONE);


        final ArrayList<Movie> movies = new ArrayList<>();

        for (int i = 0 ; i < results.length(); i++) {
            JSONObject m = results.getJSONObject(i);
            movies.add(new Movie(m.getString("title"), m.getString("year"),
                    m.getString("director"), m.getString("genres"),
                    m.getString("stars"), m.getString("id")));
        }



        ResultsAdaptor adapter = new ResultsAdaptor(movies, this);

        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);


    }

    public void onClickNext(View view) {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        String title = ((TextView) findViewById(R.id.query)).getText().toString();

        Log.d("title", title);

        offset += 10;
        String off = Integer.toString(offset);
        String url = "https://" + ip + ":8443/Fablix/search?title=" + title + "&year=&director=&star=&limit=10&offset=" + off + "&header=title&sort=asc";

        final JsonArrayRequest jsonRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("responseNext", response.toString());
                        try {



                            displayMovies(response);
                        }catch (JSONException e) {
                            System.out.println(e.toString());
                        }

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

    public void onClickPrev(View view) {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        String title = ((TextView) findViewById(R.id.query)).getText().toString();

        Log.d("title", title);


        offset -= 10;
        String off = Integer.toString(offset);
        String url = "https://" + ip + ":8443/Fablix/search?title=" + title + "&year=&director=&star=&limit=10&offset=" + off + "&header=title&sort=asc";

        final JsonArrayRequest jsonRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("responsePrev", response.toString());
                        try {
                            displayMovies(response);
                        } catch (JSONException e) {
                            System.out.println(e.toString());
                        }

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

}
