#version 300 es
precision mediump float;

// BƯỚC 1: FRAGMENT SHADER ĐƠN GIẢN
// Chỉ output texture color hoặc màu cơ bản
// Không có lighting calculations

in vec2 vTexCoord;

uniform sampler2D uTexture;
uniform bool uHeatmapMode; // Overdraw heatmap mode

out vec4 fragColor;

void main() {
    if (uHeatmapMode) {
        // Overdraw Heatmap: Output màu để tạo gradient xanh → vàng → đỏ
        // Với additive blending (GL_ONE, GL_ONE), mỗi lần pixel được vẽ sẽ add màu
        // Strategy: Dùng màu có cả đỏ và xanh, nhưng xanh nhiều hơn ban đầu
        // Khi add nhiều lần, xanh sẽ clamp ở 1.0, nhưng đỏ sẽ tiếp tục tăng → tạo gradient đỏ
        // - 1 lần vẽ: xanh lá (0.05, 0.2, 0.0) - xanh nhiều, đỏ ít
        // - 2 lần vẽ: xanh lá sáng (0.1, 0.4, 0.0) - xanh tăng, đỏ tăng
        // - 3 lần vẽ: xanh-vàng (0.15, 0.6, 0.0) - đỏ tăng, xanh tăng
        // - 4 lần vẽ: vàng-xanh (0.2, 0.8, 0.0) - đỏ tăng, xanh tăng
        // - 5 lần vẽ: vàng (0.25, 1.0, 0.0) - đỏ tăng, xanh clamp
        // - 6 lần vẽ: cam (0.3, 1.0, 0.0) - đỏ tăng nhiều hơn, xanh clamp
        // - 7 lần vẽ: cam-đỏ (0.35, 1.0, 0.0) - đỏ rõ ràng
        // - 8+ lần vẽ: đỏ (0.4+, 1.0, 0.0) - đỏ rõ ràng, xanh clamp
        // Tỷ lệ đỏ/xanh: 1/4 ban đầu, đỏ sẽ tăng nhanh hơn khi xanh clamp
        fragColor = vec4(0.05, 0.2, 0.0, 1.0); // Đỏ ít, xanh nhiều để bắt đầu từ xanh lá
    } else {
        // Normal rendering: sample texture
        fragColor = texture(uTexture, vTexCoord);
    }
}

