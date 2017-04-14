package com.wajahatkarim3.longimagecamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class LongImageCameraActivity extends AppCompatActivity {

    public final String TAG = LongImageCameraActivity.class.getSimpleName();

    Button btnSnap, btnDone;
    ImageView imgRecent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_image_camera);

        btnSnap = (Button) findViewById(R.id.btnSnap);
        btnDone = (Button) findViewById(R.id.btnDone);
        imgRecent = (ImageView) findViewById(R.id.imgRecent);

        btnSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSnapClick(view);
            }
        });

    }

    public void btnSnapClick(View view)
    {
        Log.d(TAG, "btnSnapClick: ");
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.move_up_anim);
        imgRecent.startAnimation(animation);
    }
}
