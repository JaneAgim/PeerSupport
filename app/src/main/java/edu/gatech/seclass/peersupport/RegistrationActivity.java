package edu.gatech.seclass.peersupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    EditText firstNameTextView;
    EditText lastNameTextView;
    EditText usernameTextView;
    EditText emailTextView;
    EditText passwordTextView;
    EditText confirmPasswordTextView;
    DatabaseReference DbRef;
    DatabaseReference studentDbRef;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        DbRef = FirebaseDatabase.getInstance().getReference();
        studentDbRef = DbRef.child("students");
        firstNameTextView = (EditText) findViewById(R.id.firstNameRegister);
        lastNameTextView = (EditText) findViewById(R.id.lastNameRegister);
        usernameTextView = (EditText) findViewById(R.id.usernameRegister);
        emailTextView = (EditText) findViewById(R.id.emailRegister);
        passwordTextView = (EditText) findViewById(R.id.passwordRegister);
        confirmPasswordTextView = (EditText) findViewById(R.id.confirmPasswordRegister);

        // button to register ---------------------------------------------------------------------------
        Button button1 = (Button) findViewById(R.id.register2);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DbRef.child("students_next_ID").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> iDTask) {
                        final boolean[] usernameExists = new boolean[1];
                        final boolean[] emailExists = new boolean[1];
                        boolean invalidCharacterExists = usernameTextView.getText().toString().contains("`");
                        studentDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                DataSnapshot dataSnapshot = task.getResult();
                                for (DataSnapshot studentSnapShot : dataSnapshot.getChildren()) {
                                    usernameExists[0] = studentSnapShot.child("username").getValue().toString().equalsIgnoreCase(usernameTextView.getText().toString());
                                    emailExists[0] = studentSnapShot.child("email").getValue().toString().equalsIgnoreCase(emailTextView.getText().toString());
                                    break;
                                }
                            }
                        }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!usernameExists[0] && !emailExists[0] && !invalidCharacterExists) {
                                    if (passwordTextView.getText().toString().equals(confirmPasswordTextView.getText().toString())) {
                                        int[] iD = {Integer.parseInt(iDTask.getResult().getValue().toString())};
                                        DatabaseReference ref = studentDbRef.child(iD[0]+"");
                                        Map<String, String> student = new HashMap<>();
                                        student.put("first name", firstNameTextView.getText().toString());
                                        student.put("last name", lastNameTextView.getText().toString());
                                        student.put("username", usernameTextView.getText().toString().toLowerCase());
                                        student.put("email", emailTextView.getText().toString());
                                        student.put("password", passwordTextView.getText().toString());
                                        student.put("course", "No course added yet");
                                        student.put("logged in", "true");
                                        ref.setValue(student);
                                        iD[0]++;
                                        DbRef.child("students_next_ID").setValue(iD[0]+"");
                                        username = usernameTextView.getText().toString().toLowerCase();
                                        Intent intent = new Intent(RegistrationActivity.this, CourseActivity.class);
                                        intent.putExtra("username", username);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                                    }
                                } else if (usernameExists[0]) {
                                    Toast.makeText(RegistrationActivity.this, "Username already exists", Toast.LENGTH_LONG).show();
                                } else if (emailExists[0]){
                                    Toast.makeText(RegistrationActivity.this, "Email already exists", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(RegistrationActivity.this, "Backtick character (`) not allowed in username", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }
                });

            }
        });
        // end registration button ---------------------------------------------------------------------------

        // button to cancel registration ---------------------------------------------------------------------------
        Button button = (Button) findViewById(R.id.cancel);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        // end cancel button ---------------------------------------------------------------------------
    }
}