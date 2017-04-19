package com.wajahatkarim3.longimagecamera.demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.wajahatkarim3.longimagecamera.LongImageCameraActivity;
import com.wajahatkarim3.longimagecamera.TouchImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cameraButton = (Button) findViewById(R.id.button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LongImageCameraActivity.launch(MainActivity.this);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == LongImageCameraActivity.LONG_IMAGE_RESULT_CODE)
        {
            String imageFileName = data.getStringExtra(LongImageCameraActivity.IMAGE_PATH_KEY);

            TouchImageView imageView = (TouchImageView) findViewById(R.id.imageView);

            Bitmap d = BitmapFactory.decodeFile(imageFileName);
            int newHeight = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
            Bitmap putImage = Bitmap.createScaledBitmap(d, 512, newHeight, true);
            imageView.setImageBitmap(putImage);

            Log.e(TAG, "onActivityResult: " + imageFileName );
        }
    }
}
