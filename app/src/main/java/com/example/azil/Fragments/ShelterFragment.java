package com.example.azil.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.azil.R;

public class ShelterFragment extends Fragment {
    private Button btnDelete;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shelter, container, false);

        btnDelete = view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "WORKS", Toast.LENGTH_SHORT).show(); //TODO: maybe rather open activity on click nego dialog jer je dialog isto fragment, sort of

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Brisanje skloništa"); //i profila?
                alertDialog.setMessage("Brisanje skloništa je nepovratno. Svi podaci bit će obrisani u potpunosti. Jeste li sigurni da želite nastaviti?");
                alertDialog.setIcon(R.mipmap.ic_paw);
                alertDialog.setPositiveButton("Nastavi", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        progressDialog.setMessage("Brisanje...");
                        progressDialog.dismiss();
                    }
                });
                alertDialog.setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}