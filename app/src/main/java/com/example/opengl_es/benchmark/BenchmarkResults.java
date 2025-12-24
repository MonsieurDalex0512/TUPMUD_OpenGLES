package com.example.opengl_es.benchmark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for all benchmark test results.
 */
public class BenchmarkResults implements Serializable {
    
    private List<BenchmarkResult> results = new ArrayList<>();
    private long timestamp;
    private String deviceInfo;
    
    public BenchmarkResults() {
        this.timestamp = System.currentTimeMillis();
        this.deviceInfo = android.os.Build.MODEL + " (" + android.os.Build.VERSION.RELEASE + ")";
    }
    
    public void addResult(BenchmarkResult result) {
        results.add(result);
    }
    
    public List<BenchmarkResult> getResults() {
        return results;
    }
    
    public float getOverallScore() {
        if (results.isEmpty()) return 0f;
        float sum = 0f;
        for (BenchmarkResult r : results) {
            sum += r.score;
        }
        return sum / results.size();
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getDeviceInfo() {
        return deviceInfo;
    }
}

