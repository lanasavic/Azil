package com.example.azil.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.azil.R;

public class StatisticsActivity extends AppCompatActivity {
    private Button btnBack, btnGrafVrste, btnGrafPasmine, btnGrafLokacije, btnGrafVrijeme, btnGrafPasmineLokacije, btnGrafPasmineVrijeme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnGrafVrste = (Button) findViewById(R.id.btnGrafVrste);
        btnGrafVrste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GraphSpeciesActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnGrafPasmine = (Button) findViewById(R.id.btnGrafPasmine);
        btnGrafPasmine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GraphBreedActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnGrafLokacije = (Button) findViewById(R.id.btnGrafLokacije);
        btnGrafLokacije.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GraphLocationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnGrafVrijeme = (Button) findViewById(R.id.btnGrafVrijeme);
        btnGrafVrijeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GraphTimeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnGrafPasmineLokacije = (Button) findViewById(R.id.btnGrafPasmineLokacije);
        btnGrafPasmineLokacije.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GraphBreedLocationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnGrafPasmineVrijeme = (Button) findViewById(R.id.btnGrafPasmineVrijeme);
        btnGrafPasmineVrijeme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GraphBreedTimeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}