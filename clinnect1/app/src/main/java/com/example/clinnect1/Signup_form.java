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

public class Signup_form extends AppCompatActivity {


    EditText emailId, password,confirmpassword;
    Button btn_register;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);
        getSupportActionBar().setTitle("Signup Form");

        emailId = findViewById(R.id.text1);
        password = findViewById(R.id.text2);
        confirmpassword = findViewById(R.id.text3);
        btn_register = findViewById(R.id.button1);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = emailId.getText().toString().trim();
                String pwd = password.getText().toString().trim();
                String confpwd = confirmpassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)){

                    Toast.makeText(Signup_form.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pwd)){

                    Toast.makeText(Signup_form.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(confpwd)){

                    Toast.makeText(Signup_form.this, "Please Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pwd.length()<6){

                    Toast.makeText(Signup_form.this, "Password too short", Toast.LENGTH_SHORT).show();
                }

                if (pwd.equals(confpwd)){

                    firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(Signup_form.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        Toast.makeText(Signup_form.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                                    }
                                    else {

                                        Toast.makeText(Signup_form.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });


                }
            }
        });

    }
}
