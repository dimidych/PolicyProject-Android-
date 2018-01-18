package com.dimidych.policyservicestarter;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dimidych.policydbworker.DevicePolicyAdmin;
import com.dimidych.policydbworker.PolicyAdminReceiver;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar _toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName policyDeviceAdmin = new ComponentName(getApplicationContext(),
                PolicyAdminReceiver.class);
        _toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(_toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, _toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        MenuItem mnu=navigationView.getMenu().getItem(0);

        if(mnu==null)
            return;

        mnu.setChecked(true);
        onNavigationItemSelected(mnu);
        DevicePolicyAdmin.requestAdminService(this, policyDeviceAdmin);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fTran = fragmentManager.beginTransaction();
        Fragment fragment = null;

        if (id == R.id.nav_about) {
            fragment = new AboutFragment();
            _toolbar.setTitle(getString(R.string.action_about));
        } else if (id == R.id.nav_check_policies) {
            fragment = new CheckPolicyFragment(fragmentManager);
            _toolbar.setTitle(getString(R.string.action_check_policies));
        } else if (id == R.id.nav_get_cert) {
            fragment = new GetCertFragment(fragmentManager);
            _toolbar.setTitle(getString(R.string.action_get_cert));
        } else if (id == R.id.nav_network_settings) {
            fragment = new NetworkFragment(fragmentManager);
            _toolbar.setTitle(getString(R.string.action_network_settings));
        } else if (id == R.id.nav_logs) {
            fragment = new LogViewerFragment(fragmentManager);
            _toolbar.setTitle(getString(R.string.action_logs));
        }else if (id == R.id.nav_service) {
            fragment = new ServiceFragment(fragmentManager);
            _toolbar.setTitle(getString(R.string.action_service));
        }

        fTran.replace(R.id.content_main, fragment);
        fTran.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
