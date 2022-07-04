package com.example.azil.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.azil.Models.Admin;
import com.example.azil.Models.Shelter;
import com.example.azil.Models.Shelter_Admin;
import com.example.azil.databinding.ActivityEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    DatabaseReference dbRefSkloniste, dbRefAdmin, dbRefSklonisteAdmin;
    ProgressDialog progressDialog;
    private ActivityEditBinding binding;
    String newKey, lastChild1, checkShelter, userEmail, adminUsername, shelterOib;
    Integer newKey1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.this.finish();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

        dbRefSkloniste = FirebaseDatabase.getInstance().getReference("skloniste");
        dbRefAdmin = FirebaseDatabase.getInstance().getReference("admin");
        dbRefSklonisteAdmin = FirebaseDatabase.getInstance().getReference("skloniste_admin");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        userEmail = firebaseUser.getEmail();

        Query adminEmailQuery = dbRefAdmin.orderByChild("email").equalTo(userEmail);
        adminEmailQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Admin admin = dataSnapshot.getValue(Admin.class);
                    assert admin != null;
                    adminUsername = admin.getUsername();

                    binding.etKontakt.setText(userEmail);
                    binding.etKontakt.setEnabled(false);

                    Query adminShelterQuery = dbRefSklonisteAdmin.orderByChild("admin").equalTo(adminUsername);
                    adminShelterQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                Shelter_Admin shelter_admin = dataSnapshot1.getValue(Shelter_Admin.class);
                                assert shelter_admin != null;
                                shelterOib = shelter_admin.getSkloniste();

                                Query shelterQuery = dbRefSkloniste.orderByChild("oib").equalTo(shelterOib);
                                shelterQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                            Shelter shelter = dataSnapshot2.getValue(Shelter.class);
                                            assert shelter != null;

                                            String naziv = ""+shelter.getNaziv();
                                            String adresa = ""+shelter.getAdresa();
                                            String grad = ""+shelter.getGrad();
                                            String oib = ""+shelter.getOib();
                                            String iban = ""+shelter.getIban();
                                            String dostupnihmjesta = ""+shelter.getDostupnih_mjesta();

                                            binding.etNaziv.setText(naziv);
                                            binding.etAdresa.setText(adresa);
                                            binding.etGrad.setText(grad);
                                            binding.etOib.setText(oib);
                                            binding.etIban.setText(iban);
                                            binding.etDostupnihMjesta.setText(dostupnihmjesta);

                                            binding.etOib.setEnabled(false);
                                            //(3) Osobni identifikacijski broj dodjeljuje se obvezniku broja i ne mijenja se bez obzira na promjene koje prate obveznika broja.
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    String sNaziv = "", sAdresa="", sGrad="", sOib="", sIban="", sDostupnihMjesta="", sKontakt="";
    private void validateInputs() {
        sNaziv = binding.etNaziv.getText().toString().trim();
        sAdresa = binding.etAdresa.getText().toString().trim();
        sGrad = binding.etGrad.getText().toString().trim();
        sOib = binding.etOib.getText().toString().trim();
        sIban = binding.etIban.getText().toString().trim();
        sDostupnihMjesta = binding.etDostupnihMjesta.getText().toString().trim();
        sKontakt = binding.etKontakt.getText().toString().trim();

        if(TextUtils.isEmpty(sNaziv)){
            Toast.makeText(this, "Unesite novi naziv skloništa", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(sAdresa)){
            Toast.makeText(this, "Unesite novu adresu", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(sGrad)){
            Toast.makeText(this, "Unesite grad", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(sOib) || sOib.length() < 11){
            Toast.makeText(this, "Unesite novi pravilni OIB", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(sIban) || !sIban.matches("^[H][R]\\d{7}")){
            Toast.makeText(this, "Unesite novi pravilni IBAN (npr. HR1234567)", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(sDostupnihMjesta)){
            Toast.makeText(this, "Unesite broj dostupnih mjesta", Toast.LENGTH_SHORT).show();
        }
        else{
            updateShelter();
        }
    }

    private void updateShelter() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Ažuriranje podataka o skloništu...");
        progressDialog.show();

        dbRefSkloniste = FirebaseDatabase.getInstance().getReference("skloniste");
        dbRefSklonisteAdmin = FirebaseDatabase.getInstance().getReference("skloniste_admin");

        dbRefSkloniste.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Shelter shelter = dataSnapshot.getValue(Shelter.class);
                    assert shelter != null;
                    checkShelter = shelter.getOib();
                    Log.d("checkShelter", checkShelter);
                    Log.d("sOib", sOib);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("naziv", sNaziv);
                    hashMap.put("adresa", sAdresa);
                    hashMap.put("grad", sGrad);
                    hashMap.put("oib", sOib);
                    hashMap.put("iban", sIban);
                    hashMap.put("dostupnih_mjesta", sDostupnihMjesta);

                    if(!checkShelter.equals(sOib)){
                        newKey = sOib;
                        dbRefSkloniste.child(newKey).setValue(hashMap);
                        dbRefSklonisteAdmin.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    assert firebaseUser != null;
                                    userEmail = firebaseUser.getEmail();

                                    //Shelter_Admin shelter_admin = dataSnapshot1.getValue(Shelter_Admin.class);
                                    lastChild1 = dataSnapshot1.getKey();
                                    assert lastChild1 != null;
                                    newKey1 = (Integer.parseInt(lastChild1)+1);

                                    HashMap<String, Object> hashMap1 = new HashMap<>();
                                    hashMap1.put("id", ""+newKey);
                                    hashMap1.put("skloniste", ""+sOib);
                                    hashMap1.put("admin", ""+userEmail);

                                    dbRefSklonisteAdmin.child(newKey1.toString()).setValue(hashMap1);
                                    progressDialog.dismiss();
                                    Toast.makeText(EditActivity.this, "Uspješno ažurirano!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(EditActivity.this, AdminActivity.class));
                                    finish();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(EditActivity.this, "Sklonište sa istim OIB-om već postoji!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}