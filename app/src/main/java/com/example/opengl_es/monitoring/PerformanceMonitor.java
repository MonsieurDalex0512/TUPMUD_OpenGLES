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
        try {
            long frameEndNano = System.nanoTime();
            float frameTimeMs = (frameEndNano - frameStartNano) / 1_000_000.0f;
            
            // Check for invalid frame time
            if (Float.isNaN(frameTimeMs) || Float.isInfinite(frameTimeMs) || frameTimeMs < 0) {
                frameTimeMs = 16.67f; // Default to 60fps
            }
            
            // Limit frame time to reasonable range (0-1000ms)
            frameTimeMs = Math.max(0f, Math.min(1000f, frameTimeMs));
            
            synchronized (frameTimesMs) {
                frameTimesMs.add(frameTimeMs);
                if (frameTimesMs.size() > MAX_FRAME_HISTORY) {
                    frameTimesMs.remove(0);
                }
            }
            
            // Lưu giá trị của frame này để UI đọc (tránh đọc lúc đang reset)
            lastDrawCalls = drawCalls;
            lastTriangleCount = triangleCount;
            lastTextureBinds = textureBinds;
            lastShaderSwitches = shaderSwitches;
        } catch (Exception e) {
            android.util.Log.e("PerformanceMonitor", "Error in endFrame", e);
            e.printStackTrace();
        }
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
        try {
            if (frameTimesMs == null || frameTimesMs.isEmpty()) return 0f;
            
            // Create a copy to avoid concurrent modification
            List<Float> copy;
            synchronized (frameTimesMs) {
                copy = new ArrayList<>(frameTimesMs);
            }
            
            if (copy.isEmpty()) return 0f;
            
            float sum = 0f;
            for (float time : copy) {
                if (Float.isNaN(time) || Float.isInfinite(time) || time < 0) {
                    continue; // Skip invalid values
                }
                sum += time;
            }
            
            if (copy.size() == 0) return 0f;
            return sum / copy.size();
        } catch (Exception e) {
            android.util.Log.e("PerformanceMonitor", "Error in getAverageFrameTime", e);
            return 0f;
        }
    }
    
    public float getFrameTimeVariance() {
        try {
            if (frameTimesMs == null || frameTimesMs.size() < 2) return 0f;
            
            // Create a copy to avoid concurrent modification
            List<Float> copy;
            synchronized (frameTimesMs) {
                copy = new ArrayList<>(frameTimesMs);
            }
            
            if (copy.size() < 2) return 0f;
            
            float avg = getAverageFrameTime();
            if (Float.isNaN(avg) || Float.isInfinite(avg)) {
                return 0f;
            }
            
            float variance = 0f;
            for (float time : copy) {
                if (Float.isNaN(time) || Float.isInfinite(time)) {
                    continue; // Skip invalid values
                }
                float diff = time - avg;
                variance += diff * diff;
            }
            
            return variance / copy.size();
        } catch (Exception e) {
            android.util.Log.e("PerformanceMonitor", "Error in getFrameTimeVariance", e);
            return 0f;
        }
    }
    
    public float get1PercentLow() {
        try {
            return getFrameTimePercentile(0.99f);
        } catch (Exception e) {
            android.util.Log.e("PerformanceMonitor", "Error in get1PercentLow", e);
            return 0f;
        }
    }
    
    public float get0_1PercentLow() {
        try {
            return getFrameTimePercentile(0.999f);
        } catch (Exception e) {
            android.util.Log.e("PerformanceMonitor", "Error in get0_1PercentLow", e);
            return 0f;
        }
    }
    
    public float getFrameTimePercentile(float percentile) {
        try {
            if (frameTimesMs == null || frameTimesMs.isEmpty()) return 0f;
            
            // Create a copy to avoid concurrent modification
            List<Float> copy;
            synchronized (frameTimesMs) {
                copy = new ArrayList<>(frameTimesMs);
            }
            
            if (copy.isEmpty()) return 0f;
            
            List<Float> sorted = new ArrayList<>(copy);
            Collections.sort(sorted);
            int index = (int) (sorted.size() * percentile);
            index = Math.min(index, sorted.size() - 1);
            index = Math.max(0, index); // Ensure non-negative
            
            float result = sorted.get(index);
            if (Float.isNaN(result) || Float.isInfinite(result)) {
                return 0f;
            }
            return result;
        } catch (Exception e) {
            android.util.Log.e("PerformanceMonitor", "Error in getFrameTimePercentile", e);
            return 0f;
        }
    }
    
    public int getJankCount() {
        try {
            if (frameTimesMs == null || frameTimesMs.isEmpty()) return 0;
            
            // Create a copy to avoid concurrent modification
            List<Float> copy;
            synchronized (frameTimesMs) {
                copy = new ArrayList<>(frameTimesMs);
            }
            
            int count = 0;
            for (float time : copy) {
                if (!Float.isNaN(time) && !Float.isInfinite(time) && time > 16.67f) {
                    count++;
                }
            }
            return count;
        } catch (Exception e) {
            android.util.Log.e("PerformanceMonitor", "Error in getJankCount", e);
            return 0;
        }
    }
}

