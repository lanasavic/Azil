package com.example.azil.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.azil.Models.Admin;
import com.example.azil.Models.Shelter_Admin;
import com.example.azil.databinding.ActivityAddAnimalBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddAnimalActivity extends AppCompatActivity {
    private ActivityAddAnimalBinding binding;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    private Uri imgUri = null;
    Intent intent;
    DatabaseReference dbRefZivotinja, dbRefSklonisteZivotinja, dbRefAdmin, dbRefSklonisteAdmin, dbRefSkloniste;
    private String lastChild, adminEmail, sShelterOib;
    private Integer newKey, newKey1;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAnimalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            adminEmail = extras.getString("adminEmail");
        }

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAnimalActivity.this.finish();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

        binding.btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageMenu();
            }
        });
    }

    private String ime = "", opis = "";
    private void validateInputs() {
        ime = binding.etIme.getText().toString().trim();
        opis = binding.etOpis.getText().toString().trim();

        if(TextUtils.isEmpty(ime)){
            Toast.makeText(getApplicationContext(), "Unesite ime", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(opis)){
            Toast.makeText(getApplicationContext(), "Unesite opis", Toast.LENGTH_SHORT).show();
        }
        else if(imgUri == null){
            Toast.makeText(getApplicationContext(), "Dodajte sliku", Toast.LENGTH_SHORT).show();
        }
        else {
            addAnimalWithImage();
        }
    }

    private void addAnimalWithImage() {
        progressDialog.setMessage("Spremanje podataka...");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        String filePath = ""+timestamp;

        storageReference = FirebaseStorage.getInstance().getReference(filePath);
        storageReference.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        String uploadedImgUrl = ""+uriTask.getResult();

                        saveAnimal(uploadedImgUrl);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Error", e.toString());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Došlo je do pogreške", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAnimal(String uploadedImgUrl){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ime", ""+ime);
        hashMap.put("opis", ""+opis);
        hashMap.put("zahtjevi", "0");
        if(imgUri != null){
            hashMap.put("imgurl", ""+uploadedImgUrl);
        }

        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
        dbRefSklonisteZivotinja = FirebaseDatabase.getInstance().getReference("skloniste_zivotinja");
        dbRefAdmin = FirebaseDatabase.getInstance().getReference("admin");
        dbRefSklonisteAdmin = FirebaseDatabase.getInstance().getReference("skloniste_admin");

        Query adminEmailQuery = dbRefAdmin.orderByChild("email").equalTo(adminEmail);
        adminEmailQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Admin admin = dataSnapshot.getValue(Admin.class);
                    assert admin != null;
                    String sAdminUser = admin.getUsername();

                    Query adminShelterQuery = dbRefSklonisteAdmin.orderByChild("admin").equalTo(sAdminUser);
                    adminShelterQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                Shelter_Admin shelter_admin = dataSnapshot1.getValue(Shelter_Admin.class);
                                assert shelter_admin != null;
                                sShelterOib = shelter_admin.getSkloniste();
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

        dbRefZivotinja.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    lastChild = dataSnapshot.getKey();
                }
                assert lastChild != null;
                newKey = (Integer.parseInt(lastChild)+1);
                hashMap.put("sifra", ""+newKey);
                dbRefZivotinja.child(newKey.toString()).setValue(hashMap);

                dbRefSklonisteZivotinja.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            lastChild = dataSnapshot1.getKey();
                        }
                        assert lastChild != null;
                        newKey1 = (Integer.parseInt(lastChild)+1);
                        HashMap<String, Object> hashMap1 = new HashMap<>();
                        hashMap1.put("id", ""+newKey1);
                        hashMap1.put("skloniste", ""+sShelterOib);
                        hashMap1.put("zivotinja", ""+newKey);

                        dbRefSkloniste = FirebaseDatabase.getInstance().getReference("skloniste").child(sShelterOib).child("dostupnih_mjesta");
                        dbRefSkloniste.runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                try {
                                    count = Integer.parseInt(currentData.getValue(String.class));
                                } catch (NumberFormatException e) {
                                    count = 0;
                                }
                                currentData.setValue(Integer.toString(count));
                                return Transaction.success(currentData);
                            }
                            @Override
                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                if(count > 0){
                                    dbRefSkloniste.runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                            count--;
                                            currentData.setValue(Integer.toString(count));
                                            return Transaction.success(currentData);
                                        }
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                            dbRefSklonisteZivotinja.child(newKey1.toString()).setValue(hashMap1);
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Uspješno spremljeno!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(AddAnimalActivity.this, AdminActivity.class));
                                            finish();
                                        }
                                    });
                                }
                                else{
                                    count = 0;
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Ups! U skloništu više nema dostupnih mjesta.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AddAnimalActivity.this, AdminActivity.class));
                                    finish();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addImageMenu() {
        PopupMenu popupMenu = new PopupMenu(this, binding.imgZivotinja);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Kamera");
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