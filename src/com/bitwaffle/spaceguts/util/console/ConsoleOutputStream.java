package com.bitwaffle.spaceguts.util.console;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;

/**
 * Used for creating an OutputStream that prints to a console
 * Also handles writing everything printed to the console to a log file
 * @author TranquilMarmot
 */
class ConsoleOutputStream extends OutputStream{
	/** Console to print to */
	Console console;
	
	/** Stream for saving console text to a log */
	FileOutputStream logStream;
	
	/** 
	 * Each character printed is added to this string 
	 * Whenever a newline character is encountered, the string is printed
	 * to the console and then reset.
	 * A result of this is that using print() will add characters, but they won't
	 * actually be printed until println() is used or a newline character is printed. 
	 */
	String s;
	
	/**
	 * Create a console output stream
	 * @param console Console to print to
	 */
	public ConsoleOutputStream(Console console){
		this.console = console;
		s = "";
		
		try{
			// make sure logs directory exists
			File logDirectory = new File("logs/");
			if(!logDirectory.exists())
				logDirectory.mkdir();
			
			// create log file
			File log = new File("logs/" + getCurrentDate() + "-" + getCurrentTime() + ".log");
			log.createNewFile();
			
			logStream = new FileOutputStream(log);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void write(int wat) throws IOException {
		/*
		char[] chars = Character.toChars(wat);
		for(char c : chars){
			// handle newline
			if(c == '\n'){
				console.print(s);
				
				// write line to log
				String logString = getCurrentTime() + " - " + s + "\n";
				logStream.write(logString.getBytes());
				
				s = "";
			} else{
				s += Character.toString(c);
			}
		}
		*/
	}
	
	/**
	 * @return A string representing the current time
	 */
	private static String getCurrentTime(){
		// instantiating a gergorian calendar sets it to the current time and
		// date
		Calendar calendar = new GregorianCalendar();
		// string for 'am' or 'pm'
		String am_pm;

		int hours = calendar.get(Calendar.HOUR);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);

		if (calendar.get(Calendar.AM_PM) == 0)
			am_pm = "am";
		else
			am_pm = "pm";

		/*
		 * Format the string to match mm.dd.yy-hh.mm.ss[am/pm] NOTE:
		 * This won't work properly past the year 2100.
		 */
		Formatter format = new Formatter();
		format.format("%02d.%02d.%02d" + am_pm, hours, minutes, seconds);
		
		return format.toString();
	}
	
	/**
	 * @return A string representing the current date
	 */
	private static String getCurrentDate(){
		// instantiating a gergorian calendar sets it to the current time and
		// date
		Calendar calendar = new GregorianCalendar();

		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		int year = calendar.get(Calendar.YEAR);

		/*
		 * Format the string to match mm.dd.yy-hh.mm.ss[am/pm] NOTE:
		 * This won't work properly past the year 2100.
		 */
		Formatter format = new Formatter();
		format.format("%02d.%02d.%d", month, day, year - 2000);
		
		return format.toString();
	}
}
