package com.example.softdeslogin.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;

import com.example.softdeslogin.R;
import com.example.softdeslogin.controller.CSVController;
import com.example.softdeslogin.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminMainMenuActivity extends AppCompatActivity {

    GridLayout mainGrid;
    private DatabaseReference studentsRef;
    private ArrayList<Student> studentsList;
    private CSVController csvController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminmainmenu);
        mainGrid = (GridLayout)findViewById(R.id.mainGrid);
        setSingleEvent(mainGrid);
    }

    private void setSingleEvent(GridLayout mainGrid) {
        for(int i=0; i<mainGrid.getChildCount();i++)
        {
            CardView cardview = (CardView)mainGrid.getChildAt(i);
            final int finalI = i;
            cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(finalI == 0)
                    {
                        Intent intent = new Intent(AdminMainMenuActivity.this, StudentListActivity.class);
                        startActivity(intent);
                    }
                    else if (finalI == 1)
                    {

                        csvController = new CSVController(getApplicationContext());
                        generateStudents();

                    }
                    else if (finalI == 2)
                    {

                        FirebaseAuth.getInstance().signOut();
                        Intent gobackadminlogin = new Intent(AdminMainMenuActivity.this, MainMenuActivity.class);
                        startActivity(gobackadminlogin);
                    }
                    
                }
            });
        }
    }


    private void generateStudents(){
        studentsList = new ArrayList<>();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AdminMainMenuActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.generate_csv,null);

        mBuilder.setView(mView);

        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        this.studentsRef = FirebaseDatabase.getInstance().getReference().child("Student");
        this.studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    studentsList.add(ds.getValue(Student.class));
                }
                csvController.generateCSV(mView,dialog,studentsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed()
    {

    }
}
