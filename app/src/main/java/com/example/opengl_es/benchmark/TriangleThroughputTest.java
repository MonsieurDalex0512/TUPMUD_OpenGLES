package com.example.opengl_es.benchmark;

import com.example.opengl_es.renderer.MyGLRenderer;
import com.example.opengl_es.renderer.RenderConfig;
import com.example.opengl_es.scene.Object3D;
import com.example.opengl_es.scene.SceneManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests triangle rendering throughput by increasing object count.
 */
public class TriangleThroughputTest extends BenchmarkTest {
    
    private List<Object3D> originalObjects;
    private RenderConfig originalConfig;
    
    public TriangleThroughputTest() {
        super("Triangle Throughput", 
            "Measures FPS while rendering increasing numbers of triangles");
        this.durationSeconds = 3.0f;
    }
    
    @Override
    public void setup(MyGLRenderer renderer) {
        // Save original state
        SceneManager sceneManager = renderer.getSceneManager();
        originalObjects = new ArrayList<>(sceneManager.getObjects());
        
        RenderConfig config = renderer.getRenderConfig();
        originalConfig = new RenderConfig();
        copyConfig(config, originalConfig);
        
        // Optimize for throughput test
        config.enableBackfaceCulling = true;
        config.enableFrustumCulling = false; // Disable to test pure throughput
        config.enableLOD = false;
        
        // Create many objects (grid 20x20 = 400 objects)
        sceneManager.getObjects().clear();
        for (int x = -10; x < 10; x++) {
            for (int z = -10; z < 10; z++) {
                Object3D obj = new Object3D();
                obj.positionX = x * 1.5f;
                obj.positionY = 0f;
                obj.positionZ = z * 1.5f;
                obj.mesh = com.example.opengl_es.scene.Mesh.createCube();
                sceneManager.getObjects().add(obj);
            }
        }
    }
    
    @Override
    public BenchmarkResult run(MyGLRenderer renderer) {
        // Results are collected by BenchmarkRunner
        BenchmarkResult result = new BenchmarkResult(getName());
        result.unit = "triangles/sec";
        
        // Calculate triangle throughput
        com.example.opengl_es.monitoring.PerformanceMonitor pm = renderer.getPerformanceMonitor();
        float avgFps = pm.getFPS();
        int avgTriangles = pm.triangleCount;
        
        if (avgFps > 0 && avgTriangles > 0) {
            result.averageFPS = avgFps;
            result.averageTrianglesPerFrame = avgTriangles;
            result.score = (avgFps * avgTriangles) / 10000f; // Normalize
        }
        
        return result;
    }
    
    @Override
    public void cleanup(MyGLRenderer renderer) {
        // Restore original scene
        SceneManager sceneManager = renderer.getSceneManager();
        sceneManager.getObjects().clear();
        sceneManager.getObjects().addAll(originalObjects);
        
        // Restore config
        copyConfig(originalConfig, renderer.getRenderConfig());
    }
    
    private void copyConfig(RenderConfig src, RenderConfig dst) {
        dst.useETC1Compression = src.useETC1Compression;
        dst.useMipmaps = src.useMipmaps;
        dst.useTextureAtlas = src.useTextureAtlas;
        dst.enableBackfaceCulling = src.enableBackfaceCulling;
        dst.enableFrustumCulling = src.enableFrustumCulling;
        dst.enableOcclusionCulling = src.enableOcclusionCulling;
        dst.enableLOD = src.enableLOD;
        dst.enableInstancing = src.enableInstancing;
        dst.enableDepthPrePass = src.enableDepthPrePass;
        dst.showOverdrawHeatmap = src.showOverdrawHeatmap;
    }
}




