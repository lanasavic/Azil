package com.example.azil.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.azil.Models.Breed;
import com.example.azil.R;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GraphBreedActivity extends AppCompatActivity {
    private Button btnBack;
    DatabaseReference dbRefPasmina;
    XAxis xAxis;
    YAxis leftYAxis, rightYAxis;
    BarChart barChart;
    BarDataSet barDataSet;
    BarData barData;
    private ArrayList<Breed> lBreed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_breed);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        barChart = findViewById(R.id.graphBreed);
        xAxis = barChart.getXAxis();
        leftYAxis = barChart.getAxisLeft();
        rightYAxis = barChart.getAxisRight();

        xAxis.setAxisMinimum(0);

        leftYAxis.setAxisMinimum(0);
        leftYAxis.setLabelCount(1);
        rightYAxis.setAxisMinimum(0);
        rightYAxis.setLabelCount(2);

        dbRefPasmina = FirebaseDatabase.getInstance().getReference("pasmina");
        dbRefPasmina.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    /*String string = dataSnapshot.child("naziv").getValue(String.class);
                    assert string != null;
                    String[] months = string.replace("[", "").replace("]", "").split(", ");
                    for(int i = 0; i<months.length; i++){
                        Log.d("TAG", months[i]);
                    }
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(months)); //TODO: shows only last month*/
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR", "Error:" + error.getMessage());
            }
        });
        retrieveData();
    }

    private void retrieveData(){
        dbRefPasmina = FirebaseDatabase.getInstance().getReference("pasmina");

        dbRefPasmina.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> dataVals = new ArrayList<>();
                lBreed = new ArrayList<>();
                int index = 1;
                if(snapshot.hasChildren()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Breed breed = dataSnapshot.getValue(Breed.class);
                        assert breed != null;
                        float y=Float.parseFloat(breed.getBroj_zivotinja());

                        dataVals.add(new BarEntry(index,y));
                        lBreed.add(breed);
                        xAxis.setAxisMaximum(lBreed.size()+1);
                        index++;
                    }
                    showChart(dataVals);
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
        barDataSet = new BarDataSet(dataVals, "Broj životinja po pasminama");
        barDataSet.setColor(Color.RED);
        ValueFormatter vf = new ValueFormatter() { //value format here, here is the overridden method
            @Override
            public String getFormattedValue(float value) {
                return ""+(int)value;
            }
        };
        barDataSet.setValueFormatter(vf);

        String[] breeds = new String[]{"", "Bernski planinski pas", "Jack Russel terijer", "Jazavčar", "Jorkširski terijer", "Koker španijel", "Labrador retriver", "Maltezer", "Njemački ovčar", "Pekinezer", "Zlatni retriver", "Patuljasti pinč"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(breeds));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelRotationAngle(-30f);

        barData = new BarData(barDataSet);
        barData.setBarWidth(0.85f);

        barChart.setNoDataText("Nema podataka za prikaz");
        barChart.setNoDataTextColor(Color.RED);
        barChart.getDescription().setEnabled(false);
        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(6);
        barChart.clear();
        barChart.setData(barData);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }
}