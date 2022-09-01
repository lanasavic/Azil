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
import com.example.azil.Models.Animal_Breed;
import com.example.azil.Models.Breed;
import com.example.azil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BreedFragment extends Fragment {
    View view;
    TextView dropdownBreed;
    private ArrayList<Breed> breedArrayList;
    DatabaseReference dbRefZivotinja, dbRefPasmina, dbRefZivotinjaPasmina;
    RecyclerView rvBreed;
    ArrayList<Animal> lAnimals;
    private AllAnimalsAdapter allAnimalsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_breed, container, false);

        dropdownBreed = view.findViewById(R.id.dropdownBreed);
        dropdownBreed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breedPickDialog();
            }
        });

        //DATABASE REFERENCES
        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
        dbRefPasmina = FirebaseDatabase.getInstance().getReference("pasmina");
        dbRefZivotinjaPasmina = FirebaseDatabase.getInstance().getReference("zivotinja_pasmina");

        rvBreed = view.findViewById(R.id.rvBreed);
        rvBreed.setHasFixedSize(true);
        rvBreed.setLayoutManager(new LinearLayoutManager(getActivity()));

        lAnimals = new ArrayList<>();
        allAnimalsAdapter = new AllAnimalsAdapter(getActivity(), lAnimals, this::selectedAnimal);
        rvBreed.setAdapter(allAnimalsAdapter);

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

        loadBreeds();

        return view;
    }

    private void loadBreeds() {
        breedArrayList = new ArrayList<>();

        dbRefPasmina.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                breedArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Breed breed = dataSnapshot.getValue(Breed.class);
                    breedArrayList.add(breed);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void breedPickDialog() {
        String[] breedArray = new String[breedArrayList.size()];
        for (int i=0; i<breedArrayList.size(); i++){
            breedArray[i] = breedArrayList.get(i).getNaziv();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Odaberite pasminu").setItems(breedArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String breed = breedArray[which];
                dropdownBreed.setText(breed);
                retrieveData(breed);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(950, 1050);
    }

    private void retrieveData(String chosenBreed) {
        Query breedQuery = dbRefPasmina.orderByChild("naziv").equalTo(chosenBreed);
        breedQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Breed breed = dataSnapshot.getValue(Breed.class);
                    assert breed != null;
                    String pasminaId = breed.getSifra();

                    Query animalBreedQuery = dbRefZivotinjaPasmina.orderByChild("pasmina").equalTo(pasminaId);
                    animalBreedQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                Animal_Breed animal_breed = dataSnapshot1.getValue(Animal_Breed.class);
                                assert animal_breed != null;
                                String zivotinjaId = animal_breed.getZivotinja();

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