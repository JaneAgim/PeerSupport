package edu.gatech.seclass.peersupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
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

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements Serializable {
    DatabaseReference DbRef;
    DatabaseReference studentDbRef;
    Button studentInsertData;
    String username;
    Bundle bundle;

    public DatabaseReference getDbRef() {
        return DbRef;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString();
        }

        DbRef = FirebaseDatabase.getInstance().getReference();
        studentDbRef = DbRef.child("students");
        DatabaseReference coursesDBRef;
        coursesDBRef = DbRef.child("courses");

        // button to open messenger ---------------------------------------------------------------------------
        Button button = (Button) findViewById(R.id.messenger);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // messenger button end ---------------------------------------------------------------------------

        // button to open course changer ---------------------------------------------------------------------------
        Button button1 = (Button) findViewById(R.id.changeCourse);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CourseActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // course changer button end ---------------------------------------------------------------------------

        // button to open course survey  ---------------------------------------------------------------------------
        Button button2 = (Button) findViewById(R.id.retakeSurvey);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SurveyActivityPageOne.class);
                intent.putExtra("username", username);
                resetMatches();
                startActivity(intent);
            }
        });
        // course survey button end ---------------------------------------------------------------------------

        // button to settings ---------------------------------------------------------------------------
        Button button3 = (Button) findViewById(R.id.settings);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // settings button end ---------------------------------------------------------------------------

        // button to files ---------------------------------------------------------------------------
        Button button5 = (Button) findViewById(R.id.files);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FilesActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // files button end ---------------------------------------------------------------------------


        // button to logout ---------------------------------------------------------------------------
        Button button4 = (Button) findViewById(R.id.logout);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.v("logout", username);
                studentDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        for (DataSnapshot studentSnapshot : task.getResult().getChildren()) {
                            if (studentSnapshot.child("username").getValue().toString().equalsIgnoreCase(username)) {
                                DatabaseReference ref = studentSnapshot.getRef();
                                ref.child("logged in").setValue(false);
                                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.cancel(0);
                            }
                        }
                    }
                });

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // logout button end ---------------------------------------------------------------------------




        /*studentInsertData = (Button) findViewById(R.id.push);
        studentInsertData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                coursesDBRef.push().setValue("CS6460: Educational Technology");
                coursesDBRef.push().setValue("CS6310: Software Architecture and Design");
                coursesDBRef.push().setValue("CS6750: Human Computer Interaction");
                coursesDBRef.push().setValue("CS6250: Computer Networks");
                coursesDBRef.push().setValue("CS6035: Introduction to Information Security");
                // add courses to database
                // add student to database ---------------------------------------------------------------------------
                //DbRef.child("students_next_ID").setValue(1+"");
                *//*DbRef.child("students_next_ID").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        int[] iD = {Integer.parseInt(task.getResult().getValue().toString())};
                        DatabaseReference specificStudentDbRef = studentDbRef.child(iD[0]+"");
                        specificStudentDbRef.child("course").setValue("CS6310");
                        specificStudentDbRef.child("first name").setValue("Jess");
                        specificStudentDbRef.child("last name").setValue("Schmidt");
                        specificStudentDbRef.child("username").setValue("Jess_Schmidt");
                        specificStudentDbRef.child("matched to").child("username 1").setValue("null");
                        specificStudentDbRef.child("matched to").child("username 2").setValue("null");
                        iD[0]++;
                        Log.v("on push click", iD[0]+"");
                        DbRef.child("students_next_ID").setValue(iD[0]+"");
                    }
                });*//*
                // end add student to database ---------------------------------------------------------------------------

                // add messages to database ---------------------------------------------------------------------------

                //DbRef.child("messages_next_ID").setValue(1+"");
//                DbRef.child("messages_next_ID").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DataSnapshot> task) {
//                        int[] iD = {Integer.parseInt(task.getResult().getValue().toString())};
//                        DatabaseReference ref = DbRef.child("messages").child(iD[0]+"");
//                        Map<String, String> message = new HashMap<>();
//                        message.put("sender", "Jess_Schmidt");
//                        message.put("receiver", "Benton_Miller");
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
//                        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
//                        message.put("time sent", calendar.getTime()+"");
//                        message.put("message contents", "Hi, Benton");
//                        ref.setValue(message);
//                        iD[0]++;
//                        DbRef.child("messages_next_ID").setValue(iD[0]+"");
//                    }
//                });
                // add messages end ---------------------------------------------------------------------------
            }
        });*/


        // switch from first name to username --------------------------------------------------------------------------
        // end switch from first name to username ---------------------------------------------------------------------------
    }

    public void resetMatches(){
        studentDbRef = DbRef.child("students");
        studentDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    if(studentSnapshot.child("username").getValue().toString().equalsIgnoreCase(username)){
                        DatabaseReference specificstudentDBRef = studentSnapshot.getRef();
                        specificstudentDBRef.child("matched to").removeValue();
                    }
                }
            }
        });
    }

}