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
    TextView tvSkloniste, tvOpis, tvKolicina;
    String sNaziv;
    ArrayList<RequestedDonation> lDonations;
    private DonationsAdapter donationsAdapter;
    RecyclerView rvDonations;
    EditText search_donations;
    DatabaseReference dbRefSkloniste, dbRefSklonisteDonacija, dbRefTrazenaDonacija;
    DonationItemBinding binding;
    Intent intent;
    Shelter shelter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DonationItemBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_donations);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SheltersActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvSkloniste = binding.tvSkloniste;
        intent = getIntent();
        if(intent != null){
            shelter = (Shelter) intent.getSerializableExtra("data");
            sNaziv = shelter.getNaziv();
        }
        tvSkloniste.setText(sNaziv);

        //RECYCLERVIEW ITEM INSERT + DATA
        rvDonations = findViewById(R.id.rvDonations);
        rvDonations.setHasFixedSize(true);
        rvDonations.setLayoutManager(new LinearLayoutManager(this));

        lDonations = new ArrayList<>();
        donationsAdapter = new DonationsAdapter(this, lDonations);//, this::selectedAnimal
        rvDonations.setAdapter(donationsAdapter);

        //DATABASE REFERENCES
        dbRefSkloniste = FirebaseDatabase.getInstance().getReference("skloniste");
        dbRefSklonisteDonacija = FirebaseDatabase.getInstance().getReference("skloniste_donacija");
        dbRefTrazenaDonacija = FirebaseDatabase.getInstance().getReference("trazena_donacija");

        Query shelterQuery = dbRefSkloniste.orderByChild("naziv").equalTo(sNaziv);
        shelterQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Shelter shelter = dataSnapshot.getValue(Shelter.class);
                    assert shelter != null;
                    String skloniste = shelter.getOib();
                    //Log.d("test", "SHELTER:"+shelter.getNaziv()+" "+sNaziv);

                    Query shelterDonationQuery = dbRefSklonisteDonacija.orderByChild("skloniste").equalTo(skloniste);
                    shelterDonationQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Shelter_Donation shelter_donation = dataSnapshot.getValue(Shelter_Donation.class);
                                assert shelter_donation != null;
                                String donacija = shelter_donation.getDonacija();
                                //Log.d("test", "SHELTER DONATION:"+shelter_donation.getSkloniste()+" "+shelter_donation.getDonacija()+" "+donacija);

                                Query donationQuery = dbRefTrazenaDonacija.orderByChild("sifra").equalTo(donacija);
                                donationQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                            RequestedDonation requestedDonation = dataSnapshot1.getValue(RequestedDonation.class);
                                            assert requestedDonation != null;
                                            binding.tvOpisDonacije.setText(requestedDonation.getOpis());
                                            binding.tvKolicinaDonacije.setText(requestedDonation.getKolicina());
                                            Log.d("test", "DONATION:"+requestedDonation.getOpis()+" "+requestedDonation.getKolicina());
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

        //SEARCH
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
                    Log.d("search_donations:ERR_", e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}