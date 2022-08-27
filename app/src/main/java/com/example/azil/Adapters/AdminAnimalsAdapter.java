package com.example.azil.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.azil.Activities.AdminActivity;
import com.example.azil.Activities.EditAnimalActivity;
import com.example.azil.Filters.AdminAnimalsFilter;
import com.example.azil.Models.Animal;
import com.example.azil.Models.Animal_Breed;
import com.example.azil.Models.Animal_Location;
import com.example.azil.Models.Animal_Time;
import com.example.azil.Models.Breed;
import com.example.azil.Models.Location;
import com.example.azil.Models.Shelter_Animal;
import com.example.azil.Models.Time;
import com.example.azil.databinding.AnimalItemFragmentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminAnimalsAdapter extends RecyclerView.Adapter<AdminAnimalsAdapter.ViewHolder> implements Filterable {
    private Context context;
    public ArrayList<Animal> lAnimals, animalsList;
    private AnimalItemFragmentBinding binding;
    Intent intent;
    ProgressDialog progressDialog;
    DatabaseReference dbRefSklonisteZivotinja, dbRefZivotinja, dbRefSkloniste,
            dbRefPasmina, dbRefZivotinjaPasmina, dbRefLokacija, dbRefZivotinjaLokacija, dbRefVrijeme, dbRefZivotinjaVrijeme;
    String key, pasminaId, lokacijaId, mjesecId, pasminaNaziv, lokacijaNaziv, mjesecNaziv;
    public AdminAnimalsFilter adminAnimalsFilter;

    public AdminAnimalsAdapter(Context context, ArrayList<Animal> lAnimals) {
        this.context = context;
        this.lAnimals = lAnimals;
        this.animalsList = lAnimals;
    }

    @NonNull
    @Override
    public AdminAnimalsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = AnimalItemFragmentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new AdminAnimalsAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdminAnimalsAdapter.ViewHolder holder, int position) {
        Animal animal = lAnimals.get(position);

        String ime = animal.getIme();
        String opis = animal.getOpis();
        String img = animal.getImgurl();
        String zahtjevi = animal.getZahtjevi();

        holder.ime.setText(ime);
        holder.opis.setText(opis);
        Picasso.get().load(img).into(holder.img);

        holder.ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, EditAnimalActivity.class);
                intent.putExtra("zivotinjaSifra", animal.getSifra());
                intent.putExtra("pasminaId", pasminaId);
                intent.putExtra("pasminaNaziv", pasminaNaziv);
                intent.putExtra("lokacijaId", lokacijaId);
                intent.putExtra("lokacijaNaziv", lokacijaNaziv);
                intent.putExtra("mjesecId", mjesecId);
                intent.putExtra("mjesecNaziv", mjesecNaziv);
                context.startActivity(intent);
            }
        });

        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAnimal(animal, holder);
            }
        });

        int count;
        try {
            count = Integer.parseInt(zahtjevi);
            if(count > 0){
                binding.animalWarning.setVisibility(View.VISIBLE);
            }
            else{
                binding.animalWarning.setVisibility(View.INVISIBLE);
            }
        } catch (NumberFormatException e) {
            count = 0;
            Log.d("test", "Except "+e);
        }

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
                                pasminaId = breed.getSifra();
                                pasminaNaziv = breed.getNaziv();
                                holder.pasmina.setText(pasminaNaziv);
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
                                lokacijaId = location.getSifra();
                                lokacijaNaziv = location.getNaziv();
                                holder.lokacija.setText("Lokacija pronalaska: "+lokacijaNaziv);
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
                                mjesecId = time.getSifra();
                                mjesecNaziv = time.getMjesec();
                                holder.mjesec.setText("Mjesec pronalaska: "+mjesecNaziv);
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

    private void deleteAnimal(Animal animal, AdminAnimalsAdapter.ViewHolder holder) {
        String zivotinjaSifra = animal.getSifra();
        String slikaUrl = animal.getImgurl();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Brisanje u tijeku...");
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(slikaUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Success", "Deleted from storage");

                        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
                        dbRefZivotinja.child(zivotinjaSifra).removeValue();
                        progressDialog.dismiss();
                        Toast.makeText(context, "Uspješno obrisano!", Toast.LENGTH_SHORT).show();
                        Log.d("Success", "Deleted from database");

                        dbRefSklonisteZivotinja = FirebaseDatabase.getInstance().getReference("skloniste_zivotinja");
                        Query deleteShelterAnimal = dbRefSklonisteZivotinja.orderByChild("zivotinja").equalTo(zivotinjaSifra);
                        deleteShelterAnimal.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    key = dataSnapshot.getKey();
                                    assert key != null;
                                    dbRefSklonisteZivotinja.child(key).removeValue();

                                    Shelter_Animal shelter_animal = dataSnapshot.getValue(Shelter_Animal.class);
                                    assert shelter_animal != null;
                                    String sSklonisteOib = shelter_animal.getSkloniste();

                                    dbRefSkloniste = FirebaseDatabase.getInstance().getReference("skloniste").child(sSklonisteOib).child("dostupnih_mjesta");
                                    dbRefSkloniste.runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                            int count;
                                            try {
                                                count = Integer.parseInt(currentData.getValue(String.class));
                                            } catch (NumberFormatException e) {
                                                count = 0;
                                            }
                                            count++;
                                            currentData.setValue(Integer.toString(count));
                                            intent = new Intent(context, AdminActivity.class);
                                            context.startActivity(intent);
                                            return Transaction.success(currentData);
                                        }

                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                            Log.d("TAG", "countTransaction:onComplete -- Error:" + error);
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return lAnimals.size();
    }

    @Override
    public Filter getFilter() {
        if (adminAnimalsFilter == null){
            adminAnimalsFilter = new AdminAnimalsFilter(animalsList, this);
        }
        return adminAnimalsFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ime, opis, pasmina, lokacija, mjesec;
        ImageView img;
        ImageButton ibEdit, ibDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ime = binding.tvImeZivotinje;
            opis = binding.tvOpisZivotinje;
            img = binding.imgZivotinja;
            ibEdit = binding.imageButtonEdit;
            ibDelete = binding.imageButtonDelete;

            pasmina = binding.tvPasminaZivotinje;
            lokacija = binding.tvLokacijaZivotinje;
            mjesec = binding.tvMjesecZivotinje;
        }
    }
}