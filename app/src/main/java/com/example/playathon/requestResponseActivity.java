package com.example.playathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        Intent intent = getIntent();

        final String sportId = intent.getStringExtra("sportId");

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("organized sports").child(sportId);


        textView = (TextView) findViewById(R.id.request_response_textView);
        accept = (Button) findViewById(R.id.accept);
        reject = (Button) findViewById(R.id.reject);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot d: dataSnapshot.getChildren()){
                    if (i!=0){
                        break;
                    }
                    textView.setText("you have a request from "+d.getValue());
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(eventListener);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("participants").child(mAuth.getCurrentUser().getUid()).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("sports").child("participated").child(sportId).setValue(true);
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Participants requests").child(mAuth.getCurrentUser().getUid()).removeValue();
                Intent intent = new Intent(requestResponseActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        });


    }
}
