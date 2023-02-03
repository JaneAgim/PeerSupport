package edu.gatech.seclass.peersupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class SelectSecondGroupContactActivity extends AppCompatActivity implements OtherContactsAdapter.ItemClickListener{
    String username;
    Bundle bundle;
    OtherContactsAdapter otherContactsAdapter;
    DatabaseReference studentDBRef;
    String course;
    String firstStudentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_second_group_contact);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString();
        }

        // button to take user to main menu ---------------------------------------------------------------------------
        Button button = (Button) findViewById(R.id.mainmenu);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SelectSecondGroupContactActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // end button ---------------------------------------------------------------------------

        firstStudentUsername = bundle.get("First_User").toString();

        studentDBRef = FirebaseDatabase.getInstance().getReference().child("students");
        ArrayList<String> allContacts = new ArrayList<>();

        studentDBRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot studentSnapShot : dataSnapshot.getChildren()) {
                    if (studentSnapShot.child("username").getValue().toString().equalsIgnoreCase(username)) {
                        course = studentSnapShot.child("course").getValue().toString();
                        //Log.v("Course", course);
                    }
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot studentSnapShot : dataSnapshot.getChildren()) {
                    final boolean[] currentlyMessaging = {false};
                    //Log.v("Firebase", studentSnapShot.child("first name").getValue().toString());
                    if (!studentSnapShot.child("username").getValue().toString().equalsIgnoreCase(username) && !studentSnapShot.child("username").getValue().toString().equalsIgnoreCase(firstStudentUsername)) {
                        if (studentSnapShot.child("course").getValue().toString().equalsIgnoreCase(course)) {
                            allContacts.add(studentSnapShot.child("username").getValue().toString());
                        }
                    }
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                RecyclerView allContactsRecycler = (RecyclerView) findViewById(R.id.secondGroupContacts);
                allContactsRecycler.setLayoutManager(new LinearLayoutManager(SelectSecondGroupContactActivity.this));
                Collections.sort(allContacts);
                otherContactsAdapter = new OtherContactsAdapter(SelectSecondGroupContactActivity.this, allContacts);
                otherContactsAdapter.setClickListener(SelectSecondGroupContactActivity.this);
                allContactsRecycler.setAdapter(otherContactsAdapter);
                DividerItemDecoration dividerItemDecoration1 = new DividerItemDecoration(allContactsRecycler.getContext(),1);
                allContactsRecycler.addItemDecoration(dividerItemDecoration1);
            }
        });

    }

    @Override
    public void onItemClickOther(View view, int position) {
        //Toast.makeText(this, "You clicked " + currentContactsAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        //Log.v("View", view.getTransitionName());
        Intent intent = new Intent(SelectSecondGroupContactActivity.this, MessengerActivity.class);
        Log.v("Second select", firstStudentUsername + "`" + otherContactsAdapter.getItem(position));
        intent.putExtra("Group_Info", firstStudentUsername + "`" + otherContactsAdapter.getItem(position));
        intent.putExtra("username", username);
        startActivity(intent);
    }
}