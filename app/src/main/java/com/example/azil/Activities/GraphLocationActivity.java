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

import com.example.azil.Models.Location;
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

public class GraphLocationActivity extends AppCompatActivity {
    private Button btnBack;
    BarChart barChart;
    DatabaseReference dbRefLokacija;
    XAxis xAxis;
    YAxis leftYAxis, rightYAxis;
    BarDataSet barDataSet;
    BarData barData;
    ArrayList<Location> lLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_location);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        barChart = findViewById(R.id.graphLocation);
        xAxis = barChart.getXAxis();
        leftYAxis = barChart.getAxisLeft();
        rightYAxis = barChart.getAxisRight();

        xAxis.setAxisMinimum(0);

        leftYAxis.setAxisMinimum(0);
        leftYAxis.setLabelCount(2);
        rightYAxis.setAxisMinimum(0);
        rightYAxis.setLabelCount(5);

        dbRefLokacija = FirebaseDatabase.getInstance().getReference("lokacija");
        dbRefLokacija.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    /*String string = dataSnapshot.child("mjesec").getValue(String.class);
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
        dbRefLokacija = FirebaseDatabase.getInstance().getReference("lokacija");

        dbRefLokacija.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> dataVals = new ArrayList<>();
                lLocation = new ArrayList<>();
                int index = 1;
                if(snapshot.hasChildren()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Location location = dataSnapshot.getValue(Location.class);
                        assert location != null;
                        float y=Float.parseFloat(location.getBroj_zivotinja());

                        dataVals.add(new BarEntry(index,y));
                        lLocation.add(location);
                        xAxis.setAxisMaximum(lLocation.size()+1);
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
        barDataSet = new BarDataSet(dataVals, "Broj životinja");
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

        barData = new BarData(barDataSet);
        barData.setBarWidth(0.66f);

        barChart.getDescription().setEnabled(false);
        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(4);
        barChart.clear();
        barChart.setData(barData);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }
}