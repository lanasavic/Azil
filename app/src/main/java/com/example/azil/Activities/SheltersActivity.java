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

import com.example.azil.Adapters.AnimalsAdapter;
import com.example.azil.Models.Admin;
import com.example.azil.Models.Animal;
import com.example.azil.Models.Shelter;
import com.example.azil.Models.Shelter_Admin;
import com.example.azil.Models.Shelter_Animal;
import com.example.azil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SheltersActivity extends AppCompatActivity {
    private Button btnBack;
    DatabaseReference dbRefSkloniste, dbRefAdmin, dbRefZivotinja, dbRefSklonisteAdmin,  dbRefSklonisteZivotinja;
    TextView tvNaziv, tvAdresa, tvGrad, tvOib, tvIban, tvDostupnihMjesta, tvKontakt;
    private String sNaziv, sAdresa, sGrad, sOib, sIban, sDostupnihMjesta;
    Intent intent;
    Shelter shelter;
    ArrayList<Animal> lAnimals;
    private AnimalsAdapter animalsAdapter;
    RecyclerView rvAnimals;
    EditText search_animals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelters);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvNaziv = findViewById(R.id.tvNaziv);
        tvAdresa = findViewById(R.id.tvAdresa);
        tvGrad = findViewById(R.id.tvGrad);
        tvOib = findViewById(R.id.tvOib);
        tvIban = findViewById(R.id.tvIban);
        tvDostupnihMjesta = findViewById(R.id.tvDostupnihMjesta);
        tvKontakt = findViewById(R.id.tvKontakt);

        intent = getIntent();
        if(intent != null){
            shelter = (Shelter) intent.getSerializableExtra("data");
            sNaziv = shelter.getNaziv();
            sAdresa = shelter.getAdresa();
            sGrad = shelter.getGrad();
            sOib = shelter.getOib();
            sIban = shelter.getIban();
            sDostupnihMjesta = shelter.getDostupnih_mjesta();
        }

        tvNaziv.setText(sNaziv);
        tvAdresa.setText(sAdresa);
        tvGrad.setText(sGrad);
        tvOib.setText(sOib);
        tvIban.setText(sIban);
        tvDostupnihMjesta.setText(sDostupnihMjesta);

        //DATABASE REFERENCES
        dbRefSkloniste = FirebaseDatabase.getInstance().getReference("skloniste");
        dbRefAdmin = FirebaseDatabase.getInstance().getReference("admin");
        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
        dbRefSklonisteAdmin = FirebaseDatabase.getInstance().getReference("skloniste_admin");
        dbRefSklonisteZivotinja = FirebaseDatabase.getInstance().getReference("skloniste_zivotinja");

        Query shelterAdminQuery = dbRefSklonisteAdmin.orderByChild("skloniste").equalTo(sOib);
        shelterAdminQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Shelter_Admin shelter_admin = dataSnapshot.getValue(Shelter_Admin.class);
                    assert shelter_admin != null;
                    String sAdminUser = shelter_admin.getAdmin();
                    Query adminQuery = dbRefAdmin.orderByChild("username").equalTo(sAdminUser);
                    //Toast.makeText(getApplicationContext(), "KEY:" + sAdminUser, Toast.LENGTH_SHORT).show();

                    adminQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                Admin admin = dataSnapshot1.getValue(Admin.class);
                                assert admin != null;
                                tvKontakt.setText(admin.getEmail());
                                //Toast.makeText(getApplicationContext(), "Email:" + admin.getEmail(), Toast.LENGTH_SHORT).show();
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
        search_animals = findViewById(R.id.search_animals);
        search_animals.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    animalsAdapter.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d("search_animals:ERR_", e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //RECYCLERVIEW ITEM INSERT + DATA
        rvAnimals = findViewById(R.id.rvAnimals);
        rvAnimals.setHasFixedSize(true);
        rvAnimals.setLayoutManager(new LinearLayoutManager(this));

        lAnimals = new ArrayList<>();
        animalsAdapter = new AnimalsAdapter(this, lAnimals);//, this::selectedAnimal
        rvAnimals.setAdapter(animalsAdapter);

        Query shelterAnimalQuery = dbRefSklonisteZivotinja.orderByChild("skloniste").equalTo(sOib);
        shelterAnimalQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Shelter_Animal shelter_animal = dataSnapshot.getValue(Shelter_Animal.class);
                    assert shelter_animal != null;
                    String sAnimal = shelter_animal.getZivotinja();
                    Log.d("sifra", sAnimal);

                    Query animalQuery = dbRefZivotinja.orderByChild("sifra").equalTo(sAnimal);
                    animalQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                Animal animal = dataSnapshot1.getValue(Animal.class);
                                lAnimals.add(animal);
                            }
                            animalsAdapter.notifyDataSetChanged();
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

    public void selectedAnimal(Animal animal) {
        startActivity(new Intent(this,SheltersActivity.class).putExtra("data", animal));
    }
}