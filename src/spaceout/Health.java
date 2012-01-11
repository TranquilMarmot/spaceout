package spaceout;

public interface Health {
	public int getCurrentHealth();
	public void hurt(int amount);
	public void heal(int amount);
}
