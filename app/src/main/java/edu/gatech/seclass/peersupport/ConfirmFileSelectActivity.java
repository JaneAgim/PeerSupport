package edu.gatech.seclass.peersupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ConfirmFileSelectActivity extends AppCompatActivity {
    TextView sendToTextView;
    ImageView confirmImageView;
    Bundle bundle;
    String username; // sender
    String contactUsername; // receiver
    FirebaseStorage storage;
    DatabaseReference DbRef;
    DatabaseReference groupMessagesDbRef;
    String contactUsernameTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_file_select);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString().toLowerCase();
        }

        // button to take user to main menu ---------------------------------------------------------------------------
        Button mainButton = (Button) findViewById(R.id.mainmenu);
        mainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmFileSelectActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // end button ---------------------------------------------------------------------------


        DbRef = FirebaseDatabase.getInstance().getReference();
        String[] names = bundle.get("Contact_Username").toString().split("`");
        contactUsername = names[0];

        sendToTextView = (TextView) findViewById(R.id.sendToTextView);
        sendToTextView.setText("Send this image to " + contactUsername + "?");
        if (bundle.get("Contact_Username_Two") != null) {
            contactUsernameTwo = bundle.get("Contact_Username_Two").toString();
            sendToTextView.setText("Send this image to " + contactUsername + " and " + contactUsernameTwo +"?");
        }
        confirmImageView = (ImageView) findViewById(R.id.confirmImage);

        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String uriString = bundle.get("uri").toString();
        //Uri uri = Uri.fromFile(new File(filename));
        Uri uri = Uri.parse(uriString);
        Log.v("uri in confirm", uri.toString());
        confirmImageView.setImageURI(uri);

        Button button = (Button) findViewById(R.id.yesButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (contactUsernameTwo != null) {
                    storageRef.child("PeerSupport_images").child(contactUsername + "/from_" + username + "/" + uri.getLastPathSegment()).putFile(uri);
                    storageRef.child("PeerSupport_images").child(contactUsernameTwo + "/from_" + username + "/" + uri.getLastPathSegment()).putFile(uri);
                    groupMessagesDbRef = FirebaseDatabase.getInstance().getReference().child("group messages");
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
                            message.put("message contents", username + " sent an image file: " + uri.getLastPathSegment()+"\n\nGo to files in main menu to receive.");
                            groupMessagesDbRef.child(iD[0]+"").setValue(message);
                            iD[0]++;
                            DbRef.child("group_messages_next_ID").setValue(iD[0] + "");
                        }
                    });
                } else {
                    storageRef.child("PeerSupport_images").child(contactUsername + "/from_" + username + "/" + uri.getLastPathSegment()).putFile(uri);
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
                            message.put("message contents", username + " sent an image file: " + uri.getLastPathSegment()+"\n\nGo to files in main menu to receive.");
                            message.put("read status", false+"");
                            ref.setValue(message);
                            iD[0]++;
                            DbRef.child("messages_next_ID").setValue(iD[0] + "");
                        }
                    });
                }

                Intent intent = new Intent(ConfirmFileSelectActivity.this, MessengerActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("Contact_Username", contactUsername);
                if (contactUsernameTwo != null) {
                    intent.putExtra("Group_Info", contactUsername + "`" + contactUsernameTwo);
                }
                startActivity(intent);
            }
        });

        Button button1 = (Button) findViewById(R.id.noButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmFileSelectActivity.this, FileSelectActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("Contact_Username", contactUsername);
                if (contactUsernameTwo != null) {
                    intent.putExtra("Contact_Username_Two", contactUsernameTwo);
                }
                startActivity(intent);
            }
        });
    }
}