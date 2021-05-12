package com.example.bowlnow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

public class MyCenters extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> MyCenters = (ArrayList<String>) getIntent().getSerializableExtra("MyCenters");
        setContentView(R.layout.activity_my_centers);

    }
}
