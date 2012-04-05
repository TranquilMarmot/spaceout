package com.bitwaffle.spaceout.entities.dynamic;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bitwaffle.spaceguts.physics.CollisionTypes;
import com.bitwaffle.spaceout.resources.Models;
import com.bulletphysics.collision.shapes.ConeShape;

public class Diamond extends Pickup{
	private static final Models MODEL = Models.DIAMOND;
	private static final float MASS = 1.0f;
	private static final float RESTITUTION = 0.5f;
	final static short COL_GROUP = CollisionTypes.PICKUP;
	final static short COL_WITH = (short)(CollisionTypes.WALL | CollisionTypes.PLANET | CollisionTypes.SHIP);

	public Diamond(Vector3f location, Quaternion rotation) {
		super(location, rotation, new ConeShape(0.4f, 0.7f), MASS, RESTITUTION, COL_GROUP, COL_WITH);
		this.model = MODEL.getModel();
		this.type = "Diamond";
	}
}
