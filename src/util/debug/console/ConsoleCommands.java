package util.debug.console;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import util.Runner;
import util.debug.Debug;
import util.helper.QuaternionHelper;
import entities.Entities;
import entities.Entity;

/**
 * Commands that the {@link Console} carries out. See console_commands.txt.
 * 
 * @author TranquilMarmot
 * @see Console
 * 
 */
public class ConsoleCommands {
	private static Console console = Debug.console;

	/**
	 * Issues a command
	 * 
	 * @param comm
	 *            The command to issue
	 */
	public static void issueCommand(String comm) {
		// make sure the command isn't empty
		if (comm.length() > 1) {
			// split the command at the spaces
			StringTokenizer toker = new StringTokenizer(comm, " ");

			// grab the actual command and lop off the / at the beginning
			String command = toker.nextToken();
			command = command.substring(1, command.length());

			try {
				// player position
				if (command.equals("xyz") || command.equals("pos")
						|| command.equals("position")) {
					positionCommand(toker);
				}

				// clear console
				else if (command.equals("clear")) {
					console.text.clear();
				}

				// player speed
				else if (command.equals("speed")) {
					speedCommand(toker);
				}

				// list entities
				else if (command.equals("listentities")) {
					console.print("Listing entities...");
					for (entities.Entity ent : Entities.entities)
						console.print(ent.type);
				}

				// print number of entities
				else if (command.equals("numentities")) {
					console.print("Number of entities: "
							+ Entities.entities.size());
				}

				// 99 bottles of beer on the wall
				else if (command.equals("beer")) {
					for (int i = 99; i > 0; i--) {
						console.print(i + " bottles of beer on the wall, " + i
								+ " bottles of beer");
						console.print("Take one down, pass it around, "
								+ (i - 1) + " bottles of beer on the wall");
					}
				}

				// camera command
				else if (command.equals("camera")) {
					cameraCommand(toker);
				}

				// quit
				else if (command.equals("quit") || command.equals("exit")
						|| command.equals("q")) {
					Runner.done = true;
				}

				// warp command
				else if (command.equals("warp")) {
					warpCommand(toker);
				}

				// invalid
				else {
					console.print("Invalid command! (" + command + ")");
				}
			} catch (NoSuchElementException e) {
				console.print("Not enough vairbales for command '" + command
						+ "'!");
			} catch (NumberFormatException e) {
				console.print("Incorrect number format "
						+ e.getLocalizedMessage().toLowerCase());
			}
		}
	}

	/**
	 * Carry out a command to change the player's position
	 * 
	 * @param toker
	 *            StringTokenizer containing the command
	 */
	private static void positionCommand(StringTokenizer toker) {
		float x = Float.parseFloat(toker.nextToken());
		float y = Float.parseFloat(toker.nextToken());
		float z = Float.parseFloat(toker.nextToken());

		float oldX = Entities.player.location.x;
		float oldY = Entities.player.location.y;
		float oldZ = Entities.player.location.z;

		console.print("Moving player to x: " + x + " y: " + y + " z: " + z
				+ " from x: " + oldX + " y: " + oldY + " z: " + oldZ);
		
		Transform trans = new Transform();
		Entities.player.rigidBody.getWorldTransform(trans);
		
		trans.transform(new javax.vecmath.Vector3f(x, y, z));
		
		Entities.player.rigidBody.setMotionState(new DefaultMotionState(trans));

		Entities.player.location.x = x;
		Entities.player.location.y = y;
		Entities.player.location.z = z;
	}

