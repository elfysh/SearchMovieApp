package com.example.searchmovieapp.Activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.searchmovieapp.R;
import com.example.searchmovieapp.databinding.ActivityMain2Binding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class Main2Activity extends AppCompatActivity {


    ActivityMain2Binding binding;
    private NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.navView;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager(

        ).findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mainFragment, R.id.profileFragment,R.id.searchFragment, R.id.aboutDevFragment, R.id.aboutAppFragment
        ).build();
        

        NavigationUI.setupWithNavController(navView, navController);
    }
}
