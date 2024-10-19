package com.albe.Video2Audio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.Level;
import com.albe.adapter.ViewPagerAdapter;
import com.albe.fragment.FragmentHome;
import com.albe.fragment.FragmentOutputFolder;
import com.albe.Video2Audio.Conversion.ConvertActivity;
import com.albe.Video2Audio.Conversion.Converter;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ViewPager2 pagerMain;

    BottomNavigationView bottomNavigationView;

    private static final int PERMISSION_REQUEST_CODE = 2, SETTINGS_CHANGE_CODE = 1;


    public static MainActivity INSTANCE;

    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

    String[] REQUESTED_PERMISSIONS = Build.VERSION.SDK_INT >= 33 ?
            new String[] {Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO} :
            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConvertActivity.converter = new Converter();
        ConvertActivity.converter.init();
        Config.setLogLevel(Level.AV_LOG_INFO);
        FragmentOutputFolder.configManager = new ConfigManager(this);
        checkFilePermissions();
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        pagerMain = findViewById(R.id.pageMain);
        pagerMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0 -> bottomNavigationView.setSelectedItemId(R.id.home);
                    case 1 -> bottomNavigationView.setSelectedItemId(R.id.outputFolder);
                }
                super.onPageSelected(position);
            }
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) pagerMain.setCurrentItem(0);
            else if (item.getItemId() == R.id.outputFolder) pagerMain.setCurrentItem(1);
            return true;
        });
        fragmentArrayList.add(new FragmentHome());
        fragmentArrayList.add(new FragmentOutputFolder());
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, fragmentArrayList);
        pagerMain.setAdapter(viewPagerAdapter);
        INSTANCE = this;
        bottomNavigationView.setSelectedItemId(R.id.outputFolder);
        MobileAds.initialize(this, initializationStatus -> {

        });
    }

    public FragmentHome getFragmentHome() {
        return (FragmentHome) fragmentArrayList.get(0);
    }

    public FragmentOutputFolder getFragmentOutputFolder() {
        return (FragmentOutputFolder) fragmentArrayList.get(1);
    }
    public boolean checkFilePermissions() {
        boolean hasPermissions = true;
        for (String permission : REQUESTED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) hasPermissions = false;
        }
        if (!hasPermissions) {
            ActivityCompat.requestPermissions(this,
                    REQUESTED_PERMISSIONS,
                    PERMISSION_REQUEST_CODE);
        }
        return hasPermissions;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!granted) {
                Toast.makeText(this, "Video and audio file access permissions are required to use this application!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static String changeFileExtension(String filePath, String newExtension) {
        File file = new File(filePath);
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return filePath;
        }
        String baseName = fileName.substring(0, dotIndex);
        String newFileName = baseName + "(" + ((int)System.currentTimeMillis() / 1000) + ")" + newExtension;
        String parentDir = file.getParent();
        String finalPath = parentDir != null ? parentDir + File.separator + newFileName : newFileName;
        String[] patterns = finalPath.split("/");
        return patterns[patterns.length - 1];
    }
}