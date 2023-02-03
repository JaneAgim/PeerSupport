package edu.gatech.seclass.peersupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePasswordActivity extends AppCompatActivity {
    Bundle bundle;
    String username;
    DatabaseReference DbRef;
    DatabaseReference studentDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString();
        }

        DbRef = FirebaseDatabase.getInstance().getReference();
        studentDbRef = DbRef.child("students");

        Button button = (Button) findViewById(R.id.mainmenu);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
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
                                EditText password = findViewById(R.id.newPasswordBox);
                                EditText passwordAgain = findViewById(R.id.newPasswordAgainBox);

                                if (password.getText().toString().isEmpty()) {
                                    Toast.makeText(ChangePasswordActivity.this, "Please enter your new password", Toast.LENGTH_LONG).show();
                                } else if (password.getText().toString().equals(passwordAgain.getText().toString())) {
                                    DatabaseReference specificRef = studentSnapShot.child("password").getRef();
                                    specificRef.setValue(password.getText().toString());
                                    Toast.makeText(ChangePasswordActivity.this, "Your password has been reset", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(ChangePasswordActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                                }


                            }
                        }
                    }
                });
            }
        });
    }
}