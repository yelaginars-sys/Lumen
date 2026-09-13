#version 150

uniform float time;
uniform float speed;
uniform float fillAlpha;

in float SegV;
in float SliceGlitch;
in vec4 FragColor;

out vec4 OutColor;

float hash1(float n) {
    return fract(sin(n) * 43758.5453123);
}

void main() {
    float g = clamp(SliceGlitch, 0.0, 1.0);

    // Заливки нет: спокойные срезы полностью прозрачны, виден оригинальный игрок.
    // Рисуются только смещённые глитч-осколки во время всплесков.
    if (g <= 0.01) {
        discard;
    }

    vec3 base = FragColor.rgb;
    float sliceKey = floor(g * 255.0);

    // "Интерлейс": осколок состоит из тонких горизонтальных полос, каждая со своим
    // оттенком и яркостью; состав полос перебрасывается в ритме джиттера
    float rows = 5.0 + floor(hash1(sliceKey * 3.17) * 4.0);
    float rowId = floor(SegV * rows);
    float rowRand = hash1(rowId * 12.9898 + sliceKey * 7.13 + floor(time * speed * 14.0) * 0.618);

    vec3 magenta = vec3(base.r * 1.35, base.g * 0.40, base.b * 1.40);
    vec3 cyan = vec3(base.r * 0.35, base.g * 1.25, base.b * 1.40);
    vec3 bright = base * 1.35 + 0.08;
    vec3 rowColor = rowRand < 0.34 ? magenta : (rowRand < 0.67 ? cyan : bright);
    vec3 color = mix(base, rowColor, 0.85);

    color *= 0.90 + 0.45 * hash1(rowId * 4.7 + sliceKey * 1.9);
    color *= 1.05 + g * 0.30;

    // светящиеся края разрыва
    float edge = smoothstep(0.0, 0.10, SegV) * smoothstep(1.0, 0.90, SegV);
    float tear = 1.0 - edge;
    color += tear * (0.40 + 0.40 * g);

    // часть полос почти выпадает — рваная, "битая" структура осколка
    float rowAlpha = 0.55 + 0.45 * step(0.22, rowRand);
    float alpha = fillAlpha * (0.60 + 0.40 * g) * rowAlpha;
    alpha = clamp(alpha + tear * 0.20, 0.0, 1.0);

    // FragColor.a сохраняет работу "Пульсирования"
    float alphaBase = clamp(FragColor.a * 2.0, 0.0, 1.0);
    float outAlpha = clamp(alpha * alphaBase, 0.0, 1.0);

    if (outAlpha <= 0.002) {
        discard;
    }

    OutColor = vec4(color, outAlpha);
}
