package com.bitwaffle.spaceguts.graphics.gui.menu;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.bitwaffle.spaceguts.entities.Entities;
import com.bitwaffle.spaceguts.graphics.gui.GUI;
import com.bitwaffle.spaceguts.graphics.gui.GUIObject;
import com.bitwaffle.spaceguts.graphics.gui.button.PauseMenuButton;
import com.bitwaffle.spaceguts.physics.Physics;
import com.bitwaffle.spaceguts.util.Runner;



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
			GUI.removeGUIObject(this);
			GUI.addGUIObject(new MainMenu());
		}
	}

	@Override
	public void draw() {
		backToMainButton.draw();
		resumeButton.draw();
	}
}
