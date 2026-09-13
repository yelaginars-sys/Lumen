#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D MaskSampler;
uniform float intensity;
uniform float time;

in vec2 texCoord;
in vec4 vertexColor;
out vec4 fragColor;

void main() {
    vec4 fire = texture(DiffuseSampler, texCoord);
    float mask = texture(MaskSampler, texCoord).r;

    // Применяем маску к огню
    float fireAlpha = fire.a * mask * intensity;

    // Добавляем небольшую пульсацию
    float pulse = 0.9 + 0.1 * sin(time * 2.0 + texCoord.x * 20.0 + texCoord.y * 20.0);
    fireAlpha *= pulse;

    fragColor = vec4(fire.rgb * fireAlpha, fireAlpha);
}