package com.example.opengl_es.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.opengl_es.MainActivity;
import com.example.opengl_es.R;
import com.example.opengl_es.monitoring.PerformanceMonitor;
import com.example.opengl_es.renderer.MyGLRenderer;

public class MetricsPanelFragment extends Fragment {
    
    private TextView txtAvgFrameTime;
    private TextView txtFrameVariance;
    private TextView txtJankCount;
    private TextView txtTriangles;
    private TextView txtTextureBinds;
    private TextView txtShaderSwitches;
    private TextView txtOverdraw;
    private TextView txtObjectsRendered;
    private TextView txtObjectsCulled;
    
    private MyGLRenderer renderer;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_metrics_panel, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Get renderer from MainActivity
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            renderer = activity.getRenderer();
        }
        
        // Find views
        txtAvgFrameTime = view.findViewById(R.id.txt_avg_frame_time);
        txtFrameVariance = view.findViewById(R.id.txt_frame_variance);
        txtJankCount = view.findViewById(R.id.txt_jank_count);
        txtTriangles = view.findViewById(R.id.txt_triangles);
        txtTextureBinds = view.findViewById(R.id.txt_texture_binds);
        txtShaderSwitches = view.findViewById(R.id.txt_shader_switches);
        txtOverdraw = view.findViewById(R.id.txt_overdraw);
        txtObjectsRendered = view.findViewById(R.id.txt_objects_rendered);
        txtObjectsCulled = view.findViewById(R.id.txt_objects_culled);
        
        // Start updating metrics
        startMetricsUpdate();
    }
    
    private void startMetricsUpdate() {
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (renderer != null && isAdded()) {
                    updateMetrics();
                    uiHandler.postDelayed(this, 500);
                }
            }
        }, 500);
    }
    
    private void updateMetrics() {
        if (renderer == null) return;
        
        PerformanceMonitor pm = renderer.getPerformanceMonitor();
        
        txtAvgFrameTime.setText(String.format("Avg Frame Time: %.2f ms", pm.getAverageFrameTime()));
        txtFrameVariance.setText(String.format("Frame Variance: %.2f", pm.getFrameTimeVariance()));
        txtJankCount.setText(String.format("Jank Count: %d", pm.getJankCount()));
        txtTriangles.setText(String.format("Triangles: %d", pm.triangleCount));
        txtTextureBinds.setText(String.format("Texture Binds: %d", pm.textureBinds));
        txtShaderSwitches.setText(String.format("Shader Switches: %d", pm.shaderSwitches));
        txtOverdraw.setText(String.format("Overdraw Ratio: %.2f", pm.overdrawRatio));
        txtObjectsRendered.setText(String.format("Objects Rendered: %d", pm.objectsRendered));
        txtObjectsCulled.setText(String.format("Objects Culled: %d", pm.objectsCulled));
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        uiHandler.removeCallbacksAndMessages(null);
    }
}

