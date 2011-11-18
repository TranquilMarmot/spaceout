package util.helper;

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

public class DisplayHelper {
	private static final String ICON_PATH = "res/images/";
	
	/**
	 * these change whenever the screen size is changed. The values that they
	 * are now will be the initial size of the window.
	 */
	public static int windowWidth = 1024;
	public static int windowHeight = 768;
	
	/** the minimum size that the window can be */
	private static final int minimumWindowWidth = 200;
	private static final int minimumWindowHeight = 200;
	
	/** the window's title */
	private static String windowTitle = "Spaceout Pre-alpha 0.0.7";

	/** target fps (might not be reached on slower machines) */
	public static int targetFPS = 100;

	/** this is just to handle the resizeable window */
	private final static AtomicReference<Dimension> newCanvasSize = new AtomicReference<Dimension>();

	/** the frame everything is in so that the window is resizeable */
	public static Frame frame;
	
	/** whether or not the display has requested a close (instantly shuts down game)*/
	public static boolean closeRequested = false;
	
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
	
	public static void createWindow(){
		// create the frame that holds everything
		frame = new Frame(windowTitle);
		frame.setLayout(new BorderLayout());
		frame.setBackground(java.awt.Color.black);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				ICON_PATH + "spoutlogo.png"));
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
				closeRequested = true;
			}
		});

		// add the canvas to the frame
		frame.add(canvas, BorderLayout.CENTER);

		try {
			// set the display to use the canvas
			Display.setParent(canvas);
			Display.setVSyncEnabled(true);

			// pretty self explanatory, boilerplate awt frame creation
			frame.setPreferredSize(new Dimension(windowWidth, windowHeight));
			frame.setMinimumSize(new Dimension(minimumWindowWidth,
					minimumWindowHeight));
			frame.pack();
			frame.setVisible(true);
			Display.create();

		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
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
	}
	
	public static void doFullscreenLogic(){
		if (KeyboardHandler.fullscreen && !fullscreenDown) {
			DisplayHelper.fullscreen = !DisplayHelper.fullscreen;
			fullscreenDown = true;
		}

		if (!KeyboardHandler.fullscreen) {
			fullscreenDown = false;
		}
		
		
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
		/* END FULLSCREEN LOGIC */

		if (!(frame.getTitle().equals(windowTitle)))
			frame.setTitle(windowTitle);
		
		if(!(Display.getTitle().equals(windowTitle)))
			Display.setTitle(windowTitle);
	}
}
