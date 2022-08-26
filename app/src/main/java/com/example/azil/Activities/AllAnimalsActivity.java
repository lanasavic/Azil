package com.example.azil.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.azil.Adapters.ViewPager2Adapter;
import com.example.azil.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AllAnimalsActivity extends AppCompatActivity {
    private Button btnBack;
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager2;
    private ViewPager2Adapter viewPager2Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_animals);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        viewPager2 = findViewById(R.id.viewPager2Fragments);
        viewPager2Adapter = new ViewPager2Adapter(getSupportFragmentManager());
        viewPager2.setAdapter(viewPager2Adapter);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.species:
                        viewPager2.setCurrentItem(0);
                        break;
                    case R.id.breed:
                        viewPager2.setCurrentItem(1);
                        break;
                    case R.id.location:
                        viewPager2.setCurrentItem(2);
                        break;
                    case R.id.time:
                        viewPager2.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });

        viewPager2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.species).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.breed).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.location).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.time).setChecked(true);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}