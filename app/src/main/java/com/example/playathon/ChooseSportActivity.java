package com.example.playathon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChooseSportActivity extends AppCompatActivity {

    private ListView sportList;
    private String userId;


   String[]  sports = {"CRICKET", "FOOTBALL", "BADMINTON", "BASKETBALL", "VOLLEYBALL","HOCKEY","TENNIS"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sport);


        sportList = (ListView) findViewById(R.id.sports_listview);
        sportList.setAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1,sports));

        sportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sport = (String) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(ChooseSportActivity.this, ChooseVenueActivity.class);
                intent.putExtra("sport", sport);
                startActivity(intent);
            }
        });

    }


}
