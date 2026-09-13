#version 150

uniform sampler2D Sampler0; // резкая сцена (с рукой)
uniform sampler2D Sampler1; // размытая сцена
uniform sampler2D Sampler2; // глубина мира

uniform float fogStart;     // начало тумана/блюра, В БЛОКАХ
uniform float fogEnd;       // конец (полный блюр), В БЛОКАХ
uniform float blurStrength; // сила размытия
uniform float near;         // ближняя плоскость проекции
uniform float far;          // дальняя плоскость проекции
uniform vec3  fogColor;     // цвет тумана (тонирует блюр вдали)

in vec2 TexCoord;
out vec4 OutColor;

void main() {
    vec2 uv = TexCoord;
    float depth = texture(Sampler2, uv).r;
    vec4 sharp = texture(Sampler0, uv);

    // Небо/дальний клип — оставляем как есть.
    if (depth >= 1.0) {
        OutColor = sharp;
        return;
    }

    // Линеаризуем оконную глубину в мировую дистанцию (в блоках) по near/far проекции.
    float dist = (near * far) / max(far - depth * (far - near), 0.0001);

    // Маска дистанции — как у тумана: от fogStart (блоки) до fogEnd (блоки).
    float distMask = smoothstep(fogStart, fogEnd, dist);

    float curve = mix(1.85, 0.95, clamp(blurStrength * 0.55, 0.0, 1.0));
    float blurMask = pow(clamp(distMask, 0.0, 1.0), curve) * clamp(blurStrength, 0.0, 1.0);

    // Цветной блюр: вдали подмешиваем цвет тумана к размытой картинке.
    vec4 blurred = texture(Sampler1, uv);
    vec3 tinted = mix(blurred.rgb, fogColor, distMask * 0.6);

    OutColor = vec4(mix(sharp.rgb, tinted, blurMask), sharp.a);
}
