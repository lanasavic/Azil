package com.example.azil.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.azil.Activities.AdminActivity;
import com.example.azil.Activities.EditDonationActivity;
import com.example.azil.Filters.AdminDonationsFilter;
import com.example.azil.Models.ReceivedDonation;
import com.example.azil.Models.RequestedDonation;
import com.example.azil.Models.Requested_Received;
import com.example.azil.databinding.DonationItemFragmentBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminDonationsAdapter extends RecyclerView.Adapter<AdminDonationsAdapter.ViewHolder> implements Filterable{
    private Context context;
    public ArrayList<RequestedDonation> lDonations, donationsList;
    private DonationItemFragmentBinding binding;
    Intent intent;
    ProgressDialog progressDialog;
    DatabaseReference dbRefSklonisteDonacija, dbRefTrazenaDonacija, dbRefTrazenoZaprimljeno, dbRefZaprimljenaDonacija;
    String key, key1, key2, donacijaSifra, received, requested;
    private AdminDonationsFilter adminDonationsFilter;

    public AdminDonationsAdapter(Context context, ArrayList<RequestedDonation> lDonations) {
        this.context = context;
        this.lDonations = lDonations;
        this.donationsList = lDonations;
    }

    @NonNull
    @Override
    public AdminDonationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DonationItemFragmentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new AdminDonationsAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdminDonationsAdapter.ViewHolder holder, int position) {
        RequestedDonation requestedDonation = lDonations.get(position);

        String opis = requestedDonation.getOpis();
        String kolicina = requestedDonation.getKolicina();

        holder.opis.setText(opis);
        holder.kolicina.setText("Potrebna količina: "+kolicina);

        holder.ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, EditDonationActivity.class);
                intent.putExtra("donacijaSifra", requestedDonation.getSifra());
                context.startActivity(intent);
            }
        });

        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDonation(requestedDonation, holder);
            }
        });

        int count = Integer.parseInt(kolicina);
        if(count > 0){
            binding.donationWarning.setVisibility(View.INVISIBLE);
        }
        else{
            binding.donationWarning.setVisibility(View.VISIBLE);
        }
    }

    private void deleteDonation(RequestedDonation requestedDonation, AdminDonationsAdapter.ViewHolder holder) {
        donacijaSifra = requestedDonation.getSifra();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Brisanje u tijeku...");
        progressDialog.show();

        dbRefSklonisteDonacija = FirebaseDatabase.getInstance().getReference("skloniste_donacija");
        dbRefTrazenaDonacija = FirebaseDatabase.getInstance().getReference("trazena_donacija");
        dbRefTrazenoZaprimljeno = FirebaseDatabase.getInstance().getReference("trazeno_zaprimljeno");
        dbRefZaprimljenaDonacija = FirebaseDatabase.getInstance().getReference("zaprimljena_donacija");

        Query deleteShelterDonation = dbRefSklonisteDonacija.orderByChild("donacija").equalTo(donacijaSifra);
        deleteShelterDonation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    key = dataSnapshot.getKey();
                    assert key != null;
                    dbRefSklonisteDonacija.child(key).removeValue();
                    dbRefTrazenaDonacija.child(donacijaSifra).removeValue();
                    progressDialog.dismiss();
                    Toast.makeText(context, "Uspješno obrisano!", Toast.LENGTH_SHORT).show();

                    Query deleteRequestedReceived = dbRefTrazenoZaprimljeno.orderByChild("trazeno").equalTo(donacijaSifra);
                    deleteRequestedReceived.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                key1 = dataSnapshot1.getKey();
                                assert key1 != null;
                                Requested_Received requested_received = dataSnapshot1.getValue(Requested_Received.class);
                                assert requested_received != null;
                                received = requested_received.getZaprimljeno();
                                requested = requested_received.getTrazeno();

                                if(donacijaSifra.equals(requested)){
                                    dbRefTrazenoZaprimljeno.child(key1).removeValue();
                                    intent = new Intent(context, AdminActivity.class);
                                    context.startActivity(intent);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });

                    Query deleteReceived = dbRefZaprimljenaDonacija.orderByChild("sifra").equalTo(received);
                    deleteReceived.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                                key2 = dataSnapshot2.getKey();
                                assert key2 != null;
                                ReceivedDonation receivedDonation = dataSnapshot2.getValue(ReceivedDonation.class);
                                assert receivedDonation != null;
                                String receivedDonId = receivedDonation.getSifra();

                                if(receivedDonId.equals(received)){
                                    dbRefZaprimljenaDonacija.child(key2).removeValue();
                                    intent = new Intent(context, AdminActivity.class);
                                    context.startActivity(intent);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
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
        if (adminDonationsFilter == null){
            adminDonationsFilter = new AdminDonationsFilter(donationsList, this);
        }
        return adminDonationsFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView opis, kolicina;
        ImageButton ibEdit, ibDelete;
        ImageView donationWarning;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            opis = binding.tvOpisDonacije;
            kolicina = binding.tvKolicinaDonacije;
            ibEdit = binding.ibEdit;
            ibDelete = binding.ibDelete;
            donationWarning = binding.donationWarning;
        }
    }
}