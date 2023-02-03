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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessengerActivity extends AppCompatActivity {
    MessageAdapter messageAdapter;
    String username;
    String contactUsername;
    String contactUsernameTwo;
    boolean isGroupChat;
    Bundle bundle;
    TextView name;
    DatabaseReference messagesDbRef;
    DatabaseReference groupMessagesDbRef;
    DatabaseReference DbRef;
    TextInputEditText textInputEditText;
    ValueEventListener populateMessagesListener;
//    private File privateRootDir;
//    private File imagesDir;
//    private File pdfDir;
//    File[] imageFiles;
//    String[] imageFilenames;
//    File[] pdfFiles;
//    Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString();
        }

        //Code for file sharing
//        resultIntent =
//                new Intent("edu.gatech.seclass.peersupport.MessengerActivity.ACTION_RETURN_FILE");
//        privateRootDir = getFilesDir();
//        imagesDir = new File(privateRootDir, "images");
//        pdfDir = new File(privateRootDir, "application/pdf");
//        imageFiles = imagesDir.listFiles();
//        pdfFiles = pdfDir.listFiles();
//        // Set the Activity's result to null to begin with
//        setResult(Activity.RESULT_CANCELED, null); //Change this later maybe



        // set title of activity to contact name ---------------------------------------------------------------------------
        name = (TextView) findViewById(R.id.contactName);

        //contactName = names[1] + " " +names[2];
        if (bundle.get("Group_Info") != null) {
            String[] group_info = bundle.get("Group_Info").toString().split("`");
            contactUsername = group_info[0];
            contactUsernameTwo = group_info[1];
            name.setText(contactUsername + ", " + contactUsernameTwo);
            isGroupChat = true;
        } else {
            String[] names = bundle.get("Contact_Username").toString().split("`");
            contactUsername = names[0];
            name.setText(contactUsername);
            isGroupChat = false;
        }
        // end contact name title ---------------------------------------------------------------------------

        // button to take user to contacts ---------------------------------------------------------------------------
        Button button = (Button) findViewById(R.id.contacts);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MessengerActivity.this, ContactsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // end contacts button ---------------------------------------------------------------------------

        // button to take user to main menu ---------------------------------------------------------------------------
        Button button1 = (Button) findViewById(R.id.main);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent intent = new Intent(MessengerActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // end main menu button ---------------------------------------------------------------------------

        // for notifications ---------------------------------------------------------------------------
        messagesDbRef = FirebaseDatabase.getInstance().getReference().child("messages");
        /*messagesDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String sender = messageSnapshot.child("sender").getValue().toString();
                    String receiver = messageSnapshot.child("receiver").getValue().toString();
                    boolean incoming = receiver.equalsIgnoreCase(username);
                    String messageContents = messageSnapshot.child("message contents").getValue().toString();
                    if (incoming) {
                        Intent intent = new Intent(MessengerActivity.this, ContactsActivity.class);
                        intent.putExtra("notification_info", "Click on " + sender + "'s chat to view new message");
                        intent.putExtra("username", username);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(MessengerActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        final String CHANNEL_ID = "otherMessages";
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MessengerActivity.this, CHANNEL_ID)
                                .setSmallIcon(com.google.firebase.database.R.drawable.common_full_open_on_phone)
                                .setContentTitle(sender)
                                .setContentText(messageContents)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MessengerActivity.this);
                        notificationManager.notify(0, builder.build());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });*/
        // notifications end ---------------------------------------------------------------------------

        // populate messages ---------------------------------------------------------------------------
        if (!isGroupChat) {
            populateMessagesListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> messages = new ArrayList<>();
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        String sender = messageSnapshot.child("sender").getValue().toString();
                        String receiver = messageSnapshot.child("receiver").getValue().toString();
                        boolean incoming = receiver.equalsIgnoreCase(username) && sender.equalsIgnoreCase(contactUsername);
                        boolean outgoing = receiver.equalsIgnoreCase(contactUsername) && sender.equalsIgnoreCase(username);
                        if (incoming || outgoing) {
                            String messageContents = messageSnapshot.child("message contents").getValue().toString();
                            String entireDate = messageSnapshot.child("time sent").getValue().toString();
                            String[] entireDateArray = new String[6];
                            try {
                                Date dateSent = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(entireDate);
                                Log.v("date in messenger activity", dateSent.toString());
                                entireDateArray = dateSent.toString().split(" ");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String day = entireDateArray[2];
                            String month = entireDateArray[1];
                            String year = entireDateArray[5];
                            String[] timeArray = entireDateArray[3].split(":");
                            //Log.v("time array", entireDateArray[3]);
                            String hour = timeArray[0];
                            String minute = timeArray[1];
                            if (minute.length() == 1) {
                                minute = "0" + minute;
                            }
                            if (outgoing) {
                                //sender = "Me";
                                messages.add(messageContents + "`~`~`~`~`~`~`~`~" + "Me" + "`~`~`~`~`~`~`~`~" + month + " " + day + ", " + year + " " + hour + ":" + minute);
                            } else {
                                messages.add(messageContents + "`~`~`~`~`~`~`~`~" + contactUsername + "`~`~`~`~`~`~`~`~" + month + " " + day + ", " + year + " " + hour + ":" + minute);
                                DatabaseReference ref = messageSnapshot.getRef();
                                ref.child("read status").setValue("true");
                            }

                        }
                    }
                    RecyclerView messageRecycler = (RecyclerView) findViewById(R.id.list_of_messages);
                    messageRecycler.setLayoutManager(new LinearLayoutManager(MessengerActivity.this));
                    messageAdapter = new MessageAdapter(MessengerActivity.this, messages);
                    messageRecycler.setAdapter(messageAdapter);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(messageRecycler.getContext(), 1);
                    messageRecycler.addItemDecoration(dividerItemDecoration);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            };
            messagesDbRef.addValueEventListener(populateMessagesListener);

            // end populate messages ---------------------------------------------------------------------------

            // add messages to database upon hitting send button---------------------------------------------------------------------------
            DbRef = FirebaseDatabase.getInstance().getReference();

            FloatingActionButton button2 = findViewById(R.id.sendButtonMessenger);
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DbRef.child("messages_next_ID").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            int[] iD = {Integer.parseInt(task.getResult().getValue().toString())};
                            DatabaseReference ref = DbRef.child("messages").child(iD[0] + "");
                            Map<String, String> message = new HashMap<>();
                            message.put("sender", username);
                            message.put("receiver", contactUsername);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                            message.put("time sent", calendar.getTime() + "");
                            textInputEditText = findViewById(R.id.input);
                            message.put("message contents", textInputEditText.getText().toString());
                            message.put("read status", false+"");
                            ref.setValue(message);
                            iD[0]++;
                            DbRef.child("messages_next_ID").setValue(iD[0] + "");
                            textInputEditText.setText("");
                        }
                    });
                }
            });
            //-------------------------------Sending Files------------------------------------------------
            FloatingActionButton sendFilesButton = findViewById(R.id.sendFileButton);
            sendFilesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MessengerActivity.this, FileSelectActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("Contact_Username", contactUsername);
                    if (contactUsernameTwo != null) {
                        intent.putExtra("Contact_Username_Two", contactUsernameTwo);
                    }
                    Log.v("Going into ", "file activity");
                    startActivity(intent);
                }
            });
        } else {
            groupMessagesDbRef = FirebaseDatabase.getInstance().getReference().child("group messages");
            groupMessagesDbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> messages = new ArrayList<>();
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        String sender = messageSnapshot.child("sender").getValue().toString();
                        String receiver_1 = messageSnapshot.child("receiver_1").getValue().toString();
                        String receiver_2 = messageSnapshot.child("receiver_2").getValue().toString();
                        boolean incoming = (receiver_1.equalsIgnoreCase(username) && sender.equalsIgnoreCase(contactUsername) && receiver_2.equalsIgnoreCase(contactUsernameTwo)) ||
                                (receiver_1.equalsIgnoreCase(contactUsernameTwo) && sender.equalsIgnoreCase(contactUsername) && receiver_2.equalsIgnoreCase(username)) ||
                                (receiver_1.equalsIgnoreCase(username) && sender.equalsIgnoreCase(contactUsernameTwo) && receiver_2.equalsIgnoreCase(contactUsername)) ||
                                (receiver_1.equalsIgnoreCase(contactUsername) && sender.equalsIgnoreCase(contactUsernameTwo) && receiver_2.equalsIgnoreCase(username));
                        boolean outgoing = receiver_1.equalsIgnoreCase(contactUsername) && sender.equalsIgnoreCase(username) && receiver_2.equalsIgnoreCase(contactUsernameTwo) ||
                                            (receiver_1.equalsIgnoreCase(contactUsernameTwo) && sender.equalsIgnoreCase(username) && receiver_2.equalsIgnoreCase(contactUsername));
                        if (incoming || outgoing) {
                            String messageContents = messageSnapshot.child("message contents").getValue().toString();
                            String entireDate = messageSnapshot.child("time sent").getValue().toString();
                            String[] entireDateArray = new String[6];
                            try {
                                Date dateSent = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(entireDate);
                                Log.v("date in messenger activity", dateSent.toString());
                                entireDateArray = dateSent.toString().split(" ");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String day = entireDateArray[2];
                            String month = entireDateArray[1];
                            String year = entireDateArray[5];
                            String[] timeArray = entireDateArray[3].split(":");
                            //Log.v("time array", entireDateArray[3]);
                            String hour = timeArray[0];
                            String minute = timeArray[1];
                            if (minute.length() == 1) {
                                minute = "0" + minute;
                            }
                            if (outgoing) {
                                //sender = "Me";
                                messages.add(messageContents + "`~`~`~`~`~`~`~`~" + "Me" + "`~`~`~`~`~`~`~`~" + month + " " + day + ", " + year + " " + hour + ":" + minute);
                            } else {
                                messages.add(messageContents + "`~`~`~`~`~`~`~`~" + sender + "`~`~`~`~`~`~`~`~" + month + " " + day + ", " + year + " " + hour + ":" + minute);
                            }

                        }
                    }
                    RecyclerView messageRecycler = (RecyclerView) findViewById(R.id.list_of_messages);
                    messageRecycler.setLayoutManager(new LinearLayoutManager(MessengerActivity.this));
                    messageAdapter = new MessageAdapter(MessengerActivity.this, messages);
                    messageRecycler.setAdapter(messageAdapter);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(messageRecycler.getContext(), 1);
                    messageRecycler.addItemDecoration(dividerItemDecoration);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });

            // end populate messages ---------------------------------------------------------------------------

            // add messages to database upon hitting send button---------------------------------------------------------------------------
            DbRef = FirebaseDatabase.getInstance().getReference();

            FloatingActionButton button2 = findViewById(R.id.sendButtonMessenger);
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DbRef.child("group_messages_next_ID").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            int[] iD = {Integer.parseInt(task.getResult().getValue().toString())};
                            Map<String, String> message = new HashMap<>();
                            message.put("sender", username);
                            message.put("receiver_1", contactUsername);
                            message.put("receiver_2", contactUsernameTwo);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                            message.put("time sent", calendar.getTime() + "");
                            textInputEditText = findViewById(R.id.input);
                            message.put("message contents", textInputEditText.getText().toString());
                            groupMessagesDbRef.child(iD[0]+"").setValue(message);
                            iD[0]++;
                            DbRef.child("group_messages_next_ID").setValue(iD[0] + "");
                            textInputEditText.setText("");
                        }
                    });
                }
            });

            //-------------------------------Sending Files------------------------------------------------
            FloatingActionButton sendFilesButton = findViewById(R.id.sendFileButton);
            sendFilesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MessengerActivity.this, FileSelectActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("Contact_Username", contactUsername);
                    if (contactUsernameTwo != null) {
                        intent.putExtra("Contact_Username_Two", contactUsernameTwo);
                    }
                    Log.v("Going into ", "file activity");
                    startActivity(intent);
                }
            });
        }

        // add messages end ---------------------------------------------------------------------------

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (contactUsernameTwo == null) {
            Log.v("onStop", "removed that ish");
            messagesDbRef.removeEventListener(populateMessagesListener);
        }

    }
    /*public String findLastName(String user) {
        final String[] lastName = {null};
        DatabaseReference studentDbRef = FirebaseDatabase.getInstance().getReference().child("students");
        studentDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    if (dataSnapshot.child("username").toString().equalsIgnoreCase(user)) {
                        lastName[0] = dataSnapshot.child("last name").toString();
                    }
                }
            }
        });
        return lastName[0];
    }*/
}