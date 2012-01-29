package spaceguts.util.console;

import java.util.StringTokenizer;

import javax.vecmath.Quat4f;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import spaceguts.entities.DynamicEntity;
import spaceguts.entities.Entities;
import spaceguts.entities.Entity;
import spaceguts.entities.Light;
import spaceguts.util.QuaternionHelper;
import spaceguts.util.Runner;

/**
 * All the possible console commands. These are called by the console by their name,
 * so name a command however you want it to be referenced to in-game.
 * Each command constructor takes in a command class that implements the inner Command interface.
 * Many commands can use the same command class.
 * @author TranquilMarmot
 *
 */
public enum ConsoleCommands {
	help(new HelpCommand()),
	
	list(new ListCommand()),
	
	xyz(new PositionCommand()),
	pos(new PositionCommand()),
	position(new PositionCommand()),
	
	clear(new ClearCommand()),
	
	speed(new SpeedCommand()),
	
	numentities(new NumberCommand()),
	
	beer(new BeerCommand()),
	
	camera(new CameraCommand()),
	
	quit(new QuitCommand()),
	q(new QuitCommand()),
	exit(new QuitCommand()),
	
	warp(new WarpCommand());

	
	Command function;
	private ConsoleCommands(Command function){
		this.function = function;
	}
	
	public void issue(StringTokenizer toker){
		function.issue(toker);
	}
	
	public void help(){
		function.help();
	}
}


/**
 * Every command class should implement this and override the issue() function to carry out a command
 * @author TranquilMarmot
 *
 */
interface Command{
	/**
	 * This should issue the command for the class implementing this interface
	 * @param toker This will contain the rest of the command, excluding the command itself, separated by spaces
	 */
	public void issue(StringTokenizer toker);
	
	
	/**
	 * This should print out any help info about the command to the console
	 */
	public void help();
}

class HelpCommand implements Command{
	@Override
	public void issue(StringTokenizer toker){
		if(toker.hasMoreElements()){
			ConsoleCommands command = ConsoleCommands.valueOf(toker.nextToken());
			Console.console.print("HELP for " + command + ":");
			command.help();
		} else{
			Console.console.print("AVAILABLE COMMANDS:");
			Console.console.print("(use /help COMMAND or /COMMAND help for more details)");
			for(ConsoleCommands command : ConsoleCommands.values())
				Console.console.print(command.name());
		}
	}
	
	@Override
	public void help(){
		Console.console.print("Usage: /help command (leave command blank to get a list of commands)");
	}
}

/**
 * Clear the console
 */
class ClearCommand implements Command{
	@Override
	public void issue(StringTokenizer toker) {
		Console.console.clear();
	}
	
	@Override
	public void help(){
		Console.console.print("Clears the console");
	}
}

/**
 * Change the speed of the player
 */
class SpeedCommand implements Command{
	@Override
	public void issue(StringTokenizer toker) {
		Console.console.print("Speed commands temporarily unavailable");
		
		//String speedCommand = toker.nextToken().toLowerCase();
		//Float speedChange = Float.parseFloat(toker.nextToken());
		
		/*
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
		*/
	}
	
	@Override
	public void help(){
		Console.console.print("Speed commands temporarily broken!");
	}
}

//FIXME does not work!
/**
 * Change the player's position
 */
class PositionCommand implements Command{
	@Override
	public void issue(StringTokenizer toker) {
		float x = Float.parseFloat(toker.nextToken());
		float y = Float.parseFloat(toker.nextToken());
		float z = Float.parseFloat(toker.nextToken());

		float oldX = Entities.player.location.x;
		float oldY = Entities.player.location.y;
		float oldZ = Entities.player.location.z;

		Console.console.print("Moving player to x: " + x + " y: " + y + " z: " + z
				+ " from x: " + oldX + " y: " + oldY + " z: " + oldZ);
		
		Transform trans = new Transform();
		Entities.player.rigidBody.getWorldTransform(trans);
		
		trans.transform(new javax.vecmath.Vector3f(x, y, z));
		
		Entities.player.rigidBody.setMotionState(new DefaultMotionState(trans));

		Entities.player.location.x = x;
		Entities.player.location.y = y;
		Entities.player.location.z = z;
	}
	
	@Override
	public void help(){
		Console.console.print("Position commands temporarily broken!");
	}
}

/**
 * List dynamic entitites, static entities, or lights
 */
class ListCommand implements Command{
	@Override
	public void issue(StringTokenizer toker){
		String which = toker.nextToken();
		
		if(which.equals("dynamic")){
			Console.console.print("Listing dynamic entities...");
			for (DynamicEntity ent : Entities.dynamicEntities.values())
				Console.console.print(ent.type);
		} else if(which.equals("static")){
			Console.console.print("Listing static entities...");
			for(Entity ent : Entities.passiveEntities.values())
				Console.console.print(ent.type);
		} else if(which.equals("lights") || which.equals("light")){
			Console.console.print("Listing lights...");
			for(Light l : Entities.lights.values())
				Console.console.print(l.type + "; using light at " + l.location.x + " " + l.location.y + " " + l.location.z);
		} else{
			Console.console.print("List command not recognized! (" + which + ")");
		}
	}
	
	@Override
	public void help(){
		Console.console.print("Usage: /list TYPE");
		Console.console.print("Where TYPE is one of: dynamic, static, lights");
	}
	
}

/**
 * Print out how many entities there are
 */
class NumberCommand implements Command{
	@Override
	public void issue(StringTokenizer toker){
        Console.console.print("Number of static entities: "
                + Entities.passiveEntities.size());
        Console.console.print("Number of dynamic entities: "
                + Entities.dynamicEntities.size());
        Console.console.print("Number of lights: "
                + Entities.lights.size());
	}
	
