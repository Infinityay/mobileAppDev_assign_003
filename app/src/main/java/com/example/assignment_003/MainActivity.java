package com.example.assignment_003;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
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
    private RelativeLayout relativeLayout;
    private Button loadBtn;
    private ImageView errorIcon;

    private TextView progressText;


    private int curPage = 1;
    private int maxPage=2;
    private boolean allDataToastShown = true;
    private boolean loadStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recycler_view);

        initView();


        // add click listener
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressText.setVisibility(View.VISIBLE);
//                loadTextView.setVisibility(View.VISIBLE);
//                searchView.setVisibility(View.VISIBLE);
                // get data from the internet and parse it
                downloadData(curPage,maxPage);
                loadStart=true;
            }
        });

        // adding on scroll change listener method for our nested scroll view
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Check if we have reached the bottom of the RecyclerView
                if (loadStart && !recyclerView.canScrollVertically(1)) {
                    downloadData(curPage, maxPage);
                }
            }
        });

    }



    /**
     * initView
     */
    private void initView(){
        //  initializing our views
        relativeLayout = findViewById(R.id.layout_recycler_view);
        recyclerView = findViewById(R.id.country_recycler_view);
        progressBar = findViewById(R.id.country_progressBar);
        loadBtn = findViewById(R.id.loadBtn);
        errorIcon = findViewById(R.id.errorIcon);
        progressText = findViewById(R.id.progressText);

        // init recyclerView
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        countryAdapter = new CountryAdapter(countries,this);
        // add some decoration
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        // set adapter
        recyclerView.setAdapter(countryAdapter);


    }

    /**
     * Get the country data from Internet when pressing loadBtn
     */
    @SuppressLint("NotifyDataSetChanged")
    private void downloadData(int page, int limit) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        boolean isConnected = capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));

        if (!isConnected) {
            Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            progressText.setVisibility(View.VISIBLE);
            errorIcon.setVisibility(View.VISIBLE);
            return;
        }


        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        if (page > limit) {
            Log.d(TAG, "downloadData: " + page + " " + limit);
            if (allDataToastShown) {
                Toast.makeText(this, "You have got all data...", Toast.LENGTH_SHORT).show();
                allDataToastShown = false;
            }
            // hiding our progress bar
            progressBar.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);
            return;
        }
        Log.d(TAG, "downloadData: " + page + " " + limit);
        String url = "https://studio.mg/api-country/index.php?page=" + page;
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray jsonArray = response.getJSONArray("data");
//                            String limitNum = response.getJSONArray("pagination").getJSONObject(0)
//                                            .optString("total_pages",null);

                            maxPage= (int) response.getJSONObject("pagination").opt("total_pages");


                            // passing data from our json array in our array list.
                            countries = parseJSONArray(jsonArray);

                            // update adapter
                            countryAdapter.addCountriesData(countries);
                            countryAdapter.notifyDataSetChanged();

                            // Remove the spinner
                            progressBar.setVisibility(View.GONE);
                            progressText.setVisibility(View.GONE);
                            errorIcon.setVisibility(View.GONE);
                            curPage++;


                        } catch (JSONException e) {
                            throw new RuntimeException(e);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error when downloading data",
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "Error when downloading data:" + error);
                        errorIcon.setVisibility(View.VISIBLE);
                        // Remove the spinner
                        progressBar.setVisibility(View.GONE);
                        progressText.setVisibility(View.GONE);

                    }
                });

        // add the request to queue
        requestQueue.add(jsonObjectRequest);


    }


    /**
     * Parse JSONArray and add current country into countries
     */
    public ArrayList<Country> parseJSONArray(JSONArray jsonArray) {
        ArrayList<Country> countryArrayList = new ArrayList<>();
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
                tempCountry = tempCountry.replaceAll("['-]", "_");
                tempCountry = tempCountry.replaceAll(" ", "_");
                // set flag by flagId
                flag = R.drawable.class.getField("flag_" + tempCountry);
                flagId = flag.getInt(null);
                Country temp = new Country(country, capital, flagId);

                // add current country into countries
                countryArrayList.add(temp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return countryArrayList;
    }


}