package util.xml;

import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import util.debug.Debug;
import util.helper.QuaternionHelper;
import util.manager.TextureManager;
import entities.Entity;
import entities.Skybox;
import entities.celestial.Planet;
import entities.celestial.Sun;
import entities.particles.Debris;
import entities.player.Player;

/**
 * Loads entities from an XML file and puts them into the ArrayList Entities.entities
 * @author TranquilMarmot
 * @see Entities
 *
 */
public class XMLParser {
	/**
	 * Loads entities from an XML file
	 * @param file The file to load
	 */
	public static void loadEntitiesFromXmlFile(String file) {
		// list of all the nodes
		NodeList nodes = null;
		// the player node (we need to grab the player before any other entities)
		NodeList playerNode = null;

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
			// grab the player node
			playerNode = docEle.getElementsByTagName("Player");
			// grab all the other nodes
			nodes = docEle.getChildNodes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		 * Grab the player
		 * We need the player to create lots of entities and get everything started
		 * FIXME make it so this isn't necessary somehow
		 */
		if(entities.Entities.player == null && playerNode != null && playerNode.getLength() == 1){
			Element ele = (Element) playerNode.item(0);
			Player p = new Player();

			p.location.x = getFloat(ele, "x");
			p.location.y = getFloat(ele, "y");
			p.location.z = getFloat(ele, "z");

			float yaw = getFloat(ele, "yaw");
			float pitch = getFloat(ele, "pitch");
			float roll = getFloat(ele, "roll");
			
			p.rotation = QuaternionHelper.getQuaternionFromAngles(yaw, pitch, roll);
			
			//entities.Entities.player = p;
		} else{
			if(entities.Entities.player != null)
				Debug.console.print("Player already exists!");
			else if(playerNode == null)
				Debug.console.print("Couldn't find player in XML file " + file + "! Everything is going to go to hell now... Enjoy!");
			else if(playerNode.getLength() != 1)
				Debug.console.print("Found too many or not enough Players in XML file " + file);
		}

		/*
		 * Grab the rest of the entities in the file
		 */
		if (nodes != null && nodes.getLength() > 0) {
			// loop through all the nodes
			for (int i = 0; i < nodes.getLength(); i++) {
				/*
				 *  any node with the name #text is, well, text so we skip it
				 *  we also skip the player's node becayse we already grabbed it
				 */
				if (!nodes.item(i).getNodeName().equals("#text") && !nodes.item(i).getNodeName().equals("Player")) {
					// grab the element
					Element ele = (Element) nodes.item(i);

					// get the entity
					Entity ent = getEntity(ele);

					// if the entity exists, put it into the ArrayList
					if (ent != null)
						entities.Entities.entities.add(ent);
					else
						Debug.console.print("Null entity! " + nodes.item(i).getNodeName());
				}
			}
		} else {
			Debug.console
					.print("Error in XMLParser! Either there was nothing in the given file ("
							+ file + ") or the parser simply just didn't want to work");
		}
	}

	/**
	 * Gets and entity from a given XML element
	 * @param ele The element to create the entity from
	 * @return An entity representing the element
	 */
	private static Entity getEntity(Element ele) {
		String type = ele.getNodeName().toLowerCase();

		if(type.equals("debris")){
			int numStars = getInt(ele, "numStars");
			float range = getFloat(ele, "range");
			long seed = 1337420L;
			return new Debris((Entity)entities.Entities.player, numStars, range, seed);
		} else if(type.equals("skybox")){
			float yaw = getFloat(ele, "yaw");
			float pitch = getFloat(ele, "pitch");
			float roll = getFloat(ele, "roll");
			
			return new Skybox((Entity)entities.Entities.player, pitch, yaw, roll);
		} else if(type.equals("sun")){
			float x = getFloat(ele, "x");
			float y = getFloat(ele, "y");
			float z = getFloat(ele, "z");
			float size = getFloat(ele, "size");
			
			float[] color = getColor(ele, "color");
			float[] lightAmbient = getColor(ele, "lightAmbient");
			float[] lightDiffuse = getColor(ele, "lightDiffuse");
			
			return new Sun(new Vector3f(x, y, z), size, GL11.GL_LIGHT1, color, lightAmbient, lightDiffuse);
		} else if(type.equals("planet")){
			int texture = 0;
			String name = getString(ele, "name");
			if(name.equals("Earth"))
				texture = TextureManager.EARTH;
			else if(name.equals("Mercury"))
				texture = TextureManager.MERCURY;
			else if(name.equals("Venus"))
				texture = TextureManager.VENUS;
			else if(name.equals("Mars"))
				texture = TextureManager.MARS;
			else
				System.out.println("Error! Didn't find texture while creating Planet "+ name + " in XMLParser!");
			
			float x = getFloat(ele, "x");
			float y = getFloat(ele, "y");
			float z = getFloat(ele, "z");
			
			float yaw = getFloat(ele, "yaw");
			float pitch = getFloat(ele, "pitch");
			float roll = getFloat(ele, "roll");
			
			float dxrot = getFloat(ele, "dxrot");
			float dyrot = getFloat(ele, "dyrot");
			float dzrot = getFloat(ele, "dzrot");
			
			float size = getFloat(ele, "size");
			
			Planet p = new Planet(x, y, z, texture, name);
			p.rotation = QuaternionHelper.getQuaternionFromAngles(pitch, yaw, roll);
			
			p.dxrot = dxrot;
			p.dyrot = dyrot;
			p.dzrot = dzrot;
			
			p.size = size;
			
			return p;
		}
		return null;
	}
	
	/**
	 * Returns a float arraty of 3 values given a string formatted like "1.0f,1.0f,1.0f" or similar three numbers
	 * @param ele The element to get the color array from
	 * @param tag	The tag to get the color from
	 * @return A float array containing the color
	 */
	private static float[] getColor(Element ele, String tag){
		String text = getString(ele, tag);
		StringTokenizer toker = new StringTokenizer(text, ",");
		
		float r = Float.parseFloat(toker.nextToken());
		float g = Float.parseFloat(toker.nextToken());
		float b = Float.parseFloat(toker.nextToken());
		
		return new float[]{ r, g, b};
	}

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
