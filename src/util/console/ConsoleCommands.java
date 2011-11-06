package util.console;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.lwjgl.util.vector.Vector3f;

import util.Runner;
import entities.Entities;

public class ConsoleCommands {
	private static Console console = ConsoleManager.console;

	public static void issueCommand(String comm) {
		// split the command at the spaces
		StringTokenizer toker = new StringTokenizer(comm, " ");

		// grab the actual command and lop off the / at the beginning
		String command = toker.nextToken();
		command = command.substring(1, command.length());

		try {
			if (command.equals("xyz") || command.equals("pos")
					|| command.equals("position")) {
				positionCommand(toker);
			}

			else if (command.equals("clear")) {
				console.text.clear();
			}

			else if (command.equals("speed")) {
				speedCommand(toker);
			}

			else if (command.equals("listentities")) {
				for (entities.Entity ent : Entities.entities)
					console.print(ent.type);
			}

			else if (command.equals("beer")) {
				for (int i = 99; i > 0; i--) {
					console.print(i + " bottles of beer on the wall, " + i
							+ " bottles of beer");
					console.print("Take one down, pass it around, " + (i - 1)
							+ " bottles of beer on the wall");
				}
			}

			else if (command.equals("camera")) {
				cameraCommand(toker);
			}

			else if (command.equals("quit") || command.equals("exit")
					|| command.equals("q")) {
				Runner.done = true;
			}

			else if (command.equals("warp")) {
				warpCommand(toker);
			}

			else {
				console.print("Invalid command! (" + command + ")");
			}
		} catch (NoSuchElementException e) {
			console.print("Not enough vairbales for command '" + command + "'!");
		} catch (NumberFormatException e) {
			console.print("Incorrect number format "
					+ e.getLocalizedMessage().toLowerCase());
		}
	}

	private static void positionCommand(StringTokenizer toker) {
		float x = Float.parseFloat(toker.nextToken());
		float y = Float.parseFloat(toker.nextToken());
		float z = Float.parseFloat(toker.nextToken());

		float oldX = Entities.player.location.x;
		float oldY = Entities.player.location.y;
		float oldZ = Entities.player.location.z;

		console.print("Moving player to x: " + x + " y: " + y + " z: " + z
				+ " from x: " + oldX + " y: " + oldY + " z: " + oldZ);

		Entities.player.location.x = x;
		Entities.player.location.y = y;
		Entities.player.location.z = z;
	}

	private static void warpCommand(StringTokenizer toker) {
		String warp = toker.nextToken();
		for (entities.Entity ent : Entities.entities) {
			if (ent.type.toLowerCase().equals(warp)) {
				Entities.player.location = new Vector3f(ent.location.x,
						ent.location.y, ent.location.z);
			}
		}
	}

	private static void speedCommand(StringTokenizer toker) {
		float speed = Float.parseFloat(toker.nextToken());
		float oldSpeed = Entities.player.zSpeed;

		ConsoleManager.console.print("Changing player speed from " + oldSpeed + " to "
				+ speed);
		Entities.player.zSpeed = speed;
	}

	private static void cameraCommand(StringTokenizer toker) {
		String cameraCommand = toker.nextToken().toLowerCase();
		if (cameraCommand.equals("zoom")) {
			float zoom = Float.parseFloat(toker.nextToken());
			console.print("Changing camera zoom from " + Entities.camera.zoom
					+ " to " + zoom);
			Entities.camera.zoom = zoom;
		} else if (cameraCommand.equals("yoffset")) {
			float yOffset = Float.parseFloat(toker.nextToken());
			console.print("Changing camera yOffset from "
					+ Entities.camera.yOffset + " to " + yOffset);
			Entities.camera.yOffset = yOffset;
		} else if (cameraCommand.equals("xoffset")) {
			float xOffset = Float.parseFloat(toker.nextToken());
			console.print("Changing camera xOffset from "
					+ Entities.camera.xOffset + " to " + xOffset);
			Entities.camera.xOffset = xOffset;
		} else {
			console.print("Invalid camera command! (" + cameraCommand + ")");
		}
	}
}
