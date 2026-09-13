#version 150

uniform sampler2D Sampler0;
uniform vec3 color;
uniform vec3 color2;
uniform float time;
uniform float fill;
uniform float alpha;
uniform vec2 texelSize;

in vec2 TexCoord;
out vec4 OutColor;

// ========== ШУМЫ ==========
float hash(vec2 p) {
    p = fract(p * vec2(123.34, 345.45));
    p += dot(p, p + 34.345);
    return fract(p.x * p.y);
}

float smoothNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm(vec2 p) {
    float value = 0.0;
    float amp = 0.5;
    float freq = 1.0;
    for (int i = 0; i < 4; i++) {
        value += smoothNoise(p * freq) * amp;
        freq *= 2.1;
        amp *= 0.48;
    }
    return value;
}

void main() {
    vec4 texColor = texture(Sampler0, TexCoord);
    float mask = texColor.a;

    if (mask <= 0.001) discard;

    float t = time;
    vec2 uv = TexCoord;

    // ========== ПЛАВНЫЕ ЭФФЕКТЫ ==========

    // 1. Северное сияние (плавное)
    vec2 p1 = uv * 3.0;
    float wave1 = sin(p1.x * 8.0 + t * 0.3 + fbm(vec2(p1.x * 2.0, t * 0.2)) * 2.0);
    float wave2 = sin(p1.x * 6.0 - t * 0.25 + fbm(vec2(p1.x * 1.5, t * 0.3 + 5.0)) * 1.5);
    float height = 1.0 - uv.y;
    float aurora = (wave1 * 0.6 + wave2 * 0.4) * height * height * 2.0;
    aurora = clamp(aurora, 0.0, 1.0);

    // 2. Переливы (плавные)
    vec2 p2 = uv * 4.0;
    float irid = sin(p2.x * 10.0 + p2.y * 7.0 + t * 0.5) * 0.5 + 0.5;
    irid = irid * 0.7 + 0.3;

    // 3. Волны света (плавные)
    vec2 p3 = uv * 3.5;
    float waves = sin(p3.x * 8.0 + p3.y * 5.0 + t * 0.4) * 0.5 + 0.5;
    waves = waves * 0.6 + 0.4;

    // 4. Светящийся туман (плавный)
    vec2 p4 = uv * 2.5;
    float fog = fbm(p4 + t * 0.03) * 0.6;
    fog += sin(p4.x * 4.0 + p4.y * 5.0 + t * 0.15) * 0.2;
    fog = clamp(fog * (0.5 + 0.5 * uv.y), 0.0, 1.0);

    // 5. Огненные языки (плавные)
    vec2 p5 = uv * 5.0;
    float fire1 = fbm(vec2(p5.x * 2.0, p5.y * 2.5 - t * 0.3));
    float fire2 = fbm(vec2(p5.x * 1.5 + 10.0, p5.y * 1.8 + t * 0.4));
    float fire = (fire1 * 0.6 + fire2 * 0.4) * (1.0 - uv.y * 0.7);
    fire = clamp(fire, 0.0, 1.0);

    // 6. Магические круги (плавные)
    vec2 center = vec2(0.5, 0.5);
    float dist = distance(uv, center);
    float circles = sin(dist * 25.0 - t * 0.8) * 0.5 + 0.5;
    circles *= 1.0 - dist * 1.0;
    circles = max(0.0, circles);
    circles = clamp(circles * 0.8, 0.0, 1.0);

    // 7. Хамелеон (плавный)
    vec2 p7 = uv * 6.0;
    float cham = sin(p7.x * 9.0 + p7.y * 7.0 + t * 0.4) * 0.5 + 0.5;
    cham = cham * 0.6 + 0.4;

    // ========== ЦВЕТА ==========

    vec3 color1 = mix(color, color2, 0.3 + 0.2 * sin(t * 0.15));
    vec3 color2_ = mix(color2, color, 0.3 + 0.2 * cos(t * 0.2));
    vec3 color3 = vec3(1.0, 0.3, 0.8);
    vec3 color4 = vec3(0.2, 0.9, 1.0);
    vec3 color5 = vec3(1.0, 0.9, 0.2);
    vec3 color6 = vec3(0.8, 0.2, 1.0);

    // Радуга (плавная)
    vec3 rainbow = vec3(
        0.5 + 0.5 * sin(irid * 6.283 + t * 0.3),
        0.5 + 0.5 * sin(irid * 6.283 + 2.094 + t * 0.5),
        0.5 + 0.5 * sin(irid * 6.283 + 4.188 + t * 0.2)
    );

    // ========== СМЕШИВАНИЕ ==========

    vec3 finalColor = vec3(0.0);

    // Базовый цвет с градиентом
    float gradient = uv.y;
    vec3 baseColor = mix(color1, color2_, gradient);
    finalColor += baseColor * 0.35;

    // Северное сияние
    finalColor += mix(color4, color3, aurora) * aurora * 0.7;

    // Радуга
    finalColor += rainbow * irid * 0.5;

    // Волны
    finalColor += mix(color5, color3, waves) * waves * 0.4;

    // Туман
    finalColor += mix(color3, color4, fog) * fog * 0.35;

    // Огонь
    finalColor += mix(color5, color3, fire) * fire * 0.5;

    // Круги
    finalColor += mix(color4, color6, circles) * circles * 0.4;

    // Хамелеон
    finalColor += mix(color1, color2_, cham) * cham * 0.25;

    // ========== СВЕЧЕНИЯ ==========

    // Центральное свечение
    float centerGlow = exp(-dist * 7.0) * 0.25;
    finalColor += vec3(1.0, 0.95, 0.9) * centerGlow;

    // Краевое свечение
    float edge = 1.0 - dist * 2.0;
    edge = clamp(edge, 0.0, 1.0);
    float edgeGlow = pow(edge, 3.0) * 0.15;
    finalColor += mix(color1, color2_, 0.5) * edgeGlow;

    // ========== ЯРКОСТЬ ==========

    float brightness = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
    finalColor *= brightness * fill;

    // ========== ПРОЗРАЧНОСТЬ ==========

    // Плавный градиент снизу вверх (без мерцания)
    float alphaGradient = uv.y * uv.y * (3.0 - 2.0 * uv.y);
    alphaGradient = clamp((uv.y - 0.05) / 0.95, 0.0, 1.0);

    float outAlpha = alpha * mask * alphaGradient;

    if (outAlpha <= 0.001) discard;
    OutColor = vec4(finalColor, outAlpha);
}