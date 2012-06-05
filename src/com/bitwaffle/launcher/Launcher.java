package com.bitwaffle.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

public class Launcher {
	/** Directory to use for downloading and extracting file (set to user.home by default) */
	public static String workingDir;
	
	/** where we downloadin these files from? */
	public static final String FILE_SERVER = "bitwaffle.com/spaceoutstuff";
	
	/** version string in local directory, version string from server*/
	public static String localVersion, serverVersion;
	
	/** Whether or not the server was found */
	public static boolean serverFound = true;
	
	/**
	 * @param args
	 * 		If no args are given, then System.getProperty("user.home") is used as the spot to download/extract from.
	 * 		Else one arg should be given with the home directory to use, trailing slash included ("/folder/", not "/folder")
	 */
	public static void main(String[] args){
		System.getProperty("system.fileseparator");
		if(args.length > 0){
			workingDir = args[0];
		} else{
			workingDir = System.getProperty("user.home") + "/";
		}
		
		System.out.println("Welcome to the Spaceout launcher!                            ");
		System.out.println("Using " + workingDir + " as home directory");
		
		// get version strings
		getVersions();
		
		// everything is handled through the window
		Display.createWindow();
	}
	
	/**
	 * @return Whether or not .spaceout and spaceout.jar exist
	 */
	public static boolean filesExist(){
		// TODO this should somehow check if ALL the files exist somehow (generate a list of every file)
		File dotspout = new File(workingDir + "/.spaceout");
		File spoutjar = new File(workingDir + "/.spaceout/spaceout.jar");
		
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
	public static void getVersions(){
		localVersion = getLocalVersion();
		System.out.println("Local version is " + localVersion);
		
		serverVersion = getServerVersion();
		System.out.println("Server version is " + serverVersion);
	}
	
	/**
	 * Gets the local version from WORKINGDIR/.spaceout/version
	 * @return Local version string
	 */
	public static String getLocalVersion(){
		String version = "";
		try{
			File localversion = new File(workingDir + "/.spaceout/version");
			version = FileOps.getFirstLineFromFile(localversion);
		} catch(FileNotFoundException e){
			// if there's no version file, we don't know the version
			version = "???";
		}
		return version;
	}
	
	/**
	 * Gets the server version from FILE_SERVER/version
	 * @return Server version string
	 */
	public static String getServerVersion(){
		String version;
		try{
			version = FileOps.getFirstLineFromURL("http://" + FILE_SERVER + "/version");
		} catch(UnknownHostException e){
			serverFound = false;
			version = "(error connecting to server)";
		}
		return version;
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
			nativesFile = "windows.jar";
		else if(os.contains("linux"))
			nativesFile = "linux.jar";
		else if(os.contains("mac"))
			nativesFile = "macosx.jar";
		else if(os.contains("solaris"))
			nativesFile = "solaris.jar";
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
			nativesFile = "windows.jar";
		else if(os.contains("linux"))
			nativesFile = "linux.jar";
		else if(os.contains("mac"))
			nativesFile = "macosx.jar";
		else if(os.contains("solaris"))
			nativesFile = "solaris.jar";
		else
			System.out.println("Error! OS not detected! Can't download natives!");
		
		createDirectories();
		
		// download, extract and delete .spaceout.zip
		FileOps.downloadFile(FILE_SERVER, "/.spaceout.zip", workingDir + ".spaceout/.spaceout.zip");
		FileOps.extractZip(workingDir + ".spaceout/.spaceout.zip", workingDir + ".spaceout");
		FileOps.deleteFile(workingDir + ".spaceout/.spaceout.zip");
		
		// download, extract, and delete the native folder
		FileOps.downloadFile(FILE_SERVER, "/natives/" + nativesFile, workingDir + ".spaceout/" + nativesFile);
		FileOps.extractZip(workingDir + ".spaceout/" + nativesFile, workingDir + ".spaceout/lib/natives");
		FileOps.deleteFile(workingDir + ".spaceout/" + nativesFile);
		
		FileOps.downloadFile(FILE_SERVER, "/version", workingDir + ".spaceout/version");
		
		System.out.println("Done");
		System.out.println("Ready to play!");
	}
	
	/**
	 * Creates the base directories for extracting the .zip files to
	 */
	private static void createDirectories(){
		FileOps.createDirectory(workingDir + ".spaceout");
		FileOps.createDirectory(workingDir + ".spaceout/lib");
		FileOps.createDirectory(workingDir + ".spaceout/res");
		FileOps.createDirectory(workingDir + ".spaceout/screenshots");
		FileOps.createDirectory(workingDir + ".spaceout/lib/natives");
	}
	
	/**
	 * Runs "java -jar spaceout.jar" inside of .spaceout inside of the given home directory.
	 * At this point, it's assumed that .spaceout exists.
	 */
	public static void launchGame(){
		String directory = workingDir + ".spaceout/";
		
		// so we can execute the command in the right spot
		File file = new File(directory);
		
		try {
			Runtime.getRuntime().exec("java -jar spaceout.jar", null, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Current working directory
	 */
	public static String getHomeDir(){
		return workingDir;
	}
}
