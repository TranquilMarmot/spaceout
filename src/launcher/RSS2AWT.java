package launcher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class takes an RSS feed and returns a nice looking JPanel containing a number of items in the feed.
 * Java's native XML parsing libraries are used to deal with the RSS feed.
 * @author TranquilMarmot
 *
 */
public class RSS2AWT {
	/**
	 * Gets a JPanel with the RSS feed in it
	 * @param rssUrl URL for RSS feed
	 * @param numItems Number of items to grab from RSS feed
	 * @return JPanel with every element from the RSS feed
	 */
	public static JPanel getRSSFeed(String rssUrl, int numItems){
		JPanel p = new JPanel();
		
		p.setBackground(Color.black);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc;
		NodeList nodes = null;
		try{
			db = dbf.newDocumentBuilder();
			
			URL url = new URL(rssUrl);
			URLConnection con = url.openConnection();
			
			InputStream stream = con.getInputStream();
			doc = db.parse(stream);
			stream.close();
			
			Element docEle = doc.getDocumentElement();
			nodes = docEle.getChildNodes();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		if(nodes != null && nodes.getLength() > 0){
			for(int i = 0; i < nodes.getLength(); i++){
				if(!nodes.item(i).getNodeName().equals("#text")){
					Element ele = (Element) nodes.item(i);
					if(ele.getNodeName().equals("channel"))
						p.add(parseChannel(ele, numItems));
				}
			}
		}
		
		return p;
	}
	
	/**
	 * Parses a 'channel' from an RSS feed
	 * @param channel Channel to parse
	 * @param numItems Number of items to grab from RSS feed
	 * @return JPanel with all the elements from the channel
	 */
	private static JPanel parseChannel(Element channel, int numItems){
		JPanel p = new JPanel();
		
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		NodeList items = channel.getChildNodes();
		
		int itemsParsed = 0;
		
		if(items != null && items.getLength() > 0){
			for(int i = 0; i < items.getLength(); i++){
				if(!items.item(i).getNodeName().equals("#text")){
					Element ele = (Element) items.item(i);
					
					if(ele.getNodeName().equals("item")){
						p.add(parseItem(ele));
						
						JPanel emptyPanel = new JPanel();
						emptyPanel.setSize(100, 25);
						emptyPanel.setBackground(Color.black);
						p.add(emptyPanel);
						
						itemsParsed++;
					}
				}
				
				if(itemsParsed >= numItems)
					break;
			}
		}
		
		return p;
	}
	
	/**
	 * Parses an item from an RSS feed
	 * @param item Item to parse
	 * @return JPanel containing the title, description and date of the post
	 */
	private static JPanel parseItem(Element item){
		JPanel p = new JPanel();
		
		String title = getString(item, "title");
		String description = getString(item, "description");
		// FIXME couldn't find a clean looking way to include this (ideally it would open up the default browser)
		//String link = getString(item, "link");
		String pubDate = getString(item, "pubDate");
		
		p.setLayout(new BorderLayout());
		
		if(!title.equals("Photo")){
			JLabel titleLabel = new JLabel(title);
			titleLabel.setForeground(Color.green);
			p.add(titleLabel, BorderLayout.NORTH);
		}
		
	
		/*
		 * TODO
		 * Since links don't work (yet) it might be a good idea to parse the description
		 * string either get rid of every <a></a> and change the text color to just be blue 
		 */
		JLabel descLabel = new JLabel("<html>" + description + "</html>");
		descLabel.setForeground(Color.white);
		JLabel dateLabel = new JLabel(pubDate);
		dateLabel.setForeground(Color.green);
		
		
		p.add(descLabel, BorderLayout.CENTER);
		p.add(dateLabel, BorderLayout.SOUTH);
		
		p.setBorder(new LineBorder(Color.green, 2));
		p.setBackground(Color.black);
		
		return p;
	}
	
	/**
	 * Gets a string from an element
	 * 
	 * @param ele
	 *            The element to get the string from
	 * @param tagName
	 *            The tag to get the string from
	 * @return The string from the element
	 */
	private static String getString(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}
}
