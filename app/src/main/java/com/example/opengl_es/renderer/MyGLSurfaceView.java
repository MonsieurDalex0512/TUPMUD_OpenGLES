package com.example.opengl_es.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * Custom GLSurfaceView configured for OpenGL ES 3.0.
 * Exposes hooks for toggling optimizations via RenderConfig.
 * Supports touch input for camera movement.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer renderer;
    private ScaleGestureDetector scaleDetector;
    
    // Touch handling
    private float lastTouchX = 0f;
    private float lastTouchY = 0f;
    private boolean isRotating = false;
    private boolean isPanning = false;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Request an OpenGL ES 3.0 compatible context
        setEGLContextClientVersion(3);

        renderer = new MyGLRenderer(context);
        setRenderer(renderer);

        // BƯỚC 4: RENDER MODE OPTIMIZATION
        // RENDERMODE_WHEN_DIRTY: Chỉ render khi có thay đổi → tiết kiệm pin (nhưng metrics không cập nhật)
        // RENDERMODE_CONTINUOUSLY: Render liên tục 60fps → tốn pin hơn (nhưng metrics cập nhật real-time)
        // Dùng CONTINUOUSLY để metrics cập nhật liên tục và có thể đo performance
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        
        // Setup scale gesture detector for pinch zoom
        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                // Pinch in (scaleFactor < 1.0) = zoom in (decrease radius)
                // Pinch out (scaleFactor > 1.0) = zoom out (increase radius)
                // Use logarithmic scale for smoother zoom
                float zoomDelta = (1.0f - scaleFactor) * 3.0f; // Increased sensitivity
                if (renderer != null && renderer.getSceneManager() != null) {
                    renderer.getSceneManager().getCamera().zoom(zoomDelta);
                }
                return true;
            }
        });
    }

    public MyGLRenderer getRenderer() {
        return renderer;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Handle pinch zoom
        if (scaleDetector != null) {
            scaleDetector.onTouchEvent(event);
        }
        
        int action = event.getActionMasked();
        int pointerCount = event.getPointerCount();
        
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Single touch: prepare for rotation
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                isRotating = true;
                isPanning = false;
                return true;
                
            case MotionEvent.ACTION_POINTER_DOWN:
                if (pointerCount == 2) {
                    // Two fingers: prepare for pan
                    float avgX = (event.getX(0) + event.getX(1)) / 2.0f;
                    float avgY = (event.getY(0) + event.getY(1)) / 2.0f;
                    lastTouchX = avgX;
                    lastTouchY = avgY;
                    isPanning = true;
                    isRotating = false;
                }
                return true;
                
            case MotionEvent.ACTION_MOVE:
                if (renderer == null || renderer.getSceneManager() == null) {
                    return false;
                }
                
                if (pointerCount == 1 && isRotating) {
                    // Single finger drag: rotate camera
                    float deltaX = event.getX() - lastTouchX;
                    float deltaY = event.getY() - lastTouchY;
                    
                    // Rotate horizontally (azimuth) - invert for natural rotation
                    float azimuthDelta = -deltaX * 0.8f; // Increased sensitivity, inverted
                    renderer.getSceneManager().getCamera().rotateAzimuth(azimuthDelta);
                    
                    // Rotate vertically (elevation) - invert for natural rotation
                    float elevationDelta = -deltaY * 0.8f; // Increased sensitivity, inverted
                    renderer.getSceneManager().getCamera().rotateElevation(elevationDelta);
                    
                    lastTouchX = event.getX();
                    lastTouchY = event.getY();
                    return true;
                } else if (pointerCount == 2 && isPanning) {
                    // Two finger drag: pan camera
                    float avgX = (event.getX(0) + event.getX(1)) / 2.0f;
                    float avgY = (event.getY(0) + event.getY(1)) / 2.0f;
                    
                    // Calculate delta from previous average
                    float deltaX = avgX - lastTouchX;
                    float deltaY = avgY - lastTouchY;
                    
                    // Pan with proper sensitivity (invert Y for natural movement)
                    renderer.getSceneManager().getCamera().pan(deltaX * 0.01f, -deltaY * 0.01f);
                    
                    lastTouchX = avgX;
                    lastTouchY = avgY;
                    return true;
                }
                break;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                isRotating = false;
                isPanning = false;
                return true;
        }
        
        return super.onTouchEvent(event);
    }
}

