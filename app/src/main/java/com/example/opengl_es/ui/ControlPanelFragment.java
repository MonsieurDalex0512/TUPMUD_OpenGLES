package com.example.opengl_es.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.opengl_es.MainActivity;
import com.example.opengl_es.R;
import com.example.opengl_es.benchmark.BenchmarkSuite;
import com.example.opengl_es.benchmark.BenchmarkResults;
import com.example.opengl_es.renderer.MyGLRenderer;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ControlPanelFragment extends Fragment {
    
    private SwitchMaterial switchETC1;
    private SwitchMaterial switchMipmaps;
    private SwitchMaterial switchTextureAtlas;
    private SwitchMaterial switchBackfaceCulling;
    private SwitchMaterial switchFrustumCulling;
    private SwitchMaterial switchOcclusionCulling;
    private SwitchMaterial switchLOD;
    private SwitchMaterial switchInstancing;
    private SwitchMaterial switchDepthPrepass;
    private SwitchMaterial switchOverdrawHeatmap;
    private Button btnRunBenchmark;
    
    private MyGLRenderer renderer;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_panel, container, false);
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
        switchETC1 = view.findViewById(R.id.switch_etc1);
        switchMipmaps = view.findViewById(R.id.switch_mipmaps);
        switchTextureAtlas = view.findViewById(R.id.switch_texture_atlas);
        switchBackfaceCulling = view.findViewById(R.id.switch_backface_culling);
        switchFrustumCulling = view.findViewById(R.id.switch_frustum_culling);
        switchOcclusionCulling = view.findViewById(R.id.switch_occlusion_culling);
        switchLOD = view.findViewById(R.id.switch_lod);
        switchInstancing = view.findViewById(R.id.switch_instancing);
        switchDepthPrepass = view.findViewById(R.id.switch_depth_prepass);
        switchOverdrawHeatmap = view.findViewById(R.id.switch_overdraw_heatmap);
        btnRunBenchmark = view.findViewById(R.id.btn_run_benchmark);
        
        // Load current config
        if (renderer != null) {
            loadConfig();
        }
        
        // Setup listeners
        setupListeners();
    }
    
    private void loadConfig() {
        if (renderer == null) return;
        
        com.example.opengl_es.renderer.RenderConfig config = renderer.getRenderConfig();
        switchETC1.setChecked(config.useETC1Compression);
        switchMipmaps.setChecked(config.useMipmaps);
        switchTextureAtlas.setChecked(config.useTextureAtlas);
        switchBackfaceCulling.setChecked(config.enableBackfaceCulling);
        switchFrustumCulling.setChecked(config.enableFrustumCulling);
        switchOcclusionCulling.setChecked(config.enableOcclusionCulling);
        switchLOD.setChecked(config.enableLOD);
        switchInstancing.setChecked(config.enableInstancing);
        switchDepthPrepass.setChecked(config.enableDepthPrePass);
        switchOverdrawHeatmap.setChecked(config.showOverdrawHeatmap);
    }
    
    private void setupListeners() {
        if (renderer == null) return;
        
        com.example.opengl_es.renderer.RenderConfig config = renderer.getRenderConfig();
        
        switchETC1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                config.useETC1Compression = isChecked;
                // Update texture memory immediately (no need to reload texture, just update estimate)
                if (renderer != null) {
                    com.example.opengl_es.monitoring.PerformanceMonitor pm = renderer.getPerformanceMonitor();
                    if (pm != null) {
                        // Calculate texture memory based on ETC1 state
                        int textureWidth = 512;
                        int textureHeight = 512;
                        long memoryBytes;
                        
                        if (isChecked) {
                            // ETC1: ~0.5 bytes per pixel (compressed)
                            memoryBytes = (long) (textureWidth * textureHeight * 0.5f);
                        } else {
                            // RGBA8888: 4 bytes per pixel (uncompressed)
                            memoryBytes = (long) textureWidth * textureHeight * 4;
                        }
                        
                        if (config.useMipmaps) {
                            // Mipmaps add ~33% more memory
                            memoryBytes = (long) (memoryBytes * 1.33f);
                        }
                        
                        pm.textureMemoryBytes = memoryBytes;
                        android.util.Log.d("ControlPanel", String.format(
                            "ETC1 toggled: %s, Texture Memory updated to %.2f MB",
                            isChecked, memoryBytes / (1024.0f * 1024.0f)));
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("ControlPanel", "Error toggling ETC1 compression", e);
                e.printStackTrace();
                // Revert toggle on error
                buttonView.setChecked(!isChecked);
            }
        });
        
        switchMipmaps.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.useMipmaps = isChecked;
            // Update texture memory immediately
            if (renderer != null) {
                com.example.opengl_es.monitoring.PerformanceMonitor pm = renderer.getPerformanceMonitor();
                if (pm != null) {
                    int textureWidth = 512;
                    int textureHeight = 512;
                    long memoryBytes;
                    
                    if (config.useETC1Compression) {
                        // ETC1: ~0.5 bytes per pixel (compressed)
                        memoryBytes = (long) (textureWidth * textureHeight * 0.5f);
                    } else {
                        // RGBA8888: 4 bytes per pixel (uncompressed)
                        memoryBytes = (long) textureWidth * textureHeight * 4;
                    }
                    
                    if (isChecked) {
                        // Mipmaps add ~33% more memory
                        memoryBytes = (long) (memoryBytes * 1.33f);
                    }
                    
                    pm.textureMemoryBytes = memoryBytes;
                    android.util.Log.d("ControlPanel", String.format(
                        "Mipmaps toggled: %s, Texture Memory updated to %.2f MB",
                        isChecked, memoryBytes / (1024.0f * 1024.0f)));
                }
            }
        });
        
        switchTextureAtlas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.useTextureAtlas = isChecked;
        });
        
        switchBackfaceCulling.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.enableBackfaceCulling = isChecked;
        });
        
        switchFrustumCulling.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.enableFrustumCulling = isChecked;
        });
        
        switchOcclusionCulling.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.enableOcclusionCulling = isChecked;
        });
        
        switchLOD.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.enableLOD = isChecked;
        });
        
        switchInstancing.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.enableInstancing = isChecked;
        });
        
        switchDepthPrepass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.enableDepthPrePass = isChecked;
        });
        
        switchOverdrawHeatmap.setOnCheckedChangeListener((buttonView, isChecked) -> {
            config.showOverdrawHeatmap = isChecked;
        });
        
        btnRunBenchmark.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.runBenchmarkSuite();
            }
        });
    }
}

