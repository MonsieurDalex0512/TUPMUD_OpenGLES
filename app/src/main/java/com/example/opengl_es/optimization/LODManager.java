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
        try {
            // Safety checks
            if (obj == null || camera == null) {
                android.util.Log.w("LODManager", "Object or Camera is null, returning LOD level 0");
                return 0; // Default to high detail
            }
            
            // Get camera position safely
            float camX, camY, camZ;
            try {
                camX = camera.getPositionX();
                camY = camera.getPositionY();
                camZ = camera.getPositionZ();
            } catch (Exception e) {
                android.util.Log.w("LODManager", "Error getting camera position, using defaults", e);
                camX = 0f;
                camY = 2f;
                camZ = 15f; // Default camera position
            }
            
            // Calculate distance safely
            float dist;
            try {
                dist = MathUtils.distance(
                    obj.positionX, obj.positionY, obj.positionZ,
                    camX, camY, camZ
                );
                
                // Check for invalid values
                if (Float.isNaN(dist) || Float.isInfinite(dist) || dist < 0) {
                    android.util.Log.w("LODManager", "Invalid distance calculated, using default LOD");
                    return 0; // Default to high detail
                }
            } catch (Exception e) {
                android.util.Log.w("LODManager", "Error calculating distance, using default LOD", e);
                return 0; // Default to high detail
            }
            
            // Determine LOD level based on distance
            if (dist < lodDistance0) {
                return 0; // High detail - nhiều triangles
            } else if (dist < lodDistance1) {
                return 1; // Medium detail
            } else if (dist < lodDistance2) {
                return 2; // Low detail - ít triangles
            } else {
                return 3; // Very low detail (hoặc cull)
            }
        } catch (Throwable e) {
            android.util.Log.e("LODManager", "Critical error in calculateLOD", e);
            e.printStackTrace();
            return 0; // Default to high detail on error
        }
    }
    
    /**
     * BƯỚC 3: Lấy mesh phù hợp với LOD level
     * Objects ở xa → mesh đơn giản hơn → ít triangles hơn
     */
    public Mesh getMeshForLOD(Object3D obj, Camera camera) {
        try {
            // Safety checks
            if (obj == null || camera == null) {
                android.util.Log.w("LODManager", "Object or Camera is null, returning default cube mesh");
                return Mesh.createCube(); // Return default mesh
            }
            
            int lodLevel = calculateLOD(obj, camera);
            
            // Store LOD level for statistics (safely)
            try {
                objectLODLevels.put(obj, lodLevel);
            } catch (Exception e) {
                android.util.Log.w("LODManager", "Error storing LOD level", e);
            }
            
            // Return appropriate mesh based on LOD
            Mesh mesh = null;
            try {
                switch (lodLevel) {
                    case 0:
                        // Gần camera: High detail (nhiều segments)
                        mesh = Mesh.createSphere(32);
                        break;
                    case 1:
                        // Trung bình: Medium detail
                        mesh = Mesh.createSphere(16);
                        break;
                    case 2:
                        // Xa: Low detail (ít segments)
                        mesh = Mesh.createCube(); // Cube đơn giản nhất
                        break;
                    default:
                        // Quá xa → return cube (don't return null to avoid crash)
                        mesh = Mesh.createCube();
                        break;
                }
            } catch (Exception e) {
                android.util.Log.e("LODManager", "Error creating mesh for LOD level " + lodLevel, e);
                // Fallback to cube if mesh creation fails
                mesh = Mesh.createCube();
            }
            
            // Safety: never return null
            if (mesh == null) {
                android.util.Log.w("LODManager", "Mesh is null, returning default cube");
                return Mesh.createCube();
            }
            
            return mesh;
        } catch (Throwable e) {
            android.util.Log.e("LODManager", "Critical error in getMeshForLOD", e);
            e.printStackTrace();
            // Return default mesh on error
            try {
                return Mesh.createCube();
            } catch (Exception e2) {
                android.util.Log.e("LODManager", "Error creating fallback mesh", e2);
                return null; // Last resort - but should never happen
            }
        }
    }
    
    /**
     * Get number of objects at each LOD level (for statistics)
     */
    public int getObjectCountAtLOD(int lodLevel) {
        try {
            if (objectLODLevels == null) {
                return 0;
            }
            
            int count = 0;
            for (Integer level : objectLODLevels.values()) {
                if (level != null && level == lodLevel) {
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            android.util.Log.w("LODManager", "Error getting object count at LOD level " + lodLevel, e);
            return 0;
        }
    }
    
    public void setLODDistances(float d0, float d1, float d2) {
        try {
            this.lodDistance0 = d0;
            this.lodDistance1 = d1;
            this.lodDistance2 = d2;
        } catch (Exception e) {
            android.util.Log.e("LODManager", "Error setting LOD distances", e);
        }
    }
    
    /**
     * Clear LOD statistics (safe to call anytime)
     */
    public void clearStatistics() {
        try {
            if (objectLODLevels != null) {
                objectLODLevels.clear();
            }
        } catch (Exception e) {
            android.util.Log.w("LODManager", "Error clearing statistics", e);
        }
    }
}

