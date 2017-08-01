package com.pribo.vice.opensesame05;

import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.R.attr.manageSpaceActivity;
import static android.R.attr.radius;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    LatLng latLng;
    FusedLocationProviderClient client;
    Boolean ifClicked = false;
    private Marker mCurrentMarker;
    private ArrayList<Marker> mMarkerArrayList = new ArrayList<>();
    private String RADIUS_CIRCLE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = new SupportMapFragment();

        getSupportFragmentManager().beginTransaction().
                replace(R.id.frame1, mapFragment).
                replace(R.id.frame2, new LocationFragment()).
                commit();

        //tell me when the map is loaded
        mapFragment.getMapAsync(this);
        client = LocationServices.getFusedLocationProviderClient(this);

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //addMarker(map);
        setMyLocation(map);
        setUpMap(map);

    }


    private void setUpMap(final GoogleMap map) {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                Toast.makeText(MapsActivity.this, latLng.toString(), Toast.LENGTH_SHORT).show();

                final CircleOptions circleOptions = new CircleOptions()
                        .center(latLng)
                        .radius(200)
                        .strokeColor(Color.GREEN)
                        .strokeWidth(8).clickable(true);

                Circle mCircle;

                mCircle = map.addCircle(circleOptions);

                final Circle finalMCircle = mCircle;

                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true));


                map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                        finalMCircle.setCenter(marker.getPosition());
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {

                    }
                });


            }
        });

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                Toast.makeText(MapsActivity.this, latLng.toString(), Toast.LENGTH_SHORT).show();

                map.clear();
            }
        });

    }


    private void setMyLocation(GoogleMap map) {

        if (checkLocationPermission())
            map.setMyLocationEnabled(true);
    }


    private void addMarker(GoogleMap map) {

        if (!checkLocationPermission()) return;

        Task<Location> task = client.getLastLocation();
        Location location = task.getResult();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


        map.addMarker(new MarkerOptions().position(latLng));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1
            );

            return false;
        }
        return true;
    }


}
