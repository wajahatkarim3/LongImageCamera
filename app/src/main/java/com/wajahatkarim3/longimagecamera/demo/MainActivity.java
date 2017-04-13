package com.wajahatkarim3.longimagecamera.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wajahatkarim3.longimagecamera.LongImageCameraActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent ii = new Intent(this, LongImageCameraActivity.class);
        startActivity(ii);
    }
}
