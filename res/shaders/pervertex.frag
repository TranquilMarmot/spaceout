#version 400

in vec3 Position;
in vec3 Normal;

struct LightInfo{
	vec4 LightPosition;
	vec3 LightIntensity;
};
uniform LightInfo Light;

struct MaterialInfo{
	vec3 Kd;
	vec3 Ka;
	vec3 Ks;
	float Shininess;
};
uniform MaterialInfo Material;

const int levels = 5;
const float scaleFactor = 1.0;

layout(location = 0) out vec4 FragColor;

vec3 ads(){
	vec3 n = normalize(Normal);
	vec3 s = normalize(vec3(Light.LightPosition) - Position);
	vec3 v = normalize(vec3(-Position));
	vec3 h = normalize(v + s);
	return Light.LightIntensity * 
		(Material.Ka +
		 Material.Kd * max(dot(s, n), 0.0) +
		 Material.Ks * pow(max(dot(h, n), 0.0), Material.Shininess));
}

vec3 toonShade(){
	vec3 s = normalize(Light.LightPosition.xyz - Position.xyz);
	float cosine = max(0.0, dot(s, Normal));
	vec3 diffuse = Material.Kd * floor(cosine * levels) * scaleFactor;
	return Light.LightIntensity * (Material.Ka + diffuse);
}

void main() {
	FragColor = vec4(ads(), 1.0);
}
