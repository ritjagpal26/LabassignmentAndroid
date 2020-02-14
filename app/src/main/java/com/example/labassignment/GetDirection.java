package com.example.labassignment;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

public class GetDirection extends AsyncTask<Object,String, String> {
    GoogleMap mMap;
    String directionData;
    String url;

    String distance;
    String duration;

    LatLng latLng;

    LatLng latLngUser;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        latLng = (LatLng) objects[2];
        latLngUser = (LatLng) objects[3];


        GetUrl fetchUrl = new GetUrl();
        try {
            directionData = fetchUrl.readUrl(url);
        }catch (IOException e){
            e.printStackTrace();
        }

        return directionData;
    }

    @Override
    protected void onPostExecute(String s) {
        HashMap<String, String> distanceData = null;
        DataParser distanceParser = new DataParser();
        distanceData = distanceParser.parseDistance(s);
        distance = distanceData.get("distance");
        duration = distanceData.get( "duration" );
        mMap.clear();
        // create new marker with new title and snippet

        MarkerOptions options = new MarkerOptions().position( latLng )
                .draggable( true )
                .title( "Duration" + duration)
                .snippet( "Distance: " + distance )
                .icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE ));



        mMap.addMarker( options );

        mMap.addMarker( new MarkerOptions().position( latLngUser )
                .title( "Your Location" ));

            String[] directionList;
            DataParser directioParser = new DataParser();
            directionList = directioParser.parseDirections(s);
            displayDirection(directionList);


    }
    private  void  displayDirection(String[] directionLiat){
        int count = directionLiat.length;
        for (int  i=0;i<count;i++){
            PolylineOptions options = new PolylineOptions().color(Color.RED).width(10).addAll(PolyUtil.decode(directionLiat[i]));
            mMap.addPolyline(options);


        }
    }
}
