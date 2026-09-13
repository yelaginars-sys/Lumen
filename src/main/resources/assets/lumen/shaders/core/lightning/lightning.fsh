#version 150

in vec2 Coord;   // x — путь вдоль дуги 0..1, y — поперёк ленты -1..1
in vec4 Tint;    // цвет темы + общая яркость дуги в альфе

uniform float Time;
uniform float Reveal; // какая доля дуги уже «выросла»

out vec4 fragColor;

// Дешёвый хэш без sin: синус на каждый пиксель — самый частый способ уронить фпс.
float hash11(float p) {
    p = fract(p * 0.1031);
    p *= p + 33.33;
    p *= p + p;
    return fract(p);
}

float noise11(float x) {
    float i = floor(x);
    float f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(hash11(i), hash11(i + 1.0), f);
}

void main() {
    // Растущий кончик: то, что ещё не «доросло», не рисуем вовсе.
    if (Coord.x > Reveal) discard;

    float d = abs(Coord.y);
    if (d >= 1.0) discard;

    float inv = 1.0 - d;

    // Разряд = добела раскалённая нить внутри мягкого гало. Раньше это набиралось
    // четырьмя проходами линий разной толщины, теперь обе части считаются здесь.
    float core = pow(inv, 14.0);
    float glow = pow(inv, 2.4) * 0.5;

    // Неравномерное мерцание вдоль дуги — разряд «дышит», а не светится ровно.
    float flicker = 0.72 + 0.28 * noise11(Coord.x * 16.0 + Time * 11.0);

    // Хвост тусклее головы, плюс мягкие торцы, чтобы лента не обрывалась прямоугольником.
    float along = 0.45 + 0.55 * Coord.x;
    float caps = smoothstep(0.0, 0.05, Coord.x) * smoothstep(0.0, 0.04, Reveal - Coord.x);

    float intensity = (glow + core) * flicker * along * caps;
    if (intensity <= 0.002) discard;

    // Ядро выбеливаем — как у настоящей дуги, центр почти белый, края цвета темы.
    vec3 rgb = mix(Tint.rgb, vec3(1.0), clamp(core * 0.9, 0.0, 1.0));

    // Блендинг стоит SRC_ALPHA/ONE, поэтому яркость кладём в альфу.
    fragColor = vec4(rgb, intensity * Tint.a);
}
