package com.bitwaffle.spaceguts.util.xml;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parse menus from XML files into Map<String,String>
 * 
 * @author TranquilMarmot
 * @author arthurdent
 * 
 */
public class MenuParser {
	
	private static final File DEFAULT_MENU_FILE = new File("res/menus/menus.xml");
	
	private File filename;
	private String menuname;
	private Map<String, String> menu;
	
	/**
	 * Construct a menu from a chosen XML formatted menu file
	 * @param fn The filename of the file storing XML formatted menus
	 * @param mn The name of the stored menu tp retrieve
	 */
	public MenuParser(File fn, String mn) {
		filename = fn;
		menuname = mn;
		menu = this.buildMenu();
	}
	
	/**
	 * Construct a menu from the default file containing XML formatted menus
	 * @param mn The name of the stored menu to retrieve
	 */
	public MenuParser(String mn) {
		this(DEFAULT_MENU_FILE, mn);
	}
	
	/**
	 * Retrieve the menu in a Map format for in-game parsing/rendering purposes
	 * @return Map<ButtonName,Command>
	 */
	public Map<String,String> getMenu() {
		return menu;
	}
	
	/**
	 * Print the menu for debugging
	 */
	public void dbg_printMenu() {
		for (Map.Entry<String, String> entry : menu.entrySet()) {
			System.out.println(entry.getKey() 
				+ " : " + entry.getValue());
		}
	}
	
	/**
	 * Creates a map of the menu 
	 * @return a map of the menu containing Map<"Button Name" , "command">
	 */
	private Map<String,String> buildMenu() {
		
		Map<String,String> parsedMenu = new LinkedHashMap<String,String>();
		
		try {
	
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(filename);
			doc.getDocumentElement().normalize();
			
			NodeList menuList = doc.getElementsByTagName("menu");
			
			// Cycle through each menu.
			for (int i = 0; i < menuList.getLength(); i++) {
				
				// Is this the right menu? Get the <menu>'s name attribute
				Node menu = (Node) menuList.item(i);
				String menuName = menu.getAttributes().getNamedItem("name").getNodeValue();
				
				// Get the right menu
				if (menuName.equals(menuname)) {
					
					// A NodeList of each <button /> in the menu
					NodeList buttonList = ((Element) menuList.item(i)).getElementsByTagName("button");
					
					// Get the attributes of each <button /> and place them into a map
					for (int j = 0; j < buttonList.getLength(); j++) {
						String buttonName = buttonList.item(j).getAttributes().getNamedItem("name").getNodeValue();
						String buttonValue = buttonList.item(j).getAttributes().getNamedItem("command").getNodeValue();
						
						// Add the items to the Map
						parsedMenu.put(buttonName,buttonValue);
					}
				
				}
			}
			
		} catch (Exception e) {
			System.out.println("FTTZZZ... (MenuParser)");
			e.printStackTrace();
		}
		// Return the hopefully not empty Map. Either way it's safe.
		return parsedMenu;
	}
}
