package com.example.opengl_es.benchmark;

import com.example.opengl_es.renderer.MyGLRenderer;
import com.example.opengl_es.renderer.RenderConfig;

/**
 * Tests texture fill rate by rendering large textured quads.
 */
public class TextureFillRateTest extends BenchmarkTest {
    
    private RenderConfig originalConfig;
    
    public TextureFillRateTest() {
        super("Texture Fill Rate", 
            "Measures performance when rendering large textured surfaces");
        this.durationSeconds = 3.0f;
    }
    
    @Override
    public void setup(MyGLRenderer renderer) {
        RenderConfig config = renderer.getRenderConfig();
        originalConfig = new RenderConfig();
        copyConfig(config, originalConfig);
        
        // Optimize for texture rendering
        config.useMipmaps = true;
        config.enableBackfaceCulling = true;
    }
    
    @Override
    public BenchmarkResult run(MyGLRenderer renderer) {
        BenchmarkResult result = new BenchmarkResult(getName());
        result.unit = "pixels/sec";
        
        com.example.opengl_es.monitoring.PerformanceMonitor pm = renderer.getPerformanceMonitor();
        result.averageFPS = pm.getFPS();
        result.averageFrameTimeMs = pm.getAverageFrameTime();
        result.score = result.averageFPS / 60f * 100f;
        
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

