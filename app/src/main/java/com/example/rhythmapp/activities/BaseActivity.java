package com.example.rhythmapp.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;



import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.rhythmapp.R;
import com.example.rhythmapp.databinding.ActivityBaseBinding;
import com.example.rhythmapp.fragments.ChangePasswordFragment;
import com.example.rhythmapp.fragments.CheckReportFragment;
import com.example.rhythmapp.fragments.HomeFragment;
import com.example.rhythmapp.fragments.MyDetailsFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;



public class BaseActivity extends AppCompatActivity {

    private ActivityBaseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the Toolbar as the support action bar
        setSupportActionBar(binding.materialToolbar);

        // Initialize ActionBarDrawerToggle
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.materialToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.setDrawerSlideAnimationEnabled(true);

        // Add the toggle as a listener to the DrawerLayout
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);

        loadFragment(new HomeFragment());
        handleBackPressed();


        binding.navigationView.setNavigationItemSelectedListener(item -> {

            if(item.getItemId() == R.id.nav_home){
                loadFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.nav_check_report) {
                loadFragment(new CheckReportFragment());
            } else if (item.getItemId() == R.id.nav_my_details){
                loadFragment(new MyDetailsFragment());
            } else if (item.getItemId() == R.id.nav_change_password) {
                loadFragment(new ChangePasswordFragment());
            } else if (item.getItemId() == R.id.nav_logout) {
                showLogOutDialog();
            }

            binding.drawerLayout.closeDrawers();
            return true;
        });

    }

    private void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(binding.frameLayout.getId(), fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void handleBackPressed(){
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getSupportFragmentManager();

                if (fragmentManager.getBackStackEntryCount() > 1) {
                    fragmentManager.popBackStack();
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void showLogOutDialog(){
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new
                MaterialAlertDialogBuilder(this)
                .setTitle("Log Out?")
                .setMessage("Are you sure you want to sign out? You’ll need to log in again to access your account.")
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("remembered", false);
                    editor.apply();

                    //redirect to login screen
                    Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finishAffinity();
                })
                .setNeutralButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        materialAlertDialogBuilder.show();
    }

}