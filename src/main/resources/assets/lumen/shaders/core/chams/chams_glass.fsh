#version 150

uniform sampler2D Sampler0;

uniform vec2 ScreenSize;
uniform float time;
uniform float distortStrength;
uniform float roughness;
uniform float reflectStrength;
uniform float glassAlpha;
uniform float fresnelStrength;

in vec3 ViewPos;
in vec2 TexCoord;
in vec4 FragColor;

out vec4 OutColor;

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

void main() {
    // Нормаль грани из экранных производных — в формате вершин нет атрибута нормали
    vec3 flatNormal = normalize(cross(dFdx(ViewPos), dFdy(ViewPos)));

    // Микрорельеф: шероховатость возмущает нормаль двумя октавами шума.
    // Узор привязан к TexCoord — "держится" за части тела при анимации.
    vec2 frostUv = TexCoord * 1.6;
    float bumpA = noise(frostUv * 9.0 + vec2(time * 0.020, -time * 0.015)) - 0.5;
    float bumpB = noise(frostUv * 9.0 + vec2(37.2, 11.8) + vec2(-time * 0.017, time * 0.021)) - 0.5;
    float fineA = noise(frostUv * 27.0) - 0.5;
    float fineB = noise(frostUv * 27.0 + vec2(53.1, 29.4)) - 0.5;
    vec2 micro = vec2(bumpA + fineA * 0.65, bumpB + fineB * 0.65);
    vec3 normal = normalize(flatNormal + vec3(micro * roughness * 0.9, 0.0));

    vec3 viewDir = normalize(-ViewPos);
    float facing = clamp(abs(dot(normal, viewDir)), 0.0, 1.0);
    float fresnel = pow(1.0 - facing, 3.0);

    vec2 screenUv = gl_FragCoord.xy / ScreenSize;

    // Преломление: сдвиг выборки вдоль нормали + лёгкая хроматическая аберрация
    vec2 refrShift = normal.xy * (distortStrength * 46.0) / ScreenSize;
    vec2 uvR = clamp(screenUv - refrShift * 1.18, 0.002, 0.998);
    vec2 uvG = clamp(screenUv - refrShift, 0.002, 0.998);
    vec2 uvB = clamp(screenUv - refrShift * 0.82, 0.002, 0.998);
    vec3 refracted = vec3(
        texture(Sampler0, uvR).r,
        texture(Sampler0, uvG).g,
        texture(Sampler0, uvB).b
    );

    // Отражение: экранное приближение по отражённому лучу;
    // шероховатость слегка "размазывает" отражение через возмущённую нормаль
    vec3 reflDir = reflect(-viewDir, normal);
    vec2 reflUv = clamp(screenUv + reflDir.xy * (0.16 + roughness * 0.05), 0.002, 0.998);
    vec3 reflected = texture(Sampler0, reflUv).rgb;

    // Баланс Френеля: в лоб — преломление, по касательной — отражение
    float reflAmount = clamp((0.06 + fresnel * 1.05) * reflectStrength, 0.0, 0.9);
    vec3 glass = mix(refracted, reflected, reflAmount);

    // Матовая фактура: нейтральное зерно и редкие микроблики, без цвета темы
    float grain = noise(frostUv * 52.0) * 0.6 + noise(frostUv * 104.0 + 17.3) * 0.4;
    glass += (grain - 0.5) * (0.03 + roughness * 0.05);
    float sparkle = pow(clamp(noise(frostUv * 30.0 + vec2(time * 0.008, -time * 0.006)), 0.0, 1.0), 10.0);
    glass += sparkle * (0.04 + fresnel * 0.14);

    // Светлая кайма по граням — нейтрально-белая
    glass = mix(glass, vec3(1.0), clamp(fresnel * fresnelStrength, 0.0, 1.0) * 0.30);

    // FragColor.a сохраняет работу "Пульсирования"; rgb намеренно не используется
    float alphaBase = clamp(FragColor.a * 2.0, 0.0, 1.0);
    float alpha = clamp(glassAlpha * alphaBase * (0.82 + fresnel * 0.35), 0.0, 1.0);

    if (alpha <= 0.002) {
        discard;
    }

    OutColor = vec4(glass, alpha);
}
