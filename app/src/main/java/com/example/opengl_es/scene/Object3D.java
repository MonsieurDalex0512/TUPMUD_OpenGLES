package com.example.opengl_es.scene;

/**
 * 3D object representation with mesh reference.
 */
public class Object3D {
    public float positionX;
    public float positionY;
    public float positionZ;

    public float rotationX;
    public float rotationY;
    public float rotationZ;
    
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
    public float scaleZ = 1.0f;
    
    public Mesh mesh;
    public int textureId;
    
    // Bounding sphere for culling
    public float boundingRadius = 0.866f; // sqrt(3) * 0.5 for unit cube

    public Object3D() {
        // Default to cube mesh
        this.mesh = Mesh.createCube();
    }
    
    public Object3D(Mesh mesh) {
        this.mesh = mesh;
    }

    public void update(float deltaTimeSec) {
        // Simple idle rotation to make performance changes visible
        // Giới hạn rotation để tránh overflow (0-360 độ)
        rotationY += 20f * deltaTimeSec;
        if (rotationY >= 360f) {
            rotationY -= 360f;
        }
    }
    
    public float getDistanceSquared(float x, float y, float z) {
        float dx = positionX - x;
        float dy = positionY - y;
        float dz = positionZ - z;
        return dx * dx + dy * dy + dz * dz;
    }
}

