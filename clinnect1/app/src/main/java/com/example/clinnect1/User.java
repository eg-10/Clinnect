package com.example.clinnect1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clinnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class User extends AppCompatActivity {

    private LinearLayout logout,bookmark;
    private LinearLayout userinfo;
    private String customerid = "";
    private TextView namebox, agebox, genderbox;
    FirebaseAuth mAuth;
    ImageView imageView;
    DatabaseReference curruser;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        imageView = findViewById(R.id.img);
        namebox = findViewById(R.id.name1);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        logout = findViewById(R.id.logout);
        bookmark = findViewById(R.id.bookmarks);
        customerid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        curruser = FirebaseDatabase.getInstance().getReference().child("users").child(customerid);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity((new Intent(getApplicationContext(), Login_form.class)));


            }
        });
        bookmark.setVisibility(View.GONE);
        logout.setVisibility(View.GONE);
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Bookmark.class));
            }
        });
        progressDialog.show();
        curruser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map= (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name")!= null){
                        namebox.setText(map.get("name").toString());
                    }
                    bookmark.setVisibility(View.VISIBLE);
                    logout.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Login Failed or User Not Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });}}

