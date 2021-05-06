package com.example.softdeslogin.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.softdeslogin.R;
import com.example.softdeslogin.model.Student;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class AddStudentActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    public static  final int REQUEST_CODE = 101;
    public static final int REQUEST_CODE_camera = 102;

    private ArrayList<String> cities;
    private ArrayList<String> levels;
    private ArrayList<String> courses;
    private ArrayList<String> strands;

    EditText fName,Phonenumber,lName,Email,School;
    TextView Dateview;
    Button Submit,Capturebutton;
    DatabaseReference reff;
    Student student;
    TextView firstchoice,secondchoice;


    Spinner spinner,spinner2,spinner3,spinnerAdd,strandchoice;
    ImageView PImageView;
    String currentPhotoPath;
    File f;
    Uri contentUri;
    StorageReference storageReference;
    StorageReference Image;
    String encImage;
    long maxid=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentmodule);
        //Camera
        PImageView = findViewById(R.id.cameraview);
        Capturebutton = findViewById(R.id.capture);
        Capturebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askcamerapermission();
            }
        });
        storageReference = FirebaseStorage.getInstance().getReference();

        //textview
        firstchoice = findViewById(R.id.firstchoiceview);
        secondchoice = findViewById(R.id.secondchoiceview);


        //Date
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-8"));
        Dateview = findViewById(R.id.date_layout);
        String currentdate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        Dateview.setText(currentdate);

        this.initalizeDropdown();

        // Spinner element
         strandchoice = (Spinner) findViewById(R.id.spinnerstrand);
         spinner = (Spinner) findViewById(R.id.SNR_FirstChoice);
         spinner2 = (Spinner) findViewById(R.id.SNR_SecondChoice);
         spinner3 = (Spinner) findViewById(R.id.Spinner3);
         spinnerAdd = (Spinner) findViewById(R.id.spinneraddress);
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);


        // Creating adapter for spinner

        final ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.courses);
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.levels);
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.cities);
        final ArrayAdapter<String> strandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,this.strands);

        // Drop down layout style - list view with radio button
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        strandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(courseAdapter);
        spinner2.setAdapter(courseAdapter);
        spinner3.setAdapter(levelAdapter);
        spinnerAdd.setAdapter(cityAdapter);
        strandchoice.setAdapter(strandAdapter);

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               switch (position)
               {

                   case 6:
                   { spinner.setVisibility(View.VISIBLE);
                   spinner2.setVisibility(View.VISIBLE);
                   firstchoice.setVisibility(View.VISIBLE);
                   secondchoice.setVisibility(View.VISIBLE);
                   strandchoice.setVisibility(View.INVISIBLE);
                   strandchoice.setAdapter(strandAdapter);

                   break;

                   }
                   default:
                   {spinner.setVisibility(View.INVISIBLE);
                   spinner2.setVisibility(View.INVISIBLE);
                   strandchoice.setVisibility(View.VISIBLE);
                   firstchoice.setVisibility(View.INVISIBLE);
                   secondchoice.setVisibility(View.INVISIBLE);
                   spinner.setAdapter(courseAdapter);
                   spinner2.setAdapter(courseAdapter);
                   break;

                   }


               }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }


        });




        Email = findViewById(R.id.TXT_Email);
        School =  findViewById(R.id.TXT_SchoolName);

        fName = findViewById(R.id.TXT_FirstName);
        lName =  findViewById(R.id.TXT_LastName);
        Phonenumber =  findViewById(R.id.TXT_Phone);
        Submit = findViewById(R.id.submitbutton);
        student = new Student();

        reff = FirebaseDatabase.getInstance().getReference().child("Student");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    maxid=(dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddStudentActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.activity_confirmsubmit,null);

                Button mStart = mView.findViewById(R.id.start);
                Button cancel = mView.findViewById(R.id.cancel);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                dialog.show();
                mStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!checkError()) {
                            student.setFirstName(fName.getText().toString().trim());
                            student.setLastName(lName.getText().toString().trim());
                            student.setPhone(Phonenumber.getText().toString().trim());
                            student.setDate(Dateview.getText().toString().trim());
                            student.setSchool(School.getText().toString().trim());
                            student.setEmail(Email.getText().toString().trim());
                            student.setFirstCourse(spinner.getSelectedItem().toString());
                            student.setSecondCourse(spinner2.getSelectedItem().toString());
                            student.setGradeLevel(spinner3.getSelectedItem().toString());
                            student.setAddress(spinnerAdd.getSelectedItem().toString());
                            student.setStrand(strandchoice.getSelectedItem().toString());
                            student.setImagename(contentUri.getPath());
                            String key = reff.push().getKey();

                            uploadimagetofirebase(key + ".jpeg", contentUri);
                            reff.child(key).setValue(student);

                            Toast.makeText(AddStudentActivity.this, "Data Submitted Successfully", Toast.LENGTH_LONG).show();

                            gobacktomainmenu(); }
                        dialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


            }
        });
    }


    private void uploadimagetofirebase(String name,Uri contentUri) {
        Image = storageReference.child("pictures/" + name);
        Image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Upload image URL is " + uri.toString());
                    }


                });
                Toast.makeText(AddStudentActivity.this,"Upload Succeed",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddStudentActivity.this,"Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void askcamerapermission() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CODE);
            }
        else {
            dispatchTakePictureIntent();
        }

        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
           if (requestCode == REQUEST_CODE){
               if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   dispatchTakePictureIntent();
               }else {
                   Toast.makeText(this, "Camera permission is denied", Toast.LENGTH_SHORT).show();
                   
               }
           }
        }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_camera) {
            if (resultCode == Activity.RESULT_OK){
                f = new File(currentPhotoPath);

                PImageView.setImageURI(Uri.fromFile(f));
                Log.d("tag", "Absolute url image is " + Uri.fromFile(f));
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_FINISHED);

                FileInputStream fis = null;
                try{
                    fis = new FileInputStream(f);
                    Bitmap bm = BitmapFactory.decodeStream(fis);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    byte[] b = baos.toByteArray();
                    encImage = Base64.encodeToString(b, Base64.DEFAULT);
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }


                contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);

                System.out.println(contentUri.toString());
            }

        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.softdeslogin.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, REQUEST_CODE_camera);
            }
        }
    }

    public void gobacktomainmenu()
    {
        Intent intent=new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), item, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    public boolean checkError() {
        boolean hasError = false;
        if (TextUtils.isEmpty(fName.getText())) {
            fName.setError("Field is Required!");
            hasError = true;
        }
        if (TextUtils.isEmpty(Phonenumber.getText())) {
            Phonenumber.setError("Field is Required!");
            hasError = true;
        }
        if (TextUtils.isEmpty(Email.getText()) || !Patterns.EMAIL_ADDRESS.matcher(Email.getText()).matches()) {
            Email.setError("Invalid Email");
            hasError = true;
        }
        if (TextUtils.isEmpty(School.getText())) {
            School.setError("Field is Required!");
            hasError = true;
        }
        if (TextUtils.isEmpty(lName.getText())) {
            lName.setError("Field is Required!");
            hasError = true;
        }
        if (spinner3.getSelectedItem().toString().trim().equals("Grade Level Choice")) {
            hasError = true;
            Toast.makeText(AddStudentActivity.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
        }
        if (spinner3.getSelectedItem().toString().trim().equals("Grade 7")) {
            if (strandchoice.getSelectedItem().toString().trim().equals("No Strand Choice")) {
                hasError = true;
                Toast.makeText(AddStudentActivity.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
            }

        }
        if (spinner3.getSelectedItem().toString().trim().equals("Grade 8")) {
            if (strandchoice.getSelectedItem().toString().trim().equals("No Strand Choice")) {
                hasError = true;
                Toast.makeText(AddStudentActivity.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
            }

        }
        if (spinner3.getSelectedItem().toString().trim().equals("Grade 9")) {
            if (strandchoice.getSelectedItem().toString().trim().equals("No Strand Choice")) {
                hasError = true;
                Toast.makeText(AddStudentActivity.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
            }

        }
        if (spinner3.getSelectedItem().toString().trim().equals("Grade 10")) {
            if (strandchoice.getSelectedItem().toString().trim().equals("No Strand Choice")) {
                hasError = true;
                Toast.makeText(AddStudentActivity.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
            }

        }
        if (spinner3.getSelectedItem().toString().trim().equals("Grade 11")) {
            if (strandchoice.getSelectedItem().toString().trim().equals("No Strand Choice")) {
                hasError = true;
                Toast.makeText(AddStudentActivity.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
            }

        }
        if (spinnerAdd.getSelectedItem().toString().trim().equals("Select a City")) {
            hasError = true;
            Toast.makeText(AddStudentActivity.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
        }
        if (contentUri == null) {
            hasError = true;
            Capturebutton.setError("Take photo");
            Toast.makeText(AddStudentActivity.this, "Please Take a Photo", Toast.LENGTH_SHORT).show();
        }
        if (spinner3.getSelectedItem().toString().trim().equals("Grade 12")) {
            if (spinner.getSelectedItem().toString().trim().equals("No Course Choice")) {
                hasError = true;
                Toast.makeText(AddStudentActivity.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
            }
            if (spinner2.getSelectedItem().toString().trim().equals("No Course Choice")) {
                hasError = true;
                Toast.makeText(AddStudentActivity.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
            }

        }

        return hasError;
    }

    private void initalizeDropdown(){
        this.cities = new ArrayList<>();
        this.levels = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.strands = new ArrayList<>();

        //initialize dropdown for levels
        this.levels.add("Grade Level Choice");
        this.levels.add("Grade 7");
        this.levels.add("Grade 8");
        this.levels.add("Grade 9");
        this.levels.add("Grade 10");
        this.levels.add("Grade 11");
        this.levels.add("Grade 12");

        //initialize dropdown for strands
        this.strands.add("No Strand Choice");
        this.strands.add("STEM");
        this.strands.add("HUMMS");
        this.strands.add("ABM");
        this.strands.add("GAS");


        //initialize dropdown for courses
        this.courses.add("No Course Choice");
        this.courses.add("BS in Computer Engineering");
        this.courses.add("BS in Electronics Engineering");
        this.courses.add("BS in Industrial Design");
        this.courses.add("BS in Computer Science");
        this.courses.add("BS in Information Technology");
        this.courses.add("BS in Entertainment and Multimedia Computing");
        this.courses.add("Associate in Computer Technology");
        this.courses.add("BS in Business Administration");
        this.courses.add("BS in Accountancy");
        this.courses.add("BS in Tourism Management");
        this.courses.add("BS in Management Accounting");
        this.courses.add("BS in Multimedia Arts");
        this.courses.add("BS in Psychology");

        //initialize dropdown for cities
        this.cities.add("Select a City");
        this.cities.add("Outside NCR");
        this.cities.add("Manila");
        this.cities.add("Quezon");
        this.cities.add("Caloocan");
        this.cities.add("Las Piñas");
        this.cities.add("Makati");
        this.cities.add("Malabon");
        this.cities.add("Mandaluyong");
        this.cities.add("Marikina");
        this.cities.add("Muntinlupa");
        this.cities.add("Navotas");
        this.cities.add("Parañaque");
        this.cities.add("Pasay");
        this.cities.add("Pasig");
        this.cities.add("San Juan");
        this.cities.add("Taguig");
        this.cities.add("Valenzuela");
        this.cities.add("Pateros");
    }
}

