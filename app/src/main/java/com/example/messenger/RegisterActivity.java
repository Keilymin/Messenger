package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private Button register = null;

    private EditText email = null;
    private EditText password = null;
    private EditText reply_password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        register = findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();


        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        reply_password = findViewById(R.id.reply_password);
        register.setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                register();
                break;
            case R.id.back:
                backToMainActivity();
                break;
        }
    }
    void register(){

        if(!email.getText().toString().equals("")) {
            if(password.getText().toString().length()>=6 ) {
                if(password.getText().toString().equals(reply_password.getText().toString())) {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Log", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database.getReference("Users").child(mAuth.getUid());
                                        HashMap<String,String> hashMap = new HashMap<>();
                                        hashMap.put("id",mAuth.getUid());
                                        hashMap.put("email",email.getText().toString());
                                        hashMap.put("name","");
                                        hashMap.put("image","def");
                                        myRef.setValue(hashMap);
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                });
                                        startSingInActivity();
                                        // updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("Log", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(RegisterActivity.this, "Ошибка.",
                                                Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                    }

                                    // ...
                                }
                            });
                }else {
                    Toast.makeText(RegisterActivity.this, "Пароли не совпадают",
                            Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(RegisterActivity.this, "Пароль должен быть 6 или больше символов",
                        Toast.LENGTH_SHORT).show();
                }
        }else {
            Toast.makeText(RegisterActivity.this, "Ошибка ввода емайла",
                    Toast.LENGTH_SHORT).show();
        }
    }

    void startSingInActivity(){
        Intent intent = new Intent(this, SingInActivity.class);
        startActivity(intent);
    }
    void backToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}