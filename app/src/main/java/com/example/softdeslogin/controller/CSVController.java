package com.example.softdeslogin.controller;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.softdeslogin.R;
import com.example.softdeslogin.model.Student;
import com.example.softdeslogin.view.AdminMainMenuActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class CSVController {

    private Context context;

    public CSVController(Context context){
        this.context = context;
    }

    public void generateCSV(View mView,final AlertDialog dialog, ArrayList<Student> studentsList){

        ProgressBar progressBar = mView.findViewById(R.id.progressBar);
        Button confirm = mView.findViewById(R.id.confirm);
        TextView csvMessage = mView.findViewById(R.id.csvMessage);

        try {
            Gson gson = new Gson();
            String json = gson.toJson(studentsList);

            JSONArray studentJSON = new JSONArray(json);

              String csv = CDL.toString(studentJSON);
            File[] externalStorageVolumes =
                    ContextCompat.getExternalFilesDirs(this.context, null);
            File primaryExternalStorage = externalStorageVolumes[0];

            File image = File.createTempFile(
                    "StudentList",  /* prefix */
                    ".csv",         /* suffix */
                    primaryExternalStorage      /* directory */
            );

            FileUtils.writeStringToFile(image, csv);

            progressBar.setVisibility(View.INVISIBLE);
            csvMessage.setVisibility(View.VISIBLE);
            confirm.setVisibility(View.VISIBLE);
            System.out.println("Data has been Sucessfully Writeen to "  + image.getAbsolutePath());

        } catch (Exception e) {

            progressBar.setVisibility(View.INVISIBLE);
            csvMessage.setVisibility(View.VISIBLE);
            csvMessage.setText("Failed to generate CSV");
            confirm.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }


        confirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }
}
