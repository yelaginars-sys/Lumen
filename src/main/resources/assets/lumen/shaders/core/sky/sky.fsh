#version 150

in vec3 viewDir;
out vec4 OutColor;

uniform float Time;    // animated seconds (scaled by sky speed)
uniform vec3  Theme;   // client theme color (0..1)
uniform float Style;   // selected style index
uniform float Quality; // множитель числа шагов объёмных стилей (0.4..1.5)

// Небо всегда чёрное — цвет даёт только эффект, поэтому фон держим почти в нуле.
const vec3 NIGHT = vec3(0.004, 0.005, 0.010);

const mat3 M3 = mat3( 0.00,  0.80,  0.60,
                     -0.80,  0.36, -0.48,
                     -0.60, -0.48,  0.64);

// ===================== процедурный шум =====================

// ===================== процедурный шум =====================

float hash11(float p) {
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

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float noise1(float x) {
    x *= 32.0;
    float i = floor(x), f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(hash11(i), hash11(i + 1.0), f);
}

/**
 * Оптимизированный 3D value-noise — SIMD-векторизованный хэш и быстрый гладкий smoothstep.
 */
float noise3(vec3 x) {
    x *= 32.0;
    vec3 i = floor(x), f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    float n = i.x + i.y * 157.0 + i.z * 113.0;
    vec4 n4_0 = vec4(n, n + 157.0, n + 113.0, n + 270.0);
    vec4 n4_1 = n4_0 + 1.0;
    vec4 h0 = hash11_4(n4_0);
    vec4 h1 = hash11_4(n4_1);
    vec4 mix_x = mix(h0, h1, f.x);
    vec2 mix_y = mix(mix_x.xz, mix_x.yw, f.y);
    return mix(mix_y.x, mix_y.y, f.z);
}

/** Октав меньше оригинальных пяти: это внутренний цикл марша, каждая октава дорогая. */
float fbmNebula(vec3 p) {
    float f = 0.5000 * noise3(p); p = M3 * p * 2.02;
    f += 0.2500 * noise3(p);      p = M3 * p * 2.03;
    f += 0.1250 * noise3(p);
    return f;
}

// ===================== 0. Лента =====================
float vnoise2(vec2 p) {
    vec2 i = floor(p), f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm2(vec2 p) {
    float v = 0.0, a = 0.5;
    for (int i = 0; i < 4; i++) {
        v += a * vnoise2(p);
        p = p * 2.03 + vec2(17.0, 9.0);
        a *= 0.5;
    }
    return v;
}

/**
 * Лента сияния. Домен — проекция на плоскость над головой: она бесшовная и меняется
 * и по азимуту, и по высоте. Раньше домен строился только из азимута, поэтому значение
 * было постоянным вдоль каждого меридиана и вместо ленты выходили спицы из зенита.
 * Шум здесь гладкий value-noise, а не эмуляция текстуры 256x256 — та попадала всего
 * в несколько текселей и заметно дёргалась при дрейфе времени.
 */
vec3 styleRibbon(vec3 dir, vec3 baseTint, vec3 tipTint) {
    // Знаменатель ограничен снизу — иначе ниже горизонтали проекция взрывается и лента
    // обрывается ровной чёрной границей на середине неба.
    vec2 p = dir.xz / max(dir.y + 0.30, 0.12);
    float t = Time * 0.05;

    // Изгибаем домен шумом — лента змеится, а не лежит прямой полосой.
    float warp = fbm2(p * 0.30 + vec2(t * 0.6, t * 0.25));
    float ribbon = fbm2(p * 0.55 + warp * 1.2 + vec2(-t * 0.4, t * 0.15));

    // Узкая полоса вокруг середины шума, края мягкие.
    float band = clamp(1.0 - abs(ribbon - 0.5) * 3.4, 0.0, 1.0);
    band = pow(band, 2.2);

    // Вертикальная штриховка — характерные лучи сияния.
    float rays = 0.65 + 0.35 * vnoise2(vec2(p.x * 6.0 + warp * 2.0, t * 0.5));

    // Гасим у зенита, чтобы лента висела полосой, а не заливала небо; снизу — плавно
    // уходим за горизонталь (без резкой границы, иначе небо делится на светлую/тёмную половины).
    float shape = smoothstep(-0.95, -0.10, dir.y) * (1.0 - smoothstep(0.45, 1.0, dir.y));

    vec3 tint = mix(baseTint, tipTint, clamp(ribbon, 0.0, 1.0));
    return tint * band * rays * shape * 2.2;
}

// ===================== 1. Туманность =====================
float nebulaMap(vec3 p) {
    vec3 location = p;
    p *= 0.009;
    float v = fbmNebula(p);
    v = noise3(Time * 0.0025 + p + 0.15 * vec3(v));
    float d = clamp(1.0 - length(location) / 9.0, 0.0, 1.0);
    v = v * d - 0.1;
    return 1.3 * clamp(v, 0.0, 1.0);
}

/**
 * Вклад одного источника в точке облака. Само-затенение оцениваем по плотности В ТОЧКЕ
 * (один сэмпл, {@code density} уже посчитан снаружи), без вложенного луча-марша к свету:
 * именно этот вложенный марш умножал стоимость каждого шага в разы и убивал fps.
 */
vec3 nebulaLight(vec3 samplePos, float density, vec3 lightPos, vec3 lightColor, float strength) {
    float d = length(lightPos - samplePos);
    float shadow = exp(-d * density);
    float dist = d + 1.0;
    return lightColor * strength * (1.0 / (dist * dist)) * shadow;
}

vec3 styleNebula(vec3 dir, vec3 baseTint, vec3 tipTint, int steps) {
    // Камера в центре облака: плотность падает с расстоянием, туманность окружает игрока.
    vec3 ro = vec3(0.0);
    float t = hash21(gl_FragCoord.xy) * 0.5;
    float densitySum = 0.0;
    float stepLength = 10.0 / float(steps);
    vec3 color = vec3(0.01, 0.02, 0.05);

    // Константы на кадр (не зависят от позиции на луче) — считаем один раз, а не в цикле.
    float l3z = 10.0 * (hash11(floor(0.1 * Time)) - 0.5);
    float storm = mix(1.2, 0.0, sign(fract(-0.1 + 0.1 * Time) - 0.15) * 0.5 + 0.5) * noise3(vec3(20.0 * Time));
    const vec3 ambient = vec3(0.0, 0.025, 0.025);

    vec3 lightColor1 = baseTint * 250.0;
    vec3 lightColor2 = tipTint * 200.0;

    for (int i = 0; i < 48; i++) {
        if (i >= steps) break;
        vec3 samplePos = ro + t * dir;
        float dens = nebulaMap(samplePos);
        densitySum += dens;

        float transmittance = exp(-t * (densitySum / float(i + 1)));
        if (transmittance < 0.002) break; // Ранняя отсечка при прозрачности < 0.2%

        vec3 l1 = nebulaLight(samplePos, dens, vec3(-18.0, 1.8, 0.0), lightColor1, 1.0);
        vec3 l2 = nebulaLight(samplePos, dens, vec3(0.0, 0.0, -15.0), lightColor2, 1.0);

        // Третий источник — 1D шум на координату Y
        float n = (noise1(0.7 * samplePos.y) - 0.5) - 0.2 * samplePos.y;
        vec3 l3 = nebulaLight(samplePos, dens, vec3(n, samplePos.y, l3z), vec3(1.0), storm);

        color += transmittance * dens * (ambient + l1 + l2 + l3);

        t += stepLength;
    }
    return color;
}

// ===================== main =====================
void main() {
    vec3 dir = normalize(viewDir);
    int style = int(Style + 0.5);

    // Палитра целиком из цвета темы: насыщенное основание -> смещённый оттенок в вершинах.
    // Белого в основание почти не подмешиваем, иначе эффект выцветает.
    vec3 themeC = clamp(Theme, 0.0, 1.0);
    vec3 baseTint = mix(themeC, vec3(1.0), 0.12);
    vec3 tipTint = clamp(vec3(themeC.b * 0.85 + 0.10, themeC.r * 0.55 + 0.08, themeC.g * 0.90 + 0.15), 0.0, 1.0);

    float q = clamp(Quality, 0.4, 1.5);
    vec3 col = NIGHT;

    if (style == 1) {
        col += styleNebula(dir, baseTint, tipTint, int(24.0 * q));
    } else {
        col += styleRibbon(dir, baseTint, tipTint);
    }

    // Сплошной фон по всему куполу: нижняя полусфера больше не чёрная пустота,
    // убираем эффект «светлая сторона / тёмная сторона». Пик у горизонта, плавно к полюсам.
    float dome = pow(clamp(1.0 - abs(dir.y), 0.0, 1.0), 1.5);
    col += themeC * dome * 0.10;

    // Мягкий отсвет у горизонта. Степень 9 давала тонкую полоску ровно по горизонтали,
    // из-за чего переход к чёрному читался границей — растягиваем шире.
    float horizon = pow(clamp(1.0 - abs(dir.y), 0.0, 1.0), 4.0);
    col += themeC * horizon * 0.05;

    // Тонмап по максимальному каналу, а не поканальный: покомпонентный сжимает яркие
    // места к белому и съедает цвет темы, этот сохраняет оттенок.
    float peak = max(max(col.r, col.g), col.b);
    col *= 1.0 / (1.0 + peak * 0.55);
    col = pow(clamp(col, 0.0, 1.0), vec3(0.90));

    OutColor = vec4(col, 1.0);
}
