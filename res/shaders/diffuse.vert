#version 400

layout (location = 0) in vec3 VertexPosition;
layout (location = 1) in vec3 VertexNormal;

out vec3 LightIntensity;

struct LightInfo {
	vec4 LightPosition; // Light position in eye coords.
	vec3 Kd;            // Diffuse reflectivity
	vec3 Ld;            // Diffuse light intensity
};
uniform LightInfo Light;

uniform mat4 ModelViewMatrix;
uniform mat4 ProjectionMatrix;

void main()
{
	mat3 NormalMatrix = mat3(ModelViewMatrix);
    vec3 tnorm = normalize( NormalMatrix * VertexNormal);
	
	vec4 eyeCoords = ModelViewMatrix * vec4(VertexPosition,1.0);
    vec3 s = normalize(vec3(Light.LightPosition - eyeCoords));

    LightIntensity = Light.Ld * Light.Kd * max( dot( s, tnorm ), 0.0 );

	mat4 MVP = ProjectionMatrix * ModelViewMatrix;
    gl_Position = MVP * vec4(VertexPosition,1.0);
}
