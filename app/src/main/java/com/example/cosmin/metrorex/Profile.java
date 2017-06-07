package com.example.cosmin.metrorex;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cosmin.metrorex.Adapter.AbonamentAdapter;
import com.example.cosmin.metrorex.Adapter.CalatorieAdapter;
import com.example.cosmin.metrorex.Adapter.CreditAdapter;
import com.example.cosmin.metrorex.Model.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private Button buttonLogout;

    //Database
    private DatabaseReference databaseReference;
    private TextView nameText;
    private TextView numberText;
    private Button buttonScaneaza;
    private TextView textViewCalatorii;
    private TextView textViewCredit;
    private TextView textViewAbonament;
    private Button buttonCumparaCredit;
    private ListView listViewCredit;
    private ListView listViewCalatorie;
    private ListView listViewAbonament;
    private List<Integer> listAbonament;
    private ImageView imageViewCloseButton;
    private Button buttonCalatorie;
    private Button buttonAbonament;
    private int abonamentNew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            //user not logged in
            startActivity(new Intent(this, Login.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        buttonScaneaza = (Button) findViewById(R.id.buttonScaneaza_Profile);
        textViewCalatorii = (TextView) findViewById(R.id.tvcalatorii);
        textViewCredit = (TextView) findViewById(R.id.tvcredit);
        textViewAbonament = (TextView) findViewById(R.id.tvabonament);
        buttonCumparaCredit = (Button) findViewById(R.id.bcredit);
        imageViewCloseButton = (ImageView) findViewById(R.id.im_close_button);
        buttonCalatorie = (Button) findViewById(R.id.bcalatorie);
        buttonAbonament = (Button) findViewById(R.id.babonament);

        textViewUserEmail = (TextView) findViewById(R.id.tvprofile);
        textViewUserEmail.setText("Welcome " + firebaseAuth.getCurrentUser().getEmail().toString().trim() + " !");


        databaseReference.
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                            if (firebaseAuth.getCurrentUser().getUid().toString().trim().equals(postSnapShot.getKey().toString().trim())) {
                                UserInformation userInformation = postSnapShot.getValue(UserInformation.class);
                                textViewCalatorii.setText(textViewCalatorii.getText().toString().trim() + userInformation.getNumarCalatorii());
                                textViewCredit.setText(textViewCredit.getText().toString().trim() + userInformation.getCredit());
                                textViewAbonament.setText(textViewAbonament.getText().toString().trim() + userInformation.getTipAbonament());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        System.out.println("The read failed: " + databaseError.getMessage());

                    }
                });


        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(this);
        buttonScaneaza.setOnClickListener(this);
        buttonCumparaCredit.setOnClickListener(this);
        buttonCalatorie.setOnClickListener(this);
        buttonAbonament.setOnClickListener(this);
        imageViewCloseButton.setOnClickListener(this);

    

        listAbonament = new ArrayList<>();
        listAbonament.add(1);
        listAbonament.add(10);
        listAbonament.add(30);
        listAbonament.add(90);
        listAbonament.add(180);
        listAbonament.add(365);

       
        listViewAbonament = (ListView) findViewById(R.id.lv_credit);






    }

    private void showUpdatedInfo() {
        String S = textViewCredit.getText().toString().replaceAll("[0-9]+", "");
        textViewCredit.setText(S);
        S = textViewCalatorii.getText().toString().replaceAll("[0-9]+", "");
        textViewCalatorii.setText(S);
        S = textViewAbonament.getText().toString().replaceAll("[a-zA-Z]+", "");
        textViewAbonament.setText("Abonament:");
    }

    private void saveUserInformation() {
        String name = nameText.getText().toString().trim();
        String number = numberText.getText().toString().trim();

        UserInformation userInformation = new UserInformation(name, number);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(userInformation);
        Toast.makeText(this, "Informatia a fost salvata !", Toast.LENGTH_LONG).show();
    }

    

    private void cumparaAbonament(){
       

        listViewAbonament.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                abonamentNew=(Integer)listViewAbonament.getItemAtPosition(position);
                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child(firebaseAuth.getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int abonament=dataSnapshot.child("tipAbonament").getValue(Integer.class);
                                int credit=dataSnapshot.child("credit").getValue(Integer.class);
                                int abonamentNou=abonament+abonamentNew;
                                int creditNou=credit - 4 * abonamentNew;
                                if(creditNou >= 0){
                                    showUpdatedInfo();
                                    UserInformation userInformation=new UserInformation(dataSnapshot.getValue(UserInformation.class));
                                    userInformation.setCredit(creditNou);
                                    userInformation.setTipAbonament(abonamentNou);
                                    databaseReference.child(dataSnapshot.getKey()).setValue(userInformation);
                                    Toast.makeText(Profile.this,"You added "+abonamentNou+" zile la abonament",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(Profile.this,"You don't have enough credit",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        });
        AbonamentAdapter abonamentAdapter=new AbonamentAdapter(listAbonament,Profile.this);
        listViewAbonament.setAdapter(abonamentAdapter);
        listViewAbonament.setVisibility(View.VISIBLE);
        imageViewCloseButton.setVisibility(View.VISIBLE);


    }

    public void scan(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(Profile.this,"Ai iesit din activitatea de scanare!",Toast.LENGTH_LONG).show();
            }
            else {
                StorageReference storageReference= FirebaseStorage.getInstance().getReference();
                if(storageReference.child(result.getContents())!=null) {
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).
                            addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int calatorie = dataSnapshot.child("numarCalatorii").getValue(Integer.class);
                                    int abonament = dataSnapshot.child("tipAbonament").getValue(Integer.class);
                                    if(abonament > 0){

                                        Toast.makeText(Profile.this, "Succes! Zile ramase abonament:"+abonament , Toast.LENGTH_LONG).show();
                                    }
                                    if (calatorie > 0 && abonament == 0) {
                                        showUpdatedInfo();
                                        UserInformation userInformation = new UserInformation(dataSnapshot.getValue(UserInformation.class));
                                        userInformation.setNumarCalatorii(calatorie - 1);
                                        int calatorieNew=calatorie-1;
                                        databaseReference.child(dataSnapshot.getKey()).setValue(userInformation);
                                        Toast.makeText(Profile.this, "Succes! Calatorii ramase:"+calatorieNew, Toast.LENGTH_LONG).show();
                                    } else {
                                        if(abonament == 0 && calatorie == 0){
                                        Toast.makeText(Profile.this, "Nu ai destule calatorii !", Toast.LENGTH_LONG).show();
                                        return;}
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
                else{
                    Toast.makeText(Profile.this,"Codul scanat nu exista",Toast.LENGTH_LONG).show();
                }

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {

        if (view == buttonLogout) {
            //logout
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(Profile.this, Login.class));

        }
        if (view == buttonScaneaza)
            scan();

        if (view == buttonCumparaCredit) {
           // cumparaCredit();
        }
        if(view == imageViewCloseButton){
            listViewCredit.setVisibility(View.GONE);
            listViewCalatorie.setVisibility(View.GONE);
            listViewAbonament.setVisibility(View.GONE);
            imageViewCloseButton.setVisibility(View.GONE);

        }
        if(view == buttonCalatorie){
            //cumparaCalatorie();
        }

        if(view == buttonAbonament){
            cumparaAbonament();
        }
    }
}
