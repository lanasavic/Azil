package com.example.azil.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.azil.Models.AccessKey;
import com.example.azil.Models.Admin;
import com.example.azil.Models.Shelter_Admin;
import com.example.azil.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Button btnNastavak;
    private EditText inptUsername, inptEmail, inptPassword, inptPassword2, inptAccessKey;
    DatabaseReference dbRefAccessKey;

    public void imageButtonOnClick(View v) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Pričekajte...");
        progressDialog.setCanceledOnTouchOutside(false);

        inptUsername = (EditText) findViewById(R.id.inputUsername);
        inptEmail = (EditText) findViewById(R.id.inputEmail);
        inptPassword = (EditText) findViewById(R.id.inputPassword);
        inptPassword2 = (EditText) findViewById(R.id.inputPassword2);
        inptAccessKey = (EditText) findViewById(R.id.inputAccessKey);

        btnNastavak = findViewById(R.id.btnNastavak);
        btnNastavak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    String txtUsername="", txtEmail="", txtPassword="", txtKey="";
    private void validateData() {
        txtUsername = inptUsername.getText().toString().trim();
        txtEmail = inptEmail.getText().toString().trim();
        txtPassword = inptPassword.getText().toString().trim();
        String txtPassword2 = inptPassword2.getText().toString().trim();
        txtKey = inptAccessKey.getText().toString().trim();

        if(TextUtils.isEmpty(txtUsername)){
            Toast.makeText(this, "Unesite korisničko ime", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(txtEmail)){
            Toast.makeText(this, "Unesite email adresu", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()){
            Toast.makeText(this, "Netočan oblik email adrese", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(txtPassword)){
            Toast.makeText(this, "Unesite lozinku", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(txtPassword2)){
            Toast.makeText(this, "Unesite lozinku ponovno", Toast.LENGTH_SHORT).show();
        }
        else if(!txtPassword.equals(txtPassword2)){
            Toast.makeText(this, "Lozinke se ne podudaraju", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(txtKey)){
            Toast.makeText(this, "Unesite pristupni ključ", Toast.LENGTH_SHORT).show();
        }
        else{
            dbRefAccessKey = FirebaseDatabase.getInstance().getReference("security");
            dbRefAccessKey.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AccessKey accessKey = dataSnapshot.getValue(AccessKey.class);
                        assert accessKey != null;
                        String sPrimary = accessKey.getPrimary();
                        String sSecondary = accessKey.getSecondary();

                        //Log.d("primarni", sPrimary);
                        //Log.d("sekundarni", sSecondary);
                        //Log.d("key", txtKey);

                        if (!sPrimary.equals(txtKey) && !sSecondary.equals(txtKey)) {
                            Toast.makeText(RegisterActivity.this, "Nevažeći pristupni ključ", Toast.LENGTH_SHORT).show();
                        } else {
                            createAccount();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void createAccount() {
        progressDialog.setTitle("Stvaranje računa...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(txtEmail, txtPassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.setMessage("Spremanje podataka...");
                        updateAccount();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAccount() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", txtUsername);
        hashMap.put("email", txtEmail);
        hashMap.put("password", txtPassword);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("admin");

        dbRef.child(txtUsername).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Račun uspješno kreiran", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, AdminActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}