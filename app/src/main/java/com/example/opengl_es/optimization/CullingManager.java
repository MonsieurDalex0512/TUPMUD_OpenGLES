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
    
    private boolean enableBackFaceCulling = true; // Mặc định bật để thấy impact
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
        List<Object3D> visible = new ArrayList<>(objects);
        
        // Frustum culling
        if (enableFrustumCulling) {
            visible = performFrustumCulling(visible, camera);
        }
        
        // Occlusion culling (most expensive, do last)
        if (enableOcclusionCulling) {
            visible = occlusionCulling.performOcclusionCulling(visible, camera);
        }
        
        return visible;
    }
    
    /**
     * Frustum culling based on bounding sphere test against frustum planes.
     */
    private List<Object3D> performFrustumCulling(List<Object3D> objects, Camera camera) {
        List<Object3D> visible = new ArrayList<>();
        
        // Extract frustum planes from view-projection matrix
        float[] viewProj = camera.getViewProjMatrix();
        float[][] planes = extractFrustumPlanes(viewProj);
        
        for (Object3D obj : objects) {
            // Test bounding sphere against frustum planes
            boolean inside = true;
            for (int i = 0; i < 6; i++) {
                float distance = planes[i][0] * obj.positionX + 
                                planes[i][1] * obj.positionY + 
                                planes[i][2] * obj.positionZ + 
                                planes[i][3];
                
                // If sphere is outside any plane, it's culled
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
    }
    
    /**
     * Extract 6 frustum planes from view-projection matrix.
     */
    private float[][] extractFrustumPlanes(float[] m) {
        float[][] planes = new float[6][4];
        
        // Left plane
        planes[0][0] = m[3] + m[0];
        planes[0][1] = m[7] + m[4];
        planes[0][2] = m[11] + m[8];
        planes[0][3] = m[15] + m[12];
        
        // Right plane
        planes[1][0] = m[3] - m[0];
        planes[1][1] = m[7] - m[4];
        planes[1][2] = m[11] - m[8];
        planes[1][3] = m[15] - m[12];
        
        // Bottom plane
        planes[2][0] = m[3] + m[1];
        planes[2][1] = m[7] + m[5];
        planes[2][2] = m[11] + m[9];
        planes[2][3] = m[15] + m[13];
        
        // Top plane
        planes[3][0] = m[3] - m[1];
        planes[3][1] = m[7] - m[5];
        planes[3][2] = m[11] - m[9];
        planes[3][3] = m[15] - m[13];
        
        // Near plane
        planes[4][0] = m[3] + m[2];
        planes[4][1] = m[7] + m[6];
        planes[4][2] = m[11] + m[10];
        planes[4][3] = m[15] + m[14];
        
        // Far plane
        planes[5][0] = m[3] - m[2];
        planes[5][1] = m[7] - m[6];
        planes[5][2] = m[11] - m[10];
        planes[5][3] = m[15] - m[14];
        
        // Normalize planes
        for (int i = 0; i < 6; i++) {
            float length = (float) Math.sqrt(
                planes[i][0] * planes[i][0] + 
                planes[i][1] * planes[i][1] + 
                planes[i][2] * planes[i][2]
            );
            if (length > 0.0001f) {
                planes[i][0] /= length;
                planes[i][1] /= length;
                planes[i][2] /= length;
                planes[i][3] /= length;
            }
        }
        
        return planes;
    }
}

