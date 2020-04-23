package com.example.playathon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SendRequestActivity extends AppCompatActivity {

    private TextView foundPlayerNameTextView;
    private Button sendRequestbtn;
    private String userId;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);


        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        final Intent intent = getIntent();

        String playerName = intent.getStringExtra("player");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Participants requests").child(playerName);
        foundPlayerNameTextView = (TextView) findViewById(R.id.send_request_textview);
        sendRequestbtn = (Button) findViewById(R.id.send_request_btn);
        foundPlayerNameTextView.setText(playerName);

        sendRequestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = databaseReference.child(intent.getStringExtra("sportId"));
                databaseReference.setValue("true");
            }
        });
    }
}
