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

float surface(vec2 p, float t, float goo) {
    vec2 q = p * 1.6;
    vec2 warp = vec2(fbm(q + vec2(1.7, t * 0.14)), fbm(q + vec2(-4.3, t * 0.10)));
    float body = fbm(q + warp * 1.7 + vec2(0.0, t * 0.16));
    float runnel = vnoise(vec2(p.x * 5.0, p.y * 1.2 + t * 0.40)) * 0.65
                 + vnoise(vec2(p.x * 11.0, p.y * 2.4 + t * 0.62)) * 0.35;
    runnel = smoothstep(0.30, 0.88, runnel);
    return clamp(body * 0.58 + runnel * 0.42 * goo, 0.0, 1.0);
}

void main() {
    float fade = FragColor.a;
    if (fade <= 0.004) discard;

    vec2 p = WorldXZ;
    vec2 cell = fract(p);
    float t = uTime;
    float goo = clamp(uDensity, 0.2, 2.0);

    float d = 0.012;
    float h = surface(p, t, goo);
    vec2 grad = vec2(h - surface(p + vec2(d, 0.0), t, goo),
                     h - surface(p + vec2(0.0, d), t, goo));
    grad *= mix(28.0, 14.0, clamp(goo, 0.0, 1.0));

    float rim = edgeMask(cell, 0.10);
    grad += normalize(vec2(cell.x - 0.5, cell.y - 0.5) + 1.0E-5) * rim * 3.2;

    vec3 n = normalize(vec3(grad, 1.0));
    vec3 lightDir = normalize(vec3(-0.38, 0.66, 0.65));
    vec3 halfDir = normalize(lightDir + vec3(0.0, 0.0, 1.0));
    float ndh = max(dot(n, halfDir), 0.0);
    float wrap = clamp(dot(n, lightDir) * 0.5 + 0.5, 0.0, 1.0);
    float sss = pow(wrap, 1.5);
    float spec = pow(ndh, 34.0) * 0.75 + pow(ndh, 200.0) * 1.60;
    float fresnel = pow(1.0 - clamp(n.z, 0.0, 1.0), 3.0);

    vec3 hue = FragColor.rgb / max(max(FragColor.r, max(FragColor.g, FragColor.b)), 0.001);
    vec3 deep = mix(hue * 0.34, vec3(0.10, 0.11, 0.14), 0.20);
    vec3 body = mix(hue, vec3(1.0), 0.42);
    vec3 cream = mix(hue, vec3(1.0), 0.88);

    float bubbles = smoothstep(0.62, 0.93, fbm(p * 5.0 + vec2(0.0, t * 0.22)));

    vec3 col = mix(deep, body, sss);
    col = mix(col, cream, pow(h, 2.0) * 0.65);
    col = mix(col, cream, bubbles * 0.35);
    col += vec3(1.0) * spec * uPulse;
    col += cream * fresnel * 0.35;

    float a = fade * clamp(0.55 + h * 0.45, 0.0, 1.0);
    if (a <= 0.004) discard;
    OutColor = vec4(col, a);
}
