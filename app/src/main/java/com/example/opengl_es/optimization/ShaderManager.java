package com.example.opengl_es.optimization;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * BƯỚC 1: SHADER MANAGER
 * Quản lý việc load và switch giữa simple/complex shaders
 * Để đo lường hiệu suất và so sánh
 */
public class ShaderManager {
    
    private static final String TAG = "ShaderManager";
    
    private final Context context;
    private final Map<String, Integer> shaderPrograms = new HashMap<>();
    private int currentProgram = 0;
    
    public ShaderManager(Context context) {
        this.context = context.getApplicationContext();
    }
    
    /**
     * Load shader program from assets
     */
    public int loadShaderProgram(String name, String vertexShaderPath, 
                                 String fragmentShaderPath) {
        if (shaderPrograms.containsKey(name)) {
            return shaderPrograms.get(name);
        }
        
        String vertexSource = loadShaderSource(vertexShaderPath);
        String fragmentSource = loadShaderSource(fragmentShaderPath);
        
        if (vertexSource == null || fragmentSource == null) {
            Log.e(TAG, "Failed to load shader sources");
            return 0;
        }
        
        int program = createShaderProgram(vertexSource, fragmentSource);
        
        if (program != 0) {
            shaderPrograms.put(name, program);
            Log.d(TAG, "Loaded shader program: " + name);
        }
        
        return program;
    }
    
    /**
     * Use shader program by name
     * BƯỚC 1: Switch giữa simple và complex để so sánh performance
     */
    public void useProgram(String name) {
        Integer program = shaderPrograms.get(name);
        if (program != null && program != 0) {
            GLES30.glUseProgram(program);
            currentProgram = program;
            Log.d(TAG, "Switched to shader: " + name);
        }
    }
    
    /**
     * Get current shader program ID
     */
    public int getCurrentProgram() {
        return currentProgram;
    }
    
    /**
     * Get uniform location
     */
    public int getUniformLocation(String name) {
        if (currentProgram == 0) {
            return -1;
        }
        return GLES30.glGetUniformLocation(currentProgram, name);
    }
    
    /**
     * Get attribute location
     */
    public int getAttributeLocation(String name) {
        if (currentProgram == 0) {
            return -1;
        }
        return GLES30.glGetAttribLocation(currentProgram, name);
    }
    
    /**
     * Release all shader programs
     */
    public void releaseAll() {
        for (Integer program : shaderPrograms.values()) {
            GLES30.glDeleteProgram(program);
        }
        shaderPrograms.clear();
        currentProgram = 0;
    }
    
    private String loadShaderSource(String filename) {
        try {
            InputStream is = context.getAssets().open("shaders/" + filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder source = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                source.append(line).append("\n");
            }
            reader.close();
            is.close();
            return source.toString();
        } catch (IOException e) {
            Log.e(TAG, "Failed to load shader: " + filename, e);
            return null;
        }
    }
    
    private int createShaderProgram(String vertexSource, String fragmentSource) {
        int vertexShader = compileShader(GLES30.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        
        int fragmentShader = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == 0) {
            GLES30.glDeleteShader(vertexShader);
            return 0;
        }
        
        int program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        GLES30.glLinkProgram(program);
        
        int[] linkStatus = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
        
        if (linkStatus[0] == 0) {
            String log = GLES30.glGetProgramInfoLog(program);
            Log.e(TAG, "Shader program linking failed: " + log);
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
            Log.e(TAG, "Shader compilation failed: " + log);
            GLES30.glDeleteShader(shader);
            return 0;
        }
        
        return shader;
    }
}

