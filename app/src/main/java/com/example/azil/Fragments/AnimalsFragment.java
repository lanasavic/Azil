package com.example.azil.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.azil.Activities.AddAnimalActivity;
import com.example.azil.Activities.DeleteActivity;
import com.example.azil.Activities.EditActivity;
import com.example.azil.Activities.EditAnimalActivity;
import com.example.azil.Adapters.AdminAnimalsAdapter;
import com.example.azil.Adapters.AnimalsAdapter;
import com.example.azil.Filters.AdminAnimalsFilter;
import com.example.azil.Models.Admin;
import com.example.azil.Models.Animal;
import com.example.azil.Models.Shelter;
import com.example.azil.Models.Shelter_Admin;
import com.example.azil.Models.Shelter_Animal;
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


public class AnimalsFragment extends Fragment {
    private Button btnAddAnimal;
    private RecyclerView rvAdminAnimals;
    private EditText search_fragmentAnimals;
    public AdminAnimalsFilter adminAnimalsFilter;
    DatabaseReference dbRefAdmin, dbRefZivotinja, dbRefSklonisteAdmin, dbRefSklonisteZivotinja;
    FirebaseUser firebaseUser;
    ArrayList<Animal> lAnimals;
    private AdminAnimalsAdapter adminAnimalsAdapter;
    Intent intent;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_animals, container, false);

        btnAddAnimal = view.findViewById(R.id.btnAddAnimal);
        btnAddAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), AddAnimalActivity.class);
                intent.putExtra("adminEmail", firebaseUser.getEmail());
                startActivity(intent);
            }
        });

        dbRefAdmin = FirebaseDatabase.getInstance().getReference("admin");
        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
        dbRefSklonisteAdmin = FirebaseDatabase.getInstance().getReference("skloniste_admin");
        dbRefSklonisteZivotinja = FirebaseDatabase.getInstance().getReference("skloniste_zivotinja");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //RECYCLERVIEW ITEM INSERT + DATA
        rvAdminAnimals = view.findViewById(R.id.rvAdminAnimals);
        rvAdminAnimals.setHasFixedSize(true);
        rvAdminAnimals.setLayoutManager(new LinearLayoutManager(getActivity()));

        lAnimals = new ArrayList<>();
        adminAnimalsAdapter = new AdminAnimalsAdapter(getActivity(), lAnimals);//, this::selectedAnimal
        rvAdminAnimals.setAdapter(adminAnimalsAdapter);

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

                                Query shelterAnimalQuery = dbRefSklonisteZivotinja.orderByChild("skloniste").equalTo(sShelterOib);
                                shelterAnimalQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                            Shelter_Animal shelter_animal = dataSnapshot2.getValue(Shelter_Animal.class);
                                            assert shelter_animal != null;
                                            String sAnimalId = shelter_animal.getZivotinja();

                                            Query animalQuery = dbRefZivotinja.orderByChild("sifra").equalTo(sAnimalId);
                                            animalQuery.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot dataSnapshot3 : snapshot.getChildren()){
                                                        Animal animal = dataSnapshot3.getValue(Animal.class);
                                                        lAnimals.add(animal);
                                                    }
                                                    adminAnimalsAdapter.notifyDataSetChanged();
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

        return view;
    }
}