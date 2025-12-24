#version 300 es
precision mediump float;

// BƯỚC 1: FRAGMENT SHADER ĐƠN GIẢN
// Chỉ output texture color hoặc màu cơ bản
// Không có lighting calculations

in vec2 vTexCoord;

uniform sampler2D uTexture;

out vec4 fragColor;

void main() {
    // Đơn giản: chỉ sample texture
    fragColor = texture(uTexture, vTexCoord);
    
    // Hoặc có thể dùng màu cố định để test:
    // fragColor = vec4(0.5, 0.7, 1.0, 1.0);
}

