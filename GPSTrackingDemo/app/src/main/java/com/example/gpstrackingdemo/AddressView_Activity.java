package com.example.gpstrackingdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class AddressView_Activity extends AppCompatActivity {
    TextView Area,City,State,Pincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_view);

        Area = findViewById(R.id.Area);
        City = findViewById(R.id.City);
        State = findViewById(R.id.State);
        Pincode =findViewById(R.id.Pincode);

        double latitude = getIntent().getDoubleExtra("keylat",0.0);
        double longtitude = getIntent().getDoubleExtra("keylong",0.0);

        getAddress(latitude,longtitude);
    }

    private void getAddress(double latitude,double longtitude) {

        String address ="";
        Geocoder geocoder = new Geocoder(AddressView_Activity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,longtitude,1);

            if(addresses!=null) {
               // Address returnAddress = addresses.get(0);
                //StringBuilder stringBuilderReturnAddress = new StringBuilder();
               // for (int i = 0; i <= returnAddress.getMaxAddressLineIndex(); i++) {
                //    stringBuilderReturnAddress.append(returnAddress.getAddressLine(i)).append("ppp");
               // }
               // address = stringBuilderReturnAddress.toString();
            Area.setText(addresses.get(0).getAddressLine(0));
                Toast.makeText(this,address,Toast.LENGTH_SHORT).show();
              /* State.setText(returnAddress.getSubAdminArea()+" ,"+returnAddress.getAdminArea());
               Area.setText(returnAddress.getFeatureName()+","+returnAddress.getLocale());
               City.setText(returnAddress.getSubLocality()+
                       ","+returnAddress.getLocality());
               Pincode.setText(returnAddress.getPostalCode()+","+returnAddress.getCountryName());
            */
            }
            else{
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

            Toast.makeText(this,e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }


    }
}