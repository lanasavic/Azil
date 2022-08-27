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

import com.example.azil.Models.Species;
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

public class GraphSpeciesActivity extends AppCompatActivity {
    private Button btnBack;
    DatabaseReference dbRefVrsta;
    XAxis xAxis;
    YAxis leftYAxis, rightYAxis;
    BarChart barChart;
    BarDataSet barDataSet;
    BarData barData;
    ArrayList<Species> lSpecies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_species);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        barChart = findViewById(R.id.graphSpecies);
        xAxis = barChart.getXAxis();
        leftYAxis = barChart.getAxisLeft();
        rightYAxis = barChart.getAxisRight();

        xAxis.setAxisMinimum(0);

        leftYAxis.setAxisMinimum(0);
        leftYAxis.setLabelCount(5);
        rightYAxis.setAxisMinimum(0);
        rightYAxis.setLabelCount(11);

        dbRefVrsta = FirebaseDatabase.getInstance().getReference("vrsta");
        dbRefVrsta.addValueEventListener(new ValueEventListener() {
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
        dbRefVrsta = FirebaseDatabase.getInstance().getReference("vrsta");

        dbRefVrsta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> dataVals = new ArrayList<>();
                lSpecies = new ArrayList<>();
                int index = 1;
                if(snapshot.hasChildren()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Species species = dataSnapshot.getValue(Species.class);
                        assert species != null;
                        float y=Float.parseFloat(species.getBroj_zivotinja());

                        dataVals.add(new BarEntry(index,y));
                        lSpecies.add(species);
                        xAxis.setAxisMaximum(lSpecies.size()+1);
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
        barDataSet = new BarDataSet(dataVals, "Broj životinja po vrstama");
        barDataSet.setColor(Color.RED);
        ValueFormatter vf = new ValueFormatter() { //value format here, here is the overridden method
            @Override
            public String getFormattedValue(float value) {
                return ""+(int)value;
            }
        };
        barDataSet.setValueFormatter(vf);

        String[] species = new String[]{"", "Pas", "Mačka"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(species));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

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