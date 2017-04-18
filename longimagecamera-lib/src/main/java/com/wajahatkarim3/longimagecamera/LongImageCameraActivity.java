package com.wajahatkarim3.longimagecamera;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

    public enum ImageMergeMode {
        HORIZONTAL,
        VERTICAL
    }

    Button btnSnap;
    ImageButton btnDone, btnFlashMode;
    ImageView imgRecent;
    CameraView cameraView;
    ProgressBar progressBar;

    boolean isFirstImage = true;
    ArrayList<Bitmap> bitmapsList = new ArrayList<>();
    private Handler mBackgroundHandler;

    Bitmap finalBitmap;

    ImageMergeMode mergeMode = ImageMergeMode.HORIZONTAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_image_camera);

        btnSnap = (Button) findViewById(R.id.btnSnap);
        btnDone = (ImageButton) findViewById(R.id.btnDone);
        btnFlashMode = (ImageButton) findViewById(R.id.btnFlashMode);
        imgRecent = (ImageView) findViewById(R.id.imgRecent);
        cameraView = (CameraView) findViewById(R.id.cameraView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        imgRecent.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        btnDone.setVisibility(View.GONE);

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

        btnFlashMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFlashMode();
            }
        });

        checkForCameraPermission();


        cameraView.addCallback(cameraCallback);
        cameraView.setFlash(CameraView.FLASH_AUTO);
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

    public void changeFlashMode()
    {
        if (cameraView.getFlash() == CameraView.FLASH_AUTO)
        {
            cameraView.setFlash(CameraView.FLASH_ON);
            btnFlashMode.setImageResource(R.drawable.ic_flash_on);
        }
        else if (cameraView.getFlash() == CameraView.FLASH_ON)
        {
            cameraView.setFlash(CameraView.FLASH_OFF);
            btnFlashMode.setImageResource(R.drawable.ic_flash_off);
        }
        else if (cameraView.getFlash() == CameraView.FLASH_OFF)
        {
            cameraView.setFlash(CameraView.FLASH_AUTO);
            btnFlashMode.setImageResource(R.drawable.ic_flash_auto);
        }
    }

    public void btnSnapClick(View view)
    {
        cameraView.takePicture();
    }

    public void btnDoneClick(View view)
    {
        if (bitmapsList.size() <= 0) return;

        if (mergeMode == ImageMergeMode.VERTICAL)
        {
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
        }
        else if (mergeMode == ImageMergeMode.HORIZONTAL)
        {
            int height = bitmapsList.get(0).getHeight();
            int width = 0;

            for (Bitmap bitmap : bitmapsList)
            {
                width += bitmap.getWidth();
            }

            finalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(finalBitmap);

            float tempWidth = 0;

            for (int i=0; i<bitmapsList.size(); i++)
            {
                Bitmap bitmap = bitmapsList.get(i);
                canvas.drawBitmap(bitmap, tempWidth, 0f, null);
                tempWidth += bitmap.getWidth();
            }
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
                btnDone.setVisibility(View.VISIBLE);

                Animation downAnimation = AnimationUtils.loadAnimation(LongImageCameraActivity.this, R.anim.move_down_anim);
                cameraView.startAnimation(downAnimation);
            }

            //Bitmap bitmapOriginal = BitmapFactory.decodeByteArray(data,0,data.length);

            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length, options);
            Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth()*75/100, bitmap.getHeight());

            bitmapsList.add(croppedBitmap);

            imgRecent.setImageBitmap(croppedBitmap);

            Log.d(TAG, "btnSnapClick: ");

            Animation animation = AnimationUtils.loadAnimation(LongImageCameraActivity.this, R.anim.move_up_anim);
            imgRecent.startAnimation(animation);


        }
    };

    public void saveBitmap(Bitmap finalBitmap)
    {
        btnDone.setEnabled(false);
        btnSnap.setEnabled(false);
        btnFlashMode.setEnabled(false);

        cameraView.setVisibility(View.INVISIBLE);
        btnDone.setVisibility(View.INVISIBLE);
        btnFlashMode.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        final String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

        File dstDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        dstDir.mkdirs();


        final String destDirectoryPath = dstDir.getAbsolutePath();
        final Bitmap bitmapToSave = finalBitmap;

        getBackgroundHandler().post(new Runnable() {
            @Override
            public void run() {
                File file = new File(destDirectoryPath, tmpImg);
                OutputStream os = null;
                try {
                    os = new FileOutputStream(destDirectoryPath + File.separator + tmpImg);
                    bitmapToSave.compress(Bitmap.CompressFormat.PNG, 100, os);

                    MediaScannerConnection.scanFile(LongImageCameraActivity.this, new String[] { (destDirectoryPath + File.separator + tmpImg) }, new String[] { "image/png" }, null);

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
                            btnFlashMode.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            btnDone.setVisibility(View.GONE);
                            btnFlashMode.setVisibility(View.VISIBLE);
                            btnSnap.setVisibility(View.VISIBLE);
                            isFirstImage = true;
                            bitmapsList.clear();

                            Intent ii = new Intent(LongImageCameraActivity.this, PreviewLongImageActivity.class);
                            ii.putExtra("imageName", destDirectoryPath + File.separator + tmpImg);
                            startActivity(ii);
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
