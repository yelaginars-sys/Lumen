#version 150

uniform sampler2D Sampler0;

uniform vec3 uColor;
uniform vec2 uAspect;
uniform vec2 uTexel;
uniform float uTime;
uniform float uSpeed;
uniform float uGoo;

in vec2 TexCoord;
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

float surface(vec2 p, float t, float goo) {
    vec2 q = p * 2.6;
    vec2 warp = vec2(fbm(q + vec2(1.7, t * 0.14)), fbm(q + vec2(-4.3, t * 0.10)));
    float body = fbm(q + warp * 1.7 + vec2(0.0, t * 0.16));

    vec2 r = p + warp * 0.06;
    float runnel = vnoise(vec2(r.x * 13.0, r.y * 2.0 + t * 0.50)) * 0.65
                 + vnoise(vec2(r.x * 29.0, r.y * 4.2 + t * 0.78)) * 0.35;
    runnel = smoothstep(0.30, 0.88, runnel);

    float bead = vnoise(vec2(r.x * 13.0, r.y * 9.0 + t * 0.50));
    runnel *= 0.72 + 0.55 * bead;

    return clamp(body * 0.58 + runnel * 0.42 * goo, 0.0, 1.0);
}

float softMask(vec2 uv) {
    float m = texture(Sampler0, uv).r * 4.0;
    m += texture(Sampler0, uv + vec2(uTexel.x, 0.0)).r;
    m += texture(Sampler0, uv - vec2(uTexel.x, 0.0)).r;
    m += texture(Sampler0, uv + vec2(0.0, uTexel.y)).r;
    m += texture(Sampler0, uv - vec2(0.0, uTexel.y)).r;
    return m * 0.125;
}

vec2 formGrad(vec2 uv, float r) {
    vec2 e = uTexel * r;
    float xl = texture(Sampler0, uv - vec2(e.x, 0.0)).r;
    float xr = texture(Sampler0, uv + vec2(e.x, 0.0)).r;
    float yd = texture(Sampler0, uv - vec2(0.0, e.y)).r;
    float yu = texture(Sampler0, uv + vec2(0.0, e.y)).r;
    return vec2(xl - xr, yd - yu);
}

float innerMask(vec2 uv) {
    vec2 e = uTexel * 3.0;
    float m = texture(Sampler0, uv + vec2(e.x, 0.0)).r;
    m = min(m, texture(Sampler0, uv - vec2(e.x, 0.0)).r);
    m = min(m, texture(Sampler0, uv + vec2(0.0, e.y)).r);
    m = min(m, texture(Sampler0, uv - vec2(0.0, e.y)).r);
    m = min(m, texture(Sampler0, uv + e).r);
    m = min(m, texture(Sampler0, uv - e).r);
    return m;
}

void main() {
    vec2 uv = TexCoord;
    float mask = softMask(uv);
    if (mask <= 0.004) discard;
    float inner = innerMask(uv);

    float goo = clamp(uGoo, 0.0, 2.0);
    float t = uTime * uSpeed;
    vec2 p = uv * uAspect;

    float d = 0.0035;
    float h = surface(p, t, goo);
    vec2 grad = vec2(h - surface(p + vec2(d, 0.0), t, goo),
                     h - surface(p + vec2(0.0, d), t, goo));
    grad *= mix(62.0, 34.0, clamp(goo, 0.0, 1.0));

    grad += (formGrad(uv, 4.0) * 0.6 + formGrad(uv, 9.0) * 0.4) * 3.2;

    vec3 n = normalize(vec3(grad, 1.0));
    vec3 lightDir = normalize(vec3(-0.38, 0.66, 0.65));
    vec3 halfDir = normalize(lightDir + vec3(0.0, 0.0, 1.0));
    float ndh = max(dot(n, halfDir), 0.0);
    float wrap = clamp(dot(n, lightDir) * 0.5 + 0.5, 0.0, 1.0);
    float sss = pow(wrap, 1.5);
    float spec = pow(ndh, 34.0) * 0.75 + pow(ndh, 200.0) * 1.60;
    float fresnel = pow(1.0 - clamp(n.z, 0.0, 1.0), 3.0);

    vec3 hue = uColor / max(max(uColor.r, max(uColor.g, uColor.b)), 0.001);
    vec3 deep = mix(hue * 0.34, vec3(0.10, 0.11, 0.14), 0.20);
    vec3 body = mix(hue, vec3(1.0), 0.42);
    vec3 cream = mix(hue, vec3(1.0), 0.88);

    float bubbles = smoothstep(0.62, 0.93, fbm(p * 11.0 + vec2(0.0, t * 0.22)));

    vec3 col = mix(deep, body, sss);
    col = mix(col, cream, pow(h, 2.0) * 0.65);
    col = mix(col, cream, bubbles * 0.35);
    col += vec3(1.0) * spec;
    col += cream * fresnel * 0.35 * inner;

    OutColor = vec4(col, smoothstep(0.10, 0.55, mask));
}
