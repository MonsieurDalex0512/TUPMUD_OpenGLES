package com.example.opengl_es.optimization;

import com.example.opengl_es.scene.Camera;
import com.example.opengl_es.scene.Mesh;
import com.example.opengl_es.scene.Object3D;
import com.example.opengl_es.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * BƯỚC 3: LEVEL OF DETAIL (LOD) MANAGER
 * Các vật thể ở xa nên được vẽ bằng ít đa giác hơn
 */
public class LODManager {
    
    private final Map<Object3D, Integer> objectLODLevels = new HashMap<>();
    
    // LOD distance thresholds (có thể điều chỉnh)
    private float lodDistance0 = 5.0f;   // High detail
    private float lodDistance1 = 15.0f;  // Medium detail
    private float lodDistance2 = 30.0f;  // Low detail
    
    /**
     * BƯỚC 3: Tính toán LOD level dựa trên khoảng cách từ camera
     */
    public int calculateLOD(Object3D obj, Camera camera) {
        float dist = MathUtils.distance(
            obj.positionX, obj.positionY, obj.positionZ,
            camera.getPositionX(), camera.getPositionY(), camera.getPositionZ()
        );
        
        if (dist < lodDistance0) {
            return 0; // High detail - nhiều triangles
        } else if (dist < lodDistance1) {
            return 1; // Medium detail
        } else if (dist < lodDistance2) {
            return 2; // Low detail - ít triangles
        } else {
            return 3; // Very low detail (hoặc cull)
        }
    }
    
    /**
     * BƯỚC 3: Lấy mesh phù hợp với LOD level
     * Objects ở xa → mesh đơn giản hơn → ít triangles hơn
     */
    public Mesh getMeshForLOD(Object3D obj, Camera camera) {
        int lodLevel = calculateLOD(obj, camera);
        
        // Store LOD level for statistics
        objectLODLevels.put(obj, lodLevel);
        
        // Return appropriate mesh based on LOD
        switch (lodLevel) {
            case 0:
                // Gần camera: High detail (nhiều segments)
                return Mesh.createSphere(32); // Hoặc cube với nhiều triangles
            case 1:
                // Trung bình: Medium detail
                return Mesh.createSphere(16);
            case 2:
                // Xa: Low detail (ít segments)
                return Mesh.createCube(); // Cube đơn giản nhất
            default:
                return null; // Quá xa → cull
        }
    }
    
    /**
     * Get number of objects at each LOD level (for statistics)
     */
    public int getObjectCountAtLOD(int lodLevel) {
        int count = 0;
        for (Integer level : objectLODLevels.values()) {
            if (level == lodLevel) {
                count++;
            }
        }
        return count;
    }
    
    public void setLODDistances(float d0, float d1, float d2) {
        this.lodDistance0 = d0;
        this.lodDistance1 = d1;
        this.lodDistance2 = d2;
    }
}

