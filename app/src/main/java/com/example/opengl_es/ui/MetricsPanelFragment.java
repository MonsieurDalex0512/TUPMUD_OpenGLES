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
    private TextView txtTextureMemory;
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
        txtTextureMemory = view.findViewById(R.id.txt_texture_memory);
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
                try {
                    // Safety checks before updating
                    if (renderer != null && isAdded() && getView() != null) {
                        updateMetrics();
                        // Schedule next update
                        uiHandler.postDelayed(this, 500);
                    } else {
                        // Stop updating if fragment is detached
                        android.util.Log.d("MetricsPanel", "Stopping metrics update - fragment detached");
                    }
                } catch (Exception e) {
                    android.util.Log.e("MetricsPanel", "Error in metrics update loop", e);
                    e.printStackTrace();
                    // Try to continue anyway
                    try {
                        uiHandler.postDelayed(this, 500);
                    } catch (Exception e2) {
                        android.util.Log.e("MetricsPanel", "Error scheduling next update", e2);
                    }
                }
            }
        }, 500);
    }
    
    private void updateMetrics() {
        try {
            // Safety checks
            if (renderer == null) {
                return;
            }
            
            PerformanceMonitor pm = renderer.getPerformanceMonitor();
            if (pm == null) {
                android.util.Log.w("MetricsPanel", "PerformanceMonitor is null");
                return;
            }
            
            // Update each metric safely with null checks
            try {
                if (txtAvgFrameTime != null) {
                    float avgFrameTime = pm.getAverageFrameTime();
                    if (Float.isNaN(avgFrameTime) || Float.isInfinite(avgFrameTime)) {
                        avgFrameTime = 0f;
                    }
                    txtAvgFrameTime.setText(String.format("Avg Frame Time: %.2f ms", avgFrameTime));
                }
            } catch (Exception e) {
                android.util.Log.w("MetricsPanel", "Error updating Avg Frame Time", e);
            }
            
            try {
                if (txtFrameVariance != null) {
                    float variance = pm.getFrameTimeVariance();
                    if (Float.isNaN(variance) || Float.isInfinite(variance)) {
                        variance = 0f;
                    }
                    txtFrameVariance.setText(String.format("Frame Variance: %.2f", variance));
                }
            } catch (Exception e) {
                android.util.Log.w("MetricsPanel", "Error updating Frame Variance", e);
            }
            
            try {
                if (txtJankCount != null) {
                    int jankCount = pm.getJankCount();
                    txtJankCount.setText(String.format("Jank Count: %d", jankCount));
                }
            } catch (Exception e) {
                android.util.Log.w("MetricsPanel", "Error updating Jank Count", e);
            }
            
            try {
                if (txtTriangles != null) {
                    // Use getter method for thread safety
                    int triangles = pm.getTriangleCount();
                    txtTriangles.setText(String.format("Triangles: %d", triangles));
                }
            } catch (Exception e) {
                android.util.Log.w("MetricsPanel", "Error updating Triangles", e);
            }
            
            try {
                if (txtTextureBinds != null) {
                    // Use getter method for thread safety
                    int textureBinds = pm.getTextureBinds();
                    txtTextureBinds.setText(String.format("Texture Binds: %d", textureBinds));
                }
            } catch (Exception e) {
                android.util.Log.w("MetricsPanel", "Error updating Texture Binds", e);
            }
            
            try {
                if (txtShaderSwitches != null) {
                    // Use getter method for thread safety
                    int shaderSwitches = pm.getShaderSwitches();
                    txtShaderSwitches.setText(String.format("Shader Switches: %d", shaderSwitches));
                }
            } catch (Exception e) {
                android.util.Log.w("MetricsPanel", "Error updating Shader Switches", e);
            }
            
            try {
                if (txtTextureMemory != null) {
                    long textureMemoryBytes = pm.textureMemoryBytes;
                    if (textureMemoryBytes < 0) {
                        textureMemoryBytes = 0;
                    }
                    float textureMemoryMB = textureMemoryBytes / (1024.0f * 1024.0f);
                    txtTextureMemory.setText(String.format("Texture Memory: %.2f MB", textureMemoryMB));
                }
            } catch (Exception e) {
                android.util.Log.w("MetricsPanel", "Error updating Texture Memory", e);
            }
            
            try {
                if (txtOverdraw != null) {
                    float overdraw = pm.overdrawRatio;
                    if (Float.isNaN(overdraw) || Float.isInfinite(overdraw) || overdraw < 0) {
                        overdraw = 1.0f;
                    }
                    txtOverdraw.setText(String.format("Overdraw Ratio: %.2f", overdraw));
                }
            } catch (Exception e) {
                android.util.Log.w("MetricsPanel", "Error updating Overdraw Ratio", e);
            }
            
            try {
                if (txtObjectsRendered != null) {
                    int objectsRendered = pm.objectsRendered;
                    if (objectsRendered < 0) {
                        objectsRendered = 0;
                    }
                    txtObjectsRendered.setText(String.format("Objects Rendered: %d", objectsRendered));
                }
            } catch (Exception e) {
                android.util.Log.w("MetricsPanel", "Error updating Objects Rendered", e);
            }
            
            try {
                if (txtObjectsCulled != null) {
                    int objectsCulled = pm.objectsCulled;
                    if (objectsCulled < 0) {
                        objectsCulled = 0;
                    }
                    txtObjectsCulled.setText(String.format("Objects Culled: %d", objectsCulled));
                }
            } catch (Exception e) {
                android.util.Log.w("MetricsPanel", "Error updating Objects Culled", e);
            }
        } catch (Throwable e) {
            android.util.Log.e("MetricsPanel", "Critical error in updateMetrics", e);
            e.printStackTrace();
            // Don't crash - just log the error
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        uiHandler.removeCallbacksAndMessages(null);
    }
}




