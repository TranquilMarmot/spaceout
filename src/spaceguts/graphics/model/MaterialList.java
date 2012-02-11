package spaceguts.graphics.model;

import java.util.HashMap;

/**
 * This would contain all the materials loaded in from a .mtl file
 * @author TranquilMarmot
 *
 */
public class MaterialList {
	private HashMap<String, Material> list;
	
	public MaterialList(){
		list = new HashMap<String, Material>();
	}
	
	public void addMaterial(String name, Material mat){
		list.put(name, mat);
	}
	
	public Material getMaterial(String name){
		return list.get(name);
	}
	
	public String toString(){
		String ret = "Material list:\n";
		for(String s : list.keySet()){
			Material mat = list.get(s);
			ret += "Material: " + s + "\n";
			ret += "Shininess: " + mat.getShininess() + "\n"; 
			ret += "Ka: " + mat.getKa().x + " " + mat.getKa().y + " " + mat.getKa().z + "\n";
			ret += "Kd: " + mat.getKd().x + " " + mat.getKd().y + " " + mat.getKd().z + "\n";
			ret += "Ks: " + mat.getKs().x + " " + mat.getKs().y + " " + mat.getKs().z + "\n";
			ret += "\n";
		}
		
		return ret;
	}
}
