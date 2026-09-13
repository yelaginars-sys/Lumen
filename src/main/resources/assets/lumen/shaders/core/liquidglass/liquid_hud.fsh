#version 150

in vec2 FragCoord;
in vec2 TexCoord;
in vec4 FragColor;

uniform sampler2D Sampler0;
uniform vec2 Size;
uniform vec4 Radius;
uniform float Smoothness;
uniform float CornerSmoothness;
uniform float GlobalAlpha;
uniform float FresnelPower;
uniform vec3 FresnelColor;
uniform float FresnelAlpha;
uniform float BaseAlpha;
uniform int FresnelInvert;
uniform float FresnelMix;
uniform float DistortStrength;

out vec4 OutColor;

float roundedBoxSDF(vec2 p, vec2 b, vec4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x = (p.y > 0.0) ? r.x : r.y;
    vec2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
}

void main() {
    vec2 center = Size * 0.5;
    vec2 halfSize = max(center - vec2(1.0), vec2(1.0));
    vec2 pos = (FragCoord * Size) - center;
    vec4 clampedRadius = min(Radius, vec4(min(halfSize.x, halfSize.y)));
    float edgeSoftness = max(Smoothness * (1.0 + max(CornerSmoothness - 2.0, 0.0) * 0.1), 0.001);

    float distance = roundedBoxSDF(pos, halfSize, clampedRadius);
    float alpha = 1.0 - smoothstep(-edgeSoftness, edgeSoftness, distance);
    if (alpha < 0.001) {
        discard;
    }

    float distToEdge = abs(distance);
    float maxDist = max(min(halfSize.x, halfSize.y), 1.0);
    float edgeGradient = 1.0 - clamp(distToEdge / maxDist, 0.0, 1.0);

    float fresnelBase = FresnelInvert != 0 ? edgeGradient : (1.0 - edgeGradient);
    float fresnel;
    if (FresnelPower > 20.0) {
        fresnel = exp(FresnelPower * log(clamp(fresnelBase, 0.001, 1.0)));
    } else {
        fresnel = pow(clamp(fresnelBase, 0.0, 1.0), FresnelPower);
    }
    fresnel = clamp(fresnel, 0.0, 1.0);

    vec2 dir = normalize(pos + vec2(0.001));
    vec2 baseDistort = dir * fresnel * DistortStrength;
    float aberration = DistortStrength * 2.5;
    vec3 refracted = vec3(0.0);
    float totalWeight = 0.0;

    for (int i = 0; i < 5; i++) {
        float s = float(i) * 0.25;
        float g = s * 2.0 - 1.0;
        float weight = exp(-g * g * 2.0);
        vec2 offset = baseDistort * (0.3 + s * 1.4);
        vec2 uvR = clamp(TexCoord + offset * (1.0 + aberration), 0.001, 0.999);
        vec2 uvG = clamp(TexCoord + offset, 0.001, 0.999);
        vec2 uvB = clamp(TexCoord + offset * (1.0 - aberration), 0.001, 0.999);

        refracted.r += texture(Sampler0, uvR).r * weight;
        refracted.g += texture(Sampler0, uvG).g * weight;
        refracted.b += texture(Sampler0, uvB).b * weight;
        totalWeight += weight;
    }

    vec3 baseColor = refracted / max(totalWeight, 0.0001);
    float caustic = pow(edgeGradient, 4.0) * 0.1;

    vec3 finalColor = mix(baseColor, FresnelColor, fresnel * FresnelMix);
    finalColor += caustic * FresnelColor;
    finalColor = clamp(finalColor, 0.0, 1.0);

    float finalAlpha = mix(BaseAlpha, FresnelAlpha, fresnel) * alpha * GlobalAlpha * FragColor.a;
    if (finalAlpha < 0.001) {
        discard;
    }

    OutColor = vec4(finalColor, finalAlpha);
}
