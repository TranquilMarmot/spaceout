package spaceguts.physics;

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
	
	private static short bit(int x) {
		return (short) (1 << x);
	}
}
