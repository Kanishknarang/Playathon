package com.example.playathon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class requestResponseActivity extends AppCompatActivity {

    private TextView textView;
    private Button accept;
    private Button reject;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_response);

//        mAuth = FirebaseAuth.getInstance();
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("Participants requests").child(mAuth.getCurrentUser().getUid());

        textView = (TextView) findViewById(R.id.request_response_textView);
        accept = (Button) findViewById(R.id.accept);
        reject = (Button) findViewById(R.id.reject);

        Intent intent = getIntent();

        String organizer = intent.getStringExtra("organizer");

        textView.setText("you have a request from "+organizer);



    }
}
