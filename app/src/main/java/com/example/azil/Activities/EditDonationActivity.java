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

import com.example.azil.databinding.ActivityEditDonationBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditDonationActivity extends AppCompatActivity {
    private ActivityEditDonationBinding binding;
    ProgressDialog progressDialog;
    Intent intent;
    private String donacijaSifra;
    DatabaseReference dbRefTrazenaDonacija;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditDonationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);

        donacijaSifra = getIntent().getStringExtra("donacijaSifra");
        loadDonationInfo();

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditDonationActivity.this.finish();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });
    }

    private void loadDonationInfo() {
        dbRefTrazenaDonacija = FirebaseDatabase.getInstance().getReference("trazena_donacija");
        dbRefTrazenaDonacija.child(donacijaSifra).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String opis = ""+snapshot.child("opis").getValue();
                String kolicina = ""+snapshot.child("kolicina").getValue();

                binding.etOpis.setText(opis);
                binding.etKolicina.setText(kolicina);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
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
            updateDonation();
        }
    }

    private void updateDonation() {
        progressDialog.setMessage("Ažuriranje podataka...");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("opis", ""+opis);
        hashMap.put("kolicina", ""+kolicina);

        dbRefTrazenaDonacija = FirebaseDatabase.getInstance().getReference("trazena_donacija");
        dbRefTrazenaDonacija.child(donacijaSifra).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Uspješno ažurirano!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditDonationActivity.this, AdminActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Error", e.toString());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Došlo je do pogreške", Toast.LENGTH_SHORT).show();
            }
        });
    }
}