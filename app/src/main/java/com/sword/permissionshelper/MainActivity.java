package com.sword.permissionshelper;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sword.permissionshelper.library.PermissionsHepler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LinearLayout scrollLayout = (LinearLayout) findViewById(R.id.scroll_layout);
        for (int i = 0; i < 60; i++) {
            LinearLayout textLayout = new LinearLayout(getApplicationContext());
            textLayout.setPadding(0, 10, 0, 10);
            scrollLayout.addView(textLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150));

            TextView lTextView = new TextView(getApplicationContext());
            lTextView.setText("i = " + (i + 1));
            lTextView.setTag(i);
            lTextView.setTextSize(20);
            lTextView.setTextColor(Color.WHITE);
            lTextView.setGravity(Gravity.CENTER);
            lTextView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            textLayout.addView(lTextView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            lTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View pView) {
                    int[] location = new int[2];
                    pView.getLocationInWindow(location);
                    int x = location[0];
                    int y = location[1];

                    Log.d("Sword", "pView-y-->" + y);
                    Log.d("Sword", "pView-tag-->" + pView.getTag());
                    Log.d("Sword", "-----------------------------");
                    int[] location2 = new int[2];
                    pView.getLocationOnScreen(location2);
                    int x2 = location2[0];
                    int y2 = location2[1];
                    Log.d("Sword", "pView-y2-->" + y2);
                    Log.d("Sword", "pView-tag-->" + pView.getTag());
                    Log.d("Sword", "===============================");
                }
            });
        }

        Log.d("Sword", "has permission: " + PermissionsHepler.getInstance(this).checkPermissionGranted(Manifest.permission.CAMERA));
        Log.d("Sword", "show permission: " + PermissionsHepler.getInstance(this).shouldShowRequestPermissionRationale(Manifest.permission.CAMERA));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            PermissionsHepler.getInstance(this).performCodeWithPermission("相机", new PermissionsHepler.PermissionCallback() {
                @Override
                public void hasPermission() {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                    Toast.makeText(getApplicationContext(), "获取权限成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void noPermission(String code) {
                    Toast.makeText(getApplicationContext(), "获取权限失败: " + code, Toast.LENGTH_SHORT).show();
                }
            }, true, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PermissionsHepler.getInstance(this).onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
