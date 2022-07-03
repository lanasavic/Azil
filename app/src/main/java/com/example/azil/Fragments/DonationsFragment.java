package com.example.azil.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.azil.Activities.AddDonationActivity;
import com.example.azil.Activities.ReceivedDonationsActivity;
import com.example.azil.Adapters.AdminDonationsAdapter;
import com.example.azil.Models.Admin;
import com.example.azil.Models.RequestedDonation;
import com.example.azil.Models.Shelter_Admin;
import com.example.azil.Models.Shelter_Donation;
import com.example.azil.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class DonationsFragment extends Fragment {
    private Button btnAddDonation, btnReceivedDonations;
    private RecyclerView rvAdminDonations;
    private EditText search_fragmentDonations;
    private AdminDonationsAdapter adminDonationsAdapter;
    DatabaseReference dbRefAdmin, dbRefTrazenaDonacija, dbRefSklonisteAdmin, dbRefSklonisteDonacija;
    FirebaseUser firebaseUser;
    ArrayList<RequestedDonation> lDonations;
    Intent intent;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_donations, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnAddDonation = view.findViewById(R.id.btnAddDonation);
        btnAddDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), AddDonationActivity.class);
                intent.putExtra("adminEmail", firebaseUser.getEmail());
                startActivity(intent);
            }
        });

        btnReceivedDonations = view.findViewById(R.id.btnReceivedDonations);
        btnReceivedDonations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), ReceivedDonationsActivity.class);
                intent.putExtra("adminEmail", firebaseUser.getEmail());
                startActivity(intent);
            }
        });

        dbRefAdmin = FirebaseDatabase.getInstance().getReference("admin");
        dbRefTrazenaDonacija = FirebaseDatabase.getInstance().getReference("trazena_donacija");
        dbRefSklonisteAdmin = FirebaseDatabase.getInstance().getReference("skloniste_admin");
        dbRefSklonisteDonacija = FirebaseDatabase.getInstance().getReference("skloniste_donacija");

        rvAdminDonations = view.findViewById(R.id.rvAdminDonations);
        rvAdminDonations.setHasFixedSize(true);
        rvAdminDonations.setLayoutManager(new LinearLayoutManager(getActivity()));

        lDonations = new ArrayList<>();
        adminDonationsAdapter = new AdminDonationsAdapter(getActivity(), lDonations);
        rvAdminDonations.setAdapter(adminDonationsAdapter);

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

                                            Query animalQuery = dbRefTrazenaDonacija.orderByChild("sifra").equalTo(sDonationId);
                                            animalQuery.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot dataSnapshot3 : snapshot.getChildren()){
                                                        RequestedDonation requestedDonation = dataSnapshot3.getValue(RequestedDonation.class);
                                                        lDonations.add(requestedDonation);
                                                    }
                                                    adminDonationsAdapter.notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        search_fragmentDonations = view.findViewById(R.id.search_fragmentDonations);
        search_fragmentDonations.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adminDonationsAdapter.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d("ERROR", "Error:" + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return view;
    }
}