package opengl.simple.example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

import opengl.simple.example.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    Bitmap bitmap;
    private static final String TAG = "CameraActivity";
    ImageRenderer imageRenderer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        loadImage();
        initGLSurfaceVew();
      initListeners();


    }

    private void initListeners() {
        binding.btnFilter1.setOnClickListener(v -> {
            imageRenderer.setImage(bitmap);
            binding.glSurfaceView.requestRender();
        });
    }

    private void loadImage() {
    bitmap = BitmapFactory.decodeResource(getResources(),  R.drawable.image_sample);
        binding.imageView.setImageBitmap(bitmap);
        imageRenderer = new ImageRenderer(this, bitmap);
      //  binding.glSurfaceView.requestRender();
    }

    private void initGLSurfaceVew() {
        // Request an OpenGL ES 2.0 compatible context.
        binding.glSurfaceView.setEGLContextClientVersion(2);

        // Set the renderer to our demo renderer, defined below.
        binding.glSurfaceView.setRenderer(imageRenderer);
        binding.glSurfaceView.setPreserveEGLContextOnPause(true);
        binding.glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

  






    @Override
    protected void onResume() {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        binding.glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        binding.glSurfaceView.onPause();
    }

    private void initListners() {
    }

}