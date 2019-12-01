package com.haerul.sihandist.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.haerul.sihandist.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dagger.android.AndroidInjection;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_ID_WRITE_PERMISSION = 200;
    ImageView captureButton, flashButton, changeSide;
    FrameLayout frame;
    Camera camera;
    Activity context;
    PreviewCamera previewCamera;
    Camera.Parameters params;
    int currentCameraId;
    SurfaceView surfaceView;
    int FLAG_Rotate = 0;
    String jalur = null;
    int mDegrees;
    boolean hasInited = false;
    OrientationEventListener myOrientationEventListener;

    Camera.PictureCallback jpegCallback = (data, camera) -> {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String mImageName = "/SHD_IMAGE_" + timeStamp + ".jpg";
        jalur = getIntent().getStringExtra("PhotoFile");
        String prefix = getIntent().getStringExtra("prefix");
        File Path = new File(Constants.PATH_IMG + prefix + "/");
        if (jalur == null) {
            Path = new File(Constants.PATH);
            jalur = Path.getAbsolutePath() + mImageName;
        }
        File gambar = new File(jalur);
        if (!Path.exists()) {
            Path.mkdirs();
        }
        new CompressImage(data, gambar).execute();
    };
    private boolean isFlashOn = false;
    private Camera.ShutterCallback mShutterCallback = () -> {
    };

    Camera.AutoFocusCallback mAutoFocusCallback = (success, camera) -> {
        try {
            camera.takePicture(mShutterCallback, null, jpegCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        context = this;
        askPermissionAndWriteFile();
    }

    public void takeFocusedPicture() {
        camera.autoFocus(mAutoFocusCallback);
    }

    private void releaseCameraAndPreview() {
        previewCamera.setCamera(null);
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    protected void onResume() {
        super.onResume();
        // TODO Auto-generated method stub

    }

    public void init() {
        hasInited = true;
        myOrientationEventListener
                = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                // TODO Auto-generated method stub
                if (orientation >= 330 || orientation < 30) {
                    mDegrees = 90;
                } else if (orientation >= 60 && orientation < 120) {
                    mDegrees = 180;
                } else if (orientation >= 150 && orientation < 210) {
                    mDegrees = 90;
                } else if (orientation >= 240 && orientation < 300) {
                    mDegrees = 0;
                }
            }
        };

        if (myOrientationEventListener.canDetectOrientation()) {
            myOrientationEventListener.enable();
        } else {
            finish();
        }

        captureButton = findViewById(R.id.captureButton);
        flashButton = findViewById(R.id.flashButton);
        changeSide = findViewById(R.id.changebutton);
        surfaceView = findViewById(R.id.surfaceView);
        previewCamera = new PreviewCamera(this, surfaceView);

        frame = findViewById(R.id.frameLayout);
        frame.addView(previewCamera);
        previewCamera.setKeepScreenOn(true);

        if (camera == null) {
            camera = Camera.open();
            params = camera.getParameters();
            camera.startPreview();
            camera.setErrorCallback((error, mcamera) -> {
                camera.release();
                camera = Camera.open();
            });

            if (Build.VERSION.SDK_INT >= 16) {
                setCameraDisplayOrientation(context, Camera.CameraInfo.CAMERA_FACING_BACK, camera);
            }
            previewCamera.setCamera(camera);
        } else {
            camera = Camera.open();
            params = camera.getParameters();

            if (Build.VERSION.SDK_INT >= 16) {
                setCameraDisplayOrientation(context, Camera.CameraInfo.CAMERA_FACING_BACK, camera);
            }
            previewCamera.setCamera(camera);
        }

        captureButton.setOnClickListener(v -> {
            try {
                takeFocusedPicture();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        flashButton.setOnClickListener(v -> {
            if (isFlashOn) {
                flashButton.setImageResource(R.drawable.f1);
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);
                camera.startPreview();
                isFlashOn = false;
            } else {
                flashButton.setImageResource(R.drawable.f2);
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);
                camera.startPreview();
                isFlashOn = true;
            }
        });
        changeSide.setOnClickListener(view -> {
            if (camera != null) {
                camera.stopPreview();
            }
            if (camera != null)
                camera.release();

            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                flashButton.setVisibility(View.GONE);
                changeSide.setImageResource(R.drawable.ic_camera_rear_black_24dp);
                FLAG_Rotate = 1;

            } else {
                currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                flashButton.setVisibility(View.VISIBLE);
                changeSide.setImageResource(R.drawable.ic_camera_front_black_24dp);
                FLAG_Rotate = 0;
            }
            camera = Camera.open(currentCameraId);

            setCameraDisplayOrientation(CameraActivity.this, currentCameraId, camera);
            try {
                camera.setPreviewDisplay(surfaceView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
        });
    }

    private void askPermissionAndWriteFile() {
        boolean canWrite = askPermission(REQUEST_ID_WRITE_PERMISSION, Manifest.permission.CAMERA);
        if (canWrite) {
            init();
        }
    }

    private boolean askPermission(int requestId, String permissionName) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Check if we have permission
            int permission = ActivityCompat.checkSelfPermission(this, permissionName);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // If don't have permission so prompt the user.
                requestPermissions(
                        new String[]{permissionName},
                        requestId
                );
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hasInited)
            releaseCameraAndPreview();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (hasInited)
            releaseCameraAndPreview();
    }

    private Bitmap adjustedContrast(Bitmap src, double value) {
        // btnDelete size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int) (((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (R < 0) {
                    R = 0;
                } else if (R > 255) {
                    R = 255;
                }

                G = Color.green(pixel);
                G = (int) (((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }

                B = Color.blue(pixel);
                B = (int) (((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (B < 0) {
                    B = 0;
                } else if (B > 255) {
                    B = 255;
                }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }

    public Bitmap BITMAP_RESIZER(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ID_WRITE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                Toast.makeText(context, "Ijinkan perijinan kamera untuk menggunakan Sadix", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    class CompressImage extends AsyncTask<Void, Void, String> {
        byte[] dataImages;
        File gambar;
        ProgressDialog loading;

        CompressImage(byte[] bytes, File gambarFile) {
            dataImages = bytes;
            gambar = gambarFile;
        }

        @SafeVarargs
        @Override
        protected final String doInBackground(Void... bytes) {
            return compressed2(dataImages, gambar);
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            loading.dismiss();
            Intent intent = new Intent();
            intent.putExtra("data", path);
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(CameraActivity.this);
            loading.setMessage("Please Wait..");
            loading.setCancelable(false);
            loading.show();

            releaseCameraAndPreview();
        }
    }

    private String compressed2(byte[] dataImages, File gambar) {
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeByteArray(dataImages, 0, dataImages.length, options);

        int actualHeight = bmp.getHeight();
        int actualWidth = bmp.getWidth();

        float maxHeight = 1280; //actualHeight
        float maxWidth = 730; //actualWidth
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeByteArray(dataImages, 0, dataImages.length, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        FileOutputStream outStream = null;
        try {
            // Write to SD Card
            outStream = new FileOutputStream(gambar);
            outStream.write(stream.toByteArray());
            outStream.close();
            Log.e("File is Saved in  ", "" + gambar);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gambar.getPath();
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        params.setExposureCompensation(params.getMaxExposureCompensation());

        if (params.isAutoExposureLockSupported()) {
            params.setAutoExposureLock(false);
        }
        List<int[]> frameRates = params.getSupportedPreviewFpsRange();
        int l_first = 0;
        int minFps = (frameRates.get(l_first))[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
        int maxFps = (frameRates.get(l_first))[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
        params.setPreviewFpsRange(minFps, maxFps);
        mDegrees = result;
        params.setJpegQuality(100);
        params.setRotation(degrees);
        camera.setDisplayOrientation(result);

    }
}