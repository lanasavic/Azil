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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.azil.Adapters.AllAnimalsAdapter;
import com.example.azil.Models.Animal;
import com.example.azil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/** Backup plan - instead of AllAnimalsActivity and Fragments
 * Issue: dropdown filters
 */
public class BackupAllAnimalsActivity extends AppCompatActivity {
    private Button btnBack;
    private RecyclerView rvAllAnimalsB;
    private AllAnimalsAdapter allAnimalsAdapter;
    private EditText search_allAnimalsB;
    ArrayList<Animal> lAnimals;
    DatabaseReference dbRefZivotinja, dbRefPasmina, dbRefZivotinjaPasmina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_all_animals);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        search_allAnimalsB = findViewById(R.id.search_allAnimalsB);
        search_allAnimalsB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    allAnimalsAdapter.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d("ERROR", "Error:" + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //DATABASE REFERENCES
        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
        dbRefPasmina = FirebaseDatabase.getInstance().getReference("pasmina");
        dbRefZivotinjaPasmina = FirebaseDatabase.getInstance().getReference("zivotinja_pasmina");

        rvAllAnimalsB = findViewById(R.id.rvAllAnimalsB);
        rvAllAnimalsB.setHasFixedSize(true);
        rvAllAnimalsB.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        lAnimals = new ArrayList<>();
        allAnimalsAdapter = new AllAnimalsAdapter(getApplicationContext(), lAnimals, this::selectedAnimal);
        rvAllAnimalsB.setAdapter(allAnimalsAdapter);

        ImageView ivNoResult = findViewById(R.id.ivNoResult);
        allAnimalsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }

            void checkEmpty() {
                ivNoResult.setVisibility(allAnimalsAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        });

        dbRefZivotinja.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Animal animals = dataSnapshot.getValue(Animal.class);
                    lAnimals.add(animals);
                }
                allAnimalsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void selectedAnimal(Animal animal) {
        startActivity(new Intent(getApplicationContext(), AdoptActivity.class).putExtra("data2", animal));
    }
}