	/**
	 * Command to warp to an entity
	 * 
	 * @param toker
	 *            StringTokenizer containing the command
	 */
	private static void warpCommand(StringTokenizer toker) {
		boolean hasWarped = false;
		String warp = toker.nextToken();
		for (Entity ent : Entities.entities) {
			//FIXME does not work
			if (ent.type.toLowerCase().equals(warp.toLowerCase())) {
				console.print("Warping Player to " + ent.type + " ("
						+ ent.location.x + "," + ent.location.y + ","
						+ ent.location.z + ")");
				Transform playerTransform = new Transform();
				Entities.player.rigidBody.getWorldTransform(playerTransform);
				Vector3f moveAmount = new Vector3f(0.0f, 0.0f, -10.0f);
				Quat4f playerRot = new Quat4f();
				playerTransform.getRotation(playerRot);
				Vector3f rotated = QuaternionHelper.RotateVectorByQuaternion(moveAmount, new Quaternion(playerRot.x, playerRot.y, playerRot.z, playerRot.w));
				Vector3f newLocation = new Vector3f();
				Vector3f.add(new Vector3f(ent.location.x, ent.location.y, ent.location.z), rotated, newLocation);
				
				playerTransform.transform(new javax.vecmath.Vector3f(newLocation.x, newLocation.y, newLocation.z));
				
				Entities.player.rigidBody.setWorldTransform(playerTransform);
				hasWarped = true;
				break;
			}
		}
		
		if(!hasWarped){
			console.print("Couldn't find entity " + warp);
		}
	}

	/**
	 * Command to change the player's speed variables
	 * 
	 * @param toker
	 *            StringTokenizer containing the command
	 */
	private static void speedCommand(StringTokenizer toker) {
		String speedCommand = toker.nextToken().toLowerCase();
		Float speedChange = Float.parseFloat(toker.nextToken());
		
		if(speedCommand.equals("x")){
			console.print("Changing player X acceleration from " + Entities.player.xAccel + " to " + speedChange);
			Entities.player.xAccel = speedChange;
		} else if(speedCommand.equals("y")){
			console.print("Changing player Y acceleration from " + Entities.player.yAccel + " to " + speedChange);
			Entities.player.yAccel = speedChange;
		} else if(speedCommand.equals("z")){
			console.print("Changing player Z acceleration from " + Entities.player.zAccel + " to " + speedChange);
			Entities.player.zAccel = speedChange;
		} else if(speedCommand.equals("stable")){
			console.print("Changing player stabilization speed from " + Entities.player.stabilizationSpeed + " to " + speedChange);
			Entities.player.stabilizationSpeed = speedChange;
		} else if(speedCommand.equals("stop")){
			console.print("Changing player stop speed from " + Entities.player.stopSpeed + " to " + speedChange);
			Entities.player.stopSpeed = speedChange;
		} else if(speedCommand.equals("roll")){
			console.print("Changing player roll speed from " + Entities.player.rollSpeed + " to " + speedChange);
			Entities.player.rollSpeed = speedChange;
		}
		// invalid
		else {
			console.print("Not a valid speed command!");
		}
	}

	/**
	 * Command to change how the camera behaves
	 * 
	 * @param toker
	 *            StringTokenizer containing the command
	 */
	private static void cameraCommand(StringTokenizer toker) {
		String cameraCommand = toker.nextToken().toLowerCase();

		// zoom
		if (cameraCommand.equals("zoom")) {
			float zoom = Float.parseFloat(toker.nextToken());
			console.print("Changing camera zoom from " + Entities.camera.zoom
					+ " to " + zoom);
			Entities.camera.zoom = zoom;
		}

		// y offset
		else if (cameraCommand.equals("yoffset")) {
			float yOffset = Float.parseFloat(toker.nextToken());
			console.print("Changing camera yOffset from "
					+ Entities.camera.yOffset + " to " + yOffset);
			Entities.camera.yOffset = yOffset;
		}

		// x offset
		else if (cameraCommand.equals("xoffset")) {
			float xOffset = Float.parseFloat(toker.nextToken());
			console.print("Changing camera xOffset from "
					+ Entities.camera.xOffset + " to " + xOffset);
			Entities.camera.xOffset = xOffset;
		}

		// following
		else if (cameraCommand.equals("follow")) {
			boolean changed = false;
			String toFollow = toker.nextToken();

			if (toFollow.toLowerCase().equals("player")) {
				console.print("Camera now following " + toFollow);
				Entities.camera.following = Entities.player;
				changed = true;
			} else {
				for (Entity ent : Entities.entities) {
					if (ent.type.toLowerCase().equals(toFollow.toLowerCase())) {
						console.print("Camera now following " + toFollow);
						Entities.camera.following = ent;
						changed = true;
						break;
					}
				}
			}
			
			if (!changed) {
				console.print("Couldn't find entity " + toFollow);
			}
		}

		// invalid
		else {
			console.print("Invalid camera command! (" + cameraCommand + ")");
		}
	}
}
