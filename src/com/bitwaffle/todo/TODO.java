/*
 *	TODO, I've a feeling we're not in Kansas any more!
 *
 * Bugs
 * 			TODO After the first time, the fullscreen key has to be pressed twice to have an effect
 * 			TODO Destroy the Java AWT window when going into fullscreen, then create it again on leaving fullscreen
 * JBullet
 * 			TODO Fix physics debug drawing (might require fiddling with shaders)
 * 			TODO Give the sun an immovable physics object
 * 			TODO Have the LaserBullet be represented by a cone shape instead of a convex hull
 * 			TODO Add a thing to the Physics Debug that also draws an arrow representing the current speed (first point is entity origin, second is origin+linear velocity)
 * 			TODO Give things thrusters
 * 			TODO Figure out how to warp things
 * 			TODO Turrets (pivoting on joints)
 * 			TODO Machine gun that just sends out ray tests for bullets (end point for the ray tests should be randomly varied by small amounts based on how long the gun is being shot)
 * OpenAL
 * 			TODO Add more noises!
 * 			TODO Musics
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
 * 			TODO Mipmap textures so that they aren't as detailed from far away but are close up
 * 			TODO Inside the cockpit view!
 * 			TODO Get multiple lights working- have bullets give off a glow
 * 			TODO Bump-maps, normal maps
 * 			TODO Spotlights- especially ones attached to ships
 * 			TODO Shadows
 * 			TODO You know how in Super Smash Bros, when somebody is off screen there's a little circle with a rendering of them in it? That might be nice for finding ships.
 * 		Models
 * 			TODO Look into having ships stored as a zip file containing an obj, a png, and an xml file (is it too slow? easier if its uncompressed?)
 * 			TODO Right now, it assumes that the obj file has vertices, normals, and texture coordinates in it.
 * 				 If any of those are not present, it will not work at all. Ideally, if normals aren't present they should be calculated.
 * 			TODO More powers! Things like initial rotation, center offset, etc. (keep in mind that these also have to be translated to bullet)
 *			TODO Make a model importer that takes in an obj file and lets you edit the mtl file and see how it would look in-game (look into blender plugins)
 * 			TODO Create an AnimatedModel class that has a an addFrame(Frame frame) and int curFrame.
 * System
 * 		General
 * 			TODO Make methods in the QuaternionHelper to take javax.vecmath.Vector3f and javax.vecmath.Quat4f objects (but still do the same math on them)
 * 			TODO Make a loading screen!
 * 			TODO Add a "/save" command that saves the game and return to the main menu
 * 		Console
 * 			TODO Chat color parsing
 * 			TODO Make ctrl+v work with the console
 * 			TODO Add tabs to the console (one for chat, one for commands, one for combat, etc.)
 * 			TODO Add tabs (the character) to the console- set tabstop command?
 * 			TODO System console! One that comes down from the top of the screen
 * 		XML
 * 			TODO Figure out how to write to XML files
 * 			TODO Store all options in XML files
 * 			TODO Have key configuration load from XML on startup
 * 		Builder
 * 			TODO Make a CameraMode class that keeps track of things like max zoom, min zoom, offsets, etc (then it will just be set whenever the mode is switched! Make a CameraModes enum)
 * 			TODO Make the item at the crosshair glow with an outline, like in Left 4 Dead (this will require writing a new shader)
 * 			TODO Make grabbed entitities gravitate to the cursor
 *		Launcher
 *			TODO Make it so that the game opens in the same window as the launcher (how does Minecraft do it?)
 *			TODO If the launcher can't get write access to the home directory, have it ask the user where to save the game to (rather than having it be passed in via args)
 *			TODO Give an option to un-install the game
 *			TODO Users should be able to start the game through the launcher or by just running spaceout.jar
 *				 This means that signing in will have to occur in-game and if the user logs in from the launcher,
 *				 it would start the game with args for the user. Maybe files should be downloaded in-game, too,
 *				 so that if there's an update it would download when the game starts. Although it might also be 
 *				 good to have all file downloading done through the launcher.
 *			TODO A good way to handle updates would be to have one .spaceout.zip on the server with all the latest files (so anyone without the files could just download the latest version) but then also keep around
 *				 archives specifically for updating from one version to another version, so that the whole game doesn't need to be downloaded if just one or two things are being changed.
 * Gameplay
 * 			TODO More weapons
 * 			TODO Story
 * 			TODO Multiplayer
 * 		Control
 * 			TODO Xbox 360 controller support
 * 		AI
 * 			TODO Make some basic AI routines- seek, etc.
 * 			TODO Figure out Lua parsing
 * Misc
 * 			TODO Whenever explosions get implemented, make sure to have the camera shakes if it's near one!
 */
package com.bitwaffle.todo;
public class TODO{}