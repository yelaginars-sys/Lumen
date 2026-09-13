#version 150

uniform sampler2D Sampler0; // накопленный след
uniform sampler2D Sampler1; // бинарная маска рук текущего кадра
uniform sampler2D Sampler2; // кадр с руками — реальные цвета руки/предмета

uniform float uDt;
uniform float uRise;     // скорость подъёма, доли экрана в секунду
uniform float uFade;     // скорость затухания, 1/с
uniform float uDissolve; // масштаб «крошек» распада
uniform float uTime;

in vec2 TexCoord;
out vec4 OutColor;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 345.45));
    p += dot(p, p + 34.345);
    return fract(p.x * p.y);
}

float vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

void main() {
    vec2 uv = TexCoord;

    // Гладкое поле течения: след всплывает неравномерно, с плавными волнами
    // покачивания — как дым, а не как дрожащие квадраты
    float flow = vnoise(uv * vec2(6.0, 3.0) + vec2(0.0, -uTime * 0.35));
    float flowSide = vnoise(uv * vec2(11.0, 5.0) + vec2(7.3, -uTime * 0.6));
    float riseSpeed = uRise * (0.75 + 0.55 * flow);
    float sway = (flowSide - 0.5) * uRise * 0.9;
    vec2 srcUv = uv - vec2(sway * uDt, riseSpeed * uDt);

    vec4 prev = srcUv.y < 0.0 ? vec4(0.0) : texture(Sampler0, srcUv);

    // Крошки: у каждой ячейки своё время смерти; края крошек мягкие —
    // повторный билинейный ресемплинг сам добавляет дымчатости
    vec2 cell = floor(uv * vec2(220.0, 220.0) / max(uDissolve, 0.2));
    float crumb = hash(cell);
    float a = prev.a * exp(-uFade * uDt * (0.65 + 0.75 * crumb));
    a *= smoothstep(0.0, 0.08, a); // мягкое дотлевание вместо резкого среза

    vec3 rgb = prev.rgb;

    // свежая рука впечатывается в след её реальными цветами
    float mask = texture(Sampler1, uv).r;
    if (mask > 0.5) {
        rgb = texture(Sampler2, uv).rgb;
        a = 1.0;
    }

    OutColor = vec4(rgb, a);
}
