package com.example.labassignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    Favplace favplace;
    public static final String DATABASE_NAME = "mydatabse";
    SQLiteDatabase mDatabse;

    GoogleMap mMap;
    String address;
    private final int REQUEST_CODE = 99;
    int PROXIMITY_RADIUS = 10000;

    SearchView searchView;
    private GoogleApiClient client;
    private Location lastLocation;

    private Marker currentLocationMarker;

    LatLng latLng;
    double destLat, destLong;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;


    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMap();
        mDatabse = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        createTable(mDatabse);

//        searchView = findViewById(R.id.sv_locations);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                String location = searchView.getQuery().toString();
//                List<Address> addressList = null;
//                if (location != null || !location.equals("")){
//                    Geocoder geocoder = new Geocoder(MainActivity.this);
//                    try {
//                        addressList = geocoder.getFromLocationName(location,1);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Address address = addressList.get(0);
//                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
//
//
//                }
//                return false;
//            }

//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.places_autocomplete_fragment);
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        mMap.addMarker(new MarkerOptions().position(latLng)
                                .title(place.getName())
                                .snippet(place.getAddress())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        CameraPosition cameraPosition = CameraPosition.builder()
                                .target(latLng)
                                .zoom(15)
                                .bearing(0)
                                .tilt(45)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }
                }

                @Override
                public void onError(@NonNull Status status) {

                }
            });
        }
//        String apikey =

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.api_key_places));
        }
        PlacesClient placesClient = Places.createClient(this);

        getUserLocation();
        if (!checkPermission())
            requestPermission();
        else
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected synchronized void buildGoogleApiClient() {

        //create a GoogleApiClient and connect it

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();

        //call this method inside OnMapReady()


    }

    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;

        //remove the marker if already set to some other place

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        //get he lat and lon to set the marker to it

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions(); //to set properties to that marker
        markerOptions.position(latLng);
        markerOptions.title("CURRENT LOCATION");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        currentLocationMarker = mMap.addMarker(markerOptions);

        //now we need to move camera to that loaction

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(5));

        //stop the location update once it is set
        if (client != null) {

            //location set

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }


    }


    public void onClick(View v) {
        Object dataTransfer[] = new Object[2];
        //first object will be mMap , scnd will be url

        GetNearByplaces getNearbyPlacesData = new GetNearByplaces();

        switch (v.getId()) {
//            case R.id.B_Search:
//            {
//                EditText tf_location = (EditText) findViewById(R.id.TF_location);
//                String location = tf_location.getText().toString();
//
//                List<Address> addressList = null;
//
//                MarkerOptions mo = new MarkerOptions();
//
//                if (!location.equals("")) {
//                    //if no an empty string
//                    //use geocoder class here
//
//                    Geocoder geocoder = new Geocoder(this);
//                    try {
//                        addressList = geocoder.getFromLocationName(location, 5);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    //search for a particular loaction ; put a marker on those 2 3 result addresses it gives
//
//                    for (int i = 0; i < addressList.size(); i++) {
//                        Address myAddress = addressList.get(i);
//                        LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
//                        mo.position(latLng);
//                        mo.title("Your Search Result");
//                        mMap.addMarker(mo);
//
//                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//                    }
//
//
//                }
//
//            }
//
//            break;

            case R.id.B_Hospital:
                mMap.clear(); //remove all the markers from the map
                String hospital = "hospital";
                String url = getUrl(latitude, longitude, hospital);


                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);

                Toast.makeText(this, "Showing Nearby Hospitals", Toast.LENGTH_LONG).show();
                break;

            case R.id.B_Restaurant:

                mMap.clear(); //remove all the markers from the map
                String restaurant = "restaurant";
                url = getUrl(latitude, longitude, restaurant);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);

                Toast.makeText(this, "Showing Nearby Restaurants", Toast.LENGTH_LONG).show();
                break;

            case R.id.B_School:

                mMap.clear(); //remove all the markers from the map
                String school = "school";
                url = getUrl(latitude, longitude, school);

                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                getNearbyPlacesData.execute(dataTransfer);

                Toast.makeText(this, "Showing Nearby Schools", Toast.LENGTH_LONG).show();
                break;
            case R.id.B_favplaces:
                //start activity to another activity to seee the list of employee
                Intent intent = new Intent(MainActivity.this
                        , FavouritePlaces.class);
                startActivity(intent);

                break;
            case R.id.btn_direction:
                url = getDirectionUrl(latitude, longitude, destLat, destLong);
                dataTransfer = new Object[4];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(destLat, destLong);
                dataTransfer[3] = new LatLng(latitude, longitude);
                GetDirection getDirectionData = new GetDirection();
                getDirectionData.execute(dataTransfer);

                break;


        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location" + "=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + "AIzaSyB62wouz5mEqEkkDZE5G2iRbkfbifeEMbg");

        return googlePlaceUrl.toString();
    }

    private String getDirectionUrl(double latitude, double longitude, double destlatitude, double destlongituide) {
        StringBuilder directionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        directionUrl.append("origin=" + latitude + "," + longitude);
        directionUrl.append("&destination=" + destlatitude + "," + destlongituide);
        directionUrl.append("&key=" + getString(R.string.api_key_places));
        return directionUrl.toString();
    }

    private void getUserLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);
        setHomeMarker();
    }

    private void setHomeMarker() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    latitude = userLocation.latitude;
                    longitude = userLocation.longitude;

                    CameraPosition cameraPosition = CameraPosition.builder()
                            .target(userLocation)
                            .zoom(15)
                            .bearing(0)
                            .tilt(45)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mMap.addMarker(new MarkerOptions().position(userLocation)
                            .title("your location"));
                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add Marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                latLng = marker.getPosition();


                address = marker.getTitle();

                System.out.println(address);
                System.out.println(latLng.latitude + "Longitude" + latLng.longitude);
                addAddress();

                return false;
            }

        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng)
                        .title(latLng.toString())
                        .draggable(true));



                destLat = latLng.latitude;
                destLong = latLng.longitude;


            }






        });
    }

    private boolean checkPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setHomeMarker();
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();

        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //use fusedloaction api to get the current location

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        }
//dhjdhkuhdijdlf
    }

    private void createTable(SQLiteDatabase mDatabse) {

        String sql = "CREATE TABLE IF NOT EXISTS favplaces(" +
                "id INTEGER NOT NULL CONSTRAINT fav_pk PRIMARY KEY AUTOINCREMENT, " +
                "adress VARCHAR(700) NOT NULL, " +
                "lat DOUBLE NOT NULL, " +

                "long DOUBLE NOT NULL);";

        mDatabse.execSQL(sql);

    }

    private void addAddress() {


        String lat = String.valueOf(latLng.latitude);
        String lon = String.valueOf(latLng.longitude);



        //using the Calendar object to get the current time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String joingiDate = simpleDateFormat.format(calendar.getTime());


        String sql = "INSERT INTO favplaces (adress, lat ,long)" + "VALUES(?,?,?)";
        mDatabse.execSQL(sql, new String[]{address, lat, lon});
        Toast.makeText(this, "Place Added", Toast.LENGTH_SHORT).show();


    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
