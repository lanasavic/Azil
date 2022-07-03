package com.example.azil.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azil.Models.RequestedDonation;
import com.example.azil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class DonateActivity extends AppCompatActivity {
    private Button btnBack, btnHome, btnNastavak;
    private EditText etImePrezime, etEmail, etKolicina, etKomentar;
    private String opisDonacije, kolicinaDonacije, sifraDonacije, lastChild;
    private Integer newKey, newKey1;
    TextView tvOdabranaDonacija, tvKomentarHelp2;
    Intent intent;
    RequestedDonation requestedDonation;
    ProgressDialog progressDialog;
    DatabaseReference dbRefTrazenaDonacija, dbRefZaprimljenaDonacija, dbRefTrazenoZaprimljeno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnHome = (Button) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvKomentarHelp2 = findViewById(R.id.tvKomentarHelp2);
        tvKomentarHelp2.setVisibility(View.INVISIBLE);
        etKomentar = findViewById(R.id.etKomentar);
        etKomentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvKomentarHelp2.setVisibility(View.VISIBLE);
            }
        });

        progressDialog = new ProgressDialog(this);
        dbRefZaprimljenaDonacija = FirebaseDatabase.getInstance().getReference("zaprimljena_donacija");
        dbRefTrazenaDonacija = FirebaseDatabase.getInstance().getReference("trazena_donacija");
        dbRefTrazenoZaprimljeno = FirebaseDatabase.getInstance().getReference("trazeno_zaprimljeno");

        etImePrezime = findViewById(R.id.etImePrezime);
        etEmail = findViewById(R.id.etEmail);
        etKolicina = findViewById(R.id.etKolicina);

        intent = getIntent();
        if(intent != null){
            requestedDonation = (RequestedDonation) intent.getSerializableExtra("data");
            opisDonacije = requestedDonation.getOpis();
            kolicinaDonacije = requestedDonation.getKolicina();
            sifraDonacije = requestedDonation.getSifra();
        }

        tvOdabranaDonacija = findViewById(R.id.tvOdabranaDonacija);
        tvOdabranaDonacija.setText(getString(R.string.holder_OdabranaDonacija, opisDonacije, kolicinaDonacije));

        btnNastavak = (Button) findViewById(R.id.btnNastavak);
        btnNastavak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });
    }

    private String ime_prezime = "", email = "", kolicina = "", komentar = "";
    private void validateInputs() {
        ime_prezime = etImePrezime.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        kolicina = etKolicina.getText().toString().trim();
        komentar = etKomentar.getText().toString().trim();

        if(TextUtils.isEmpty(ime_prezime)){
            Toast.makeText(getApplicationContext(), "Unesite ime i prezime", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(getApplicationContext(), "Unesite valjanu email adresu", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(kolicina)){
            Toast.makeText(getApplicationContext(), "Unesite valjanu količinu", Toast.LENGTH_SHORT).show();
        }
        else {
            addReceivedDonation();
        }
    }

    private void addReceivedDonation() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ime_prezime", ""+ime_prezime);
        hashMap.put("email", ""+email);
        hashMap.put("kolicina", ""+kolicina);
        hashMap.put("komentar", ""+komentar);

        dbRefZaprimljenaDonacija.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    lastChild = dataSnapshot.getKey();
                }
                assert lastChild != null;
                newKey = (Integer.parseInt(lastChild)+1);
                hashMap.put("sifra", ""+newKey);
                dbRefZaprimljenaDonacija.child(newKey.toString()).setValue(hashMap);

                dbRefTrazenoZaprimljeno.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            lastChild = dataSnapshot1.getKey();
                        }
                        assert lastChild != null;
                        newKey1 = (Integer.parseInt(lastChild)+1);
                        HashMap<String, Object> hashMap1 = new HashMap<>();
                        hashMap1.put("id", ""+newKey1);
                        hashMap1.put("trazeno", ""+sifraDonacije);
                        hashMap1.put("zaprimljeno", ""+newKey);

                        dbRefTrazenoZaprimljeno.child(newKey1.toString()).setValue(hashMap1);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Uspješno poslano!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DonateActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}