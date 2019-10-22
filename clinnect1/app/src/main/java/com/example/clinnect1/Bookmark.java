package com.example.clinnect1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.clinnect.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Bookmark extends AppCompatActivity {
    Context context;
    private static final String apiKey = "AIzaSyBtiMxQ4SyLkAaHLyXofMt3CkQb8FiW_tk";
    RecyclerView recyclerView;
    String name,addr;
    PlaceAdapter adapter;
    static List<Place> placeList;
    DatabaseReference userbookmarks,place;
    String customerid;
    public static JSONObject results;
    ProgressDialog progressDialog;
    String placeid;
    TextView empty;

    double rating;
    static double[] ratings;
    int i;
    private final String TAG = "Bookmark";
    Context cont;
    Place places;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        cont = this;
        placeList = new ArrayList<>();
        placeList.clear();
        customerid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        empty = findViewById(R.id.empty_view);
        place = FirebaseDatabase.getInstance().getReference().child("users").child(customerid).child("places");
        userbookmarks = FirebaseDatabase.getInstance().getReference().child("users").child(customerid).child("places").child("bookmarks");
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        ratings = new double[20];
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaceAdapter(this,placeList);
        progressDialog.show();
        place.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("bookmarks")){
                    userbookmarks.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                i = 0;

                                for (DataSnapshot child : dataSnapshot.getChildren()) {


                       /* currplace = FirebaseDatabase.getInstance().getReference().child("places").child(bookmarks[i]).child("rating");
                        currplace.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                    double sum = 0;
                                    int count = 0;
                                    double avg;
                                    for(DataSnapshot chil : dataSnapshot.getChildren()){
                                        sum = sum + Double.valueOf(chil.getValue().toString());
                                        count++;
                                    }
                                    avg = sum/count;
                                    rating = avg;
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });*/
                                    placeid = child.getKey();
                                    rating = Double.valueOf(child.child("rating").getValue().toString());
                                    name= child.child("name").getValue().toString();
                                    addr = child.child("address").getValue().toString();
                                    places = new Place(name,addr,rating,placeid);
                                    placeList.add(places);

                                    //placeList.add(new Place(name,addr,rating));
                                    // Log.i(TAG,names[i]+addrs[i]+ratings[i]);
                                    i++;
                                }
                                adapter.notifyDataSetChanged();
                                progressDialog.dismiss();


                            }}

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressDialog.dismiss();
                        }
                    });
                }
                else {
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }
}
