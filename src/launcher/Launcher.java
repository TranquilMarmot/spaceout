package launcher;

import java.io.File;
import java.io.IOException;

public class Launcher {
	/** Directory to use for downloading and extracting file */
	private static String homeDir;
	
	/**
	 * @param args
	 * 		If no args are given, then System.getProperty("user.home") is used as the spot to download/extract from.
	 * 		Else one arg should be given with the home directory to use, trailing slash included ("/folder/", not "/folder")
	 */
	public static void main(String[] args){
		if(args.length > 0){
			homeDir = args[0];
		} else{
			homeDir = System.getProperty("user.home") + "/";
		}
		
		Display.createWindow();
		
		System.out.println("Welcome to the Spaceout launcher!                            ");
		System.out.println("Using " + homeDir + " as home directory");
	}
	
	/**
	 * @return Whether or not .spaceout and spaceout.jar exist
	 */
	public static boolean filesExist(){
		File dotspout = new File(homeDir + "/.spaceout");
		File spout = new File(homeDir + "/.spaceout/spaceout.jar");
		
		return dotspout.exists() && spout.exists();
	}
	
	/**
	 * @return Total file size for everything that needs to be downloaded
	 */
	public static int totalFileSize(){
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
			System.out.println("Error! OS not detected! Can't download natives!");
		
		return FileOps.fileSizeOnServer(fileServ, "/.spaceout.zip") + FileOps.fileSizeOnServer(fileServ, "/natives/" + nativesFile);
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
			System.out.println("Error! OS not detected! Can't download natives!");
		
		createDirectories();
		
		// download, extract and delete .spaceout.zip
		FileOps.downloadFile(fileServ, "/.spaceout.zip", homeDir + ".spaceout/.spaceout.zip");
		FileOps.extractZip(homeDir + ".spaceout/.spaceout.zip", homeDir + ".spaceout");
		FileOps.deleteFile(homeDir + ".spaceout/.spaceout.zip");
		
		// download, extract, and delete the native folder
		FileOps.downloadFile(fileServ, "/natives/" + nativesFile, homeDir + ".spaceout/" + nativesFile);
		FileOps.extractZip(homeDir + ".spaceout/" + nativesFile, homeDir + ".spaceout/lib/natives");
		FileOps.deleteFile(homeDir + ".spaceout/" + nativesFile);
		
		System.out.println("Done");
		System.out.println("Ready to play!");
	}
	
	/**
	 * Creates the base directories for extracting the .zip files to
	 */
	private static void createDirectories(){
		FileOps.createDirectory(homeDir + ".spaceout");
		FileOps.createDirectory(homeDir + ".spaceout/lib");
		FileOps.createDirectory(homeDir + ".spaceout/res");
		FileOps.createDirectory(homeDir + ".spaceout/screenshots");
		FileOps.createDirectory(homeDir + ".spaceout/lib/natives");
	}
	
	/**
	 * Runs "java -jar spaceout.jar" inside of .spaceout inside of the given home directory.
	 * At this point, it's assumed that .spaceout exists.
	 */
	public static void launchGame(){
		String directory = homeDir + ".spaceout/";
		
		// so we can execute the command in the right spot
		File file = new File(directory);
		
		try {
			Runtime.getRuntime().exec("java -jar spaceout.jar", null, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getHomeDir(){
		return homeDir;
	}
}
