package com.example.azil.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import com.example.azil.Models.Animal_Breed;
import com.example.azil.Models.Animal_Location;
import com.example.azil.Models.Animal_Species;
import com.example.azil.Models.Animal_Time;
import com.example.azil.Models.Breed;
import com.example.azil.Models.Location;
import com.example.azil.Models.Species;
import com.example.azil.Models.Time;
import com.example.azil.databinding.ActivityEditAnimalBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class EditAnimalActivity extends AppCompatActivity {
    private ActivityEditAnimalBinding binding;
    StorageReference storageReference;
    private Uri imgUri = null;
    ProgressDialog progressDialog;
    DatabaseReference dbRefZivotinja, dbRefVrsta, dbRefZivotinjaVrsta, dbRefPasmina, dbRefZivotinjaPasmina,
            dbRefLokacija, dbRefZivotinjaLokacija, dbRefVrijeme, dbRefZivotinjaVrijeme;
    private String zivotinjaSifra, pasminaId, lokacijaId, mjesecId, pasminaNaziv, lokacijaNaziv, mjesecNaziv;
    Intent intent;
    private ArrayList<Species> speciesArrayList;
    private ArrayList<Breed> breedArrayList;
    private ArrayList<Location> locationArrayList;
    private ArrayList<Time> timeArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditAnimalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);

        zivotinjaSifra = getIntent().getStringExtra("zivotinjaSifra");
        pasminaId = getIntent().getStringExtra("pasminaId");
        pasminaNaziv = getIntent().getStringExtra("pasminaNaziv");
        lokacijaId = getIntent().getStringExtra("lokacijaId");
        lokacijaNaziv = getIntent().getStringExtra("lokacijaNaziv");
        mjesecId = getIntent().getStringExtra("mjesecId");
        mjesecNaziv = getIntent().getStringExtra("mjesecNaziv");

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

        /*loadSpecies();
        binding.dropdownSpecies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speciesPickDialog();
            }
        });*/

        loadBreeds();
        binding.dropdownBreed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breedPickDialog();
            }
        });

        loadLocations();
        binding.dropdownLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationPickDialog();
            }
        });

        loadMonths();
        binding.dropdownTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickDialog();
            }
        });
    }

    /*private void loadSpecies() {
        speciesArrayList = new ArrayList<>();

        dbRefVrsta = FirebaseDatabase.getInstance().getReference("vrsta");
        dbRefVrsta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                speciesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Species species = dataSnapshot.getValue(Species.class);
                    speciesArrayList.add(species);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void speciesPickDialog() {
        String[] speciesArray = new String[speciesArrayList.size()];
        for (int i=0; i<speciesArrayList.size(); i++){
            speciesArray[i] = speciesArrayList.get(i).getNaziv();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Odaberite vrstu").setItems(speciesArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String species = speciesArray[which];
                binding.dropdownSpecies.setText(species);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(650, 800);
    }*/

    private void loadBreeds() {
        breedArrayList = new ArrayList<>();

        dbRefPasmina = FirebaseDatabase.getInstance().getReference("pasmina");
        dbRefPasmina.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                breedArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Breed breed = dataSnapshot.getValue(Breed.class);
                    breedArrayList.add(breed);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void breedPickDialog() {
        String[] breedArray = new String[breedArrayList.size()];
        for (int i=0; i<breedArrayList.size(); i++){
            breedArray[i] = breedArrayList.get(i).getNaziv();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Odaberite pasminu").setItems(breedArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String breed = breedArray[which];
                binding.dropdownBreed.setText(breed);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(650, 800);
    }

    private void loadLocations() {
        locationArrayList = new ArrayList<>();

        dbRefLokacija = FirebaseDatabase.getInstance().getReference("lokacija");
        dbRefLokacija.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locationArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Location location = dataSnapshot.getValue(Location.class);
                    locationArrayList.add(location);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void locationPickDialog() {
        String[] locationArray = new String[locationArrayList.size()];
        for (int i=0; i<locationArrayList.size(); i++){
            locationArray[i] = locationArrayList.get(i).getNaziv();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Odaberite lokaciju pronalaska").setItems(locationArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String location = locationArray[which];
                binding.dropdownLocation.setText(location);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(650, 800);
    }

    private void loadMonths() {
        timeArrayList = new ArrayList<>();

        dbRefVrijeme = FirebaseDatabase.getInstance().getReference("vrijeme");
        dbRefVrijeme.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timeArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Time time = dataSnapshot.getValue(Time.class);
                    timeArrayList.add(time);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void timePickDialog() {
        String[] timeArray = new String[timeArrayList.size()];
        for (int i=0; i<timeArrayList.size(); i++){
            timeArray[i] = timeArrayList.get(i).getMjesec();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Odaberite vrijeme pronalaska").setItems(timeArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String time = timeArray[which];
                binding.dropdownTime.setText(time);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(650, 800);
    }

    private void loadAnimalInfo() {
        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
        dbRefZivotinja.child(zivotinjaSifra).addListenerForSingleValueEvent(new ValueEventListener() {
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

        dbRefVrsta = FirebaseDatabase.getInstance().getReference("vrsta");
        dbRefPasmina = FirebaseDatabase.getInstance().getReference("pasmina");
        dbRefLokacija = FirebaseDatabase.getInstance().getReference("lokacija");
        dbRefVrijeme = FirebaseDatabase.getInstance().getReference("vrijeme");
        dbRefZivotinjaVrsta = FirebaseDatabase.getInstance().getReference("zivotinja_vrsta");
        dbRefZivotinjaPasmina = FirebaseDatabase.getInstance().getReference("zivotinja_pasmina");
        dbRefZivotinjaLokacija = FirebaseDatabase.getInstance().getReference("zivotinja_lokacija");
        dbRefZivotinjaVrijeme = FirebaseDatabase.getInstance().getReference("zivotinja_vrijeme");

        Query animalSpeciesQuery = dbRefZivotinjaVrsta.orderByChild("zivotinja").equalTo(zivotinjaSifra);
        animalSpeciesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    Animal_Species animal_species = dataSnapshot1.getValue(Animal_Species.class);
                    assert animal_species != null;
                    String sVrsta = animal_species.getVrsta();

                    Query speciesQuery = dbRefVrsta.orderByChild("sifra").equalTo(sVrsta);
                    speciesQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                Species species = dataSnapshot2.getValue(Species.class);
                                assert species != null;
                                String vrsta = species.getNaziv();
                                binding.dropdownSpecies.setText(vrsta);
                                binding.dropdownSpecies.setFocusable(false);
                                binding.dropdownSpecies.setEnabled(false);
                                binding.dropdownSpecies.setTextColor(Color.GRAY);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        Query animalBreedQuery = dbRefZivotinjaPasmina.orderByChild("zivotinja").equalTo(zivotinjaSifra);
        animalBreedQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    Animal_Breed animal_breed = dataSnapshot1.getValue(Animal_Breed.class);
                    assert animal_breed != null;
                    String sPasmina = animal_breed.getPasmina();

                    Query breedQuery = dbRefPasmina.orderByChild("sifra").equalTo(sPasmina);
                    breedQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                Breed breed = dataSnapshot2.getValue(Breed.class);
                                assert breed != null;
                                String pasmina = breed.getNaziv();
                                binding.dropdownBreed.setText(pasmina);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        Query animalLocationQuery = dbRefZivotinjaLokacija.orderByChild("zivotinja").equalTo(zivotinjaSifra);
        animalLocationQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    Animal_Location animal_location = dataSnapshot1.getValue(Animal_Location.class);
                    assert animal_location != null;
                    String sLokacija = animal_location.getLokacija();

                    Query locationQuery = dbRefLokacija.orderByChild("sifra").equalTo(sLokacija);
                    locationQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                Location location = dataSnapshot2.getValue(Location.class);
                                assert location != null;
                                String lokacija = location.getNaziv();
                                binding.dropdownLocation.setText(lokacija);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        Query animalTimeQuery = dbRefZivotinjaVrijeme.orderByChild("zivotinja").equalTo(zivotinjaSifra);
        animalTimeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                    Animal_Time animal_time = dataSnapshot1.getValue(Animal_Time.class);
                    assert animal_time != null;
                    String sMjesec = animal_time.getMjesec();

                    Query timeQuery = dbRefVrijeme.orderByChild("sifra").equalTo(sMjesec);
                    timeQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot2 : snapshot.getChildren()){
                                Time time = dataSnapshot2.getValue(Time.class);
                                assert time != null;
                                String mjesec = time.getMjesec();
                                binding.dropdownTime.setText(mjesec);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String ime = "", opis = "", vrsta = "", pasmina = "", lokacija = "", vrijeme = "";
    private void validateInputs() {
        ime = binding.etIme.getText().toString().trim();
        opis = binding.etOpis.getText().toString().trim();
        vrsta = binding.dropdownSpecies.getText().toString().trim();
        pasmina = binding.dropdownBreed.getText().toString().trim();
        lokacija = binding.dropdownLocation.getText().toString().trim();
        vrijeme = binding.dropdownTime.getText().toString().trim();

        if(TextUtils.isEmpty(ime)){
            Toast.makeText(getApplicationContext(), "Unesite novo ime", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(opis)){
            Toast.makeText(getApplicationContext(), "Unesite novi opis", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(vrsta)){
            Toast.makeText(getApplicationContext(), "Odaberite vrstu", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pasmina)){
            Toast.makeText(getApplicationContext(), "Odaberite pasminu", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(lokacija)){
            Toast.makeText(getApplicationContext(), "Odaberite lokaciju pronalaska", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(vrijeme)){
            Toast.makeText(getApplicationContext(), "Odaberite vrijeme pronalaska", Toast.LENGTH_SHORT).show();
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

        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
        dbRefVrsta = FirebaseDatabase.getInstance().getReference("vrsta");
        dbRefPasmina = FirebaseDatabase.getInstance().getReference("pasmina");
        dbRefLokacija = FirebaseDatabase.getInstance().getReference("lokacija");
        dbRefVrijeme = FirebaseDatabase.getInstance().getReference("vrijeme");
        dbRefZivotinjaVrsta = FirebaseDatabase.getInstance().getReference("zivotinja_vrsta");
        dbRefZivotinjaPasmina = FirebaseDatabase.getInstance().getReference("zivotinja_pasmina");
        dbRefZivotinjaLokacija = FirebaseDatabase.getInstance().getReference("zivotinja_lokacija");
        dbRefZivotinjaVrijeme = FirebaseDatabase.getInstance().getReference("zivotinja_vrijeme");

        dbRefZivotinja.child(zivotinjaSifra).updateChildren(hashMap)
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
                        Toast.makeText(getApplicationContext(), "Došlo je do pogreške", Toast.LENGTH_SHORT).show();
                    }
                });

        //TODO: update pasmina-br_zivotinja if pasminaNaziv != pasmina, same for lokacija and mjesec;
        // zivotinja_pasmina,zivotinja_lokacija,zivotinja_vrijeme change pasmina, lokacija, vrijeme if necessary
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
                        Toast.makeText(getApplicationContext(), "Došlo je do pogreške", Toast.LENGTH_SHORT).show();
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

        dbRefZivotinja = FirebaseDatabase.getInstance().getReference("zivotinja");
        dbRefZivotinja.child(zivotinjaSifra).updateChildren(hashMap)
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
                        Toast.makeText(getApplicationContext(), "Došlo je do pogreške", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addImageMenu() {
        PopupMenu popupMenu = new PopupMenu(this, binding.imgZivotinja);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Kamera"); //doesn't work on my phone ?
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