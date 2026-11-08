package ru.sergeipavlov.metrologyhelper;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private ConverterPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = new ConverterPreferences(this);

        setupViews();
        setupToolbar();
        setupNavigationDrawer();

        // Load length converter by default
        if (savedInstanceState == null) {
            loadFragment(new LengthConverterFragment());
            navigationView.setCheckedItem(R.id.nav_unit_converter);
        }
    }

    private void setupViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_unit_converter) {
            loadFragment(new LengthConverterFragment());
        } else if (itemId == R.id.nav_reset_values) {
            showResetDialog();
        } else if (itemId == R.id.nav_scale_signal ||
                   itemId == R.id.nav_measurement_devices ||
                   itemId == R.id.nav_instructions ||
                   itemId == R.id.nav_app_version ||
                   itemId == R.id.nav_website ||
                   itemId == R.id.nav_settings) {
            Toast.makeText(this, R.string.feature_unavailable, Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    private void showResetDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.reset_dialog_title)
                .setMessage(R.string.reset_dialog_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.reset, (dialog, which) -> {
                    preferences.resetAllValues();

                    // Refresh current fragment
                    Fragment currentFragment = getSupportFragmentManager()
                            .findFragmentById(R.id.content_frame);
                    if (currentFragment instanceof LengthConverterFragment) {
                        ((LengthConverterFragment) currentFragment).refreshFromReset();
                    }

                    Toast.makeText(this, "Значения сброшены", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
