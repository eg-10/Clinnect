package com.example.clinnect1;

public class Place {
    private String name,address,id;
    private double rating;

    public Place(String name, String address, double rating,String id) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }
}
