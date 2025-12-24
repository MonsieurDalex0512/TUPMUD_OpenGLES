package com.example.opengl_es.benchmark;

import com.example.opengl_es.renderer.MyGLRenderer;

/**
 * Base class for all benchmark tests.
 * Each test runs for a specified duration and measures performance metrics.
 */
public abstract class BenchmarkTest {
    
    protected String name;
    protected String description;
    protected float durationSeconds = 5.0f; // Default 5 seconds
    
    public BenchmarkTest(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    /**
     * Setup before running the benchmark (e.g., configure renderer state)
     */
    public abstract void setup(MyGLRenderer renderer);
    
    /**
     * Run the benchmark and collect results.
     * This is called on the GL thread.
     */
    public abstract BenchmarkResult run(MyGLRenderer renderer);
    
    /**
     * Cleanup after benchmark (restore renderer state)
     */
    public abstract void cleanup(MyGLRenderer renderer);
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public float getDurationSeconds() {
        return durationSeconds;
    }
    
    public void setDurationSeconds(float seconds) {
        this.durationSeconds = seconds;
    }
}

