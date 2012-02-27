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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class Launcher {
	private static final String ICON_PATH = "res/images/";
	
	public static void main(String[] args){
		createWindow();
	}
	
	private static void createWindow(){
		Frame frame = new Frame("Spaceout Launcher Pre-alpha");
		frame.setLayout(new BorderLayout());
		
		Panel buttons = new Panel();
		buttons.setLayout(new FlowLayout());
		
		Button start = new Button("Start Game");
		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				launchGame();
				System.exit(0);
			}
		});
		buttons.add(start);
		
		
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
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ICON_PATH + "icon.png"));
		frame.setSize(750, 500);
		frame.setVisible(true);
	}
	
	private static void downloadFiles(){
		String un = "spaceout%40capitolhillmedia.com:apple007%21";
		String ftpServ = "ftp.capitolhillmedia.com";
		String fileName = "/.spaceout.zip";
		
		try{
			URL url = new URL("ftp://" + un + "@" + ftpServ + fileName + ";type=i");
			URLConnection con = url.openConnection();
			BufferedInputStream in = new BufferedInputStream(con.getInputStream());
			FileOutputStream out = new FileOutputStream(System.getProperty("user.home") + fileName);
			
			int i = 0;
			byte[] bytesIn = new byte[1024];
			System.out.println(in.available());
			while((i = in.read(bytesIn)) >= 0){
				out.write(bytesIn, 0, i);
			}
			out.close();
			in.close();
			
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private static void launchGame(){
		String os = System.getProperty("os.name").toLowerCase();
		String directory = System.getProperty("user.home");
		
		if(os.contains("windows"))
			directory += "\\.spaceout\\";
		else if(os.contains("linux") || os.contains("mac") || os.contains("solaris"))
			directory += "/.spaceout/";
		
		File file = new File(directory);
		
		try {
			Runtime.getRuntime().exec("java -jar spaceout.jar", null, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
