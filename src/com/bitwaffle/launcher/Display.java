package com.bitwaffle.launcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FilePermission;
import java.security.AccessController;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicProgressBarUI;

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
	
	/** Default size of launcher window */
	private static final int DEFAULT_WIDTH = 886, DEFAULT_HEIGHT = 710;
	
	/** Our main frame*/
	public static JFrame frame;
	
	/** All the better to start the game with */
	public static JButton start;
	
	/** Our progress bar */
	public static JProgressBar progBar;
	
	/** Checkbox to tell the launcher whether or not to look for updates from the server */
	public static JCheckBox checkForUpdate;
	
	/** Info string */
	public static JLabel info = new JLabel("");
	
	/**
	 * Creates the AWT window and fills it
	 */
	public static void createWindow(){
		frame = new JFrame("Spaceout Pre-alpha Launcher");
		frame.setLayout(new BorderLayout());
		
		frame.add(createSouthernPanel(), BorderLayout.SOUTH);
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
		
		south.add(createLogoAndCheckBox(), BorderLayout.WEST);
		
		south.add(createButtonPanel(), BorderLayout.EAST);
		
		// add info label
		info.setForeground(Color.green);
		south.add(info, BorderLayout.CENTER);
		
		return south;
	}
	
	/**
	 * @return Panel with logo and download updates checkbox
	 */
	private static JPanel createLogoAndCheckBox(){
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		panel.add(createLogo(), BorderLayout.WEST);
		panel.add(createUpdateCheckBox(), BorderLayout.EAST);
		
		panel.setBackground(Color.black);
		
		return panel;
	}
	
	/**
	 * @return Logo for Spaceout
	 */
	private static JLabel createLogo(){
		// TODO make this have some sort of logo (might have to download it from the server)
		JLabel spout = new JLabel("     SPACEOUT     ");
		spout.setBackground(Color.black);
		spout.setForeground(Color.green);
		
		return spout;
	}
	
	/**
	 * @return Checkbox to tell whether or not to check for updates
	 */
	private static JCheckBox createUpdateCheckBox(){
		checkForUpdate = new JCheckBox("   Check for updates   ");
		checkForUpdate.setSelected(true);
		checkForUpdate.addActionListener(getCheckBoxListener());
		checkForUpdate.setBackground(Color.black);
		checkForUpdate.setForeground(Color.green);
		
		return checkForUpdate;
	}
	
	/**
	 * @return Listener for the update checkbox
	 */
	private static ActionListener getCheckBoxListener(){
		return new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(!Launcher.serverFound){
					if(!Launcher.filesExist()){
						info.setText("ERROR: Server not found!");
						start.setText("No game files!");
						start.setEnabled(false);
					} else{
						info.setText("ERROR: Server not found! Game files exist, though!");
						start.setText("Start Game");
						start.addActionListener(getLaunchListener());
					}
				} else{
					JCheckBox boxxy = (JCheckBox) e.getSource();
					
					// basically just switch the state of the start button
					if(boxxy.isSelected()){
						if(!Launcher.filesExist()){ 
							start.setText("Download Game (" + Launcher.totalFileSize() + " bytes)");
							start.removeActionListener(start.getActionListeners()[0]);
							start.addActionListener(getDownloadListener());
						} else if(Launcher.updateRequired()){
							start.setText("Update Game (from " + Launcher.localVersion + " to " + Launcher.serverVersion + ")");
							start.removeActionListener(start.getActionListeners()[0]);
							start.addActionListener(getDownloadListener());
						} else{
							start.setText("Start Game");
							start.removeActionListener(start.getActionListeners()[0]);
							start.addActionListener(getLaunchListener());
						}
					} else{
						if(!Launcher.filesExist()){ 
							start.setText("Download Game (" + Launcher.totalFileSize() + " bytes)");
							start.removeActionListener(start.getActionListeners()[0]);
							start.addActionListener(getDownloadListener());
						}else{
							start.setText("Start Game");
							start.removeActionListener(start.getActionListeners()[0]);
							start.addActionListener(getLaunchListener());
						}
					}
				}
			}
		};
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
		// if there's no server and files exist, let the game be played, otherwise don't
		if(!Launcher.serverFound){
			if(!Launcher.filesExist()){
				info.setText("ERROR: Server not found!");
				start = new JButton("No game files!");
				start.setEnabled(false);
			} else{
				info.setText("ERROR: Server not found! Game files exist, though!");
				start = new JButton("Start Game");
				start.addActionListener(getLaunchListener());
			}
		} else{
			if(!Launcher.filesExist()){
				info.setText("Using " + Launcher.getHomeDir() + " as home directory");
				start = new JButton("Download Game (" + Launcher.totalFileSize() + " bytes)");
				start.addActionListener(getDownloadListener());
			}else if(Launcher.updateRequired()){
				start = new JButton("Update Game (from " + Launcher.localVersion + " to " + Launcher.serverVersion + ")");
				start.addActionListener(getDownloadListener());
			}else{
				progBar.setVisible(false);
				start = new JButton("Start Game");
				start.addActionListener(getLaunchListener());
			}
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
				if(!FileOps.hasWriteAccess(Launcher.workingDir)){
					chooseNewHomeDir();
					if(Launcher.filesExist()){
						Launcher.localVersion = Launcher.getLocalVersion();
						start.setText("Start Game");
						start.removeActionListener(start.getActionListeners()[0]);
						start.addActionListener(getLaunchListener());
					}
				} else{
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
			}
		};
	}
	
	private static void chooseNewHomeDir(){
		try{
			AccessController.checkPermission(new FilePermission(Launcher.workingDir, "write"));
		} catch (java.security.AccessControlException e){
			
		}
		
		int ret = JOptionPane.showConfirmDialog(null, "Error: Couldn't get write access to " + Launcher.workingDir + "\nSelect a different directory?", "Write Access Denied", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
		if(ret == JOptionPane.YES_OPTION){
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
		    int returnVal = chooser.showOpenDialog(null);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	Launcher.workingDir = chooser.getSelectedFile().getAbsolutePath();
		    	if(!Launcher.workingDir.endsWith(System.getProperty("file.separator")))
		    		Launcher.workingDir += System.getProperty("file.separator");
		    	
		    	Display.info.setText("Using " + Launcher.workingDir + " as home directory");
		    }
		}
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
		
		progBar.setUI(new BasicProgressBarUI() {
		      protected Color getSelectionBackground() { return Color.green; }
		      protected Color getSelectionForeground() { return Color.black; }
		});
		
		progBar.setVisible(false);
		
		return progBar;
	}
}
