package edu.gatech.seclass.peersupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    String username;
    EditText usernameTextView;
    EditText passwordTextView;
    String email;
    String password;
    DatabaseReference DbRef;
    DatabaseReference studentDbRef;
    Date currentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createNotificationChannel();
        DbRef = FirebaseDatabase.getInstance().getReference();
        studentDbRef = DbRef.child("students");
        usernameTextView = ((EditText) findViewById(R.id.username));
        passwordTextView = ((EditText) findViewById(R.id.password));
        currentDate = Calendar.getInstance().getTime();


        Button button = (Button) findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });


        // if a name is input, sign in with that name ---------------------------------------------------------------------------
        Button button1 = (Button) findViewById(R.id.signin);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                studentDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot dataSnapshot = task.getResult();
                        for (DataSnapshot studentSnapShot : dataSnapshot.getChildren()) {
                            boolean usernameCorrect = studentSnapShot.child("username").getValue().toString().equalsIgnoreCase(usernameTextView.getText().toString()) || studentSnapShot.child("email").getValue().toString().equalsIgnoreCase(usernameTextView.getText().toString());
                            boolean passwordCorrect = studentSnapShot.child("password").getValue().toString().equalsIgnoreCase(passwordTextView.getText().toString());
                            if (usernameCorrect && passwordCorrect) {
                                DatabaseReference ref = studentSnapShot.getRef();
                                ref.child("logged in").setValue(true);
                                username = usernameTextView.getText().toString().trim().toLowerCase();
                                //Log.v("username", username);
                                password = passwordTextView.toString().trim();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("username", username);

                                // for notifications ---------------------------------------------------------------------------
                                DatabaseReference messagesDbRef = FirebaseDatabase.getInstance().getReference().child("messages");
                                messagesDbRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        DbRef.child("messages_next_ID").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                int[] mostRecentMessageID = {Integer.parseInt(task.getResult().getValue().toString()) - 1};
                                                DataSnapshot messageSnapshot = dataSnapshot.child(mostRecentMessageID[0]+"");
                                                //Log.v("message snapshot key", messageSnapshot.getKey());
                                                String dateStr = messageSnapshot.child("time sent").getValue().toString();
                                                Date dateInputted;
                                                boolean isAfterCurrentTime = false;
                                                try {
                                                    dateInputted = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(dateStr);
                                                    isAfterCurrentTime = (currentDate.before(dateInputted));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                if (isAfterCurrentTime) {
                                                    String sender = messageSnapshot.child("sender").getValue().toString();
                                                    //Log.v("data", messageSnapshot.toString());
                                                    String receiver = messageSnapshot.child("receiver").getValue().toString();
                                                    boolean incoming = receiver.equalsIgnoreCase(username);
                                                    if (incoming) {
                                                        Intent intent = new Intent(LoginActivity.this, ContactsActivity.class);
                                                        intent.putExtra("notification_info", "Click on " + sender + "'s chat to view new message");
                                                        intent.putExtra("username", username);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        PendingIntent pendingIntent = PendingIntent.getActivity(LoginActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                        final String CHANNEL_ID = "otherMessages";
                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(LoginActivity.this, CHANNEL_ID)
                                                                .setSmallIcon(com.google.firebase.database.R.drawable.common_full_open_on_phone)
                                                                .setContentTitle(sender)
                                                                .setContentText("A new message from " + sender)
                                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                                .setContentIntent(pendingIntent)
                                                                .setAutoCancel(true);
                                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(LoginActivity.this);
                                                        notificationManager.notify(0, builder.build());
                                                        //Log.v("waiting", "done");
                                                    }
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // ...
                                    }
                                });
                                // notifications end ---------------------------------------------------------------------------
                                startActivity(intent);
                                break;
                            }
                        }
                        if (username == null || password == null) {
                            Toast.makeText(LoginActivity.this, "Username or password is incorrect. \n                Please try again.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });
        // end input name ---------------------------------------------------------------------------

        /*// test button for developing purposes ---------------------------------------------------------------------------
        Button button2 = (Button) findViewById(R.id.test);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                username = "apurva.kulkarni";
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // end test button ---------------------------------------------------------------------------*/
    }

    @Override
    public void onBackPressed() {
        studentDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot studentSnapshot : task.getResult().getChildren()) {
                    if (studentSnapshot.child("username").getValue().toString().equalsIgnoreCase(username)) {
                        DatabaseReference ref = studentSnapshot.getRef();
                        ref.child("logged in").setValue(false);
                    }
                }
            }
        });
        finish();
    }


    private void createNotificationChannel() {
        final String CHANNEL_ID = "otherMessages";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Messages From Students";
            String description = "This channel concerns messages from students in your course you have not been matched with.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}