package launcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileOps {
	/**
	 * Downloads a file over HTTP
	 * @param server The server to connect to
	 * @param filePath Path of file on server
	 * @param destinationPath Where to download the file to
	 */
	public static void downloadFile(String server, String filePath, String destinationPath){
		try{
			// Open up an input stream from the file server
			URL url = new URL("http://" + server + filePath);
			URLConnection con = url.openConnection();
			BufferedInputStream in = new BufferedInputStream(con.getInputStream());
			
			println("Downloading " + filePath + " to " + destinationPath + "... (" + con.getContentLength() + " bytes)");
			
			// TODO have this print out dots every time it gets so far in downloading the file
			
			FileOutputStream out = new FileOutputStream(destinationPath);
			
			// Fill file with bytes from server
			int i = 0;
			byte[] bytesIn = new byte[1024];
			while((i = in.read(bytesIn)) >= 0){
				out.write(bytesIn, 0, i);
			}
			
			// don't cross the streams!
			out.close();
			in.close();
			
			println(filePath + " downloaded to " + destinationPath);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a directory at the given path
	 * @param directoryPath Directory to create
	 */
	public static void createDirectory(String directoryPath){
		File dir = new File(directoryPath);
		if(!dir.exists()){
			boolean succ = dir.mkdir();
			
			if(succ)
				println("Created directory " + directoryPath);
			else
				println("Error creating directory " + directoryPath + "!");
		}
	}
	
	/**
	 * Extracts a zip file to a given destination
	 * @param zipPath Path to .zip file
	 * @param destinationPath Path to extract to
	 */
	public static void extractZip(String zipPath, String destinationPath){
		try{
			FileInputStream fis = new FileInputStream(zipPath);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			
			final int BUFFER = 512;
			
			ZipEntry ent;
			while((ent = zis.getNextEntry()) != null){
				if(ent.isDirectory()){
					FileOps.createDirectory(Launcher.getPath(destinationPath + "/" + ent.getName()));
				} else{
					println("Extracting " + ent.getName() + " to " + destinationPath);
					
					// stream to write file to
					FileOutputStream fos = new FileOutputStream(Launcher.getPath(destinationPath + "/" + ent.getName()));
					BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER); 
					
					// write the data
					int count;
					byte[] data = new byte[BUFFER];
					
					while((count = zis.read(data, 0, BUFFER)) != -1){
						dest.write(data, 0, count);
					}
					
					// shake the extra drops out and flush
					dest.flush();
					dest.close();
				}
			}
			
			fis.close();
			zis.close();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Delete a file
	 * @param path File to delete
	 */
	public static void deleteFile(String path){
		File toDel = new File(path);
		
		boolean succ = toDel.delete();
		
		if(succ)
			println("Deleted " + path);
		else
			println("Failed to delete " + path + "!");
	}
	
	/**
	 * @param s String to print to info console
	 */
	private static void println(String s){
		Display.println(s);
	}
}
