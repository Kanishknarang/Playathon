package com.example.playathon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class foundPlayersActivity extends AppCompatActivity {

    private static final String TAG = "hello";
    DatabaseReference databaseReference;
    private String userId;
    private FirebaseAuth mAuth;

    private ListView listView;

    ArrayList players;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_players);

        players = new ArrayList();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("organizer requests").child(userId).child("found players");

        listView = (ListView) findViewById(R.id.found_players_list);



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int i = 0;
                    for(DataSnapshot d : dataSnapshot.getChildren()) {
                        //Log.d(TAG, "onCreate: "+ d.getKey());
                        players.add(d.getKey()) ;
                        Log.i(TAG, "onCreate: "+ players);

                    }



                }

                listView.setAdapter(new ArrayAdapter(foundPlayersActivity.this,android.R.layout.simple_list_item_1, players));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String player = (String) adapterView.getItemAtPosition(i);
                        Intent intent = new Intent(foundPlayersActivity.this, SendRequestActivity.class);
                        intent.putExtra("player", player);
                        startActivity(intent);
                    }
                });
            }//onDataChange

            @Override
            public void onCancelled(DatabaseError error) {

            }//onCancelled
        });



    }
}
