package com.wajahatkarim3.longimagecamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class PreviewLongImageActivity extends AppCompatActivity {

    TouchImageView imgLongPreview;
    ImageView imgClose;

    String imageFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_long_image);

        imgLongPreview = (TouchImageView) findViewById(R.id.imgLongPreview);
        imgClose = (ImageView) findViewById(R.id.imgClose);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (getIntent().getExtras() != null && getIntent().hasExtra("imageName"))
        {
            imageFileName = getIntent().getExtras().getString("imageName");

            Bitmap d = BitmapFactory.decodeFile(imageFileName);
            int newHeight = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
            Bitmap putImage = Bitmap.createScaledBitmap(d, 512, newHeight, true);
            imgLongPreview.setImageBitmap(putImage);
        }
    }
}
