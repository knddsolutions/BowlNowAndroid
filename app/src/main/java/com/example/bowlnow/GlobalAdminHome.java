package com.example.bowlnow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class GlobalAdminHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> ActiveCenters = (ArrayList<String>) getIntent().getSerializableExtra("ActiveCenters");
        setContentView(R.layout.activity_global_admin_home);


    }
}
