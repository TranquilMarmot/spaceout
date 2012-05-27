package com.bitwaffle.spaceguts.util;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicReference;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import com.bitwaffle.spaceguts.input.KeyBindings;
import com.bitwaffle.spaceout.Runner;


/**
 * This class handles creating and displaying a resizeable window to render to
 * @author TranquilMarmot
 *
 */
public class DisplayHelper {
	private static final String ICON_PATH = "res/images/";
	
	/** How many samples to use for multisample anti-aliasing */
	public static final int MSAA_SAMPLES = 4;
	
	/**
	 * these change whenever the screen size is changed. The values that they
	 * are now will be the initial size of the window.
	 */
	public static int windowWidth = 1024;
	public static int windowHeight = 768;
	
	/** the minimum size that the window can be */
	private static final int MIN_WINDOW_WIDTH = 200;
	private static final int MIN_WINDOW_HEIGHT = 200;
	
	/** the window's title */
	private static String windowTitle = "Spaceout Pre-alpha " + Runner.VERSION;

	/** target fps (might not be reached on slower machines) */
	public static int targetFPS = 60;
	
	public static boolean vsync = false;

	/** this is just to handle the resizeable window */
	private final static AtomicReference<Dimension> newCanvasSize = new AtomicReference<Dimension>();

	/** the frame everything is in so that the window is resizeable */
	public static Frame frame;
	
	/**
	 * used to preserve the size of the window when switching between fullscreen
	 * and windowed
	 */
	private static int oldWindowHeight;
	private static int oldWindowWidth;
	
	/** whether or not the game is running in fullscreen mode */
	public static boolean fullscreen = false;
	
	/** boolean to check for fullscreen key being held down */
	private static boolean fullscreenDown = false;
	
	/**
	 * Creates a window to render to
	 */
	public static void createWindow(){
		// create the frame that holds everything
		frame = new Frame(windowTitle);
		frame.setLayout(new BorderLayout());
		frame.setBackground(java.awt.Color.black);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				ICON_PATH + "icon.png"));
		final Canvas canvas = new Canvas();

		// add listeners to the canvas
		// this one handles resizes
		canvas.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				newCanvasSize.set(canvas.getSize());
			}
		});

		// the canvas is the only thing we want focus on
		frame.addWindowFocusListener(new WindowAdapter() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				canvas.requestFocusInWindow();
			}
		});

		// handles closing the window
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Runner.done = true;
			}
		});

		// add the canvas to the frame
		frame.add(canvas, BorderLayout.CENTER);

		try {
			// set the display to use the canvas
			Display.setParent(canvas);
			Display.setVSyncEnabled(vsync);

			// pretty self explanatory, boilerplate awt frame creation
			frame.setPreferredSize(new Dimension(windowWidth, windowHeight));
			frame.setMinimumSize(new Dimension(MIN_WINDOW_WIDTH,
					MIN_WINDOW_HEIGHT));
			frame.pack();
			frame.setVisible(true);
			
			// for creating a display with multisampling
			PixelFormat pf = new PixelFormat().withSamples(MSAA_SAMPLES);
			
			try{
				Display.create(pf);
			} catch(LWJGLException e){
				System.out.println("Couldn't initialize display with " + MSAA_SAMPLES + "x MSAA, initializing with no anti-aliasing instead");
				Display.create();
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Resizes the window if it's dimensions have been changed
	 */
	public static void resizeWindow(){
		Dimension d = new Dimension();

		/*
		 * newCanvasSize is updated by the listener on the canvas that
		 * everything is being displayed on. This gets the current
		 * newCanvasSize, and then sets it to null. This way, whenever the size
		 * hasn't changed newCanvasSize is null and nothing happens. If the size
		 * has changed, newCanvasSize will contain the new size and won't be
		 * null.
		 */
		d = newCanvasSize.getAndSet(null);

		if (d != null) {
			// change the height and width
			windowWidth = (int) d.getWidth();
			windowHeight = (int) d.getHeight();
			// reset the viewport
			GL11.glViewport(0, 0, windowWidth, windowHeight);
		}
		
		doFullscreenLogic();
	}
	
	/**
	 * Checks to see if the fullscreen key has been pressed and, if it has, acts accordingly
	 */
	private static void doFullscreenLogic(){
		//check for fullscreen key press
		if (KeyBindings.SYS_FULLSCREEN.isPressed() && !fullscreenDown) {
			fullscreen = !fullscreen;
			fullscreenDown = true;
		}

		if (!KeyBindings.SYS_FULLSCREEN.isPressed()) {
			fullscreenDown = false;
		}
		
		
		//take care of necessary OpenGL calls
		try {
			// switch from windowed to fullscreen
			if (fullscreen && !Display.isFullscreen()) {
				// save the window width and height
				oldWindowWidth = windowWidth;
				oldWindowHeight = windowHeight;
				// fullscreen the display
				Display.setFullscreen(fullscreen);
				// set the width and height
				DisplayMode dm = Display.getDisplayMode();
				windowWidth = dm.getWidth();
				windowHeight = dm.getHeight();
				// change the viewport
				GL11.glViewport(0, 0, windowWidth, windowHeight);
				
				// switch from fullscreen to windowed
			} else if (!fullscreen && Display.isFullscreen()) {
				// get the window width and height from before fullscreen
				windowWidth = oldWindowWidth;
				windowHeight = oldWindowHeight;
				// un-fullscreen the display
				Display.setFullscreen(fullscreen);
				// change the viewport
				GL11.glViewport(0, 0, windowWidth, windowHeight);
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		// make sure the frame's title is right (this allows for a changing title!)
		if (!(frame.getTitle().equals(windowTitle)))
			frame.setTitle(windowTitle);
		
		// make sure the display's title is right
		if(!(Display.getTitle().equals(windowTitle)))
			Display.setTitle(windowTitle);
	}
}
