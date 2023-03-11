package com.example.assignment_003;

public class Country {
    private String country;
    private String capital;
    private int flagId;


    //  Constructive function
    Country(String country, String capital, int flagId) {
        this.country = country;
        this.capital = capital;
        this.flagId = flagId;
    }


    //  getter and setter
    public int getFlagId() {
        return flagId;
    }

    public String getCountry() {
        return country;
    }

    public String getCapital() {
        return capital;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public void setFlagId(int flagId) {
        this.flagId = flagId;
    }


}
