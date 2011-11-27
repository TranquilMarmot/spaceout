package util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import util.debug.Debug;

/**
 * Takes a screenshot of the current OpenGL context. A majority of the code for
 * this was taken from <a
 * href="http://lwjgl.org/wiki/index.php?title=Taking_Screen_Shots">the LWJGL
 * wiki.</a>
 * 
 * @author TranquilMarmot
 * 
 */
public class Screenshot {
	static int screenshotWidth, screenshotHeight;

	/**
	 * Takes a screenshot of the current OpenGL context.
	 * 
	 * @param currentWindowWidth
	 *            Current width of the window
	 * @param currentWindowHeight
	 *            Current height of the window
	 */
	public static void takeScreenshot(int currentWindowWidth,
			int currentWindowHeight) {
		// instantiating a gergorian calendar sets it to the current time and
		// date
		Calendar calendar = new GregorianCalendar();
		// string for 'am' or 'pm'
		String am_pm;

		int hours = calendar.get(Calendar.HOUR);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);

		if (calendar.get(Calendar.AM_PM) == 0)
			am_pm = "am";
		else
			am_pm = "pm";

		/*
		 * Format the string to match spaceout-mm.dd.yy-hh.mm.ss[am/pm] NOTE:
		 * This won't work properly past the year 2100.
		 */
		Formatter format = new Formatter();
		format.format("spaceout-%02d.%02d.%d-%02d.%d.%02d" + am_pm, month, day,
				year - 2000, hours, minutes, seconds);

		GL11.glReadBuffer(GL11.GL_FRONT);
		int bpp = 4; // Assuming a 32-bit display with a byte each for red,
						// green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(currentWindowWidth
				* currentWindowHeight * bpp);
		GL11.glReadPixels(0, 0, currentWindowWidth, currentWindowHeight,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		File file = new File("res/screenshots/" + format.toString() + ".jpg");
		String fileFormat = "JPG";
		BufferedImage image = new BufferedImage(currentWindowWidth,
				currentWindowHeight, BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < currentWindowWidth; x++)
			for (int y = 0; y < currentWindowHeight; y++) {
				int i = (x + (currentWindowWidth * y)) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, currentWindowHeight - (y + 1), (0xFF << 24)
						| (r << 16) | (g << 8) | b);
			}

		try {
			file.createNewFile();
			ImageIO.write(image, fileFormat, file);
			Debug.console
					.print("Saved screenshot to " + file.getAbsolutePath());
		} catch (IOException e) {
			System.out.println("Error writing screenshot to "
					+ file.getAbsolutePath()
					+ "\nMaybe the folder doesn't exist?");
		}
	}
}
