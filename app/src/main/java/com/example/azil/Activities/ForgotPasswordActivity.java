package com.example.azil.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.azil.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Button btnPosalji;
    private EditText inptEmail;

    public void imageButtonOnClick(View v) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void registerOnClick(View v) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        inptEmail = (EditText) findViewById(R.id.inputEmail);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Pričekajte...");
        progressDialog.setCanceledOnTouchOutside(false);

        btnPosalji = findViewById(R.id.btnPosalji);
        btnPosalji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private String txtEmail = "";
    private void validateData() {
        txtEmail = inptEmail.getText().toString().trim();
        if(txtEmail.isEmpty()){
            Toast.makeText(this, "Upišite email adresu", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()){
            Toast.makeText(this, "Netočan oblik email adrese", Toast.LENGTH_SHORT).show();
        }
        else{
            changePassword();
        }
    }

    private void changePassword() {
        progressDialog.setMessage("Slanje poveznice za promjenu lozinke...");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(txtEmail)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Poveznica za promjenu lozinke uspješno poslana na email adresu: " + txtEmail, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Došlo je do pogreške (" + e.getMessage() + ")", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}