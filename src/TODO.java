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
 * 			TODO Figure out how to warp things
 * 			TODO Figure out how to get a ship to reorient itself to start going in a new direction when the direction it's facing is changed
 * 			TODO Revamp controls! First person cockpit view- ship turns a different speed based on how far from the center the cursor would be
 * 			TODO Third person ship controls like Halo- you look where you want to go and the ship turns towards it	
 * 			TODO Explosions!
 * 			TODO Turrets
 * OpenAL
 * 			TODO Decide on a format for sound files (ogg?)
 * 			TODO Figure out how to make noises
 * 			TODO Figure out how to make noises at a specific spot (doppler effect?)
 * 			TODO Create a nice interface for making noises when things happen
 * 			TODO Jukebox that plays songs from a given directory
 * Rendering
 * 			TODO Scene graphs, all the way down
 * 		GUI
 * 			TODO Make a text input box (put it somewhere and let the player set his/her name)
 * 			TODO Make the arrow keys/WASD control which item is selected and have enter load that item ('controller friendly')
 * 			TODO Make draggable boxes
 * 			TODO Make an options menu for the menu screen
 * 			TODO Might be a good idea to have the Button constructor take a scale in to control how big the image is (xScale and yScale) rather than the size of the image
 * 		OpenGL
 * 			TODO Everything is impossible to find! Find a way to billboard text and have it drawn over something (text size probably based on distance from object)
 * 			TODO Mipmap textures so that they aren't as detailed from far away but are close up
 * 			TODO Antialiasing
 * 			TODO Inside the cockpit view!
 * 			TODO Particle effects- thrusters, explosions, etc.
 * 			TODO Get multiple lights working- have bullets give off a glow
 * 			TODO Spotlights- espeically ones attached to ships
 * 			TODO Shadows
 * 		Models
 * 			TODO Multiple objects in one file
 * 			TODO Right now, it assumes that the obj file has vertices, normals, and texture coordinates in it.
 * 			If any of those are not present, it will not work at all. Ideally, if normals aren't present they should be calculated
 * 			TODO More powers! Things like initial rotation, center offset, etc. (keep in mind that these also have to be translated to bullet)
 * 			TODO Bump-maps, normal maps
 * 			TODO Create an AnimatedModel class that has a an addFrame(Frame frame) and int curFrame.
 * System
 * 		General
 * 			TODO Look into having ships stored as a zip file containing an obj, a png, and an xml file (is it too slow? easier if its uncompressed?)
 * 			TODO Add a "/save" command that saves the game and return to the main menu
 * 		Console
 * 			TODO Make up/down keys control chat history
 * 			TODO Save chat/console history to text file
 * 			TODO Chat color parsing
 * 			TODO Make ctrl+v work with the console
 * 			TODO Add tabs to the console (one for chat, one for commands, one for combat, etc.)
 * 		XML
 * 			TODO Figure out how to write to XML files
 * 			TODO Store all options in XML files
 * 			TODO Have key configuration load from XML on startup
 * 		Builder
 * 			TODO Make camera zoom go to 0 in free mode and have mouse wheel control speed instead
 * 			TODO Make grabbed entititys gravitate to the cursor
 * 			TODO Make a menu for placing new entities
 *			TODO Add a way to toggle rigidbody activation states
 *			TODO Right click to rotate, left click to move (mouse wheel while grabbed moves along Z)
 * Gameplay
 * 			TODO More weapons
 * 			TODO Story
 * 			TODO Multiplayer
 * 		AI
 * 			TODO Make some basic AI routines- seek, etc.
 * 			TODO Make our own scripting language? Find an already created one? Possibilities are endless.
 * Misc
 * 			TODO Whenever explosions get implemented, make sure to have the camera shakes if it's near one!
 */