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
import com.example.azil.Models.Animal_Time;
import com.example.azil.Models.Breed;
import com.example.azil.Models.Time;
import com.example.azil.R;
import com.example.azil.databinding.ActivityGraphBreedTimeBinding;
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

public class GraphBreedTimeActivity extends AppCompatActivity {
    private ActivityGraphBreedTimeBinding binding;
    private Button btnBack;
    BarChart barChart;
    DatabaseReference dbRefPasmina, dbRefZivotinjaPasmina, dbRefZivotinjaVrijeme, dbRefVrijeme;
    XAxis xAxis;
    YAxis leftYAxis, rightYAxis;
    ArrayList<IBarDataSet> lBarDataSets = new ArrayList<>();
    BarDataSet barDataSet;
    BarData barData;
    private ArrayList<Time> lTime, lSijecanj, lVeljaca, lOzujak, lTravanj, lSvibanj, lLipanj,
            lSrpanj, lKolovoz, lRujan, lListopad, lStudeni, lProsinac;
    private ArrayList<Breed> breedArrayList;
    ArrayList<BarEntry> dataVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGraphBreedTimeBinding.inflate(getLayoutInflater());
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

        barChart = findViewById(R.id.graphBreedTime);
        xAxis = barChart.getXAxis();
        leftYAxis = barChart.getAxisLeft();
        rightYAxis = barChart.getAxisRight();

        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(13);
        leftYAxis.setAxisMinimum(0);
        leftYAxis.setLabelCount(2);
        rightYAxis.setAxisMinimum(0);
        rightYAxis.setLabelCount(9);

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
        dbRefZivotinjaVrijeme = FirebaseDatabase.getInstance().getReference("zivotinja_vrijeme");
        dbRefVrijeme = FirebaseDatabase.getInstance().getReference("vrijeme");

        lTime = new ArrayList<>();

        dataVals = new ArrayList<>();
        lSijecanj = new ArrayList<>();
        lVeljaca = new ArrayList<>();
        lOzujak = new ArrayList<>();
        lTravanj = new ArrayList<>();
        lSvibanj = new ArrayList<>();
        lLipanj = new ArrayList<>();
        lSrpanj = new ArrayList<>();
        lKolovoz = new ArrayList<>();
        lRujan = new ArrayList<>();
        lListopad = new ArrayList<>();
        lStudeni = new ArrayList<>();
        lProsinac = new ArrayList<>();

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

                                    Query animalTimeQuery = dbRefZivotinjaVrijeme.orderByChild("zivotinja").equalTo(zivotinjaId);
                                    animalTimeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                                Animal_Time animal_time = dataSnapshot2.getValue(Animal_Time.class);
                                                assert animal_time != null;
                                                String mjesecId = animal_time.getMjesec();

                                                Query timeQuery = dbRefVrijeme.orderByChild("sifra").equalTo(mjesecId);
                                                timeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot dataSnapshot3 : snapshot.getChildren()){
                                                            Time time = dataSnapshot3.getValue(Time.class);
                                                            lTime.add(time);

                                                            assert time != null;
                                                            int monthNum = Integer.parseInt(time.getSifra());

                                                            switch(monthNum){
                                                                case 1:
                                                                    lSijecanj.add(time);
                                                                    dataVals.add(new BarEntry(1, lSijecanj.size()));
                                                                    Log.d("MJ1", String.valueOf(lSijecanj.size()));
                                                                case 2:
                                                                    lVeljaca.add(time);
                                                                    dataVals.add(new BarEntry(2, lVeljaca.size()));
                                                                    Log.d("MJ2", String.valueOf(lVeljaca.size()));
                                                                    break;
                                                                case 3:
                                                                    lOzujak.add(time);
                                                                    dataVals.add(new BarEntry(3, lOzujak.size()));
                                                                    Log.d("MJ3", String.valueOf(lOzujak.size()));
                                                                    break;
                                                                case 4:
                                                                    lTravanj.add(time);
                                                                    dataVals.add(new BarEntry(4, lTravanj.size()));
                                                                    Log.d("MJ4", String.valueOf(lTravanj.size()));
                                                                    break;
                                                                case 5:
                                                                    lSvibanj.add(time);
                                                                    dataVals.add(new BarEntry(5, lSvibanj.size()));
                                                                    Log.d("MJ5", String.valueOf(lSvibanj.size()));
                                                                    break;
                                                                case 6:
                                                                    lLipanj.add(time);
                                                                    dataVals.add(new BarEntry(6, lLipanj.size()));
                                                                    Log.d("MJ6", String.valueOf(lLipanj.size()));
                                                                    break;
                                                                case 7:
                                                                    lSrpanj.add(time);
                                                                    dataVals.add(new BarEntry(7, lSrpanj.size()));
                                                                    Log.d("MJ7", String.valueOf(lSrpanj.size()));
                                                                    break;
                                                                case 8:
                                                                    lKolovoz.add(time);
                                                                    dataVals.add(new BarEntry(8, lKolovoz.size()));
                                                                    Log.d("MJ8", String.valueOf(lKolovoz.size()));
                                                                    break;
                                                                case 9:
                                                                    lRujan.add(time);
                                                                    dataVals.add(new BarEntry(9, lRujan.size()));
                                                                    Log.d("MJ9", String.valueOf(lRujan.size()));
                                                                    break;
                                                                case 10:
                                                                    lListopad.add(time);
                                                                    dataVals.add(new BarEntry(10, lListopad.size()));
                                                                    Log.d("MJ10", String.valueOf(lListopad.size()));
                                                                    break;
                                                                case 11:
                                                                    lStudeni.add(time);
                                                                    dataVals.add(new BarEntry(11, lStudeni.size()));
                                                                    Log.d("MJ11", String.valueOf(lStudeni.size()));
                                                                    break;
                                                                case 12:
                                                                    lProsinac.add(time);
                                                                    dataVals.add(new BarEntry(12, lProsinac.size()));
                                                                    Log.d("MJ12", String.valueOf(lProsinac.size()));
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
        barDataSet = new BarDataSet(dataVals, "Broj životinja odabrane pasmine po mjesecima");
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

        String[] months = new String[]{"", "Siječanj", "Veljača", "Ožujak", "Travanj", "Svibanj", "Lipanj", "Srpanj", "Kolovoz", "Rujan", "Listopad", "Studeni", "Prosinac"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
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