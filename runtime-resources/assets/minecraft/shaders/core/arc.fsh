#version 150

uniform vec4 color1;
uniform vec4 color2;
uniform float radius;
uniform float thickness;
uniform float start;
uniform float end;
uniform vec2 size;
uniform vec2 location;

out vec4 fragColor;

#define PI 3.141592653589793
#define RAD 0.0174533

void main() {
    float tau = PI * 2.0;
    float startAngle = mod(start * RAD, tau);
    float sweepAngle = clamp(end * RAD, 0.0, tau);
    float endAngle = startAngle + sweepAngle;

    float smoothThresh = 6.0 * (1.0 / length(size));
    vec2 centerPos = ((gl_FragCoord.xy - location) / size.xy) * 2.0 - 1.0;

    float dist = length(centerPos);
    float bandAlpha = smoothstep(radius, radius + smoothThresh, dist) * smoothstep(radius + thickness, (radius + thickness) - smoothThresh, dist);
    float angle = (atan(centerPos.y, centerPos.x) + PI);
    float angleAlpha;

    if (sweepAngle >= tau - smoothThresh) {
        angleAlpha = 1.0;
    } else if (endAngle <= tau) {
        float startMask = smoothstep(startAngle - smoothThresh, startAngle + smoothThresh, angle);
        float endMask = 1.0 - smoothstep(endAngle - smoothThresh, endAngle + smoothThresh, angle);
        angleAlpha = startMask * endMask;
    } else {
        float wrappedEnd = endAngle - tau;
        float startMask = smoothstep(startAngle - smoothThresh, startAngle + smoothThresh, angle);
        float endMask = 1.0 - smoothstep(wrappedEnd - smoothThresh, wrappedEnd + smoothThresh, angle);
        angleAlpha = max(startMask, endMask);
    }

    float angle2 = (angle / PI * 180.);
    angle2 = angle2 - 360. * floor(angle2 / 360.);
    if (angle2 >= 180.) {
        angle2 = (360. - angle2) * 2.;
    } else {
        angle2 = angle2 * 2.;
    }
    fragColor = mix(color1, color2, angle2 / 360.) * bandAlpha * angleAlpha;
}
