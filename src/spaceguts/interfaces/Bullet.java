package spaceguts.interfaces;

import spaceguts.entities.Entity;

/**
 * Interface for a bullet (anything that does damage)
 * @author TranquilMarmot
 */
public interface Bullet {
	/**
	 * How much damage does this bullet do when it hits something?
	 * @return Amount of damage to do
	 */
	public int getDamage();
	
	/**
	 * Who owns this bullet? (Who can't it hurt?)
	 * @return Whoever fired the bullet
	 */
	public Entity getOwner();
}
