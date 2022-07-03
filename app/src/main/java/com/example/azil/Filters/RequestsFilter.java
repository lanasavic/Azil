package com.example.azil.Filters;

import android.widget.Filter;

import com.example.azil.Adapters.RequestsAdapter;
import com.example.azil.Models.Request;

import java.util.ArrayList;

public class RequestsFilter extends Filter {
    ArrayList<Request> requestsList;
    RequestsAdapter requestsAdapter;

    public RequestsFilter(ArrayList<Request> requestsList, RequestsAdapter requestsAdapter){
        this.requestsList = requestsList;
        this.requestsAdapter = requestsAdapter;
    }

    protected FilterResults performFiltering(CharSequence constraint){
        FilterResults res = new FilterResults();

        if(constraint != null && constraint.length()>0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<Request> filteredRequests = new ArrayList<>();

            for(int i=0; i<requestsList.size(); i++){
                if(requestsList.get(i).getIme_prezime().toUpperCase().contains(constraint) || requestsList.get(i).getEmail().toUpperCase().contains(constraint)){
                    filteredRequests.add(requestsList.get(i));
                }
            }
            res.count = filteredRequests.size();
            res.values = filteredRequests;
        }
        else{
            res.count = requestsList.size();
            res.values = requestsList;
        }
        return res;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        requestsAdapter.lRequests = (ArrayList<Request>)results.values;
        requestsAdapter.notifyDataSetChanged();
    }
}