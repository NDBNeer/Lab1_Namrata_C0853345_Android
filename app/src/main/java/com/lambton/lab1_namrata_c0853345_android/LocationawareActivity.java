package com.lambton.lab1_namrata_c0853345_android;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationawareActivity extends AppCompatActivity implements LocationListener {
    TextView val_latitude,val_longitude,val_address;
    LocationManager locationManager;
    String CLASS_TAG = "LOCATION_AWARE";
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationaware_layout);
        findvidebyId();
        mContext=LocationawareActivity.this;
        startLocationUpdates();
    }

    // check permissions

    private void startLocationUpdates(){
        Log.d(CLASS_TAG, "Running Location update init");
        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(CLASS_TAG, "Permission Not Given");


        }else{
            locationManager = (LocationManager)  mContext.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        }

    }

    private void findvidebyId() {
        val_latitude=findViewById(R.id.val_latitude);
        val_longitude=findViewById(R.id.val_longitude);
        val_address=findViewById(R.id.val_address);
    }
    private void updateFields(String valLatitude, String valLongitude, String valAddress){
        val_longitude.setText(valLongitude);
        val_latitude.setText(valLatitude);
        val_address.setText(valAddress);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);

    }
    @Override
    public void onLocationChanged(Location location) {
        List<Address> addresses = null;
        //remove location callback:
        Log.d(CLASS_TAG, " Latitude:" + location.getLatitude());
       // Toast.makeText(this, " Latitude:" + location.getLatitude(),Toast.LENGTH_SHORT).show();
        Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        try {
            addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateFields(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), addresses.get(0).getAddressLine(0));
    }
}
