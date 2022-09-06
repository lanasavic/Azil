package com.example.azil.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azil.Models.Breed;
import com.example.azil.Models.Location;
import com.example.azil.Models.RequestedDonation;
import com.example.azil.Models.Species;
import com.example.azil.Models.Time;
import com.example.azil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class StatisticsDataActivity extends AppCompatActivity {
    private Button btnBack;
    private TextView tvVrijeme, tvVrste, tvLokacijeMedijan, tvPasmineMedijan, tvPasmineMod;
    private ArrayList<Species> speciesArrayList;
    private ArrayList<Breed> breedArrayList;
    private ArrayList<Location> locationArrayList;
    private ArrayList<Time> timeArrayList;
    private ArrayList<Double> lAnimalsMonths, lAnimalsLocations, lAnimalsBreeds;
    private String animalsMonthsNum, dogsNum, catsNum, animalsLocationNum, animalsBreedNum;
    DatabaseReference dbRefVrsta, dbRefZivotinjaVrsta, dbRefPasmina, dbRefZivotinjaPasmina,
            dbRefLokacija, dbRefZivotinjaLokacija, dbRefVrijeme, dbRefZivotinjaVrijeme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_data);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvVrijeme = findViewById(R.id.tvVrijeme);
        tvVrste = findViewById(R.id.tvVrste);
        tvLokacijeMedijan = findViewById(R.id.tvLokacijeMedijan);
        tvPasmineMedijan = findViewById(R.id.tvPasmineMedijan);
        tvPasmineMod = findViewById(R.id.tvPasmineMod);

        //DATABASE REFERENCES
        dbRefVrsta = FirebaseDatabase.getInstance().getReference("vrsta");
        dbRefPasmina = FirebaseDatabase.getInstance().getReference("pasmina");
        dbRefLokacija = FirebaseDatabase.getInstance().getReference("lokacija");
        dbRefVrijeme = FirebaseDatabase.getInstance().getReference("vrijeme");
        dbRefZivotinjaVrsta = FirebaseDatabase.getInstance().getReference("zivotinja_vrsta");
        dbRefZivotinjaPasmina = FirebaseDatabase.getInstance().getReference("zivotinja_pasmina");
        dbRefZivotinjaLokacija = FirebaseDatabase.getInstance().getReference("zivotinja_lokacija");
        dbRefZivotinjaVrijeme = FirebaseDatabase.getInstance().getReference("zivotinja_vrijeme");

        loadAnimalsMonths();
        loadAnimalsSpecies();
        loadAnimalsLocations();
        loadAnimalsBreeds();
    }

    //ARITMETIČKA SREDINA
    private void loadAnimalsMonths() {
        timeArrayList = new ArrayList<>();
        lAnimalsMonths = new ArrayList<>();

        dbRefVrijeme.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timeArrayList.clear();
                lAnimalsMonths.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Time time = dataSnapshot.getValue(Time.class);
                    timeArrayList.add(time);
                    assert time != null;
                    animalsMonthsNum = time.getBroj_zivotinja();

                    double doubleAnimalsNums = Double.parseDouble(animalsMonthsNum);
                    lAnimalsMonths.add(doubleAnimalsNums);
                }
                //System.out.println(lAnimalsMonths);

                double sum = 0;
                for(int i=0; i<lAnimalsMonths.size(); i++){
                    sum = sum + lAnimalsMonths.get(i);
                }
                double mean = sum/lAnimalsMonths.size(); //arithmetic mean

                DecimalFormat df = new DecimalFormat("#.###");
                df.setRoundingMode(RoundingMode.CEILING);

                tvVrijeme.setText(df.format(mean));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //OMJER
    private void loadAnimalsSpecies() {
        speciesArrayList = new ArrayList<>();

        dbRefVrsta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                speciesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Species species = dataSnapshot.getValue(Species.class);
                    speciesArrayList.add(species);
                    assert species != null;

                    Query dogsQuery = dbRefVrsta.orderByChild("naziv").equalTo("Pas");
                    dogsQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                Species species = dataSnapshot1.getValue(Species.class);
                                assert species != null;
                                dogsNum = species.getBroj_zivotinja();

                                Query catsQuery = dbRefVrsta.orderByChild("naziv").equalTo("Mačka");
                                catsQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                            Species species = dataSnapshot2.getValue(Species.class);
                                            assert species != null;
                                            catsNum = species.getBroj_zivotinja();
                                            int catsNumInt = Integer.parseInt(catsNum);

                                            if(catsNumInt == 0){
                                                tvVrste.setText("Psi: "+dogsNum+", podataka o mačkama nema.\nOmjer nije moguće prikazati.");
                                            }
                                            else{
                                                tvVrste.setText(dogsNum + " : " + catsNum);
                                            }
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

    //MEDIJAN
    private void loadAnimalsLocations() {
        locationArrayList = new ArrayList<>();
        lAnimalsLocations = new ArrayList<>();

        dbRefLokacija.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationArrayList.clear();
                lAnimalsLocations.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Location location = dataSnapshot.getValue(Location.class);
                    locationArrayList.add(location);
                    assert location != null;
                    animalsLocationNum = location.getBroj_zivotinja();

                    double doubleAnimalsNums = Double.parseDouble(animalsLocationNum);
                    lAnimalsLocations.add(doubleAnimalsNums);
                }
                //System.out.println(lAnimalsLocations);

                Object[] numArray = lAnimalsLocations.toArray();
                Arrays.sort(numArray);
                //System.out.println(Arrays.toString(numArray));

                double median;
                if (numArray.length % 2 == 0)
                    median = ((double)numArray[numArray.length/2] + (double)numArray[numArray.length/2 - 1])/2;
                else
                    median = (double) numArray[numArray.length/2];

                tvLokacijeMedijan.setText(Double.toString(median));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAnimalsBreeds() {
        breedArrayList = new ArrayList<>();
        lAnimalsBreeds = new ArrayList<>();

        dbRefPasmina.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                breedArrayList.clear();
                lAnimalsBreeds.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Breed breed = dataSnapshot.getValue(Breed.class);
                    breedArrayList.add(breed);
                    assert breed != null;
                    animalsLocationNum = breed.getBroj_zivotinja();

                    double doubleAnimalsNums = Double.parseDouble(animalsLocationNum);
                    lAnimalsBreeds.add(doubleAnimalsNums);
                }
                //System.out.println(lAnimalsBreeds);

                //MEDIJAN
                Object[] numArray = lAnimalsBreeds.toArray();
                Arrays.sort(numArray);
                //System.out.println(Arrays.toString(numArray));

                double median;
                if (numArray.length % 2 == 0)
                    median = ((double)numArray[numArray.length/2] + (double)numArray[numArray.length/2 - 1])/2;
                else
                    median = (double) numArray[numArray.length/2];

                tvPasmineMedijan.setText(Double.toString(median));

                //MOD
                double epsilon = 0.001; //TOLERANCE
                double maxValue = -1.0d;
                int maxCount = 0;
                for (int i = 0; i < numArray.length; ++i) {
                    double currentValue = (double) numArray[i];
                    int currentCount = 1;
                    for (int j = i + 1; j < numArray.length; ++j) {
                        if (Math.abs((double) numArray[j] - currentValue) < epsilon) {
                            ++currentCount;
                        }
                    }
                    if (currentCount > maxCount) {
                        maxCount = currentCount;
                        maxValue = currentValue;
                    } else if (currentCount == maxCount) {
                        maxValue = Double.NaN;
                    }
                }
                //System.out.println("mode: " + maxValue);
                if(Double.isNaN(maxValue)){
                    tvPasmineMod.setText("Nema vrijednosti koja se ponavlja.");
                }
                else{
                    tvPasmineMod.setText(Double.toString(maxValue));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}