package com.example.opengl_es.benchmark;

import com.example.opengl_es.renderer.MyGLRenderer;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates all benchmark tests and collects results.
 */
public class BenchmarkSuite {
    
    private List<BenchmarkTest> tests = new ArrayList<>();
    private BenchmarkRunner runner;
    
    public BenchmarkSuite(MyGLRenderer renderer, GLSurfaceView glSurfaceView) {
        this.runner = new BenchmarkRunner(renderer, glSurfaceView);
        
        // Add all benchmark tests
        tests.add(new TriangleThroughputTest());
        tests.add(new TextureFillRateTest());
        tests.add(new ShaderComplexityTest());
        tests.add(new CullingEffectivenessTest());
        tests.add(new OverdrawTest());
        tests.add(new MemoryBandwidthTest());
    }
    
    /**
     * Run all benchmark tests and return results.
     */
    public BenchmarkResults runAll() {
        BenchmarkResults results = new BenchmarkResults();
        
        for (BenchmarkTest test : tests) {
            try {
                BenchmarkResult result = runner.runTest(test);
                results.addResult(result);
            } catch (Exception e) {
                android.util.Log.e("BenchmarkSuite", "Test failed: " + test.getName(), e);
            }
        }
        
        return results;
    }
    
    /**
     * Run a specific test by name.
     */
    public BenchmarkResult runTest(String testName) {
        for (BenchmarkTest test : tests) {
            if (test.getName().equals(testName)) {
                return runner.runTest(test);
            }
        }
        return null;
    }
    
    public List<BenchmarkTest> getTests() {
        return tests;
    }
}

