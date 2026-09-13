#version 150

in vec2 uv;
out vec4 OutColor;

uniform float Time;        // секунды
uniform vec3  Theme;       // цвет темы клиента (0..1)
uniform vec2  Resolution;  // размер экрана (px)

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float vnoise(vec2 p) {
    vec2 i = floor(p), f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p) {
    float v = 0.0, a = 0.5;
    mat2 m = mat2(1.6, 1.2, -1.2, 1.6);
    for (int i = 0; i < 5; i++) {
        v += a * vnoise(p);
        p = m * p;
        a *= 0.5;
    }
    return v;
}

void main() {
    vec2 asp = vec2(Resolution.x / max(Resolution.y, 1.0), 1.0);
    vec2 p = (uv - 0.5) * asp;

    vec3 theme  = Theme;
    vec3 dark   = theme * 0.04 + 0.012;
    vec3 deep   = theme * 0.30;
    vec3 bright = mix(theme, vec3(1.0), 0.55);
    vec3 accent = clamp(vec3(theme.b * 0.9 + 0.1, theme.r * 0.6 + 0.2, theme.g * 0.9 + 0.1), 0.0, 1.0);

    // базовый градиент с потемнением к краям
    vec3 col = mix(deep * 0.55, dark, clamp(length(p) * 0.8, 0.0, 1.0));

    // текучая туманность через domain warping
    float t = Time * 0.04;
    vec2 q = vec2(fbm(p * 1.4 + vec2(0.0, t)), fbm(p * 1.4 + vec2(3.7, -t)));
    float f = clamp(fbm(p * 1.4 + 2.2 * q + vec2(t * 0.6, 0.0)), 0.0, 1.0);
    col = mix(col, mix(deep, accent, f), f * 0.75);
    col += bright * pow(f, 3.5) * 0.6;

    // мягкая аврора-лента по центру
    float ribbon = smoothstep(0.28, 0.5, uv.y) * (1.0 - smoothstep(0.5, 0.85, uv.y));
    float wave = sin(p.x * 2.5 + fbm(p * 2.0 + t * 1.5) * 6.0 + Time * 0.4) * 0.5 + 0.5;
    col += mix(accent, bright, wave) * ribbon * wave * 0.22;

    // лёгкий движущийся блик
    float sheen = (sin(uv.x * 3.1416 + Time * 0.2) * 0.5 + 0.5) * 0.05;
    col += bright * sheen;

    // виньетка
    float vig = smoothstep(1.25, 0.35, length((uv - 0.5) * vec2(1.1, 1.25)));
    col *= mix(0.55, 1.0, vig);

    // тонмаппинг
    col = col / (col + vec3(0.75)) * 1.3;
    col = pow(col, vec3(0.95));

    OutColor = vec4(col, 1.0);
}
