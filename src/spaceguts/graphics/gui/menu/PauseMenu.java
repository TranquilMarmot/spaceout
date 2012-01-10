package spaceguts.graphics.gui.menu;

import spaceguts.entities.Entities;
import spaceguts.graphics.gui.GUI;
import spaceguts.graphics.gui.GUIObject;
import spaceguts.graphics.gui.button.PauseMenuButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import spaceguts.physics.Physics;

import spaceguts.util.Runner;

/**
 * The pause menu!
 * @author TranquilMarmot
 *
 */
public class PauseMenu extends GUIObject{
	/** button on the pause menu */
	private PauseMenuButton backToMainButton, resumeButton;
	
	/** whether or not to go back to the main menu on the next update */
	private boolean backToMainMenu = false;
	
	/**
	 * Pause menu constructor. Automatically adds the pause menu to GUI.guiObjects
	 */
	public PauseMenu() {
		super(0, 0);

		// button to resume the game
		resumeButton = new PauseMenuButton("resume",
				119, 28, -75, 50);
		resumeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Runner.paused = false;
			}
		});

		// button to go back to the main menu
		backToMainButton = new PauseMenuButton("main menu", 119,
				28, 75, 50);
		backToMainButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backToMainMenu = true;
			}
		});
	}

	@Override
	public void update() {
		backToMainButton.update();
		resumeButton.update();
		
		if(backToMainMenu){
			Entities.cleanup();
			Physics.cleanup();
			GUI.removeBuffer.add(this);
			GUI.addBuffer.add(new MainMenu());
		}
		
	}

	@Override
	public void draw() {
		backToMainButton.draw();
		resumeButton.draw();
	}
}
