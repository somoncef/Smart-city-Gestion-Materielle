package com.example.gestion_materielle_hackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment homeFragment = new Home();
    Fragment supervisionFragment = new Supervision();
    Fragment classificationFragment = new Classification();
    Fragment actionFragment = new Action();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

}