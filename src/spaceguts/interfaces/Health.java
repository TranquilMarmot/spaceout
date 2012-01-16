package spaceguts.interfaces;

/**
 * Interface for anything that can get hurt to implement.
 * @author TranquilMarmot
 */
public interface Health {
	/**
	 * Get the current health of the entity
	 * @return Current health
	 */
	public int getCurrentHealth();
	
	/**
	 * Hurt the entity
	 * @param amount The amount to hurt the entity by (i.e. from {@link Bullet}.getDamage())
	 */
	public void hurt(int amount);
	
	/**
	 * Heal the entity
	 * @param amount The amount to heal the entity by
	 */
	public void heal(int amount);
}
