package com.example.opengl_es.optimization;

import android.opengl.GLES30;

import com.example.opengl_es.scene.Camera;
import com.example.opengl_es.scene.Object3D;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages frustum culling, back-face culling, and occlusion culling.
 */
public class CullingManager {
    
    private boolean enableBackFaceCulling = false;
    private boolean enableFrustumCulling = false;
    private boolean enableOcclusionCulling = false;
    
    private final OcclusionCulling occlusionCulling = new OcclusionCulling();
    
    /**
     * Set culling flags
     */
    public void setBackFaceCulling(boolean enable) {
        this.enableBackFaceCulling = enable;
        if (enable) {
            GLES30.glEnable(GLES30.GL_CULL_FACE);
            GLES30.glCullFace(GLES30.GL_BACK);
        } else {
            GLES30.glDisable(GLES30.GL_CULL_FACE);
        }
    }
    
    public void setFrustumCulling(boolean enable) {
        this.enableFrustumCulling = enable;
    }
    
    public void setOcclusionCulling(boolean enable) {
        this.enableOcclusionCulling = enable;
    }
    
    /**
     * Cull objects based on enabled techniques
     */
    public List<Object3D> cullObjects(List<Object3D> objects, Camera camera) {
        try {
            List<Object3D> visible = new ArrayList<>(objects);
            
            // Frustum culling
            if (enableFrustumCulling) {
                visible = performFrustumCulling(visible, camera);
                // Safety: if frustum culling returns empty and we had objects, use fallback
                if (visible.isEmpty() && !objects.isEmpty()) {
                    android.util.Log.w("CullingManager", "Frustum culling culled all objects, using distance-based fallback");
                    visible = performDistanceBasedCulling(objects, camera);
                }
            }
            
            // Occlusion culling (most expensive, do last)
            // Culls objects that are far from camera (likely occluded by closer objects)
            if (enableOcclusionCulling) {
                try {
                    if (occlusionCulling != null && camera != null) {
                        visible = occlusionCulling.performOcclusionCulling(visible, camera);
                        
                        // Safety: if occlusion culling returns empty and we had objects, keep some
                        if (visible.isEmpty() && !objects.isEmpty()) {
                            android.util.Log.w("CullingManager", "Occlusion culling culled all objects, keeping original");
                            visible = new ArrayList<>(objects);
                        }
                    } else {
                        android.util.Log.w("CullingManager", "OcclusionCulling or Camera is null, skipping");
                    }
                } catch (Throwable e) { // Catch cả Error và Exception
                    android.util.Log.e("CullingManager", "Error in occlusion culling", e);
                    e.printStackTrace();
                    // Keep current visible list on error - don't crash
                    android.util.Log.w("CullingManager", "Occlusion culling failed, using current visible list");
                }
            }
            
            return visible;
        } catch (Exception e) {
            android.util.Log.e("CullingManager", "Error in cullObjects", e);
            e.printStackTrace();
            // Return all objects on error to prevent crash
            return objects;
        }
    }
    
    /**
     * Fallback: Simple distance-based culling if frustum culling fails.
     * This is less accurate but safer.
     */
    private List<Object3D> performDistanceBasedCulling(List<Object3D> objects, Camera camera) {
        List<Object3D> visible = new ArrayList<>();
        float maxDistance = 50.0f; // Maximum render distance
        
        for (Object3D obj : objects) {
            if (obj == null) continue;
            
            float distSq = obj.getDistanceSquared(
                camera.getPositionX(),
                camera.getPositionY(),
                camera.getPositionZ()
            );
            
            if (distSq < maxDistance * maxDistance) {
                visible.add(obj);
            }
        }
        
        return visible;
    }
    
    /**
     * Frustum culling based on bounding sphere test against frustum planes.
     */
    private List<Object3D> performFrustumCulling(List<Object3D> objects, Camera camera) {
        try {
            List<Object3D> visible = new ArrayList<>();
            
            // Safety check: ensure camera and matrix are valid
            if (camera == null) {
                android.util.Log.w("CullingManager", "Camera is null, skipping frustum culling");
                return objects; // Return all objects if camera invalid
            }
            
            // Extract frustum planes from view-projection matrix
            float[] viewProj = camera.getViewProjMatrix();
            if (viewProj == null || viewProj.length < 16) {
                android.util.Log.w("CullingManager", "View-projection matrix is invalid, skipping frustum culling");
                return objects; // Return all objects if matrix invalid
            }
            
            float[][] planes = extractFrustumPlanes(viewProj);
            if (planes == null) {
                android.util.Log.w("CullingManager", "Failed to extract frustum planes, skipping frustum culling");
                return objects; // Return all objects if extraction failed
            }
            
            for (Object3D obj : objects) {
                if (obj == null) continue;
                
                // Test bounding sphere against frustum planes
                boolean inside = true;
                for (int i = 0; i < 6; i++) {
                    // Calculate signed distance from object center to plane
                    float distance = planes[i][0] * obj.positionX + 
                                    planes[i][1] * obj.positionY + 
                                    planes[i][2] * obj.positionZ + 
                                    planes[i][3];
                    
                    // If sphere is outside any plane (distance + radius < 0), it's culled
                    if (distance < -obj.boundingRadius) {
                        inside = false;
                        break;
                    }
                }
                
                if (inside) {
                    visible.add(obj);
                }
            }
            
            return visible;
        } catch (Exception e) {
            android.util.Log.e("CullingManager", "Error in performFrustumCulling", e);
            e.printStackTrace();
            // Return all objects on error to prevent crash
            return objects;
        }
    }
    
