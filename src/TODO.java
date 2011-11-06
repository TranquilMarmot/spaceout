/*
 * In sort of order of importance:
 * TODO Enhance acceleration. Make it so that everything slows down at the same speed.
 * TODO Also, the acceleration is not rotation independent. I.E. if you look down and speed up forward, then stop accelerating and look somewhere else, 
 * you continue going in the direction you're looking (instead of going the direction you were looking at before)
 * TODO create the rest of the solar system
 * TODO Organize the res folder! It's pretty bad at the moment
 * 
 * TODO figure out how to texture the ship and other things
 * TODO planets are impossible to find! Find a way to billboard text and have it drawn over a planet (text size probably based on distance from planet)
 * TODO Make the skybox a box instead of a sphere
 * TODO Make geometry (spheres specifically, but could also go for stars) draw with less polygons when you're far away and more the closer you get
 * TODO Mipmap textures so that they aren't as detailed from far away but are close up
 * TODO Make it so that only things that are visible are drawn (i.e. stuff behind the camera wouldn't be drawn)
 * TODO Make everything that extends 'Light' have a 'light' int that points to one of LIGHT0-LIGHT8
 * TODO Threads, threads and more threads! Make this more multi-processor friendly!
 * TODO Create a GUI (maybe using nifty gui? http://nifty-gui.lessvoid.com/) 
 * 
 * 
 * NICK Tasks: Console, XML, Lua(?), By Category, Importance
 * TODO Console
 * -TODO Make text wrap around after certain number of mono-spaced characters. (It's nice that they're mono-spaced.)
 * -TODO Make console text draw always, but fade to dark after a few seconds.
 * -TODO Make up/down keys control chat history
 * -TODO Make text draw in front of transparent console background. (currently draws behind)
 * -TODO Save chat/console history to text file
 * -TODO Chat color parsing
 * -TODO Make ctrl+v work with the console
 * TODO XML
 * -TODO Figure out how to write to XML files
 * -TODO Store all options in XML files
 * -TODO Re-do Key Configuration so it loads from XML on startup.
 * -TODO Difficult: Re-do Console Commands so they load from XML, possibly writable with LUA 
 * 
 */