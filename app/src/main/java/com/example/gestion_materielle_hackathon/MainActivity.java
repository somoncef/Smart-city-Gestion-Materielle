package com.example.gestion_materielle_hackathon;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    Fragment homeFragment = new Home();
    Fragment supervisionFragment = new Supervision();
    Fragment classificationFragment = new Classification();
    Fragment actionFragment = new Action();

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    askNotificationPermission();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        handleIntent(getIntent());


        // Add this line to launch the Home fragment at first launch
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.iHome) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
                return true;
            } else if (item.getItemId() == R.id.iFriends) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, supervisionFragment).commit();
                return true;
            }else if (item.getItemId() == R.id.iGroup) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, classificationFragment).commit();
                return true;
            }else if (item.getItemId() == R.id.iProfile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, actionFragment).commit();
                return true;
            }

            return false;
        });
    }
    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("targetActivity")) {
            String targetActivity = intent.getStringExtra("targetActivity");
            String zoneName = intent.getStringExtra("zone_name");
            if ("SecondActivity".equals(targetActivity)) {
                Intent activityIntent = new Intent(this, AssignementConfirmation.class);
                activityIntent.putExtra("zone_name", zoneName);
                startActivity(activityIntent);
                finish();
            }
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }


}