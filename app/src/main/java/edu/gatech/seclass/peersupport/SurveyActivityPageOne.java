package edu.gatech.seclass.peersupport;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SurveyActivityPageOne extends AppCompatActivity {
    String username;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_page_one);
        bundle = getIntent().getExtras();
        if (username == null) {
            username = bundle.get("username").toString();
        }

        Button button = (Button) findViewById(R.id.mainmenu);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SurveyActivityPageOne.this, MainActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        Map<String, String> answers = new HashMap<>();

        RadioGroup q1 = findViewById(R.id.radioGroup1);
        RadioButton q1_introverted = findViewById(R.id.introvertedAnswer);
        RadioButton q1_extroverted = findViewById(R.id.extrovertedAnswer);

        RadioGroup q2 = findViewById(R.id.radioGroup2);
        RadioButton q2_busy = findViewById(R.id.busyAnswer);
        RadioButton q2_notBusy = findViewById(R.id.notBusyAnswer);

        RadioGroup q3 = findViewById(R.id.radioGroup3);
        RadioButton q3_morning = findViewById(R.id.morningAnswer);
        RadioButton q3_afternoon = findViewById(R.id.afternoonAnswer);
        RadioButton q3_evening= findViewById(R.id.eveningAnswer);

        RadioGroup q4 = findViewById(R.id.radioGroup4);
        RadioButton q4_visual = findViewById(R.id.visualAnswer);
        RadioButton q4_auditory = findViewById(R.id.auditoryAnswer);
        RadioButton q4_kinesthetic= findViewById(R.id.kinestheticAnswer);

        RadioGroup q5 = findViewById(R.id.radioGroup5);
        RadioButton q5_pstMst = findViewById(R.id.pstMSTAnswer);
        RadioButton q5_cstET = findViewById(R.id.cstETAnswer);
        RadioButton q5_other= findViewById(R.id.otherAnswer);

        TextView q6Question = findViewById(R.id.question_6);
        q6Question.setMovementMethod(new ScrollingMovementMethod());
        RadioGroup q6 = findViewById(R.id.radioGroup6);
        RadioButton q6_zeroToThree = findViewById(R.id.zeroToThreeAnswer);
        RadioButton q6_fourToSix = findViewById(R.id.fourToSixAnswer);
        RadioButton q6_above7= findViewById(R.id.above7Answer);

        RadioGroup q7 = findViewById(R.id.radioGroup7);
        RadioButton q7_veryAble = findViewById(R.id.veryAbleAnswer);
        q7_veryAble.setMovementMethod(new ScrollingMovementMethod());
        RadioButton q7_moderatelyAble = findViewById(R.id.moderatelyAbleAnswer);
        q7_moderatelyAble.setMovementMethod(new ScrollingMovementMethod());
        RadioButton q7_notAble= findViewById(R.id.notAbleAnswer);
        q7_notAble.setMovementMethod(new ScrollingMovementMethod());

        Button button1 = (Button) findViewById(R.id.nextButtonSurvey);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean missingAnswer = false;
                //int surveyScore = 0;
                if (q1.getCheckedRadioButtonId() == q1_introverted.getId()) {
                    answers.put("q1","introverted");
                    //surveyScore += 0;
                } else if (q1.getCheckedRadioButtonId() == q1_extroverted.getId()) {
                    answers.put("q1","extroverted");
                    //`surveyScore += 1;
                } else {
                    missingAnswer = true;
                }

                if (q2.getCheckedRadioButtonId() == q2_busy.getId()) {
                    answers.put("q2","busy");
                } else if (q2.getCheckedRadioButtonId() == q2_notBusy.getId()) {
                    answers.put("q2","not busy");
                } else {
                    missingAnswer = true;
                }

                if (q3.getCheckedRadioButtonId() == q3_morning.getId()) {
                    answers.put("q3","morning");
                } else if (q3.getCheckedRadioButtonId() == q3_afternoon.getId()) {
                    answers.put("q3","afternoon");
                } else if (q3.getCheckedRadioButtonId() == q3_evening.getId()) {
                    answers.put("q3","evening");
                }
                else {
                    missingAnswer = true;
                }

                if (q4.getCheckedRadioButtonId() == q4_auditory.getId()) {
                    answers.put("q4","auditory");
                } else if (q4.getCheckedRadioButtonId() == q4_visual.getId()) {
                    answers.put("q4","visual");
                } else if (q4.getCheckedRadioButtonId() == q4_kinesthetic.getId()) {
                    answers.put("q4","kinesthetic");
                }
                else {
                    missingAnswer = true;
                }

                if (q5.getCheckedRadioButtonId() == q5_pstMst.getId()) {
                    answers.put("q5","PST or MST");
                } else if (q5.getCheckedRadioButtonId() == q5_cstET.getId()) {
                    answers.put("q5","CST or EST");
                } else if (q5.getCheckedRadioButtonId() == q5_other.getId()) {
                    answers.put("q5","Other");
                }
                else {
                    missingAnswer = true;
                }

                if (q6.getCheckedRadioButtonId() == q6_zeroToThree.getId()) {
                    answers.put("q6","0-3");
                } else if (q6.getCheckedRadioButtonId() == q6_fourToSix.getId()) {
                    answers.put("q6","4-6");
                } else if (q6.getCheckedRadioButtonId() == q6_above7.getId()) {
                    answers.put("q6","Above 7");
                }
                else {
                    missingAnswer = true;
                }

                if (q7.getCheckedRadioButtonId() == q7_veryAble.getId()) {
                    answers.put("q7","Very Able");
                } else if (q7.getCheckedRadioButtonId() == q7_moderatelyAble.getId()) {
                    answers.put("q7","Moderately Able");
                } else if (q7.getCheckedRadioButtonId() == q7_notAble.getId()) {
                    answers.put("q7","Not Able");
                }
                else {
                    missingAnswer = true;
                }

                if (!missingAnswer) {
                    Intent intent = new Intent(SurveyActivityPageOne.this, SurveyActivityPageTwo.class);
                    intent.putExtra("username", username);
                    intent.putExtra("answers", (Serializable) answers);
                    startActivity(intent);
                } else {
                    Toast.makeText(SurveyActivityPageOne.this, "Make sure to answer all questions before proceeding.", Toast.LENGTH_LONG).show();
                }


            }
        });

        // for notifications ---------------------------------------------------------------------------
        /*DatabaseReference messagesDbRef = FirebaseDatabase.getInstance().getReference().child("messages");
        messagesDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String sender = messageSnapshot.child("sender").getValue().toString();
                    //Log.v("data", messageSnapshot.toString());
                    String receiver = messageSnapshot.child("receiver").getValue().toString();
                    boolean incoming = receiver.equalsIgnoreCase(username);
                    if (incoming) {
                        Intent intent = new Intent(SurveyActivityPageOne.this, ContactsActivity.class);
                        intent.putExtra("notification_info", "Click on " + sender + "'s chat to view new message");
                        intent.putExtra("username", username);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(SurveyActivityPageOne.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        final String CHANNEL_ID = "otherMessages";
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(SurveyActivityPageOne.this, CHANNEL_ID)
                                .setSmallIcon(com.google.firebase.database.R.drawable.common_full_open_on_phone)
                                .setContentTitle(sender)
                                .setContentText("A new message from " + sender)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(SurveyActivityPageOne.this);
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

    }
}
