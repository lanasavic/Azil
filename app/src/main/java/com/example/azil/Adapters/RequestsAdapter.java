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

import com.example.azil.Filters.RequestsFilter;
import com.example.azil.Models.Request;
import com.example.azil.databinding.RequestItemBinding;

import java.util.ArrayList;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> implements Filterable {
    private Context context;
    public ArrayList<Request> lRequests, requestsList;
    private RequestItemBinding binding;
    private RequestsFilter requestsFilter;

    public RequestsAdapter(Context context, ArrayList<Request> lRequests) {
        this.context = context;
        this.lRequests = lRequests;
        this.requestsList = lRequests;
    }

    @NonNull
    @Override
    public RequestsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RequestItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new RequestsAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsAdapter.ViewHolder holder, int position) {
        Request request = lRequests.get(position);

        String ime_prezime = request.getIme_prezime();
        String email = request.getEmail();
        String komentar = request.getKomentar();
        String datum = request.getDatum();

        holder.ime_prezime.setText("Ime i prezime: "+ime_prezime);
        holder.email.setText("Email: "+email);
        if(komentar.equals("")){
            holder.komentar.setText("Komentar: /");
        }
        else{
            holder.komentar.setText("Komentar: "+komentar);

        }
        holder.datum.setText("Datum slanja: "+datum);
    }

    @Override
    public int getItemCount() {
        return lRequests.size();
    }

    @Override
    public Filter getFilter(){
        if (requestsFilter == null){
            requestsFilter = new RequestsFilter(requestsList, this);
        }
        return requestsFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ime_prezime, email, komentar, datum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ime_prezime = binding.tvImePrezime;
            email = binding.tvEmail;
            komentar = binding.tvKomentar;
            datum = binding.tvDatum;
        }
    }
}