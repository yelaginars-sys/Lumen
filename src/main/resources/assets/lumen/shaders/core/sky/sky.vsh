#version 150

in vec3 Position;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 InvViewProj;

out vec3 viewDir;

void main() {
    // Pin the quad to the far plane (NDC z = 1) so the hardware depth test
    // keeps it behind any rendered geometry — only true sky pixels survive.
    vec4 ndc = vec4(Position.xy, 1.0, 1.0);
    gl_Position = ndc;

    // Reconstruct the camera-relative world ray for this pixel.
    vec4 world = InvViewProj * ndc;
    viewDir = world.xyz / world.w;
}
