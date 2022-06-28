package com.example.azil.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.azil.Fragments.ShelterFragment;
import com.example.azil.Models.Admin;
import com.example.azil.Models.Shelter;
import com.example.azil.Models.Shelter_Admin;
import com.example.azil.R;
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

        Query adminEmailQuery = dbRefAdmin.orderByChild("email").equalTo(firebaseUser.getEmail());
        adminEmailQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Admin admin = dataSnapshot.getValue(Admin.class);
                    assert admin != null;
                    String sAdminUser = admin.getUsername();
                    String email = firebaseUser.getEmail();

                    binding.etKontakt.setText(email);
                    binding.etKontakt.setEnabled(false);

                    Query adminShelterQuery = dbRefSklonisteAdmin.orderByChild("admin").equalTo(sAdminUser);
                    adminShelterQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                Shelter_Admin shelter_admin = dataSnapshot1.getValue(Shelter_Admin.class);
                                assert shelter_admin != null;
                                String sShelterOib = shelter_admin.getSkloniste();

                                Query shelterQuery = dbRefSkloniste.orderByChild("oib").equalTo(sShelterOib);
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
        /*else if(TextUtils.isEmpty(sOib)){
            Toast.makeText(this, "Unesite novi OIB", Toast.LENGTH_SHORT).show();
        }*/
        else if(TextUtils.isEmpty(sIban) || !sIban.matches("^[H][R]\\d{7}")){
            Toast.makeText(this, "Unesite novi pravilni IBAN (npr. HR1234567)", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(sDostupnihMjesta)){
            Toast.makeText(this, "Unesite broj dostupnih mjesta", Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Ažuriranje podataka o skloništu...");
            progressDialog.show();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("naziv", sNaziv);
            hashMap.put("adresa", sAdresa);
            hashMap.put("grad", sGrad);
            hashMap.put("oib", sOib);
            hashMap.put("iban", sIban);
            hashMap.put("dostupnih_mjesta", sDostupnihMjesta);

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("skloniste");

            dbRef.child(sOib).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(EditActivity.this, "Promjene spremljene!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditActivity.this, AdminActivity.class));
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
}

//INTENT ne dohvaca podatke ?