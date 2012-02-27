package launcher;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class Launcher {
	/** Directory to use for downloading and extracting file */
	private static String homeDir;
	
	private static boolean isWindows = false;
	
	/** Whether or not we're using the default home directory or one given in args */
	private static boolean homeDirSet = false;
	
	static Frame frame;
	static JTextArea info;
	
	public static void main(String[] args){
		if(System.getProperty("os.name").toLowerCase().contains("windows"))
			isWindows = true;
		
		// TODO In my opinion, it should be an option to just choose where the game is saved and the launcher will save the configuration somehow
		// set the home directory if we're given it, else default to user.home (wherever that may be)
		if(args.length > 0){
			homeDir = args[0];
			homeDirSet = true;
		} else{
			homeDir = System.getProperty("user.home");
		}
		
		createWindow();
		
		println("Using " + homeDir + " as home directory");
	}
	
	/**
	 * Creates an AWT window and fills it with things
	 */
	private static void createWindow(){
		frame = new Frame("Spaceout Launcher Pre-alpha");
		frame.setLayout(new BorderLayout());
		
		Panel buttons = new Panel();
		buttons.setLayout(new FlowLayout());
		
		// start button
		Button start = new Button("Start Game");
		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				launchGame();
				System.exit(0);
			}
		});
		buttons.add(start);
		
		// download button
		Button download = new Button("Download Files");
		download.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				downloadFiles();
			}
		});
		buttons.add(download);
		
		buttons.setBackground(Color.black);
		frame.add(buttons, BorderLayout.PAGE_END);
		
		
		Panel webPage = new Panel();
		
		webPage.setLayout(new BorderLayout());
		
		info = new JTextArea();
		info.setBackground(Color.black);
		info.setEditable(false);
		info.setForeground(Color.green);
		
		JScrollPane infoPane = new JScrollPane();
		infoPane.getVerticalScrollBar().setBackground(Color.black);
		infoPane.getHorizontalScrollBar().setBackground(Color.black);
		infoPane.getViewport().add(info);
		
		webPage.add(infoPane, BorderLayout.WEST);
		
		JTextPane tp = new JTextPane();
		JScrollPane js = new JScrollPane();
		js.getVerticalScrollBar().setBackground(Color.black);
		js.getHorizontalScrollBar().setBackground(Color.black);
		
		try{
			/*
			 *  FIXME This is very ugly and doesnt render well!!
			 *  Either find a way to get a simple tumblr page or
			 *  parse the RSS feed as an XML document and create
			 *  tons of little JTextPane/JEditorPane objects
			 *  that will render the little bits.
			 */
			URL url = new URL("http://spaceoutgame.tumblr.com");
			tp.setPage(url);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		js.getViewport().add(tp);
		
		webPage.add(js, BorderLayout.CENTER);
		
		frame.add(webPage, BorderLayout.CENTER);
		
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("res/images/icon.png"));
		frame.setSize(750, 500);
		frame.setVisible(true);
	}
	
	/**
	 * Downloads .spaceout.zip from the FTP server and extracts it
	 */
	private static void downloadFiles(){
		// spaceout@capitolhillmedia.com:apple007!
		String un = "spaceout%40capitolhillmedia.com:apple007%21";
		String ftpServ = "ftp.capitolhillmedia.com";
		
		// get .spaceout.zip
		try{
			println("Downloading .spaceout.zip..");
			String zipFile = "/.spaceout.zip";
			
			// Open up an input stream from the FTP server
			URL url = new URL("ftp://" + un + "@" + ftpServ + zipFile + ";type=i");
			URLConnection con = url.openConnection();
			BufferedInputStream in = new BufferedInputStream(con.getInputStream());
			
			FileOutputStream out = new FileOutputStream(homeDir + zipFile);
			
			// Fill file with bytes from server
			int i = 0;
			byte[] bytesIn = new byte[1024];
			while((i = in.read(bytesIn)) >= 0){
				out.write(bytesIn, 0, i);
			}
			
			// don't cross the streams!
			out.close();
			in.close();
			
			println(".spaceout downloaded, extracting...");
			extractFiles();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		// get natives zip
		try{
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
				println("Error! OS not detected! Can't download natives!");
			
			println("Downloading natives (" + nativesFile + ")...");
			
			if(nativesFile != null){
				// Open up an input stream from the FTP server
				URL url = new URL("ftp://" + un + "@" + ftpServ + "/natives/" + nativesFile + ";type=i");
				URLConnection con = url.openConnection();
				BufferedInputStream in = new BufferedInputStream(con.getInputStream());
				
				FileOutputStream out = new FileOutputStream(homeDir + getPath("/" + nativesFile));
				
				// Fill file with bytes from server
				int i = 0;
				byte[] bytesIn = new byte[1024];
				while((i = in.read(bytesIn)) >= 0){
					out.write(bytesIn, 0, i);
				}
				
				// don't cross the streams!
				out.close();
				in.close();
				
				println(nativesFile + " downloaded, extracting...");
				extractNatives(nativesFile);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Extracts the natives zip to the right location
	 * @param nativesFile zip file to extract
	 */
	private static void extractNatives(String nativesFile){
		// TODO these extract methods should really be one method that take some parameters that tell them what to download/extract
		try{
			// create /lib/natives
			File nativedir = new File(homeDir + getPath("/.spaceout/lib/natives"));
			if(!nativedir.exists()){
				boolean success = nativedir.mkdir();
				
				if(success)
					println("Directory /lib/natives created");
				else
					//System.out.println("Error creating directory!");
					info.append("Error creating directory!");
			}
			
			FileInputStream fis = new FileInputStream(homeDir + getPath("/" + nativesFile));
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fis));
			
			final int BUFFER = 512;
			
			ZipEntry ent;
			while((ent = zin.getNextEntry()) != null){
				if(ent.isDirectory()){
					println("There shouldn't be any directories inside of the natives zip!");
				} else{
					println("Extracting " + ent.getName());
					
					// stream to write file to
					FileOutputStream fos = new FileOutputStream(homeDir + getPath("/.spaceout/lib/natives/" + ent.getName()));
					BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER); 
					
					// write the data
					int count;
					byte[] data = new byte[BUFFER];
					
					while((count = zin.read(data, 0, BUFFER)) != -1){
						dest.write(data, 0, count);
					}
					
					// shake the extra drops out and flush
					dest.flush();
					dest.close();
				}
			}
			
			zin.close();
			fis.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		
		// clean up after ourselves
		File toDel = new File(homeDir + getPath("/" + nativesFile));
		boolean succ = toDel.delete();
		
		if(succ)
			println("Deleted " + nativesFile);
		else
			println("Couldn't delete " + nativesFile + "!");
	}
	
	/**
	 * Extracts .spaceout.zip
	 */
	private static void extractFiles(){
		try{
			// create directories
			createDirectories();
			
			// input stream for zip file
			FileInputStream fis = new FileInputStream(homeDir + getPath("/.spaceout.zip"));
			ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fis));
			
			// size to use for buffer for extracting
			final int BUFFER = 512;
			
			//iterate through all the zip file entries
			ZipEntry ent;
			while((ent = zin.getNextEntry()) != null){
				// create a directory if it's a directory
				if(ent.isDirectory()){
					File dir = new File(homeDir + getPath("/.spaceout/" + ent.getName()));
					
					// create the directory if it doesn't exist
					if(!dir.exists()){
						boolean succ = dir.mkdir();
						
						if(succ){
							println("Directory " + ent.getName() + " created");
							dir.setWritable(true);
						}else
							println("Error creating directory " + ent.getName() + "!!!");
					}
				// if it's not a directory, its a file
				} else{
					println("Extracting " + ent.getName());
					
					// stream to write file to
					FileOutputStream fos = new FileOutputStream(homeDir + getPath("/.spaceout/" + ent.getName()));
					BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER); 
					
					// write the data
					int count;
					byte[] data = new byte[BUFFER];
					
					while((count = zin.read(data, 0, BUFFER)) != -1){
						dest.write(data, 0, count);
					}
					
					// shake the extra drops out and flush
					dest.flush();
					dest.close();
				}
			}
			
			zin.close();
			fis.close();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		// clean up after ourselves
		File toDel = new File(homeDir + getPath("/.spaceout.zip"));
		boolean succ = toDel.delete();
		
		if(succ)
			println("Deleted .spaceout.zip");
		else
			println("Couldn't delete spaceout.zip!");
	}
	
	/**
	 * Creates the base directories for extracting the .zip to
	 */
	private static void createDirectories(){
		// create .spaceout
		File dotsp = new File(homeDir + "/.spaceout");
		if(!dotsp.exists()){
			boolean success = dotsp.mkdir();
			
			if(success)
				println("Directory .spaceout created");
			else
				println("Error creating directory!");
		}
		
		// create lib
		File lib = new File(homeDir + getPath("/.spaceout/lib"));
		if(!lib.exists()){
			boolean success = lib.mkdir();
			
			if(success)
				println("Directory lib created");
			else
				println("Error creating lib!");
		}
		
		// create res
		File res = new File(homeDir + getPath("/.spaceout/res"));
		if(!res.exists()){
			boolean success = res.mkdir();
			
			if(success)
				println("Directory res created");
			else
				println("Error creating res!");
		}
		
		// create screenshot folder
		File screeny = new File(homeDir + getPath("/.spaceout/screenshots"));
		if(!screeny.exists()){
			boolean success = screeny.mkdir();
			
			if(success)
				println("Directory screenshots created");
			else
				println("Error creating screenshots!");
		}
	}
	
	/**
	 * Converts all / in a string to \\ (for windows)
	 * @param path Path to convert
	 * @return Path corrected to work with current OS
	 */
	private static String getPath(String path){
		if(isWindows)
			return path.replace('/', '\\');
		else
			return path;
	}
	
	private static void println(String s){
		info.append(s + "\n");
		info.repaint();
	}
	
	/**
	 * Runs "java -jar spaceout.jar" inside of .spaceout inside of the given home directory.
	 * At this point, it's assumed that .spaceout exists.
	 */
	private static void launchGame(){
		String directory = homeDir + getPath("/.spaceout/");
		
		// so we can execute the command in the right spot
		File file = new File(directory);
		
		try {
			if(homeDirSet){
				Runtime.getRuntime().exec("java -jar spaceout.jar " + homeDir, null, file);
			}else
				Runtime.getRuntime().exec("java -jar spaceout.jar", null, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
