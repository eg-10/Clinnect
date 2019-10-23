package com.example.clinnect1;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    RecyclerView recyclerView;

    public static JSONArray results;

    private float DEFAULT_RADIUS = 3000;

    private static final String TAG = "MapsActivity";

    private static final String apiKey = "AIzaSyBtiMxQ4SyLkAaHLyXofMt3CkQb8FiW_tk";

//    public RecyclerView results_rv = findViewById(R.id.results_rv);

    public static HashMap<String,String> types = new HashMap<>();
    public static HashMap<String,String> keywords = new HashMap<>();
    public static HashSet <String> selected_types= new HashSet<>();
    final boolean[] checked = new boolean[6];
    PlaceAdapter adapter;
    static List<com.example.clinnect1.Place> placeList;
    com.example.clinnect1.Place places;
    private LatLng location;
    private String loc_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_maps);


        types.put("Dentist","dentist");
        types.put("Clinic","doctor");
        types.put("Hospital", "hospital");
        types.put("Pharmacy", "pharmacy");
        types.put("Physiotherapist", "physiotherapist");
        types.put("Veterinary care", "veterinary_care");

        keywords.put("Dentist","dentist");
        keywords.put("Clinic","clinic");
        keywords.put("Hospital", "hospital");
        keywords.put("Pharmacy", "pharmacy");
        keywords.put("Physiotherapist", "physiotherapist");
        keywords.put("Veterinary care", "vet");

        Intent intent = getIntent();

        selected_types = (HashSet<String>) intent.getSerializableExtra("selected_types");
        if (selected_types.isEmpty())
            selected_types.add("Clinic");

        int i = 0;
        for(boolean isChecked : intent.getBooleanArrayExtra("checked")){
            checked[i++] = isChecked;
        }

        Log.i(TAG,"Got selections="+selected_types);

        location = new LatLng(intent.getDoubleExtra("lat", 0.0), intent.getDoubleExtra("lng", 0.0));
        loc_name = intent.getStringExtra("name");
        Log.i(TAG, "name,lat,lng:::::::::" + location.latitude + location.longitude + loc_name);
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
        FloatingActionButton location_fab = findViewById(R.id.location_fab);
        location_fab.setImageResource(R.drawable.ic_my_location_white_24dp);
        location_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(location)      // Sets the center of the map to location
                        .zoom(14.0f)                   // Sets the zoom
                        .build();               //Builds the new camera position
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

        FloatingActionButton filter_fab = findViewById(R.id.filter_fab);
        filter_fab.setImageResource(R.drawable.ic_filter_list_white_24dp);
        filter_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final HashSet<String> currSelection = new HashSet<>();

                final String[] options = new String[types.keySet().toArray().length];



                for(int i = 0; i < types.keySet().toArray().length; i++){
                    options[i] = (String) types.keySet().toArray()[i];
                    checked[i] = false;
                }
                final List optionsList = Arrays.asList(options);

                for(String selection: selected_types){
                    currSelection.add(selection);
                    checked[optionsList.indexOf(selection)] = true;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle(R.string.title_dialog)
                        .setIcon(R.drawable.ic_filter_list_black_24dp)
                        .setMultiChoiceItems(options, checked, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean isChecked) {

                                checked[i] = isChecked;
                                Log.i(TAG, options[i] + " set to " + checked[i]);

                                if(isChecked){
                                    currSelection.add(options[i]);
                                    Log.i(TAG,"Currently Selected "+currSelection);
                                }
                                else if(currSelection.contains(options[i])){
                                    currSelection.remove(options[i]);
                                    Log.i(TAG,"Currently Selected "+currSelection);
                                }

                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(currSelection.isEmpty()) {
                                    selected_types.clear();
                                    selected_types.add("Clinic");
                                    Log.i(TAG, "selection empty, added default");
                                }
                                else
                                    selected_types = (HashSet<String>) currSelection.clone();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                currSelection.clear();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
                Log.i(TAG, "Final selection ="+selected_types);
            }
        });

        setPlaceMarker(location, loc_name);

        Iterator<String> it = selected_types.iterator();

        for(String selection: selected_types){
            results = new JSONArray();

            Log.i(TAG, "type="+types.get(selection)+"\tkeyword="+keywords.get(selection));

            setNearbyPlacesArray(location.latitude, location.longitude, types.get(selection), keywords.get(selection));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
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

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.RATING));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                location = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                loc_name = place.getName();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Place Name & ID \n" + place.getName() + "\n" + place.getId(),
                        Toast.LENGTH_SHORT);
                toast.show();
                mMap.clear();
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                Log.e(TAG, "Lat lng:" + place.getLatLng().latitude);
                setPlaceMarker(place.getLatLng(), place.getName());

                Iterator<String> it = selected_types.iterator();

                for(String selection: selected_types){
                    results = new JSONArray();

                    Log.i(TAG, "type="+types.get(selection)+"\tkeyword="+keywords.get(selection));

                    setNearbyPlacesArray(location.latitude, location.longitude, types.get(selection), keywords.get(selection));
                }

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


        placeList = new ArrayList<>();
        placeList.clear();
        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaceAdapter(this,placeList);

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
                            int n = (results.length() <= 15 ) ? results.length() : 10;
                            for (int i=0; i< n; i++){

                                try {

                                    double lat = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                    double lng = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                    String placeID = results.getJSONObject(i).getString("place_id");
                                    LatLng latLng = new LatLng(lat,lng);
                                    String name = results.getJSONObject(i).getString("name");
                                    String address = results.getJSONObject(i).getString("vicinity");
                                    double rating = results.getJSONObject(i).getDouble("rating");
                                    places = new com.example.clinnect1.Place(name,address,rating,placeID);
                                    placeList.add(places);
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
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);

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