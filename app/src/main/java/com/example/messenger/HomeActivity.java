package com.example.messenger;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private TextView textEmail;
    private TextView textLogin;
    private CircleImageView imageView;
    private String image = "111";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        textEmail = hView.findViewById(R.id.text_email);
        textLogin = hView.findViewById(R.id.text_login);
        imageView = hView.findViewById(R.id.imageView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_settings,R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }
    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            if (currentUser.getDisplayName() == null) {
                DialogFragment dialog = new UsernameDialog();
                dialog.show(getSupportFragmentManager(), "dia");

            } else {
                updateUI(currentUser);

            }
        }else {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }
    Context tgetContext(){
        return this;
    }
     void updateUI(FirebaseUser currentUser){

        textEmail.setText(currentUser.getEmail());
        textLogin.setText(currentUser.getDisplayName());
         FirebaseDatabase database = FirebaseDatabase.getInstance();
         DatabaseReference myRef = database.getReference("Users").child(mAuth.getUid()).child("image");

         myRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 // This method is called once with the initial value and again
                 // whenever data at this location is updated.
                  image = dataSnapshot.getValue(String.class);
                 if(image.equals("def")){
                     imageView.setImageResource(R.mipmap.ic_launcher);
                 } else {
                     Glide.with(tgetContext()).load(image).into(imageView);
                 }

             }

             @Override
             public void onCancelled(DatabaseError error) {
                 imageView.setImageResource(R.mipmap.ic_launcher);

             }
         });


    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
    void backToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
