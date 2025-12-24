#version 300 es
precision mediump float;

// BƯỚC 1: SHADER ĐƠN GIẢN
// Chỉ tính toán vị trí, không có lighting phức tạp
// Giảm thiểu các phép tính toán học không cần thiết

in vec4 aPosition;
in vec3 aNormal;
in vec2 aTexCoord;

uniform mat4 uMVPMatrix;

out vec2 vTexCoord;

void main() {
    // Chỉ tính toán vị trí - đơn giản nhất có thể
    gl_Position = uMVPMatrix * aPosition;
    
    // Chỉ pass through texture coordinates
    vTexCoord = aTexCoord;
}

