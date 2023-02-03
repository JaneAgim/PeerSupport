package edu.gatech.seclass.peersupport;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class SurveyCompletionActivity extends AppCompatActivity implements OtherContactsAdapter.ItemClickListener{
    String username;
    Bundle bundle;
    HashMap<String, String> matches;
    DatabaseReference dbRef;
    OtherContactsAdapter surveyContactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_completion);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString();
        }

        // button to take user to main menu ---------------------------------------------------------------------------
        Button button = (Button) findViewById(R.id.mainmenu);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SurveyCompletionActivity.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        // end button ---------------------------------------------------------------------------

        ArrayList<String> suggestions = (ArrayList<String>) getIntent().getSerializableExtra("suggestions");
        Log.v("suggestions in survey completion activity", suggestions.toString());

        RecyclerView recommendedStudentsRecycler = (RecyclerView) findViewById(R.id.recommendedPeopleList);
        recommendedStudentsRecycler.setLayoutManager(new LinearLayoutManager(SurveyCompletionActivity.this));
        surveyContactsAdapter = new OtherContactsAdapter(SurveyCompletionActivity.this, suggestions);
        surveyContactsAdapter.setClickListener(SurveyCompletionActivity.this);
        recommendedStudentsRecycler.setAdapter(surveyContactsAdapter);
        DividerItemDecoration dividerItemDecoration1 = new DividerItemDecoration(recommendedStudentsRecycler.getContext(),1);
        recommendedStudentsRecycler.addItemDecoration(dividerItemDecoration1);
        /*for(String student : suggestions){

        }*/

        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("students").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    if(studentSnapshot.child("username").getValue().toString().equalsIgnoreCase(username)){
                        DatabaseReference specificstudentDBRef = studentSnapshot.getRef();
                        specificstudentDBRef.child("matched to").setValue(suggestions);
                    }
                }
            }
        });

    }

    @Override
    public void onItemClickOther(View view, int position) {
        //Toast.makeText(this, "You clicked " + otherContactsAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        //Log.v("View", view.getTransitionName());
        Intent intent = new Intent(SurveyCompletionActivity.this, MessengerActivity.class);
        intent.putExtra("Contact_Username", surveyContactsAdapter.getItem(position));
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
