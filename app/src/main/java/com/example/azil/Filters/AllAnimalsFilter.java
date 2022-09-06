package com.example.azil.Filters;

import android.widget.Filter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.azil.Adapters.AllAnimalsAdapter;
import com.example.azil.Models.Animal;
import com.example.azil.Models.Species;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllAnimalsFilter extends Filter {
    ArrayList<Animal> animalsList;
    AllAnimalsAdapter allAnimalsAdapter;

    public AllAnimalsFilter(ArrayList<Animal> animalsList, AllAnimalsAdapter allAnimalsAdapter){
        this.animalsList = animalsList;
        this.allAnimalsAdapter = allAnimalsAdapter;
    }

    protected FilterResults performFiltering(CharSequence constraint){
        FilterResults res = new FilterResults();

        if(constraint != null && constraint.length()>0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<Animal> filteredAnimals = new ArrayList<>();

            for(int i=0; i<animalsList.size(); i++){
                if(animalsList.get(i).getIme().toUpperCase().contains(constraint) || animalsList.get(i).getOpis().toUpperCase().contains(constraint)){
                    filteredAnimals.add(animalsList.get(i));
                }
            }
            res.count = filteredAnimals.size();
            res.values = filteredAnimals;
        }
        else{
            res.count = animalsList.size();
            res.values = animalsList;
        }
        return res;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        allAnimalsAdapter.lAnimals = (ArrayList<Animal>)results.values;
        allAnimalsAdapter.notifyDataSetChanged();
    }
}