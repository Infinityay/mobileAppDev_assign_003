package com.example.assignment_003;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Country> countries = new ArrayList<>();
    private RecyclerView recyclerView;
    private CountryAdapter countryAdapter;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;

    private int curPage = 1;
    private final int maxPage = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recycler_view);

        //  initializing our views
        nestedScrollView = findViewById(R.id.country_NestedScrollView);
        recyclerView = findViewById(R.id.country_recycler_view);
        progressBar = findViewById(R.id.country_progressBar);

        // get data from the internet and parse it
        downloadData(curPage,maxPage);

        // adding on scroll change listener method for our nested scroll view
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                // if the scroll distance equals to top - bottom
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    curPage++;
                    progressBar.setVisibility(View.VISIBLE);
                    downloadData(curPage,maxPage);
                }
            }
        });


    }

    /**
     * Get the country data from Internet when pressing loadBtn
     * */
    private void downloadData(int page, int limit){
        if (page>limit){
            Log.d(TAG, "downloadData: "+ page + " " + limit);
            Toast.makeText(this, "You have got all data...", Toast.LENGTH_SHORT).show();
            // hiding our progress bar
            progressBar.setVisibility(View.GONE);
            return;
        }
        Log.d(TAG, "downloadData: "+ page + " " + limit);
        String url = "https://studio.mg/api-country/index.php?page="+page;
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray jsonArray = response.getJSONArray("data");

                            // passing data from our json array in our array list.
                            parseJSONArray(jsonArray);
                
                            // passing array list to our adapter class
                            countryAdapter = new CountryAdapter(countries,MainActivity.this);

                            // setting layout manager to our recycler view
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                            // setting adapter to our recycler view
                            recyclerView.setAdapter(countryAdapter);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Error when downloading data",
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG","Error when downloading data:"+error);
                    }
                });

        // add the request to queue
        requestQueue.add(jsonObjectRequest);


    }


    /**
     *  Parse JSONArray and add current country into countries
     */
    public void parseJSONArray(JSONArray jsonArray) {
        String country;
        String capital;
        Field flag;
        int flagId;

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // set country and capital
                country = jsonObject.optString("country", null);
                capital = jsonObject.optString("capital", null);


                // replace capital to find it in res 'drawable'
                String tempCountry;
                tempCountry = country.replaceAll("\\([^)]*\\)|\\[.*\\]", "");
                tempCountry = tempCountry.toLowerCase().trim();
                tempCountry = tempCountry.replaceAll("['-]","_");
                tempCountry = tempCountry.replaceAll(" ","_");
                // set flag by flagId
                flag = R.drawable.class.getField("flag_"+tempCountry);
                flagId = flag.getInt(null);
                Country temp = new Country(country,capital,flagId);

                // add current country into countries
                countries.add(temp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}