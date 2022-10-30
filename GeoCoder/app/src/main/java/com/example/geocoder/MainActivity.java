package com.example.geocoder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;


public class MainActivity<locationCallback> extends AppCompatActivity {

    EditText LocationName;
    Button submit,play;
    TextView Addresse,lat,lang;
    int REQUEST_CHECK_SETTINGS = 12;
    int locationRequestCode = 101;

    LocationRequest locationRequest;

    Remainder rmd = new Remainder();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submit = findViewById(R.id.button);
        play = findViewById(R.id.button1);
        LocationName = findViewById(R.id.LocationName);
        Addresse = findViewById(R.id.Address);
        lat = findViewById(R.id.Lat);
        lang = findViewById(R.id.lang);




        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rmd.play();
            }
        });
        play.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                rmd.stop();
                return true;
            }
        });
        submit.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Geocoder geocoder = new Geocoder(MainActivity.this);
                String locName = LocationName.getText().toString();
                try {
                    List addressList = geocoder.getFromLocationName(locName,1);
                    if(addressList != null && addressList.size() > 0) {

                        Address address = (Address) addressList.get(0);
                        //StringBuilder stringBuilder = new StringBuilder();
                        Addresse.setText(String.valueOf(address));
                        lat.setText(String.valueOf(address.getFeatureName()));
                        lang.setText(String.valueOf(address.getLongitude()));

                    }
                    else{
                        Toast.makeText(MainActivity.this,"Button working&no address",Toast.LENGTH_SHORT).show();
                    }

                }catch (IOException e){
                   e.printStackTrace();
                }
            }
        });

        submit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

               Intent intent = new Intent(getApplicationContext(),locationUpdates.class);
               if(!isMyServiceRunning(locationUpdates.class)){
                  startService(intent);
               }else {
                   Toast.makeText(MainActivity.this,"Service is already running",Toast.LENGTH_SHORT).show();
                   checkSettingsAndStartlocatonupdates();

               }

              return true;
            }
        });
    }

    private void checkSettingsAndStartlocatonupdates() {
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder();
        locationSettingsRequestBuilder.addLocationRequest(locationRequest);
        locationSettingsRequestBuilder.setAlwaysShow(true);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

            }

        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                    try {
                        resolvableApiException.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });

    }
    private void askPermissions() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},locationRequestCode);

            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, locationRequestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == locationRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                // checkSettingsAndStartlocatonupdates();
            } else {
askPermissions();
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        askPermissions();
        super.onStart();
    }
}