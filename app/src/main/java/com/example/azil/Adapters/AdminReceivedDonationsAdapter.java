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

import com.example.azil.Filters.AdminReceivedDonationsFilter;
import com.example.azil.Models.ReceivedDonation;
import com.example.azil.databinding.ReceivedDonationItemBinding;

import java.util.ArrayList;

public class AdminReceivedDonationsAdapter extends RecyclerView.Adapter<AdminReceivedDonationsAdapter.ViewHolder> implements Filterable {
    private Context context;
    public ArrayList<ReceivedDonation> lDonations, donationsList;
    private ReceivedDonationItemBinding binding;
    private AdminReceivedDonationsFilter adminReceivedDonationsFilter;

    public AdminReceivedDonationsAdapter(Context context, ArrayList<ReceivedDonation> lDonations) {
        this.context = context;
        this.lDonations = lDonations;
        this.donationsList = lDonations;
    }

    @NonNull
    @Override
    public AdminReceivedDonationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ReceivedDonationItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new AdminReceivedDonationsAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdminReceivedDonationsAdapter.ViewHolder holder, int position) {
        ReceivedDonation receivedDonation = lDonations.get(position);

        String ime_prezime = receivedDonation.getIme_prezime();
        String email = receivedDonation.getEmail();
        String kolicina = receivedDonation.getKolicina();
        String komentar = receivedDonation.getKomentar();

        holder.ime_prezime.setText("Ime i prezime: "+ime_prezime);
        holder.email.setText("Email: "+email);
        holder.kolicina.setText("Količina: "+kolicina);
        if(komentar.equals("")){
            holder.komentar.setText("Komentar: /");
        }
        else{
            holder.komentar.setText("Komentar: "+komentar);
        }
    }

    @Override
    public int getItemCount() {
        return lDonations.size();
    }

    @Override
    public Filter getFilter(){
        if (adminReceivedDonationsFilter == null){
            adminReceivedDonationsFilter = new AdminReceivedDonationsFilter(donationsList, this);
        }
        return adminReceivedDonationsFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ime_prezime, email, kolicina, komentar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ime_prezime = binding.tvImePrezime;
            email = binding.tvEmail;
            kolicina = binding.tvKolicinaDonacije;
            komentar = binding.tvKomentar;
        }
    }
}