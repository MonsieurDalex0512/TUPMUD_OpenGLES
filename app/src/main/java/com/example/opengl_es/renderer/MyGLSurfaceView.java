package com.example.opengl_es.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Custom GLSurfaceView configured for OpenGL ES 3.0.
 * Exposes hooks for toggling optimizations via RenderConfig.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer renderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Request an OpenGL ES 3.0 compatible context
        setEGLContextClientVersion(3);

        renderer = new MyGLRenderer(context);
        setRenderer(renderer);

        // BƯỚC 4: RENDER MODE OPTIMIZATION
        // RENDERMODE_WHEN_DIRTY: Chỉ render khi có thay đổi → tiết kiệm pin (nhưng metrics không cập nhật)
        // RENDERMODE_CONTINUOUSLY: Render liên tục 60fps → tốn pin hơn (nhưng metrics cập nhật real-time)
        // Dùng CONTINUOUSLY để metrics cập nhật liên tục và có thể đo performance
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public MyGLRenderer getRenderer() {
        return renderer;
    }
}

