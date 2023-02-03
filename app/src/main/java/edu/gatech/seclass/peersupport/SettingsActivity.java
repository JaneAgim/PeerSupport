package edu.gatech.seclass.peersupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.view.Change;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    Bundle bundle;
    DatabaseReference DbRef;
    DatabaseReference studentDbRef;
    TextView usernameTextView;
    TextView emailTextView;
    TextView currentCourseTextView;
    TextView nameTextView;
    TextView matchedUsersTextView;
    String username;
    String currentCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString();
        }

        DbRef = FirebaseDatabase.getInstance().getReference();
        studentDbRef = DbRef.child("students");

        usernameTextView = findViewById(R.id.usernameSettings);
        emailTextView = findViewById(R.id.emailSettings);
        currentCourseTextView = findViewById(R.id.currentCourseSettings);
        nameTextView = findViewById(R.id.nameSettings);
        matchedUsersTextView = findViewById(R.id.matchedUsersSettings);

        Button button = (Button) findViewById(R.id.mainSettings);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        Button button1 = (Button) findViewById(R.id.changeNameSettings);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangeNameActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        Button button2 = (Button) findViewById(R.id.changePasswordSettings);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        Button button3 = (Button) findViewById(R.id.changeEmailSettings);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangeEmailActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        studentDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot studentSnapShot : dataSnapshot.getChildren()) {
                    if (studentSnapShot.child("username").getValue().toString().equalsIgnoreCase(username)) {
                        usernameTextView.setText(username);
                        emailTextView.setText(studentSnapShot.child("email").getValue().toString());
                        currentCourseTextView.setText(studentSnapShot.child("course").getValue().toString());
                        nameTextView.setText(studentSnapShot.child("first name").getValue().toString() + " " + studentSnapShot.child("last name").getValue().toString());
                        String matches = "";
                        for (DataSnapshot matchedSnapshot : studentSnapShot.child("matched to").getChildren()) {
                            Log.v("in the for loop", "we are here");
                            matches += matchedSnapshot.getValue().toString() + ", ";
                        }
                        if (!matches.isEmpty()) {
                            Log.v("in matches", "we are here");
                            matchedUsersTextView.setText(matches);
                            matchedUsersTextView.setMovementMethod(new ScrollingMovementMethod());
                        }

                    }
                }

            }
        });

    }
}