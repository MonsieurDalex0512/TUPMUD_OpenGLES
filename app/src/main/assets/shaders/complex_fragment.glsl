#version 300 es
precision mediump float;

// BƯỚC 1: FRAGMENT SHADER PHỨC TẠP
// Phong lighting với multiple lights
// Specular highlights, nhiều phép tính toán học

in vec3 vNormal;
in vec3 vPosition;
in vec2 vTexCoord;

uniform sampler2D uTexture;
uniform vec3 uViewPosition;

// Multiple lights (4 lights)
uniform vec3 uLightPositions[4];
uniform vec3 uLightColors[4];
uniform float uLightIntensities[4];

out vec4 fragColor;

void main() {
    vec4 texColor = texture(uTexture, vTexCoord);
    vec3 normal = normalize(vNormal);
    vec3 viewDir = normalize(uViewPosition - vPosition);
    
    vec3 finalColor = vec3(0.0);
    
    // Tính toán lighting từ 4 lights (nhiều phép tính)
    for (int i = 0; i < 4; i++) {
        vec3 lightDir = normalize(uLightPositions[i] - vPosition);
        float distance = length(uLightPositions[i] - vPosition);
        
        // Attenuation calculation (phép tính phức tạp)
        float attenuation = 1.0 / (1.0 + 0.1 * distance + 0.01 * distance * distance);
        
        // Diffuse lighting
        float ndotl = max(dot(normal, lightDir), 0.0);
        vec3 diffuse = uLightColors[i] * ndotl * uLightIntensities[i] * attenuation;
        
        // Specular lighting (phép tính phức tạp nhất)
        vec3 reflectDir = reflect(-lightDir, normal);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);
        vec3 specular = uLightColors[i] * spec * 0.5 * uLightIntensities[i] * attenuation;
        
        finalColor += diffuse + specular;
    }
    
    // Ambient
    finalColor += vec3(0.1);
    
    fragColor = texColor * vec4(finalColor, 1.0);
}

