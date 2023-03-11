package com.example.assignment_003;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private Context context;

    public CountryAdapter(ArrayList<Country> countryList, Context context) {
        this.countryList = countryList;
        this.context = context;
    }

    public void setCountriesData(ArrayList<Country> countries){
        this.countryList = countries;
    }

    public void addCountriesData(ArrayList<Country> countries){
        this.countryList.addAll(countries);
    }

    public ArrayList<Country> getCountryList() {
        return countryList;
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
        Country country = countryList.get(position);
        holder.flagImage.setImageResource(country.getFlagId());
        holder.countryName.setText(country.getCountry());
        holder.capitalName.setText(country.getCapital());

    }

    @Override
    public int getItemCount() {
        return countryList.size();
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
}
