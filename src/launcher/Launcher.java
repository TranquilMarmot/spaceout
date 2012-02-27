package launcher;

import java.awt.BorderLayout;
import java.awt.Button;
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
import javax.swing.JTextPane;

public class Launcher {
	/** Directory to use for downloading and extracting file */
	private static String homeDir;
	
	private static boolean isWindows = false;
	
	/** Whether or not we're using the default home directory or one given in args */
	private static boolean homeDirSet = false;
	
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
	}
	
	/**
	 * Creates an AWT window and fills it with things
	 */
	private static void createWindow(){
		Frame frame = new Frame("Spaceout Launcher Pre-alpha");
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
		
		frame.add(buttons, BorderLayout.PAGE_END);
		
		
		Panel webPage = new Panel();
		
		webPage.setLayout(new BorderLayout());
		
		JTextPane tp = new JTextPane();
		JScrollPane js = new JScrollPane();
		
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
		/*
		 * TODO
		 * This should only download the natives that the game needs, not all of them!
		 */
		
		// spaceout@capitolhillmedia.com:apple007!
		String un = "spaceout%40capitolhillmedia.com:apple007%21";
		String ftpServ = "ftp.capitolhillmedia.com";
		String zipFile = "/.spaceout.zip";
		
		try{
			// Open up an input stream from the FTP server
			URL url = new URL("ftp://" + un + "@" + ftpServ + zipFile + ";type=i");
			URLConnection con = url.openConnection();
			BufferedInputStream in = new BufferedInputStream(con.getInputStream());
			
			FileOutputStream out = new FileOutputStream(homeDir + zipFile);
			
			// Fill file with bytes from server
			int i = 0;
			byte[] bytesIn = new byte[1024];
			System.out.println(in.available());
			while((i = in.read(bytesIn)) >= 0){
				out.write(bytesIn, 0, i);
			}
			
			// don't cross the streams!
			out.close();
			in.close();
			
			extractFiles();
		} catch(Exception e){
			e.printStackTrace();
		}
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
					File dir = new File(homeDir + getPath(".spaceout/" + ent.getName()));
					
					// create the directory if it doesn't exist
					if(!dir.exists()){
						boolean succ = dir.mkdir();
						
						if(succ){
							System.out.println("Directory " + ent.getName() + " created");
							dir.setWritable(true);
						}else
							System.out.println("Error creating directory " + ent.getName() + "!!!");
					}
				// if it's not a directory, its a file
				} else{
					System.out.println("Extracting " + ent.getName());
					File file = new File(homeDir + getPath("/.spaceout/" + ent.getName()));
					
					// create the file only if it doesn't exit
					if(!file.exists()){
						if(!file.createNewFile())
							System.out.println("error creating new file!");
					}
					
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
	}
	
	/**
	 * Creates the base directories for extracting the .zip to
	 */
	private static void createDirectories(){
		// create .spaceout
		File dotsp = new File(homeDir + ".spaceout");
		if(!dotsp.exists()){
			boolean success = dotsp.mkdir();
			
			if(success)
				System.out.println("Directory .spaceout created");
			else
				System.out.println("Error creating directory!");
		}
		
		// create lib
		File lib = new File(homeDir + getPath(".spaceout/lib"));
		if(!lib.exists()){
			boolean success = lib.mkdir();
			
			if(success)
				System.out.println("Directory lib created");
			else
				System.out.println("Error creating lib!");
		}
		
		// create res
		File res = new File(homeDir + getPath(".spaceout/res"));
		if(!res.exists()){
			boolean success = res.mkdir();
			
			if(success)
				System.out.println("Directory res created");
			else
				System.out.println("Error creating res!");
		}
		
		// create screenshot folder
		File screeny = new File(homeDir + getPath(".spaceout/screenshots"));
		if(!screeny.exists()){
			boolean success = screeny.mkdir();
			
			if(success)
				System.out.println("Directory screenshots created");
			else
				System.out.println("Error creating screenshots!");
		}
	}
	
	/**
	 * 
	 * @param path Path to convert
	 * @return Path corrected to work with current OS
	 */
	private static String getPath(String path){
		if(isWindows)
			return path.replace('/', '\\');
		else
			return path;
	}
	
	/**
	 * Runs "java -jar spaceout.jar" inside of .spaceout inside of the given home directory.
	 * At this point, it's assumed that .spaceout exists.
	 */
	private static void launchGame(){
		String directory = homeDir;
		
		// figure out what OS we're on and add to the directory accordingly
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("windows"))
			directory += "\\.spaceout\\";
		else if(os.contains("linux") || os.contains("mac") || os.contains("solaris"))
			directory += "/.spaceout/";
		
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
