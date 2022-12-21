package com.example.navigationbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId==R.id.accueil){ selectedFragment = new FragmentAccueil();}
        else if (itemId==R.id.chatbot){selectedFragment = new FragmentChat();}
        else if (itemId==R.id.image){selectedFragment = new FragmentImage();}
        if (selectedFragment!=null){getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, selectedFragment).commit();}
        return true;
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(navListener);
    }




}