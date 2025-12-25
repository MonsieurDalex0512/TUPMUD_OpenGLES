package com.example.opengl_es.benchmark;

import com.example.opengl_es.renderer.MyGLRenderer;
import com.example.opengl_es.renderer.RenderConfig;

/**
 * Tests overdraw by rendering overlapping objects.
 */
public class OverdrawTest extends BenchmarkTest {
    
    private RenderConfig originalConfig;
    
    public OverdrawTest() {
        super("Overdraw", 
            "Measures performance impact of rendering overlapping geometry");
        this.durationSeconds = 3.0f;
    }
    
    @Override
    public void setup(MyGLRenderer renderer) {
        RenderConfig config = renderer.getRenderConfig();
        originalConfig = new RenderConfig();
        copyConfig(config, originalConfig);
        
        // Disable depth test to maximize overdraw
        // (This would be done in renderer, but for now just mark it)
        config.showOverdrawHeatmap = true;
    }
    
    @Override
    public BenchmarkResult run(MyGLRenderer renderer) {
        BenchmarkResult result = new BenchmarkResult(getName());
        result.unit = "overdraw ratio";
        
        com.example.opengl_es.monitoring.PerformanceMonitor pm = renderer.getPerformanceMonitor();
        result.averageFPS = pm.getFPS();
        result.averageFrameTimeMs = pm.getAverageFrameTime();
        result.overdrawRatio = pm.overdrawRatio;
        result.score = 100f - (result.overdrawRatio * 10f); // Lower overdraw = higher score
        
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





