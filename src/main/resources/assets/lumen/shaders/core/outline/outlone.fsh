#version 150

uniform float time;
uniform float speed;
uniform float glowStrength;
uniform float density;
uniform float opacity;
uniform vec3 glowColor;
uniform int outlineType;
uniform vec2 resolution;

in vec2 TexCoord;
out vec4 fragColor;

// Функции шума (из вашего шейдера)
float hash(vec2 p) {
    p = fract(p * vec2(123.34, 345.45));
    p += dot(p, p + 34.345);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);

    float a = hash(i + vec2(0.0, 0.0));
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));

    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 5; i++) {
        value += noise(p) * amplitude;
        p = p * 2.03 + vec2(13.1, 7.7);
        amplitude *= 0.5;
    }
    return value;
}

float ridged(vec2 p) {
    float value = 0.0;
    float amplitude = 0.55;
    for (int i = 0; i < 4; i++) {
        float r = 1.0 - abs(noise(p) * 2.0 - 1.0);
        value += r * amplitude;
        p = p * 2.15 + vec2(4.7, 9.2);
        amplitude *= 0.5;
    }
    return value;
}

void main() {
    vec2 uv = TexCoord;

    float pulse = 0.7 + 0.3 * sin(time * speed * 2.5);
    float fastPulse = 0.5 + 0.5 * sin(time * speed * 12.0);

    vec2 flow = uv * (8.0 + density * 4.0);
    vec2 drift = vec2(time * speed * 0.35, time * speed * 0.25);

    float pattern = 0.0;

    // Создаем узор для линии
    vec2 warp = vec2(
        fbm(flow * 0.85 + drift * 0.55),
        fbm(flow * 0.80 - drift * 0.42)
    );
    vec2 q = flow + (warp - 0.5) * mix(1.3, 2.2, density * 0.3);

    float mist = fbm(q * 0.70 - drift * 0.18);

    float diagonal1 = 1.0 - abs(sin((q.x * 1.85 + q.y * 0.8) * 1.5 + time * speed * 2.0));
    float diagonal2 = 1.0 - abs(sin((q.x * -0.8 + q.y * 1.2) * 1.8 - time * speed * 1.8));

    pattern = clamp(diagonal1 * 0.7 + diagonal2 * 0.5 + mist * 0.3, 0.0, 1.0);
    pattern = pow(pattern, 1.2) * pulse;

    // Добавляем эффект "течения" энергии
    float flowEffect = sin(uv.y * 50.0 - time * 25.0) * 0.3 + 0.7;
    pattern *= flowEffect;

    // Цвет с свечением
    vec3 finalColor = glowColor * (0.6 + pattern * 1.2);
    finalColor += vec3(0.8, 0.4, 1.0) * pattern * 0.5;

    float alpha = clamp(pattern * glowStrength * opacity * (0.6 + 0.4 * fastPulse), 0.0, 0.9);

    fragColor = vec4(finalColor, alpha);
}