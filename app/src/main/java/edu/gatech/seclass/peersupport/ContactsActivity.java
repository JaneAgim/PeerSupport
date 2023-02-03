//TODO: make "matched to this user" / "currently messaging" array and populate, then fill
package edu.gatech.seclass.peersupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class ContactsActivity extends AppCompatActivity implements CurrentContactsAdapter.ItemClickListener, OtherContactsAdapter.ItemClickListener {
    CurrentContactsAdapter currentContactsAdapter;
    OtherContactsAdapter otherContactsAdapter;
    FirebaseDatabase database;
    DatabaseReference studentDbRef;
    DatabaseReference DbRef;
    String username;
    String course;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString();
        }

        // toast for when user clicks on notification, and it brings them to ContactsActivity ---------------------------------------------------------------------------
        if (getIntent().getExtras() != null && getIntent().getExtras().get("notification_info") != null) {
            //Log.v("notifs", getIntent().getExtras().get("notification_info").toString());
            Toast.makeText(ContactsActivity.this, getIntent().getExtras().get("notification_info").toString(), Toast.LENGTH_LONG).show();
        }
        // end toast ---------------------------------------------------------------------------

        // button to take user to main menu ---------------------------------------------------------------------------
        Button button = (Button) findViewById(R.id.mainmenu);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactsActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // end button ---------------------------------------------------------------------------

        // button to take user to groups ---------------------------------------------------------------------------
        Button button1 = (Button) findViewById(R.id.groupButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactsActivity.this, GroupContactsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // end button ---------------------------------------------------------------------------

        // populate activity with contacts ---------------------------------------------------------------------------

        database = FirebaseDatabase.getInstance();
        DbRef = database.getReference();
        studentDbRef = DbRef.child("students");
        ArrayList<String> currentContacts = new ArrayList<>();

        /*
        for (int i=1; i <= 20; i++) {
            currentContacts.add("Contact name " + i);
        }
        */

        ArrayList<String> otherContacts = new ArrayList<>();

        studentDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                    final boolean[] hasReadMessage = {true};
                    //Log.v("Firebase", studentSnapShot.child("first name").getValue().toString());
                    if (!studentSnapShot.child("username").getValue().toString().equalsIgnoreCase(username)) {
                            DatabaseReference messagesDbRef = FirebaseDatabase.getInstance().getReference().child("messages");
                            messagesDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    String date = "";
                                    String isOnline = "";
                                    for (DataSnapshot messageSnapshot : task.getResult().getChildren()) {
                                        String sender = messageSnapshot.child("sender").getValue().toString();
                                        String receiver = messageSnapshot.child("receiver").getValue().toString();
                                        //Log.v("sender/receiver", sender + " " + receiver + " " + studentSnapShot.child("username").getValue().toString());
                                        boolean incoming = sender.equalsIgnoreCase(studentSnapShot.child("username").getValue().toString()) && receiver.equalsIgnoreCase(username);
                                        boolean outgoing = receiver.equalsIgnoreCase(studentSnapShot.child("username").getValue().toString()) && sender.equalsIgnoreCase(username);
                                        if (incoming || outgoing) {
                                            //Log.v("timestamp", messageSnapshot.child("time sent").getValue().toString());
                                            currentlyMessaging[0] = true;
                                            date = messageSnapshot.child("time sent").getValue().toString();
                                            if (incoming && messageSnapshot.child("read status").getValue().toString().equalsIgnoreCase("false")) {
                                                hasReadMessage[0] = false;
                                            }
                                        }
                                        if (studentSnapShot.child("logged in").getValue() != null) {
                                            isOnline = studentSnapShot.child("logged in").getValue().toString();
                                        }
                                    }
                                    if (currentlyMessaging[0]) {
                                        //Log.v("isOnline in contactsactivity", isOnline+"");
                                        currentContacts.add(studentSnapShot.child("username").getValue().toString() + "`" + date + "`" + isOnline + "`" + hasReadMessage[0]);
                                    } else {
                                        if (studentSnapShot.child("course").getValue().toString().equalsIgnoreCase(course)) {
                                            //Log.v("isOnline in contactsactivity", isOnline+"");
                                            otherContacts.add(studentSnapShot.child("username").getValue().toString() + "`" + date + "`" + isOnline);
                                        }
                                    }
                                }
                            }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    //Log.v("current contacts size", currentContacts.size()+"");
                                    RecyclerView currentContactsRecycler = (RecyclerView) findViewById(R.id.currentContact);
                                    currentContactsRecycler.setLayoutManager(new LinearLayoutManager(ContactsActivity.this));
                                    Collections.sort(currentContacts, new CurrentContactsComparator());
                                    currentContactsAdapter = new CurrentContactsAdapter(ContactsActivity.this, currentContacts, true);
                                    currentContactsAdapter.setClickListener(ContactsActivity.this);
                                    currentContactsRecycler.setAdapter(currentContactsAdapter);
                                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(currentContactsRecycler.getContext(),1);
                                    currentContactsRecycler.addItemDecoration(dividerItemDecoration);

                                    RecyclerView otherContactsRecycler = (RecyclerView) findViewById(R.id.otherContacts);
                                    otherContactsRecycler.setLayoutManager(new LinearLayoutManager(ContactsActivity.this));
                                    Collections.sort(otherContacts);
                                    otherContactsAdapter = new OtherContactsAdapter(ContactsActivity.this, otherContacts);
                                    otherContactsAdapter.setClickListener(ContactsActivity.this);
                                    otherContactsRecycler.setAdapter(otherContactsAdapter);
                                    DividerItemDecoration dividerItemDecoration1 = new DividerItemDecoration(otherContactsRecycler.getContext(),1);
                                    otherContactsRecycler.addItemDecoration(dividerItemDecoration1);
                                }
                            });

                    }
                }

            }
        });
        // end populate activity with contacts---------------------------------------------------------------------------
    }

    @Override
    public void onItemClickCurrent(View view, int position) {
        //Toast.makeText(this, "You clicked " + currentContactsAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        //Log.v("View", view.getTransitionName());
        Intent intent = new Intent(ContactsActivity.this, MessengerActivity.class);
        intent.putExtra("Contact_Username", currentContactsAdapter.getItem(position));
        intent.putExtra("username", username);
        startActivity(intent);
    }
    @Override
    public void onItemClickOther(View view, int position) {
        //Toast.makeText(this, "You clicked " + otherContactsAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        //Log.v("View", view.getTransitionName());
        Intent intent = new Intent(ContactsActivity.this, MessengerActivity.class);
        intent.putExtra("Contact_Username", otherContactsAdapter.getItem(position));
        intent.putExtra("username", username);
        startActivity(intent);
    }
}