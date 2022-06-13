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

import com.example.azil.Adapters.SheltersAdapter;
import com.example.azil.Models.Shelter;
import com.example.azil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button btnLogin;
    private RecyclerView rvMain;
    DatabaseReference databaseReference;
    private SheltersAdapter sheltersAdapter;
    ArrayList<Shelter> lShelters;
    private EditText search_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        search_main = findViewById(R.id.search_main);
        search_main.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    sheltersAdapter.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d("search_main:ERR_", e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        rvMain = findViewById(R.id.rvMain);
        rvMain.setHasFixedSize(true);
        rvMain.setLayoutManager(new LinearLayoutManager(this));

        lShelters = new ArrayList<>();
        sheltersAdapter = new SheltersAdapter(this, lShelters, this::selectedShelter);
        rvMain.setAdapter(sheltersAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("skloniste");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Shelter shelter = dataSnapshot.getValue(Shelter.class);
                    lShelters.add(shelter);
                }
                sheltersAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("dbRef:ERR_", error.getMessage());
            }
        });
    }

    public void selectedShelter(Shelter shelter) {
        startActivity(new Intent(this,SheltersActivity.class).putExtra("data", shelter));
    }
}

//TODO: back button on phone izlazi iz app umjesto da ode nazad u prosli activity