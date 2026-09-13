#version 150

uniform float uTime;
uniform float uPulse;
uniform float uDensity;

in vec2 WorldXZ;
in vec4 FragColor;
out vec4 OutColor;

float hash21(vec2 p) {
    p = fract(p * vec2(127.1, 311.7));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float gridDistance(vec2 p) {
    vec2 cell = abs(fract(p) - 0.5);
    return 0.5 - max(cell.x, cell.y);
}

void main() {
    float fade = FragColor.a;
    if (fade <= 0.004) discard;

    vec2 p = WorldXZ;
    float t = uTime;

    vec2 warp = vec2(
        vnoise(p * 2.6 + vec2(0.0, t * 0.45)),
        vnoise(p * 3.1 + vec2(5.2, -t * 0.37))
    ) - 0.5;

    float dist = gridDistance(p + warp * 0.030 * uDensity);

    float px = fwidth(dist);
    float width = max(px * 1.15, 0.0016);

    float line = 1.0 - smoothstep(width, width * 2.2, dist);
    float glowNear = exp(-dist / max(width * 9.0, 0.012));
    float glowFar = exp(-dist / max(width * 34.0, 0.055));

    float shimmer = 0.5 + 0.5 * sin((p.x + p.y) * 2.2 - t * 2.6);
    float breath = mix(0.82, 1.20, 0.5 + 0.5 * sin(t * 2.1 + (p.x + p.y) * 0.35));
    float energy = uPulse * breath;

    vec3 base = FragColor.rgb;
    vec3 hot = mix(base, vec3(1.0), 0.90);
    vec3 shift = mix(base.gbr, base, 0.35);

    vec3 col = vec3(0.0);
    col += hot * line * 2.60;
    col += base * glowNear * 1.15 * energy;
    col += shift * glowFar * 0.42 * energy;
    col += hot * line * shimmer * 0.45 * energy;

    float intensity = line * 1.25 + glowNear * 0.85 + glowFar * 0.30;
    float a = fade * clamp(intensity, 0.0, 1.0);
    if (a <= 0.004) discard;
    OutColor = vec4(col, a);
}
