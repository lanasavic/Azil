package com.example.azil.Filters;

import android.widget.Filter;

import com.example.azil.Adapters.AdminAnimalsAdapter;
import com.example.azil.Models.Animal;

import java.util.ArrayList;

public class AdminAnimalsFilter extends Filter {
    ArrayList<Animal> animalsList;
    AdminAnimalsAdapter adminAnimalsAdapter;

    public AdminAnimalsFilter(ArrayList<Animal> animalsList, AdminAnimalsAdapter adminAnimalsAdapter){
        this.animalsList = animalsList;
        this.adminAnimalsAdapter = adminAnimalsAdapter;
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
        adminAnimalsAdapter.lAnimals = (ArrayList<Animal>)results.values;
        adminAnimalsAdapter.notifyDataSetChanged();
    }
}