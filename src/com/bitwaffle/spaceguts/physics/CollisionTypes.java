package com.bitwaffle.spaceguts.physics;

/**
 * This is an enumeration of the different groups that each {@link DynamicEntity} can belong to
 * @author TranquilMarmot
 *
 */
public class CollisionTypes {
	public static final short NOTHING = 0;
	public static final short SHIP = bit(0);
	public static final short WALL = bit(1);
	public static final short PLANET = bit(2);
	public static final short PROJECTILE = bit(3);
	public static final short PICKUP = bit(4);
	
	public static final short EVERYTHING = (short)(SHIP | WALL | PLANET | PROJECTILE | PICKUP); 
	
	private static short bit(int x) {
		return (short) (1 << x);
	}
}
