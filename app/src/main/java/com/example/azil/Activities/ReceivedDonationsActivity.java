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
import android.widget.Toast;

import com.example.azil.Adapters.AdminReceivedDonationsAdapter;
import com.example.azil.Models.Admin;
import com.example.azil.Models.ReceivedDonation;
import com.example.azil.Models.RequestedDonation;
import com.example.azil.Models.Requested_Received;
import com.example.azil.Models.Shelter_Admin;
import com.example.azil.Models.Shelter_Donation;
import com.example.azil.R;
import com.example.azil.databinding.ReceivedDonationItemBinding;
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

public class ReceivedDonationsActivity extends AppCompatActivity {
    private Button btnBack;
    ArrayList<ReceivedDonation> lDonations;
    ReceivedDonationItemBinding binding;
    String adminEmail, sReceivedDonation, receivedNum, sRequestedDonation;
    RecyclerView rvReceivedDonations;
    AdminReceivedDonationsAdapter adminReceivedDonationsAdapter;
    DatabaseReference dbRefTrazenaDonacija, dbRefTrazenoZaprimljeno, dbRefZaprimljenaDonacija, dbRefAdmin, dbRefSklonisteAdmin, dbRefSklonisteDonacija;
    FirebaseUser firebaseUser;
    private EditText search_receivedDonations;
    int count, received;
    ProgressDialog progressDialog;

    public void imageButtonCheck(View v) {
        updateDonationData();
    }

    public void imageButtonDecline(View v) {
        progressDialog.setMessage("Brisanje donacije...");
        progressDialog.show();
        dbRefZaprimljenaDonacija.child(sReceivedDonation).removeValue();
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), "Uspješno obrisano!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ReceivedDonationsActivity.this, AdminActivity.class));
        finish();
    }

    private void updateDonationData() {
        progressDialog.setMessage("Ažuriranje u tijeku...");
        progressDialog.show();

        dbRefTrazenaDonacija = FirebaseDatabase.getInstance().getReference("trazena_donacija").child(sRequestedDonation).child("kolicina");
        dbRefTrazenaDonacija.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                try {
                    count = Integer.parseInt(currentData.getValue(String.class));
                } catch (NumberFormatException e) {
                    count = 0;
                }
                currentData.setValue(Integer.toString(count));
                return Transaction.success(currentData);
            }
            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if(count > 0){
                    dbRefTrazenaDonacija.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            received = Integer.parseInt(receivedNum);
                            count = count - received;
                            currentData.setValue(Integer.toString(count));
                            if(count <= 0){
                                count = 0;
                                dbRefTrazenaDonacija.setValue(Integer.toString(count));
                            }
                            return Transaction.success(currentData);
                        }
                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                            dbRefZaprimljenaDonacija.child(sReceivedDonation).removeValue();
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Uspješno ažurirano!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ReceivedDonationsActivity.this, AdminActivity.class));
                            finish();
                        }
                    });
                }
                else{
                    count = 0;
                    dbRefTrazenaDonacija.setValue(Integer.toString(count));
                    dbRefZaprimljenaDonacija.child(sReceivedDonation).removeValue();
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Količina donacija ispunjena!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ReceivedDonationsActivity.this, AdminActivity.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ReceivedDonationItemBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_received_donations);

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

        rvReceivedDonations = findViewById(R.id.rvReceivedDonations);
        rvReceivedDonations.setHasFixedSize(true);
        rvReceivedDonations.setLayoutManager(new LinearLayoutManager(this));

        lDonations = new ArrayList<>();
        adminReceivedDonationsAdapter = new AdminReceivedDonationsAdapter(this, lDonations);
        rvReceivedDonations.setAdapter(adminReceivedDonationsAdapter);

        dbRefAdmin = FirebaseDatabase.getInstance().getReference("admin");
        dbRefSklonisteAdmin = FirebaseDatabase.getInstance().getReference("skloniste_admin");
        dbRefSklonisteDonacija = FirebaseDatabase.getInstance().getReference("skloniste_donacija");
        dbRefTrazenaDonacija = FirebaseDatabase.getInstance().getReference("trazena_donacija");
        dbRefTrazenoZaprimljeno = FirebaseDatabase.getInstance().getReference("trazeno_zaprimljeno");
        dbRefZaprimljenaDonacija = FirebaseDatabase.getInstance().getReference("zaprimljena_donacija");

        Query adminEmailQuery = dbRefAdmin.orderByChild("email").equalTo(firebaseUser.getEmail());
        adminEmailQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Admin admin = dataSnapshot.getValue(Admin.class);
                    assert admin != null;
                    String sAdminUser = admin.getUsername();

                    Query adminShelterQuery = dbRefSklonisteAdmin.orderByChild("admin").equalTo(sAdminUser);
                    adminShelterQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                Shelter_Admin shelter_admin = dataSnapshot1.getValue(Shelter_Admin.class);
                                assert shelter_admin != null;
                                String sShelterOib = shelter_admin.getSkloniste();

                                Query shelterDonationQuery = dbRefSklonisteDonacija.orderByChild("skloniste").equalTo(sShelterOib);
                                shelterDonationQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                            Shelter_Donation shelter_donation = dataSnapshot2.getValue(Shelter_Donation.class);
                                            assert shelter_donation != null;
                                            String sDonationId = shelter_donation.getDonacija();

                                            Query donationQuery = dbRefTrazenaDonacija.orderByChild("sifra").equalTo(sDonationId);
                                            donationQuery.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot dataSnapshot3 : snapshot.getChildren()){
                                                        RequestedDonation requestedDonation = dataSnapshot3.getValue(RequestedDonation.class);
                                                        assert requestedDonation != null;
                                                        sRequestedDonation = requestedDonation.getSifra();

                                                        Query requestedDonationQuery = dbRefTrazenoZaprimljeno.orderByChild("trazeno").equalTo(sRequestedDonation);
                                                        requestedDonationQuery.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for(DataSnapshot dataSnapshot4 : snapshot.getChildren()){
                                                                    Requested_Received requested_received = dataSnapshot4.getValue(Requested_Received.class);
                                                                    assert requested_received != null;
                                                                    sReceivedDonation = requested_received.getZaprimljeno();

                                                                    Query receivedDonationQuery = dbRefZaprimljenaDonacija.orderByChild("sifra").equalTo(sReceivedDonation);
                                                                    receivedDonationQuery.addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            for(DataSnapshot dataSnapshot5 : snapshot.getChildren()){
                                                                                ReceivedDonation receivedDonation = dataSnapshot5.getValue(ReceivedDonation.class);
                                                                                assert receivedDonation != null;
                                                                                receivedNum = receivedDonation.getKolicina();
                                                                                lDonations.add(receivedDonation);
                                                                            }
                                                                            adminReceivedDonationsAdapter.notifyDataSetChanged();
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

        search_receivedDonations = findViewById(R.id.search_receivedDonations);
        search_receivedDonations.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adminReceivedDonationsAdapter.getFilter().filter(s);
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