#version 150

in vec3 Position;
in vec2 UV0;

uniform mat4 ModelViewMat;  // объявлены для совместимости с глобальной программой
uniform mat4 ProjMat;

out vec2 uv;

void main() {
    // Полноэкранный квад прямо в NDC; матрицы не нужны.
    gl_Position = vec4(Position.xy, 0.0, 1.0);
    uv = UV0;
}
