#version 150

uniform float time;
uniform vec2 screenSize;
uniform vec4 baseColor;
uniform float alpha;

in vec2 texCoord;
out vec4 fragColor;

void mainImage(out vec4 o, vec2 fragCoord) {
    vec2 v = max(screenSize, vec2(1.0));
    vec2 u = 0.2 * ((fragCoord + fragCoord) - v) / v.y;
    vec2 w = vec2(0.0);
    vec2 k = u;
    o = vec4(1.0, 2.0, 3.0, 0.0);
    float a = 0.5;
    float t = time;
    for (float i = 1.0; i < 19.0; i += 1.0) {
        a += 0.03;
        t += 1.0;
        v = cos(t - 7.0 * u * pow(a, i)) - 5.0 * u;
        vec4 rotator = cos(i + t * 0.02 - vec4(0.0, 11.0, 33.0, 0.0));
        u = u * mat2(rotator.x, rotator.y, rotator.z, rotator.w);
        u += 0.005 * tanh(40.0 * dot(u, u) * cos(100.0 * u.yx + t))
            + 0.2 * a * u
            + 0.003 * cos(t + 4.0 * exp(-0.01 * dot(o, o)));
        w = u / (1.0 - 2.0 * dot(u, u));
        o += (1.0 + cos(vec4(0.0, 1.0, 3.0, 0.0) + t))
            / length((1.0 + i * dot(v, v)) * sin(w * 3.0 - 9.0 * u.yx + t));
    }
    o = 1.0 - sqrt(exp(-o * o * o / 200.0));
    o = pow(o, vec4(0.3));
    o -= dot(k - u, k - u) / 250.0;
}

void main() {
    vec4 color;
    mainImage(color, gl_FragCoord.xy);
    vec3 shaderColor = clamp(color.rgb, 0.0, 1.0);
    shaderColor = mix(shaderColor, shaderColor * baseColor.rgb, 0.7);
    fragColor = vec4(shaderColor, alpha * 0.5);
}
