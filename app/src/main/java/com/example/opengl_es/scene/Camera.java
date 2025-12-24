package com.example.opengl_es.scene;

import android.opengl.Matrix;

/**
 * Camera implementation: view + projection matrices.
 * Supports spherical coordinates for rotation and zoom.
 */
public class Camera {
    private final float[] viewMatrix = new float[16];
    private final float[] projMatrix = new float[16];
    private final float[] viewProjMatrix = new float[16];

    // Spherical coordinates for camera
    private float radius = 15f; // Distance from origin
    private float azimuth = 0f; // Horizontal rotation (0-360 degrees)
    private float elevation = 10f; // Vertical angle (0-90 degrees, 0 = top, 90 = side)
    
    // Target point (center of scene)
    private float targetX = 0f;
    private float targetY = 0f;
    private float targetZ = 0f;

    public Camera() {
        setPerspective(45f, 1f, 0.1f, 100f);
        updatePosition();
    }

    public void setPerspective(float fovYDeg, float aspect, float near, float far) {
        float fovRad = (float) (fovYDeg * Math.PI / 180.0);
        float top = (float) (Math.tan(fovRad / 2.0) * near);
        float bottom = -top;
        float left = bottom * aspect;
        float right = top * aspect;
        Matrix.frustumM(projMatrix, 0, left, right, bottom, top, near, far);
        updateViewProj();
    }

    /**
     * Update camera position based on spherical coordinates
     */
    private void updatePosition() {
        // Convert spherical to cartesian
        float azRad = (float) (azimuth * Math.PI / 180.0);
        float elRad = (float) (elevation * Math.PI / 180.0);
        
        float x = (float) (radius * Math.sin(elRad) * Math.cos(azRad));
        float y = (float) (radius * Math.cos(elRad));
        float z = (float) (radius * Math.sin(elRad) * Math.sin(azRad));
        
        // Camera position = target + offset
        float camX = targetX + x;
        float camY = targetY + y;
        float camZ = targetZ + z;
        
        // Update view matrix
        Matrix.setLookAtM(viewMatrix, 0, camX, camY, camZ, targetX, targetY, targetZ, 0, 1, 0);
        updateViewProj();
    }

    /**
     * Rotate camera horizontally (azimuth)
     */
    public void rotateAzimuth(float deltaDegrees) {
        azimuth += deltaDegrees;
        if (azimuth < 0) azimuth += 360f;
        if (azimuth >= 360f) azimuth -= 360f;
        updatePosition();
    }

    /**
     * Rotate camera vertically (elevation)
     */
    public void rotateElevation(float deltaDegrees) {
        elevation += deltaDegrees;
        // Limit elevation to avoid flipping
        if (elevation < 5f) elevation = 5f;
        if (elevation > 85f) elevation = 85f;
        updatePosition();
    }

    /**
     * Zoom camera (change radius)
     */
    public void zoom(float delta) {
        radius += delta;
        // Limit zoom range
        if (radius < 5f) radius = 5f;
        if (radius > 50f) radius = 50f;
        updatePosition();
    }

    /**
     * Pan camera (move target point)
     */
    public void pan(float deltaX, float deltaY) {
        // Calculate right and up vectors
        float azRad = (float) (azimuth * Math.PI / 180.0);
        float elRad = (float) (elevation * Math.PI / 180.0);
        
        // Right vector (perpendicular to view direction)
        float rightX = (float) -Math.sin(azRad);
        float rightZ = (float) Math.cos(azRad);
        
        // Up vector (always Y up)
        float upY = 1.0f;
        
        // Pan speed based on distance
        float panSpeed = radius * 0.01f;
        
        targetX += rightX * deltaX * panSpeed;
        targetZ += rightZ * deltaX * panSpeed;
        targetY += upY * deltaY * panSpeed;
        
        updatePosition();
    }

    public void setPosition(float x, float y, float z) {
        // Convert cartesian to spherical
        float dx = x - targetX;
        float dy = y - targetY;
        float dz = z - targetZ;
        
        radius = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (radius < 0.1f) radius = 15f; // Default if too close
        
        azimuth = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI);
        elevation = (float) (Math.acos(dy / radius) * 180.0 / Math.PI);
        
        updatePosition();
    }

    private void lookAt(float tx, float ty, float tz) {
        float posX = targetX + (float) (radius * Math.sin(elevation * Math.PI / 180.0) * Math.cos(azimuth * Math.PI / 180.0));
        float posY = targetY + (float) (radius * Math.cos(elevation * Math.PI / 180.0));
        float posZ = targetZ + (float) (radius * Math.sin(elevation * Math.PI / 180.0) * Math.sin(azimuth * Math.PI / 180.0));
        
        Matrix.setLookAtM(viewMatrix, 0, posX, posY, posZ, tx, ty, tz, 0, 1, 0);
        updateViewProj();
    }
    
    private void updateViewProj() {
        Matrix.multiplyMM(viewProjMatrix, 0, projMatrix, 0, viewMatrix, 0);
    }

    public float[] getViewProjMatrix() {
        return viewProjMatrix;
    }
    
    public float getPositionX() {
        float azRad = (float) (azimuth * Math.PI / 180.0);
        float elRad = (float) (elevation * Math.PI / 180.0);
        return targetX + (float) (radius * Math.sin(elRad) * Math.cos(azRad));
    }
    
    public float getPositionY() {
        float elRad = (float) (elevation * Math.PI / 180.0);
        return targetY + (float) (radius * Math.cos(elRad));
    }
    
    public float getPositionZ() {
        float azRad = (float) (azimuth * Math.PI / 180.0);
        float elRad = (float) (elevation * Math.PI / 180.0);
        return targetZ + (float) (radius * Math.sin(elRad) * Math.sin(azRad));
    }
}

