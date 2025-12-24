package com.example.opengl_es.scene;

import android.opengl.Matrix;

/**
 * Camera implementation: view + projection matrices.
 */
public class Camera {
    private final float[] viewMatrix = new float[16];
    private final float[] projMatrix = new float[16];
    private final float[] viewProjMatrix = new float[16];

    private float posX = 0f, posY = 2f, posZ = 15f; // Camera cao hơn và xa hơn để nhìn thấy scene

    public Camera() {
        setPerspective(45f, 1f, 0.1f, 100f);
        lookAt(0, 0, 0); // Nhìn về center của scene
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

    public void setPosition(float x, float y, float z) {
        posX = x;
        posY = y;
        posZ = z;
        lookAt(0, 0, 0);
    }

    public void lookAt(float tx, float ty, float tz) {
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
        return posX;
    }
    
    public float getPositionY() {
        return posY;
    }
    
    public float getPositionZ() {
        return posZ;
    }
}

