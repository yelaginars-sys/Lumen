#version 150

// Объёмная туманность вокруг выделенного блока.
//
// Sampler0 — маска РАСШИРЕННОГО бокса (блок + запас uMargin). От неё нужно только
// покрытие в альфе: она растеризуется с тестом глубины, поэтому геометрия мира
// эффект перекрывает. Луч восстанавливается из обратной view-projection, а не из
// маски: в 8-битный буфер направление не влезает по точности и картинка распадается
// на квадраты в несколько пикселей. Все координаты — в блоках от угла блока.
uniform sampler2D Sampler0;

uniform mat4 uInvViewProj; // обратная view-projection: даёт луч на пиксель
uniform vec3 uCamW;     // камера в системе «угол блока = 0», блоки
uniform vec3 uBoxSize;  // размер бокса блока, блоки
uniform float uMargin;  // насколько туман вылезает за блок, блоки

uniform vec3 uColor;    // цвет темы
uniform vec3 uColor2;   // второй цвет темы
uniform float uTime;
uniform float uSpeed;   // скорость клубления
uniform float uDensity; // масштаб/плотность туманности
uniform float uStars;   // яркость звёзд
uniform float uGlow;    // сила объёмного свечения
uniform float uOutline; // ширина обводки по рёбрам
uniform float uFill;    // плотность тумана
uniform float uAlpha;

in vec2 TexCoord;
out vec4 OutColor;

const int STEPS = 24;

const mat3 M3 = mat3( 0.00,  0.80,  0.60,
                     -0.80,  0.36, -0.48,
                     -0.60, -0.48,  0.64);

/**
 * Interleaved gradient noise — дизер для смещения первого шага марша. Белый шум
 * на его месте даёт заметную попиксельную крупу, этот же паттерн разбивает кольца
 * и при этом читается гладко.
 */
float dither(vec2 p) {
    return fract(52.9829189 * fract(dot(p, vec2(0.06711056, 0.00583715))));
}

float hash31(vec3 p) {
    p = fract(p * 0.1031);
    p += dot(p, p.yzx + 33.33);
    return fract((p.x + p.y) * p.z);
}

float noise3(vec3 x) {
    vec3 i = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    float n000 = hash31(i);
    float n100 = hash31(i + vec3(1.0, 0.0, 0.0));
    float n010 = hash31(i + vec3(0.0, 1.0, 0.0));
    float n110 = hash31(i + vec3(1.0, 1.0, 0.0));
    float n001 = hash31(i + vec3(0.0, 0.0, 1.0));
    float n101 = hash31(i + vec3(1.0, 0.0, 1.0));
    float n011 = hash31(i + vec3(0.0, 1.0, 1.0));
    float n111 = hash31(i + vec3(1.0, 1.0, 1.0));
    return mix(mix(mix(n000, n100, f.x), mix(n010, n110, f.x), f.y),
               mix(mix(n001, n101, f.x), mix(n011, n111, f.x), f.y), f.z);
}

/** Три октавы — это внутренний цикл марша, каждая лишняя стоит 24 выборки. */
float fbmStep(vec3 p) {
    float f = 0.5000 * noise3(p); p = M3 * p * 2.02;
    f += 0.2500 * noise3(p);      p = M3 * p * 2.03;
    f += 0.1250 * noise3(p);
    return f;
}

/** Четыре октавы с домейн-варпом — только для «кожи» блока, она считается один раз. */
float fbmSkin(vec3 p) {
    float f = 0.5000 * noise3(p); p = M3 * p * 2.02;
    f += 0.2500 * noise3(p);      p = M3 * p * 2.03;
    f += 0.1250 * noise3(p);      p = M3 * p * 2.01;
    f += 0.0625 * noise3(p);
    return f;
}

float starfield3(vec3 p, float scale, float threshold, float speed) {
    vec3 g = p * scale;
    vec3 cell = floor(g);
    vec3 f = fract(g);
    float h = hash31(cell);
    vec3 pos = vec3(hash31(cell + 11.3), hash31(cell + 27.7), hash31(cell + 41.1));
    float twinkle = 0.45 + 0.55 * sin(uTime * speed + h * 47.0);
    return smoothstep(0.25, 0.0, length(f - pos)) * twinkle * step(threshold, h);
}

/** Пересечение луча со слэбами бокса [lo, hi]: x — вход, y — выход. */
vec2 hitBox(vec3 ro, vec3 rd, vec3 lo, vec3 hi) {
    vec3 inv = 1.0 / rd;
    vec3 a = (lo - ro) * inv;
    vec3 b = (hi - ro) * inv;
    vec3 tmin = min(a, b);
    vec3 tmax = max(a, b);
    return vec2(max(max(tmin.x, tmin.y), tmin.z),
                min(min(tmax.x, tmax.y), tmax.z));
}

