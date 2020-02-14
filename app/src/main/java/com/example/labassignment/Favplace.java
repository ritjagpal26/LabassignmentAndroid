package com.example.labassignment;

public class Favplace {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Favplace(int id, String address, Double lat, Double aLong) {
        this.address = address;
        Lat = lat;
        Long = aLong;
        this.id = id;
    }
int id;
    String address;
    Double Lat;
    Double Long;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLong() {
        return Long;
    }

    public void setLong(Double aLong) {
        Long = aLong;
    }
}
