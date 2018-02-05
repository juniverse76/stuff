package xyz.juniverse.stuff.image;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import xyz.juniverse.stuff.console;

/**
 * Created by juniverse on 29/03/2017.
 *
 * Manifest.xml에 application 태그안에 android:hardwareAccelerated="true" 있어야 함!!! 전에는 안그랬는데...
 */

public abstract class CameraActivityDelegate implements TextureView.SurfaceTextureListener
{
    static private final int REQUEST_CAMERA_PERMISSION_ID = 39245;

    public Size imageDimension;

    private AppCompatActivity activity;
    private FlexibleTextureView textureView;
    private int initialLens;

    public CameraActivityDelegate(AppCompatActivity activity, @Nullable ViewGroup parent)
    {
        this.activity = activity;
        this.initialLens = CameraCharacteristics.LENS_FACING_BACK;
        onCreate(parent);
    }

    public CameraActivityDelegate(AppCompatActivity activity, @Nullable ViewGroup parent, int initialLens)
    {
        this.activity = activity;

        this.initialLens = CameraCharacteristics.LENS_FACING_BACK;
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] idList = manager.getCameraIdList();
            for (String id : idList) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == initialLens) {
                    this.initialLens = initialLens;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        onCreate(parent);
    }

    private FrameLayout main;
    private void onCreate(@Nullable ViewGroup parent)
    {
        this.main = new FrameLayout(activity.getBaseContext());
        main.setBackgroundColor(0x00000000);
        textureView = new FlexibleTextureView(activity.getBaseContext());
        textureView.setTag("camera_preview");
        textureView.setSurfaceTextureListener(this);

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        flp.gravity = Gravity.CENTER;
        main.addView(textureView, flp);

        if (parent != null)
            parent.addView(main, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        else
            activity.addContentView(main, flp);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        console.i("onSurfaceTextureAvailable");
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        console.i("onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        console.i("onSurfaceTextureDestroyed");
        closeCamera();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public void closeCamera() {
        if (cameraDevice == null)
            return;
        cameraDevice.close();
        cameraDevice = null;
    }

    private String cameraId = null;
    private void openCamera()
    {
        openCamera(initialLens);
    }

    private void openCamera(int lensFace)
    {
        console.i("openCamera");
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] idList = manager.getCameraIdList();
            for (String id : idList) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == lensFace) {
                    cameraId = id;
                    break;
                }
            }

            console.i("cameraId?", cameraId);

            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

//            int maxCounts = characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT);
//            int modes[] = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);
//            console.i("maxCounts?", maxCounts);
//            for (int mode : modes)
//                console.i("mode?", mode);

            // todo 특정 사이즈로 output을 지정하는 게 필요함.. 무조건 full size로 할수 없음!

            assert map != null;
            Size[] sizes = map.getOutputSizes(SurfaceTexture.class);
//            for (Size size : sizes)
//                console.i("size?", size);
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
//            imageDimension = chooseOptimalSize(sizes, 640, 480, 640, 480, new Size(640, 480));
            console.i("imageDimension?", imageDimension);


            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION_ID);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private Size chooseOptimalSize(Size[] choices, int textureViewWidth, int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
//            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    private class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    CameraDevice cameraDevice = null;
    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            console.i("onDisconnected");
            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int i) {
            console.i("onError");
            if (cameraDevice != null) {
                cameraDevice.close();
                cameraDevice = null;
            }
        }
    };

    CaptureRequest.Builder captureRequestBuilder;
    CameraCaptureSession cameraCaptureSessions;
    private void createCameraPreview()
    {
        console.i("createCameraPreview");
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            textureView.setAspectRatio(imageDimension.getWidth(), imageDimension.getHeight());

            ArrayList<Surface> surfaces = new ArrayList<>();

            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            surfaces.add(surface);

//            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//        captureRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CameraMetadata.STATISTICS_FACE_DETECT_MODE_FULL);
//        captureRequestBuilder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CameraMetadata.STATISTICS_FACE_DETECT_MODE_SIMPLE);

            // todo add custom capture request for different application.
            applyCustomCaptureRequest(captureRequestBuilder, surfaces);

            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(activity.getBaseContext(), "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview()
    {
        if(null == cameraDevice) {
            console.i("...");
        }

        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), captureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback()
    {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
            onCaptureResult(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            onCaptureResult(result);
        }
    };

    HandlerThread mBackgroundThread;
    Handler mBackgroundHandler;
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean hasCameraHardware() {
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }





    public FrameLayout getMainLayout()
    {
        return main;
    }

    public void switchCamera()
    {
        if (cameraId == null)
            return;

        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] idList = manager.getCameraIdList();
            if (idList.length != 2)
                return;

            for (String id : idList) {
                if (id != cameraId)
                {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                    cameraDevice.close();
                    openCamera(characteristics.get(CameraCharacteristics.LENS_FACING));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPortrait()
    {
        return imageDimension.getHeight() > imageDimension.getWidth();
    }

    public int getCameraOrientation()
    {
        try {
            CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            return characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean isFrontFacing() {
        try {
            CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            return characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Rect getSensorArraySize()
    {
        try {
            CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            return characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        } catch (Exception e) {}
        return null;
    }

    // 필수 구현 함수들...
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(activity, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public void onResume() {
        startBackgroundThread();
        if (textureView.isAvailable()) {
            console.i("onResume");
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(this);
        }
    }

    public void onPause() {
        closeCamera();
        stopBackgroundThread();
    }

    abstract protected void applyCustomCaptureRequest(CaptureRequest.Builder builder, List<Surface> surfaceList);
    abstract protected void onCaptureResult(CaptureResult result);

}