void main() {
    if (texture(Sampler0, TexCoord).a <= 0.001) discard;

    // Луч на пиксель: NDC -> обратная view-projection. Начало координат матрицы
    // совпадает с камерой, поэтому распакованная точка сама и есть направление.
    vec4 world = uInvViewProj * vec4(TexCoord * 2.0 - 1.0, 1.0, 1.0);
    vec3 rd = world.xyz / world.w;
    float len = length(rd);
    if (len < 1.0e-5) discard;
    rd /= len;

    vec3 ro = uCamW;

    vec2 outer = hitBox(ro, rd, vec3(-uMargin), uBoxSize + uMargin);
    float t0 = max(outer.x, 0.0);
    if (outer.y <= t0) discard;

    // Сам блок непрозрачен: туман копится только до его передней грани, дальше
    // рисуется «кожа» блока, а за ней уже ничего не видно
    vec2 solid = hitBox(ro, rd, vec3(0.0), uBoxSize);
    bool hitsBlock = solid.y > max(solid.x, 0.0);
    float tEnd = hitsBlock ? clamp(max(solid.x, 0.0), t0, outer.y) : outer.y;

    float t = uTime * uSpeed * 0.15;
    vec3 drift = vec3(t * 0.20, -t * 0.30, -t * 0.15);
    vec3 deep = mix(vec3(0.015, 0.010, 0.045), uColor * 0.10, 0.65);
    vec3 alt = uColor2.gbr;
    vec3 hot = mix(uColor, vec3(1.0), 0.7);
    float scale = 3.2 * uDensity;

    // ===================== объёмный марш =====================
    vec3 acc = vec3(0.0);
    float transmittance = 1.0;

    float stepLen = max((tEnd - t0) / float(STEPS), 1.0e-4);
    float march = t0 + stepLen * dither(gl_FragCoord.xy); // смещение против колец

    for (int i = 0; i < STEPS; i++) {
        vec3 p = ro + rd * march;
        march += stepLen;

        // Огибающая: полная плотность внутри блока, к краю запаса гаснет в ноль
        vec3 outside = max(vec3(0.0), max(-p, p - uBoxSize));
        float dOut = length(outside);
        float env = 1.0 - smoothstep(0.0, uMargin, dOut);
        if (env <= 0.001) continue;

        vec3 lp = p / uBoxSize;
        float f = fbmStep((lp - 0.5) * scale + drift);
        float clouds = pow(clamp(f * 1.35, 0.0, 1.0), 1.8);
        float dens = clouds * env * env;

        // Свечение тоже объёмное: жмётся к поверхности блока и вытекает наружу,
        // а у рёбер ярче — снаружи ребра сразу две координаты выходят за бокс
        float m1 = max(max(outside.x, outside.y), outside.z);
        float m3 = min(min(outside.x, outside.y), outside.z);
        float m2 = outside.x + outside.y + outside.z - m1 - m3;
        float edgeness = m2 / max(m1, 1.0e-4);
        float halo = exp(-dOut * (4.0 / max(uMargin, 0.02)));
        float glowStep = halo * (0.45 + 0.55 * clamp(edgeness, 0.0, 1.0));

        vec3 stepCol = mix(deep, uColor, clouds) + hot * pow(clouds, 4.0) * 0.85;

        float a = clamp(dens * stepLen * uFill * 4.5, 0.0, 1.0);
        acc += transmittance * (stepCol * a + hot * glowStep * uGlow * stepLen * 1.1);
        transmittance *= 1.0 - a;
        if (transmittance < 0.01) break;
    }

    vec3 color = acc;
    float alpha = 1.0 - transmittance;

    // ===================== «кожа» блока =====================
    if (hitsBlock) {
        vec3 lp = clamp((ro + rd * max(solid.x, 0.0)) / uBoxSize, 0.0, 1.0);
        vec3 q = (lp - 0.5) * scale;

        vec3 warp = vec3(
            fbmSkin(q + vec3(0.0, drift.y * 2.0, 0.0)),
            fbmSkin(q + vec3(3.7, -drift.y * 1.6, 1.2)),
            fbmSkin(q + vec3(-2.1, drift.y * 1.3, 5.5))
        );
        float clouds = pow(clamp(fbmSkin(q + warp * 1.3 + drift) * 1.25, 0.0, 1.0), 1.7);
        float wisps = pow(clamp(fbmSkin(q * 2.3 - warp * 0.8 - drift) * 1.15, 0.0, 1.0), 3.0);

        vec3 skin = deep;
        skin = mix(skin, uColor, clouds);
        skin = mix(skin, alt * 1.15, wisps * 0.5);
        skin += hot * pow(clouds, 4.0) * 0.85;

        float stars = starfield3(lp, 10.0, 0.968, 0.6)
                    + starfield3(lp, 22.0, 0.988, 0.9) * 1.5;
        skin += vec3(0.85, 0.90, 1.0) * stars * uStars * 1.6;

        // Обводка: на грани одна координата прижата к 0/1, поэтому расстоянием
        // до ребра работает СРЕДНЯЯ из трёх дистанций до стенок бокса
        vec3 dd = min(lp, 1.0 - lp);
        float dMin = min(min(dd.x, dd.y), dd.z);
        float dMax = max(max(dd.x, dd.y), dd.z);
        float edge = dd.x + dd.y + dd.z - dMin - dMax;
        float rim = 1.0 - smoothstep(0.0, 0.025 * max(uOutline, 0.05), edge);
        skin += hot * rim * uGlow * 1.2;

        float density = clamp(0.35 + clouds * 0.9 + wisps * 0.5 + stars * 3.0, 0.0, 1.5);
        float skinAlpha = clamp(uFill * (0.55 + density * 0.45) + rim * 0.9, 0.0, 1.0);

        // Кожа лежит ЗА уже накопленным туманом
        color += transmittance * skin * skinAlpha;
        alpha += transmittance * skinAlpha;
    }

    alpha = clamp(alpha * uAlpha, 0.0, 1.0);
    if (alpha <= 0.002) discard;
    OutColor = vec4(color, alpha);
}
