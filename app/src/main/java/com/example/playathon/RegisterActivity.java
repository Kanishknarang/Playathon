package com.example.playathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText memail;
    private EditText mpassword;
    private Button registerbtn;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DatabaseReference databaseRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsername = (EditText) findViewById(R.id.username);
        memail =(EditText) findViewById(R.id.register_email_text);
        mpassword = (EditText)findViewById(R.id.register_password_text);
        registerbtn = (Button) findViewById(R.id.register_button);

        mAuth = FirebaseAuth.getInstance();

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegistartion();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    private void startRegistartion(){
        String email = memail.getText().toString();
        String password = mpassword.getText().toString();
        final String username = mUsername.getText().toString();

        if (TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this,"field empty",Toast.LENGTH_LONG).show();
        }else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "login problem!", Toast.LENGTH_LONG).show();
                    }else{
                        userId = mAuth.getCurrentUser().getUid();
                        databaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("username");
                        databaseRef.setValue(username);

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }
}
