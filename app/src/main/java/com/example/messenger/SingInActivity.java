package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SingInActivity extends AppCompatActivity implements View.OnClickListener {

    private Button singIn = null;
    private Button back = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sing_in_activity);
        singIn = findViewById(R.id.sing_in);
        back = findViewById(R.id.back);
        singIn.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sing_in:
                startHomeActivity();
                break;
            case R.id.back:
                backToMainActivity();
                break;
        }
    }
    void startHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    void backToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}