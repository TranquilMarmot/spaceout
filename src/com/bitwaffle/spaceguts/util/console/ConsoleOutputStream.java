package com.bitwaffle.spaceguts.util.console;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Used for creating an OutputStream that prints to a console
 * @author TranquilMarmot
 */
class ConsoleOutputStream extends OutputStream{
	/** Console to print to */
	Console console;
	
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
	}
	
	@Override
	public void write(int wat) throws IOException {
		char[] chars = Character.toChars(wat);
		for(char c : chars){
			// handle newline
			if(c == '\n'){
				console.print(s);
				s = "";
			} else{
				s += Character.toString(c);
			}
		}
	}
}
