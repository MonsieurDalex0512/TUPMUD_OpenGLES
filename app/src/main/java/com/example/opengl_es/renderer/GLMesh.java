package com.example.opengl_es.renderer;

import android.opengl.GLES30;

import com.example.opengl_es.scene.Mesh;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * OpenGL mesh wrapper that holds VBOs and IBO for a Mesh.
 */
public class GLMesh {
    
    private final int[] vbos = new int[4]; // 0: vertices, 1: normals, 2: texCoords, 3: indices
    private final Mesh mesh;
    private final int indexCount;
    
    public GLMesh(Mesh mesh) {
        this.mesh = mesh;
        this.indexCount = mesh.indexCount;
        
        // Generate buffers
        GLES30.glGenBuffers(4, vbos, 0);
        
        // Upload vertex data
        uploadBuffer(vbos[0], mesh.vertices, GLES30.GL_ARRAY_BUFFER);
        uploadBuffer(vbos[1], mesh.normals, GLES30.GL_ARRAY_BUFFER);
        uploadBuffer(vbos[2], mesh.texCoords, GLES30.GL_ARRAY_BUFFER);
        uploadIndexBuffer(vbos[3], mesh.indices);
    }
    
    private void uploadBuffer(int vbo, float[] data, int target) {
        GLES30.glBindBuffer(target, vbo);
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(data);
        buffer.position(0);
        GLES30.glBufferData(target, data.length * 4, buffer, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(target, 0);
    }
    
    private void uploadIndexBuffer(int ibo, short[] data) {
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ibo);
        ByteBuffer bb = ByteBuffer.allocateDirect(data.length * 2);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer buffer = bb.asShortBuffer();
        buffer.put(data);
        buffer.position(0);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, data.length * 2, buffer, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    
    public void bind(int positionLoc, int normalLoc, int texCoordLoc) {
        // Bind vertex buffer
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbos[0]);
        GLES30.glEnableVertexAttribArray(positionLoc);
        GLES30.glVertexAttribPointer(positionLoc, 3, GLES30.GL_FLOAT, false, 0, 0);
        
        // Bind normal buffer
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbos[1]);
        GLES30.glEnableVertexAttribArray(normalLoc);
        GLES30.glVertexAttribPointer(normalLoc, 3, GLES30.GL_FLOAT, false, 0, 0);
        
        // Bind texCoord buffer
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbos[2]);
        GLES30.glEnableVertexAttribArray(texCoordLoc);
        GLES30.glVertexAttribPointer(texCoordLoc, 2, GLES30.GL_FLOAT, false, 0, 0);
        
        // Bind index buffer
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, vbos[3]);
    }
    
    public void draw() {
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indexCount, GLES30.GL_UNSIGNED_SHORT, 0);
    }
    
    public void unbind() {
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    
    public void release() {
        GLES30.glDeleteBuffers(4, vbos, 0);
    }
    
    public int getTriangleCount() {
        return indexCount / 3;
    }
}

