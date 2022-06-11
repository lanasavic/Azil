package com.example.azil.Filters;

import android.widget.Filter;

import com.example.azil.Adapters.SheltersAdapter;
import com.example.azil.Models.Shelter;

import java.util.ArrayList;

public class SheltersFilter extends Filter {
    ArrayList<Shelter> sheltersList;
    SheltersAdapter sheltersAdapter;

    public SheltersFilter(ArrayList<Shelter> sheltersList, SheltersAdapter sheltersAdapter){
        this.sheltersList = sheltersList;
        this.sheltersAdapter = sheltersAdapter;
    }

    protected FilterResults performFiltering(CharSequence constraint){
        FilterResults res = new FilterResults();

        if(constraint != null && constraint.length()>0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<Shelter> filteredShelters = new ArrayList<>();

            for(int i=0; i<sheltersList.size(); i++){
                if(sheltersList.get(i).getNaziv().toUpperCase().contains(constraint) || sheltersList.get(i).getGrad().toUpperCase().contains(constraint)){
                    filteredShelters.add(sheltersList.get(i));
                }
            }
            res.count = filteredShelters.size();
            res.values = filteredShelters;
        }
        else{
            res.count = sheltersList.size();
            res.values = sheltersList;
        }
        return res;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        sheltersAdapter.lShelters = (ArrayList<Shelter>)results.values;
        sheltersAdapter.notifyDataSetChanged();
    }
}