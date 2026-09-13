#version 150

in vec2 FragCoord;
in vec2 TexCoord;
in vec4 FragColor;

uniform sampler2D Sampler0;
uniform vec2 Size;
uniform vec4 Radius;
uniform float Smoothness;
uniform float CornerSmoothness;
uniform float GlobalAlpha;
uniform float FresnelPower;
uniform vec3 FresnelColor;
uniform float FresnelAlpha;
uniform float BaseAlpha;
uniform int FresnelInvert;
uniform float FresnelMix;
uniform float DistortStrength;
uniform float ShineStrength;
uniform float Time;

out vec4 OutColor;

const float PI = 3.14159265359;

float roundedBoxSDF(vec2 center, vec2 size, vec4 radius) {
    radius.xy = (center.x > 0.0) ? radius.xy : radius.zw;
    radius.x = (center.y > 0.0) ? radius.x : radius.y;
    vec2 q = abs(center) - size + radius.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - radius.x;
}

float easeInOut(float t) {
    return 0.5 - 0.5 * cos(clamp(t, 0.0, 1.0) * PI);
}

// Более нежный блюр с увеличенными радиусами и сниженной интенсивностью
float blurBand(float coord, float centerPos, float coreWidth, float blurWidth) {
    float dist = abs(coord - centerPos);
    float glow = 1.0 - smoothstep(coreWidth, coreWidth + blurWidth, dist);
    // Более плавный спад
    return glow * glow * glow * 0.8; // Уменьшаем интенсивность и добавляем кубическую кривую
}

float sweepingDiagonalShine(float diagCoord, float progress) {
    float sweep = mix(-0.45, 2.45, progress);
    // Увеличиваем размытие, делаем более широкие и мягкие полосы
    float haze = blurBand(diagCoord, sweep, 0.085, 0.45);  // Было 0.052, 0.24
    float glow = blurBand(diagCoord, sweep, 0.065, 0.32);  // Было 0.028, 0.16
    float core = blurBand(diagCoord, sweep, 0.035, 0.12);  // Было 0.006, 0.030
    // Снижаем общую интенсивность для более нежного эффекта
    return haze * 0.18 + glow * 0.15 + core * 0.05; // Было 0.28, 0.24, 0.08
}

void main() {
    vec2 center = Size * 0.5;
    vec2 pos = (FragCoord * Size) - center;

    float dist = roundedBoxSDF(pos, center - 1.0, Radius);
    float alpha = 1.0 - smoothstep(-Smoothness, Smoothness, dist);

    if (alpha < 0.001) {
        discard;
    }

    float edgeDist = abs(dist);
    float maxDist = min(center.x, center.y) * 0.5;
    float edgeGradient = clamp(edgeDist / maxDist, 0.0, 1.0);

    // Берем цвет из текстуры (то, что находится за стеклом)
    vec4 texColor = texture(Sampler0, TexCoord);

    // Базовый цвет - это то, что видно сквозь стекло
    vec3 baseColor = texColor.rgb;

    // Добавляем едва заметный эффект преломления (опционально)
    vec2 distortDir = normalize(pos + vec2(0.0001));
    float edgeDistort = pow(edgeGradient, 2.0) * 0.3;
    vec2 distortedUV = TexCoord + distortDir * edgeDistort * DistortStrength * 0.15; // Уменьшаем силу искажения
    distortedUV = clamp(distortedUV, 0.0, 1.0);
    vec4 distortedColor = texture(Sampler0, distortedUV);

    // Смешиваем исходное изображение с искаженным для эффекта стекла
    baseColor = mix(baseColor, distortedColor.rgb, 0.08); // Уменьшаем смешивание

    // Минимальные блики (очень слабые, белые) - более нежные
    vec2 uv = FragCoord;
    float cycle = mod(Time * 0.35, 2.0); // Медленнее
    float phase = floor(cycle);
    float progress = easeInOut(fract(cycle));

    float stripe;
    if (phase < 0.5) {
        stripe = sweepingDiagonalShine(uv.x + uv.y, progress);
    } else {
        stripe = sweepingDiagonalShine((1.0 - uv.x) + uv.y, progress);
    }

    float innerMask = smoothstep(0.0, min(center.x, center.y) * 0.12, edgeDist); // Расширяем маску
    stripe *= innerMask;

    // Еще более слабые белые блики
    vec3 shineColor = vec3(1.0) * clamp(stripe, 0.0, 1.0) * 1.5 * max(ShineStrength, 0.0); // Было 0.15

    // Прозрачность стекла - чуть более прозрачное
    float glassAlpha = alpha * GlobalAlpha * BaseAlpha * 0.25; // Было 0.7

    // Финальный цвет - видно то, что за стеклом + слабые блики
    OutColor = vec4(baseColor + shineColor, glassAlpha * FragColor.a);
}
