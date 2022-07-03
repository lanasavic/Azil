package com.example.azil.Filters;

import android.widget.Filter;

import com.example.azil.Adapters.AdminReceivedDonationsAdapter;
import com.example.azil.Models.ReceivedDonation;

import java.util.ArrayList;

public class AdminReceivedDonationsFilter extends Filter {
    ArrayList<ReceivedDonation> donationsList;
    AdminReceivedDonationsAdapter adminReceivedDonationsAdapter;

    public AdminReceivedDonationsFilter(ArrayList<ReceivedDonation> donationsList, AdminReceivedDonationsAdapter adminReceivedDonationsAdapter){
        this.donationsList = donationsList;
        this.adminReceivedDonationsAdapter = adminReceivedDonationsAdapter;
    }

    protected FilterResults performFiltering(CharSequence constraint){
        FilterResults res = new FilterResults();

        if(constraint != null && constraint.length()>0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<ReceivedDonation> filteredDonations = new ArrayList<>();

            for(int i=0; i<donationsList.size(); i++){
                if(donationsList.get(i).getIme_prezime().toUpperCase().contains(constraint) || donationsList.get(i).getEmail().toUpperCase().contains(constraint) || donationsList.get(i).getKolicina().toUpperCase().contains(constraint)){
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
        adminReceivedDonationsAdapter.lDonations = (ArrayList<ReceivedDonation>)results.values;
        adminReceivedDonationsAdapter.notifyDataSetChanged();
    }
}