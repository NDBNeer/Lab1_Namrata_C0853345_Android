package com.lambton.lab1_namrata_c0853345_android;

import android.Manifest;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapoverlaysActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener, View.OnClickListener {
    MapView mapView;
    GoogleMap googleMap;
    EditText location_address;
    Button add_location;
    ArrayList<LatLng> cityCoordinates = new ArrayList<LatLng>();
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    //for poliline
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapoverlays_layout);
        findId();
        locationPermissionRequest();
        add_location.setOnClickListener(this);
        Bundle mapbundle = null;
        if (savedInstanceState != null)
        {
            mapbundle=savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapbundle);
        mapView.getMapAsync(this);

    }
    private void findId() {
        mapView=findViewById(R.id.mapView);
        location_address=findViewById(R.id.location_address);
        add_location=findViewById(R.id.add_location);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void locationPermissionRequest(){
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                Toast.makeText(this," Precise location access granted",Toast.LENGTH_SHORT).show();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                Toast.makeText(this,"Only approximate location access granted",Toast.LENGTH_SHORT).show();
                            } else {
                                // No location access granted.
                                Toast.makeText(this,"No location access granted",Toast.LENGTH_SHORT).show();
                            }
                        }
                );
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
        // to show information on marker click
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap = googleMap;
        List<Address> addresses = null;
        googleMap.setMinZoomPreference(12);
        // UI settings of map
        googleMap.setIndoorEnabled(true);
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        LatLng ny = new LatLng(43.6532, -79.3832);
        //adding marker to the map
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(ny);
        googleMap.addMarker(markerOptions);
        Geocoder geocoder = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        Geocoder.isPresent();
        try {
            addresses = geocoder.getFromLocation(43.6532, -79.3832, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng address = getLocationFromAddress(43.6532, -79.3832);
        googleMap.addMarker(new MarkerOptions().position(address).title(addresses.get(0).getAddressLine(0))).showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ny));

        GoogleMap finalGoogleMap = googleMap;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                Log.d("OVERLAY", "Clicked at latitude:" + latLng.latitude + " longitude:" + latLng.longitude);
                cityCoordinates.add(latLng);
            }
        });
        cityCoordinates.add(new LatLng(43.72794780541092, -79.4320173561573));
        cityCoordinates.add(new LatLng(43.733723496612285, -79.34824928641319));
        cityCoordinates.add(new LatLng(43.67470019767365, -79.33726295828819));
        cityCoordinates.add(new LatLng(43.613089684481054, -79.39044613391161));
        cityCoordinates.add(new LatLng(43.6573606926499, -79.43788502365351));
        //draw polylines between above points
        for(int i=1; i < cityCoordinates.size(); i++){
            Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .color(Color.parseColor("Red"))
                    .add(
                            cityCoordinates.get(i -1),
                            cityCoordinates.get(i))
            );
        }
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .color(Color.RED)
                .add(
                        cityCoordinates.get(0),
                        cityCoordinates.get(cityCoordinates.size()-1))
        );
        Polygon polygon1 = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(cityCoordinates.get(0),
                        cityCoordinates.get(1),
                        cityCoordinates.get(2),
                        cityCoordinates.get(3),
                        cityCoordinates.get(4)
                )
                .fillColor(Color.parseColor("#ff00ff00"))

        );

        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {
                List<LatLng> polyLinePoints = polyline.getPoints();
                float[] results = new float[1];
                Location.distanceBetween(polyLinePoints.get(0).latitude, polyLinePoints.get(0).longitude,
                        polyLinePoints.get(1).latitude, polyLinePoints.get(1).longitude, results);
                Log.d("POLYGON_CLICK", "Calculate Distance between two points of polyline: " + results);

            }
        });

        googleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(@NonNull Polygon polygon) {
                List<LatLng> polygonPoints = polygon.getPoints();
                float[] results = new float[1];
                Log.d("POLYGON_CLICK", "Calculate Distance between all points");
            }
        });
    }

    public LatLng getLocationFromAddress(Double latitude,Double longitude)
    {
        LatLng p1 = null;
        try
        {
            p1 = new LatLng(latitude, longitude);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;
    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {
    }

    @Override
    public void onClick(View view) {

    }
}
