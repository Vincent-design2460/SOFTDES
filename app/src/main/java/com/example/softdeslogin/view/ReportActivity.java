package com.example.softdeslogin.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;

import com.example.softdeslogin.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ReportActivity extends AppCompatActivity {
    private DatabaseReference numberOfProspectRef, numberOfOutsideNCRRef, numberInsideMakatiRef, numberCompEngrRef;
    private TextView tNumberOfProspects, tNumberOfOutsideNCR, tNumberInsideMakati, tNumberCompEngr;
    private int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportgen);


        tNumberOfProspects = findViewById(R.id.numberProspect);
        tNumberOfOutsideNCR = findViewById(R.id.numberOutsideNCR);
        tNumberInsideMakati = findViewById(R.id.numberInsideMakati);
        tNumberCompEngr = findViewById(R.id.numberCompEngrStud);

        numberOfProspectRef = FirebaseDatabase.getInstance().getReference().child("Student");
        numberOfOutsideNCRRef = FirebaseDatabase.getInstance().getReference().child("Student");
        numberInsideMakatiRef = FirebaseDatabase.getInstance().getReference().child("Student");
        numberCompEngrRef = FirebaseDatabase.getInstance().getReference().child("Student");


        numberOfProspectRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists())
                {counter = counter + 1;
                String strCounter = String.valueOf(counter);
                tNumberOfProspects.setText(strCounter);}
                else
                {tNumberOfProspects.setText("0");}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query queryNCR = numberOfOutsideNCRRef.orderByChild("address").equalTo("Outside NCR");

        queryNCR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                     {    String numberOutsideNCR = String.valueOf(dataSnapshot.getChildrenCount());
                   tNumberOfOutsideNCR.setText(numberOutsideNCR);}
                else
                    {tNumberOfOutsideNCR.setText("0");}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query queryMakati = numberInsideMakatiRef.orderByChild("address").equalTo("Makati");

        queryMakati.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {    String numberInsideMakati = String.valueOf(dataSnapshot.getChildrenCount());
                    tNumberInsideMakati.setText(numberInsideMakati);}
                else
                {tNumberInsideMakati.setText("0");}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query queryCompEngrStud = numberCompEngrRef.orderByChild("firstCourse").equalTo("BS in Computer Engineering");

        queryCompEngrStud.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {    String numberCompEngrStud = String.valueOf(dataSnapshot.getChildrenCount());
                    tNumberCompEngr.setText(numberCompEngrStud);}
                else
                {tNumberCompEngr.setText("0");}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }




}