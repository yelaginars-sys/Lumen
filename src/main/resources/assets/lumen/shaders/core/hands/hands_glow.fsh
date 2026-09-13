#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform vec3 color;
uniform vec3 color2;
uniform float exposure;
uniform float time;
uniform float fireIntensity;
uniform float fireSpeed;
uniform float fireDirection;

in vec2 TexCoord;
out vec4 OutColor;

void main() {
    vec2 uv = TexCoord;

    // --- Основное волновое смещение для обводки ---
    float waveOffsetX = sin(time * fireSpeed * 0.5 + uv.y * 8.0 + fireDirection * 2.0) * 0.03 * fireIntensity;
    float waveOffsetY = cos(time * fireSpeed * 0.3 + uv.x * 6.0 + fireDirection * 1.5) * 0.02 * fireIntensity;
    vec2 waveUv = uv + vec2(waveOffsetX, waveOffsetY);

    // --- Эффект "следа" пламени (вторичные смещения) ---
    float trailOffset1 = 0.025 * fireIntensity * sin(time * fireSpeed * 0.7 + uv.y * 12.0 + fireDirection * 4.0);
    float trailOffset2 = 0.015 * fireIntensity * cos(time * fireSpeed * 0.5 + uv.x * 9.0 + fireDirection * 3.0);
    vec2 trailUv1 = uv + vec2(trailOffset1 * 1.2, trailOffset2 * 0.8) + vec2(fireDirection * 0.01, 0.0);

    float trailOffset3 = 0.035 * fireIntensity * cos(time * fireSpeed * 0.9 + uv.y * 15.0 + fireDirection * 5.0);
    float trailOffset4 = 0.025 * fireIntensity * sin(time * fireSpeed * 0.6 + uv.x * 11.0 + fireDirection * 3.5);
    vec2 trailUv2 = uv + vec2(trailOffset3 * 1.5, trailOffset4 * 0.6) + vec2(fireDirection * 0.015, -0.005);

    float sparkOffsetX = 0.04 * fireIntensity * sin(time * fireSpeed * 1.2 + uv.y * 20.0 + fireDirection * 6.0);
    float sparkOffsetY = 0.03 * fireIntensity * cos(time * fireSpeed * 0.8 + uv.x * 14.0 + fireDirection * 4.5);
    vec2 sparkUv = uv + vec2(sparkOffsetX, sparkOffsetY) + vec2(fireDirection * 0.02, 0.01);

    // --- Сборка всех слоев обводки ---
    vec4 bloom_main = texture(Sampler0, waveUv);
    vec4 bloom_trail1 = texture(Sampler0, trailUv1);
    vec4 bloom_trail2 = texture(Sampler0, trailUv2);
    vec4 bloom_spark = texture(Sampler0, sparkUv);

    vec4 mask = texture(Sampler1, uv);

    float outer_main = bloom_main.a * (1.0 - mask.a);
    float outer_trail1 = bloom_trail1.a * (1.0 - mask.a) * 0.5;
    float outer_trail2 = bloom_trail2.a * (1.0 - mask.a) * 0.3;
    float outer_spark = bloom_spark.a * (1.0 - mask.a) * 0.7;

    float outer = outer_main + outer_trail1 + outer_trail2 + outer_spark;

    // --- Волновое мерцание с эффектом пульсации ---
    float wavePulse = 0.7 + 0.3 * sin(time * fireSpeed * 0.4 + uv.x * 10.0 + uv.y * 8.0 + fireDirection * 3.0);
    float breathPulse = 0.9 + 0.1 * sin(time * fireSpeed * 0.2 + fireDirection * 2.0);
    outer *= wavePulse * breathPulse;

    // --- Градиент: color (как есть) к color2 (тёмный) ---
    vec3 darkColor2 = color2 * 0.4;  // Затемняем только второй цвет

    // Основной градиент: color (обычный) к color2 (тёмный)
    vec3 grad = mix(color, darkColor2, uv.y);

    // Следы — с градиентом темы
    vec3 trailColor = mix(color, darkColor2, uv.y * 0.7);
    float trailMix = (outer_trail1 + outer_trail2) / (outer_main + 0.001);
    grad = mix(grad, trailColor * 1.1, trailMix * 0.3);

    // Искры — чуть ярче
    vec3 sparkColor = mix(color, darkColor2, uv.y * 0.5) * 1.3;
    float sparkMix = outer_spark / (outer + 0.001);
    grad = mix(grad, sparkColor, sparkMix * 0.4);

    // Интенсивность с экспозицией
    float intensity = clamp(outer * exposure, 0.0, 1.0);

    // --- Эффект "свечения" в тонах темы ---
    float glow = intensity * 0.15 * (1.0 - uv.y);
    grad += color * glow * 0.5;

    if (intensity <= 0.001) discard;
    OutColor = vec4(grad, intensity);
}