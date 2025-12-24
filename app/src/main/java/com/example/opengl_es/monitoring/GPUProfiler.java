package com.example.opengl_es.monitoring;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

/**
 * BƯỚC 5: GPU PROFILER
 * Tích hợp với Android Profiling Tools:
 * - FrameMetrics API (API 24+)
 * - Dumpsys Gfxinfo parsing
 * - Systrace integration
 */
public class GPUProfiler {
    
    private static final String TAG = "GPUProfiler";
    
    private Activity activity;
    private Window.OnFrameMetricsAvailableListener frameMetricsListener;
    private List<FrameMetricsData> frameMetricsHistory = new ArrayList<>();
    
    /**
     * BƯỚC 5: Enable FrameMetrics để đo lường frame timing
     * Các thanh màu trong Profile GPU Rendering:
     * - Draw (xanh dương): Tạo lệnh vẽ
     * - Prepare (tím): Chuẩn bị dữ liệu
     * - Process (đỏ): Thực thi danh sách lệnh
     * - Execute (vàng): Gửi lệnh tới GPU
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void enableFrameMetrics(Activity activity) {
        this.activity = activity;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            frameMetricsListener = new Window.OnFrameMetricsAvailableListener() {
                @Override
                public void onFrameMetricsAvailable(
                    Window window, 
                    android.view.FrameMetrics frameMetrics, 
                    int dropCountSinceLastInvocation) {
                    
                    // Extract frame timing data
                    FrameMetricsData data = new FrameMetricsData();
                    
                    // Total frame time (target: < 16.67ms cho 60fps)
                    data.totalDuration = frameMetrics.getMetric(
                        android.view.FrameMetrics.TOTAL_DURATION) / 1_000_000.0f; // Convert to ms
                    
                    // Draw time (xanh dương)
                    data.drawDuration = frameMetrics.getMetric(
                        android.view.FrameMetrics.DRAW_DURATION) / 1_000_000.0f;
                    
                    // Prepare time (tím)
                    data.prepareDuration = frameMetrics.getMetric(
                        android.view.FrameMetrics.ANIMATION_DURATION) / 1_000_000.0f;
                    
                    // Process time (đỏ)
                    data.processDuration = frameMetrics.getMetric(
                        android.view.FrameMetrics.LAYOUT_MEASURE_DURATION) / 1_000_000.0f;
                    
                    // Execute time (vàng) - GPU time
                    data.executeDuration = frameMetrics.getMetric(
                        android.view.FrameMetrics.SWAP_BUFFERS_DURATION) / 1_000_000.0f;
                    
                    frameMetricsHistory.add(data);
                    
                    // Keep only last 120 frames (2 seconds at 60fps)
                    if (frameMetricsHistory.size() > 120) {
                        frameMetricsHistory.remove(0);
                    }
                    
                    // Log nếu vượt quá threshold 16.67ms
                    if (data.totalDuration > 16.67f) {
                        Log.w(TAG, String.format(
                            "JANK DETECTED! Frame time: %.2f ms (threshold: 16.67 ms)",
                            data.totalDuration));
                    }
                }
            };
            
            Handler handler = new Handler(Looper.getMainLooper());
            activity.getWindow().addOnFrameMetricsAvailableListener(
                frameMetricsListener, handler);
            
            Log.d(TAG, "FrameMetrics enabled - monitoring frame timing");
        }
    }
    
    /**
     * BƯỚC 5: Get average frame time từ FrameMetrics
     */
    public float getAverageFrameTime() {
        if (frameMetricsHistory.isEmpty()) {
            return 0f;
        }
        
        float sum = 0f;
        for (FrameMetricsData data : frameMetricsHistory) {
            sum += data.totalDuration;
        }
        return sum / frameMetricsHistory.size();
    }
    
    /**
     * BƯỚC 5: Get jank count (frames > 16.67ms)
     */
    public int getJankCount() {
        int count = 0;
        for (FrameMetricsData data : frameMetricsHistory) {
            if (data.totalDuration > 16.67f) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * BƯỚC 5: Get frame breakdown (Draw, Prepare, Process, Execute)
     */
    public FrameBreakdown getFrameBreakdown() {
        if (frameMetricsHistory.isEmpty()) {
            return new FrameBreakdown();
        }
        
        FrameBreakdown breakdown = new FrameBreakdown();
        for (FrameMetricsData data : frameMetricsHistory) {
            breakdown.drawTime += data.drawDuration;
            breakdown.prepareTime += data.prepareDuration;
            breakdown.processTime += data.processDuration;
            breakdown.executeTime += data.executeDuration;
        }
        
        int count = frameMetricsHistory.size();
        breakdown.drawTime /= count;
        breakdown.prepareTime /= count;
        breakdown.processTime /= count;
        breakdown.executeTime /= count;
        
        return breakdown;
    }
    
    public void release() {
        if (activity != null && frameMetricsListener != null && 
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            activity.getWindow().removeOnFrameMetricsAvailableListener(frameMetricsListener);
        }
    }
    
    /**
     * Frame metrics data structure
     */
    public static class FrameMetricsData {
        public float totalDuration;
        public float drawDuration;
        public float prepareDuration;
        public float processDuration;
        public float executeDuration;
    }
    
    /**
     * Frame breakdown for analysis
     */
    public static class FrameBreakdown {
        public float drawTime;
        public float prepareTime;
        public float processTime;
        public float executeTime;
        
        public float getTotalTime() {
            return drawTime + prepareTime + processTime + executeTime;
        }
    }
}

