package com.example.opengl_es.benchmark;

import com.example.opengl_es.renderer.MyGLRenderer;
import com.example.opengl_es.renderer.RenderConfig;

/**
 * Tests memory bandwidth by rendering many different meshes/textures.
 */
public class MemoryBandwidthTest extends BenchmarkTest {
    
    private RenderConfig originalConfig;
    
    public MemoryBandwidthTest() {
        super("Memory Bandwidth", 
            "Measures performance when frequently switching meshes/textures");
        this.durationSeconds = 3.0f;
    }
    
    @Override
    public void setup(MyGLRenderer renderer) {
        RenderConfig config = renderer.getRenderConfig();
        originalConfig = new RenderConfig();
        copyConfig(config, originalConfig);
        
        // Force frequent texture/mesh switches
        config.useTextureAtlas = false; // More texture switches
    }
    
    @Override
    public BenchmarkResult run(MyGLRenderer renderer) {
        BenchmarkResult result = new BenchmarkResult(getName());
        result.unit = "mb/s";
        
        com.example.opengl_es.monitoring.PerformanceMonitor pm = renderer.getPerformanceMonitor();
        result.averageFPS = pm.getFPS();
        result.averageFrameTimeMs = pm.getAverageFrameTime();
        result.textureBinds = pm.textureBinds;
        result.shaderSwitches = pm.shaderSwitches;
        
        // Estimate memory bandwidth (simplified)
        float estimatedBandwidth = (pm.textureMemoryBytes + pm.vboMemoryBytes) * pm.getFPS() / (1024f * 1024f);
        result.score = Math.min(100f, estimatedBandwidth / 100f); // Normalize
        
        return result;
    }
    
    @Override
    public void cleanup(MyGLRenderer renderer) {
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

