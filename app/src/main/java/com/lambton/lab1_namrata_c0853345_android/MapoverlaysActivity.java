package com.lambton.lab1_namrata_c0853345_android;

import android.Manifest;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapoverlaysActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener, View.OnClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
    MapView mapView;
    GoogleMap googleMap;
    EditText location_address;
    Button add_location;
    String str_locationadd;
    LatLng lat_long_add;
    Toolbar toolbar;
    HashMap<String, Marker> annotationMarkersPolyLine = new HashMap<>();
    HashMap<String, Marker> annotationMarkersPolygon = new HashMap<>();
    // Marker Labels and current markers
    ArrayList<String> markerlabel = new ArrayList<String>(Arrays.asList("A", "B", "C", "D"));
    final int polygon_sides = 4;
    final int POINT_DELETE_DISTANCE_THRESHOLD = 50;
    ArrayList<Marker> current_marker = new ArrayList<Marker>();
    ArrayList<Polyline> current_poly_lines = new ArrayList<Polyline>();
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
        toolbar.setTitle("Map Overlays");
        setSupportActionBar(toolbar);
        locationPermissionRequest();
        add_location.setOnClickListener(this);
        Bundle mapbundle = null;
        if (savedInstanceState != null)
        {
            mapbundle=savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapbundle);
        mapView.getMapAsync(this);
        mapView.setClickable(true);
    }
    private void findId() {
        toolbar = findViewById(R.id.toolbar);
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
                                Toast.makeText(this,"Precise location access granted",Toast.LENGTH_SHORT).show();
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
        this.googleMap = googleMap;
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
        LatLng lat = new LatLng(43.6532, -79.3832);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(lat));
        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnPolygonClickListener(this);
        googleMap.setOnPolylineClickListener(this);

    }

    private String nextmarker(){
        if(markerlabel.size() > 0){
            String currentMarker =  markerlabel.get(0);
            markerlabel.remove(0);
            return currentMarker;
        }else{
            return "";
        }
    }
    private void deleteMarker(){}
    private void sortMarkers(){}
    private void clearallMarkers(){
        markerlabel = new ArrayList<String>(Arrays.asList("A", "B", "C", "D"));
    }
    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng latlong_val = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Log.d("GEOCODE", "Got the location:" + address);
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            latlong_val = new LatLng( (location.getLatitude()),
                    (location.getLongitude()));

            return latlong_val;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        if(annotationMarkersPolyLine.get(polyline.getId()) != null){
            annotationMarkersPolyLine.get(polyline.getId()).remove();
            annotationMarkersPolyLine.remove(polyline.getId());
            return;
        }
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }
        Log.d("map", "Polyline clicked.!");
        List<LatLng> coordiantesPolyLine = polyline.getPoints();
        LatLng polylineStart = coordiantesPolyLine.get(0);
        LatLng polylineEnd = coordiantesPolyLine.get(1);
        float[] results = new float[1];
        Location.distanceBetween(polylineStart.latitude, polylineStart.longitude,
                polylineEnd.latitude, polylineEnd.longitude, results);

        LatLng midPoint = getPolylineCentroid(polyline);
        Marker centerOneMarker = googleMap.addMarker(new MarkerOptions()
                .position(midPoint)
                .icon(makeBitmaptoShowDetails(String.valueOf(results[0]))));
        annotationMarkersPolyLine.put(polyline.getId(), centerOneMarker);
    }
    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {
        //googleMap. clear();
        Log.d("map", "Polygon Clicked.!");
        if(annotationMarkersPolygon.get(polygon.getId()) != null){
            annotationMarkersPolygon.get(polygon.getId()).remove();
            annotationMarkersPolygon.remove(polygon.getId());
            return;
        }
        float totalDistance = 0;
        // find total distance  A - D
        for (Polyline polyline: current_poly_lines) {
            LatLng polylineStart = polyline.getPoints().get(0);
            LatLng polylineEnd = polyline.getPoints().get(1);
            float[] results = new float[1];
            Location.distanceBetween(polylineStart.latitude, polylineStart.longitude,
                    polylineEnd.latitude, polylineEnd.longitude, results);
            totalDistance += results[0];
        }

        Marker centerOneMarker = googleMap.addMarker(new MarkerOptions()
                .position(getPolygonCenterPoint(polygon.getPoints()))
                .icon(makeBitmaptoShowDetails(String.valueOf(totalDistance))));
        annotationMarkersPolygon.put(polygon.getId(), centerOneMarker);
    }

    private LatLng getPolygonCenterPoint(List<LatLng> polygonPointsList){
        LatLng centerLatLng = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0 ; i < polygonPointsList.size() ; i++)
        {
            builder.include(polygonPointsList.get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng =  bounds.getCenter();

        return centerLatLng;
    }

    public LatLng getPolylineCentroid(Polyline p) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0; i < p.getPoints().size(); i++){
            builder.include(p.getPoints().get(i));
        }

        LatLngBounds bounds = builder.build();

        return bounds.getCenter();
    }

    private BitmapDescriptor makeBitmaptoShowDetails(String label){
        LinearLayout distanceMarkerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.marker_layout, null);

        distanceMarkerLayout.setDrawingCacheEnabled(true);
        distanceMarkerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        distanceMarkerLayout.layout(0, 0, distanceMarkerLayout.getMeasuredWidth(), distanceMarkerLayout.getMeasuredHeight());
        distanceMarkerLayout.buildDrawingCache(true);

        TextView positionDistance = distanceMarkerLayout.findViewById(R.id.markertext);

        positionDistance.setText(label);

        Bitmap flagBitmap = Bitmap.createBitmap(distanceMarkerLayout.getDrawingCache());
        distanceMarkerLayout.setDrawingCacheEnabled(false);
        BitmapDescriptor flagBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(flagBitmap);
        return  flagBitmapDescriptor;

    }

    @Override
    public void onClick(View view) {
        str_locationadd = location_address.getText().toString();
        lat_long_add =getLocationFromAddress(str_locationadd);
        Log.d("lat_long_add", String.valueOf(lat_long_add));
        if(lat_long_add!=null)
        {
            Log.d("lat_long_add******",String.valueOf(lat_long_add));
            addmarkertomap(lat_long_add);
        }
        else{
            Toast.makeText(MapoverlaysActivity.this,"Oops.!, Location Not found",Toast.LENGTH_SHORT).show();
        }
    }

    private void addmarkertomap(LatLng lat_long_add) {

        String currentMarker = nextmarker();
        if(currentMarker == ""){
            Log.d("MARKER_LIMIT", "Marker Limit Reached");
            return;
        }
        MarkerOptions markerOption = new MarkerOptions().position(lat_long_add).title(currentMarker);
        Marker marker = googleMap.addMarker(markerOption);
        marker.showInfoWindow();
        current_marker.add(marker);
        if(current_marker.size() > 1){
            //draw polyline
            Log.d("MAP_CLICK", "Adding polyline");
            Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .color(Color.RED)
                    .add(
                            current_marker.get(current_marker.size() - 2).getPosition(),
                            current_marker.get(current_marker.size() - 1).getPosition())
            );
            current_poly_lines.add(polyline);

            if(current_marker.size() > polygon_sides - 1){

                //Adding final poly line connecting all
                Polyline polylineEnf = googleMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .color(Color.RED)
                        .add(
                                current_marker.get(0).getPosition(),
                                current_marker.get(current_marker.size() - 1).getPosition())
                );
                current_poly_lines.add(polyline);


                PolygonOptions opts=new PolygonOptions();

                for (Marker markerX: current_marker) {
                    opts.add(markerX.getPosition());
                }
                Polygon polygon = googleMap.addPolygon(opts.strokeColor(Color.RED).fillColor(Color.parseColor("#4300ff00")));
                polygon.setClickable(true);
    }}}

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        Log.d("mapclick", "Map just click" + latLng);
        addmarkertomap(latLng);
    }
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Log.d("MAP", "Map Long click" + latLng);
        //clear marker
        float[] pointADistance = new float[1];
        float[] pointBDistance = new float[1];
        float[] pointCDistance = new float[1];
        float[] pointEDistance = new float[1];
        Location.distanceBetween(current_marker.get(0).getPosition().latitude, current_marker.get(0).getPosition().longitude,
                latLng.latitude, latLng.longitude, pointADistance);
        Location.distanceBetween(current_marker.get(1).getPosition().latitude, current_marker.get(1).getPosition().longitude,
                latLng.latitude, latLng.longitude, pointBDistance);
        Location.distanceBetween(current_marker.get(2).getPosition().latitude, current_marker.get(2).getPosition().longitude,
                latLng.latitude, latLng.longitude, pointCDistance);
        Location.distanceBetween(current_marker.get(3).getPosition().latitude, current_marker.get(3).getPosition().longitude,
                latLng.latitude, latLng.longitude, pointEDistance);

        if(pointADistance[0] < POINT_DELETE_DISTANCE_THRESHOLD){
            current_marker.get(0).remove();
        }
        if(pointBDistance[0] < POINT_DELETE_DISTANCE_THRESHOLD){
            current_marker.get(1).remove();
        }
        if(pointCDistance[0] < POINT_DELETE_DISTANCE_THRESHOLD){
            current_marker.get(2).remove();
        }
        if(pointEDistance[0] < POINT_DELETE_DISTANCE_THRESHOLD){
            current_marker.get(3).remove();
        }
    }

}
