package com.example.azil.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azil.Activities.DeleteActivity;
import com.example.azil.Activities.EditActivity;
import com.example.azil.Activities.LoginActivity;
import com.example.azil.Activities.MainActivity;
import com.example.azil.Models.Admin;
import com.example.azil.Models.Shelter;
import com.example.azil.Models.Shelter_Admin;
import com.example.azil.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;

public class ShelterFragment extends Fragment {
    private Button btnDelete;
    ProgressDialog progressDialog;
    TextView tvNaziv, tvAdresa, tvGrad, tvOib, tvIban, tvDostupnihMjesta, tvKontakt;
    DatabaseReference dbRefSkloniste, dbRefAdmin, dbRefSklonisteAdmin;
    FirebaseUser firebaseUser;
    ImageButton imageButtonEdit;
    private View view;
    private Intent intent;

    public void imageButtonOnClick() {
        imageButtonEdit = (ImageButton) view.findViewById(R.id.imageButtonEdit);
        imageButtonEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Edit", Toast.LENGTH_SHORT).show();
                intent = new Intent(getActivity(), EditActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shelter, container, false);

        imageButtonOnClick();
        btnDelete = view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), DeleteActivity.class);
                startActivity(intent);
            }
        });

        tvNaziv = view.findViewById(R.id.tvNaziv);
        tvAdresa = view.findViewById(R.id.tvAdresa);
        tvGrad = view.findViewById(R.id.tvGrad);
        tvOib = view.findViewById(R.id.tvOib);
        tvIban = view.findViewById(R.id.tvIban);
        tvDostupnihMjesta = view.findViewById(R.id.tvDostupnihMjesta);
        tvKontakt = view.findViewById(R.id.tvKontakt);

        dbRefSkloniste = FirebaseDatabase.getInstance().getReference("skloniste");
        dbRefAdmin = FirebaseDatabase.getInstance().getReference("admin");
        dbRefSklonisteAdmin = FirebaseDatabase.getInstance().getReference("skloniste_admin");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Query adminEmailQuery = dbRefAdmin.orderByChild("email").equalTo(firebaseUser.getEmail());
        adminEmailQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Admin admin = dataSnapshot.getValue(Admin.class);
                    assert admin != null;
                    String sAdminUser = admin.getUsername();
                    tvKontakt.setText(firebaseUser.getEmail());
                    //Toast.makeText(getActivity(), "KEY:" + sAdminUser, Toast.LENGTH_SHORT).show();

                    Query adminShelterQuery = dbRefSklonisteAdmin.orderByChild("admin").equalTo(sAdminUser);
                    adminShelterQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                Shelter_Admin shelter_admin = dataSnapshot1.getValue(Shelter_Admin.class);
                                assert shelter_admin != null;
                                String sShelterOib = shelter_admin.getSkloniste();

                                Query shelterQuery = dbRefSkloniste.orderByChild("oib").equalTo(sShelterOib);
                                shelterQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                            Shelter shelter = dataSnapshot2.getValue(Shelter.class);
                                            assert shelter != null;
                                            tvNaziv.setText(shelter.getNaziv());
                                            tvAdresa.setText(shelter.getAdresa());
                                            tvGrad.setText(shelter.getGrad());
                                            tvOib.setText(shelter.getOib());
                                            tvIban.setText(shelter.getIban());
                                            tvDostupnihMjesta.setText(shelter.getDostupnih_mjesta());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}