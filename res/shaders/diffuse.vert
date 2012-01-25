#version 400

layout (location = 0) in vec3 VertexPosition;
layout (location = 1) in vec3 VertexNormal;

out vec3 LightIntensity;

uniform vec3 LightPosition; // Light position in eye coords.
uniform vec3 Kd;            // Diffuse reflectivity
uniform vec3 Ld;            // Diffuse light intensity

uniform mat4 ModelViewMatrix;
uniform mat4 ProjectionMatrix;

void main()
{
	mat3 NormalMatrix = mat3(ModelViewMatrix);
    vec3 tnorm = normalize( NormalMatrix * VertexNormal);
    vec3 s = normalize(LightPosition);

    LightIntensity = Ld * Kd * max( dot( s, tnorm ), 0.0 );

	mat4 MVP = ProjectionMatrix * ModelViewMatrix;
    gl_Position = MVP * vec4(VertexPosition,1.0);
}
