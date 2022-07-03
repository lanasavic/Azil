package com.example.azil.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azil.Adapters.RequestsAdapter;
import com.example.azil.Models.Admin;
import com.example.azil.Models.Animal_Request;
import com.example.azil.Models.Request;
import com.example.azil.Models.Shelter_Admin;
import com.example.azil.Models.Shelter_Animal;
import com.example.azil.R;
import com.example.azil.databinding.RequestItemBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestsActivity extends AppCompatActivity {
    private Button btnBack;
    ArrayList<Request> lRequests;
    RequestItemBinding binding;
    String adminEmail, sRequestId, key, sAnimalId, sShelterOib, sAnimalRequestId, targetAnimal;
    RecyclerView rvRequests;
    RequestsAdapter requestsAdapter;
    DatabaseReference dbRefZivotinjaZahtjev, dbRefZivotinja, dbRefZahtjev, dbRefAdmin, dbRefSklonisteAdmin, dbRefSklonisteZivotinja, dbRefSkloniste;
    FirebaseUser firebaseUser;
    private EditText search_requests;
    int count;
    ProgressDialog progressDialog;

    public void imageButtonCheck(View v) {
        updateData();
    }

    public void imageButtonDecline(View v) {
        progressDialog.setMessage("Brisanje zahtjeva...");
        progressDialog.show();

        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja").child(targetAnimal).child("zahtjevi");
        dbRefZivotinja.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                try {
                    count = Integer.parseInt(currentData.getValue(String.class));
                } catch (NumberFormatException e) {
                    count = 0;
                }
                count--;
                currentData.setValue(Integer.toString(count));
                return Transaction.success(currentData);
            }
            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                dbRefZahtjev.child(sRequestId).removeValue();
                dbRefZivotinjaZahtjev.child(sAnimalRequestId).removeValue();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Uspješno obrisano!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RequestsActivity.this, AdminActivity.class));
                finish();
            }
        });
    }

    private void updateData() {
        progressDialog.setMessage("Ažuriranje u tijeku...");
        progressDialog.show();

        dbRefSkloniste = FirebaseDatabase.getInstance().getReference("skloniste").child(sShelterOib).child("dostupnih_mjesta");
        dbRefSkloniste.runTransaction(new Transaction.Handler() {
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
                dbRefSklonisteZivotinja.orderByChild("zivotinja").equalTo(targetAnimal).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            key = dataSnapshot.getKey();

                            dbRefZivotinja.child(targetAnimal).removeValue();
                            dbRefSklonisteZivotinja.child(key).removeValue();
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Uspješno ažurirano!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RequestsActivity.this, AdminActivity.class));
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Došlo je do pogreške", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RequestsActivity.this, AdminActivity.class));
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RequestItemBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_requests);

        progressDialog = new ProgressDialog(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            adminEmail = extras.getString("adminEmail");
        }

        rvRequests = findViewById(R.id.rvRequests);
        rvRequests.setHasFixedSize(true);
        rvRequests.setLayoutManager(new LinearLayoutManager(this));

        lRequests = new ArrayList<>();
        requestsAdapter = new RequestsAdapter(this, lRequests);
        rvRequests.setAdapter(requestsAdapter);

        dbRefAdmin = FirebaseDatabase.getInstance().getReference("admin");
        dbRefSklonisteAdmin = FirebaseDatabase.getInstance().getReference("skloniste_admin");
        dbRefZivotinjaZahtjev = FirebaseDatabase.getInstance().getReference("zivotinja_zahtjev");
        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
        dbRefZahtjev = FirebaseDatabase.getInstance().getReference("zahtjev");
        dbRefSklonisteZivotinja = FirebaseDatabase.getInstance().getReference("skloniste_zivotinja");

        Query adminEmailQuery = dbRefAdmin.orderByChild("email").equalTo(firebaseUser.getEmail());
        adminEmailQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Admin admin = dataSnapshot.getValue(Admin.class);
                    assert admin != null;
                    String sAdminUser = admin.getUsername();
                    //Log.d("check: sAdminUser_", sAdminUser);

                    Query adminShelterQuery = dbRefSklonisteAdmin.orderByChild("admin").equalTo(sAdminUser);
                    adminShelterQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                Shelter_Admin shelter_admin = dataSnapshot1.getValue(Shelter_Admin.class);
                                assert shelter_admin != null;
                                sShelterOib = shelter_admin.getSkloniste();
                                //Log.d("check: sShelterOib_", sShelterOib);

                                Query shelterAnimalQuery = dbRefSklonisteZivotinja.orderByChild("skloniste").equalTo(sShelterOib);
                                shelterAnimalQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                            Shelter_Animal shelter_animal = dataSnapshot2.getValue(Shelter_Animal.class);
                                            assert shelter_animal != null;
                                            sAnimalId = shelter_animal.getZivotinja();
                                            //Log.d("check: sAnimalId_", sAnimalId);

                                            Query animalRequestQuery = dbRefZivotinjaZahtjev.orderByChild("zivotinja").equalTo(sAnimalId);
                                            animalRequestQuery.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot3 : snapshot.getChildren()) {
                                                        Animal_Request animal_request = dataSnapshot3.getValue(Animal_Request.class);
                                                        assert animal_request != null;
                                                        sRequestId = animal_request.getZahtjev();
                                                        sAnimalRequestId = animal_request.getId();
                                                        targetAnimal = animal_request.getZivotinja();
                                                        //Log.d("check: sRequestId_", sRequestId);
                                                        //Log.d("check:sAnimalRequestId_", sAnimalRequestId);
                                                        //Log.d("check: targetAnimal_", targetAnimal);

                                                        Query requestQuery = dbRefZahtjev.orderByChild("sifra").equalTo(sRequestId);
                                                        requestQuery.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for (DataSnapshot dataSnapshot4 : snapshot.getChildren()) {
                                                                    Request request = dataSnapshot4.getValue(Request.class);
                                                                    assert request != null;
                                                                    lRequests.add(request);
                                                                    //String requestSifra = request.getSifra();
                                                                    //Log.d("check: requestSifra_", requestSifra);
                                                                }
                                                                requestsAdapter.notifyDataSetChanged();
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        search_requests = findViewById(R.id.search_requests);
        search_requests.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    requestsAdapter.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d("ERROR", "Error:" + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}