package com.example.azil.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.azil.Models.Admin;
import com.example.azil.Models.Shelter;
import com.example.azil.Models.Shelter_Admin;
import com.example.azil.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DeleteActivity extends AppCompatActivity {
    private Button btnCancel, btnDelete;
    FirebaseUser firebaseUser;
    DatabaseReference dbRefSkloniste, dbRefAdmin, dbRefZivotinja, dbRefSklonisteAdmin, dbRefSklonisteZivotinja;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        btnCancel = findViewById(R.id.btnCancel);
        btnDelete = findViewById(R.id.btnDelete);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteActivity.this.finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteShelter();
            }
        });
    }

    private void deleteShelter() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Brisanje...");
        progressDialog.show();

        dbRefSkloniste = FirebaseDatabase.getInstance().getReference("skloniste");
        dbRefAdmin = FirebaseDatabase.getInstance().getReference("admin");
        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
        dbRefSklonisteAdmin = FirebaseDatabase.getInstance().getReference("skloniste_admin");
        dbRefSklonisteZivotinja = FirebaseDatabase.getInstance().getReference("skloniste_zivotinja");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Query adminEmailQuery = dbRefAdmin.orderByChild("email").equalTo(firebaseUser.getEmail());
        adminEmailQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Admin admin = dataSnapshot.getValue(Admin.class);
                    assert admin != null;
                    String sAdminUser = admin.getUsername();

                    dataSnapshot.getRef().removeValue();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(firebaseUser.getEmail(), "123456"); //password

                    // Prompt the user to re-provide their sign-in credentials
                    firebaseUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    firebaseUser.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("DELETE", "User account deleted.");
                                                    }
                                                }
                                            });
                                }
                            });

                    Query adminShelterQuery = dbRefSklonisteAdmin.orderByChild("admin").equalTo(sAdminUser);
                    adminShelterQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                Shelter_Admin shelter_admin = dataSnapshot1.getValue(Shelter_Admin.class);
                                assert shelter_admin != null;
                                String sShelterOib = shelter_admin.getSkloniste();
                                dataSnapshot1.getRef().removeValue();

                                Query shelterQuery = dbRefSkloniste.orderByChild("oib").equalTo(sShelterOib);
                                shelterQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                            progressDialog.dismiss();
                                            Toast.makeText(DeleteActivity.this, "Brisanje uspješno!", Toast.LENGTH_SHORT).show();
                                            dataSnapshot2.getRef().removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });

                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}