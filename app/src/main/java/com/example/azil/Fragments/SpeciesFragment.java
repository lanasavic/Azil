package com.example.azil.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azil.Activities.AdoptActivity;
import com.example.azil.Adapters.AllAnimalsAdapter;
import com.example.azil.Models.Animal;
import com.example.azil.Models.Animal_Species;
import com.example.azil.Models.Species;
import com.example.azil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpeciesFragment extends Fragment {
    View view;
    TextView dropdownSpecies;
    private ArrayList<Species> speciesArrayList;
    DatabaseReference dbRefZivotinja, dbRefVrsta, dbRefZivotinjaVrsta;
    RecyclerView rvSpecies;
    ArrayList<Animal> lAnimals;
    private AllAnimalsAdapter allAnimalsAdapter;
    private EditText search_fragmentSpecies, hiddenSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_species, container, false);

        dropdownSpecies = view.findViewById(R.id.dropdownSpecies);
        dropdownSpecies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speciesPickDialog();
            }
        });

        search_fragmentSpecies = view.findViewById(R.id.search_fragmentSpecies);
        search_fragmentSpecies.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    allAnimalsAdapter.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d("ERROR", "Error:" + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //DATABASE REFERENCES
        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
        dbRefVrsta = FirebaseDatabase.getInstance().getReference("vrsta");
        dbRefZivotinjaVrsta = FirebaseDatabase.getInstance().getReference("zivotinja_vrsta");

        rvSpecies = view.findViewById(R.id.rvSpecies);
        rvSpecies.setHasFixedSize(true);
        rvSpecies.setLayoutManager(new LinearLayoutManager(getActivity()));

        lAnimals = new ArrayList<>();
        allAnimalsAdapter = new AllAnimalsAdapter(getActivity(), lAnimals, this::selectedAnimal);
        rvSpecies.setAdapter(allAnimalsAdapter);

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

        loadSpecies();

        return view;
    }

    private void loadSpecies() {
        speciesArrayList = new ArrayList<>();

        dbRefVrsta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                speciesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Species species = dataSnapshot.getValue(Species.class);
                    speciesArrayList.add(species);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void speciesPickDialog() {
        String[] speciesArray = new String[speciesArrayList.size()];
        for (int i=0; i<speciesArrayList.size(); i++){
            speciesArray[i] = speciesArrayList.get(i).getNaziv();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Odaberite vrstu").setItems(speciesArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String species = speciesArray[which];
                dropdownSpecies.setText(species);
                //retrieveData(species);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //alertDialog.getWindow().setLayout(950, 1050);
    }

    private void retrieveData(String chosenSpecies) {
        //Log.d("tag", chosenSpecies);
        //lAnimals.clear();
        Query speciesQuery = dbRefVrsta.orderByChild("naziv").equalTo(chosenSpecies);
        speciesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Species species = dataSnapshot.getValue(Species.class);
                    assert species != null;
                    String vrstaId = species.getSifra();

                    Query animalSpeciesQuery = dbRefZivotinjaVrsta.orderByChild("vrsta").equalTo(vrstaId);
                    animalSpeciesQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                Animal_Species animal_species = dataSnapshot1.getValue(Animal_Species.class);
                                assert animal_species != null;
                                String zivotinjaId = animal_species.getZivotinja();

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