/*
 *	TODO, I've a feeling we're not in Kansas any more!
 *
 * Temp
 * 			TODO Do at least one homework assignment today
 * Bugs
 * 			TODO After the first time, the fullscreen key has to be pressed twice to have an effect
 * 			TODO Destroy the Java AWT window when going into fullscreen, then create it again on leaving fullscreen
 * JBullet
 * 			TODO Update all non-dynamic entities by the amount of time passed in the physics sim (see DynamicEntityCallback.java)
 * 			TODO Fix physics debug drawing (might require fiddling with shaders)
 * 			TODO Add a thing to the Physics Debug that also draws an arrow representing the current speed (first point is entity origin, second is origin+linear velocity)
 * 			TODO Give things thrusters
 * 			TODO Figure out how to warp things
 * 			TODO Figure out how to get a ship to reorient itself to start going in a new direction when the direction it's facing is changed
 * 			TODO Third person ship controls like Halo- you look where you want to go and the ship turns towards it	
 * 			TODO Explosions! Use a sphere to see what objects are inside of the explosion and apply impulse accordingly
 * 			TODO Turrets (pivoting on joints)
 * 			TODO Maybe things shouldn't just float/spin forever (reduce velocities verrrrry slowly)
 * 			TODO Machine gun that just sends out ray tests for bullets (end point for the ray tests should be randomly varied by small amounts based on how long the gun is being shot)
 * OpenAL
 * 			TODO Decide on a format for sound files (wav? ogg?)
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
 * 			TODO Everything is impossible to find! Find a way to billboard an image and have it drawn over something (size probably based on distance from object)
 * 			TODO Mipmap textures so that they aren't as detailed from far away but are close up
 * 			TODO Antialiasing
 * 			TODO Inside the cockpit view!
 * 			TODO Particle effects- thrusters, explosions, etc.
 * 			TODO Get multiple lights working- have bullets give off a glow
 * 			TODO Spotlights- especially ones attached to ships
 * 			TODO Shadows
 * 			TODO You know how in Super Smash Bros, when somebody is off screen there's a little bubble with a rendering of them in it? That might be nice for finding ships.
 * 		Models
 * 			TODO Multiple objects in one file
 * 			TODO Right now, it assumes that the obj file has vertices, normals, and texture coordinates in it.
 * 			If any of those are not present, it will not work at all. Ideally, if normals aren't present they should be calculated
 * 			TODO More powers! Things like initial rotation, center offset, etc. (keep in mind that these also have to be translated to bullet)
 * 			TODO Bump-maps, normal maps
 * 			TODO Create an AnimatedModel class that has a an addFrame(Frame frame) and int curFrame.
 * System
 * 		General
 * 			TODO Debug class shouldn't really be all static- variables are being hammered every update (in the init() method)
 * 			TODO Make methods in the QuaternionHelper to take javax.vecmath.Vector3f and javax.vecmath.Quat4f objects (but still do the same math on them)
 * 			TODO Make a loading screen!
 * 			TODO Look into having ships stored as a zip file containing an obj, a png, and an xml file (is it too slow? easier if its uncompressed?)
 * 			TODO Add a "/save" command that saves the game and return to the main menu
 * 		Console
 * 			TODO Make up/down keys control chat history
 * 			TODO Save chat/console history to text file
 * 			TODO Chat color parsing
 * 			TODO Make ctrl+v work with the console
 * 			TODO Add tabs to the console (one for chat, one for commands, one for combat, etc.)
 * 			TODO System console! One that comes down from the top of the screen
 * 			TODO Use System.setOut(PrintStream out) to set System.out.println to print to the system console (might also be good to do System.setErr(PrintStream out) to print errors to the in-game console)
 * 		XML
 * 			TODO Figure out how to write to XML files
 * 			TODO Store all options in XML files
 * 			TODO Have key configuration load from XML on startup
 * 		Builder
 * 			TODO Make a CameraMode class that keeps track of things like max zoom, min zoom, offsets, etc (then it will just be set whenever the mode is switched! Make a CameraModes enum)
 * 			TODO Give the camera an object in the physics world so it bumps into things (figure out how to get it to collide, but not effect other things when it hits them) - might require a lot of fiddling with things!
 * 			TODO Make the crosshair behave differently when it's grabbing things (i.e. make it an open hand and when you grab something it changes)
 * 			TODO Make the item at the crosshair glow with an outline, like in Left 4 Dead (this will require writing a new shader)
 * 			TODO Make grabbed entitities gravitate to the cursor
 * 			TODO Make a menu for placing new entities (SPACEBAR while in free mode)
 *			TODO Make a model importer that takes in an obj file and lets you edit the mtl file and see how it would look in-game (look into blender plugins)
 * Gameplay
 * 			TODO More weapons
 * 			TODO Story
 * 			TODO Multiplayer
 * 		Control
 * 			TODO Xbox 360 controller support
 * 			TODO Make the mouse be represented by something that moves around the screen, and then turn the ship a different speed based on how far the mouse is from the middle of the screen.
 * 				 Would work for controllers, too! When the right thumbstick is moved, the thing on screen moves with it and when the stick is released it goes back to the middle.
 * 				 This could also be used for precision aiming, if un-projecting the mouse coordinates doesn't prove to be too hard
 * 		AI
 * 			TODO Make some basic AI routines- seek, etc.
 * 			TODO Make our own scripting language? Find an already created one? Possibilities are endless.
 * Misc
 * 			TODO Whenever explosions get implemented, make sure to have the camera shakes if it's near one!
 */