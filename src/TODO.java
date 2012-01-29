/*
 *	TODO, I've a feeling we're not in Kansas any more!
 *
 * Temp
 * 			TODO Do at least one homework assignment today
 * Bugs
 * 			TODO After the first time, the fullscreen key has to be pressed twice to have an effect
 * 			TODO Destroy the Java AWT window when going into fullscreen, then create it again on leaving fullscreen
 * JBullet
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
 * 			TODO For the GLSL rendering loop, have the camera's location and rotation be a uniform and have each entity's location and rotation be a uniform as well
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
 * 			TODO Have key configuration load from XML on startup
 * 			TODO Impossible: Re-do Console Commands so they load from XML (how are you going to store their code? Precompiled java classes?)
 * Misc
 * 			TODO Whenever explosions get implemented, make sure to have the camera shakes if it's near one!
 */