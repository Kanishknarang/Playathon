package com.example.playathon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseVenueActivity extends AppCompatActivity {



    private EditText venueName;
    private Button submit;
    private String venue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_venue);



        venueName = (EditText) findViewById(R.id.venue_text);
        submit = (Button) findViewById(R.id.choose_venue_btn);



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reveivingintent = getIntent();
                String sport = reveivingintent.getStringExtra("sport");
                venue = venueName.getText().toString();
                Intent intent = new Intent(ChooseVenueActivity.this,OrganizerMapActivity.class);
                intent.putExtra("venue",venue);
                intent.putExtra("sport",sport);

                startActivity(intent);
            }
        });

    }

}
