package com.example.azil.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azil.Activities.AdoptActivity;
import com.example.azil.Adapters.AllAnimalsAdapter;
import com.example.azil.Models.Animal;
import com.example.azil.Models.Animal_Time;
import com.example.azil.Models.Time;
import com.example.azil.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TimeFragment extends Fragment {
    View view;
    TextView dropdownTime;
    private ArrayList<Time> timeArrayList;
    DatabaseReference dbRefZivotinja, dbRefVrijeme, dbRefZivotinjaVrijeme;
    RecyclerView rvTime;
    ArrayList<Animal> lAnimals;
    private AllAnimalsAdapter allAnimalsAdapter;
    private EditText search_fragmentTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_time, container, false);

        dropdownTime = view.findViewById(R.id.dropdownTime);
        dropdownTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickDialog();
            }
        });

        search_fragmentTime = view.findViewById(R.id.search_fragmentTime);
        search_fragmentTime.addTextChangedListener(new TextWatcher() {
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
        dbRefVrijeme = FirebaseDatabase.getInstance().getReference("vrijeme");
        dbRefZivotinjaVrijeme = FirebaseDatabase.getInstance().getReference("zivotinja_vrijeme");

        rvTime = view.findViewById(R.id.rvTime);
        rvTime.setHasFixedSize(true);
        rvTime.setLayoutManager(new LinearLayoutManager(getActivity()));

        lAnimals = new ArrayList<>();
        allAnimalsAdapter = new AllAnimalsAdapter(getActivity(), lAnimals, this::selectedAnimal);
        rvTime.setAdapter(allAnimalsAdapter);

        ImageView ivNoResult = view.findViewById(R.id.ivNoResult);
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
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        loadMonths();

        return view;
    }

    private void loadMonths() {
        timeArrayList = new ArrayList<>();

        dbRefVrijeme.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timeArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Time time = dataSnapshot.getValue(Time.class);
                    timeArrayList.add(time);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void timePickDialog() {
        String[] timeArray = new String[timeArrayList.size()];
        for (int i=0; i<timeArrayList.size(); i++){
            timeArray[i] = timeArrayList.get(i).getMjesec();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Odaberite vrijeme pronalaska").setItems(timeArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String time = timeArray[which];
                dropdownTime.setText(time);
                retrieveData(time);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(950, 1050);
    }

    private void retrieveData(String chosenMonth) {
        Query monthQuery = dbRefVrijeme.orderByChild("mjesec").equalTo(chosenMonth);
        monthQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Time time = dataSnapshot.getValue(Time.class);
                    assert time != null;
                    String mjesecId = time.getSifra();

                    Query animalTimeQuery = dbRefZivotinjaVrijeme.orderByChild("mjesec").equalTo(mjesecId);
                    animalTimeQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                Animal_Time animal_time = dataSnapshot1.getValue(Animal_Time.class);
                                assert animal_time != null;
                                String zivotinjaId = animal_time.getZivotinja();

                                Query animalQuery = dbRefZivotinja.orderByChild("sifra").equalTo(zivotinjaId);
                                animalQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                            Animal animal = dataSnapshot2.getValue(Animal.class);
                                            lAnimals.add(animal);
                                        }
                                        allAnimalsAdapter.notifyDataSetChanged();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void selectedAnimal(Animal animal) {
        startActivity(new Intent(getContext(), AdoptActivity.class).putExtra("data2", animal));
    }
}