package com.example.azil.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.azil.Activities.AddAnimalActivity;
import com.example.azil.Activities.AdminActivity;
import com.example.azil.Activities.EditAnimalActivity;
import com.example.azil.Models.Animal;
import com.example.azil.databinding.AnimalItemFragmentBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    DatabaseReference dbRefShelterAnimal;
    String key;

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

        /*holder.ime.setText(animal.getIme());
        holder.opis.setText(animal.getOpis());
        Picasso.get().load(animal.getImgurl()).into(holder.img);*/
        String ime = animal.getIme();
        String opis = animal.getOpis();
        String img = animal.getImgurl();

        holder.ime.setText(ime);
        holder.opis.setText(opis);
        Picasso.get().load(img).into(holder.img);

        holder.ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, EditAnimalActivity.class);
                intent.putExtra("zivotinjaSifra", animal.getSifra());
                context.startActivity(intent);
            }
        });

        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAnimal(animal, holder);
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

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("zivotinja");
                        databaseReference.child(zivotinjaSifra).removeValue();

                        Query deleteShelterAnimal = dbRefShelterAnimal.orderByChild("zivotinja").equalTo(zivotinjaSifra);
                        deleteShelterAnimal.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    key = dataSnapshot.getKey();
                                    assert key != null;
                                    dbRefShelterAnimal.child(key).removeValue();
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Uspješno obrisano!", Toast.LENGTH_SHORT).show();
                                    intent = new Intent(context, AdminActivity.class);
                                    context.startActivity(intent);
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
                        Log.d("Error", e.toString());
                        progressDialog.dismiss();
                        Toast.makeText(context, "Došlo je do pogreške.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return lAnimals.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ime, opis;
        ImageView img;
        ImageButton ibEdit, ibDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ime = binding.tvImeZivotinje;
            opis = binding.tvOpisZivotinje;
            img = binding.imgZivotinja;
            ibEdit = binding.imageButtonEdit;
            ibDelete = binding.imageButtonDelete;
        }
    }
}