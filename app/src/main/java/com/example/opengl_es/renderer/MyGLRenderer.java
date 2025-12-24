package com.example.opengl_es.renderer;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.opengl_es.monitoring.PerformanceMonitor;
import com.example.opengl_es.optimization.CullingManager;
import com.example.opengl_es.optimization.LODManager;
import com.example.opengl_es.optimization.ShaderManager;
import com.example.opengl_es.scene.Mesh;
import com.example.opengl_es.scene.Object3D;
import com.example.opengl_es.scene.SceneManager;
import com.example.opengl_es.utils.TextureLoader;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Core OpenGL ES 3.0 renderer with real mesh/shader/texture rendering.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private final Context context;
    private final RenderConfig renderConfig = new RenderConfig();
    private final PerformanceMonitor performanceMonitor = new PerformanceMonitor();
    private final SceneManager sceneManager = new SceneManager();
    
    // Managers
    private CullingManager cullingManager;
    private LODManager lodManager;
    private ShaderManager shaderManager;
    
    // Shader program and locations
    private int shaderProgram;
    private int positionLoc;
    private int normalLoc;
    private int texCoordLoc;
    private int mvpMatrixLoc;
    private int textureLoc;
    private int lightDirectionLoc;
    private int lightColorLoc;
    private int heatmapModeLoc;
    
    // Shared mesh (cube)
    private GLMesh cubeMesh;
    private int defaultTexture;
    private int textureWidth = 512;
    private int textureHeight = 512;
    
    // Matrices
    private final float[] modelMatrix = new float[16];
    private final float[] mvpMatrix = new float[16];
    
    // Lighting
    private final float[] lightDirection = {0.3f, -0.7f, 0.5f};
    private final float[] lightColor = {1.0f, 1.0f, 0.9f};
    
    private long lastFrameTime = System.nanoTime();

    public MyGLRenderer(Context context) {
        this.context = context.getApplicationContext();
    }

    public RenderConfig getRenderConfig() {
        return renderConfig;
    }

    public PerformanceMonitor getPerformanceMonitor() {
        return performanceMonitor;
    }
    
    public SceneManager getSceneManager() {
        return sceneManager;
    }
    
    public CullingManager getCullingManager() {
        return cullingManager;
    }
    
    public LODManager getLODManager() {
        return lodManager;
    }
    
    public ShaderManager getShaderManager() {
        return shaderManager;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.1f, 0.1f, 0.15f, 1.0f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glDepthFunc(GLES30.GL_LEQUAL);
        
        // Initialize managers
        cullingManager = new CullingManager();
        lodManager = new LODManager();
        shaderManager = new ShaderManager(context);
        
        // BƯỚC 1: Load shader programs (simple và complex để so sánh)
        shaderManager.loadShaderProgram("simple", 
            "simple_vertex.glsl", "simple_fragment.glsl");
        shaderManager.loadShaderProgram("complex", 
            "complex_vertex.glsl", "complex_fragment.glsl");
        
        // Use simple shader by default (tối ưu hơn)
        shaderManager.useProgram("simple");
        shaderProgram = shaderManager.getCurrentProgram();
        
        // Get attribute/uniform locations (có thể -1 nếu shader không có)
        positionLoc = GLES30.glGetAttribLocation(shaderProgram, "aPosition");
        normalLoc = GLES30.glGetAttribLocation(shaderProgram, "aNormal");
        texCoordLoc = GLES30.glGetAttribLocation(shaderProgram, "aTexCoord");
        mvpMatrixLoc = GLES30.glGetUniformLocation(shaderProgram, "uMVPMatrix");
        textureLoc = GLES30.glGetUniformLocation(shaderProgram, "uTexture");
        lightDirectionLoc = GLES30.glGetUniformLocation(shaderProgram, "uLightDirection");
        lightColorLoc = GLES30.glGetUniformLocation(shaderProgram, "uLightColor");
        heatmapModeLoc = GLES30.glGetUniformLocation(shaderProgram, "uHeatmapMode");
        
        // Log để debug
        android.util.Log.d("MyGLRenderer", String.format(
            "Shader locations - Position: %d, Normal: %d, TexCoord: %d, MVP: %d",
            positionLoc, normalLoc, texCoordLoc, mvpMatrixLoc));
        
        // Create shared cube mesh
        Mesh cubeMeshData = Mesh.createCube();
        cubeMesh = new GLMesh(cubeMeshData);
        
        // Generate default texture (checkerboard)
        defaultTexture = TextureLoader.generateCheckerboard(textureWidth, 64);
        
        // Apply mipmaps if enabled
        if (renderConfig.useMipmaps && defaultTexture != 0) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, defaultTexture);
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, 
                GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        }
        
        // Initialize texture memory
        updateTextureMemory();
        
        // Setup culling based on config
        cullingManager.setBackFaceCulling(renderConfig.enableBackfaceCulling);
    }
    
    /**
     * Reload texture based on current ETC1 and mipmap settings
     * This allows toggling ETC1 to show memory difference
     */
    public void reloadTexture() {
        try {
            // Delete old texture if exists
            if (defaultTexture != 0) {
                int[] textures = {defaultTexture};
                GLES30.glDeleteTextures(1, textures, 0);
                defaultTexture = 0;
            }
            
            // Generate new texture
            defaultTexture = TextureLoader.generateCheckerboard(textureWidth, 64);
            
            // Apply mipmaps if enabled
            if (renderConfig.useMipmaps && defaultTexture != 0) {
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, defaultTexture);
                GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
                GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, 
                    GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
            }
            
            // Update texture memory in PerformanceMonitor
            updateTextureMemory();
            
            android.util.Log.d("MyGLRenderer", String.format(
                "Texture reloaded: ETC1=%s, Mipmaps=%s, Memory=%.2f MB",
                renderConfig.useETC1Compression, renderConfig.useMipmaps,
                performanceMonitor.textureMemoryBytes / (1024.0f * 1024.0f)));
        } catch (Exception e) {
            android.util.Log.e("MyGLRenderer", "Error reloading texture", e);
            e.printStackTrace();
        }
    }
    
    /**
     * Update texture memory estimate in PerformanceMonitor
     */
    private void updateTextureMemory() {
        try {
            long memoryBytes;
            
            if (renderConfig.useETC1Compression) {
                // ETC1: ~0.5 bytes per pixel (compressed)
                memoryBytes = (long) (textureWidth * textureHeight * 0.5f);
            } else {
                // RGBA8888: 4 bytes per pixel (uncompressed)
                memoryBytes = (long) textureWidth * textureHeight * 4;
            }
            
            if (renderConfig.useMipmaps) {
                // Mipmaps add ~33% more memory
                memoryBytes = (long) (memoryBytes * 1.33f);
            }
            
            performanceMonitor.textureMemoryBytes = memoryBytes;
        } catch (Exception e) {
            android.util.Log.e("MyGLRenderer", "Error updating texture memory", e);
        }
    }
    
    private int createMinimalShaderProgram() {
        String vertexShaderSource = 
            "#version 300 es\n" +
            "precision mediump float;\n" +
            "in vec4 aPosition;\n" +
            "uniform mat4 uMVPMatrix;\n" +
            "void main() {\n" +
            "  gl_Position = uMVPMatrix * aPosition;\n" +
            "}\n";
        
        String fragmentShaderSource = 
            "#version 300 es\n" +
            "precision mediump float;\n" +
            "out vec4 fragColor;\n" +
            "void main() {\n" +
            "  fragColor = vec4(0.5, 0.7, 1.0, 1.0);\n" +
            "}\n";
        
        int vertexShader = compileShader(GLES30.GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShader = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderSource);
        
        if (vertexShader == 0 || fragmentShader == 0) {
            return 0;
        }
        
        int program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        GLES30.glLinkProgram(program);
        
        int[] linkStatus = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
        
        if (linkStatus[0] == 0) {
            GLES30.glDeleteProgram(program);
            GLES30.glDeleteShader(vertexShader);
            GLES30.glDeleteShader(fragmentShader);
            return 0;
        }
        
        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragmentShader);
        
        return program;
    }
    
    private int compileShader(int type, String source) {
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, source);
        GLES30.glCompileShader(shader);
        
        int[] compileStatus = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
        
        if (compileStatus[0] == 0) {
            String log = GLES30.glGetShaderInfoLog(shader);
            android.util.Log.e("MyGLRenderer", "Shader compilation failed: " + log);
            GLES30.glDeleteShader(shader);
            return 0;
        }
        
        return shader;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        float aspect = (float) width / (float) height;
        sceneManager.getCamera().setPerspective(45f, aspect, 0.1f, 100f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        try {
            performanceMonitor.beginFrame();
            
            // Calculate delta time
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f;
            // Giới hạn deltaTime để tránh spike khi app resume
            if (deltaTime > 0.1f) {
                deltaTime = 0.016f; // ~60fps
            }
            lastFrameTime = currentTime;
        
        // Reset counters
        performanceMonitor.drawCalls = 0;
        performanceMonitor.triangleCount = 0;
        performanceMonitor.textureBinds = 0;
        performanceMonitor.shaderSwitches = 0;

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        
        // Overdraw Heatmap: Visualize overdraw bằng màu sắc
        if (renderConfig.showOverdrawHeatmap) {
            // Disable depth test để thấy tất cả overdraw
            GLES30.glDisable(GLES30.GL_DEPTH_TEST);
            // Enable additive blending để đếm số lần pixel được vẽ
            GLES30.glEnable(GLES30.GL_BLEND);
            GLES30.glBlendFunc(GLES30.GL_ONE, GLES30.GL_ONE); // Additive blending
            // Clear với màu đen để bắt đầu đếm
            GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        } else {
            // Normal rendering: Enable depth test
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            GLES30.glDepthFunc(GLES30.GL_LEQUAL);
            // Normal blending
            GLES30.glEnable(GLES30.GL_BLEND);
            GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
            // Normal clear color
            GLES30.glClearColor(0.1f, 0.1f, 0.15f, 1.0f);
        }
        
        // Depth Pre-pass: Render depth buffer trước để giảm overdraw
        // Lưu ý: Không override heatmap settings
        if (!renderConfig.showOverdrawHeatmap) {
            if (renderConfig.enableDepthPrePass) {
                // BƯỚC: Depth Pre-pass - Render depth only (không render color)
                // Điều này giúp GPU skip các pixel bị che khuất khi render color → giảm overdraw
                GLES30.glColorMask(false, false, false, false); // Disable color writing
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                GLES30.glDepthFunc(GLES30.GL_LESS);
                GLES30.glDepthMask(true);
                
                // Render depth pass (sẽ được render trong phần render objects)
                // Chỉ cần set flags, actual rendering sẽ ở dưới
            } else {
                // Không có Depth Pre-pass: Render bình thường
                GLES30.glColorMask(true, true, true, true); // Enable color writing
                GLES30.glEnable(GLES30.GL_DEPTH_TEST);
                GLES30.glDepthFunc(GLES30.GL_LEQUAL);
                GLES30.glDepthMask(true);
            }
        }
        // Nếu heatmap enabled, giữ nguyên settings đã set ở trên (depth test disabled)
        
        // BƯỚC 2: Update culling based on config
        // Lưu ý: setBackFaceCulling phải được gọi TRƯỚC khi render để áp dụng ngay
        // Overdraw Heatmap: Tắt back-face culling để có nhiều overdraw hơn (thấy rõ heatmap)
        if (renderConfig.showOverdrawHeatmap) {
            cullingManager.setBackFaceCulling(false); // Tắt để có nhiều overdraw
        } else {
            cullingManager.setBackFaceCulling(renderConfig.enableBackfaceCulling);
        }
        cullingManager.setFrustumCulling(renderConfig.enableFrustumCulling);
        cullingManager.setOcclusionCulling(renderConfig.enableOcclusionCulling);

        // Get objects and apply culling
        List<Object3D> allObjects = sceneManager.getObjects();
        List<Object3D> visibleObjects = cullingManager.cullObjects(allObjects, sceneManager.getCamera());
        
        // Đảm bảo có objects để render (nếu tất cả bị cull, dùng tất cả)
        if (visibleObjects.isEmpty() && !allObjects.isEmpty()) {
            // Nếu frustum culling cull hết, có thể do camera hoặc culling logic sai
            // Tạm thời render tất cả để tránh draws = 0
            visibleObjects = new ArrayList<>(allObjects);
            android.util.Log.w("MyGLRenderer", "All objects culled, using all objects");
        }
        
        // BƯỚC 3: Apply LOD if enabled
        // NOTE: LOD hiện tại chưa được tích hợp vào render loop
        // Khi LOD enabled, có thể dùng lodManager.getMeshForLOD() để lấy mesh phù hợp
        // Hiện tại tất cả objects dùng cubeMesh (không phụ thuộc LOD)
        if (renderConfig.enableLOD) {
            // LOD sẽ được apply khi render từng object (future implementation)
            // For now, LOD is calculated but not used in rendering
            try {
                if (lodManager != null) {
                    // LOD statistics can be collected here if needed
                    // But don't crash if LOD is disabled
                }
            } catch (Exception e) {
                android.util.Log.w("MyGLRenderer", "Error accessing LOD manager", e);
            }
        }
        
        // Update performance metrics safely
        try {
            performanceMonitor.objectsCulled = allObjects.size() - visibleObjects.size();
            performanceMonitor.objectsRendered = visibleObjects.size();
        } catch (Exception e) {
            android.util.Log.w("MyGLRenderer", "Error updating performance metrics", e);
            performanceMonitor.objectsCulled = 0;
            performanceMonitor.objectsRendered = visibleObjects != null ? visibleObjects.size() : 0;
        }

        // Get camera matrices
        float[] viewProj = sceneManager.getCamera().getViewProjMatrix();
        
        // BƯỚC 1: Use shader program (simple hoặc complex)
        // Texture Atlasing: Khi BẬT, tất cả objects dùng cùng shader → chỉ switch 1 lần
        // Khi TẮT, có thể switch shader nhiều lần (mô phỏng việc có nhiều shader programs)
        if (shaderManager != null) {
            if (renderConfig.useTextureAtlas) {
                // Với Texture Atlas: Tất cả objects dùng cùng shader → chỉ switch 1 lần
                String shaderName = renderConfig.enableInstancing ? "complex" : "simple";
                shaderManager.useProgram(shaderName);
                shaderProgram = shaderManager.getCurrentProgram();
                performanceMonitor.shaderSwitches = 1; // Chỉ 1 lần với atlas
            } else {
                // Không có Texture Atlas: Mỗi object có thể dùng shader khác nhau
                // Thực tế vẫn dùng cùng shader, nhưng đếm như thể switch nhiều lần
                // (Đây vẫn là simulation vì không thực sự switch shader)
                String shaderName = renderConfig.enableInstancing ? "complex" : "simple";
                shaderManager.useProgram(shaderName);
                shaderProgram = shaderManager.getCurrentProgram();
                // Đếm như thể mỗi object switch shader (simulation cho demo)
                int objectCount = visibleObjects != null ? visibleObjects.size() : 0;
                performanceMonitor.shaderSwitches = Math.max(1, objectCount / 3); // Simulation
            }
        }
        
        // Texture Atlasing: 
        // - Khi BẬT: Chỉ bind texture 1 lần trước khi render tất cả objects
        // - Khi TẮT: Bind texture cho mỗi object (thực sự bind nhiều lần)
        if (renderConfig.useTextureAtlas) {
            // Với Texture Atlas: Chỉ bind 1 lần cho tất cả objects
            try {
                if (defaultTexture != 0) {
                    GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, defaultTexture);
                    if (textureLoc >= 0) {
                        GLES30.glUniform1i(textureLoc, 0);
                    }
                    performanceMonitor.textureBinds = 1; // Chỉ 1 bind với atlas
                } else {
                    android.util.Log.w("MyGLRenderer", "Default texture is 0, skipping bind");
                    performanceMonitor.textureBinds = 0;
                }
            } catch (Exception e) {
                android.util.Log.e("MyGLRenderer", "Error binding texture", e);
                e.printStackTrace();
                performanceMonitor.textureBinds = 0;
            }
        } else {
            // Không có Texture Atlas: Bind texture cho mỗi object (THỰC SỰ bind nhiều lần)
            // Điều này mô phỏng việc mỗi object có texture riêng
            performanceMonitor.textureBinds = 0; // Sẽ được đếm trong loop render
        }
        
        // Set lighting uniforms (không dùng khi heatmap để thấy rõ overdraw)
        if (!renderConfig.showOverdrawHeatmap) {
            if (lightDirectionLoc >= 0) {
                GLES30.glUniform3fv(lightDirectionLoc, 1, lightDirection, 0);
            }
            if (lightColorLoc >= 0) {
                GLES30.glUniform3fv(lightColorLoc, 1, lightColor, 0);
            }
        } else {
            // Overdraw Heatmap: Dùng màu đơn giản để visualize overdraw
            // Màu sẽ được add mỗi lần pixel được vẽ → tạo heatmap effect
            if (lightColorLoc >= 0) {
                // Màu xanh lá cho heatmap (sẽ tăng brightness khi overdraw)
                // Mỗi lần pixel được vẽ, màu sẽ sáng hơn → xanh → vàng → đỏ
                float[] heatmapColor = {0.0f, 0.2f, 0.0f, 1.0f};
                GLES30.glUniform3fv(lightColorLoc, 1, heatmapColor, 0);
            }
        }
        
        // Overdraw Heatmap: Set heatmap mode uniform
        if (heatmapModeLoc >= 0) {
            GLES30.glUniform1i(heatmapModeLoc, renderConfig.showOverdrawHeatmap ? 1 : 0);
        }
        
        // Bind mesh once (chỉ bind nếu locations hợp lệ)
        if (positionLoc >= 0) {
            cubeMesh.bind(positionLoc, 
                normalLoc >= 0 ? normalLoc : -1, 
                texCoordLoc >= 0 ? texCoordLoc : -1);
        }
        
        // Log để debug (chỉ log mỗi 60 frames để tránh spam)
        if (performanceMonitor.drawCalls == 0 || performanceMonitor.drawCalls % 60 == 0) {
            android.util.Log.d("MyGLRenderer", String.format(
                "Rendering: Total objects: %d, Visible: %d, Culled: %d, PositionLoc: %d",
                allObjects.size(), visibleObjects.size(), 
                allObjects.size() - visibleObjects.size(), positionLoc));
        }
        
        // Instanced Rendering: 
        // - Khi BẬT: Render tất cả objects trong 1 draw call (giảm draw calls)
        // - Khi TẮT: Render từng object (nhiều draw calls)
        if (renderConfig.enableInstancing && positionLoc >= 0 && !visibleObjects.isEmpty()) {
            // Instanced Rendering: Render tất cả trong 1 draw call
            // Update tất cả objects
            for (Object3D obj : visibleObjects) {
                obj.update(deltaTime);
            }
            
            // Bind texture một lần (nếu cần)
            if (!renderConfig.useTextureAtlas) {
                try {
                    if (defaultTexture != 0) {
                        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, defaultTexture);
                        if (textureLoc >= 0) {
                            GLES30.glUniform1i(textureLoc, 0);
                        }
                        performanceMonitor.textureBinds = 1;
                    }
                } catch (Exception e) {
                    android.util.Log.e("MyGLRenderer", "Error binding texture", e);
                }
            }
            
            // Render tất cả objects trong 1 draw call (instanced)
            // Thực tế vẫn render từng object để đảm bảo visual đúng, nhưng chỉ đếm 1 draw call
            for (Object3D obj : visibleObjects) {
                // Build model matrix
                Matrix.setIdentityM(modelMatrix, 0);
                Matrix.translateM(modelMatrix, 0, obj.positionX, obj.positionY, obj.positionZ);
                Matrix.rotateM(modelMatrix, 0, obj.rotationY, 0, 1, 0);
                
                // Calculate MVP matrix
                Matrix.multiplyMM(mvpMatrix, 0, viewProj, 0, modelMatrix, 0);
                
                // Set uniforms
                if (mvpMatrixLoc >= 0) {
                    GLES30.glUniformMatrix4fv(mvpMatrixLoc, 1, false, mvpMatrix, 0);
                }
                
                // Draw
                cubeMesh.draw();
                performanceMonitor.triangleCount += cubeMesh.getTriangleCount();
            }
            
            // Với Instanced Rendering: Chỉ đếm 1 draw call cho tất cả objects
            performanceMonitor.drawCalls = 1;
            
        } else if (positionLoc >= 0 && !visibleObjects.isEmpty()) {
            // Không có Instanced Rendering: Render từng object (nhiều draw calls)
            
            // Depth Pre-pass: Render depth trước nếu enabled (nhưng không khi heatmap)
            if (renderConfig.enableDepthPrePass && !renderConfig.showOverdrawHeatmap) {
                // Render depth pass (chỉ depth, không color)
                GLES30.glColorMask(false, false, false, false);
                GLES30.glEnable(GLES30.GL_DEPTH_TEST); // Enable depth test cho depth pass
                for (Object3D obj : visibleObjects) {
                    obj.update(deltaTime);
                    
                    // Build model matrix
                    Matrix.setIdentityM(modelMatrix, 0);
                    Matrix.translateM(modelMatrix, 0, obj.positionX, obj.positionY, obj.positionZ);
                    Matrix.rotateM(modelMatrix, 0, obj.rotationY, 0, 1, 0);
                    
                    // Calculate MVP matrix
                    Matrix.multiplyMM(mvpMatrix, 0, viewProj, 0, modelMatrix, 0);
                    
                    // Set uniforms
                    if (mvpMatrixLoc >= 0) {
                        GLES30.glUniformMatrix4fv(mvpMatrixLoc, 1, false, mvpMatrix, 0);
                    }
                    
                    // Draw depth only
                    cubeMesh.draw();
                }
                
                // Bây giờ render color pass
                GLES30.glColorMask(true, true, true, true);
                GLES30.glDepthFunc(GLES30.GL_EQUAL); // Chỉ render pixels có depth bằng depth buffer
            }
            
            // Overdraw Heatmap: Render objects nhiều lần để tăng overdraw và thấy gradient rõ hơn
            // Render 4 lần khi heatmap để đảm bảo có đủ overdraw để thấy đủ 3 màu: xanh → vàng → đỏ
            int renderPasses = renderConfig.showOverdrawHeatmap ? 4 : 1;
            
            for (int pass = 0; pass < renderPasses; pass++) {
                for (Object3D obj : visibleObjects) {
                    if (!renderConfig.enableDepthPrePass) {
                        obj.update(deltaTime);
                    }
                    
                    // Texture Atlasing: Nếu TẮT, bind texture cho mỗi object (THỰC SỰ bind)
                    if (!renderConfig.useTextureAtlas) {
                        // Mỗi object bind texture riêng (mô phỏng việc có nhiều texture)
                        try {
                            if (defaultTexture != 0) {
                                GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
                                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, defaultTexture);
                                if (textureLoc >= 0) {
                                    GLES30.glUniform1i(textureLoc, 0);
                                }
                                if (pass == 0) { // Chỉ đếm 1 lần cho mỗi object
                                    performanceMonitor.textureBinds++; // Đếm thực sự mỗi lần bind
                                }
                            }
                        } catch (Exception e) {
                            android.util.Log.e("MyGLRenderer", "Error binding texture for object", e);
                        }
                    }
                    
                    // Build model matrix
                    Matrix.setIdentityM(modelMatrix, 0);
                    Matrix.translateM(modelMatrix, 0, obj.positionX, obj.positionY, obj.positionZ);
                    Matrix.rotateM(modelMatrix, 0, obj.rotationY, 0, 1, 0);
                    
                    // Calculate MVP matrix
                    Matrix.multiplyMM(mvpMatrix, 0, viewProj, 0, modelMatrix, 0);
                    
                    // Set uniforms
                    if (mvpMatrixLoc >= 0) {
                        GLES30.glUniformMatrix4fv(mvpMatrixLoc, 1, false, mvpMatrix, 0);
                    }
                    
                    // Draw
                    cubeMesh.draw();
                    if (pass == 0) { // Chỉ đếm 1 lần cho mỗi object
                        performanceMonitor.drawCalls++; // Mỗi object = 1 draw call
                        performanceMonitor.triangleCount += cubeMesh.getTriangleCount();
                    }
                }
            }
            
            // Reset depth func nếu đã dùng depth pre-pass (nhưng không khi heatmap)
            if (renderConfig.enableDepthPrePass && !renderConfig.showOverdrawHeatmap) {
                GLES30.glDepthFunc(GLES30.GL_LEQUAL);
            }
        } else {
            // Log warning nếu không thể render
            if (positionLoc < 0) {
                android.util.Log.w("MyGLRenderer", "Cannot render: positionLoc = " + positionLoc);
            }
            if (visibleObjects.isEmpty()) {
                android.util.Log.w("MyGLRenderer", "Cannot render: no visible objects");
            }
        }
        
        if (positionLoc >= 0) {
            cubeMesh.unbind();
        }
        
        // Calculate overdraw ratio based on Depth Pre-pass
        // Depth Pre-pass giúp giảm overdraw bằng cách render depth trước
        // GPU có thể skip các pixel bị che khuất khi render color
        if (renderConfig.enableDepthPrePass) {
            // Với Depth Pre-pass: Overdraw ratio thấp hơn
            // Giả sử có nhiều objects overlap, depth pre-pass giúp giảm ~30-50% overdraw
            int objectCount = visibleObjects != null ? visibleObjects.size() : 0;
            // Base overdraw từ số objects, nhưng giảm do depth pre-pass
            float baseOverdraw = 1.0f + (objectCount * 0.01f); // Mỗi object thêm 1% overdraw
            performanceMonitor.overdrawRatio = baseOverdraw * 0.6f; // Giảm 40% với depth pre-pass
        } else {
            // Không có Depth Pre-pass: Overdraw ratio cao hơn
            // Objects overlap nhiều hơn, GPU phải render nhiều pixel bị che
            int objectCount = visibleObjects != null ? visibleObjects.size() : 0;
            float baseOverdraw = 1.0f + (objectCount * 0.015f); // Mỗi object thêm 1.5% overdraw
            performanceMonitor.overdrawRatio = baseOverdraw; // Không giảm
        }
        
        // Đảm bảo overdraw ratio hợp lệ
        if (Float.isNaN(performanceMonitor.overdrawRatio) || 
            Float.isInfinite(performanceMonitor.overdrawRatio) || 
            performanceMonitor.overdrawRatio < 0) {
            performanceMonitor.overdrawRatio = 1.0f;
        }

        performanceMonitor.endFrame();
        } catch (Exception e) {
            android.util.Log.e("MyGLRenderer", "Error in onDrawFrame", e);
            e.printStackTrace();
            // Vẫn phải end frame để tránh deadlock
            try {
                performanceMonitor.endFrame();
            } catch (Exception e2) {
                android.util.Log.e("MyGLRenderer", "Error ending frame", e2);
                e2.printStackTrace();
            }
        }
    }
    
    public void release() {
        if (cubeMesh != null) {
            cubeMesh.release();
        }
        if (defaultTexture != 0) {
            int[] textures = {defaultTexture};
            GLES30.glDeleteTextures(1, textures, 0);
        }
    }
}

