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

const mat2 ROT = mat2(0.80, 0.60, -0.60, 0.80);

float fbm(vec2 p) {
    float amp = 0.5;
    float sum = 0.0;
    for (int i = 0; i < 4; i++) {
        sum += amp * vnoise(p);
        p = ROT * p * 2.07;
        amp *= 0.5;
    }
    return sum;
}

float edgeMask(vec2 cell, float width) {
    vec2 d = min(cell, 1.0 - cell);
    float e = min(d.x, d.y);
    return 1.0 - smoothstep(0.0, width, e);
}

void main() {
    float fade = FragColor.a;
    if (fade <= 0.004) discard;

    vec2 p = WorldXZ;
    vec2 cell = fract(p);
    float t = uTime;

    vec2 q = p * (0.55 * uDensity);
    vec2 warp = vec2(fbm(q + vec2(0.0, t * 0.09)), fbm(q + vec2(4.7, -t * 0.07)));
    float clouds = pow(clamp(fbm(q + warp * 1.9 + vec2(t * 0.05, -t * 0.03)) * 1.25, 0.0, 1.0), 1.7);
    float wisps = pow(clamp(fbm(q * 2.4 - warp * 1.2) * 1.15, 0.0, 1.0), 3.0);

    vec2 g = p * 26.0;
    vec2 sc = floor(g);
    float h = hash21(sc);
    vec2 sp = vec2(hash21(sc + 11.3), hash21(sc + 27.7));
    float star = smoothstep(0.16, 0.0, length(fract(g) - sp)) * step(0.972, h)
               * (0.45 + 0.55 * sin(t * 2.6 + h * 47.0));

    vec3 base = FragColor.rgb;
    vec3 deep = mix(vec3(0.015, 0.010, 0.045), base * 0.10, 0.65);
    vec3 hot = mix(base, vec3(1.0), 0.7);

    vec3 col = deep;
    col = mix(col, base, clouds);
    col = mix(col, base.gbr * 1.15, wisps * 0.5);
    col += hot * pow(clouds, 4.0) * 0.85;
    col += vec3(0.85, 0.90, 1.0) * star * 1.8;

    float rim = edgeMask(cell, 0.06);
    col += hot * rim * 0.35 * uPulse;

    float fogNear = fbm(p * (0.22 * uDensity) + vec2(t * 0.030, -t * 0.021));
    float fogMid  = fbm(p * (0.48 * uDensity) + vec2(-t * 0.055, t * 0.038) + warp * 0.8);
    float fogFar  = fbm(p * (1.05 * uDensity) + vec2(t * 0.092, t * 0.067) - warp * 1.3);
    float fog = fogNear * 0.50 + fogMid * 0.32 + fogFar * 0.18;
    fog = pow(clamp(fog * 1.45, 0.0, 1.0), 1.35);

    float shafts = pow(clamp(fbm(p * vec2(0.35, 2.6) + vec2(0.0, t * 0.24)), 0.0, 1.0), 2.2);
    fog = clamp(fog + shafts * 0.28, 0.0, 1.0);

    vec3 fogColor = mix(base, hot, 0.35);
    col += fogColor * fog * (0.55 + 0.45 * uPulse);
    col = mix(col, fogColor * 0.85, fog * 0.30);

    float density = clamp(0.30 + clouds * 0.9 + wisps * 0.5 + star * 3.0 + fog * 1.15, 0.0, 2.2);
    float a = fade * clamp(0.28 + density * 0.52, 0.0, 1.0);
    if (a <= 0.004) discard;
    OutColor = vec4(col * (0.8 + density * 0.45), a);
}
