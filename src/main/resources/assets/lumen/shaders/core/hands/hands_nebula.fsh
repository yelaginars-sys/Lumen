#version 150

uniform sampler2D Sampler0; // маска рук текущего кадра
uniform sampler2D Sampler1; // размытая маска — ореол вокруг руки
uniform sampler2D Sampler2; // кадр с руками — реальные цвета руки/предмета

uniform vec3 uColor;    // цвет темы
uniform vec2 uAspect;   // (ширина/высота, 1.0) — чтобы туманность не растягивало
uniform float uTime;
uniform float uSpeed;   // скорость клубления облаков
uniform float uDensity; // масштаб/плотность туманности
uniform float uStars;   // яркость звёзд
uniform float uGlow;    // сила ореола и кромки
uniform float uBlend;   // насколько космос перекрывает саму руку
uniform float uOuter;   // 1.0 — проход ореола, 0.0 — заливка внутри маски

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
    for (int i = 0; i < 5; i++) {
        sum += amp * vnoise(p);
        p = ROT * p * 2.03;
        amp *= 0.5;
    }
    return sum;
}

// Звёздное поле: в редких ячейках зажигается точка со своим мерцанием
float starfield(vec2 p, float scale, float threshold, float t) {
    vec2 g = p * scale;
    vec2 cell = floor(g);
    vec2 f = fract(g);
    float h = hash21(cell);
    vec2 pos = vec2(hash21(cell + 11.3), hash21(cell + 27.7));
    float core = smoothstep(0.14, 0.0, length(f - pos));
    float twinkle = 0.45 + 0.55 * sin(t * 2.6 + h * 47.0);
    return core * twinkle * step(threshold, h);
}

void main() {
    vec2 uv = TexCoord;
    float mask = texture(Sampler0, uv).r;
    float halo = texture(Sampler1, uv).a;

    // Вне руки работает только проход ореола, внутри — только заливка
    float coverage = uOuter > 0.5 ? halo * (1.0 - mask) : mask;
    if (coverage <= 0.004) discard;

    vec2 p = uv * uAspect;
    float t = uTime * uSpeed;

    // Домейн-варп: облака клубятся и перетекают, а не просто ползут по экрану
    vec2 q = p * (2.6 * uDensity);
    vec2 warp = vec2(
        fbm(q + vec2(0.0, t * 0.09)),
        fbm(q + vec2(4.7, -t * 0.07))
    );
    float clouds = fbm(q + warp * 1.9 + vec2(t * 0.05, -t * 0.03));
    float wisps = fbm(q * 2.4 - warp * 1.2 + vec2(-t * 0.08, t * 0.04));

    clouds = pow(clamp(clouds * 1.25, 0.0, 1.0), 1.7);
    wisps = pow(clamp(wisps * 1.15, 0.0, 1.0), 3.0);

    // Палитра строится от цвета темы: глубина космоса, тело туманности,
    // сдвинутый по тону подсвет и раскалённое ядро
    vec3 deep = mix(vec3(0.015, 0.010, 0.045), uColor * 0.10, 0.65);
    vec3 body = uColor;
    vec3 alt = uColor.gbr;
    vec3 hot = mix(uColor, vec3(1.0), 0.7);

    vec3 col = deep;
    col = mix(col, body, clouds);
    col = mix(col, alt * 1.15, wisps * 0.5);
    col += hot * pow(clouds, 4.0) * 0.85;

    // Два звёздных плана с разной скоростью — при движении руки даёт параллакс
    float st = starfield(p + vec2(t * 0.010, -t * 0.004), 120.0, 0.978, t)
             + starfield(p + vec2(-t * 0.018, t * 0.007), 70.0, 0.986, t * 1.4) * 1.6;
    col += vec3(0.85, 0.90, 1.0) * st * uStars * 1.6;

    float density = clamp(0.35 + clouds * 0.9 + wisps * 0.5 + st * 3.0, 0.0, 1.5);

    if (uOuter > 0.5) {
        // Ореол: аддитивная космическая дымка вокруг руки
        float a = pow(clamp(coverage * 1.35, 0.0, 1.0), 1.25) * uGlow;
        if (a <= 0.004) discard;
        OutColor = vec4(col * (0.8 + density * 0.6), a);
        return;
    }

    // На максимуме перекрытия рука заменяется целиком: ни её светотень,
    // ни её пиксели сквозь туманность больше не проступают
    float full = smoothstep(0.8, 1.0, uBlend);

    // Заливка: светотень руки сохраняется, чтобы силуэт остался читаемым
    vec3 hand = texture(Sampler2, uv).rgb;
    float lum = dot(hand, vec3(0.299, 0.587, 0.114));
    col *= mix(0.5 + lum * 0.95, 1.0, full);
    col += hot * st * uStars * 0.6;

    // Кромка: у края маски размытие ещё слабое — там подсвечиваем контур
    float rim = clamp(1.0 - halo, 0.0, 1.0) * mask;
    col += hot * pow(rim, 1.5) * 0.9 * uGlow;

    float a = mask * clamp(uBlend * (0.55 + density * 0.45), 0.0, 1.0);
    a = mix(a, mask, full);
    if (a <= 0.004) discard;
    OutColor = vec4(col, a);
}
