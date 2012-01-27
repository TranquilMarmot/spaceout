#version 400

subroutine vec3 shadeModelType(vec4 position, vec3 normal);
subroutine uniform shadeModelType shadeModel;

layout (location = 0) in vec3 VertexPosition;
layout (location = 1) in vec3 VertexNormal;

out vec3 LightIntensity;

struct LightInfo {
  vec4 Position; // Light position in eye coords.
  vec3 La;       // Ambient light intensity
  vec3 Ld;       // Diffuse light intensity
  vec3 Ls;       // Specular light intensity
};
uniform LightInfo Light;

struct MaterialInfo {
  vec3 Ka;            // Ambient reflectivity
  vec3 Kd;            // Diffuse reflectivity
  vec3 Ks;            // Specular reflectivity
  float Shininess;    // Specular shininess factor
};
uniform MaterialInfo Material;

uniform mat4 ModelViewMatrix;
uniform mat4 ProjectionMatrix;

mat3 NormalMatrix;

void getEyeSpace(out vec3 norm, out vec4 position){
	norm = normalize(NormalMatrix * VertexNormal);
	position = ModelViewMatrix * vec4(VertexPosition, 1.0);
}

subroutine(shadeModelType)
vec3 phongModel(vec4 position, vec3 norm){
	vec3 s;
	if( Light.Position.w == 0.0 )
		s = normalize(vec3(Light.Position));
	else
		s = normalize(vec3(Light.Position - position));


	//vec3 s = normalize(vec3(Light.Position - position));
	vec3 v = normalize(-position.xyz);
	vec3 r = reflect(-s, norm);
	vec3 ambient = Light.La * Material.Ka;
	float sDotN = max(dot(s, norm), 0.0);
	vec3 diffuse = Light.Ld * Material.Kd * sDotN;
	vec3 spec = vec3(0.0);
	if(sDotN > 0.0)
		spec = Light.Ls * Material.Ks * pow(max(dot(r, v), 0.0), Material.Shininess);
	return ambient + diffuse + spec;
}

subroutine(shadeModelType)
vec3 diffuseOnly(vec4 position, vec3 norm){
	vec3 s = normalize(vec3(Light.Position - position));
	return Light.Ld * Material.Kd * max(dot(s, norm), 0.0);
}

void main()
{
	NormalMatrix = mat3(ModelViewMatrix);
	
	vec3 eyeNorm;
	vec4 eyePosition;
	
	// get position and normal in eye space
	getEyeSpace(eyeNorm, eyePosition);
	
	// evaluate lighting equation
	LightIntensity = shadeModel(eyePosition, eyeNorm);
	
	mat4 MVP = ProjectionMatrix * ModelViewMatrix;
    gl_Position = MVP * vec4(VertexPosition,1.0);
}
