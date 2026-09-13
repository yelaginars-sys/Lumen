#version 150

uniform sampler2D Sampler0; // размытый фон — основа матового стекла

uniform vec2 ScreenSize;
uniform float time;
uniform float dissolveAmount; // 0..1 — насколько копия рассыпана (растёт с отставанием)
uniform float dissolveScale;  // масштаб крошек распада
uniform float glassAlpha;

in vec3 ViewPos;
in vec2 TexCoord;  // x — шумовая координата модели, y — метры вдоль движения (перед +, зад −)
in vec4 FragColor; // rgb — цвет темы, a — фейд по дистанции отставания

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

    // Микрорельеф матового стекла: две октавы шума возмущают нормаль
    vec2 frostUv = vec2(TexCoord.x * 1.6, TexCoord.x * 1.1 + TexCoord.y * 1.8);
    float bumpA = noise(frostUv * 9.0 + vec2(time * 0.020, -time * 0.015)) - 0.5;
    float bumpB = noise(frostUv * 9.0 + vec2(37.2, 11.8) + vec2(-time * 0.017, time * 0.021)) - 0.5;
    float fineA = noise(frostUv * 27.0) - 0.5;
    float fineB = noise(frostUv * 27.0 + vec2(53.1, 29.4)) - 0.5;
    vec3 normal = normalize(flatNormal + vec3(bumpA + fineA * 0.65, bumpB + fineB * 0.65, 0.0) * 0.42);

    vec3 viewDir = normalize(-ViewPos);
    float facing = clamp(abs(dot(normal, viewDir)), 0.0, 1.0);
    float fresnel = pow(1.0 - facing, 3.0);

    vec2 screenUv = gl_FragCoord.xy / ScreenSize;

    // Преломление размытого фона + лёгкая хроматическая аберрация
    vec2 refrShift = normal.xy * 38.0 / ScreenSize;
    vec2 uvR = clamp(screenUv - refrShift * 1.18, 0.002, 0.998);
    vec2 uvG = clamp(screenUv - refrShift, 0.002, 0.998);
    vec2 uvB = clamp(screenUv - refrShift * 0.82, 0.002, 0.998);
    vec3 refracted = vec3(
        texture(Sampler0, uvR).r,
        texture(Sampler0, uvG).g,
        texture(Sampler0, uvB).b
    );

    // Экранное отражение по отражённому лучу
    vec3 reflDir = reflect(-viewDir, normal);
    vec2 reflUv = clamp(screenUv + reflDir.xy * 0.17, 0.002, 0.998);
    vec3 reflected = texture(Sampler0, reflUv).rgb;

    float reflAmount = clamp(0.06 + fresnel * 0.85, 0.0, 0.85);
    vec3 glass = mix(refracted, reflected, reflAmount);

    // В отличие от чамсового стекла, копия несёт цвет темы: тонировка толщи,
    // кайма и блёстки — всё в теме
    vec3 theme = FragColor.rgb;
    glass = mix(glass, glass * (0.30 + theme * 1.15), 0.55);
    glass = mix(glass, theme, clamp(fresnel, 0.0, 1.0) * 0.40);
    float sparkle = pow(clamp(noise(frostUv * 30.0 + vec2(time * 0.008, -time * 0.006)), 0.0, 1.0), 10.0);
    glass += theme * sparkle * (0.12 + fresnel * 0.30);

    // Осколки-биллборды помечены v=2: они светятся темой сами — биллборд смотрит
    // ровно в камеру, френель на нём нулевой, и стеклянная кайма не работает
    bool isCrumb = TexCoord.y > 1.5;
    if (isCrumb) {
        glass = mix(glass, theme * (1.15 + sparkle * 1.5), 0.8);
    }

    // «Отсталый»: копия рассыпается СЗАДИ. Крошки — в ЭКРАННЫХ ячейках: обе
    // стенки конечности в одном пикселе делят ОДНУ крошку, поэтому дыра у
    // хвоста прожигается насквозь. С крошками в координатах модели дыру в
    // задней стенке «латала» бы внутренняя сторона передней — распад невидим
    float backness = clamp(0.55 - TexCoord.y * 1.5, 0.0, 1.0);
    float cellPx = max(3.0, 7.0 * max(dissolveScale, 0.2) * ScreenSize.y / 1080.0);
    vec2 cell = floor(gl_FragCoord.xy / cellPx);
    float crumb = hash(cell);
    // поле течения тоже экранное и общее для обеих стенок — порог согласован
    float flow = noise(cell * 0.13 + vec2(0.0, -time * 1.1));
    float erosion = dissolveAmount * (0.06 + 1.35 * backness) * (0.60 + 0.55 * flow);
    float life = crumb - erosion;
    // осколок экранная эрозия не ест — он гаснет своей CPU-жизнью, иначе
    // ~5-13% осколков случайно пропадали бы и мигали на границах ячеек
    if (life < 0.0 && !isCrumb) {
        discard;
    }
    float edge = isCrumb ? 0.0 : 1.0 - smoothstep(0.0, 0.16, life);
    glass += theme * edge * (1.1 + sparkle * 2.0);

    float alpha = isCrumb
        ? clamp(glassAlpha * FragColor.a * 1.05, 0.0, 1.0)
        : clamp(glassAlpha * FragColor.a * (0.80 + fresnel * 0.35), 0.0, 1.0);
    alpha *= 1.0 - edge * 0.35; // умирающая крошка тает

    if (alpha <= 0.002) {
        discard;
    }

    OutColor = vec4(glass, alpha);
}
