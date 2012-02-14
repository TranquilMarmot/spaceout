#version 400

layout (location = 0) in vec3 VertexPosition;
layout (location = 1) in vec3 VertexNormal;
layout (location = 2) in vec2 VertexTexCoord;

out vec3 Position;
out vec3 Normal;
out vec2 TexCoord;

uniform mat4 ModelViewMatrix;
uniform mat4 ProjectionMatrix;

mat3 NormalMatrix;

void main()
{
	NormalMatrix = mat3(ModelViewMatrix);
    TexCoord = vec2(VertexTexCoord.x, VertexTexCoord.y);
    Normal = normalize( NormalMatrix * VertexNormal);
    Position = vec3( ModelViewMatrix * vec4(VertexPosition,1.0) );

	mat4 MVP = ProjectionMatrix * ModelViewMatrix;
    gl_Position = MVP * vec4(VertexPosition,1.0);
}
