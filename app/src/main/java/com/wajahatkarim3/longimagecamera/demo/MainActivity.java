package com.wajahatkarim3.longimagecamera.demo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.wajahatkarim3.longimagecamera.LongImageCameraActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LongImageCameraActivity.launch(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == LongImageCameraActivity.LONG_IMAGE_RESULT_CODE)
        {
            String path = data.getStringExtra(LongImageCameraActivity.IMAGE_PATH_KEY);

            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageURI(Uri.parse(path));

            Log.e(TAG, "onActivityResult: " + path );
        }
    }
}
