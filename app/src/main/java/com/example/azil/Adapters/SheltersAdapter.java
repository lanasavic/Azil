package com.example.azil.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.azil.Models.Shelter;
import com.example.azil.R;
import com.example.azil.Filters.SheltersFilter;

import java.util.ArrayList;

public class SheltersAdapter extends RecyclerView.Adapter<SheltersAdapter.ViewHolder> implements Filterable {
    private Context context;
    public ArrayList<Shelter> lShelters, sheltersList;
    private RecyclerViewClickListener listener;
    public SheltersFilter sheltersFilter;

    public SheltersAdapter(Context context, ArrayList<Shelter> lShelters, RecyclerViewClickListener listener) {
        this.context = context;
        this.lShelters = lShelters;
        this.sheltersList = lShelters;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shelter_item, parent, false);
        return new SheltersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Shelter shelter = lShelters.get(position);

        holder.naziv.setText(shelter.getNaziv());
        holder.adresa.setText(shelter.getAdresa());
        holder.grad.setText(shelter.getGrad());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.selectedShelter(shelter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lShelters.size();
    }

    @Override
    public Filter getFilter(){
        if (sheltersFilter == null){
            sheltersFilter = new SheltersFilter(sheltersList, this);
        }
        return sheltersFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView naziv, adresa, grad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            naziv = itemView.findViewById(R.id.tvNaziv);
            adresa = itemView.findViewById(R.id.tvAdresa);
            grad = itemView.findViewById(R.id.tvGrad);
        }
    }

    public interface RecyclerViewClickListener {
        void selectedShelter(Shelter shelter);
    }
}