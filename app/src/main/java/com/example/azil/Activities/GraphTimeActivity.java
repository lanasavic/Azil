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

import com.example.azil.Models.Time;
import com.example.azil.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GraphTimeActivity extends AppCompatActivity {
    private Button btnBack;
    LineChart lineChart;
    DatabaseReference dbRefVrijeme;
    XAxis xAxis;
    YAxis leftYAxis, rightYAxis;
    LineDataSet lineDataSet;
    LineData lineData;
    ArrayList<Time> lTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_time);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        lineChart = findViewById(R.id.graphTime);
        xAxis = lineChart.getXAxis();
        leftYAxis = lineChart.getAxisLeft();
        rightYAxis = lineChart.getAxisRight();

        xAxis.setAxisMinimum(0);

        leftYAxis.setAxisMinimum(0);
        leftYAxis.setLabelCount(1);
        rightYAxis.setAxisMinimum(0);
        rightYAxis.setLabelCount(9);

        dbRefVrijeme = FirebaseDatabase.getInstance().getReference("vrijeme");
        dbRefVrijeme.addValueEventListener(new ValueEventListener() {
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
        dbRefVrijeme = FirebaseDatabase.getInstance().getReference("vrijeme");

        dbRefVrijeme.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> dataVals = new ArrayList<>();
                lTime = new ArrayList<>();
                int index = 0;
                if(snapshot.hasChildren()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Time time = dataSnapshot.getValue(Time.class);
                        assert time != null;
                        float y=Float.parseFloat(time.getBroj_zivotinja());

                        dataVals.add(new Entry(index,y));
                        lTime.add(time);
                        xAxis.setAxisMaximum(lTime.size()-1);
                        index++;
                    }
                    showChart(dataVals);
                }else{
                    lineChart.clear();
                    lineChart.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showChart(ArrayList<Entry> dataVals) {
        lineDataSet = new LineDataSet(dataVals, "Broj životinja");
        lineDataSet.setColor(Color.RED);
        ValueFormatter vf = new ValueFormatter() { //value format here, here is the overridden method
            @Override
            public String getFormattedValue(float value) {
                return ""+(int)value;
            }
        };
        lineDataSet.setValueFormatter(vf);

        String[] months = new String[]{"Siječanj", "Veljača", "Ožujak", "Travanj", "Svibanj", "Lipanj", "Srpanj", "Kolovoz", "Rujan", "Listopad", "Studeni", "Prosinac"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        lineData = new LineData(lineDataSet);

        lineChart.setNoDataText("Nema podataka za prikaz");
        lineChart.setNoDataTextColor(Color.RED);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDragEnabled(true);
        lineChart.setVisibleXRangeMaximum(6);
        lineChart.clear();
        lineChart.setData(lineData);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
}