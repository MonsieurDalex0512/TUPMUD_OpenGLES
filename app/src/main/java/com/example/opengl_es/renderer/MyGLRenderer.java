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
    
    // Shared mesh (cube)
    private GLMesh cubeMesh;
    private int defaultTexture;
    
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
        
        // Log để debug
        android.util.Log.d("MyGLRenderer", String.format(
            "Shader locations - Position: %d, Normal: %d, TexCoord: %d, MVP: %d",
            positionLoc, normalLoc, texCoordLoc, mvpMatrixLoc));
        
        // Create shared cube mesh
        Mesh cubeMeshData = Mesh.createCube();
        cubeMesh = new GLMesh(cubeMeshData);
        
        // Generate default texture (checkerboard)
        defaultTexture = TextureLoader.generateCheckerboard(512, 64);
        
        // Setup culling based on config
        cullingManager.setBackFaceCulling(renderConfig.enableBackfaceCulling);
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
        
        // BƯỚC 2: Update culling based on config
        // Lưu ý: setBackFaceCulling phải được gọi TRƯỚC khi render để áp dụng ngay
        cullingManager.setBackFaceCulling(renderConfig.enableBackfaceCulling);
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
        if (renderConfig.enableLOD) {
            // LOD sẽ được apply khi render từng object
        }
        
        performanceMonitor.objectsCulled = allObjects.size() - visibleObjects.size();
        performanceMonitor.objectsRendered = visibleObjects.size();

        // Get camera matrices
        float[] viewProj = sceneManager.getCamera().getViewProjMatrix();
        
        // BƯỚC 1: Use shader program (simple hoặc complex)
        // Có thể switch dựa trên config để so sánh performance
        if (shaderManager != null) {
            String shaderName = renderConfig.enableInstancing ? "complex" : "simple";
            shaderManager.useProgram(shaderName);
            shaderProgram = shaderManager.getCurrentProgram();
            performanceMonitor.shaderSwitches++;
        }
        
        // Bind texture
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, defaultTexture);
        if (textureLoc >= 0) {
            GLES30.glUniform1i(textureLoc, 0);
        }
        performanceMonitor.textureBinds++;
        
        // Set lighting uniforms
        if (lightDirectionLoc >= 0) {
            GLES30.glUniform3fv(lightDirectionLoc, 1, lightDirection, 0);
        }
        if (lightColorLoc >= 0) {
            GLES30.glUniform3fv(lightColorLoc, 1, lightColor, 0);
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
        
        // Render each visible object
        if (positionLoc >= 0 && !visibleObjects.isEmpty()) {
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
                
                // Draw
                cubeMesh.draw();
                performanceMonitor.drawCalls++;
                performanceMonitor.triangleCount += cubeMesh.getTriangleCount();
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
        
        // Calculate overdraw ratio (simplified: assume 1.0 for now)
        performanceMonitor.overdrawRatio = 1.0f;

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

