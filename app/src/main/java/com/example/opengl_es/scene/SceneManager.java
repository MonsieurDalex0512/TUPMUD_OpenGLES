package com.example.opengl_es.scene;

import java.util.ArrayList;
import java.util.List;

/**
 * Scene manager for 3D objects and camera.
 */
public class SceneManager {

    private final List<Object3D> objects = new ArrayList<>();
    private final Camera camera = new Camera();

    public SceneManager() {
        createSimpleScene();
    }

    private void createSimpleScene() {
        // Create a larger grid of cubes (10x10 = 100 objects) để thấy rõ impact của back-face culling
        for (int x = -5; x <= 4; x++) {
            for (int z = -5; z <= 4; z++) {
                Object3D obj = new Object3D();
                obj.positionX = x * 1.5f;
                obj.positionY = 0f;
                obj.positionZ = z * 1.5f;
                obj.mesh = Mesh.createCube();
                objects.add(obj);
            }
        }
        
        // Add more spheres (20 objects)
        for (int i = 0; i < 20; i++) {
            Object3D obj = new Object3D();
            obj.positionX = (float) (Math.random() * 15 - 7.5);
            obj.positionY = 2.0f;
            obj.positionZ = (float) (Math.random() * 15 - 7.5);
            obj.mesh = Mesh.createSphere(16);
            objects.add(obj);
        }
        
        // Add more pyramids (15 objects)
        for (int i = 0; i < 15; i++) {
            Object3D obj = new Object3D();
            obj.positionX = (float) (Math.random() * 15 - 7.5);
            obj.positionY = -1.0f;
            obj.positionZ = (float) (Math.random() * 15 - 7.5);
            obj.mesh = Mesh.createPyramid();
            objects.add(obj);
        }
        
        // Tổng cộng: 100 + 20 + 15 = 135 objects (đủ để thấy impact của back-face culling)
    }

    public List<Object3D> getObjects() {
        return objects;
    }

    public Camera getCamera() {
        return camera;
    }
}

