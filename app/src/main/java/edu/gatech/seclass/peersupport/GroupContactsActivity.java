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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class GroupContactsActivity extends AppCompatActivity implements GroupContactsAdapter.ItemClickListener{
    GroupContactsAdapter groupContactsAdapter;
    String username;
    List<List<String>> groups;
    List<String> groupContacts;
    Bundle bundle;
    DatabaseReference groupMessagesDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_contacts);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString();
        }

        // button to take user to main menu ---------------------------------------------------------------------------
        Button button = (Button) findViewById(R.id.mainmenu);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(GroupContactsActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // end button ---------------------------------------------------------------------------

        // button to take user to add contacts to group ---------------------------------------------------------------------------
        FloatingActionButton button1 = (FloatingActionButton) findViewById(R.id.addToGroupButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(GroupContactsActivity.this, SelectFirstGroupContactActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // end button ---------------------------------------------------------------------------

        groupContacts = new ArrayList<>();
        groups = new ArrayList<>();

        groupMessagesDbRef = FirebaseDatabase.getInstance().getReference().child("group messages");
        groupMessagesDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String date = "";
                for (DataSnapshot messageSnapshot : task.getResult().getChildren()) {
                    String sender = messageSnapshot.child("sender").getValue().toString();
                    String receiver_1 = messageSnapshot.child("receiver_1").getValue().toString();
                    String receiver_2 = messageSnapshot.child("receiver_2").getValue().toString();
                    //Log.v("sender/receiver", sender + " " + receiver + " " + studentSnapShot.child("username").getValue().toString());
                    boolean incoming = receiver_1.equalsIgnoreCase(username) || receiver_2.equalsIgnoreCase(username);
                    boolean outgoing = sender.equalsIgnoreCase(username);
                    if (incoming || outgoing) {
                        date = messageSnapshot.child("time sent").getValue().toString();
                        if (receiver_1.equalsIgnoreCase(username)) {
                            List<String> group = new ArrayList<>();
                            group.add(receiver_2.toLowerCase());
                            group.add(sender.toLowerCase());
                            Collections.sort(group);
                            if (groups.contains(group)) {
                                //groups.add(group);
                                for (String contact : groupContacts) {
                                    if (contact.contains(receiver_2 + "`" + sender + "`")) {
                                        groupContacts.set(groupContacts.indexOf(contact), receiver_2 + "`" + sender + "`" + date);
                                    } else if (contact.contains(sender + "`" + receiver_2 + "`")) {
                                        groupContacts.set(groupContacts.indexOf(contact), sender + "`" + receiver_2 + "`" + date);
                                    }
                                }
                                //groupContacts.add(receiver_2 + "`" + sender + "`" + date);
                            } else {
                                groups.add(group);
                                groupContacts.add(receiver_2 + "`" + sender + "`" + date);
                            }
                        } else if (receiver_2.equalsIgnoreCase(username)) {
                            List<String> group = new ArrayList<>();
                            group.add(receiver_1.toLowerCase());
                            group.add(sender.toLowerCase());
                            Collections.sort(group);
                            if (groups.contains(group)) {
                                //groups.add(group);
                                for (String contact : groupContacts) {
                                    if (contact.contains(receiver_1 + "`" + sender + "`")) {
                                        groupContacts.set(groupContacts.indexOf(contact), receiver_1 + "`" + sender + "`" + date);
                                    } else if (contact.contains(sender + "`" + receiver_1 + "`")) {
                                        groupContacts.set(groupContacts.indexOf(contact), sender + "`" + receiver_1 + "`" + date);
                                    }
                                }
                                //groupContacts.add(receiver_2 + "`" + sender + "`" + date);
                            } else {
                                groups.add(group);
                                groupContacts.add(receiver_1 + "`" + sender + "`" + date);
                            }
                        } else {
                            List<String> group = new ArrayList<>();
                            group.add(receiver_1.toLowerCase());
                            group.add(receiver_2.toLowerCase());
                            Collections.sort(group);
                            if (groups.contains(group)) {
                                //groups.add(group);
                                for (String contact : groupContacts) {
                                    if (contact.contains(receiver_1 + "`" + receiver_2 + "`")) {
                                        groupContacts.set(groupContacts.indexOf(contact), receiver_1 + "`" + receiver_2 + "`" + date);
                                    } else if (contact.contains(receiver_2 + "`" + receiver_1 + "`")) {
                                        groupContacts.set(groupContacts.indexOf(contact), receiver_2 + "`" + receiver_1 + "`" + date);
                                    }
                                }
                                //groupContacts.add(receiver_2 + "`" + sender + "`" + date);
                            } else {
                                groups.add(group);
                                groupContacts.add(receiver_1 + "`" + receiver_2 + "`" + date);
                            }
                        }
                    }
                }

            }
        }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                //Log.v("current contacts size", currentContacts.size()+"");
                RecyclerView groupContactsRecycler = (RecyclerView) findViewById(R.id.groupContacts);
                groupContactsRecycler.setLayoutManager(new LinearLayoutManager(GroupContactsActivity.this));
                Collections.sort(groupContacts, new GroupContactsComparator());
                /*Set groupContactsSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                groupContactsSet.addAll(groupContacts);
                for (Object string : groupContactsSet) {

                }*/
                groupContactsAdapter = new GroupContactsAdapter(GroupContactsActivity.this, groupContacts);
                groupContactsAdapter.setClickListener(GroupContactsActivity.this);
                groupContactsRecycler.setAdapter(groupContactsAdapter);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(groupContactsRecycler.getContext(),1);
                groupContactsRecycler.addItemDecoration(dividerItemDecoration);
            }
        });
    }


    @Override
    public void onItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + currentContactsAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        //Log.v("View", view.getTransitionName());
        Intent intent = new Intent(GroupContactsActivity.this, MessengerActivity.class);
        intent.putExtra("Group_Info", groupContactsAdapter.getItem(position));
        intent.putExtra("username", username);
        startActivity(intent);
    }
}

