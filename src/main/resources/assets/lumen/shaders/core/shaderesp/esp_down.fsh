#version 150

in vec2 TexCoord;
out vec4 fragColor;

uniform sampler2D Sampler0;

uniform vec4 EspKawaseParams; // halfPixelX, halfPixelY, offset, padding

// Dual-Kawase downsample: 5 taps, weight 4/1/1/1/1.
void main() {
    vec2 hp = EspKawaseParams.xy;

    vec4 sum = texture(Sampler0, TexCoord) * 4.0;
    sum += texture(Sampler0, TexCoord - hp.xy);
    sum += texture(Sampler0, TexCoord + hp.xy);
    sum += texture(Sampler0, TexCoord + vec2(hp.x, -hp.y));
    sum += texture(Sampler0, TexCoord - vec2(hp.x, -hp.y));

    fragColor = vec4((sum * 0.125).rgb, 1.0);
}
