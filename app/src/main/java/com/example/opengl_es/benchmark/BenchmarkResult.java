package com.example.opengl_es.benchmark;

import java.io.Serializable;

/**
 * Results from a single benchmark test run.
 */
public class BenchmarkResult implements Serializable {
    
    public String testName;
    public float averageFPS;
    public float minFPS;
    public float maxFPS;
    public float averageFrameTimeMs;
    public float minFrameTimeMs;
    public float maxFrameTimeMs;
    
    // Advanced metrics
    public int totalDrawCalls;
    public int totalTriangles;
    public float averageDrawCallsPerFrame;
    public float averageTrianglesPerFrame;
    
    // Test-specific metrics
    public float score; // Normalized score (0-100, higher is better)
    public String unit; // e.g., "triangles/sec", "fps", "ms"
    
    // Additional metrics for specific tests
    public int objectsCulled;
    public int objectsRendered;
    public int textureBinds;
    public int shaderSwitches;
    public float overdrawRatio;
    
    public BenchmarkResult(String testName) {
        this.testName = testName;
    }
    
    @Override
    public String toString() {
        return String.format(
            "%s: %.2f FPS (%.2f-%.2f), %.2f ms/frame (%.2f-%.2f), Score: %.2f %s",
            testName, averageFPS, minFPS, maxFPS,
            averageFrameTimeMs, minFrameTimeMs, maxFrameTimeMs,
            score, unit
        );
    }
}

