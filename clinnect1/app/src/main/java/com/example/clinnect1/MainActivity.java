package com.example.clinnect1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clinnect.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private static final String apiKey = "AIzaSyBtiMxQ4SyLkAaHLyXofMt3CkQb8FiW_tk";
    private static final String TAG = "MainActivity.java" ;
    private Button userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userinfo = (Button) findViewById(R.id.user);
        userinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),User.class));

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
        autocompleteFragment.setTypeFilter(TypeFilter.REGIONS);
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