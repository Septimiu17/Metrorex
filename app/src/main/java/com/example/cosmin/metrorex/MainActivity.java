package com.example.cosmin.metrorex;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.R.attr.password;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignIn;

    private ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth= FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);

        buttonRegister=(Button)findViewById(R.id.buttonRegister);

        editTextEmail=(EditText)findViewById(R.id.etusername);
        editTextPassword=(EditText)findViewById(R.id.etpassword);

        textViewSignIn=(TextView)findViewById(R.id.tvsignin);

        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);





    }

    private void registerUser() {
        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this,"Please enter an email address", Toast.LENGTH_SHORT).show();
            //stop the function to progress
            return;
        }

        if(TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this,"Please enter a password",Toast.LENGTH_SHORT).show();
            //stop the function to progress
            return;
        }

        //if validations are ok show progresDialog
        progressDialog.setMessage("Registering user.");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //user is succesfully registered and logged in
                            //start the profile activty
                            Toast.makeText(MainActivity.this,"Registered Succesfully !",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(MainActivity.this,Profile.class));
                        }
                        else {
                            Toast.makeText(MainActivity.this,"Could not register, please try again .",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    @Override
    public void onClick(View view) {
        if(view == buttonRegister) {
            registerUser();
        }
        if(view == textViewSignIn) {
        //will open login activty here
            startActivity(new Intent(this,Login.class));
        }



    }
}



