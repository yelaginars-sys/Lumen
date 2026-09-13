#version 150

in vec3 Position;
in vec2 UV0;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float time;
uniform float speed;
uniform float strength;
uniform float constantGlitch;

out float SegV;
out float SliceGlitch;
out vec4 FragColor;

float hash1(float n) {
    return fract(sin(n) * 43758.5453123);
}

void main() {
    // UV0.x — seed среза (одинаков у всех вершин сегмента: срез смещается жёстко,
    // целиком, без перекоса граней), UV0.y — вертикальная позиция внутри среза
    float sliceSeed = UV0.x;
    float t = time * speed;

    // всплески: большую часть времени модель цела, глитч приходит короткими окнами
    float windowId = floor(t * 2.5);
    float burstGate = step(0.62, hash1(windowId * 17.31));
    // внутри всплеска смещения перебрасываются рывками ~14 раз в секунду
    float jitterStep = floor(t * 14.0);
    // редкие одиночные "микросбои" между всплесками
    float microGate = step(0.965, hash1(jitterStep * 7.77 + 3.1));
    // "Постоянный глитч": ворота всплесков всегда открыты, эффект не затухает
    float gate = max(max(burstGate, microGate), constantGlitch);

    float sliceRand = hash1(sliceSeed * 91.7 + jitterStep * 13.7);
    float active = gate * step(0.45, sliceRand);

    float amp = strength * 0.085 * active * (0.35 + 0.65 * hash1(sliceSeed * 5.3 + jitterStep * 1.7));
    float dir = step(0.5, hash1(sliceSeed * 3.7 + jitterStep * 0.913)) * 2.0 - 1.0;

    // смещение в view-пространстве: срезы уезжают горизонтально относительно экрана
    vec4 viewPos = ModelViewMat * vec4(Position, 1.0);
    viewPos.x += dir * amp;
    viewPos.y += (hash1(sliceSeed * 9.1 + jitterStep * 2.3) - 0.5) * amp * 0.35;

    SegV = UV0.y;
    SliceGlitch = active * sliceRand * clamp(strength, 0.0, 1.0);
    FragColor = Color;

    gl_Position = ProjMat * viewPos;
}
