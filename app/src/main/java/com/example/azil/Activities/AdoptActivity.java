package com.example.azil.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.azil.Models.Animal;
import com.example.azil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AdoptActivity extends AppCompatActivity {
    private Button btnBack, btnHome, btnNastavak;
    private EditText etImePrezime, etEmail, etKomentar;
    private String imeZivotinje, sifraZivotinja, lastChild;
    private Integer newKey, newKey1;
    private TextView tvOdabranaZivotinja, tvKomentarHelp;
    Intent intent;
    Animal animal;
    ProgressDialog progressDialog;
    DatabaseReference dbRefZivotinja, dbRefZahtjev, dbRefZivotinjaZahtjev;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adopt);

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

        progressDialog = new ProgressDialog(this);
        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
        dbRefZahtjev = FirebaseDatabase.getInstance().getReference("zahtjev");
        dbRefZivotinjaZahtjev = FirebaseDatabase.getInstance().getReference("zivotinja_zahtjev");

        etImePrezime = findViewById(R.id.etImePrezime);
        etEmail = findViewById(R.id.etEmail);
        etKomentar = findViewById(R.id.etKomentar);

        intent = getIntent();
        if(intent != null){
            animal = (Animal) intent.getSerializableExtra("data2");
            imeZivotinje = animal.getIme();
            sifraZivotinja = animal.getSifra();
        }

        tvOdabranaZivotinja = findViewById(R.id.tvOdabranaZivotinja);
        tvOdabranaZivotinja.setText(getString(R.string.holder_OdabranaZivotinja, imeZivotinje));

        tvKomentarHelp = findViewById(R.id.tvKomentarHelp);
        tvKomentarHelp.setVisibility(View.INVISIBLE);
        etKomentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvKomentarHelp.setVisibility(View.VISIBLE);
            }
        });

        btnNastavak = (Button) findViewById(R.id.btnNastavak);
        btnNastavak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });
    }

    private String ime_prezime = "", email = "", komentar = "";
    private void validateInputs() {
        ime_prezime = etImePrezime.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        komentar = etKomentar.getText().toString().trim();

        if(TextUtils.isEmpty(ime_prezime)){
            Toast.makeText(getApplicationContext(), "Unesite ime i prezime", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(getApplicationContext(), "Unesite valjanu email adresu", Toast.LENGTH_SHORT).show();
        }
        else {
            addRequest();
        }
    }

    Date atm = Calendar.getInstance().getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    String formattedDate = dateFormat.format(atm);

    private void addRequest() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ime_prezime", ""+ime_prezime);
        hashMap.put("email", ""+email);
        hashMap.put("komentar", ""+komentar);
        hashMap.put("datum", ""+formattedDate);

        dbRefZahtjev.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    lastChild = dataSnapshot.getKey();
                }
                assert lastChild != null;
                newKey = (Integer.parseInt(lastChild)+1);
                hashMap.put("sifra", ""+newKey);
                dbRefZahtjev.child(newKey.toString()).setValue(hashMap);

                dbRefZivotinjaZahtjev.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            lastChild = dataSnapshot1.getKey();
                        }
                        assert lastChild != null;
                        newKey1 = (Integer.parseInt(lastChild)+1);
                        HashMap<String, Object> hashMap1 = new HashMap<>();
                        hashMap1.put("id", ""+newKey1);
                        hashMap1.put("zivotinja", ""+sifraZivotinja);
                        hashMap1.put("zahtjev", ""+newKey);

                        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja").child(sifraZivotinja).child("zahtjevi");
                        dbRefZivotinja.runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                try {
                                    count = Integer.parseInt(currentData.getValue(String.class));
                                } catch (NumberFormatException e) {
                                    count = 0;
                                }
                                count++;
                                currentData.setValue(Integer.toString(count));
                                return Transaction.success(currentData);
                            }
                            @Override
                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                /*Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                                i.putExtra(Intent.EXTRA_SUBJECT, "Zahtjev za posvajanjem - Azil");
                                i.putExtra(Intent.EXTRA_TEXT   , "Pozdrav!\nVaš zahtjev za posvajanjem jedne od naših životinja je uspješno zaprimljen. Veselimo se skorom susretu!\n-Azil");
                                try {
                                    startActivity(Intent.createChooser(i, "Slanje email-a..."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(AdoptActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }*/
                                dbRefZivotinjaZahtjev.child(newKey1.toString()).setValue(hashMap1);
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Uspješno poslano!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AdoptActivity.this, MainActivity.class));
                                finish();
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}