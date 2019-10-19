package com.example.clinnect1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.clinnect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_form extends AppCompatActivity {


    EditText emailId, password;
    LinearLayout lnregister;
    LinearLayout login;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);

        emailId = findViewById(R.id.text1);
        password = findViewById(R.id.text2);
        lnregister =findViewById(R.id.lnreg);
        login=findViewById(R.id.button2);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = emailId.getText().toString().trim();
                String pwd = password.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {

                    Toast.makeText(Login_form.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pwd)) {

                    Toast.makeText(Login_form.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pwd.length() < 6) {

                    Toast.makeText(Login_form.this, "Password too short", Toast.LENGTH_SHORT).show();
                }


                firebaseAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(Login_form.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                } else {


                                    Toast.makeText(Login_form.this, "Login Failed or User Not Available", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });


            }
        });

        lnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_form.this, Signup_form.class));
                finish();
            }
        });

    }




    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }
}