package launcher;

import java.io.File;
import java.io.IOException;

public class Launcher {
	/** Directory to use for downloading and extracting file */
	private static String homeDir;
	
	private static boolean isWindows = false;
	
	/**
	 * @param args
	 * 		If no args are given, then System.getProperty("user.home") is used as the spot to download/extract from.
	 * 		Else one arg should be given with the home directory to use, trailing slash included ("/folder/", not "/folder")
	 */
	public static void main(String[] args){
		if(System.getProperty("os.name").toLowerCase().contains("windows"))
			isWindows = true;
		
		if(args.length > 0){
			homeDir = args[0];
		} else{
			homeDir = System.getProperty("user.home") + getPath("/");
		}
		
		Display.createWindow();
		
		Display.println("Welcome to the Spaceout launcher!                            ");
		Display.println("Using " + homeDir + " as home directory");
	}
	
	/**
	 * Downloads .spaceout.zip and natives from the FTP server and extracts them
	 */
	public static void downloadAndExtractFiles(){
		String fileServ = "bitwaffle.com/spaceoutstuff";
		
		String nativesFile = null;
		
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("windows"))
			nativesFile = "windows.zip";
		else if(os.contains("linux"))
			nativesFile = "linux.zip";
		else if(os.contains("mac"))
			nativesFile = "macosx.zip";
		else if(os.contains("solaris"))
			nativesFile = "solaris.zip";
		else
			Display.println("Error! OS not detected! Can't download natives!");
		
		createDirectories();
		
		// download, extract and delete .spaceout.zip
		FileOps.downloadFile(fileServ, "/.spaceout.zip", homeDir + getPath(".spaceout/.spaceout.zip"));
		FileOps.extractZip(homeDir + getPath(".spaceout/.spaceout.zip"), homeDir + getPath(".spaceout"));
		FileOps.deleteFile(homeDir + getPath(".spaceout/.spaceout.zip"));
		
		// download, extract, and delete the native folder
		FileOps.downloadFile(fileServ, "/natives/" + nativesFile, homeDir + getPath(".spaceout/" + nativesFile));
		FileOps.extractZip(homeDir + getPath(".spaceout/" + nativesFile), homeDir + getPath(".spaceout/lib/natives"));
		FileOps.deleteFile(homeDir + getPath(".spaceout/" + nativesFile));
		
		Display.println("Done");
		Display.println("Ready to play!");
	}
	
	/**
	 * Creates the base directories for extracting the .zip files to
	 */
	private static void createDirectories(){
		FileOps.createDirectory(homeDir + getPath(".spaceout"));
		FileOps.createDirectory(homeDir + getPath(".spaceout/lib"));
		FileOps.createDirectory(homeDir + getPath(".spaceout/res"));
		FileOps.createDirectory(homeDir + getPath(".spaceout/screenshots"));
		FileOps.createDirectory(homeDir + getPath(".spaceout/lib/natives"));
	}
	
	/**
	 * Runs "java -jar spaceout.jar" inside of .spaceout inside of the given home directory.
	 * At this point, it's assumed that .spaceout exists.
	 */
	public static void launchGame(){
		String directory = homeDir + getPath(".spaceout/");
		
		// so we can execute the command in the right spot
		File file = new File(directory);
		
		try {
			Runtime.getRuntime().exec("java -jar spaceout.jar", null, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts all / in a string to \\ (for windows)
	 * @param path Path to convert
	 * @return Path corrected to work with current OS
	 */
	public static String getPath(String path){
		if(isWindows)
			return path.replace('/', '\\');
		else
			return path;
	}
}
