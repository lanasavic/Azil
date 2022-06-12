package com.example.azil.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azil.Fragments.AnimalsFragment;
import com.example.azil.Fragments.DonationsFragment;
import com.example.azil.Fragments.ShelterFragment;
import com.example.azil.Models.Admin;
import com.example.azil.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {
    private Button btnLogout;
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    FirebaseUser firebaseUser;
    DatabaseReference dbRefAdmin;
    TextView helloAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(), "Odjavili ste se.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRefAdmin = FirebaseDatabase.getInstance().getReference("admin");
        Query adminEmailQuery = dbRefAdmin.orderByChild("email").equalTo(firebaseUser.getEmail());
        adminEmailQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Admin admin = dataSnapshot.getValue(Admin.class);
                    helloAdmin = findViewById(R.id.helloAdmin);
                    helloAdmin.setText(getString(R.string.holder_helloAdmin, admin.getUsername()));
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.shelter :
                        selectorFragment = new ShelterFragment();
                        break;

                    case R.id.animals :
                        selectorFragment = new AnimalsFragment();
                        break;

                    case R.id.donations :
                        selectorFragment = new DonationsFragment();
                        break;
                }
                if(selectorFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.relativeLayoutAdmin, selectorFragment).commit();
                }

                return true;
            }
        });
    }
}