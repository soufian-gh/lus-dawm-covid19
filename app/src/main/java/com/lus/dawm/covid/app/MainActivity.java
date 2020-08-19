package com.lus.dawm.covid.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.leo.simplearcloader.SimpleArcLoader;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // String currentCountry = "morocco";
    String currentLocationAPI ;
    String country8;


    TextView
            tvCountryTodayCases,tvCountryTodayRecoverds,tvCountryTodayDeaths ,textInf,
            tvCases,tvRecovered,tvCritical,tvActive,tvTodayCases,tvTotalDeaths,tvTodayDeaths,tvAffectedCountries,tvMild;
    SimpleArcLoader simpleArcLoader;
    ScrollView scrollView;
    PieChart pieChart;


    static String tag = "MainActivity";
    ////////////////////////////////////////////////////////////////////////////////////////////
    //getting crrent location to show data based on it
    public String pays (){

        try {

            double longitude = 0.0,latitude=0.0;
            Location gps_lock = null , network_lock = null , finla_lock = null;


            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            if
            (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE ) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"access not garanted" , Toast.LENGTH_SHORT).show();
            }

            try {
                gps_lock = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                network_lock = locationManager .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            if (gps_lock!=null){
                finla_lock = gps_lock;
                latitude = finla_lock.getLatitude();
                longitude = finla_lock.getLongitude();
            }
            else if(network_lock!=null){
                finla_lock = network_lock;
                latitude = finla_lock.getLatitude();
                longitude = finla_lock.getLongitude();
            }
            else{
                longitude=0.0;
                latitude=0.0;
            }


            try {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address>addresses = geocoder.getFromLocation(latitude,longitude,1);
                if(addresses != null && addresses.size()>0){
                    country8 = addresses.get(0).getCountryName();

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace ();
        }
        // Toast.makeText(this,"You are in "+country8 , Toast.LENGTH_SHORT).show();
        return country8;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //checking gps state

        tvCases = findViewById(R.id.tvCases);
        tvRecovered = findViewById(R.id.tvRecovered);
        tvCritical = findViewById(R.id.tvCritical);
        tvActive = findViewById(R.id.tvActive);
        // tvMild = findViewById(R.id.tvMild);
        tvTodayCases = findViewById(R.id.tvTodayCases);
        tvTotalDeaths = findViewById(R.id.tvTotalDeaths);
        tvTodayDeaths = findViewById(R.id.tvTodayDeaths);
        tvAffectedCountries = findViewById(R.id.tvAffectedCountries);

        simpleArcLoader = findViewById(R.id.loader);
        scrollView = findViewById(R.id.scrollStats);
        pieChart = findViewById(R.id.piechart);


        fetchData();

    }

    private void fetchData() {
        String url  = "https://corona.lmao.ninja/v2/all/";

        simpleArcLoader.start();

        ///////////////////////////////////////////////////////////////////////////////////////
        textInf = (TextView)findViewById(R.id.countryInf);
        currentLocationAPI = "https://corona.lmao.ninja/v2/countries/"+pays ().toString ();

        StringRequest rq1 = new StringRequest(Request.Method.GET, currentLocationAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject2 = new JSONObject(response.toString());

                            String country  = jsonObject2.getString("country");
                            float cases     = Float.parseFloat ( jsonObject2.getString("cases"));
                            float recovered = Float.parseFloat ( jsonObject2.getString("recovered"));
                            float active    = Float.parseFloat ( jsonObject2.getString("active"));
                            float deaths    = Float.parseFloat ( jsonObject2.getString("deaths"));

                            //  tvCountryTodayCases = (TextView) findViewById(R.id.countrytodaycases);
                            //  tvCountryTodayRecoverds = (TextView) findViewById(R.id.countrytodayrecovered);
                            //  tvCountryTodayDeaths = (TextView) findViewById(R.id.countrytodaydeaths);

                            int CountryTodayCases          = Integer.parseInt (jsonObject2.getString ("todayCases"));
                            int CountryTodayDeaths         = Integer.parseInt (jsonObject2.getString ("todayDeaths"));






                            BarChart mBarChart = (BarChart) findViewById(R.id.barchart);

                            mBarChart.addBar(new BarModel("cases",cases, 0xFF123354));
                            mBarChart.addBar(new BarModel("recovered",recovered,0xFF123354));
                            mBarChart.addBar(new BarModel("active",active, 0xFF563456));
                            mBarChart.addBar(new BarModel("deaths",deaths, 0xFF873F56));

                            mBarChart.startAnimation();

                            textInf.setText("Last numbers about your country : "+country+"  ->  "+CountryTodayCases);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////

        StringRequest rq2 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());

                            tvCases.setText(jsonObject.getString("cases"));
                            tvRecovered.setText(jsonObject.getString("recovered"));
                            tvCritical.setText(jsonObject.getString("critical"));
                            tvActive.setText(jsonObject.getString("active"));
                            // tvMild.setText(jsonObject.getString());
                            tvTodayCases.setText(jsonObject.getString("todayCases"));
                            tvTotalDeaths.setText(jsonObject.getString("deaths"));
                            tvTodayDeaths.setText(jsonObject.getString("todayDeaths"));
                            tvAffectedCountries.setText(jsonObject.getString("affectedCountries"));

                            //totalcases
                            int tot = Integer.parseInt(tvCases.getText().toString());
                            //dishcharged cases
                            int deaths =    Integer.parseInt(tvTotalDeaths.getText().toString());
                            int recovered = Integer.parseInt(tvRecovered.getText().toString());
                            //active cases
                            int critical =  Integer.parseInt(tvCritical.getText().toString());
                            int mild =      tot-critical-deaths-recovered;

                            TextView tv1mild = (TextView)findViewById(R.id.tv1mild);
                            TextView tv1death = (TextView)findViewById(R.id.tv1deaths);
                            TextView tv1critical = (TextView)findViewById(R.id.tv1critical);
                            TextView tv1recovered = (TextView)findViewById(R.id.tv1recovered);

                            tv1mild.setText(Integer.toString(mild)+"  Mild");
                            tv1recovered.setText(Integer.toString(recovered)+"  Recovered");
                            tv1critical.setText(Integer.toString(critical)+"  Critical");
                            tv1death.setText(Integer.toString(deaths)+"  Deaths");

                            pieChart.addPieSlice(new PieModel("Recovered",recovered, Color.parseColor ("#37D53D")));
                            pieChart.addPieSlice(new PieModel("Mild",mild, Color.parseColor ("#00C6B4")));
                            pieChart.addPieSlice(new PieModel("Critical",critical,Color.parseColor ("#FF9800")));
                            pieChart.addPieSlice(new PieModel("Deaths",deaths, Color.parseColor ("#DA0808")));



                            pieChart.startAnimation();

                            simpleArcLoader.stop();
                            simpleArcLoader.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);



                        } catch (JSONException e) {
                            e.printStackTrace();
                            simpleArcLoader.stop();
                            simpleArcLoader.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                simpleArcLoader.stop();
                simpleArcLoader.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(rq2);
        requestQueue.add(rq1);


    }

    public void goTrackCountries(View view) {

        startActivity(new Intent(getApplicationContext(),AffectedCountries.class));

    }



    public void goMorelayout(View view) {

        startActivity(new Intent(getApplicationContext(),MoreActivity.class));

    }


}
