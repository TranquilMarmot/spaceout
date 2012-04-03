package com.bitwaffle.launcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
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
			Display.info.setText("Downloading " + filePath + "...");
			// Open up an input stream from the file server
			URL url = new URL("http://" + server + filePath);
			URLConnection con = url.openConnection();
			BufferedInputStream in = new BufferedInputStream(con.getInputStream());
			
			System.out.println("Downloading " + filePath + " to " + destinationPath + "... (" + con.getContentLength() + " bytes)");
			
			FileOutputStream out = new FileOutputStream(destinationPath);
			
			int progress = 0;
			Display.progBar.setMinimum(progress);
			Display.progBar.setMaximum(con.getContentLength());
			
			// Fill file with bytes from server
			int i = 0;
			byte[] bytesIn = new byte[1024];
			while((i = in.read(bytesIn)) >= 0){
				out.write(bytesIn, 0, i);
				progress += bytesIn.length;
				Display.progBar.setValue(progress);
			}
			
			// don't cross the streams!
			out.close();
			in.close();
			
			System.out.println(filePath + " downloaded to " + destinationPath);
			Display.info.setText("");
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static int fileSizeOnServer(String server, String filePath){
		try{
			// Open up an input stream from the file server
			URL url = new URL("http://" + server + filePath);
			URLConnection con = url.openConnection();
			return con.getContentLength();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		return 0;
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
				System.out.println("Created directory " + directoryPath);
			else
				System.out.println("Error creating directory " + directoryPath + "!");
		}
	}
	
	/**
	 * Extracts a zip file to a given destination
	 * @param zipPath Path to .zip file
	 * @param destinationPath Path to extract to
	 */
	public static void extractZip(String zipPath, String destinationPath){
		try{
			Display.info.setText("Extracting " + zipPath + "...");
			FileInputStream fis = new FileInputStream(zipPath);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			final int BUFFER = 512;
			
			ZipEntry ent;
			while((ent = zis.getNextEntry()) != null){
				Display.info.setText("Extracting " + ent.getName() + "...");
				if(ent.isDirectory()){
					FileOps.createDirectory(destinationPath + "/" + ent.getName());
				} else{
					Display.progBar.setMaximum((int) ent.getSize());
					
					int prog = 0;
					Display.progBar.setMinimum(prog);
					
					// stream to write file to
					FileOutputStream fos = new FileOutputStream(destinationPath + "/" + ent.getName());
					BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER); 
					
					// write the data
					int count;
					byte[] data = new byte[BUFFER];
					
					while((count = zis.read(data, 0, BUFFER)) != -1){
						dest.write(data, 0, count);
						prog += data.length;
						Display.progBar.setValue(prog);
					}
					
					// shake the extra drops out and flush
					dest.flush();
					dest.close();
					Display.info.setText("");
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
			System.out.println("Deleted " + path);
		else
			System.out.println("Failed to delete " + path + "!");
	}
	
	/**
	 * @param URL URL of file to grab first line from
	 * @return The first line from the file at the URL
	 */
	public static String getFirstLineFromURL(String URL) throws UnknownHostException{
		String vers = "0";
		try{
			URL url = new URL(URL);
			URLConnection con = url.openConnection();
			BufferedInputStream in = new BufferedInputStream(con.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			vers = br.readLine();
			
			br.close();
			in.close();
		} catch(UnknownHostException e){
			throw e;
		} catch(IOException e){
			e.printStackTrace();
		}
		
		return vers;
	}
	
	/**
	 * @param file File to get line from
	 * @return The first line in the file
	 */
	public static String getFirstLineFromFile(File file) throws FileNotFoundException{
		String vers = "0";
		
		try{
			FileInputStream fis = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			vers = br.readLine();
			
			br.close();
			in.close();
			fis.close();
		} catch(FileNotFoundException e){
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return vers;
	}
}
