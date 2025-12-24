package com.example.opengl_es.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.opengl.GLES30;
import android.opengl.GLUtils;

/**
 * Utility for loading and generating textures for OpenGL ES.
 */
public class TextureLoader {
    
    /**
     * Generate procedural checkerboard texture
     */
    public static int generateCheckerboard(int size, int checkerSize) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        Paint black = new Paint();
        black.setColor(Color.BLACK);
        Paint white = new Paint();
        white.setColor(Color.WHITE);
        
        for (int y = 0; y < size; y += checkerSize) {
            for (int x = 0; x < size; x += checkerSize) {
                Paint paint = ((x / checkerSize + y / checkerSize) % 2 == 0) 
                    ? black : white;
                canvas.drawRect(x, y, x + checkerSize, y + checkerSize, paint);
            }
        }
        
        return loadTextureFromBitmap(bitmap);
    }
    
    /**
     * Load texture from Bitmap
     */
    public static int loadTextureFromBitmap(Bitmap bitmap) {
        int[] textureHandle = new int[1];
        GLES30.glGenTextures(1, textureHandle, 0);
        
        if (textureHandle[0] == 0) {
            return 0;
        }
        
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, 
            GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, 
            GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, 
            GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, 
            GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
        
        bitmap.recycle();
        return textureHandle[0];
    }
}

