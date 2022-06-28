package com.example.azil.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.azil.Activities.EditAnimalActivity;
import com.example.azil.Models.Animal;
import com.example.azil.databinding.AnimalItemFragmentBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminAnimalsAdapter extends RecyclerView.Adapter<AdminAnimalsAdapter.ViewHolder> implements Filterable {
    private Context context;
    public ArrayList<Animal> lAnimals, animalsList;
    private AnimalItemFragmentBinding binding;
    Intent intent;

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
                context.startActivity(intent);
            }
        });

        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
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