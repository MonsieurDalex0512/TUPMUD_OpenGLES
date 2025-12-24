#version 300 es
precision mediump float;

// BƯỚC 1: SHADER PHỨC TẠP (để so sánh)
// Có normal transformation, multiple matrix calculations
// Nhiều phép tính toán học hơn

in vec4 aPosition;
in vec3 aNormal;
in vec2 aTexCoord;

uniform mat4 uMVPMatrix;
uniform mat4 uModelMatrix;
uniform mat4 uViewMatrix;
uniform mat3 uNormalMatrix;

out vec3 vNormal;
out vec3 vPosition;
out vec2 vTexCoord;

void main() {
    // Tính toán vị trí trong world space
    vec4 worldPos = uModelMatrix * aPosition;
    vPosition = vec3(worldPos);
    
    // Transform normal với normal matrix (phép tính phức tạp hơn)
    vNormal = normalize(uNormalMatrix * aNormal);
    
    // Tính toán vị trí clip space
    gl_Position = uMVPMatrix * aPosition;
    
    vTexCoord = aTexCoord;
}

