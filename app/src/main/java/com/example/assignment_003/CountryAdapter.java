package com.example.assignment_003;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Customize adapter and ViewHolder
 */
public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {
    private ArrayList<Country> countryList;
    private ArrayList<Country> countryListFiltered;
    private Context context;

    public CountryAdapter(ArrayList<Country> countryList, Context context) {
        this.countryList = countryList;
        this.countryListFiltered = countryList;
        this.context = context;
    }


    public void addCountriesData(ArrayList<Country> countries){
        this.countryList.addAll(countries);
        this.countryListFiltered = this.countryList;
    }


    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()
        ).inflate(R.layout.item_recycler_view, parent, false);
        return new CountryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        Country country = countryListFiltered.get(position);
        holder.flagImage.setImageResource(country.getFlagId());
        holder.countryName.setText(country.getCountry());
        holder.capitalName.setText(country.getCapital());

    }

    @Override
    public int getItemCount() {

        return countryListFiltered.size();
    }


    public static class CountryViewHolder extends RecyclerView.ViewHolder {

        private TextView countryName;
        private TextView capitalName;
        private ImageView flagImage;

        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views using itemView.findViewById()
            countryName = itemView.findViewById(R.id.item_country);
            capitalName = itemView.findViewById(R.id.item_capital);
            flagImage = itemView.findViewById(R.id.flagView);
        }
    }


    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();

                if (query.isEmpty()){
                    countryListFiltered = countryList;
                }
                else {
                    ArrayList<Country> temp = new ArrayList<>();
                    for (Country item : countryList) {
                        if (item.getCountry().toLowerCase().contains(query)) {
                            temp.add(item);
                        }
                    }
                    countryListFiltered = temp;
                }
                results.values = countryListFiltered;
                results.count = countryListFiltered.size();
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                countryListFiltered = (ArrayList<Country>) results.values;
                notifyDataSetChanged();
            }
        };
    }


}
