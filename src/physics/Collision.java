package physics;

public class Collision {
	public enum CollisionTypes {
		COL_NOTHING(0),
		COL_SHIP(bit(0)),
		COL_WALL(bit(1)),
		COL_PLANET(bit(2));

		private int value;

		private CollisionTypes(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	};

	private static int bit(int x) {
		return 1 << x;
	}
}
