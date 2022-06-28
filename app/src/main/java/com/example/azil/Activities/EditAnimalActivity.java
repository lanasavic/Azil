package com.example.azil.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.azil.R;
import com.example.azil.databinding.ActivityEditAnimalBinding;
import com.example.azil.databinding.ActivityEditBinding;

public class EditAnimalActivity extends AppCompatActivity {
    private ActivityEditAnimalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditAnimalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAnimalActivity.this.finish();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });
    }

    private void validateInputs() {
        Toast.makeText(this, "Ayo", Toast.LENGTH_SHORT).show();
    }
}