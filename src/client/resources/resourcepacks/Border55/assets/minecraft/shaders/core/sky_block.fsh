#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

in vec4 shimmer;
in vec2 texCoordBlock;
in vec4 texProjSky;
in vec2 texCoordGlint;

out vec4 fragColor;

const float borderWidth = 0.02;
const vec4 borderColor = vec4(0.0, 0.2, 0.0, 0.55);

void main() {
    vec2 texCoordAtlas = texCoordBlock * vec2(1024.0, 512.0);
    vec2 texCoordLocal = mod(texCoordAtlas, vec2(16.0, 16.0));

    texCoordLocal /= vec2(16.0, 16.0);

    float distX = min(texCoordLocal.x, 1.0 - texCoordLocal.x);
    float distY = min(texCoordLocal.y, 1.0 - texCoordLocal.y);

    vec4 textureColor = texture(Sampler0, texCoordBlock);
    vec4 skyColor = textureProj(Sampler1, texProjSky);
    vec4 glintColor = texture(Sampler2, texCoordGlint) * shimmer;

    vec4 finalColor = mix(skyColor, textureColor, glintColor * textureColor.a);

    if (distX < borderWidth || distY < borderWidth) {
        fragColor = mix(finalColor, borderColor, borderColor.a);
    } else {
        fragColor = finalColor;
    }
}
