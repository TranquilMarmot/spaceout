/*
 *	TODO, I've a feeling we're not in Kansas any more!
 *
 * Bugs
 * 			TODO IMPORTANT Right now all resources are created by being passed a 'new FileInputStream', so there's never a chance to close the stream once the data is loaded. Check to make sure either the code being used closes the stream or refactor some code
 * 			TODO After the first time, the fullscreen key has to be pressed twice to have an effect
 * 			TODO Destroy the Java AWT window when going into fullscreen, then create it again on leaving fullscreen
 * Bullet
 * 			TODO Add a thing to the Physics Debug that also draws an arrow representing the current speed (first point is entity origin, second is origin+linear velocity)
 * 			TODO Give things thrusters
 * Rendering
 * 		GUI
 * 			TODO Make a text input box (put it somewhere and let the player set his/her name)
 * 			TODO Make the arrow keys/WASD control which item is selected and have enter load that item
 * 			TODO Make draggable boxes
 * 			TODO Make an options menu for the menu screen
 * 			TODO Might be a good idea to have the Button constructor take a scale in to control how big the image is (xScale and yScale) rather than the size of the image
 * 		OpenGL
 * 			TODO Use GLSL and more modern OpenGL in place of the current arhaic methods
 * 			TODO Everything is impossible to find! Find a way to billboard text and have it drawn over something (text size probably based on distance from object)
 * 			TODO Mipmap textures so that they aren't as detailed from far away but are close up
 * 		Models
 * 			TODO Materials
 * 			TODO Multiple objects in one file
 * 			TODO Right now, it assumes that the obj file has vertices, normals, and texture coordinates in it.
 * 			If any of those are not present, it will not work at all. Ideally, if normals aren't present they should be calculated
 * 			TODO More powers! Things like initial rotation, center offset, etc. (keep in mind that these also have to be translated to bullet)
 * 			TODO Create an AnimatedModel class that has a call list for every frame (probably in a good old ArrayList) Give it addFrame(int callList) and int curFrame.
 * System
 * 		General
 * 			TODO Threads, threads and more threads! Make this more multi-processor friendly!
 * 			TODO Have all the data managers (ModelManager, TextureManager) keep track of how many objects are using the data. If none are for an extended period of time, delete them to free up space! (Be careful with this one! Might not even be necessary)
 * 			TODO In DebugKeyManager there are keys that don't go through the KeyboardManager
 * 			TODO Look into having ships stored as a zip file containing an obj, a png, and an xml file (is it too slow? easier if its uncompressed?)
 * 			TODO Add a "/save" command that will (in the future) save the game and return to the main menu
 * 		Console
 * 			TODO Make up/down keys control chat history
 * 			TODO Save chat/console history to text file
 * 			TODO Chat color parsing
 * 			TODO Make ctrl+v work with the console
 * 			TODO Add help for all the commands
 * 			TODO Add tabs to the console (one for chat, one for commands, one for combat, etc.)
 * 		XML
 * 			TODO Figure out how to write to XML files
 * 			TODO Store all options in XML files
 * 			TODO Re-do Key Configuration so it loads from XML on startup
 * 			TODO Difficult: Re-do Console Commands so they load from XML
 * Misc
 * 			TODO Whenever explosions get implemented, make sure to have the camera shakes if it's near one!
 */