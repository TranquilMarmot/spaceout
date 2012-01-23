#version 400

layout (location = 0) in vec3 VertexPosition;
layout (location = 1) in vec3 VertexColor;

out vec3 Color;

uniform mat4 ModelView;
uniform mat4 Projection;

void main()
{
    Color = VertexColor;
	mat4 MVP =  Projection * ModelView;
    gl_Position = MVP * vec4(VertexPosition,1.0);
}
