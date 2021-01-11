package com.example.petwalk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.petwalk.models.Usuarios;
import com.example.petwalk.ui.Profile;
import com.example.petwalk.utilerias.SessionManager;
import com.google.android.material.navigation.NavigationView;

public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Usuarios user;
    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = (Usuarios) getIntent().getSerializableExtra("user");

        byte[] decodedString = Base64.decode(user.getUserphoto(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        editMenu(navigationView);

        View header = navigationView.getHeaderView(0);

        ImageView iv = header.findViewById(R.id.imageView);
        iv.setImageBitmap(decodedByte);
        ((TextView)header.findViewById(R.id.userInfo)).setText(user.getUsername());
        ((TextView)header.findViewById(R.id.textView)).setText(user.getUsermail());


        header.setOnClickListener(v -> {
            Intent profile = new Intent(getApplicationContext(), Profile.class);
            profile.putExtra("userCurrent", user);
            startActivity(profile);
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    private void editMenu(NavigationView navigationView) {
        if(user.getUsertype() == 2){
            Menu nav = navigationView.getMenu();
            nav.findItem(R.id.nav_tools).setVisible(false);
            nav.findItem(R.id.nav_gallery).setVisible(false);
            nav.findItem(R.id.nav_send).setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
