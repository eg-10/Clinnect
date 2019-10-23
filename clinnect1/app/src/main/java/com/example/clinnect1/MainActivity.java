package com.example.clinnect1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.clinnect.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String apiKey = "AIzaSyBtiMxQ4SyLkAaHLyXofMt3CkQb8FiW_tk";
    private static final String TAG = "MainActivity.java" ;
    public static HashMap<String,String> types = new HashMap<>();
    final boolean[] checked = new boolean[6];
    public static HashMap<String,String> keywords = new HashMap<>();
    public static HashSet <String> selected_types= new HashSet<>();
    private LinearLayout userinfo;
    LocationManager locationManager;
    private static  final int REQUEST_LOCATION=1;


    //types = {"dentist", "doctor", "hospital", "pharmacy", "physiotherapist", "veterinary_care"}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        userinfo = findViewById(R.id.user);
        userinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),User.class));

            }
        });

        if(selected_types.isEmpty())
            selected_types.add("Clinic");

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

        FloatingActionButton filter_fab = findViewById(R.id.filter_fab);
        filter_fab.setImageResource(R.drawable.ic_filter_list_blue);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

        FloatingActionButton location_fab = findViewById(R.id.location_fab);
        location_fab.setImageResource(R.drawable.ic_my_location_black_24dp);
        location_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

                //Check gps is enable or not

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    //Get user to enable gps

                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else
                {
                    //GPS is already On then

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,

                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                        Toast.makeText(MainActivity.this, "Please allow location access and try again!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        final Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        Location LocationGps= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Location LocationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                        double lat = 0,lng = 0;

                        if (LocationGps !=null)
                        {
                            lat=LocationGps.getLatitude();
                            lng=LocationGps.getLongitude();
                        }
                        else if (LocationNetwork !=null)
                        {
                            lat=LocationNetwork.getLatitude();
                            lng=LocationNetwork.getLongitude();
                        }
                        else if (LocationPassive !=null)
                        {
                            lat=LocationPassive.getLatitude();
                            lng=LocationPassive.getLongitude();
                        }
                        if(lat == 0 && lng == 0)
                        {
                            Toast.makeText(MainActivity.this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            intent.putExtra("lat",lat);
                            intent.putExtra("lng",lng);
                            intent.putExtra("name","Your Location");
                            intent.putExtra("selected_types",selected_types);
                            intent.putExtra("checked",checked);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

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
        final Intent intent = new Intent(this, MapsActivity.class);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                                                            @Override
                                                            public void onPlaceSelected(Place place) {
                                                                Log.i(TAG, "Place:::::::: " + place.getName() + ", " + place.getId());
                                                                double lat = place.getLatLng().latitude;
                                                                double lng = place.getLatLng().longitude;
                                                                String name = place.getName();
                                                                intent.putExtra("lat",lat);
                                                                intent.putExtra("lng",lng);
                                                                intent.putExtra("name",name);
                                                                intent.putExtra("selected_types",selected_types);
                                                                intent.putExtra("checked",checked);
                                                                startActivity(intent);

                                                            }

                                                            @Override
                                                            public void onError(Status status) {
                                                                Log.i(TAG, "An error occurred: " + status);
                                                            }

                                                        }
        );

    }
    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
}