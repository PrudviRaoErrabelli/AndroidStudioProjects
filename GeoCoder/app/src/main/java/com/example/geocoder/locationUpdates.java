package com.example.geocoder;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;

public class locationUpdates extends Service {


   // LocationRequest locationRequest;
   LocationRequest.Builder  locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationCallback locationCallback  = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            // super.onLocationResult(locationResult);
            if (locationResult == null) {

            }
            for (Location location : locationResult.getLocations()) {
                updateUIValues(location);
            }
        }
    };


    @Override
    public void onCreate() {


        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000*10);
        locationRequest.setIntervalMillis(1000 * 10);
        locationRequest.setWaitForAccurateLocation(true);
        locationRequest.build();
        startLocationUpdates();

       return START_STICKY;

    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest.build(), locationCallback, Looper.getMainLooper());


    }


    private void updateUIValues(Location location) {

        Geocoder geocoder = new Geocoder(getApplicationContext());
        try {
            List addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if(addressList != null && addressList.size() > 0) {

                Address address = (Address) addressList.get(0);
                Log.d("Location_Details",":"+address.getAddressLine(0).toString());
        }
            else{

                }

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}