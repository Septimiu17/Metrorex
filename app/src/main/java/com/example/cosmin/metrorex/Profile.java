package com.example.cosmin.metrorex;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Profile extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private Button buttonLogout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth=FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            //user not logged in
            startActivity(new Intent(this,Login.class));
        }

        textViewUserEmail=(TextView)findViewById(R.id.tvprofile);
        textViewUserEmail.setText("Welcome "+firebaseAuth.getCurrentUser().getEmail().toString().trim()+" !");


        buttonLogout=(Button)findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view == buttonLogout){
            //logout
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(Profile.this,Login.class));

        }

    }
}
