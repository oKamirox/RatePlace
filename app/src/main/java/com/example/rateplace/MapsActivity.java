package com.example.rateplace;
import android.Manifest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,

        GoogleMap.OnMyLocationClickListener,

        OnMapReadyCallback,

        ActivityCompat.OnRequestPermissionsResultCallback {

    private  Button chatButton;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean mPermissionDenied = false;

    private GoogleMap mMap;

    private LocationManager locationManager;

    public static boolean geolocationEnabled = false;


    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_maps, container, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        BottomNavigationView  bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        chatButton = findViewById(R.id.chat_button);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMessagesActivity();
            }
        });


        getSupportFragmentManager().beginTransaction().replace(R.id.map,

                new SupportMapFragment());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    public void openMessagesActivity(){
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_map:
                            selectedFragment = new Fragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;


                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);

        mMap.setOnMyLocationClickListener(this);

        enableMyLocation();

    }



    /**

     * Enables the My Location layer if the fine location permission has been granted.

     */

    private void enableMyLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

                != PackageManager.PERMISSION_GRANTED) {

            // Permission to access the location is missing.

            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,

                    Manifest.permission.ACCESS_FINE_LOCATION, true);

        } else if (mMap != null) {

            // Access to the location has been granted to the app.

            mMap.setMyLocationEnabled(true);


        }

    }



    @Override

    public boolean onMyLocationButtonClick() {

        return false;

    }



    @Override

    public void onMyLocationClick(@NonNull Location location) {

        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();

    }



    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,

                                           @NonNull int[] grantResults) {

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {

            return;

        }



        if (PermissionUtils.isPermissionGranted(permissions, grantResults,

                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Enable the my location layer if the permission has been granted.

            enableMyLocation();

        } else {

            // Display the missing permission error dialog when the fragments resume.

            mPermissionDenied = true;

        }

    }



    @Override

    protected void onResumeFragments() {

        super.onResumeFragments();

        if (mPermissionDenied) {

            // Permission was not granted, display error dialog.

            showMissingPermissionError();

            mPermissionDenied = false;

        }

    }



    /**

     * Displays a dialog with error message explaining that the location permission is missing.

     */

    private void showMissingPermissionError() {

        PermissionUtils.PermissionDeniedDialog

                .newInstance(true).show(getSupportFragmentManager(), "dialog");

    }



}





