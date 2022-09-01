package com.example.azil.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azil.Activities.AdoptActivity;
import com.example.azil.Adapters.AllAnimalsAdapter;
import com.example.azil.Models.Animal;
import com.example.azil.Models.Animal_Location;
import com.example.azil.Models.Location;
import com.example.azil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LocationFragment extends Fragment {
    View view;
    TextView dropdownLocation;
    private ArrayList<Location> locationArrayList;
    DatabaseReference dbRefZivotinja, dbRefLokacija, dbRefZivotinjaLokacija;
    RecyclerView rvLocation;
    ArrayList<Animal> lAnimals;
    private AllAnimalsAdapter allAnimalsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_location, container, false);

        dropdownLocation = view.findViewById(R.id.dropdownLocation);
        dropdownLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationPickDialog();
            }
        });

        //DATABASE REFERENCES
        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
        dbRefLokacija = FirebaseDatabase.getInstance().getReference("lokacija");
        dbRefZivotinjaLokacija = FirebaseDatabase.getInstance().getReference("zivotinja_lokacija");

        rvLocation = view.findViewById(R.id.rvLocation);
        rvLocation.setHasFixedSize(true);
        rvLocation.setLayoutManager(new LinearLayoutManager(getActivity()));

        lAnimals = new ArrayList<>();
        allAnimalsAdapter = new AllAnimalsAdapter(getActivity(), lAnimals, this::selectedAnimal);
        rvLocation.setAdapter(allAnimalsAdapter);

        ImageView ivNoResult = view.findViewById(R.id.ivNoResult);
        allAnimalsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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
                ivNoResult.setVisibility(allAnimalsAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        });

        dbRefZivotinja.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Animal animals = dataSnapshot.getValue(Animal.class);
                    lAnimals.add(animals);
                }
                allAnimalsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        loadLocations();

        return view;
    }

    private void loadLocations() {
        locationArrayList = new ArrayList<>();

        dbRefLokacija.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Location location = dataSnapshot.getValue(Location.class);
                    locationArrayList.add(location);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void locationPickDialog() {
        String[] locationArray = new String[locationArrayList.size()];
        for (int i=0; i<locationArrayList.size(); i++){
            locationArray[i] = locationArrayList.get(i).getNaziv();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Odaberite lokaciju pronalaska").setItems(locationArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String location = locationArray[which];
                dropdownLocation.setText(location);
                retrieveData(location);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(950, 1050);
    }

    private void retrieveData(String chosenLocation) {
        Query locationQuery = dbRefLokacija.orderByChild("naziv").equalTo(chosenLocation);
        locationQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Location location = dataSnapshot.getValue(Location.class);
                    assert location != null;
                    String lokacijaId = location.getSifra();

                    Query animalLocationQuery = dbRefZivotinjaLokacija.orderByChild("lokacija").equalTo(lokacijaId);
                    animalLocationQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                Animal_Location animal_location = dataSnapshot1.getValue(Animal_Location.class);
                                assert animal_location != null;
                                String zivotinjaId = animal_location.getZivotinja();

                                Query animalQuery = dbRefZivotinja.orderByChild("sifra").equalTo(zivotinjaId);
                                animalQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                            Animal animal = dataSnapshot2.getValue(Animal.class);
                                            lAnimals.add(animal);
                                        }
                                        allAnimalsAdapter.notifyDataSetChanged();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void selectedAnimal(Animal animal) {
        startActivity(new Intent(getContext(), AdoptActivity.class).putExtra("data2", animal));
    }
}