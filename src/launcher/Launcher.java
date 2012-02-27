package launcher;

import java.io.File;
import java.io.IOException;

public class Launcher {
	public static void main(String[] args){
		String os = System.getProperty("os.name").toLowerCase();
		String directory = System.getProperty("user.home");
		
		if(os.contains("windows"))
			directory += "\\.spaceout\\";
		else if(os.contains("linux") || os.contains("mac") || os.contains("solaris"))
			directory += "/.spaceout/";
		
		File file = new File(directory);
		
		try {
			Runtime.getRuntime().exec("java -jar spaceout.jar", null, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
