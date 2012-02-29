package launcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

/**
 * This class manages methods that create the frame and all the panels for the launcher
 * @author TranquilMarmot
 *
 */
public class Display {
	/** URL to RSS feed to use in center window */
	private static final String RSSFEED = "http://spoutupdate.tumblr.com/rss";
	/** How many items from the feed to show */
	private static final int NUMITEMS = 5;
	
	private static final int DEFAULT_WIDTH = 886, DEFAULT_HEIGHT = 710;
	
	/** Our main frame*/
	public static JFrame frame;
	
	public static JButton start, download;
	
	public static JProgressBar progBar;
	
	public static JLabel info;
	
	/**
	 * Creates the AWT window and fills it
	 */
	public static void createWindow(){
		frame = new JFrame("Spaceout Pre-alpha Launcher");
		frame.setLayout(new BorderLayout());
		
		frame.add(createSouthernPanel(), BorderLayout.PAGE_END);
		frame.add(createCenterPane(), BorderLayout.CENTER);
		
		// close the frame when its close button is clicked
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		
		// set the icon and size
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("res/images/icon.png"));
		frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		frame.setVisible(true);
	}
	
	/**
	 * @return The center pane
	 */
	private static JPanel createCenterPane(){
		JPanel centerPane = new JPanel();
		
		centerPane.setLayout(new BorderLayout());
		
		//centerPane.add(createInfoPane(), BorderLayout.WEST);
		centerPane.add(createRSSPane(), BorderLayout.CENTER);
		
		return centerPane;
	}
	
	/**
	 * @return An RSS pane with the RSS feed define at the top of this class
	 */
	private static JScrollPane createRSSPane(){
		JScrollPane rssPane = new JScrollPane();
		rssPane.getVerticalScrollBar().setBackground(Color.black);
		rssPane.getHorizontalScrollBar().setBackground(Color.black);
		
		rssPane.getViewport().add(RSS2AWT.getRSSFeed(RSSFEED, NUMITEMS));
		
		return rssPane;
	}
	
	/**
	 * @return The southern panel
	 */
	private static JPanel createSouthernPanel(){
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout());
		south.setBackground(Color.black);
		south.setForeground(Color.green);
		
		south.add(createButtonPanel(), BorderLayout.EAST);
		
		info = new JLabel("");
		info.setForeground(Color.green);
		south.add(info, BorderLayout.CENTER);
		
		// TODO make this have some sort of logo (might have to download it from the server)
		JLabel spout = new JLabel("     SPACEOUT     ");
		spout.setBackground(Color.black);
		spout.setForeground(Color.green);
		south.add(spout, BorderLayout.WEST);
		
		return south;
	}
	
	/**
	 * @return A panel with some buttons on it
	 */
	private static JPanel createButtonPanel(){
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		
		JPanel sep = new JPanel();
		sep.setBackground(Color.black);
		
		JPanel sep2 = new JPanel();
		sep2.setBackground(Color.black);
		
		
		buttons.add(createProgressBar());
		buttons.add(sep);
		buttons.add(sep2);
		buttons.add(createStartButton());
		
		buttons.setBackground(Color.black);
		
		return buttons;
	}
	
	/**
	 * @return A button that starts the game
	 */
	private static JButton createStartButton(){
		if(!Launcher.filesExist()){
			start = new JButton("Download Game (" + Launcher.totalFileSize() + " bytes)");
			start.addActionListener(getDownloadListener());
		}else{
			progBar.setVisible(false);
			start = new JButton("Start Game");
			start.addActionListener(getLaunchListener());
		}
		start.setBackground(Color.black);
		start.setForeground(Color.green);
		
		return start;
	}
	
	/**
	 * @return An action listener that launches the game
	 */
	private static ActionListener getLaunchListener(){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Launcher.launchGame();
				System.exit(0);
			}
			
		};
	}
	
	/**
	 * @return An action listener that downloads and extracts files
	 */
	private static ActionListener getDownloadListener(){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				progBar.setVisible(true);
				start.setText("Downloading...");
				start.setEnabled(false);
				Thread t = new Thread(){
					@Override
					public void run(){
						Launcher.downloadAndExtractFiles();
						progBar.setVisible(false);
						start.setEnabled(true);
						start.setText("Start Game");
						start.removeActionListener(start.getActionListeners()[0]);
						start.addActionListener(getLaunchListener());
					}
				};
				t.start();
			}
		};
	}
	
	/**
	 * @return The progress bar
	 */
	private static JProgressBar createProgressBar(){
		progBar = new JProgressBar();
		progBar.setValue(0);
		progBar.setStringPainted(true);
		
		progBar.setBackground(Color.black);
		progBar.setForeground(Color.green);
		
		progBar.setVisible(false);
		
		return progBar;
	}
	
	/**
	 * Prints a string to the info console
	 * @param s String to print
	 */
	public static void println(String s){
		System.out.println(s);
	}
}
