#version 150

in vec3 Position;
in vec2 UV0;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 CameraPos;

out vec3 WorldPos;
out vec3 ViewPos;
out vec4 FragColor;

void main() {
    vec4 viewPos = ModelViewMat * vec4(Position, 1.0);
    ViewPos = viewPos.xyz;
    // Мировые координаты: узор непрерывен по всему телу — накладывается одним целым,
    // без швов на стыках частей модели
    WorldPos = Position + CameraPos;
    FragColor = Color;
    gl_Position = ProjMat * viewPos;
}
