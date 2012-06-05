package com.bitwaffle.spaceguts.util.xml;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class SaveLister {
	
	public static final String SAVE_FOLDER_LOCATION = "./saves";
	
	public ArrayList<String> saveList;

	public SaveLister() {
		saveList = this.buildList();
	}
	
	public ArrayList<String> getList() {
		return saveList;
	}
	
	private ArrayList<String> buildList() {
		
		File saveFolder = new File(SAVE_FOLDER_LOCATION);
		File[] fileList = saveFolder.listFiles();
		String file;
		
		ArrayList<String> sList = new ArrayList<String>();
		
		for (int i = 0; i < fileList.length; i++) {
			file = fileList[i].getName();
			String saveName;
			if (file.endsWith(".xml")) {
				
				try {
					
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(new File(fileList[i].getAbsolutePath()));
					doc.getDocumentElement().normalize();
					
					saveName = doc.getElementsByTagName("save").item(0).getAttributes().getNamedItem("name").getNodeValue();
					
					sList.add(saveName);
					
				} catch (Exception e) {
					System.out.println("FTTZZZ... (SaveLister)");
					e.printStackTrace();
				}
			}
		}
		return sList;
	}
}
