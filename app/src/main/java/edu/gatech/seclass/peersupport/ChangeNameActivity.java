package edu.gatech.seclass.peersupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.view.Change;

public class ChangeNameActivity extends AppCompatActivity {
    Bundle bundle;
    String username;
    DatabaseReference DbRef;
    DatabaseReference studentDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString();
        }

        DbRef = FirebaseDatabase.getInstance().getReference();
        studentDbRef = DbRef.child("students");

        Button button = (Button) findViewById(R.id.mainmenu);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ChangeNameActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        Button button1 = (Button) findViewById(R.id.submitButtonChange);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                studentDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot dataSnapshot = task.getResult();
                        for (DataSnapshot studentSnapShot : dataSnapshot.getChildren()) {
                            if (studentSnapShot.child("username").getValue().toString().equalsIgnoreCase(username)) {
                                EditText firstName = findViewById(R.id.first_name_answer);
                                EditText lastName = findViewById(R.id.last_name_answer);

                                if (firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty()) {
                                    Toast.makeText(ChangeNameActivity.this, "Please enter information for all fields", Toast.LENGTH_LONG).show();
                                } else {
                                    DatabaseReference specificRef = studentSnapShot.child("first name").getRef();
                                    specificRef.setValue(firstName.getText().toString());
                                    specificRef = studentSnapShot.child("last name").getRef();
                                    specificRef.setValue(lastName.getText().toString());

                                    Toast.makeText(ChangeNameActivity.this, "Your name has been changed to " + firstName.getText().toString() + " " + lastName.getText().toString(), Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(ChangeNameActivity.this, SettingsActivity.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                });
            }
        });
    }
}