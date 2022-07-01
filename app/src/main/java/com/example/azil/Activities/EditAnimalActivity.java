package com.example.azil.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.azil.R;
import com.example.azil.databinding.ActivityEditAnimalBinding;
import com.example.azil.databinding.ActivityEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditAnimalActivity extends AppCompatActivity {
    private ActivityEditAnimalBinding binding;
    StorageReference storageReference;
    private Uri imgUri = null;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    private String zivotinjaSifra;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditAnimalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);

        zivotinjaSifra = getIntent().getStringExtra("zivotinjaSifra");
        loadAnimalInfo();

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

        binding.btnEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageMenu();
            }
        });
    }

    private void loadAnimalInfo() {
        databaseReference = FirebaseDatabase.getInstance().getReference("zivotinja");
        databaseReference.child(zivotinjaSifra).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ime = ""+snapshot.child("ime").getValue();
                String opis = ""+snapshot.child("opis").getValue();
                String img = ""+snapshot.child("imgurl").getValue();

                binding.etIme.setText(ime);
                binding.etOpis.setText(opis);
                Picasso.get().load(img).into(binding.imgZivotinja);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String ime = "", opis = "";
    private void validateInputs() {
        ime = binding.etIme.getText().toString().trim();
        opis = binding.etOpis.getText().toString().trim();

        if(TextUtils.isEmpty(ime)){
            Toast.makeText(getApplicationContext(), "Unesite novo ime", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(opis)){
            Toast.makeText(getApplicationContext(), "Unesite novi opis", Toast.LENGTH_SHORT).show();
        }
        else{
            if(imgUri == null){
                updateAnimalWithoutImage();
            }
            else{
                updateAnimalWithImage();
            }
        }
    }

    private void updateAnimalWithoutImage() {
        progressDialog.setMessage("Ažuriranje podataka...");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ime", ""+ime);
        hashMap.put("opis", ""+opis);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("zivotinja");
        databaseReference.child(zivotinjaSifra).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Uspješno ažurirano!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditAnimalActivity.this, AdminActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error", e.toString());
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Došlo je do pogreške.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAnimalWithImage() {
        progressDialog.setMessage("Ažuriranje podataka...");
        progressDialog.show();

        String filePath = zivotinjaSifra;

        storageReference = FirebaseStorage.getInstance().getReference(filePath);
        storageReference.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedImgUrl = ""+uriTask.getResult();

                        updateAnimal(uploadedImgUrl);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error", e.toString());
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Došlo je do pogreške.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAnimal(String uploadedImgUrl) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ime", ""+ime);
        hashMap.put("opis", ""+opis);
        if(imgUri != null){
            hashMap.put("imgurl", ""+uploadedImgUrl);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("zivotinja");
        databaseReference.child(zivotinjaSifra).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Ažuriranje uspješno!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditAnimalActivity.this, AdminActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error", e.toString());
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Došlo je do pogreške.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addImageMenu() {
        PopupMenu popupMenu = new PopupMenu(this, binding.imgZivotinja);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Kamera"); //iz nekog razloga ne radi na mobitelu
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Galerija");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int which = item.getItemId();
                if (which == 0){
                    pickImageCamera();
                }
                else if(which == 1){
                    pickImageGallery();
                }
                return false;
            }
        });
    }

    private void pickImageCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nova slika");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Primjerak opisa slike");
        imgUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private void pickImageGallery() {
        intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData(); //unnecessary bcs camera
                    binding.imgZivotinja.setImageURI(imgUri);
                }
                else{
                    Log.d("Intent", "Camera: CANCELLED");
                }
            }
        });

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        assert data != null;
                        imgUri = data.getData();
                        binding.imgZivotinja.setImageURI(imgUri);
                    }
                    else{
                        Log.d("Intent", "Gallery: CANCELLED");
                    }
                }
            });
}