#version 150

in vec3 Position;
in vec2 UV0;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec2 WorldXZ;
out vec4 FragColor;

void main() {
    vec4 viewPos = ModelViewMat * vec4(Position, 1.0);
    WorldXZ = UV0;
    FragColor = Color;
    gl_Position = ProjMat * viewPos;
}
