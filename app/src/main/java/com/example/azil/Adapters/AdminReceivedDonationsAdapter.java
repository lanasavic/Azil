package com.example.azil.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.azil.Filters.AdminReceivedDonationsFilter;
import com.example.azil.Models.ReceivedDonation;
import com.example.azil.Models.RequestedDonation;
import com.example.azil.Models.Requested_Received;
import com.example.azil.databinding.ReceivedDonationItemBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminReceivedDonationsAdapter extends RecyclerView.Adapter<AdminReceivedDonationsAdapter.ViewHolder> implements Filterable {
    private Context context;
    public ArrayList<ReceivedDonation> lDonations, donationsList;
    private ReceivedDonationItemBinding binding;
    private AdminReceivedDonationsFilter adminReceivedDonationsFilter;
    private String sTrazenaDonacija;
    DatabaseReference dbRefTrazenoZaprimljeno, dbRefTrazenaDonacija;

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

        dbRefTrazenoZaprimljeno = FirebaseDatabase.getInstance().getReference("trazeno_zaprimljeno");
        dbRefTrazenaDonacija = FirebaseDatabase.getInstance().getReference("trazena_donacija");

        String primljenaDonacijaId = receivedDonation.getSifra();
        Query requestedReceivedQuery = dbRefTrazenoZaprimljeno.orderByChild("zaprimljeno").equalTo(primljenaDonacijaId);
        requestedReceivedQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Requested_Received requested_received = dataSnapshot.getValue(Requested_Received.class);
                    assert requested_received != null;
                    sTrazenaDonacija = requested_received.getTrazeno();

                    Query requestedDonationQuery = dbRefTrazenaDonacija.orderByChild("sifra").equalTo(sTrazenaDonacija);
                    requestedDonationQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                RequestedDonation requestedDonation = dataSnapshot1.getValue(RequestedDonation.class);
                                assert requestedDonation != null;
                                holder.donacija.setText("Donacija: "+requestedDonation.getOpis());
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
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
        TextView ime_prezime, email, kolicina, komentar, donacija;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ime_prezime = binding.tvImePrezime;
            email = binding.tvEmail;
            kolicina = binding.tvKolicinaDonacije;
            komentar = binding.tvKomentar;

            donacija = binding.tvDonacija;
        }
    }
}