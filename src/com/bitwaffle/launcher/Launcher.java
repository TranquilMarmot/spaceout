package com.bitwaffle.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

public class Launcher {
	/** Directory to use for downloading and extracting file */
	public static String homeDir;
	
	/** where we downloadin these files from? */
	public static final String FILE_SERVER = "bitwaffle.com/spaceoutstuff";
	
	/** version string in local directory, version string from server*/
	public static String localVersion, serverVersion;
	
	public static boolean serverFound = true;
	
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
		
		System.out.println("Welcome to the Spaceout launcher!                            ");
		System.out.println("Using " + homeDir + " as home directory");
		
		// get version strings
		getVersions();
		
		// everything is handled through the window
		Display.createWindow();
	}
	
	/**
	 * @return Whether or not .spaceout and spaceout.jar exist
	 */
	public static boolean filesExist(){
		// TODO this should somehow check if ALL the files exist somehow (maybe check file sizes against something?)
		File dotspout = new File(homeDir + "/.spaceout");
		File spoutjar = new File(homeDir + "/.spaceout/spaceout.jar");
		
		return dotspout.exists() && spoutjar.exists();
	}
	
	/**
	 * @return Whether or not updated files need to be downloaded
	 */
	public static boolean updateRequired(){
		// don't check if the checkbox is deselected
		if(!Display.checkForUpdate.isSelected()){
			return false;
		} else{
			// if we already have the version strings, just compare them
			if(localVersion != null && serverVersion != null){
				 if(!localVersion.equals(serverVersion))
					 return true;
				 else
					 return false;
			}
			else{
				return compareVersions();
			}
		}
	}
	
	/**
	 * Gets the local version from HOME/.spaceout/version
	 * and the server version from FILE_SERVER/version
	 */
	private static void getVersions(){
		try{
			File localversion = new File(homeDir + "/.spaceout/version");
			localVersion = FileOps.getFirstLineFromFile(localversion);
		} catch(FileNotFoundException e){
			// if there's no version file, we don't know the version
			localVersion = "???";
		}
		System.out.println("Local version is " + localVersion);
		
		try{
			serverVersion = FileOps.getFirstLineFromURL("http://" + FILE_SERVER + "/version");
		} catch(UnknownHostException e){
			serverFound = false;
			serverVersion = "(error connecting to server)";
		}
		System.out.println("Server version is " + serverVersion);
	}
	
	/**
	 * @return Whether or not the versions are the same (if they aren't, better update)
	 */
	private static boolean compareVersions(){
		 // nothing was in the file!
		 if(localVersion == null || localVersion.equals("0"))
			 return true;
		 else{
			 if(serverVersion == null)
				 return false;
			 else{
				 if(!localVersion.equals(serverVersion))
					 return true;
				 else
					 return false;
			 }
		}
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
		FileOps.downloadFile(FILE_SERVER, "/.spaceout.zip", homeDir + ".spaceout/.spaceout.zip");
		FileOps.extractZip(homeDir + ".spaceout/.spaceout.zip", homeDir + ".spaceout");
		FileOps.deleteFile(homeDir + ".spaceout/.spaceout.zip");
		
		// download, extract, and delete the native folder
		FileOps.downloadFile(FILE_SERVER, "/natives/" + nativesFile, homeDir + ".spaceout/" + nativesFile);
		FileOps.extractZip(homeDir + ".spaceout/" + nativesFile, homeDir + ".spaceout/lib/natives");
		FileOps.deleteFile(homeDir + ".spaceout/" + nativesFile);
		
		FileOps.downloadFile(FILE_SERVER, "/version", homeDir + ".spaceout/version");
		
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
