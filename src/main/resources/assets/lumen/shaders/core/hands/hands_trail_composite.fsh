#version 150

uniform sampler2D Sampler0; // след
uniform sampler2D Sampler1; // маска рук текущего кадра

uniform float uAlpha;
uniform vec3 uColor;  // цвет темы
uniform vec2 uTexel;

in vec2 TexCoord;
out vec4 OutColor;

void main() {
    // мягкость: крест из 5 выборок сглаживает крошки в дымку
    vec4 c = texture(Sampler0, TexCoord);
    c += texture(Sampler0, TexCoord + vec2(uTexel.x * 1.5, 0.0));
    c += texture(Sampler0, TexCoord - vec2(uTexel.x * 1.5, 0.0));
    c += texture(Sampler0, TexCoord + vec2(0.0, uTexel.y * 1.5));
    c += texture(Sampler0, TexCoord - vec2(0.0, uTexel.y * 1.5));
    c *= 0.2;

    float mask = texture(Sampler1, TexCoord).r;
    float age = 1.0 - c.a; // 0 — свежий дубль, 1 — умирающий

    // Тонировка темой: свежий след ещё узнаваем как рука/предмет, к распаду
    // уходит в цвет темы; умирающие крошки слегка «тлеют» цветом темы
    vec3 tinted = mix(c.rgb, uColor * (0.85 + age * 0.55), 0.45 + age * 0.45);
    tinted += uColor * age * c.a * 0.35;

    float alpha = pow(c.a, 1.1) * (1.0 - mask) * uAlpha;
    if (alpha <= 0.008) {
        discard;
    }

    OutColor = vec4(tinted, alpha);
}
