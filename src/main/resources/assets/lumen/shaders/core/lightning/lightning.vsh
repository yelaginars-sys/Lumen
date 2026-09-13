#version 150

in vec3 Position;
in vec2 UV0;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

// x — доля пути вдоль дуги (0 хвост, 1 голова), y — поперёк ленты (-1..1).
out vec2 Coord;
out vec4 Tint;

void main() {
    Coord = UV0;
    Tint = Color;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}
