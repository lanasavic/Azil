package com.example.azil.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.azil.Models.Animal_Breed;
import com.example.azil.Models.Animal_Location;
import com.example.azil.Models.Breed;
import com.example.azil.Models.Location;
import com.example.azil.R;
import com.example.azil.databinding.ActivityGraphBreedLocationBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GraphBreedLocationActivity extends AppCompatActivity {
    private ActivityGraphBreedLocationBinding binding;
    private Button btnBack;
    BarChart barChart;
    DatabaseReference dbRefPasmina, dbRefZivotinjaPasmina, dbRefZivotinjaLokacija, dbRefLokacija;
    XAxis xAxis;
    YAxis leftYAxis, rightYAxis;
    ArrayList<IBarDataSet> lBarDataSets = new ArrayList<>();
    BarDataSet barDataSet;
    BarData barData;
    private ArrayList<Location> lLocation;
    private ArrayList<Breed> breedArrayList;
    ArrayList<Location> lCakovec, lKarlovac, lKoprivnica, lSisak, lVarazdin, lVirovitica, lZagreb;
    ArrayList<BarEntry> dataVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGraphBreedLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.dropdownBreed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breedPickDialog();
            }
        });

        loadBreeds();

        barChart = findViewById(R.id.graphBreedLocation);
        xAxis = barChart.getXAxis();
        leftYAxis = barChart.getAxisLeft();
        rightYAxis = barChart.getAxisRight();

        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(8);
        leftYAxis.setAxisMinimum(0);
        leftYAxis.setLabelCount(2);
        rightYAxis.setAxisMinimum(0);
        rightYAxis.setLabelCount(5);

        barChart.setNoDataText("Nema podataka za prikaz");
        barChart.setNoDataTextColor(Color.RED);
    }

    private void loadBreeds() {
        breedArrayList = new ArrayList<>();

        dbRefPasmina = FirebaseDatabase.getInstance().getReference("pasmina");
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
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void breedPickDialog() {
        String[] breedArray = new String[breedArrayList.size()];
        for (int i=0; i<breedArrayList.size(); i++){
            breedArray[i] = breedArrayList.get(i).getNaziv();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Odaberite pasminu").setItems(breedArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String breed = breedArray[which];
                binding.dropdownBreed.setText(breed);
                retrieveData(breed);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(650, 800);
    }

    private void retrieveData(String chosenBreed) {
        dbRefPasmina = FirebaseDatabase.getInstance().getReference("pasmina");
        dbRefZivotinjaPasmina = FirebaseDatabase.getInstance().getReference("zivotinja_pasmina");
        dbRefZivotinjaLokacija = FirebaseDatabase.getInstance().getReference("zivotinja_lokacija");
        dbRefLokacija = FirebaseDatabase.getInstance().getReference("lokacija");

        lLocation = new ArrayList<>();

        dataVals = new ArrayList<>();
        lCakovec = new ArrayList<>();
        lKarlovac = new ArrayList<>();
        lKoprivnica = new ArrayList<>();
        lSisak = new ArrayList<>();
        lVarazdin = new ArrayList<>();
        lVirovitica = new ArrayList<>();
        lZagreb = new ArrayList<>();

        Query breedQuery = dbRefPasmina.orderByChild("naziv").equalTo(chosenBreed);
        breedQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChildren()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Breed breed = dataSnapshot.getValue(Breed.class);
                        assert breed != null;
                        String pasminaId = breed.getSifra();

                        Query animalBreedQuery = dbRefZivotinjaPasmina.orderByChild("pasmina").equalTo(pasminaId);
                        animalBreedQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                    Animal_Breed animal_breed = dataSnapshot1.getValue(Animal_Breed.class);
                                    assert animal_breed != null;
                                    String zivotinjaId = animal_breed.getZivotinja();

                                    Query animalLocationQuery = dbRefZivotinjaLokacija.orderByChild("zivotinja").equalTo(zivotinjaId);
                                    animalLocationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                                Animal_Location animal_location = dataSnapshot2.getValue(Animal_Location.class);
                                                assert animal_location != null;
                                                String lokacijaId = animal_location.getLokacija();

                                                Query locationQuery = dbRefLokacija.orderByChild("sifra").equalTo(lokacijaId);
                                                locationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot dataSnapshot3 : snapshot.getChildren()){
                                                            Location location = dataSnapshot3.getValue(Location.class);
                                                            lLocation.add(location);

                                                            assert location != null;
                                                            int locationNum = Integer.parseInt(location.getSifra());

                                                            switch(locationNum){
                                                                case 1:
                                                                    lCakovec.add(location);
                                                                    dataVals.add(new BarEntry(1, lCakovec.size()));
                                                                    Log.d("CK", String.valueOf(lCakovec.size()));
                                                                    break;
                                                                case 2:
                                                                    lKarlovac.add(location);
                                                                    dataVals.add(new BarEntry(2, lKarlovac.size()));
                                                                    Log.d("KA", String.valueOf(lKarlovac.size()));
                                                                    break;
                                                                case 3:
                                                                    lKoprivnica.add(location);
                                                                    dataVals.add(new BarEntry(3, lKoprivnica.size()));
                                                                    Log.d("KC", String.valueOf(lKoprivnica.size()));
                                                                    break;
                                                                case 4:
                                                                    lSisak.add(location);
                                                                    dataVals.add(new BarEntry(4, lSisak.size()));
                                                                    Log.d("SK", String.valueOf(lSisak.size()));
                                                                    break;
                                                                case 5:
                                                                    lVarazdin.add(location);
                                                                    dataVals.add(new BarEntry(5, lVarazdin.size()));
                                                                    Log.d("VZ", String.valueOf(lVarazdin.size()));
                                                                    break;
                                                                case 6:
                                                                    lVirovitica.add(location);
                                                                    dataVals.add(new BarEntry(6, lVirovitica.size()));
                                                                    Log.d("VT", String.valueOf(lVirovitica.size()));
                                                                    break;
                                                                case 7:
                                                                    lZagreb.add(location);
                                                                    dataVals.add(new BarEntry(7, lZagreb.size()));
                                                                    Log.d("ZG", String.valueOf(lZagreb.size()));
                                                                    break;
                                                            }
                                                        }
                                                        showChart(dataVals);
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
                }else{
                    barChart.clear();
                    barChart.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showChart(ArrayList<BarEntry> dataVals) {
        barDataSet = new BarDataSet(dataVals, "Broj životinja odabrane pasmine po lokacijama");
        lBarDataSets.clear();
        lBarDataSets.add(barDataSet);
        barDataSet.notifyDataSetChanged();
        barDataSet.setColor(Color.RED);
        ValueFormatter vf = new ValueFormatter() { //value format here, here is the overridden method
            @Override
            public String getFormattedValue(float value) {
                return ""+(int)value;
            }
        };
        barDataSet.setValueFormatter(vf);

        String[] areas = new String[]{"", "Čakovec i okolica", "Karlovac i okolica", "Koprivnica i okolica", "Sisak i okolica", "Varaždin i okolica", "Virovitica i okolica", "Zagreb i okolica"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(areas));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelRotationAngle(-30f);

        barData = new BarData(lBarDataSets);
        barData.notifyDataChanged();
        barData.setBarWidth(0.85f);

        barChart.getDescription().setEnabled(false);
        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(6);
        barChart.clear();
        barChart.setData(barData);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }
}