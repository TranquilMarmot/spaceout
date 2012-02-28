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
import java.io.File;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class Launcher {
	/** Directory to use for downloading and extracting file */
	private static String homeDir;
	
	private static boolean isWindows = false;
	
	static Frame frame;
	static JTextArea info;
	
	private static final String RSSFEED = "http://spoutupdate.tumblr.com/rss";
	
	public static void main(String[] args){
		if(System.getProperty("os.name").toLowerCase().contains("windows"))
			isWindows = true;
		
		// TODO In my opinion, it should be an option to just choose where the game is saved and the launcher will save the configuration somehow
		// set the home directory if we're given it, else default to user.home (wherever that may be)
		if(args.length > 0){
			homeDir = args[0];
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
		start.setBackground(Color.black);
		start.setForeground(Color.green);
		buttons.add(start);
		
		// download button
		Button download = new Button("Download Files");
		download.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				// download and extract the files in its own thread so that the frame can still update
				Thread t = new Thread(){
					@Override
					public void run(){
						downloadAndExtractFiles();
					}
				};
				t.start();
			}
		});
		download.setBackground(Color.black);
		download.setForeground(Color.green);
		buttons.add(download);
		
		buttons.setBackground(Color.black);
		frame.add(buttons, BorderLayout.PAGE_END);
		
		Panel centerPane = new Panel();
		
		centerPane.setLayout(new BorderLayout());
		
		info = new JTextArea();
		info.setBackground(Color.black);
		info.setEditable(false);
		info.setForeground(Color.green);
		// set the info pane to always be at the bottom
		DefaultCaret caret = (DefaultCaret) info.getCaret();
		caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
		
		JScrollPane infoPane = new JScrollPane();
		infoPane.getVerticalScrollBar().setBackground(Color.black);
		infoPane.getHorizontalScrollBar().setBackground(Color.black);
		infoPane.getViewport().add(info);
		
		centerPane.add(infoPane, BorderLayout.WEST);
		
		JScrollPane js = new JScrollPane();
		js.getVerticalScrollBar().setBackground(Color.black);
		js.getHorizontalScrollBar().setBackground(Color.black);
		
		js.getViewport().add(RSS2AWT.getRSSFeed(RSSFEED));
		
		centerPane.add(js, BorderLayout.CENTER);
		
		frame.add(centerPane, BorderLayout.CENTER);
		
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
	 * Downloads .spaceout.zip and natives from the FTP server and extracts them
	 */
	private static void downloadAndExtractFiles(){
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
			println("Error! OS not detected! Can't download natives!");
		
		createDirectories();
		
		// download, extract and delete .spaceout.zip
		FileOps.downloadFile(fileServ, "/.spaceout.zip", homeDir + getPath("/.spaceout/.spaceout.zip"));
		FileOps.extractZip(homeDir + getPath("/.spaceout/.spaceout.zip"), homeDir + getPath("/.spaceout"));
		FileOps.deleteFile(homeDir + getPath("/.spaceout/.spaceout.zip"));
		
		// download, extract, and delete the native folder
		FileOps.downloadFile(fileServ, "/natives/" + nativesFile, homeDir + getPath("/.spaceout/" + nativesFile));
		FileOps.extractZip(homeDir + getPath("/.spaceout/" + nativesFile), homeDir + getPath("/.spaceout/lib/natives"));
		FileOps.deleteFile(homeDir + getPath("/.spaceout/" + nativesFile));
	}
	
	/**
	 * Creates the base directories for extracting the .zip files to
	 */
	private static void createDirectories(){
		FileOps.createDirectory(homeDir + getPath("/.spaceout"));
		FileOps.createDirectory(homeDir + getPath("/.spaceout/lib"));
		FileOps.createDirectory(homeDir + getPath("/.spaceout/res"));
		FileOps.createDirectory(homeDir + getPath("/.spaceout/screenshots"));
		FileOps.createDirectory(homeDir + getPath("/.spaceout/lib/natives"));
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
	
	/**
	 * Prints a string to the info console
	 * @param s String to print
	 */
	public static void println(String s){
		info.append(s + "\n");
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
			Runtime.getRuntime().exec("java -jar spaceout.jar", null, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
