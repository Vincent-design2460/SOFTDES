package com.example.softdeslogin.view;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;


import com.example.softdeslogin.R;
import com.example.softdeslogin.adapter.RecyclerViewAdapter;
import com.example.softdeslogin.model.Student;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class StudentListActivity extends AppCompatActivity implements RecyclerViewAdapter.OnStudentListener {

    private DatabaseReference ref;
    private ArrayList<Student> studentList;
    private StorageReference storageReference;
    private Student user;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve);

        ref = FirebaseDatabase.getInstance().getReference().child("Student");

        studentList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);

        toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));

        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);
        initAdapter();

    }


    private boolean isLoading = false;
    private int startingIndex = 20;
    private int currentSize;
    private int nextLimit;
    private String finalKey = "";

    private void initAdapter() {
        ref.limitToFirst(startingIndex).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    user = ds.getValue(Student.class);
                    user.setKey(ds.getKey());
                    studentList.add(user);
                }
                finalKey = studentList.get(studentList.size()-1).getKey();
                finalKey = finalKey;
                initializeAdapter();
                initScrollListener();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initializeAdapter(){
        recyclerViewAdapter = new RecyclerViewAdapter(studentList,this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == studentList.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });


    }


    private void loadMore() {
        System.out.println("HI");
        recyclerView.post(new Runnable() {
            public void run() {
                studentList.add(null);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                studentList.remove(studentList.size() - 1);
                int scrollPosition = studentList.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);
                currentSize = scrollPosition;
                nextLimit = currentSize + 20;

                    ref.limitToFirst(nextLimit).orderByKey().startAt(finalKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!finalKey.equals(studentList.get(studentList.size()-1).getKey())) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                user = ds.getValue(Student.class);
                                user.setKey(ds.getKey());
                                studentList.add(user);
                            }
                                finalKey = studentList.get(studentList.size() - 1).getKey();
                                recyclerViewAdapter.notifyDataSetChanged();
                                isLoading = false;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            }
        }, 2000);


    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(StudentListActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_confirmation,null);

        Button mStart = mView.findViewById(R.id.start);
        Button cancel = mView.findViewById(R.id.cancel);

            mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

            dialog.show();
            mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ref.child(studentList.get(viewHolder.getAdapterPosition()).getKey()).removeValue();
                studentList.remove(viewHolder.getAdapterPosition());
                recyclerViewAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

            cancel.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                recyclerViewAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

    }


    };


    @Override
    public void onStudentClick(int position) {

        final long ONE_MEGABYTE = 3000 * 3000;


        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final AlertDialog dialog = imageDialog.create();

        View layout = inflater.inflate(R.layout.student_detail,
                (ViewGroup) findViewById(R.id.layout_root));

        Button mStart = layout.findViewById(R.id.button);

        imageView = layout.findViewById(R.id.fullimage);

        storageReference = FirebaseStorage.getInstance().getReference().child("pictures/" + studentList.get(position).getKey() + ".jpeg");

        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                imageView.setMinimumHeight(dm.heightPixels);
                imageView.setMinimumWidth(dm.widthPixels);
                imageView.setImageBitmap(bm);
            }
        });
        mStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                recyclerViewAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        setStudentDetails(layout,position);
        dialog.setView(layout);
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);

        MenuItem menuItem = menu.findItem(R.id.searchView);

        SearchView searchView = (SearchView)menuItem.getActionView();

        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                recyclerViewAdapter.getFilter().filter(s);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.searchView)
            return true;

        return super.onOptionsItemSelected(item);
    }

    private void setStudentDetails(View mView,int position){
        TextView firstName = mView.findViewById(R.id.firstName);
        TextView lastName = mView.findViewById(R.id.lastName);
        TextView school = mView.findViewById(R.id.school);
        TextView gradeLevel = mView.findViewById(R.id.gradeLevel);
        TextView city = mView.findViewById(R.id.city);
        TextView emailPhone = mView.findViewById(R.id.emailPhone);
        TextView firstCourse = mView.findViewById(R.id.firstCourse);
        TextView secondCourse = mView.findViewById(R.id.secondCourse);
        TextView strandchoice = mView.findViewById(R.id.Strand);


        firstName.setText(studentList.get(position).getFirstName());
        lastName.setText(studentList.get(position).getLastName());
        school.setText(studentList.get(position).getSchool());
        gradeLevel.setText(studentList.get(position).getGradeLevel());
        city.setText(studentList.get(position).getAddress());
        emailPhone.setText(studentList.get(position).getEmail() + "/" + studentList.get(position).getPhone());
        strandchoice.setText(studentList.get(position).getStrand());
        firstCourse.setText(studentList.get(position).getFirstCourse());
        secondCourse.setText(studentList.get(position).getSecondCourse());

    }
}
