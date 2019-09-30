package com.example.clinnect1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clinnect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login_form extends AppCompatActivity {


    EditText emailId, password;
    Button btn_login;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
        getSupportActionBar().setTitle("Login Form");
        emailId = findViewById(R.id.text1);
        password = findViewById(R.id.text2);
        btn_login = findViewById(R.id.button2);
        firebaseAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {
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

    }

    public void btn_signupForm(View view) {

        startActivity(new Intent(getApplicationContext(),Signup_form.class));
    }
}