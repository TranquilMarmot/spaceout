package launcher;

import java.io.File;
import java.io.IOException;

public class Launcher {
	/** Directory to use for downloading and extracting file */
	private static String homeDir;
	
	public static final String FILE_SERVER = "bitwaffle.com/spaceoutstuff";
	
	/** version string in local directory, version string from server*/
	public static String localVersion, serverVersion;
	
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
				return downloadAndCompareVersions();
			}
		}
	}
	
	/**
	 * Sets the localVersion to the string from the version file
	 * Downloads the version file from the server and sets the serverVersion string
	 * @return Whether or not the two version strings are the same
	 */
	private static boolean downloadAndCompareVersions(){
		File localversion = new File(homeDir + "/.spaceout/version");
		localVersion = FileOps.getFirstLineFromFile(localversion);
		 
		 // nothing was in the file!
		 if(localVersion == null && !localVersion.equals("0"))
			 return true;
		 else{
			 FileOps.downloadFile(FILE_SERVER, "/version", homeDir + ".spaceout/servversion");
			 
			 File servVersion = new File(homeDir + "/.spaceout/servversion");
			 serverVersion = FileOps.getFirstLineFromFile(servVersion);
			 FileOps.deleteFile(homeDir + ".spaceout/servversion");
			 
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
