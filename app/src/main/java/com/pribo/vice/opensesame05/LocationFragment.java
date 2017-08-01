package com.pribo.vice.opensesame05;

import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import butterknife.Unbinder;

public class LocationFragment extends Fragment {


    Unbinder unbinder;
    FusedLocationProviderClient client;
    @BindView(R.id.etRadius)
    EditText etRadius;
    @BindView(R.id.etPhoneNum)
    EditText etetPhoneNum;


    public LocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);
        unbinder = ButterKnife.bind(this, view);

        client = new FusedLocationProviderClient(getContext());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        client.removeLocationUpdates(callback);
    }


    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1
            );

            return false;
        }
        return true;
    }


    private void getLocationUpdates() {
        LocationRequest request = new LocationRequest();
       /* request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //GPS
        request.setPriority(LocationRequest.PRIORITY_LOW_POWER); //Cellular
        request.setPriority(LocationRequest.PRIORITY_NO_POWER); //Last Known Location*/
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); //GPS + Cellular

        request.setInterval(5 * 1000); //in Miliseconds
        request.setFastestInterval(5000);

        //request.setNumUpdates();
        //request.setExpirationDuration(60 * 60 * 1000); //stop after one hour
        //request.setSmallestDisplacement();

        if (!checkLocationPermission()) return;
        client.requestLocationUpdates(request, callback /*Callback*/, null /*Looper*/);


    }

    LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();


            String latitude = String.format(Locale.getDefault(), "%e ",
                    location.getLatitude());

            String longitude = String.format(Locale.getDefault(), "%e ", location.getLongitude());

            //tvLatLong.setText(latitude + longitude);

            //getAdress(location);
        }
    };

    //GPS -> Address
    //Requires Internet permission
    private void getAdress(Location l) {
        //1) Initiate a Geocoder
        Geocoder geocoder = new Geocoder(getContext());

        //2) From Location
        try {
            List<Address> addresses = geocoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
            if (addresses.size() > 0) return;


            Address address = addresses.get(0);

            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                String line = address.getAddressLine(i);
                Toast.makeText(getContext(), line, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) { //Cant connect -> Internet?
            e.printStackTrace();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            getLocationUpdates();
        boolean shouldShowRequestPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);


    }

}
