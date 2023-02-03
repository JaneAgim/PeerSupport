package edu.gatech.seclass.peersupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SurveyActivityPageTwo extends AppCompatActivity {
    HashMap<String, String> answers;
    String username;
    String currentCourse;
    ArrayList<String> suggestions;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_page_two);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString();
        }
        answers = (HashMap<String, String>)getIntent().getSerializableExtra("answers");
        Log.v("hashmap", answers.toString());

        suggestions = new ArrayList<>();


        Button button = (Button) findViewById(R.id.mainmenu);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SurveyActivityPageTwo.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        TextView q8Question = findViewById(R.id.question_8);
        q8Question.setMovementMethod(new ScrollingMovementMethod());
        EditText q8 = findViewById(R.id.q8AnswerBox);
        EditText q9 = findViewById(R.id.q9AnswerBox);

        RadioGroup q10 = findViewById(R.id.radioGroup10);
        RadioButton q10_follows = findViewById(R.id.followsOthersAnswers);
        RadioButton q10_leads = findViewById(R.id.leadsOthersAnswer);

        RadioGroup q11 = findViewById(R.id.radioGroup11);
        RadioButton q11_goodGrade = findViewById(R.id.goodGradeAnswer);
        RadioButton q11_learningMaterial = findViewById(R.id.learningMaterialAnswer);
        RadioButton q11_other= findViewById(R.id.q11OtherAnswer);

        RadioGroup q12 = findViewById(R.id.radioGroup12);
        RadioButton q12_rarelyNever = findViewById(R.id.rarelyNeverAnswer);
        RadioButton q12_moderately = findViewById(R.id.moderatelyAnswer);
        RadioButton q12_extremely= findViewById(R.id.extremelyAnswer);

        RadioGroup q13 = findViewById(R.id.radioGroup13);
        RadioButton q13_individualPlayer = findViewById(R.id.individualPlayerAnswer);
        RadioButton q13_teamPlayer = findViewById(R.id.teamPlayerAnswer);

        EditText q14 = findViewById(R.id.q14AnswerBox);
        TextView q14Question = findViewById(R.id.question_14);
        q14Question.setMovementMethod(new ScrollingMovementMethod());

        Button submitButton = (Button) findViewById(R.id.submitButtonSurvey);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean missingAnswer = false;

                if(!q8.getText().toString().equals("")){
                    answers.put("q8", q8.getText().toString());
                } else {
                    //missingAnswer = true;
                }
                if(!q9.getText().toString().equals("")){
                    answers.put("q9", q9.getText().toString());
                } else {
                    //missingAnswer = true;
                }


                if (q10.getCheckedRadioButtonId() == q10_follows.getId()) {
                    answers.put("q10","follows others");
                } else if (q10.getCheckedRadioButtonId() == q10_leads.getId()) {
                    answers.put("q10","leads others");
                } else {
                    missingAnswer = true;
                }

                if (q11.getCheckedRadioButtonId() == q11_goodGrade.getId()) {
                    answers.put("q11","Good Grade");
                } else if (q11.getCheckedRadioButtonId() == q11_learningMaterial.getId()) {
                    answers.put("q11","Learning Material");
                } else if (q11.getCheckedRadioButtonId() == q11_other.getId()) {
                    answers.put("q11","Other");
                }
                else {
                    missingAnswer = true;
                }

                if (q12.getCheckedRadioButtonId() == q12_rarelyNever.getId()) {
                    answers.put("q12","Rarely/Never");
                } else if (q12.getCheckedRadioButtonId() == q12_moderately.getId()) {
                    answers.put("q12","Moderately");
                } else if (q12.getCheckedRadioButtonId() == q12_extremely.getId()) {
                    answers.put("q12","Extremely");
                }
                else {
                    missingAnswer = true;
                }

                if (q13.getCheckedRadioButtonId() == q13_individualPlayer.getId()) {
                    answers.put("q13","Individual Player");
                } else if (q13.getCheckedRadioButtonId() == q13_teamPlayer.getId()) {
                    answers.put("q13","Team Player");
                } else {
                    missingAnswer = true;
                }

                answers.put("q14", q14.getText().toString());

                if(!missingAnswer){

                    DatabaseReference studentDbRef = FirebaseDatabase.getInstance().getReference().child("students");
                    studentDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot dataSnapshot = task.getResult();

                            for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                                if (studentSnapshot.child("username").getValue().toString().equalsIgnoreCase(username)) {
                                    DatabaseReference specificRef = studentSnapshot.getRef();
                                    //Log.v("Going", "HERE");
                                    currentCourse = studentSnapshot.child("course").getValue().toString();
                                    specificRef.child("survey responses").setValue(answers);
                                    generateSuggestions();
                                    Log.v("Near generate suggestions, line 151", "Hey");
                                }
                            }
                        }
                    });



                } else {
                    Toast.makeText(SurveyActivityPageTwo.this, "Make sure to answer all questions before submitting.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public ArrayList<String> generateSuggestions(){
        //Log.v("Got in generateSuggestions", "GENERATE");

        DatabaseReference studentDbRef = FirebaseDatabase.getInstance().getReference().child("students");
        studentDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot dataSnapshot = task.getResult();
//                String currentUserCourse = "";
//                //Loop to find course of current user
//                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
//                    if (studentSnapshot.child("username").getValue().toString().equalsIgnoreCase(username)) {
//                        currentUserCourse =  (String) studentSnapshot.child("course").getValue();
//                        Log.v("Current User Course", currentUserCourse);
//                    }
//                }
                //Loop through other users in same course who have taken survey and compare responses
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    if (studentSnapshot.child("course").getValue().toString().equalsIgnoreCase(currentCourse) && !studentSnapshot.child("username").getValue().toString().equalsIgnoreCase(username)){
                        DatabaseReference surveyRef = studentSnapshot.getRef().child("survey responses");
                        HashMap<String, String> surveyResponses = (HashMap<String, String>) studentSnapshot.child("survey responses").getValue();
                        //HashMap<String, String> surveyResponses = surveyRef.child("survey responses").;
                        Log.v("Other User Survey Responses: ", surveyResponses+"");
                        Boolean canMatch = false;
                        if(surveyResponses != null){
                            canMatch = compareResponses(answers, surveyResponses);
                        }
                        if(canMatch){
                            Log.v("MATCH!!!", "MATCH!!!!");
                            if (!suggestions.contains(studentSnapshot.child("username").getValue().toString())) {
                                suggestions.add(studentSnapshot.child("username").getValue().toString());
                            }

                        }
                    }
                }

            }
        }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Log.v("Recommended Users: ", suggestions+"");
                //Add matched users to database
                HashMap<String, String> matches = new HashMap<>();
                int i = 1;
                for(String suggestion : suggestions){
                    DataSnapshot dataSnapshot = task.getResult();
                    for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                        if(studentSnapshot.child("username").getValue().toString().equalsIgnoreCase(suggestion)){
                            boolean isAlreadyMatched = false;
                            for (DataSnapshot matchInDB : studentSnapshot.child("matched to").getChildren()) {
                                isAlreadyMatched = matchInDB.getValue().toString().equalsIgnoreCase(username);
                            }
                            if (!isAlreadyMatched) {
                                DatabaseReference specificStudentRef = studentSnapshot.getRef();
                                specificStudentRef.child("matched to").push().setValue(username);
                            }
                        }
                    }
                    matches.put("User " + i, suggestion);
                    i += 1;
                }
                /*DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    if(studentSnapshot.child("username").getValue().toString().equalsIgnoreCase(username)){
                        DatabaseReference specificStudentRef = studentSnapshot.getRef();
                        specificStudentRef.child("matched to").setValue(matches);
                    }
                }*/
                Intent intent = new Intent(SurveyActivityPageTwo.this, SurveyCompletionActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("suggestions", suggestions);
                startActivity(intent);
            }
        });

        return suggestions;

    }

    /*If any responses are ideal for each other (e.g. extroverted and introverted) then they can be a match*/
    public boolean compareResponses(HashMap<String, String> currUserResponses, HashMap<String, String> otherUserResponses){
//        Boolean match = true;
        //Compare Q1 Responses
        if (currUserResponses.get("q1").equals("introverted") && otherUserResponses.get("q1").equals("introverted")) {
            return false;
        }
//        else if(currUserResponses.get("q1").equals("introverted") && otherUserResponses.get("q1").equals("extroverted")) {
//            match = true;
//        } else if(currUserResponses.get("q1").equals("extroverted") && otherUserResponses.get("q1").equals("extroverted")) {
//            match = false;
//        } else {
//            match = true;
//        }

        //Compare Q2 Responses
        if(currUserResponses.get("q2").equals("busy") && otherUserResponses.get("q2").equals("busy")) {
            return false;
        }
//        if (currUserResponses.get("q2").equals("busy") && otherUserResponses.get("q2").equals("not busy")) {
//            match = true;
//        } else if(currUserResponses.get("q2").equals("busy") && otherUserResponses.get("q2").equals("busy")) {
//            match = false;
//        } else if(currUserResponses.get("q2").equals("not busy") && otherUserResponses.get("q2").equals("not busy")) {
//            match = true;
//        } else {
//            match = false;
//        }

        //Compare Q3 Responses
        if(!currUserResponses.get("q3").equals(otherUserResponses.get("q3"))){
            return false;
        }

        //Q4 Responses won't be considered for match (learning styles should not affect whether they match)

        //Compare Q5 Responses
        if(!currUserResponses.get("q5").equals(otherUserResponses.get("q5"))){
            //Not same time zone
            return false;
        }


        //Compare Q6 Responses (If both users have only taken between 0 and 3 courses may be hard for them to work together)
        if (currUserResponses.get("q6").equals("0-3") && otherUserResponses.get("q6").equals("0-3")) {
            return false;
        }

        //Compare Q7 Responses
        if (currUserResponses.get("q7").equals("Not Able") && otherUserResponses.get("q7").equals("Not Able")) {
            return false;
        }

        return true;
    }


}