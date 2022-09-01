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

import com.example.azil.Filters.RequestsFilter;
import com.example.azil.Models.Animal;
import com.example.azil.Models.Animal_Request;
import com.example.azil.Models.Request;
import com.example.azil.databinding.RequestItemBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> implements Filterable {
    private Context context;
    public ArrayList<Request> lRequests, requestsList;
    private RequestItemBinding binding;
    private RequestsFilter requestsFilter;
    private String sZivotinja, formattedDate;
    DatabaseReference dbRefZivotinjaZahtjev, dbRefZivotinja;

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

        SimpleDateFormat fromFirebase = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat wantedFormat = new SimpleDateFormat("dd.MM.yyyy.");
        try {
            formattedDate = wantedFormat.format(fromFirebase.parse(datum));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.datum.setText("Datum slanja: "+formattedDate);

        dbRefZivotinjaZahtjev = FirebaseDatabase.getInstance().getReference("zivotinja_zahtjev");
        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");

        String zahtjevId = request.getSifra();
        Query animalRequestQuery = dbRefZivotinjaZahtjev.orderByChild("zahtjev").equalTo(zahtjevId);
        animalRequestQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Animal_Request animal_request = dataSnapshot.getValue(Animal_Request.class);
                    assert animal_request != null;
                    sZivotinja = animal_request.getZivotinja();

                    Query animalQuery = dbRefZivotinja.orderByChild("sifra").equalTo(sZivotinja);
                    animalQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                Animal animal = dataSnapshot1.getValue(Animal.class);
                                assert animal != null;
                                holder.zivotinja.setText("Odabrana životinja: "+animal.getIme());
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
        TextView ime_prezime, email, komentar, datum, zivotinja;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ime_prezime = binding.tvImePrezime;
            email = binding.tvEmail;
            komentar = binding.tvKomentar;
            datum = binding.tvDatum;

            zivotinja = binding.tvZivotinja;
        }
    }
}