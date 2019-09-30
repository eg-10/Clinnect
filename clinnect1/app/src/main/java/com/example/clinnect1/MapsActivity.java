package com.example.clinnect1;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.clinnect.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    public static JSONArray results;

    private float DEFAULT_RADIUS = 3000;

    private static final String TAG = "MapsActivity";

    private static final String apiKey = "AIzaSyBtiMxQ4SyLkAaHLyXofMt3CkQb8FiW_tk";

    private LatLng start;
    private String start_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_maps);
        Intent intent = getIntent();
        start = new LatLng(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("lng", 0.0));
        start_name = intent.getStringExtra("name");
        Log.i(TAG, "name,lat,lng:::::::::" + start.latitude + start.longitude + start_name);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        mMap.setOnInfoWindowClickListener(this);
        mMap.clear();
        loadPlacesAutocomplete();
        // Add a marker in Mumbai and move the camera
//        LatLng mumbai = new LatLng(19.0760, 72.8777);
//        mMap.addMarker(new MarkerOptions()
//                .position(mumbai)
//                .title("Default Marker")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
//        Circle circle = mMap.addCircle(new CircleOptions()
//                .center(mumbai)
//                .radius(2000)
//                .strokeWidth(0)
//                .strokeColor(Color.parseColor("#2271cce7"))
//                .fillColor(Color.parseColor("#2271cce7")));
        setPlaceMarker(start, start_name);
        results = new JSONArray();
        setNearbyPlacesArray(start.latitude, start.longitude, "hospital", "hospital");
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Info window clicked!");
        String placeID = (String) marker.getTag();
        if (placeID != null) {
            Log.i(TAG, "Place_id = " + marker.getTag());
            Intent intent = new Intent(this, PlaceDetails.class);
            intent.putExtra("placeID", placeID);
            startActivity(intent);
        }
        else {
            Log.i(TAG, "No place id!");
        }
    }

    private void loadPlacesAutocomplete(){

        /*
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

// Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Place Name & ID \n" + place.getName() + "\n" + place.getId(),
                        Toast.LENGTH_SHORT);
                toast.show();
                mMap.clear();
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                Log.e(TAG, "Lat lng:" + place.getLatLng().latitude);
                setPlaceMarker(place.getLatLng(), place.getName());
                results = new JSONArray();
                setNearbyPlacesArray(place.getLatLng().latitude, place.getLatLng().longitude, "hospital", "hospital");
//                setNearbyPlacesArray(place.getLatLng().latitude, place.getLatLng().longitude, "doctor", "clinic");
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void setNearbyPlacesArray(double latitude, double longitude, String type, String keyword){


        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + latitude + "," + longitude +
                "&type=" + type +
                "&keyword=" + keyword +
                "&rankby=distance" +
                "&key=" + apiKey;

        Log.i(TAG, url);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i(TAG, response.getString("status"));
                            results = response.getJSONArray("results");
                            Log.i(TAG, "LLLEEngth:" + results.length());
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Retrieved results!",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            for (int i=0; i<results.length(); i++){

                                try {

                                    double lat = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                    double lng = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                    String placeID = results.getJSONObject(i).getString("place_id");
                                    LatLng latLng = new LatLng(lat,lng);
                                    String name = results.getJSONObject(i).getString("name");
                                    setMarker(latLng, name, placeID);
                                } catch (JSONException e){

                                    toast = Toast.makeText(getApplicationContext(),
                                            "Error in retrieving nearby hospitals!inner",
                                            Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        } catch (JSONException e){

                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Error in retrieving nearby hospitals!outer",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Error in retrieving nearby hospitals!",
                                Toast.LENGTH_SHORT);
                        toast.show();

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void setPlaceMarker(LatLng location, String name){

        Log.e(TAG, "Lat lng:" + location.latitude + location.longitude);
        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.addCircle(new CircleOptions()
                .center(location)
                .radius(DEFAULT_RADIUS)
                .strokeWidth(0)
                .strokeColor(Color.parseColor("#2271cce7"))
                .fillColor(Color.parseColor("#2271cce7")));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
    }

    private void setMarker(LatLng location, String name, String placeID){

       Marker marker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker()));
       marker.setTag(placeID);
    }


}
