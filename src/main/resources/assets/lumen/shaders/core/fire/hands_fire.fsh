#version 150

uniform sampler2D DiffuseSampler;
uniform float time;
uniform float speed;
uniform float scale;
uniform float intensity;
uniform float pulse;
uniform float direction;
uniform float waveOffset1;
uniform float waveOffset2;
uniform float waveOffset3;
uniform float noise1;
uniform float noise2;
uniform vec3 color1;
uniform vec3 color2;

in vec2 texCoord;
in vec4 vertexColor;
out vec4 fragColor;

// Быстрый арифметический хэш вместо sin
float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float smoothNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    float frequency = 1.0;
    for (int i = 0; i < 4; i++) {
        value += amplitude * smoothNoise(p * frequency);
        frequency *= 2.0;
        amplitude *= 0.5;
    }
    return value;
}

void main() {
    vec2 uv = texCoord;

    // Получаем маску рук
    float mask = texture(DiffuseSampler, uv).r;
    if (mask < 0.01) {
        fragColor = vec4(0.0);
        return;
    }

    // Создаем эффект огня с движением из стороны в сторону
    float fireMovement = sin(time * speed * 0.3 + direction * 2.0) * 0.3 + 0.5;
    float fireMovement2 = cos(time * speed * 0.2 + direction * 1.5) * 0.2 + 0.5;

    // Смещение для анимации пламени
    vec2 fireUv = uv;
    fireUv.x += (waveOffset1 - 0.5) * 0.1 * direction;
    fireUv.y += (waveOffset2 - 0.5) * 0.1;

    // Основная текстура огня
    vec2 fireCoord = fireUv * scale;
    float fire = fbm(fireCoord + vec2(time * speed * 0.2, time * speed * 0.3));

    // Второй слой огня для большей детализации
    vec2 fireCoord2 = fireUv * scale * 0.7 + vec2(time * speed * 0.15, time * speed * 0.25);
    float fire2 = fbm(fireCoord2 + vec2(noise1 * 0.1, noise2 * 0.1));

    // Третий слой для языков пламени
    vec2 fireCoord3 = fireUv * scale * 1.3 - vec2(time * speed * 0.1, time * speed * 0.2);
    float fire3 = fbm(fireCoord3 + vec2(waveOffset3 * 0.2, 0.0));

    // Комбинируем слои
    float finalFire = (fire * 0.5 + fire2 * 0.3 + fire3 * 0.2) * pulse;

    // Эффект движения из стороны в сторону
    float sideMovement = sin(time * speed * 0.15 + uv.y * 5.0 + direction * 1.5) * 0.5 + 0.5;
    finalFire = mix(finalFire, finalFire * (0.7 + 0.3 * sideMovement), 0.3);

    // Вертикальные языки пламени
    float flameHeight = 1.0 - uv.y;
    flameHeight = pow(flameHeight, 0.5 + 0.3 * sin(time * speed * 0.2 + uv.x * 10.0));
    finalFire *= flameHeight;

    // Анимация интенсивности
    finalFire *= (0.8 + 0.2 * sin(time * speed * 0.5 + uv.x * 20.0 + uv.y * 20.0));

    // Применяем маску
    finalFire *= mask * intensity;

    // Создаем градиент цвета от желтого к красному
    vec3 fireColor = mix(color1, color2, finalFire * 1.5);
    fireColor = mix(fireColor, vec3(1.0, 0.8, 0.2), finalFire * 0.3);

    // Добавляем свечение
    float glow = finalFire * 0.5;

    float alpha = finalFire * 2.0;
    alpha = clamp(alpha, 0.0, 1.0);

    fragColor = vec4(fireColor + glow, alpha * 0.9);
}