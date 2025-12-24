package com.example.opengl_es.monitoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Real-time performance monitoring with frame timing, FPS, and advanced metrics.
 */
public class PerformanceMonitor {
    
    // Frame timing
    private long frameStartNano;
    private final List<Float> frameTimesMs = new ArrayList<>();
    private static final int MAX_FRAME_HISTORY = 120; // 2 seconds at 60fps
    
    // Rendering metrics (current frame)
    public int drawCalls = 0;
    public int triangleCount = 0;
    public int textureBinds = 0;
    public int shaderSwitches = 0;
    
    // Last frame values (for UI display to avoid reading during reset)
    private int lastDrawCalls = 0;
    private int lastTriangleCount = 0;
    private int lastTextureBinds = 0;
    private int lastShaderSwitches = 0;
    
    // Memory tracking
    public long textureMemoryBytes = 0;
    public long vboMemoryBytes = 0;
    
    // Advanced metrics
    public float overdrawRatio = 1.0f;
    public int objectsCulled = 0;
    public int objectsRendered = 0;
    
    public void beginFrame() {
        frameStartNano = System.nanoTime();
    }
    
    public void endFrame() {
        long frameEndNano = System.nanoTime();
        float frameTimeMs = (frameEndNano - frameStartNano) / 1_000_000.0f;
        
        frameTimesMs.add(frameTimeMs);
        if (frameTimesMs.size() > MAX_FRAME_HISTORY) {
            frameTimesMs.remove(0);
        }
        
        // Lưu giá trị của frame này để UI đọc (tránh đọc lúc đang reset)
        lastDrawCalls = drawCalls;
        lastTriangleCount = triangleCount;
        lastTextureBinds = textureBinds;
        lastShaderSwitches = shaderSwitches;
    }
    
    // Getters để UI đọc giá trị an toàn (không bị 0 khi đang reset)
    public int getDrawCalls() {
        return lastDrawCalls;
    }
    
    public int getTriangleCount() {
        return lastTriangleCount;
    }
    
    public int getTextureBinds() {
        return lastTextureBinds;
    }
    
    public int getShaderSwitches() {
        return lastShaderSwitches;
    }
    
    public float getFPS() {
        if (frameTimesMs.isEmpty()) return 0f;
        float avgFrameTime = getAverageFrameTime();
        return avgFrameTime > 0 ? 1000f / avgFrameTime : 0f;
    }
    
    public float getAverageFrameTime() {
        if (frameTimesMs.isEmpty()) return 0f;
        float sum = 0f;
        for (float time : frameTimesMs) {
            sum += time;
        }
        return sum / frameTimesMs.size();
    }
    
    public float getFrameTimeVariance() {
        if (frameTimesMs.size() < 2) return 0f;
        float avg = getAverageFrameTime();
        float variance = 0f;
        for (float time : frameTimesMs) {
            float diff = time - avg;
            variance += diff * diff;
        }
        return variance / frameTimesMs.size();
    }
    
    public float get1PercentLow() {
        return getFrameTimePercentile(0.99f);
    }
    
    public float get0_1PercentLow() {
        return getFrameTimePercentile(0.999f);
    }
    
    public float getFrameTimePercentile(float percentile) {
        if (frameTimesMs.isEmpty()) return 0f;
        List<Float> sorted = new ArrayList<>(frameTimesMs);
        Collections.sort(sorted);
        int index = (int) (sorted.size() * percentile);
        index = Math.min(index, sorted.size() - 1);
        return sorted.get(index);
    }
    
    public int getJankCount() {
        int count = 0;
        for (float time : frameTimesMs) {
            if (time > 16.67f) { // Missed 60fps
                count++;
            }
        }
        return count;
    }
}

