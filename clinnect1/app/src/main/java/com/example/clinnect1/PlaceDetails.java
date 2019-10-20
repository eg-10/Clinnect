package com.example.clinnect1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.clinnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PlaceDetails extends AppCompatActivity {

    private static final String TAG = "PlaceDetails";
    private String customerid = "";
    private static final String apiKey = "AIzaSyBtiMxQ4SyLkAaHLyXofMt3CkQb8FiW_tk";
    private RatingBar rbar;
    private Button bookmarkBut;
    FirebaseAuth mAuth;
    DatabaseReference curruser, currplace, bookmark,bm;
    public static JSONObject results;
    int flag;
    float rate;
    String name,addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        rbar = findViewById(R.id.ratingBar);
        bookmarkBut = findViewById(R.id.bookmark);
        final TextView title = findViewById(R.id.name);
        final TextView address = findViewById(R.id.address);
        final TextView phone = findViewById(R.id.phone);
        final TextView website = findViewById(R.id.website);
        final TextView rating = findViewById(R.id.rating);
        customerid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Intent intent = getIntent();
        final String placeID = intent.getStringExtra("placeID");
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
                            name = results.getString("name");
                            addr = results.getString("formatted_address");
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
        currplace = FirebaseDatabase.getInstance().getReference().child("places").child(placeID).child("rating");
        curruser = FirebaseDatabase.getInstance().getReference().child("users").child(customerid).child("places").child("ratings").child(placeID);
        currplace.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    float sum = 0;
                    int count = 0;
                    float avg;
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        sum = sum + Float.valueOf(child.getValue().toString());
                        count++;
                    }
                    avg = sum/count;
                    if (avg!=0) {
                        rbar.setRating(avg);
                        rate = avg;
                    }

                }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        flag = 1;
        bookmark = FirebaseDatabase.getInstance().getReference().child("users").child(customerid).child("places").child("bookmarks");
        bookmark.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(placeID)){
                    bookmarkBut.setText("Unbookmark");
                    flag = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        rbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                curruser.setValue(Float.toString(v));
                currplace.child(customerid).setValue(Float.toString(v));
                rbar.setRating(v);
            }
        });
        bookmarkBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Map<String, Object> map = new HashMap<>();
                map.put("placeID", placeID);
                bookmark.updateChildren(map);*/
                if (flag ==1 ) {   //button switch
                    bm = bookmark.child(placeID);
                    bm.child("name").setValue(name);
                    bm.child("address").setValue(addr);
                    bm.child("placeid").setValue(placeID);
                    bm.child("rating").setValue((rate));
                    bookmarkBut.setText("Unbookmark");
                    flag = 0;
                }
                else{
                    bookmark.child(placeID).removeValue();
                    bookmarkBut.setText("Bookmark");
                    flag=1;
                }
            }
        });
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
