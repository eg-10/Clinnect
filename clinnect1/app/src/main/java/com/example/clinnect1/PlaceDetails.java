package com.example.clinnect1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.clinnect.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetails extends AppCompatActivity {

    private static final String TAG = "PlaceDetails";

    private static final String apiKey = "AIzaSyBtiMxQ4SyLkAaHLyXofMt3CkQb8FiW_tk";



    public static JSONObject results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        final TextView title = findViewById(R.id.name);
        final TextView address = findViewById(R.id.address);
        final TextView phone = findViewById(R.id.phone);
        final TextView website = findViewById(R.id.website);
        final TextView rating = findViewById(R.id.rating);

        Intent intent = getIntent();
        String placeID = intent.getStringExtra("placeID");
        Log.i(TAG, "place id: " + placeID);
        String url = "https://maps.googleapis.com/maps/api/place/details/json?" +
                "place_id=" + placeID +
                "&fields=name,formatted_address,opening_hours,rating,formatted_phone_number,website" +
                "&key=" + apiKey;
        Log.i(TAG, "URL: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i(TAG, response.getString("status"));
                            results = response.getJSONObject("result");
                            Log.i(TAG, "Length:" + results.length());
                            title.setText(results.getString("name"));
                            address.setText("Address: \n" + results.getString("formatted_address"));
                            phone.setText("Phone: " + results.getString("formatted_phone_number"));
                            rating.setText("Ratings: " + results.getString("rating"));
                            website.setText("Website: " + results.getString("website"));
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Retrieved Place Details results!",
                                    Toast.LENGTH_SHORT);
                            toast.show();

                        } catch (JSONException e){

                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Error in retrieving Place Details!outer",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Error in retrieving Place Details!",
                                Toast.LENGTH_SHORT);
                        toast.show();

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

//    private void setNearbyPlacesArray(double latitude, double longitude, String type, String keyword){
//
//
//        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
//                "location=" + latitude + "," + longitude +
//                "&type=" + type +
//                "&keyword=" + keyword +
//                "&rankby=distance" +
//                "&key=" + apiKey;
//
//        Log.i(TAG, url);
//
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            Log.i(TAG, response.getString("status"));
//                            results = response.getJSONArray("results");
//                            Log.i(TAG, "LLLEEngth:" + results.length());
//                            Toast toast = Toast.makeText(getApplicationContext(),
//                                    "Retrieved results!",
//                                    Toast.LENGTH_SHORT);
//                            toast.show();
//                            for (int i=0; i<results.length(); i++){
//
//                                try {
//
//                                    double lat = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
//                                    double lng = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
//                                    String placeID = results.getJSONObject(i).getString("place_id");
//                                    LatLng latLng = new LatLng(lat,lng);
//                                    String name = results.getJSONObject(i).getString("name");
//                                    setMarker(latLng, name, placeID);
//                                } catch (JSONException e){
//
//                                    toast = Toast.makeText(getApplicationContext(),
//                                            "Error in retrieving nearby hospitals!inner",
//                                            Toast.LENGTH_SHORT);
//                                    toast.show();
//                                }
//                            }
//                        } catch (JSONException e){
//
//                            Toast toast = Toast.makeText(getApplicationContext(),
//                                    "Error in retrieving nearby hospitals!outer",
//                                    Toast.LENGTH_SHORT);
//                            toast.show();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast toast = Toast.makeText(getApplicationContext(),
//                                "Error in retrieving nearby hospitals!",
//                                Toast.LENGTH_SHORT);
//                        toast.show();
//
//                    }
//                });
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(jsonObjectRequest);
//    }
}
