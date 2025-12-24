package com.example.opengl_es.renderer;

/**
 * Central configuration flags for all rendering optimizations.
 * These flags are toggled from the UI (ControlPanelFragment) and
 * consumed by MyGLRenderer / optimization managers.
 */
public class RenderConfig {

    // Texture optimizations
    public boolean useETC1Compression = false;
    public boolean useMipmaps = true;
    public boolean useTextureAtlas = false;

    // Culling
    public boolean enableBackfaceCulling = true;
    public boolean enableFrustumCulling = false; // Tạm thời disable để test
    public boolean enableOcclusionCulling = false;

    // LOD
    public boolean enableLOD = true;

    // Rendering pipeline
    public boolean enableInstancing = false;
    public boolean enableDepthPrePass = false;

    // Benchmark / debug
    public boolean showOverdrawHeatmap = false;
}

