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
    BarDataSet barDataSet;
    BarData barData;
    private ArrayList<Time> lTime;
    private ArrayList<Breed> breedArrayList;

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
        }).show();
    }

    private void retrieveData(String chosenBreed) {
        dbRefPasmina = FirebaseDatabase.getInstance().getReference("pasmina");
        dbRefZivotinjaPasmina = FirebaseDatabase.getInstance().getReference("zivotinja_pasmina");
        dbRefZivotinjaVrijeme = FirebaseDatabase.getInstance().getReference("zivotinja_vrijeme");
        dbRefVrijeme = FirebaseDatabase.getInstance().getReference("vrijeme");

        Query breedQuery = dbRefPasmina.orderByChild("naziv").equalTo(chosenBreed);
        breedQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> dataVals = new ArrayList<>();
                lTime = new ArrayList<>();

                if(snapshot.hasChildren()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Breed breed = dataSnapshot.getValue(Breed.class);
                        assert breed != null;
                        float y=Float.parseFloat(breed.getBroj_zivotinja());
                        String pasminaId = breed.getSifra();

                        Query animalBreedQuery = dbRefZivotinjaPasmina.orderByChild("pasmina").equalTo(pasminaId);
                        animalBreedQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                    Animal_Breed animal_breed = dataSnapshot1.getValue(Animal_Breed.class);
                                    assert animal_breed != null;
                                    String zivotinjaId = animal_breed.getZivotinja();

                                    Query animalTimeQuery = dbRefZivotinjaVrijeme.orderByChild("zivotinja").equalTo(zivotinjaId);
                                    animalTimeQuery.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                                Animal_Time animal_time = dataSnapshot2.getValue(Animal_Time.class);
                                                assert animal_time != null;
                                                String mjesecId = animal_time.getMjesec();

                                                Query timeQuery = dbRefVrijeme.orderByChild("sifra").equalTo(mjesecId);
                                                timeQuery.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot dataSnapshot3 : snapshot.getChildren()){
                                                            Time time = dataSnapshot3.getValue(Time.class);
                                                            lTime.add(time);

                                                            assert time != null;
                                                            float x=Float.parseFloat(time.getSifra());
                                                            
                                                            int monthNum = Integer.parseInt(time.getSifra());

                                                            switch(monthNum){
                                                                case 1:
                                                                    ArrayList<Time> lSijecanj = new ArrayList<>();
                                                                    lSijecanj.add(time);
                                                                    dataVals.add(new BarEntry(1, lSijecanj.size()));
                                                                case 2:
                                                                    ArrayList<Time> lVeljaca = new ArrayList<>();
                                                                    lVeljaca.add(time);
                                                                    dataVals.add(new BarEntry(2, lVeljaca.size()));
                                                                    break;
                                                                case 3:
                                                                    ArrayList<Time> lOzujak = new ArrayList<>();
                                                                    lOzujak.add(time);
                                                                    dataVals.add(new BarEntry(3, lOzujak.size()));
                                                                    break;
                                                                case 4:
                                                                    ArrayList<Time> lTravanj = new ArrayList<>();
                                                                    lTravanj.add(time);
                                                                    dataVals.add(new BarEntry(4, lTravanj.size()));
                                                                    break;
                                                                case 5:
                                                                    ArrayList<Time> lSvibanj = new ArrayList<>();
                                                                    lSvibanj.add(time);
                                                                    dataVals.add(new BarEntry(5, lSvibanj.size()));
                                                                    break;
                                                                case 6:
                                                                    ArrayList<Time> lLipanj = new ArrayList<>();
                                                                    lLipanj.add(time);
                                                                    dataVals.add(new BarEntry(6, lLipanj.size()));
                                                                    break;
                                                                case 7:
                                                                    ArrayList<Time> lSrpanj = new ArrayList<>();
                                                                    lSrpanj.add(time);
                                                                    dataVals.add(new BarEntry(7, lSrpanj.size()));
                                                                    break;
                                                                case 8:
                                                                    ArrayList<Time> lKolovoz = new ArrayList<>();
                                                                    lKolovoz.add(time);
                                                                    dataVals.add(new BarEntry(8, lKolovoz.size()));
                                                                    break;
                                                                case 9:
                                                                    ArrayList<Time> lRujan = new ArrayList<>();
                                                                    lRujan.add(time);
                                                                    dataVals.add(new BarEntry(9, lRujan.size()));
                                                                    break;
                                                                case 10:
                                                                    ArrayList<Time> lListopad = new ArrayList<>();
                                                                    lListopad.add(time);
                                                                    dataVals.add(new BarEntry(10, lListopad.size()));
                                                                    break;
                                                                case 11:
                                                                    ArrayList<Time> lStudeni = new ArrayList<>();
                                                                    lStudeni.add(time);
                                                                    dataVals.add(new BarEntry(11, lStudeni.size()));
                                                                    break;
                                                                case 12:
                                                                    ArrayList<Time> lProsinac = new ArrayList<>();
                                                                    lProsinac.add(time);
                                                                    dataVals.add(new BarEntry(12, lProsinac.size()));
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

        barData = new BarData(barDataSet);
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