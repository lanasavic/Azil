package com.example.azil.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.azil.Activities.DonateActivity;
import com.example.azil.Models.RequestedDonation;
import com.example.azil.databinding.DonationItemBinding;

import java.util.ArrayList;

public class DonationsAdapter extends RecyclerView.Adapter<DonationsAdapter.ViewHolder> {
    private Context context;
    public ArrayList<RequestedDonation> lDonations, donationsList;
    private DonationItemBinding binding;
    Intent intent;

    public DonationsAdapter(Context context, ArrayList<RequestedDonation> lDonations) {
        this.context = context;
        this.lDonations = lDonations;
        this.donationsList = donationsList;
    }

    @NonNull
    @Override
    public DonationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DonationItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new DonationsAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull DonationsAdapter.ViewHolder holder, int position) {
        RequestedDonation requestedDonation = lDonations.get(position);

        String opis = requestedDonation.getOpis();
        String kolicina = requestedDonation.getKolicina();

        holder.opis.setText(opis);
        holder.kolicina.setText(kolicina);

        holder.ibDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, DonateActivity.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView opis, kolicina;
        ImageButton ibDonate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            opis = binding.tvOpisDonacije;
            kolicina = binding.tvKolicinaDonacije;
            ibDonate = binding.imageButtonDonate;
        }
    }
}
