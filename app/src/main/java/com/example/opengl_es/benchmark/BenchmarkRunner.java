package com.example.opengl_es.benchmark;

import android.opengl.GLSurfaceView;
import com.example.opengl_es.renderer.MyGLRenderer;
import com.example.opengl_es.monitoring.PerformanceMonitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to run benchmarks on the GL thread.
 */
public class BenchmarkRunner {
    
    private final MyGLRenderer renderer;
    private final GLSurfaceView glSurfaceView;
    
    public BenchmarkRunner(MyGLRenderer renderer, GLSurfaceView glSurfaceView) {
        this.renderer = renderer;
        this.glSurfaceView = glSurfaceView;
    }
    
    /**
     * Run a single benchmark test on the GL thread.
     */
    public BenchmarkResult runTest(BenchmarkTest test) {
        // Setup
        glSurfaceView.queueEvent(() -> test.setup(renderer));
        
        // Wait for setup to complete
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Run benchmark
        PerformanceMonitor pm = renderer.getPerformanceMonitor();
        pm.beginFrame();
        
        long startTime = System.nanoTime();
        long endTime = startTime + (long)(test.getDurationSeconds() * 1_000_000_000L);
        
        List<Float> fpsSamples = new ArrayList<>();
        List<Float> frameTimeSamples = new ArrayList<>();
        List<Integer> drawCallSamples = new ArrayList<>();
        List<Integer> triangleSamples = new ArrayList<>();
        
        // Collect samples during benchmark duration
        while (System.nanoTime() < endTime) {
            // Trigger a frame render
            glSurfaceView.requestRender();
            
            // Wait a bit
            try {
                Thread.sleep(16); // ~60fps sampling rate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            
            // Collect metrics
            float fps = pm.getFPS();
            float frameTime = pm.getAverageFrameTime();
            if (fps > 0 && frameTime > 0) {
                fpsSamples.add(fps);
                frameTimeSamples.add(frameTime);
                drawCallSamples.add(pm.getDrawCalls());
                triangleSamples.add(pm.getTriangleCount());
            }
        }
        
        // Calculate results
        BenchmarkResult result = new BenchmarkResult(test.getName());
        
        if (!fpsSamples.isEmpty()) {
            float sumFps = 0f, sumFrameTime = 0f;
            float minFps = Float.MAX_VALUE, maxFps = 0f;
            float minFrameTime = Float.MAX_VALUE, maxFrameTime = 0f;
            int sumDrawCalls = 0, sumTriangles = 0;
            
            for (int i = 0; i < fpsSamples.size(); i++) {
                float fps = fpsSamples.get(i);
                float frameTime = frameTimeSamples.get(i);
                
                sumFps += fps;
                sumFrameTime += frameTime;
                minFps = Math.min(minFps, fps);
                maxFps = Math.max(maxFps, fps);
                minFrameTime = Math.min(minFrameTime, frameTime);
                maxFrameTime = Math.max(maxFrameTime, frameTime);
                
                sumDrawCalls += drawCallSamples.get(i);
                sumTriangles += triangleSamples.get(i);
            }
            
            result.averageFPS = sumFps / fpsSamples.size();
            result.minFPS = minFps;
            result.maxFPS = maxFps;
            result.averageFrameTimeMs = sumFrameTime / frameTimeSamples.size();
            result.minFrameTimeMs = minFrameTime;
            result.maxFrameTimeMs = maxFrameTime;
            result.averageDrawCallsPerFrame = (float) sumDrawCalls / fpsSamples.size();
            result.averageTrianglesPerFrame = (float) sumTriangles / fpsSamples.size();
            result.totalDrawCalls = sumDrawCalls;
            result.totalTriangles = sumTriangles;
            
            // Score is normalized FPS (0-100)
            result.score = Math.min(100f, result.averageFPS / 60f * 100f);
            result.unit = "fps";
        }
        
        // Cleanup
        glSurfaceView.queueEvent(() -> test.cleanup(renderer));
        
        return result;
    }
}

