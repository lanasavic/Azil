package com.example.azil.Filters;

import android.widget.Filter;

import com.example.azil.Adapters.AnimalsAdapter;
import com.example.azil.Models.Animal;

import java.util.ArrayList;

public class AdminAnimalsFilter extends Filter {
    ArrayList<Animal> animalsList;
    AnimalsAdapter animalsAdapter;

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        return null;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

    }
}
//mozda i ne treba jer imam animalfilter, no?