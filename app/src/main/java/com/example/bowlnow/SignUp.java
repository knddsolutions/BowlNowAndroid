package com.example.bowlnow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button UserAccountButton = findViewById(R.id.buttonUser);
        Button BowlingCenterButton = findViewById(R.id.buttonCenter);

        UserAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToUserRegister();
            }
        });

        BowlingCenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToCenterRegister();
            }
        });
    }

    public void ToUserRegister() {
        Intent Intent = new Intent(this, UserRegister.class);
        startActivity(Intent);
    }

    public void ToCenterRegister() {
        Intent Intent = new Intent(this, CenterRegister.class);
        startActivity(Intent);
    }
}
