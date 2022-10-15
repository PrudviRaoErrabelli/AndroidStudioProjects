package com.example.gpstrackingdemo;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.audiofx.EnvironmentalReverb;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    public static final int DEFAULT_UPDATE_INTERVAL = 4;
    public static final int FAST_UPDATE_INTERVAL = 2;
    private static final int locationRequestCode = 99;
    private static final int REQUEST_CHECK_SETTINGS = 1001;


    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    Switch sw_gps, sw_locationupdates;
    Button fab,floatid;

    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            //super.onLocationResult(locationResult);
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                updateUIValues(location);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Activity", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //give each UI variable a value

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationupdates = findViewById(R.id.sw_locationupdates);
        fab =findViewById(R.id.fab);
        floatid = findViewById(R.id.fS);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");

                } else {
                    locationRequest.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + Wifi");

                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                double longtitude = Double.parseDouble(tv_lon.getText().toString());
                double latitude = Double.parseDouble(tv_lat.getText().toString());
                tv_address.setText(String.valueOf(getAddress(latitude,longtitude)));

                notification();

            }
        });

             fab.setOnLongClickListener(new View.OnLongClickListener() {

                 @Override
                 public boolean onLongClick(View v) {
                     double longtitude = Double.parseDouble(tv_lon.getText().toString());
                     double latitude = Double.parseDouble(tv_lat.getText().toString());

                    /*Intent intent = new Intent(MainActivity.this,AddressView_Activity.class);
                     intent.putExtra("keylat",latitude);
                     intent.putExtra("keylong",longtitude);
                     startActivity(intent);*/
                Intent intent = new Intent(MainActivity.this,MyBackgroundServices.class);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    }
                else {
                        startService(intent);
                    }
                     return true;
                 }
             });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationupdates.isChecked()) {
                    startLocationUpdates();
                    tv_updates.setText("On");
                }else {
                    stopLocationUpdates();
                    tv_updates.setText("Off");
                }
                }
        });


        floatid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                       startActivity(intent);
                        try {
                            Thread.sleep(5000*10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                Intent intent = new Intent(MainActivity.this,FloatingViewService.class);
                startService(intent);

            }
        });
    }//end onCreate method





    private void notification() {
        createNotificationChanenel();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);

        Intent intent1 = new Intent(MainActivity.this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,0,intent1,PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this,"ChannelId1");
        notification.setContentText("Download in progress")
                      .setSmallIcon(R.drawable.ic_baseline_add_location_24)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                      .setPriority(NotificationCompat.PRIORITY_LOW);
        /*int PROGRESS_MAX = 100;
        final int PROGRESS_CURRENT = 0;
        notification.setProgress(PROGRESS_MAX,PROGRESS_CURRENT,false);
        notificationManager.notify(01,notification.build());*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <=105 ; i+=5) {

                    notification.setProgress(100, i, false);
                    notificationManager.notify(01, notification.build());
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                    notification.setProgress(0,0,true);
                    notificationManager.notify(01,notification.build());
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                notification.setProgress(0,0,false)
                        .setContentText("Download Completed")
                        .setOngoing(false);
                notificationManager.notify(01,notification.build());
            }
        }).start();

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
                startLocationUpdates();
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

    private void updateUIValues(Location location) {
        //update all of the text view objects with new location.
        //tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()) {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        } else {
            tv_altitude.setText("Not available");
        }
        if (location.hasSpeed()) {
            tv_speed.setText(String.valueOf(location.getSpeed()));
        } else {
            tv_speed.setText("Not available");
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private String getAddress(double Latitude,double Longtitude){
        String address ="";
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(Latitude,Longtitude,1);

            if(addresses!=null) {
                Address returnAddress = addresses.get(0);
                StringBuilder stringBuilderReturnAddress = new StringBuilder();

                for (int i = 0; i <= returnAddress.getMaxAddressLineIndex(); i++) {
                    stringBuilderReturnAddress.append(returnAddress.getAddressLine(i)).append("\n");
                }
                   address = stringBuilderReturnAddress.toString();
            }
            else{
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

            Toast.makeText(this,e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }

    @Override
    protected void onStart() {
        Log.e("Activity","onStart()");
        super.onStart();
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
        checkSettingsAndStartlocatonupdates();
        }
        else{
            askPermissions();
        }
    }


    private void askPermissions() {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {

        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},locationRequestCode);

        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, locationRequestCode);
        }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == locationRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                checkSettingsAndStartlocatonupdates();
            } else {

            }
        }
    }

    @Override
    protected void onResume() {
        Log.e("Activity","onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.e("Activity","onPause()");
        super.onPause();
        stopLocationUpdates();

    }

    @Override
    protected void onStop() {
        Log.e("Activity","onStop()");
        super.onStop();
stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        Log.e("Activity","onDestroy()");
        super.onDestroy();

    }

    private void createNotificationChanenel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "ChannelId1","Foregroud Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);

        }
    }
}