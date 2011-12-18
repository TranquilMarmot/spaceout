package entities;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import entities.dynamic.DynamicEntity;
import graphics.model.Model;

public class Enemy extends DynamicEntity{
	public Enemy(Vector3f location, Quaternion rotation, Model model, float mass,
			float restitution) {
		super(location, rotation, model, mass, restitution);
	}
	
	public Enemy(Vector3f location, Quaternion rotation, int model, float mass,
			float restitution) {
		super(location, rotation, model, mass, restitution);
	}
	
	
}
