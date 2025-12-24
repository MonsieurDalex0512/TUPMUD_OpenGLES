package com.example.opengl_es.scene;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Mesh generator utilities for procedural geometry.
 * Generates cube, sphere, pyramid with vertices, normals, UVs, and indices.
 */
public class Mesh {
    
    public float[] vertices;
    public float[] normals;
    public float[] texCoords;
    public short[] indices;
    
    public int vertexCount;
    public int indexCount;
    
    /**
     * Generate a cube mesh centered at origin with size 1.0
     */
    public static Mesh createCube() {
        Mesh mesh = new Mesh();
        
        // 8 vertices for a cube
        float[] v = {
            // Front face
            -0.5f, -0.5f,  0.5f,  // 0
             0.5f, -0.5f,  0.5f,  // 1
             0.5f,  0.5f,  0.5f,  // 2
            -0.5f,  0.5f,  0.5f,  // 3
            // Back face
            -0.5f, -0.5f, -0.5f,  // 4
             0.5f, -0.5f, -0.5f,  // 5
             0.5f,  0.5f, -0.5f,  // 6
            -0.5f,  0.5f, -0.5f   // 7
        };
        
        // Normals (per vertex, same as positions for cube)
        float[] n = {
            // Front
            0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,
            // Back
            0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1,
            // Top
            0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,
            // Bottom
            0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0,
            // Right
            1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
            // Left
            -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0
        };
        
        // UV coordinates
        float[] uv = {
            0, 0, 1, 0, 1, 1, 0, 1,  // Front
            1, 0, 0, 0, 0, 1, 1, 1,  // Back
            0, 1, 1, 1, 1, 0, 0, 0,  // Top
            0, 0, 1, 0, 1, 1, 0, 1,  // Bottom
            1, 0, 0, 0, 0, 1, 1, 1,  // Right
            0, 0, 1, 0, 1, 1, 0, 1   // Left
        };
        
        // Indices (12 triangles = 6 faces * 2 triangles each)
        short[] idx = {
            // Front
            0, 1, 2,  0, 2, 3,
            // Back
            5, 4, 7,  5, 7, 6,
            // Top
            3, 2, 6,  3, 6, 7,
            // Bottom
            4, 5, 1,  4, 1, 0,
            // Right
            1, 5, 6,  1, 6, 2,
            // Left
            4, 0, 3,  4, 3, 7
        };
        
        mesh.vertices = v;
        mesh.normals = n;
        mesh.texCoords = uv;
        mesh.indices = idx;
        mesh.vertexCount = 24; // 6 faces * 4 vertices
        mesh.indexCount = 36;  // 12 triangles * 3 indices
        
        return mesh;
    }
    
    /**
     * Generate a sphere mesh with specified segments.
     * @param segments Number of segments (latitude/longitude divisions)
     */
    public static Mesh createSphere(int segments) {
        Mesh mesh = new Mesh();
        List<Float> verts = new ArrayList<>();
        List<Float> norms = new ArrayList<>();
        List<Float> uvs = new ArrayList<>();
        List<Short> inds = new ArrayList<>();
        
        // Generate vertices
        for (int lat = 0; lat <= segments; lat++) {
            float theta = (float) (lat * Math.PI / segments);
            float sinTheta = (float) Math.sin(theta);
            float cosTheta = (float) Math.cos(theta);
            
            for (int lon = 0; lon <= segments; lon++) {
                float phi = (float) (lon * 2 * Math.PI / segments);
                float sinPhi = (float) Math.sin(phi);
                float cosPhi = (float) Math.cos(phi);
                
                float x = cosPhi * sinTheta;
                float y = cosTheta;
                float z = sinPhi * sinTheta;
                
                verts.add(x * 0.5f);
                verts.add(y * 0.5f);
                verts.add(z * 0.5f);
                
                norms.add(x);
                norms.add(y);
                norms.add(z);
                
                uvs.add((float) lon / segments);
                uvs.add((float) lat / segments);
            }
        }
        
        // Generate indices
        for (int lat = 0; lat < segments; lat++) {
            for (int lon = 0; lon < segments; lon++) {
                int first = lat * (segments + 1) + lon;
                int second = first + segments + 1;
                
                inds.add((short) first);
                inds.add((short) second);
                inds.add((short) (first + 1));
                
                inds.add((short) second);
                inds.add((short) (second + 1));
                inds.add((short) (first + 1));
            }
        }
        
        mesh.vertices = toFloatArray(verts);
        mesh.normals = toFloatArray(norms);
        mesh.texCoords = toFloatArray(uvs);
        mesh.indices = toShortArray(inds);
        mesh.vertexCount = verts.size() / 3;
        mesh.indexCount = inds.size();
        
        return mesh;
    }
    
    /**
     * Generate a pyramid (tetrahedron) mesh
     */
    public static Mesh createPyramid() {
        Mesh mesh = new Mesh();
        
        // 4 vertices for pyramid
        float[] v = {
             0.0f,  0.5f,  0.0f,  // Top
            -0.5f, -0.5f, -0.5f,  // Bottom left back
             0.5f, -0.5f, -0.5f,  // Bottom right back
             0.5f, -0.5f,  0.5f,  // Bottom right front
            -0.5f, -0.5f,  0.5f   // Bottom left front
        };
        
        // Normals (simplified, per face)
        float[] n = {
            0, 1, 0,  // Top normal (approximate)
            -1, -1, -1, 0, 1, -1, 1, 1, -1, 1, 1, 1, -1, 1, 1
        };
        
        float[] uv = {
            0.5f, 1.0f,  // Top
            0, 0, 1, 0, 1, 1, 0, 1  // Base
        };
        
        // 6 triangles (4 faces)
        short[] idx = {
            0, 1, 2,  // Face 1
            0, 2, 3,  // Face 2
            0, 3, 4,  // Face 3
            0, 4, 1,  // Face 4
            1, 2, 3,  // Base 1
            1, 3, 4   // Base 2
        };
        
        mesh.vertices = v;
        mesh.normals = n;
        mesh.texCoords = uv;
        mesh.indices = idx;
        mesh.vertexCount = 5;
        mesh.indexCount = 18;
        
        return mesh;
    }
    
    private static float[] toFloatArray(List<Float> list) {
        float[] arr = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }
    
    private static short[] toShortArray(List<Short> list) {
        short[] arr = new short[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }
    
    public FloatBuffer createVertexBuffer() {
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(vertices);
        buffer.position(0);
        return buffer;
    }
    
    public FloatBuffer createNormalBuffer() {
        ByteBuffer bb = ByteBuffer.allocateDirect(normals.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(normals);
        buffer.position(0);
        return buffer;
    }
    
    public FloatBuffer createTexCoordBuffer() {
        ByteBuffer bb = ByteBuffer.allocateDirect(texCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(texCoords);
        buffer.position(0);
        return buffer;
    }
    
    public ShortBuffer createIndexBuffer() {
        ByteBuffer bb = ByteBuffer.allocateDirect(indices.length * 2);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer buffer = bb.asShortBuffer();
        buffer.put(indices);
        buffer.position(0);
        return buffer;
    }
}

