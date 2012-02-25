package spaceguts.util.xml;

import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import spaceguts.entities.Camera;
import spaceguts.entities.DynamicEntity;
import spaceguts.entities.Entities;
import spaceguts.util.console.Console;
import spaceout.entities.dynamic.Planet;
import spaceout.entities.dynamic.Player;
import spaceout.entities.passive.Skybox;
import spaceout.entities.passive.Sun;
import spaceout.entities.passive.particles.Debris;
import spaceout.resources.Models;
import spaceout.resources.Textures;
import spaceout.ship.Ship;

/**
 * Loads entities from an XML file and puts them into the ArrayList
 * Entities.entities
 * 
 * @author TranquilMarmot
 * @see Entities
 * 
 */
public class XMLParser {
	/**
	 * Loads entities from an XML file
	 * 
	 * @param file
	 *            The file to load
	 */
	public static void loadEntitiesFromXmlFile(String file) {
		// list of all the nodes
		NodeList nodes = null;

		// create a new DocumentBuilderFactory to read the XML file
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// The document builder
		DocumentBuilder db;
		// The actual document
		Document doc;
		try {
			// create a new document builder from the factory
			db = dbf.newDocumentBuilder();
			// tell the document builder to parse the file
			doc = db.parse(file);
			// create an element from the document
			Element docEle = doc.getDocumentElement();
			// grab all the other nodes
			nodes = docEle.getChildNodes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Grab the rest of the entities in the file
		 */
		if (nodes != null && nodes.getLength() > 0) {
			Camera.createCamera();

			Entities.skybox = new Skybox(Entities.camera);

			// loop through all the nodes
			for (int i = 0; i < nodes.getLength(); i++) {
				/*
				 * any node with the name #text is, well, text so we skip it we
				 * also skip the player's node becayse we already grabbed it
				 */
				if (!nodes.item(i).getNodeName().equals("#text")) {
					// grab the element
					Element ele = (Element) nodes.item(i);

					// get the entity
					makeEntity(ele);
				}
			}
			if (Entities.player != null) {
				Entities.camera.following = Entities.player;
				Entities.camera.freeMode = false;
			}
		} else {
			Console.console
					.print("Error in XMLParser! Either there was nothing in the given file ("
							+ file
							+ ") or the parser simply just didn't want to work");
		}
	}

	/**
	 * Gets and entity from a given XML element
	 * 
	 * @param ele
	 *            The element to create the entity from
	 * @return An entity representing the element
	 */
	private static void makeEntity(Element ele) {
		String type = ele.getNodeName().toLowerCase();

		if (type.equals("player")) {
			makePlayer(ele);
		} else if (type.equals("debris")) {
			makeDebris(ele);
		} else if (type.equals("sun")) {
			makeSun(ele);
		} else if (type.equals("planet")) {
			makePlanet(ele);
		} else if (type.equals("saucer")) {
			makeSaucer(ele);
		}
	}

	private static void makeSaucer(Element ele) {
		Vector3f location = getVector3f(ele, "location");
		Quaternion rotation = getRotation(ele);
		float mass = getFloat(ele, "mass");
		float restitution = getFloat(ele, "restitution");

		DynamicEntity saucer = new DynamicEntity(location, rotation,
				Models.SAUCER, mass, restitution);
		saucer.type = "Saucer";
		
		Entities.addDynamicEntity(saucer);
	}

	private static void makePlayer(Element ele) {
		Vector3f location = getVector3f(ele, "location");
		Quaternion rotation = getRotation(ele);
		float mass = getFloat(ele, "mass");
		float restitution = getFloat(ele, "restitution");
		
		/* TEMP SHIP INFO TODO make this load from XML */
		String shipName = "WingX";
		Models shipModel = Models.SAUCER;
		int shipHealth = 100;
		float shipMass = 50.0f;
		float shipRestitution = 0.01f;
		Vector3f shipAcceleration = new Vector3f(1000.0f, 1000.0f, 1000.0f);
		float shipTopSpeed = 200.0f;
		float shipStabilizationSpeed = 0.5f;
		float shipStopSpeed = 1.0f;
		float shipRollSpeed = 0.5f;
		float shipXTurnSpeed = 0.0025f;
		float shipYTurnSpeed = 0.0025f;
		
		Ship ship = new Ship(shipName, shipModel, shipHealth, shipMass, shipRestitution, shipAcceleration, shipTopSpeed, shipStabilizationSpeed, shipStopSpeed, shipRollSpeed, shipXTurnSpeed, shipYTurnSpeed);
		
		Player player = new Player(location, rotation, ship,
				mass, restitution);
		player.type = "dynamicPlayer";

		Entities.player = player;
	}

	private static void makeDebris(Element ele) {
		int numStars = getInt(ele, "numStars");
		float range = getFloat(ele, "range");
		long seed = 1337420L;
		Debris debris = new Debris(Entities.camera, numStars,
				range, seed);
		
		Entities.addPassiveEntity(debris);
	}

	private static void makeSun(Element ele) {
		Vector3f location = getVector3f(ele, "location");
		float size = getFloat(ele, "size");
		Vector3f intensity = getVector3f(ele, "intensity");

		Sun sun = new Sun(location, size,
				intensity);
		
		Entities.addLight(sun);
	}

	private static void makePlanet(Element ele) {
		Textures texture = Textures.CHECKERS;
		String name = getString(ele, "name");
		if (name.equals("Earth"))
			texture = Textures.EARTH;
		else if (name.equals("Mercury"))
			texture = Textures.MERCURY;
		else if (name.equals("Venus"))
			texture = Textures.VENUS;
		else if (name.equals("Mars"))
			texture = Textures.MARS;
		else
			System.out
					.println("Error! Didn't find texture while creating Planet "
							+ name + " in XMLParser!");

		Vector3f location = getVector3f(ele, "location");
		Quaternion rotation = getRotation(ele);
		float mass = getFloat(ele, "mass");
		float size = getFloat(ele, "size");
		float restitution = getFloat(ele, "restitution");

		Planet p = new Planet(location, rotation, size, mass, restitution,
				texture);
		p.type = name;
		
		Entities.addDynamicEntity(p);
	}

	/**
	 * Gets a vector3f representing a location
	 * 
	 * @param ele
	 *            The element to get the location from
	 * @return A vector representing he location
	 */
	private static Vector3f getVector3f(Element ele, String tag) {
		String loc = getString(ele, tag);
		StringTokenizer toker = new StringTokenizer(loc, ",");
		float x = Float.parseFloat(toker.nextToken());
		float y = Float.parseFloat(toker.nextToken());
		float z = Float.parseFloat(toker.nextToken());
		return new Vector3f(x, y, z);
	}

	/**
	 * Gets a quaternion representing a rotation
	 * 
	 * @param ele
	 *            The element to get the rotation from
	 * @return A quaternion representing the rotation
	 */
	private static Quaternion getRotation(Element ele) {
		String rot = getString(ele, "rotation");
		StringTokenizer toker = new StringTokenizer(rot, ",");
		float x = Float.parseFloat(toker.nextToken());
		float y = Float.parseFloat(toker.nextToken());
		float z = Float.parseFloat(toker.nextToken());
		float w = Float.parseFloat(toker.nextToken());
		return new Quaternion(x, y, z, w);
	}

	/**
	 * Returns a float array of 3 values given a string formatted like
	 * "1.0f,1.0f,1.0f" or similar three numbers
	 * 
	 * @param ele
	 *            The element to get the color array from
	 * @param tag
	 *            The tag to get the color from
	 * @return A float array containing the color
	 */
	/*
	private static Vector3f getColor(Element ele, String tag) {
		String text = getString(ele, tag);
		StringTokenizer toker = new StringTokenizer(text, ",");

		float r = Float.parseFloat(toker.nextToken());
		float g = Float.parseFloat(toker.nextToken());
		float b = Float.parseFloat(toker.nextToken());

		return new Vector3f(r, g, b);
	}
	*/

	/**
	 * Gets a string from an element
	 * 
	 * @param ele
	 *            The element to get the string from
	 * @param tagName
	 *            The tag to get the string from
	 * @return The string from the element
	 */
	private static String getString(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	/**
	 * Gets a boolean from an element
	 * 
	 * @param ele
	 *            The element to get the boolean from
	 * @param tagName
	 *            The tag to get the boolean from
	 * @return The boolean from the element
	 */
	@SuppressWarnings("unused")
	private static boolean getBoolean(Element ele, String tagName) {
		return Boolean.parseBoolean(getString(ele, tagName));
	}

	/**
	 * Gets an int from an element
	 * 
	 * @param ele
	 *            The element to get the int from
	 * @param tagName
	 *            The tag to get the int from
	 * @return The int from the element
	 */
	private static int getInt(Element ele, String tagName) {
		return Integer.parseInt(getString(ele, tagName));
	}

	private static float getFloat(Element ele, String tagName) {
		return Float.parseFloat(getString(ele, tagName));
	}
}
