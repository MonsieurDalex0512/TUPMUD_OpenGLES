package com.example.opengl_es.optimization;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.util.Log;

import com.example.opengl_es.utils.TextureLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * BƯỚC 6: TEXTURE MANAGER
 * Quản lý texture loading, compression (ETC1), và mipmaps
 */
public class TextureManager {
    
    private static final String TAG = "TextureManager";
    
    private final Context context;
    private final Map<String, Integer> textureCache = new HashMap<>();
    private final Map<String, TextureInfo> textureInfo = new HashMap<>();
    
    private long totalTextureMemoryBytes = 0;
    
    public TextureManager(Context context) {
        this.context = context.getApplicationContext();
    }
    
    /**
     * BƯỚC 6: Load texture với optional ETC1 compression và mipmaps
     * NOTE: ETC1 compression chưa được implement đầy đủ - chỉ tính toán memory estimate
     */
    public int loadTexture(String name, Bitmap bitmap, 
                          boolean useETC1, boolean generateMipmaps) {
        try {
            // Safety checks
            if (name == null || bitmap == null) {
                Log.e(TAG, "Invalid parameters: name or bitmap is null");
                return 0;
            }
            
            if (textureCache.containsKey(name)) {
                return textureCache.get(name);
            }
            
            // Load texture from bitmap (always use standard format for now)
            // TODO: Implement ETC1 compression when useETC1 = true
            int textureId = 0;
            try {
                textureId = TextureLoader.loadTextureFromBitmap(bitmap);
            } catch (Exception e) {
                Log.e(TAG, "Error loading texture from bitmap", e);
                return 0;
            }
            
            if (textureId == 0) {
                Log.w(TAG, "Failed to load texture: " + name);
                return 0;
            }
            
            // BƯỚC 6: Generate mipmaps nếu enabled
            if (generateMipmaps) {
                try {
                    GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
                    GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
                    GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, 
                        GLES30.GL_TEXTURE_MIN_FILTER, 
                        GLES30.GL_LINEAR_MIPMAP_LINEAR);
                    GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, 
                        GLES30.GL_TEXTURE_MAG_FILTER, 
                        GLES30.GL_LINEAR);
                    Log.d(TAG, "Generated mipmaps for texture: " + name);
                } catch (Exception e) {
                    Log.e(TAG, "Error generating mipmaps", e);
                    // Continue without mipmaps
                }
            }
            
            // NOTE: ETC1 compression chưa được implement đầy đủ
            // Hiện tại chỉ tính toán memory estimate, không thực sự compress texture
            // TODO: BƯỚC 6: Implement ETC1 compression nếu useETC1 = true
            // ETC1 giảm memory từ 4 bytes/pixel xuống ~0.5 bytes/pixel
            // Cần convert bitmap sang ETC1 format trước khi upload
            
            textureCache.put(name, textureId);
            
            // Estimate memory usage
            TextureInfo info = new TextureInfo();
            info.textureId = textureId;
            info.width = bitmap.getWidth();
            info.height = bitmap.getHeight();
            
            if (useETC1) {
                // ETC1: ~0.5 bytes per pixel (estimate only - not actually compressed yet)
                info.memoryBytes = (long) (bitmap.getWidth() * bitmap.getHeight() * 0.5f);
            } else {
                // RGBA8888: 4 bytes per pixel
                info.memoryBytes = (long) bitmap.getWidth() * bitmap.getHeight() * 4;
            }
            
            if (generateMipmaps) {
                // Mipmaps add ~33% more memory
                info.memoryBytes = (long) (info.memoryBytes * 1.33f);
            }
            
            textureInfo.put(name, info);
            totalTextureMemoryBytes += info.memoryBytes;
            
            Log.d(TAG, String.format(
                "Loaded texture: %s, Size: %dx%d, Memory: %.2f MB (ETC1: %s, Mipmaps: %s)",
                name, info.width, info.height, 
                info.memoryBytes / (1024.0f * 1024.0f),
                useETC1 ? "Yes (estimate)" : "No",
                generateMipmaps ? "Yes" : "No"));
            
            return textureId;
        } catch (Throwable e) {
            Log.e(TAG, "Critical error in loadTexture", e);
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Get texture ID by name
     */
    public int getTexture(String name) {
        Integer id = textureCache.get(name);
        return id != null ? id : 0;
    }
    
    /**
     * Bind texture by name
     */
    public void bindTexture(String name, int textureUnit) {
        int textureId = getTexture(name);
        if (textureId != 0) {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + textureUnit);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        }
    }
    
    /**
     * BƯỚC 6: Get total texture memory usage in MB
     */
    public float getTotalTextureMemoryMB() {
        return totalTextureMemoryBytes / (1024.0f * 1024.0f);
    }
    
    /**
     * Release all textures
     */
    public void releaseAll() {
        int[] textureIds = new int[textureCache.size()];
        int i = 0;
        for (Integer id : textureCache.values()) {
            textureIds[i++] = id;
        }
        GLES30.glDeleteTextures(textureIds.length, textureIds, 0);
        textureCache.clear();
        textureInfo.clear();
        totalTextureMemoryBytes = 0;
    }
    
    private static class TextureInfo {
        int textureId;
        int width;
        int height;
        long memoryBytes;
    }
}

