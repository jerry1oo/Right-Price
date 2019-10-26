package com.example.rightprice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class Map extends AppCompatActivity implements OnMapReadyCallback {
    //Set up RequestQueue
    Cache cache;
    Network network;
    RequestQueue requestQueue;

    private GoogleMap mMap;
    private ImageButton settingsButton;
    private ImageButton filterButton;
    private ToggleButton birdFilter;
    private ToggleButton limeFilter;
    private ToggleButton spinFilter;

    //slider initialize maxPrice
    private ToggleButton bikeFilter;
    private ToggleButton scooFilter;
    private LinearLayout servicesLayer;
    private LinearLayout filterOptionsLayer;
    private Button logoutButton;
    private Button resetButton;
    private FirebaseAuth mAuth;
    private Button gpsButton;
    private Location currentLocation;
    private TextView startPrice;
    private TextView startBat;
    private TextView minutePrice;


    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //marker implementation
    private ArrayList<Vehicle> vehicleArrayList;
    private ArrayList<Marker> markerArrayList;
    //popup window implementation
    private TextView serviceLabel;
    private TextView batteryValue;
    private TextView startValue;
    private TextView minuteValue;
    private Button startButton;
    private Button DIRButtom;
    private Button closeButton;
    private LinearLayout popupLayer;
    private Vehicle vehicle;

    private boolean birdOn, limeOn, spinOn, bikeOn, scooterOn;

    private boolean mapReady = false;

    /*
     * This method
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_map);

        getLocationPermission();

        mAuth = getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final String userUID = user.getUid();
        final DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("Users").document(userUID);

        settingsButton = (ImageButton) findViewById(R.id.settings_button);
        filterButton = (ImageButton) findViewById(R.id.filter_button);


        //start of button initialization

        birdFilter = findViewById(R.id.vehicle_bird_toggle);
        limeFilter = findViewById(R.id.vehicle_lime_toggle);
        spinFilter = findViewById(R.id.vehicle_spin_toggle);
        bikeFilter = findViewById(R.id.service_bike_toggle);
        scooFilter = findViewById(R.id.service_scooter_toggle);

        DocumentReference filtersRef = db.collection("Users").document(user.getUid());
        filtersRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    try {
                        if (documentSnapshot.getBoolean("birdFilter"))
                            birdFilter.toggle();
                    } catch (Exception e){
                        Toast.makeText(Map.this, "Please create a new account." ,
                                Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();

                        startActivity(new Intent(Map.this, MainActivity.class));
                        finish();
                    }

                    try {
                        if (documentSnapshot.getBoolean("limeFilter"))
                            limeFilter.toggle();
                    } catch (Exception e){
                        Toast.makeText(Map.this, "Please create a new account." ,
                                Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();

                        startActivity(new Intent(Map.this, MainActivity.class));
                        finish();
                    }

                    try {
                        if (documentSnapshot.getBoolean("spinFilter"))
                            spinFilter.toggle();
                    } catch (Exception e){
                        Toast.makeText(Map.this, "Please create a new account." ,
                                Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();

                        startActivity(new Intent(Map.this, MainActivity.class));
                        finish();
                    }

                    try {
                        if (documentSnapshot.getBoolean("bikeFilter"))
                            bikeFilter.toggle();
                    } catch (Exception e){
                        Toast.makeText(Map.this, "Please create a new account." ,
                                Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();

                        startActivity(new Intent(Map.this, MainActivity.class));
                        finish();
                    }

                    try {
                        if (documentSnapshot.getBoolean("scooterFilter"))
                            scooFilter.toggle();
                    } catch (Exception e){
                        Toast.makeText(Map.this, "Please create a new account." ,
                                Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();

                        startActivity(new Intent(Map.this, MainActivity.class));
                        finish();
                    }

                } else {
                    System.err.println("No such document!");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("getCurrSettings", "getCurrSettings:failure", e);
                Toast.makeText(Map.this, "failed to get settings.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        /*
         * This method will toggle the bird filter boolean and then call the filter function.
         */
        birdFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filter(markerArrayList);
            }
        });

        limeFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filter(markerArrayList);
            }
        });

        spinFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filter(markerArrayList);
            }
        });

        //maxPrice find

        bikeFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filter(markerArrayList);
            }
        });

        scooFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // The toggle is enabled
                filter(markerArrayList);
            }
        });

        logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // implement logging out.
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Map.this, MainActivity.class));
                finish();
            }
        });

        resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent forgotPassView = new Intent(Map.this, ForgotPassActivity.class);
                startActivity(forgotPassView);
            }
        });

        //some more functions for later

        //HANDLE SERVICE FILTERS
        //filter for bird
        birdFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //handle bird filter toggle
                HashMap<String, Object> Bird = new HashMap<>();
                if(birdFilter.isChecked()) {
                    Bird.put("birdFilter", true);
                } else {
                    Bird.put("birdFilter", false);
                }
                userDocRef.update(Bird);
            }
        });
        //filter for lime
        limeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //handle lime filter toggle
                HashMap<String, Object> Lime = new HashMap<>();
                if(limeFilter.isChecked()) {
                    Lime.put("limeFilter", true);
                } else {
                    Lime.put("limeFilter", false);
                }
                userDocRef.update(Lime);
            }
        });
        //filter for spin
        spinFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //handle spin filter toggle
                HashMap<String, Object> Spin = new HashMap<>();
                if(spinFilter.isChecked()) {
                    Spin.put("spinFilter", true);
                } else {
                    Spin.put("spinFilter", false);
                }
                userDocRef.update(Spin);
            }
        });
        //alter maxPrice variable

        //handle vehicle filters
        //filter for bike
        bikeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //handle spin filter toggle
                HashMap<String, Object> Bike = new HashMap<>();
                if(bikeFilter.isChecked()) {
                    Bike.put("bikeFilter", true);
                } else {
                    Bike.put("bikeFilter", false);
                }
                userDocRef.update(Bike);
            }
        });
        //filter for scooter
        scooFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //handle spin filter toggle
                HashMap<String, Object> Scooter = new HashMap<>();
                if(scooFilter.isChecked()) {
                    Scooter.put("scooterFilter", true);
                } else {
                    Scooter.put("scooterFilter", false);
                }
                userDocRef.update(Scooter);
            }
        });


        servicesLayer = (LinearLayout) findViewById(R.id.services_layer);
        servicesLayer.setVisibility(View.INVISIBLE);

        filterOptionsLayer = (LinearLayout) findViewById(R.id.filter_options_layer);
        filterOptionsLayer.setVisibility(View.INVISIBLE);

        // Shows settings when pressing the Settings Button
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (servicesLayer.getVisibility()==View.VISIBLE) {
                    servicesLayer.setVisibility(View.INVISIBLE);
                } else {
                    servicesLayer.setVisibility(View.VISIBLE);
                    filterOptionsLayer.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Handle services filter
        // Shows filter menu when pressing the filter Button
        filterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (filterOptionsLayer.getVisibility() == View.VISIBLE) {
                    filterOptionsLayer.setVisibility(View.INVISIBLE);
                } else {
                    filterOptionsLayer.setVisibility(View.VISIBLE);
                    servicesLayer.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Added initializations for Adding pins
        vehicleArrayList = new ArrayList<Vehicle>();
        markerArrayList = new ArrayList<Marker>();
        // Added Stuff for popup window
        batteryValue = findViewById(R.id.popup_battery_value);
        startValue = findViewById(R.id.popup_start_value);
        startButton = findViewById(R.id.start_button);
        popupLayer = findViewById(R.id.popup_layer);
        popupLayer.setVisibility(View.INVISIBLE);
        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //implement starting the bird
                if (vehicle.getVendor().equals("bird"))
                {
                    try {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("co.bird.android");
                        startActivity(launchIntent);
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=co.bird.android")));
                    }
                }
                // implement starting the lime
                if (vehicle.getVendor().equals("lime"))
                {
                    try {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.limebike");
                        startActivity(launchIntent);
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.limebike")));
                    }
                }
                // implement starting the spin
                if (vehicle.getVendor().equals("spin"))
                {
                    try {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("pm.spin");
                        startActivity(launchIntent);
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=pm.spin")));
                    }
                }
            }
        });
        closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                popupLayer.setVisibility(View.INVISIBLE);
                //Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                //        Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + vehicle.getLat() + "," + vehicle.getLng()+ "&travelmode=walking"));
                //startActivity(intent);
            }
        });

        DIRButtom = findViewById(R.id.Dir_button);
        DIRButtom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + vehicle.getLat() + "," + vehicle.getLng()+ "&travelmode=walking"));
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onResume(){
        super.onResume();
        if(mapReady){
            Intent reCreate = new Intent(Map.this, Map.class);
            startActivity(reCreate);
            finish();
        }

    }

    /*
     * This method is called when the GoogleMap is ready to be presented. It will call the getDeviceLocation
     * which will center the camera on the device's location.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        mMap.setPadding(0, 160, 0, 0);
        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            //mMap.getUiSettings().setMyLocationButtonEnabled(false);

        }

        startPrice = findViewById(R.id.popup_start_value);
        startBat = findViewById(R.id.popup_battery_value);

        final ImageView serviceImg = findViewById(R.id.service_img);

        /*
         * This method creates a listener for a marker click. The marker click will get the vehicle
         * that is associated with the marker. It will also open a popup window and populate its
         * contents with the marker's vehicle information.
         */
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.getTag() != null ){
                    servicesLayer.setVisibility(View.INVISIBLE);
                    filterOptionsLayer.setVisibility(View.INVISIBLE);
                    // Populates popup layer
                    vehicle = (Vehicle)marker.getTag();
                    String serviceName = (vehicle.getVendor().substring(0,1).toUpperCase()+vehicle.getVendor().substring(1));
                    System.out.println(serviceName);
                    if(serviceName.equals("Lime")){
                        serviceImg.setImageResource(R.drawable.lime_logo);
                    } else if (serviceName.equals("Bird")){
                        serviceImg.setImageResource(R.drawable.bird_logo);
                    } else {
                        serviceImg.setImageResource(R.drawable.spin_logo);
                    }



                    popupLayer.setVisibility(View.VISIBLE);
                    startPrice.setText("$" + vehicle.getPrice().substring(1));
                    if(vehicle.getBattery() == -1){
                        startBat.setVisibility(View.INVISIBLE);
                    } else {
                        startBat.setVisibility(View.VISIBLE);
                        startBat.setText("Battery: " + String.valueOf(vehicle.getBattery()) + "%");
                    }
                }
                return true; //suppresses default behavior. false uses default.
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng l){
                popupLayer.setVisibility(View.INVISIBLE);
                servicesLayer.setVisibility(View.INVISIBLE);
                filterOptionsLayer.setVisibility(View.INVISIBLE);
            }

        });


        //Set up RequestQueue
        cache = new DiskBasedCache(this.getCacheDir(), 1024*1024);
        network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache,network);
        requestQueue.start();

        /**
         * ITS LIME TIME...
         * Here we will initialize lime and set up all the requets
         */

        final Lime lime = new Lime();
        final Response.Listener<JSONObject> onVehicleResLime = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                System.out.println("Limes got.. success");
                System.out.println(response.toString());
                System.out.println("Its LIMETIME $$Scooooooot..$$");

                try {
                    lime.generateVehicles(response);
                    loadVehiclePins(mMap,(ArrayList<Vehicle>)lime.getVehicles(),markerArrayList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //If the init goes poorly for lime, this is the callback
        Response.ErrorListener onInitErrLime = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //bad request or something
                System.out.println("Error Request when constructing Lime()");
                lime.generateVehicleReq(currentLocation,20,onVehicleResLime);
                requestQueue.add(lime.getVehicleReq());

            }
        };


        //response for lime initialization
        Response.Listener<JSONObject> onInitResLime = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("GET RECIEVED from Lime");
                System.out.println(response.toString());
            }
        };

        final Spin spin = new Spin();
        //Spin vehicle response listener
        final Response.Listener<JSONObject> onVehicleResSpin = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                System.out.println("GET RECIEVED FROM SPIN");

                lime.generateVehicleReq(currentLocation,20,onVehicleResLime);
                requestQueue.add(lime.getVehicleReq());
                System.out.println(response.toString());
                try {
                    spin.generateVehicles(response);
                    loadVehiclePins(mMap,(ArrayList<Vehicle>)spin.getVehicles(),markerArrayList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        /**
         * SPIN Setup
         */

        //Callback for Init request for spin
        Response.Listener<JSONObject> onInitResSpin = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("Request success with SPIN");
                if(response.has("jwt")){
                    try {
                        //set token for spin
                        spin.setToken("Bearer " + response.getString("jwt"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //set spin request
                spin.generateVehicleReq(currentLocation,onVehicleResSpin);
                requestQueue.add(spin.getVehicleReq());
                System.out.println(response.toString());

            }
        };


        try {
            spin.generateInitReq(onInitResSpin);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestQueue.add(spin.getInitReq());

        /**
         * BIRD SETUP
         */
        final Bird bird = new Bird();
        //response listener for bird vehicle request
        final Response.Listener<JSONObject> onVehicleResBird = new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                System.out.println("GET RECIEVED FROM BIRD");
                System.out.println(response.toString());
                try {
                    bird.generateVehicles(response);
                    loadVehiclePins(mMap,(ArrayList<Vehicle>)bird.getVehicles(),markerArrayList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        //Response listener for Initialiation request for bird
        Response.Listener<JSONObject> onInitResBird = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response.has("token")){
                    try {
                        bird.setToken("Bird "+response.getString("token"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(response.has("id")){
                    try {
                        bird.setId(response.getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    bird.generateVehicleReq(currentLocation, 1000,onVehicleResBird);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                requestQueue.add(bird.getVehicleReq());
                System.out.println(response.toString());

            }
        };


        try {
            bird.generateInitReq(onInitResBird);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestQueue.add(bird.getInitReq());

        mapReady = true;

    }

    /*
     * This method gets the current location of the device. It will then move the camera to the
     * location of the device.
     */
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(Map.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    /*
     * This method moves the camera to the position that is given. It takes a LatLng which is a
     * latitude and a longitude that corresponds to a location. It also takes a float zoom that
     * tells you how much to zoom the camera in.
     */
    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /*
     * This method initializes the map and puts it on the activity.
     */
    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(Map.this);
    }

    /*
     *
     */
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "getLocationPermission: granted 1");
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                Log.d(TAG, "getLocationPermission: granted 2");
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
                Log.d(TAG, "getLocationPermission: failed 2");

            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
            Log.d(TAG, "getLocationPermission: failed 1");

        }
    }

    /*
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed " + i + " " + grantResults[i] + " " + PackageManager.PERMISSION_GRANTED);
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }

    }

    /*
     * This method loads the marker pins and puts them on the map and then adds it to the
     * markerArrayList. It takes a GoogleMap to put the markers on, an ArrayList<Vehicle> that
     * holds all of the vehicles to attach to a marker, and a ArrayList<Marker> that will have all
     * of the markers that are put on the map.
     */
    public void loadVehiclePins(GoogleMap googleMap, ArrayList<Vehicle> vehicleArrayList, ArrayList<Marker> markerArrayList){
        for(int i = 0; i < vehicleArrayList.size(); i++){
            Vehicle vehicle = vehicleArrayList.get(i);
            LatLng pos = new LatLng( vehicle.getLat(), vehicle.getLng());
            String vendor = vehicle.getVendor();
            String icon;
            switch(vendor) {
                case "bird":
                    icon = "bird_scooter";
                    break;
                case "spin":
                    if(vehicle.getType().equals("bicycle")) {
                        icon = "spin_bike";
                    } else {
                        icon = "spin_scooter";
                    }
                    break;
                case "lime":
                    icon = "lime_scooter";
                    break;
                default:
                    icon = "logo";
            }
            Marker marker = googleMap.addMarker(new MarkerOptions().position(pos)
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(icon,100,100))));
            marker.setTag(vehicle); //adds vehicle to the marker.
            markerArrayList.add(marker);
        }
        filter(markerArrayList);
    }

    /*
     * This method is called to resize the icons of the scooters and bikes. It takes the icon name
     * which will be referenced to find in the drawable resource folder. It also takes a width and
     * height to resize the icon to.
     */
    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    /*
     * This method runs every time a filter button is tapped. It will check which options are checked
     * when filtering and decides which vehicles are shown on the map. It takes a ArrayList<Marker>
     * that holds all of the markers and then decides which markers to show and not.
     */
    public void filter(ArrayList<Marker> markerArrayList){
        birdOn = birdFilter.isChecked();
        limeOn = limeFilter.isChecked();
        spinOn = spinFilter.isChecked();

        scooterOn = scooFilter.isChecked();
        bikeOn = bikeFilter.isChecked();

        if(!birdOn && !spinOn && !limeOn) {
            birdOn = true;
            spinOn = true;
            limeOn = true;
        }

        if(!scooterOn && !bikeOn) {
            scooterOn = true;
            bikeOn = true;
        }

        for(int i = 0; i < markerArrayList.size(); i++) {
            Marker marker = markerArrayList.get(i);
            Vehicle vehicle = (Vehicle) marker.getTag();
            if (vehicle.getVendor().equals("bird")) {
                if (vehicle.getType().equals("bicycle")) {
                    marker.setVisible(bikeOn && birdOn);
                } else if (vehicle.getType().equals("scooter")) {
                    marker.setVisible(scooterOn && birdOn);
                }
            } else if (vehicle.getVendor().equals("spin")) {
                if (vehicle.getType().equals("bicycle")) {
                    marker.setVisible(bikeOn && spinOn);
                } else if (vehicle.getType().equals("scooter")) {
                    marker.setVisible(scooterOn && spinOn);
                }
            } else if (vehicle.getVendor().equals("lime")) {
                if (vehicle.getType().equals("bicycle")) {
                    marker.setVisible(bikeOn && limeOn);
                } else if (vehicle.getType().equals("scooter")) {
                    marker.setVisible(scooterOn && limeOn);
                }
            }

        }
    }

}
