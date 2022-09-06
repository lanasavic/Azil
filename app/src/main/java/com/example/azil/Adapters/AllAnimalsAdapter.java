package com.example.azil.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.azil.Filters.AllAnimalsFilter;
import com.example.azil.Models.Animal;
import com.example.azil.Models.Animal_Breed;
import com.example.azil.Models.Animal_Location;
import com.example.azil.Models.Animal_Time;
import com.example.azil.Models.Breed;
import com.example.azil.Models.Location;
import com.example.azil.Models.Species;
import com.example.azil.Models.Time;
import com.example.azil.databinding.AnimalItemBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AllAnimalsAdapter extends RecyclerView.Adapter<AllAnimalsAdapter.ViewHolder> implements Filterable {
    private Context context;
    //private Activity activity;
    public ArrayList<Animal> lAnimals, animalsList;
    public AllAnimalsFilter allAnimalsFilter;
    private AnimalItemBinding binding;
    private AllAnimalsAdapter.RecyclerViewClickListener listener;
    DatabaseReference dbRefPasmina, dbRefZivotinjaPasmina, dbRefLokacija, dbRefZivotinjaLokacija, dbRefVrijeme, dbRefZivotinjaVrijeme;

    /*public AllAnimalsAdapter(FragmentActivity activity, ArrayList<Animal> lAnimals, AllAnimalsAdapter.RecyclerViewClickListener listener) {
        this.activity = activity;
        this.lAnimals = lAnimals;
        this.animalsList = lAnimals;
        this.listener = listener;
    }*/

    public AllAnimalsAdapter(Context context, ArrayList<Animal> lAnimals, AllAnimalsAdapter.RecyclerViewClickListener listener) {
        this.context = context;
        this.lAnimals = lAnimals;
        this.animalsList = lAnimals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AllAnimalsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = AnimalItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new AllAnimalsAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AllAnimalsAdapter.ViewHolder holder, int position) {
        Animal animal = lAnimals.get(position);

        String ime = animal.getIme();
        String opis = animal.getOpis();
        String img = animal.getImgurl();

        holder.ime.setText(ime);
        holder.opis.setText(opis);
        Picasso.get().load(img).into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.selectedAnimal(animal);
            }
        });

        dbRefPasmina = FirebaseDatabase.getInstance().getReference("pasmina");
        dbRefLokacija = FirebaseDatabase.getInstance().getReference("lokacija");
        dbRefVrijeme = FirebaseDatabase.getInstance().getReference("vrijeme");
        dbRefZivotinjaPasmina = FirebaseDatabase.getInstance().getReference("zivotinja_pasmina");
        dbRefZivotinjaLokacija = FirebaseDatabase.getInstance().getReference("zivotinja_lokacija");
        dbRefZivotinjaVrijeme = FirebaseDatabase.getInstance().getReference("zivotinja_vrijeme");

        String sifra = animal.getSifra();
        Query animalBreedQuery = dbRefZivotinjaPasmina.orderByChild("zivotinja").equalTo(sifra);
        animalBreedQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    Animal_Breed animal_breed = dataSnapshot1.getValue(Animal_Breed.class);
                    assert animal_breed != null;
                    String sPasmina = animal_breed.getPasmina();

                    Query breedQuery = dbRefPasmina.orderByChild("sifra").equalTo(sPasmina);
                    breedQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                Breed breed = dataSnapshot2.getValue(Breed.class);
                                assert breed != null;
                                holder.pasmina.setText(breed.getNaziv());
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        Query animalLocationQuery = dbRefZivotinjaLokacija.orderByChild("zivotinja").equalTo(sifra);
        animalLocationQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    Animal_Location animal_location = dataSnapshot1.getValue(Animal_Location.class);
                    assert animal_location != null;
                    String sLokacija = animal_location.getLokacija();

                    Query locationQuery = dbRefLokacija.orderByChild("sifra").equalTo(sLokacija);
                    locationQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                Location location = dataSnapshot2.getValue(Location.class);
                                assert location != null;
                                holder.lokacija.setText("Lokacija pronalaska: "+location.getNaziv());
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        Query animalTimeQuery = dbRefZivotinjaVrijeme.orderByChild("zivotinja").equalTo(sifra);
        animalTimeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    Animal_Time animal_time = dataSnapshot1.getValue(Animal_Time.class);
                    assert animal_time != null;
                    String sMjesec = animal_time.getMjesec();

                    Query timeQuery = dbRefVrijeme.orderByChild("sifra").equalTo(sMjesec);
                    timeQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                Time time = dataSnapshot2.getValue(Time.class);
                                assert time != null;
                                holder.mjesec.setText("Mjesec pronalaska: "+time.getMjesec());
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lAnimals.size();
    }

    @Override
    public Filter getFilter(){
        if (allAnimalsFilter == null){
            allAnimalsFilter = new AllAnimalsFilter(animalsList, this);
        }
        return allAnimalsFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ime, opis, pasmina, lokacija, mjesec;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ime = binding.tvImeZivotinje;
            opis = binding.tvOpisZivotinje;
            img = binding.imgZivotinja;

            pasmina = binding.tvPasminaZivotinje;
            lokacija = binding.tvLokacijaZivotinje;
            mjesec = binding.tvMjesecZivotinje;
        }
    }

    public interface RecyclerViewClickListener {
        void selectedAnimal(Animal animal);
    }
}