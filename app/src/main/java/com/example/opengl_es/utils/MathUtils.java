package com.example.opengl_es.utils;

/**
 * BƯỚC 3: MATH UTILITIES
 * Các hàm toán học hỗ trợ cho LOD và culling
 */
public class MathUtils {
    
    /**
     * BƯỚC 3: Tính khoảng cách giữa hai điểm 3D
     * Dùng cho LOD calculation
     */
    public static float distance(float x1, float y1, float z1, 
                                  float x2, float y2, float z2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Normalize a 3D vector
     */
    public static void normalize(float[] vec, int offset) {
        float len = (float) Math.sqrt(
            vec[offset] * vec[offset] + 
            vec[offset + 1] * vec[offset + 1] + 
            vec[offset + 2] * vec[offset + 2]
        );
        if (len > 0.0001f) {
            vec[offset] /= len;
            vec[offset + 1] /= len;
            vec[offset + 2] /= len;
        }
    }
    
    /**
     * Clamp value between min and max
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Convert degrees to radians
     */
    public static float toRadians(float degrees) {
        return (float) (degrees * Math.PI / 180.0);
    }
}

