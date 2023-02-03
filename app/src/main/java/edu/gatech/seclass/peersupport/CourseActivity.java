package edu.gatech.seclass.peersupport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends AppCompatActivity implements CourseAdapter.ItemClickListener{
    CourseAdapter courseAdapter;
    String username;
    Bundle bundle;
    FirebaseDatabase database;
    DatabaseReference studentDbRef;
    DatabaseReference coursesDbRef;
    DatabaseReference DbRef;
    TextView currentCourseTextView;
    String currentCourse;
    String course;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString();
        }
        database = FirebaseDatabase.getInstance();
        DbRef = database.getReference();
        studentDbRef = DbRef.child("students");
        coursesDbRef = DbRef.child("courses");
        currentCourseTextView = findViewById(R.id.currentCourseName);

        Button button = (Button) findViewById(R.id.mainmenu);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        //TODO: Pull courses from Firebase
        List<String> masterCourseList = new ArrayList<>();
//        masterCourseList.add("CS6310: Software Architecture and Design");
//        masterCourseList.add("CS6460: Educational Technology");
//        masterCourseList.add("CS6750: Human Computer Interaction");
        coursesDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot courseSnapShot : dataSnapshot.getChildren()) {
                    course = courseSnapShot.getValue().toString();
                    masterCourseList.add(course);
                }
            }

        });

        studentDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot studentSnapShot : dataSnapshot.getChildren()) {
                    if (studentSnapShot.child("username").getValue().toString().equalsIgnoreCase(username)) {
                        currentCourse = studentSnapShot.child("course").getValue().toString();
                        for (String course : masterCourseList) {
                            if (course.contains(currentCourse)) {
                                currentCourse = course;
                            }
                            currentCourseTextView.setText(currentCourse);
                        }
                    }
                }
                List<String> courses = new ArrayList<>();
                for (String course : masterCourseList) {
                    if (!course.equalsIgnoreCase(currentCourse)) {
                        courses.add(course);
                    }
                }
                RecyclerView courseRecycler = (RecyclerView) findViewById(R.id.courseList);
                courseRecycler.setLayoutManager(new LinearLayoutManager(CourseActivity.this));
                courseAdapter = new CourseAdapter(CourseActivity.this, courses);
                courseAdapter.setClickListener(CourseActivity.this);
                courseRecycler.setAdapter(courseAdapter);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(courseRecycler.getContext(),1);
                courseRecycler.addItemDecoration(dividerItemDecoration);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + courseAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        //Log.v("View", view.getTransitionName());
        Intent intent = new Intent(CourseActivity.this, ConfirmCourseActivity.class);
        intent.putExtra("Current Course", currentCourse);
        intent.putExtra("New Course", courseAdapter.getItem(position));
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
