#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform vec3 color;
uniform vec3 color2;
uniform float exposure;
uniform float time;
uniform float animate;

in vec2 TexCoord;
out vec4 OutColor;

float max4(vec4 value) {
    return max(max(value.r, value.g), max(value.b, value.a));
}

void main() {
    vec2 uv = TexCoord;
    float mask = max4(texture(Sampler1, uv));
    float bloom = max4(texture(Sampler0, uv));

    float shell = max(bloom - mask, 0.0);
    float ring = smoothstep(0.018, 0.060, shell) * (1.0 - smoothstep(0.105, 0.165, shell));
    float crisp = smoothstep(0.010, 0.032, shell) * (1.0 - smoothstep(0.060, 0.095, shell));
    float edge = smoothstep(0.50, 0.92, mask);
    float pulse = 1.0 + 0.04 * sin(time * max(0.0, animate) * 1.35 + uv.y * 4.0);
    float intensity = (ring * 0.95 + crisp * 0.70 + edge * 0.12) * exposure * pulse;
    intensity = clamp(intensity, 0.0, 1.0);

    if (intensity <= 0.002) {
        discard;
    }

    vec3 gradient = mix(color2, color, clamp(edge * 0.55 + crisp * 0.35 + uv.y * 0.10, 0.0, 1.0));
    float alpha = clamp(intensity * (0.62 + ring * 0.22 + crisp * 0.18), 0.0, 1.0);

    OutColor = vec4(gradient * min(1.0, intensity), alpha);
}
