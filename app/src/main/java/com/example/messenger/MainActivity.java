package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button singIn = null;
    private Button register = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        singIn = findViewById(R.id.sing_in);
        register = findViewById(R.id.register);
        singIn.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sing_in:
                startSingInActivity();
                break;
            case R.id.register:
                startRegisterActivity();
                break;
        }
    }
    void startSingInActivity(){
        Intent intent = new Intent(this, SingInActivity.class);
        startActivity(intent);
    }
    void startRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}