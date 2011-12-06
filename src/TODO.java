/*
 * Collision branch TODO
 * TODO Check out JBullet!!!!!
 * Maybe do these:
 * TODO When the model loader is building the object from the file, it needs to keep track of all the min/max x/y/z coordinates
 * TODO Those min/max coordinates can then be used to make the bounding box for the object
 * TODO Find a way to rotate those boxes with the quaternions (there's equations for it)
 * TODO Find out how to see if two boxes have hit each other
 * 
 * GUI branch TODO
 * TODO Make draggable boxes
 * TODO Maybe make everything drawn by call lists instead of point-by-point-per-frame. Would it really be worth the performance increase? Probably not.
 * TODO Make an XML selection screen that checks the directory for XML files and lets you select one to load
 * TODO Make an options menu for the menu screen. Have as many graphics options as possible.
 * TODO Might be a good idea to have the Button constructor take a scale in to control how big the image is (xScale and yScale) rather than the size of the image
 * TODO Give the main menu's start button its own image
 * 
 * ModelLoader TODO
 * TODO Make it handle materials
 * TODO Make it handle multiple objects in one file
 * TODO Right now, it assumes that the obj file has vertices, normals, and texture coordinates in it.
 * If any of those are not present, it will not work at all. Ideally, if normals aren't present they should be calculated
 * TODO Give it more powers! Things like initial rotation, center offset, etc. (keep in mind that these also have to be translated to the physics engine)
 * TODO Figure out how to create a CollisionObject with the vertex data
 *
 * In sort of order of importance:
 * TODO After the first time, the fullscreen key has to be pressed twice to have an effect
 * TODO Enhance acceleration. Make it so that everything slows down at the same speed.
 * TODO Also, the acceleration is not rotation independent. I.E. if you look down and speed up forward, then stop accelerating and look somewhere else, 
 * you continue going in the direction you're looking (instead of going the direction you were looking at before)
 * TODO create the rest of the solar system
 * TODO ConsoleCommands.java is pretty damn ugly still!
 * TODO In DebugKeyManager there are keys that don't go through the KeyboardManager
 * TODO Have all the data managers (ModelManager, TextureManager) keep track of how many objects are using the data. If none are for an extended period of time, delete them! (Be careful with this one!)
 * TODO It might also be a good idea to replace everything that's references as an int (in ModelHandler and TextureHandler) and replace them with enumerations, but it's really no big deal
 * TODO Create an AnimatedModel class that has a call list for every frame (probably in a good old ArrayList) Give it addFrame(int callList) and int curFrame.
 * TODO It would make sense to have multiple types of Entities- ones that are solely for rendering (debris, skybox), and ones that can collide with stuff
 * TODO create a Ship class that the player uses. Create a Gun class that the Ship uses to fire bullets.
 * 
 * TODO planets are impossible to find! Find a way to billboard text and have it drawn over a planet (text size probably based on distance from planet)
 * TODO Make the skybox a box instead of a sphere
 * TODO Make geometry (spheres specifically, but could also go for stars) draw with less polygons when you're far away and more the closer you get
 * TODO Mipmap textures so that they aren't as detailed from far away but are close up
 * TODO Make it so that only things that are visible are drawn (i.e. stuff behind the camera wouldn't be drawn)
 * TODO Make everything that extends 'Light' have a 'light' int that points to one of LIGHT0-LIGHT8
 * TODO Right now, everything simply has a name string. It would make more sense to reference objects by some sort of hash tag
 * TODO Threads, threads and more threads! Make this more multi-processor friendly!
 * TODO Create a GUI (maybe using nifty gui? http://nifty-gui.lessvoid.com/)
 * TODO Whenever explosions get implemented, make sure to have the camera shake if it's near one!
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
 * -TODO If you're looking at an earlier line in the console, then submit something, it should jump back to the bottom
 * -TODO Add help for all the commands
 * -TODO Add tabs to the console (one for chat, one for commands, one for combat, etc.)
 * TODO XML
 * -TODO Figure out how to write to XML files
 * -TODO Store all options in XML files
 * -TODO Re-do Key Configuration so it loads from XML on startup.
 * -TODO Difficult: Re-do Console Commands so they load from XML, possibly writable with LUA 
 * 
 */