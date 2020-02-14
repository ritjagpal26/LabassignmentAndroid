package com.example.labassignment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    private HashMap<String , String> getPlace(JSONObject googlePlaceJson)
    {
        HashMap<String , String> googlePlacesMap = new HashMap<>();

        //store all the parameters using String

        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        try {

            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }

            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity");
            }

            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = googlePlaceJson.getString("reference");

            googlePlacesMap.put("place_name" , placeName);
            googlePlacesMap.put("vicinity" , vicinity);
            googlePlacesMap.put("lat" , latitude);
            googlePlacesMap.put("lng" , longitude);
            googlePlacesMap.put("reference" , reference);

        } catch(JSONException e) {
            e.printStackTrace();

        }

        return googlePlacesMap;

        //to store one place we are using a HashMap
    }


    //to store all the places create a list of HashMap
    private List<HashMap<String,String>> getPlaces(JSONArray jsonArray)
    {

        //getPlace returns a HashMap for each place
        //getPlaces() creates a list of HashMaps

        int count = jsonArray.length();
        List<HashMap<String,String>> placesList = new ArrayList<>();
        HashMap<String,String> placeMap = null; //to store each place we fetch

        for(int i=0 ; i<count;i++) {

            //use getPlace method to fetch one place
            //then , add it to list of hashmap

            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return placesList;
    }
    public List<HashMap<String,String>> parse(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }
    public  String[] parseDirections(String jsondata) {

        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPaths(jsonArray);
    }

    private  String[]  getPaths(JSONArray jsonArray){
        int count = jsonArray.length();
        String[] polilines = new String[count];
        for (int i = 0 ; i<count;i++){
            try {
                polilines[i] = getPath(jsonArray.getJSONObject(i));
            }
            catch(JSONException e){
                e.printStackTrace();
            }

        }
        return polilines;
    }

    private String getPath(JSONObject jsonObject) {
        String polyline = "";
        try {
            polyline = jsonObject.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  polyline;

    }

    public  HashMap<String,String> parseDistance(String jsonData){
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
        }catch (JSONException e){
            e.printStackTrace();


        }
        return getDuration(jsonArray);
    }

    private  HashMap<String,String > getDuration(JSONArray distanceDuration){
        HashMap<String, String> directionMAp = new HashMap<>();
        String duration = "";
        String distance = "";
        try {
            duration = distanceDuration.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = distanceDuration.getJSONObject(0).getJSONObject("distance").getString("text");

            directionMAp.put("duration", duration);
            directionMAp.put("distance", distance);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return directionMAp;

    }
    //call this parse method whenever you create Data Parser
    //it will parse the JSON data n send it to getPlaces method
    //getPlaces method takes the JSONArray
    //will call getPlace method to fetch each element for each place and store it in a list
    //return the list to parse method


}
