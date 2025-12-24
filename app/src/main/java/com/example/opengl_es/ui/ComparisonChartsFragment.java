package com.example.opengl_es.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;

import com.example.opengl_es.MainActivity;
import com.example.opengl_es.R;
import com.example.opengl_es.monitoring.PerformanceMonitor;
import com.example.opengl_es.monitoring.ChartGenerator;
import com.example.opengl_es.renderer.MyGLRenderer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.BarChart;

import java.util.ArrayList;
import java.util.List;

public class ComparisonChartsFragment extends Fragment {
    
    private LineChart chartFps;
    private BarChart chartComparison;
    
    private MyGLRenderer renderer;
    private List<Float> fpsHistory = new ArrayList<>();
    private Handler updateHandler = new Handler(Looper.getMainLooper());
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comparison_charts, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Get renderer from MainActivity
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            renderer = activity.getRenderer();
        }
        
        // Find charts
        chartFps = view.findViewById(R.id.chart_fps);
        chartComparison = view.findViewById(R.id.chart_comparison);
        
        // Setup charts
        setupFpsChart();
        setupComparisonChart();
        
        // Start updating charts
        startChartUpdate();
    }
    
    private void startChartUpdate() {
        updateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (renderer != null && isAdded()) {
                    com.example.opengl_es.monitoring.PerformanceMonitor pm = renderer.getPerformanceMonitor();
                    float fps = pm.getFPS();
                    fpsHistory.add(fps);
                    if (fpsHistory.size() > 100) {
                        fpsHistory.remove(0);
                    }
                    updateFpsChart(fps);
                    
                    // Update comparison chart
                    if (getActivity() instanceof MainActivity) {
                        MainActivity activity = (MainActivity) getActivity();
                        com.example.opengl_es.monitoring.MetricsCollector collector = activity.getMetricsCollector();
                        if (collector != null) {
                            com.example.opengl_es.monitoring.MetricsCollector.ComparisonReport report = collector.generateComparisonReport();
                            com.example.opengl_es.monitoring.ChartGenerator.updateComparisonChart(chartComparison, report);
                        }
                    }
                    
                    updateHandler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        updateHandler.removeCallbacksAndMessages(null);
    }
    
    private void setupFpsChart() {
        if (chartFps == null) return;
        ChartGenerator.updateFpsChart(chartFps, fpsHistory);
    }
    
    private void setupComparisonChart() {
        if (chartComparison == null) return;
        chartComparison.getDescription().setEnabled(false);
        chartComparison.setTouchEnabled(true);
        chartComparison.setDragEnabled(true);
        chartComparison.setScaleEnabled(true);
    }
    
    private void updateFpsChart(float fps) {
        if (chartFps == null) return;
        ChartGenerator.updateFpsChart(chartFps, fpsHistory);
    }
}

