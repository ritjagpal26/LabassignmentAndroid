package com.example.labassignment;

import android.os.Parcel;
import android.os.Parcelable;

public class Favplace implements Parcelable {
    protected Favplace(Parcel in) {
        id = in.readInt();
        address = in.readString();
        if (in.readByte() == 0) {
            Lat = null;
        } else {
            Lat = in.readDouble();
        }
        if (in.readByte() == 0) {
            Long = null;
        } else {
            Long = in.readDouble();
        }
    }

    public static final Creator<Favplace> CREATOR = new Creator<Favplace>() {
        @Override
        public Favplace createFromParcel(Parcel in) {
            return new Favplace(in);
        }

        @Override
        public Favplace[] newArray(int size) {
            return new Favplace[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(address);
        if (Lat == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(Lat);
        }
        if (Long == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(Long);
        }
    }
}
