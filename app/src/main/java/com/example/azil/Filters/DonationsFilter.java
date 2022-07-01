package com.example.azil.Filters;

import android.widget.Filter;

import com.example.azil.Adapters.DonationsAdapter;
import com.example.azil.Adapters.SheltersAdapter;
import com.example.azil.Models.RequestedDonation;
import com.example.azil.Models.Shelter;

import java.util.ArrayList;

public class DonationsFilter extends Filter {
    ArrayList<RequestedDonation> donationsList;
    DonationsAdapter donationsAdapter;

    public DonationsFilter(ArrayList<RequestedDonation> donationsList, DonationsAdapter donationsAdapter){
        this.donationsList = donationsList;
        this.donationsAdapter = donationsAdapter;
    }

    protected FilterResults performFiltering(CharSequence constraint){
        FilterResults res = new FilterResults();

        if(constraint != null && constraint.length()>0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<RequestedDonation> filteredDonations = new ArrayList<>();

            for(int i=0; i<donationsList.size(); i++){
                if(donationsList.get(i).getOpis().toUpperCase().contains(constraint) || donationsList.get(i).getKolicina().toUpperCase().contains(constraint)){
                    filteredDonations.add(donationsList.get(i));
                }
            }
            res.count = filteredDonations.size();
            res.values = filteredDonations;
        }
        else{
            res.count = donationsList.size();
            res.values = donationsList;
        }
        return res;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        donationsAdapter.lDonations = (ArrayList<RequestedDonation>)results.values;
        donationsAdapter.notifyDataSetChanged();
    }
}