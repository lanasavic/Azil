package com.example.azil.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azil.Adapters.DonationsAdapter;
import com.example.azil.Models.RequestedDonation;
import com.example.azil.Models.Shelter;
import com.example.azil.Models.Shelter_Donation;
import com.example.azil.R;
import com.example.azil.databinding.DonationItemBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DonationsActivity extends AppCompatActivity {
    private Button btnBack;
    ArrayList<RequestedDonation> lDonations;
    private DonationsAdapter donationsAdapter;
    RecyclerView rvDonations;
    EditText search_donations;
    TextView tvDonacijeSklonista;
    DatabaseReference dbRefSkloniste, dbRefSklonisteDonacija, dbRefTrazenaDonacija;
    DonationItemBinding binding;
    String nazivSklonista, opisDonacije, kolicinaDonacije;
    RequestedDonation requestedDonation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DonationItemBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_donations);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nazivSklonista = extras.getString("nazivSklonista");
        }

        tvDonacijeSklonista = findViewById(R.id.tvDonacijeSklonista);
        tvDonacijeSklonista.setText(getString(R.string.helper_skloniste, nazivSklonista));

        rvDonations = findViewById(R.id.rvDonations);
        rvDonations.setHasFixedSize(true);
        rvDonations.setLayoutManager(new LinearLayoutManager(this));

        lDonations = new ArrayList<>();
        donationsAdapter = new DonationsAdapter(this, lDonations, this::selectedDonation);
        rvDonations.setAdapter(donationsAdapter);

        ImageView ivNoResult = findViewById(R.id.ivNoResult);
        donationsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }

            void checkEmpty() {
                ivNoResult.setVisibility(donationsAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        });

        dbRefSkloniste = FirebaseDatabase.getInstance().getReference("skloniste");
        dbRefSklonisteDonacija = FirebaseDatabase.getInstance().getReference("skloniste_donacija");
        dbRefTrazenaDonacija = FirebaseDatabase.getInstance().getReference("trazena_donacija");

        Query shelterQuery = dbRefSkloniste.orderByChild("naziv").equalTo(nazivSklonista);
        shelterQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Shelter shelter = dataSnapshot.getValue(Shelter.class);
                    assert shelter != null;
                    String skloniste = shelter.getOib();
                    //Log.d("test", "SHELTER:"+shelter.getNaziv()+" | "+nazivSklonista);

                    Query shelterDonationQuery = dbRefSklonisteDonacija.orderByChild("skloniste").equalTo(skloniste);
                    shelterDonationQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Shelter_Donation shelter_donation = dataSnapshot.getValue(Shelter_Donation.class);
                                assert shelter_donation != null;
                                String donacija = shelter_donation.getDonacija();
                                //Log.d("test", "SHELTER DONATION:"+shelter_donation.getSkloniste()+" | "+shelter_donation.getDonacija()+" | "+donacija);

                                Query donationQuery = dbRefTrazenaDonacija.orderByChild("sifra").equalTo(donacija);
                                donationQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                            requestedDonation = dataSnapshot1.getValue(RequestedDonation.class);
                                            assert requestedDonation != null;
                                            opisDonacije = requestedDonation.getOpis();
                                            //Log.d("test", opisDonacije);
                                            kolicinaDonacije = requestedDonation.getKolicina();
                                            lDonations.add(requestedDonation);
                                            //Log.d("test", "DONATION:"+requestedDonation.getOpis()+" | "+requestedDonation.getKolicina()+" | "+nazivSklonista);
                                        }
                                        donationsAdapter.notifyDataSetChanged();
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

        search_donations = findViewById(R.id.search_donations);
        search_donations.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    donationsAdapter.getFilter().filter(s);
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

    public void selectedDonation(RequestedDonation requestedDonation) {
        startActivity(new Intent(this, DonateActivity.class).putExtra("data", requestedDonation));
    }
}