#version 150

uniform sampler2D Sampler0;

uniform float GlowStrength;
uniform vec2 TexelSize;

in vec2 TexCoord;

out vec4 OutColor;

void main() {
    vec4 center = texture(Sampler0, TexCoord);
    vec4 result = center;

    // ореол вокруг содержимого буфера: три кольца выборок по 8 направлений
    if (GlowStrength > 0.001) {
        vec3 glowColor = vec3(0.0);
        float glowWeight = 0.0;
        for (int ring = 1; ring <= 3; ring++) {
            float radius = 4.0 * float(ring);
            for (int i = 0; i < 8; i++) {
                float angle = float(i) * 0.7853981634 + float(ring) * 0.3926990817;
                vec2 offset = vec2(cos(angle), sin(angle)) * radius * TexelSize;
                vec4 tap = texture(Sampler0, TexCoord + offset);
                float weight = tap.a / (0.6 + 0.7 * float(ring));
                glowColor += tap.rgb * weight;
                glowWeight += weight;
            }
        }

        if (glowWeight > 0.001) {
            glowColor /= glowWeight;
            float halo = clamp(glowWeight / 7.0, 0.0, 1.0) * GlowStrength * 0.8;
            halo *= 1.0 - center.a * 0.85; // ореол в основном вокруг, чуть заходит на края
            float outAlpha = center.a + halo * (1.0 - center.a);
            vec3 outColor = outAlpha > 0.0001
                ? (center.rgb * center.a + glowColor * 1.6 * halo * (1.0 - center.a)) / outAlpha
                : vec3(0.0);
            result = vec4(outColor, outAlpha);
        }
    }

    if (result.a <= 0.002) {
        discard;
    }

    OutColor = result;
}
