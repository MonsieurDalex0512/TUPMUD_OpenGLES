package com.example.opengl_es;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Build;

import com.example.opengl_es.renderer.MyGLSurfaceView;
import com.example.opengl_es.renderer.MyGLRenderer;
import com.example.opengl_es.monitoring.PerformanceMonitor;
import com.example.opengl_es.monitoring.GPUProfiler;
import com.example.opengl_es.monitoring.MetricsCollector;
import com.example.opengl_es.benchmark.BenchmarkSuite;
import com.example.opengl_es.benchmark.BenchmarkResults;
import com.example.opengl_es.ui.ViewPagerAdapter;
import com.example.opengl_es.ui.BenchmarkResultsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    private MyGLSurfaceView glSurfaceView;
    private MyGLRenderer renderer;
    private TextView txtFps;
    private TextView txtFrameTime;
    private TextView txtDrawCalls;
    
    // BƯỚC 5: GPU Profiler để đo lường frame timing
    private GPUProfiler gpuProfiler;
    private MetricsCollector metricsCollector;
    private BenchmarkSuite benchmarkSuite;
    
    // Bottom Sheet UI
    private View bottomSheet;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private FloatingActionButton fabControls;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    
    private Handler uiHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main);

            // Create and attach GLSurfaceView programmatically
            View glContainer = findViewById(R.id.gl_container);
            if (glContainer instanceof android.view.ViewGroup) {
                glSurfaceView = new MyGLSurfaceView(this);
                renderer = glSurfaceView.getRenderer();
                ((android.view.ViewGroup) glContainer).addView(glSurfaceView);
            }

            // HUD views
            View hud = findViewById(R.id.overlay_metrics_hud);
            if (hud != null) {
                txtFps = hud.findViewById(R.id.txt_fps);
                txtFrameTime = hud.findViewById(R.id.txt_frame_time);
                txtDrawCalls = hud.findViewById(R.id.txt_draw_calls);
            }

            // BƯỚC 5: Enable GPU Profiling (FrameMetrics API)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    gpuProfiler = new GPUProfiler();
                    gpuProfiler.enableFrameMetrics(this);
                } catch (Exception e) {
                    android.util.Log.e("MainActivity", "Error enabling GPU profiler", e);
                }
            }
            
            // Initialize metrics collector
            metricsCollector = new MetricsCollector();
            
            // Initialize benchmark suite
            if (renderer != null && glSurfaceView != null) {
                try {
                    benchmarkSuite = new BenchmarkSuite(renderer, glSurfaceView);
                } catch (Exception e) {
                    android.util.Log.e("MainActivity", "Error initializing benchmark suite", e);
                }
            }
            
            // Setup bottom sheet
            setupBottomSheet();
            
            // Start metrics update
            startMetricsUpdate();
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Error in onCreate", e);
            e.printStackTrace();
            // Vẫn hiển thị error dialog để user biết
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Cleanup Handler để tránh memory leak
        if (uiHandler != null) {
            uiHandler.removeCallbacksAndMessages(null);
        }
        
        // BƯỚC 5: Release GPU Profiler
        if (gpuProfiler != null) {
            gpuProfiler.release();
        }
        
        // Release renderer resources
        if (renderer != null) {
            renderer.release();
        }
    }
    
    private void setupBottomSheet() {
        bottomSheet = findViewById(R.id.bottom_sheet);
        fabControls = findViewById(R.id.fab_controls);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        
        if (bottomSheet != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            bottomSheetBehavior.setHideable(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        
        // Setup ViewPager2 with fragments
        if (viewPager != null && tabLayout != null) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(this);
            viewPager.setAdapter(adapter);
            
            // Connect TabLayout with ViewPager2
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText("Controls");
                        break;
                    case 1:
                        tab.setText("Metrics");
                        break;
                    case 2:
                        tab.setText("Charts");
                        break;
                }
            }).attach();
        }
        
        // FAB click to toggle bottom sheet
        if (fabControls != null) {
            fabControls.setOnClickListener(v -> {
                if (bottomSheetBehavior != null) {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN ||
                        bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }
            });
        }
    }
    
    private Runnable metricsUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                // Luôn schedule tiếp để đảm bảo không bị dừng
                if (uiHandler != null) {
                    uiHandler.postDelayed(this, 500);
                }
                
                // Kiểm tra activity state
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                
                if (renderer != null && txtFps != null) {
                    PerformanceMonitor pm = renderer.getPerformanceMonitor();
                    if (pm != null) {
                        float fps = pm.getFPS();
                        float frameTime = pm.getAverageFrameTime();
                        int draws = pm.getDrawCalls();
                        
                        // Update UI
                        txtFps.setText(String.format("FPS: %.1f", fps));
                        txtFrameTime.setText(String.format("Frame: %.2f ms", frameTime));
                        txtDrawCalls.setText("Draws: " + draws);
                        
                        // Collect metrics (giảm tần suất để tránh memory leak)
                        if (metricsCollector != null) {
                            metricsCollector.collect(pm);
                        }
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("MainActivity", "Error updating metrics", e);
                // Vẫn schedule tiếp để không bị dừng
                if (uiHandler != null) {
                    uiHandler.postDelayed(this, 500);
                }
            }
        }
    };
    
    private void startMetricsUpdate() {
        if (uiHandler != null) {
            uiHandler.postDelayed(metricsUpdateRunnable, 500);
        }
    }
    
    public void runBenchmarkSuite() {
        if (benchmarkSuite == null) {
            Toast.makeText(this, "Benchmark suite not initialized", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Toast.makeText(this, "Running benchmark suite...", Toast.LENGTH_SHORT).show();
        
        // Run benchmark in background thread
        new Thread(() -> {
            try {
                BenchmarkResults results = benchmarkSuite.runAll();
                
                // Show results on UI thread
                uiHandler.post(() -> {
                    Intent intent = new Intent(this, BenchmarkResultsActivity.class);
                    intent.putExtra("benchmark_results", results);
                    startActivity(intent);
                    Toast.makeText(this, "Benchmark completed!", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                uiHandler.post(() -> {
                    Toast.makeText(this, "Benchmark failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
    
    public MetricsCollector getMetricsCollector() {
        return metricsCollector;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (glSurfaceView != null) {
            glSurfaceView.onResume();
        }
        // Resume metrics update
        startMetricsUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
        // Pause metrics update để tiết kiệm resources
        if (uiHandler != null) {
            uiHandler.removeCallbacksAndMessages(null);
        }
    }
    
    public MyGLRenderer getRenderer() {
        return renderer;
    }
}
