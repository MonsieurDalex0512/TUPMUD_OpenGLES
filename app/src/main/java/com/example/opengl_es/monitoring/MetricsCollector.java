package com.example.opengl_es.monitoring;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects performance metrics over time for comparison and analysis.
 */
public class MetricsCollector {
    
    private List<MetricsSnapshot> snapshots = new ArrayList<>();
    private static final int MAX_SNAPSHOTS = 1000;
    
    public static class MetricsSnapshot {
        public long timestamp;
        public float fps;
        public float frameTimeMs;
        public int drawCalls;
        public int triangles;
        public int textureBinds;
        public int shaderSwitches;
        public float overdrawRatio;
        public int objectsRendered;
        public int objectsCulled;
        
        public MetricsSnapshot(PerformanceMonitor pm) {
            this.timestamp = System.currentTimeMillis();
            this.fps = pm.getFPS();
            this.frameTimeMs = pm.getAverageFrameTime();
            this.drawCalls = pm.getDrawCalls();
            this.triangles = pm.getTriangleCount();
            this.textureBinds = pm.getTextureBinds();
            this.shaderSwitches = pm.getShaderSwitches();
            this.overdrawRatio = pm.overdrawRatio;
            this.objectsRendered = pm.objectsRendered;
            this.objectsCulled = pm.objectsCulled;
        }
    }
    
    public void collect(PerformanceMonitor pm) {
        try {
            if (pm == null) return;
            
            MetricsSnapshot snapshot = new MetricsSnapshot(pm);
            snapshots.add(snapshot);
            
            // Giới hạn số snapshots để tránh memory leak
            if (snapshots.size() > MAX_SNAPSHOTS) {
                snapshots.remove(0);
            }
        } catch (Exception e) {
            android.util.Log.e("MetricsCollector", "Error collecting metrics", e);
        }
    }
    
    public List<MetricsSnapshot> getSnapshots() {
        return new ArrayList<>(snapshots);
    }
    
    public void clear() {
        snapshots.clear();
    }
    
    public ComparisonReport generateComparisonReport() {
        return new ComparisonReport(snapshots);
    }
    
    public static class ComparisonReport {
        public float avgFps;
        public float minFps;
        public float maxFps;
        public float avgFrameTime;
        public float avgDrawCalls;
        public float avgTriangles;
        public float avgOverdraw;
        
        public ComparisonReport(List<MetricsSnapshot> snapshots) {
            if (snapshots.isEmpty()) {
                return;
            }
            
            float sumFps = 0, sumFrameTime = 0, sumDrawCalls = 0, sumTriangles = 0, sumOverdraw = 0;
            minFps = Float.MAX_VALUE;
            maxFps = 0;
            
            for (MetricsSnapshot s : snapshots) {
                sumFps += s.fps;
                sumFrameTime += s.frameTimeMs;
                sumDrawCalls += s.drawCalls;
                sumTriangles += s.triangles;
                sumOverdraw += s.overdrawRatio;
                
                minFps = Math.min(minFps, s.fps);
                maxFps = Math.max(maxFps, s.fps);
            }
            
            int count = snapshots.size();
            avgFps = sumFps / count;
            avgFrameTime = sumFrameTime / count;
            avgDrawCalls = sumDrawCalls / count;
            avgTriangles = sumTriangles / count;
            avgOverdraw = sumOverdraw / count;
        }
    }
}

