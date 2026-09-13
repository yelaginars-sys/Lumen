#version 150

uniform float time;
uniform float speed;
uniform float patternScale;
uniform float fillAlpha;

in vec3 WorldPos;
in vec3 ViewPos;
in vec4 FragColor;

out vec4 OutColor;

float hash11_1(float p) {
    p = fract(p * 0.1031);
    p *= p + 33.33;
    p *= p + p;
    return fract(p);
}

vec4 hash11_4(vec4 p) {
    p = fract(p * 0.1031);
    p *= p + 33.33;
    p *= p + p;
    return fract(p);
}

float vnoise3(vec3 p) {
    vec3 i = floor(p), f = fract(p);
    vec3 u = f * f * (3.0 - 2.0 * f);
    float n = i.x + i.y * 157.0 + i.z * 113.0;
    vec4 n4_0 = vec4(n, n + 157.0, n + 113.0, n + 270.0);
    vec4 n4_1 = n4_0 + 1.0;
    vec4 h0 = hash11_4(n4_0);
    vec4 h1 = hash11_4(n4_1);
    vec4 mix_x = mix(h0, h1, u.x);
    vec2 mix_y = mix(mix_x.xz, mix_x.yw, u.y);
    return mix(mix_y.x, mix_y.y, u.z);
}

float fbm3(vec3 p) {
    float v = 0.0, a = 0.5;
    for (int i = 0; i < 4; i++) {
        v += a * vnoise3(p);
        p = p * 2.02 + vec3(19.0, 33.0, 47.0);
        a *= 0.5;
    }
    return v;
}

vec3 sat(vec3 c) {
    return clamp(c, 0.0, 1.0);
}

void main() {
    // Палитра из цвета темы — как в шейдере неба Ambience ("Разводы")
    vec3 themeC = FragColor.rgb;
    vec3 dark = themeC * 0.05;
    vec3 deep = themeC * 0.30;
    vec3 bright = mix(themeC, vec3(1.0), 0.45);
    vec3 accent = sat(vec3(themeC.b * 0.9 + 0.1, themeC.r * 0.7 + 0.2, themeC.g));

    // Текучие разводы: двойной доменный варп fbm в мировых координатах
    vec3 p = WorldPos * (2.1 * patternScale);
    float t = time * speed * 0.10;
    vec3 q = vec3(
        fbm3(p + vec3(0.0, 0.0, t)),
        fbm3(p + vec3(5.2, 1.3, t)),
        fbm3(p + vec3(1.7, 9.2, t))
    );
    vec3 r = vec3(
        fbm3(p + 4.0 * q + vec3(1.7, 9.2, 0.10 * t)),
        fbm3(p + 4.0 * q + vec3(8.3, 2.8, 0.12 * t)),
        fbm3(p + 4.0 * q + vec3(2.8, 8.3, 0.15 * t))
    );
    float f = fbm3(p + 4.0 * r);

    vec3 col = mix(deep, bright, clamp(f * f * 1.6, 0.0, 1.0));
    col = mix(col, accent, clamp(length(q) - 0.4, 0.0, 1.0) * 0.7);
    col = mix(col, dark, clamp(r.x * 0.7, 0.0, 1.0) * 0.5);
    col += bright * pow(clamp(f, 0.0, 1.0), 3.0) * 0.4;

    // лёгкий световой обод по граням — силуэт читается объёмным
    vec3 normal = normalize(cross(dFdx(ViewPos), dFdy(ViewPos)));
    float facing = clamp(abs(dot(normal, normalize(-ViewPos))), 0.0, 1.0);
    col += bright * pow(1.0 - facing, 2.5) * 0.35;

    // мягкий тонмап из оригинала
    col = col / (col + vec3(0.7)) * 1.25;
    col = pow(col, vec3(0.95));

    // FragColor.a сохраняет работу "Пульсирования"
    float alphaBase = clamp(FragColor.a * 2.0, 0.0, 1.0);
    float outAlpha = clamp(fillAlpha * alphaBase, 0.0, 1.0);

    if (outAlpha <= 0.002) {
        discard;
    }

    OutColor = vec4(col, outAlpha);
}
