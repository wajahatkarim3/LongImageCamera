package com.wajahatkarim3.longimagecamera;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.Permission;
import java.util.ArrayList;

public class LongImageCameraActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1002;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 1247;
    public final String TAG = LongImageCameraActivity.class.getSimpleName();

    Button btnSnap, btnDone;
    ImageView imgRecent;
    CameraView cameraView;

    boolean isFirstImage = true;
    ArrayList<Bitmap> bitmapsList = new ArrayList<>();
    private Handler mBackgroundHandler;

    Bitmap finalBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_image_camera);

        btnSnap = (Button) findViewById(R.id.btnSnap);
        btnDone = (Button) findViewById(R.id.btnDone);
        imgRecent = (ImageView) findViewById(R.id.imgRecent);
        cameraView = (CameraView) findViewById(R.id.cameraView);

        imgRecent.setVisibility(View.GONE);

        btnSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSnapClick(view);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDoneClick(view);
            }
        });

        checkForCameraPermission();


        cameraView.addCallback(cameraCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
        {
            // Start camera
            cameraView.start();
        }
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    public void btnSnapClick(View view)
    {
        cameraView.takePicture();
    }

    public void btnDoneClick(View view)
    {
        if (bitmapsList.size() <= 0) return;

        int width = bitmapsList.get(0).getWidth();
        int height = 0;

        for (Bitmap bitmap : bitmapsList)
        {
            height += bitmap.getHeight();
        }

        finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(finalBitmap);
        float tempHeight = 0;

        for (int i=0; i<bitmapsList.size(); i++)
        {
            Bitmap bitmap = bitmapsList.get(i);
            canvas.drawBitmap(bitmap, 0f, tempHeight, null);
            tempHeight += bitmap.getHeight();
        }


        checkForFileWritePermission();
    }

    public void checkForFileWritePermission()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
        {
            // Save file
            saveBitmap(finalBitmap);
        }
        else {
            // ask for permission
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);
        }
    }

    public void checkForCameraPermission()
    {
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
        {
            // Start camera
            cameraView.start();
        }
        else {
            // Ask for permissions here

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // Show dialog explaning why we need camera permission here

                showInfoDialog();

            }
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

            }
        }
    }

    public void showInfoDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_title));
        builder.setMessage(getString(R.string.dialog_message));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic

                        // Ask for permission. User has agreed to grant it.
                        ActivityCompat.requestPermissions(LongImageCameraActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic

                        // let it go then
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    cameraView.start();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // save the image here now
                    saveBitmap(finalBitmap);


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    public CameraView.Callback cameraCallback = new CameraView.Callback() {

        @Override
        public void onCameraClosed(CameraView cameraView) {
            super.onCameraClosed(cameraView);
        }

        @Override
        public void onCameraOpened(CameraView cameraView) {
            super.onCameraOpened(cameraView);
        }

        @Override
        public void onPictureTaken(CameraView cameraView, byte[] data) {
            super.onPictureTaken(cameraView, data);


            Log.d(TAG, "onPictureTaken: Picture Taken");

            if (isFirstImage)
            {
                imgRecent.setVisibility(View.VISIBLE);
            }

            Bitmap bitmapOriginal = BitmapFactory.decodeByteArray(data,0,data.length);

            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length, options);

            bitmapsList.add(bitmapOriginal);

            imgRecent.setImageBitmap(bitmap);

            Log.d(TAG, "btnSnapClick: ");
            Animation animation = AnimationUtils.loadAnimation(LongImageCameraActivity.this, R.anim.move_up_anim);
            imgRecent.startAnimation(animation);

        }
    };

    public void saveBitmap(Bitmap finalBitmap)
    {
        btnDone.setEnabled(false);
        btnSnap.setEnabled(false);

        final String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

        final String destDirectoryPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "ocr" + File.separator).getAbsolutePath();
        final Bitmap bitmapToSave = finalBitmap;

        getBackgroundHandler().post(new Runnable() {
            @Override
            public void run() {
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), tmpImg);
                OutputStream os = null;
                try {
                    os = new FileOutputStream(destDirectoryPath + tmpImg);
                    bitmapToSave.compress(Bitmap.CompressFormat.PNG, 100, os);

                    MediaScannerConnection.scanFile(LongImageCameraActivity.this, new String[] { (destDirectoryPath + tmpImg) }, new String[] { "image/png" }, null);

                    Toast.makeText(getApplicationContext(), "Image Saved Successfully!", Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    Log.w(TAG, "Cannot write to " + file, e);
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            // Ignore
                        }
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnDone.setEnabled(true);
                            btnSnap.setEnabled(true);
                        }
                    });

                }
            }
        });
    }






    private Handler getBackgroundHandler() {
        if (mBackgroundHandler == null) {
            HandlerThread thread = new HandlerThread("background");
            thread.start();
            mBackgroundHandler = new Handler(thread.getLooper());
        }
        return mBackgroundHandler;
    }

}
