package com.example.azil.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.azil.Models.Admin;
import com.example.azil.Models.Shelter_Admin;
import com.example.azil.databinding.ActivityAddDonationBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddDonationActivity extends AppCompatActivity {
    private ActivityAddDonationBinding binding;
    ProgressDialog progressDialog;
    Intent intent;
    DatabaseReference dbRefTrazenaDonacija, dbRefSklonisteDonacija, dbRefAdmin, dbRefSklonisteAdmin;
    private String lastChild, lastChild1,  adminEmail, sShelterOib;
    private Integer newKey, newKey1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDonationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            adminEmail = extras.getString("adminEmail");
        }

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDonationActivity.this.finish();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });
    }

    private String opis = "", kolicina = "";
    private void validateInputs() {
        opis = binding.etOpis.getText().toString().trim();
        kolicina =  binding.etKolicina.getText().toString().trim();

        if(TextUtils.isEmpty(opis)){
            Toast.makeText(getApplicationContext(), "Unesite novi opis", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(kolicina) || !kolicina.matches("\\d+(?:\\.\\d+)?")){
            Toast.makeText(getApplicationContext(), "Unesite novu valjanu količinu", Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog.setMessage("Spremanje podataka...");
            progressDialog.show();
            saveDonation();
        }
    }

    private void saveDonation() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("opis", ""+opis);
        hashMap.put("kolicina", ""+kolicina);

        dbRefTrazenaDonacija = FirebaseDatabase.getInstance().getReference("trazena_donacija");
        dbRefSklonisteDonacija = FirebaseDatabase.getInstance().getReference("skloniste_donacija");
        dbRefAdmin = FirebaseDatabase.getInstance().getReference("admin");
        dbRefSklonisteAdmin = FirebaseDatabase.getInstance().getReference("skloniste_admin");

        Query adminEmailQuery = dbRefAdmin.orderByChild("email").equalTo(adminEmail);
        adminEmailQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Admin admin = dataSnapshot.getValue(Admin.class);
                    assert admin != null;
                    String sAdminUser = admin.getUsername();

                    Query adminShelterQuery = dbRefSklonisteAdmin.orderByChild("admin").equalTo(sAdminUser);
                    adminShelterQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                Shelter_Admin shelter_admin = dataSnapshot1.getValue(Shelter_Admin.class);
                                assert shelter_admin != null;
                                sShelterOib = shelter_admin.getSkloniste();
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        dbRefTrazenaDonacija.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    lastChild = dataSnapshot.getKey();
                }
                assert lastChild != null;
                newKey = (Integer.parseInt(lastChild)+1);
                hashMap.put("sifra", ""+newKey);
                dbRefTrazenaDonacija.child(newKey.toString()).setValue(hashMap);
                dbRefSklonisteDonacija.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            lastChild1 = dataSnapshot1.getKey();
                        }
                        assert lastChild1 != null;
                        newKey1 = (Integer.parseInt(lastChild1)+1);
                        HashMap<String, Object> hashMap1 = new HashMap<>();
                        hashMap1.put("id", ""+newKey1);
                        hashMap1.put("skloniste", ""+sShelterOib);
                        hashMap1.put("donacija", ""+newKey);

                        dbRefSklonisteDonacija.child(newKey1.toString()).setValue(hashMap1);
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Uspješno dodano!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddDonationActivity.this, AdminActivity.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddDonationActivity.this, AdminActivity.class));
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
}