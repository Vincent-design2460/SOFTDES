package com.example.softdeslogin.view;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.softdeslogin.R;

public class SplashActivity extends AppCompatActivity {
    private Button Button_Guest, Button_Admissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        Button_Guest=(Button)findViewById(R.id.btn_guest2);
        Button_Admissions=(Button)findViewById(R.id.btn_admissions) ;


        Button_Guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Openguest();
            }
        });
        Button_Admissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Openadmissioms();
            }
        });

    }
    public void Openguest()
    {
        Intent intent=new Intent(this, AddStudentActivity.class);
        startActivity(intent);
    }
    public void Openadmissioms()
    {
        Intent intent=new Intent(this, AdminLoginActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() { }

}
