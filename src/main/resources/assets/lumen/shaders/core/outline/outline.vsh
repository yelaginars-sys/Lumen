#version 150

in vec3 Position;

uniform mat4 ProjMat;
uniform vec2 resolution;

out vec2 TexCoord;

void main() {
    vec4 pos = ProjMat * vec4(Position, 1.0);
    gl_Position = pos;

    // Нормализованные координаты текстуры (0-1)
    TexCoord = Position.xy / resolution;
}