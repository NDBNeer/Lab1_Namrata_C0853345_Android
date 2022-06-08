package com.lambton.lab1_namrata_c0853345_android;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button location_aware_btn,map_overlays_btn;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map_overlays_btn=findViewById(R.id.map_overlays_btn);
        location_aware_btn=findViewById(R.id.location_aware_btn);
        location_aware_btn.setOnClickListener(this);
        map_overlays_btn.setOnClickListener(this);
        locationPermissionRequest();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.map_overlays_btn:
                startActivity(new Intent(MainActivity.this,MapoverlaysActivity.class));
            break;
            case R.id.location_aware_btn:
                startActivity(new Intent(MainActivity.this,LocationawareActivity.class));
                break;
        }
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
}