	@Override
	public void help(){
		Console.console.print("Lists the number of entities");
	}
}

class BeerCommand implements Command{
	@Override
	public void issue(StringTokenizer toker){
		 int i = 99;
         //Timer beerTimer = new Timer();
        do {
                 //if (beerTimer.getTime() > 0.1f) {
                         Console.console.print(i + " bottles of beer on the wall, " + i
                                         + " bottles of beer");
                         Console.console.print("Take one down, pass it around, "
                                         + (i - 1) + " bottles of beer on the wall");
                         i--;
                         //beerTimer.reset();
                 //}
                 //Timer.tick();
                 //System.out.println(beerTimer.getTime());
                 //System.out.println(i);
         } while(i > 0);
	}
	
	@Override
	public void help(){
		Console.console.print("Computers are much faster at counting than you are!");
	}
}

/**
 * Change values for the camera-
 * zoom - how zoomed in/out the camera is
 * yoffset - camera offset on y axis
 * xoffset - camera offset on x axis
 * follow - tell the camera to follow an entity, either the player or the name of an entity type from Entities.dynamicEntities
 */
class CameraCommand implements Command{
	@Override
	public void issue(StringTokenizer toker){
		String cameraCommand = toker.nextToken().toLowerCase();

		// zoom
		if (cameraCommand.equals("zoom")) {
			float zoom = Float.parseFloat(toker.nextToken());
			Console.console.print("Changing camera zoom from " + Entities.camera.zoom
					+ " to " + zoom);
			Entities.camera.zoom = zoom;
		}

		// y offset
		else if (cameraCommand.equals("yoffset")) {
			float yOffset = Float.parseFloat(toker.nextToken());
			Console.console.print("Changing camera yOffset from "
					+ Entities.camera.yOffset + " to " + yOffset);
			Entities.camera.yOffset = yOffset;
		}

		// x offset
		else if (cameraCommand.equals("xoffset")) {
			float xOffset = Float.parseFloat(toker.nextToken());
			Console.console.print("Changing camera xOffset from "
					+ Entities.camera.xOffset + " to " + xOffset);
			Entities.camera.xOffset = xOffset;
		}

		// following
		else if (cameraCommand.equals("follow")) {
			boolean changed = false;
			String toFollow = toker.nextToken();

			if (toFollow.toLowerCase().equals("player")) {
				Console.console.print("Camera now following " + toFollow);
				Entities.camera.following = Entities.player;
				changed = true;
			} else {
				for (DynamicEntity ent : Entities.dynamicEntities.values()) {
					if (ent.type.toLowerCase().equals(toFollow.toLowerCase())) {
						Console.console.print("Camera now following " + toFollow);
						Entities.camera.following = ent;
						changed = true;
						break;
					}
				}
				
				if(!changed){
					for (Entity ent : Entities.passiveEntities.values()) {
						if (ent.type.toLowerCase().equals(toFollow.toLowerCase())) {
							Console.console.print("Camera now following " + toFollow);
							Entities.camera.following = ent;
							changed = true;
							break;
						}
					}
				}
			}
			
			if (!changed) {
				Console.console.print("Couldn't find entity " + toFollow);
			}
		}

		// invalid
		else {
			Console.console.print("Invalid camera command! (" + cameraCommand + ")");
		}
	}
	
	@Override
	public void help(){
		Console.console.print("Usage: /camera COMMAND NEWVALUE");
		Console.console.print("Possible commands:");
		Console.console.print("zoom - Zooms the camera to a given number");
		Console.console.print("xoffset - Change the camera's alignment on the x axis");
		Console.console.print("yoffset - Change the camera's alignment on the y axis");
		Console.console.print("follow - Finds the first entity it can and follows it (entity must be player or from DynamicEntities)");
	}
}

/**
 * Quit the game
 */
class QuitCommand implements Command{
	@Override
	public void issue(StringTokenizer toker){
		Runner.done = true;
	}
	
	@Override
	public void help(){
		Console.console.print("Quit the game... instantly");
	}
}

//FIXME does not work
/**
 * Warp the player to the given entity
 */
class WarpCommand implements Command{
	@Override
	public void issue(StringTokenizer toker){
		boolean hasWarped = false;
		String warp = toker.nextToken();
		for (Entity ent : Entities.dynamicEntities.values()){
			if (ent.type.toLowerCase().equals(warp.toLowerCase())) {
				Console.console.print("Warping Player to " + ent.type + " ("
						+ ent.location.x + "," + ent.location.y + ","
						+ ent.location.z + ")");
				Transform playerTransform = new Transform();
				Entities.player.rigidBody.getWorldTransform(playerTransform);
				Vector3f moveAmount = new Vector3f(0.0f, 0.0f, -10.0f);
				Quat4f playerRot = new Quat4f();
				playerTransform.getRotation(playerRot);
				Vector3f rotated = QuaternionHelper.rotateVectorByQuaternion(moveAmount, new Quaternion(playerRot.x, playerRot.y, playerRot.z, playerRot.w));
				Vector3f newLocation = new Vector3f();
				Vector3f.add(new Vector3f(ent.location.x, ent.location.y, ent.location.z), rotated, newLocation);
				
				playerTransform.transform(new javax.vecmath.Vector3f(newLocation.x, newLocation.y, newLocation.z));
				
				Entities.player.rigidBody.setWorldTransform(playerTransform);
				hasWarped = true;
				break;
			}
		}
		
		if(!hasWarped){
			Console.console.print("Couldn't find entity " + warp);
		}
	}
	
	@Override
	public void help(){
		Console.console.print("Warp command temporarily broken :(");
	}
}