    /**
     * Extract 6 frustum planes from view-projection matrix.
     * OpenGL ES uses column-major order matrices.
     * Formula based on: http://www8.cs.umu.se/kurser/5DV051/HT12/lab/plane_extraction.pdf
     */
    private float[][] extractFrustumPlanes(float[] m) {
        try {
            if (m == null || m.length < 16) {
                android.util.Log.e("CullingManager", "Matrix is null or invalid (length: " + 
                    (m != null ? m.length : 0) + ")");
                return null;
            }
            
            float[][] planes = new float[6][4];
            
            // Extract frustum planes from view-projection matrix (column-major)
            // Matrix layout: m[0-3]=col0, m[4-7]=col1, m[8-11]=col2, m[12-15]=col3
            
            // Left plane: (m[3] + m[0], m[7] + m[4], m[11] + m[8], m[15] + m[12])
            planes[0][0] = m[3] + m[0];
            planes[0][1] = m[7] + m[4];
            planes[0][2] = m[11] + m[8];
            planes[0][3] = m[15] + m[12];
            
            // Right plane: (m[3] - m[0], m[7] - m[4], m[11] - m[8], m[15] - m[12])
            planes[1][0] = m[3] - m[0];
            planes[1][1] = m[7] - m[4];
            planes[1][2] = m[11] - m[8];
            planes[1][3] = m[15] - m[12];
            
            // Bottom plane: (m[3] + m[1], m[7] + m[5], m[11] + m[9], m[15] + m[13])
            planes[2][0] = m[3] + m[1];
            planes[2][1] = m[7] + m[5];
            planes[2][2] = m[11] + m[9];
            planes[2][3] = m[15] + m[13];
            
            // Top plane: (m[3] - m[1], m[7] - m[5], m[11] - m[9], m[15] - m[13])
            planes[3][0] = m[3] - m[1];
            planes[3][1] = m[7] - m[5];
            planes[3][2] = m[11] - m[9];
            planes[3][3] = m[15] - m[13];
            
            // Near plane: (m[3] + m[2], m[7] + m[6], m[11] + m[10], m[15] + m[14])
            planes[4][0] = m[3] + m[2];
            planes[4][1] = m[7] + m[6];
            planes[4][2] = m[11] + m[10];
            planes[4][3] = m[15] + m[14];
            
            // Far plane: (m[3] - m[2], m[7] - m[6], m[11] - m[10], m[15] - m[14])
            planes[5][0] = m[3] - m[2];
            planes[5][1] = m[7] - m[6];
            planes[5][2] = m[11] - m[10];
            planes[5][3] = m[15] - m[14];
            
            // Normalize planes (normalize the plane normal vector)
            for (int i = 0; i < 6; i++) {
                float length = (float) Math.sqrt(
                    planes[i][0] * planes[i][0] + 
                    planes[i][1] * planes[i][1] + 
                    planes[i][2] * planes[i][2]
                );
                
                // Avoid division by zero
                if (length > 0.0001f) {
                    float invLength = 1.0f / length;
                    planes[i][0] *= invLength;
                    planes[i][1] *= invLength;
                    planes[i][2] *= invLength;
                    planes[i][3] *= invLength;
                } else {
                    // If plane is invalid, log warning and use default plane
                    android.util.Log.w("CullingManager", "Invalid plane " + i + ", length: " + length);
                    // Set to a default plane that won't cull anything (far plane pointing away)
                    planes[i][0] = 0;
                    planes[i][1] = 0;
                    planes[i][2] = -1; // Pointing away from camera
                    planes[i][3] = -1000; // Very far away
                }
            }
            
            return planes;
        } catch (Exception e) {
            android.util.Log.e("CullingManager", "Error in extractFrustumPlanes", e);
            e.printStackTrace();
            return null;
        }
    }
}

