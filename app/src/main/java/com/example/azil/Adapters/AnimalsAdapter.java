package com.example.azil.Adapters;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.azil.Filters.AnimalsFilter;
import com.example.azil.Models.Animal;
import com.example.azil.R;
import com.example.azil.databinding.AnimalItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AnimalsAdapter extends RecyclerView.Adapter<AnimalsAdapter.ViewHolder> implements Filterable {
    private Context context;
    public ArrayList<Animal> lAnimals, animalsList;
    private AnimalsAdapter.RecyclerViewClickListener listener;
    public AnimalsFilter animalsFilter;
    private AnimalItemBinding binding;

    public AnimalsAdapter(Context context, ArrayList<Animal> lAnimals) {
        this.context = context;
        this.lAnimals = lAnimals;
        this.animalsList = lAnimals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(context).inflate(R.layout.animal_item, parent, false);
        return new AnimalsAdapter.ViewHolder(view);*/

        binding = AnimalItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.selectedAnimal(animal);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return lAnimals.size();
    }

    @Override
    public Filter getFilter(){
        if (animalsFilter == null){
            animalsFilter = new AnimalsFilter(animalsList, this);
        }
        return animalsFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ime, opis;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            /*ime = itemView.findViewById(R.id.tvImeZivotinje);
            opis = itemView.findViewById(R.id.tvOpisZivotinje);
            img = itemView.findViewById(R.id.imgZivotinja);*/
            ime = binding.tvImeZivotinje;
            opis = binding.tvOpisZivotinje;
            img = binding.imgZivotinja;
        }
    }

    public interface RecyclerViewClickListener {
        void selectedAnimal(Animal animal);
    }
}