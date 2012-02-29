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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

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
	
	/** Info text area for printing to (use the println method) */
	private static JTextArea info;
	
	/**
	 * Creates the AWT window and fills it
	 */
	public static void createWindow(){
		frame = new JFrame("Spaceout Launcher Pre-alpha");
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
		
		centerPane.add(createInfoPane(), BorderLayout.WEST);
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
	 * @return A scroll pane containing the info text box
	 */
	private static JScrollPane createInfoPane(){
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
		
		return infoPane;
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
		
		// TODO make this have some sort of logo
		JLabel spout = new JLabel("     SPACEOUT");
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
		
		buttons.add(createStartButton());
		//buttons.add(createDownloadButton());
		
		buttons.setBackground(Color.black);
		
		return buttons;
	}
	
	/**
	 * @return A button that starts the game
	 */
	private static JButton createStartButton(){
		start = new JButton("Start Game");
		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				// if files dont exist, download them before launching
				if(!Launcher.filesExist()){
					Thread t = new Thread(){
						@Override
						public void run(){
							Launcher.downloadAndExtractFiles();
							
							Launcher.launchGame();
							System.exit(0);
						}
					};
					t.start();
				// else we're good, just launch
				} else{
					Launcher.launchGame();
					System.exit(0);
				}
			}
		});
		start.setBackground(Color.black);
		start.setForeground(Color.green);
		
		return start;
	}
	
	/**
	 * Prints a string to the info console
	 * @param s String to print
	 */
	public static void println(String s){
		info.append(s + "\n");
		System.out.println(s);
	}
}
