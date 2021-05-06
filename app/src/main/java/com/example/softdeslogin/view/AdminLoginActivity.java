package com.example.softdeslogin.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.softdeslogin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AdminLoginActivity extends AppCompatActivity {
    public EditText Passwordedittext;
    public EditText Usernameedittext;
    public Button signinbutton;
     FirebaseAuth mFirebaseAuth;
   FirebaseAuth.AuthStateListener mAuthstatelistener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        Passwordedittext = (EditText) findViewById(R.id.inputPassword);
        Usernameedittext = (EditText) findViewById(R.id.inputUsername);
        signinbutton = (Button) findViewById(R.id.btn_signIn);

        signinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mAuthstatelistener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseuser = mFirebaseAuth.getCurrentUser();
                if ( mFirebaseuser !=null){
                    Toast.makeText(AdminLoginActivity.this,"Welcome Admin!",Toast.LENGTH_SHORT ).show();
                }
                else {

                }

            }
        };


        signinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Usernameedittext.getText().toString();
                String password = Passwordedittext.getText().toString();
                Toast.makeText(AdminLoginActivity.this, "LOADING...",Toast.LENGTH_LONG).show();
                if (username.isEmpty()){
                    Usernameedittext.setError("Please Enter Username");
                    Usernameedittext.requestFocus();
                }
                else if (password.isEmpty()){
                    Passwordedittext.setError("Please Enter Password");
                    Passwordedittext.requestFocus();
                }
                else if (username.isEmpty() && password.isEmpty()){
                    Toast.makeText(AdminLoginActivity.this, "Fields are Empty!",Toast.LENGTH_SHORT).show();
                }
                else if (!(username.isEmpty() && password.isEmpty())){

                    mFirebaseAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(AdminLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(AdminLoginActivity.this, "Login Error, Please Login Again.",Toast.LENGTH_LONG).show();
                            }
                            else {

                                Intent successintent = new Intent (AdminLoginActivity.this, AdminMainMenuActivity.class);
                                startActivity(successintent);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(AdminLoginActivity.this, "Error Occured",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthstatelistener);
    }




}

