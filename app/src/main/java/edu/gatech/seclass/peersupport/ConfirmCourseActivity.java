package edu.gatech.seclass.peersupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfirmCourseActivity extends AppCompatActivity {
    TextView currentCourseTextView;
    TextView newCourseTextView;
    String username;
    Bundle bundle;
    String currentCourse;
    String newCourse;
    FirebaseDatabase database;
    DatabaseReference DbRef;
    DatabaseReference studentDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_course);

        Button button = (Button) findViewById(R.id.mainmenu);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmCourseActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        currentCourseTextView = findViewById(R.id.currentCourseName);
        newCourseTextView = findViewById(R.id.newCourseName);

        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString();
        }
        currentCourse = bundle.get("Current Course").toString();
        newCourse = bundle.get("New Course").toString();

        currentCourseTextView.setText(currentCourse);
        newCourseTextView.setText(newCourse);

        boolean surveyNotYetTaken = currentCourse.equals("No course added yet");

        database = FirebaseDatabase.getInstance();
        DbRef = database.getReference();
        studentDbRef = DbRef.child("students");

        Button yesBtn = (Button) findViewById(R.id.yesButton);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
//                intent.putExtra("Current Course", newCourse);
                studentDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot dataSnapshot = task.getResult();
                        for (DataSnapshot studentSnapShot : dataSnapshot.getChildren()) {
                            if (studentSnapShot.child("username").getValue().toString().equalsIgnoreCase(username)){
                                DatabaseReference currentStudentDBRef = studentSnapShot.getRef();
                                currentStudentDBRef.child("course").setValue(newCourse);
                            }
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Intent intent;
                        if (surveyNotYetTaken) {
                            intent = new Intent(ConfirmCourseActivity.this, SurveyActivityPageOne.class);
                        } else {
                            intent = new Intent(ConfirmCourseActivity.this, CourseActivity.class);
                        }
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                });

            }
        });

        Button noBtn = (Button) findViewById(R.id.noButton);
        noBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmCourseActivity.this, CourseActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

    }
}