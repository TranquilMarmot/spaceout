package gui.menu;

import entities.Camera;
import entities.Entities;
import gui.GUI;
import gui.GUIObject;
import gui.button.PauseMenuButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import util.Runner;

public class PauseMenu extends GUIObject{
	private PauseMenuButton quitButton, pauseButton;
	
	private boolean backToMainMenu = false;
	
	public PauseMenu() {
		super(0, 0);

		// initialize the camera
		Entities.camera = new Camera(Entities.player.location.x,
				Entities.player.location.y, Entities.player.location.z);
		Entities.camera.zoom = 10.0f;
		Entities.camera.yOffset = -2.5f;
		Entities.camera.xOffset = 0.1f;
		Entities.camera.following = Entities.player;

		// button to resume the game if it's paused
		pauseButton = new PauseMenuButton("resume",
				115, 39, -75, 50);
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Runner.paused = false;
			}
		});

		// button to quit the game if it's paused
		quitButton = new PauseMenuButton("main menu", 115,
				39, 75, 50);
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backToMainMenu = true;
			}
		});
	}

	@Override
	public void update() {
		quitButton.update();
		pauseButton.update();
		
		if(backToMainMenu){
			Entities.entities.clear();
			Entities.player = null;
			Entities.camera = null;
			GUI.removeBuffer.add(this);
			GUI.addBuffer.add(new MainMenu());
		}
		
	}

	@Override
	public void draw() {
		quitButton.draw();
		pauseButton.draw();
	}
}
