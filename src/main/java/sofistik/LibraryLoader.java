package sofistik;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

import gui.*;
import exception.*;

public class LibraryLoader
{
	public final static String pathToLibs = System.getProperty("user.dir") + "\\src\\main\\resources\\lib"; //relative to src
	private int[] strLines;
	
	public static void addLibDir(String pathToAdd) throws IOException {
	    try {
	    	
	    	//-------------------------------------------------------------------------------------------------------------
	        // This enables the java.library.path to be modified at runtime
	        // From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
	        //
//	        Field field = ClassLoader.class.getDeclaredField("usr_paths");
//	        field.setAccessible(true);
//	        String[] paths = (String[])field.get(null);
//	        List<String> pathsList = Arrays.asList(paths);
//	        if(!pathsList.contains(pathToAdd)){
//	        	pathsList.add(pathToAdd);
//	        	field.set(null,pathsList.toArray());
//	        	System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s);
//	        }
	    	//-------------------------------------------------------------------------------------------------------------
	    	
	    	
	    	// sources : http://fahdshariff.blogspot.de/2011/08/changing-java-library-path-at-runtime.html
	    	final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
	        usrPathsField.setAccessible(true);
	     
	        //get array of paths
	        final String[] paths = (String[])usrPathsField.get(null);
	     
	        //check if the path to add is already present
	        for(String path : paths) {
	            if(path.trim().equals(pathToAdd)) {
	                return;
	            }
	        }
	     
	        //add the new path
	        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
	        newPaths[newPaths.length-1] = pathToAdd;
	        usrPathsField.set(null, newPaths);

	    
	    } catch (IllegalAccessException e) {
	        throw new IOException("Failed to get permissions to set library path");
	    } catch (NoSuchFieldException e) {
	        throw new IOException("Failed to get field handle to set library path");
	    }
	}

	public LibraryLoader() throws ExceptionThrower //static constructor
	{
		//attempt to load lib path into java system paths
		try
		{			
			LibraryLoader.addLibDir(pathToLibs);
		} 
		catch (IOException ioe) 
		{
			throw new ExceptionThrower("Fatal error whilst opening the C++ based dll : ", new Throwable(ioe));
		}

		//path is ok, now try to open the library
		GuiMain.messageCentre.append("Opening C++ based CDB dll(s)..." + "\n");
		
		//look for exceptions when opening
		try
		{
			GuiMain.messageCentre.append("Re/Loading <qtcore_sofistik33_x64_d4.dll>" + "\n");
			//System.loadLibrary("qtcore_sofistik33_x64_d4");
			System.loadLibrary("\\qtcore_sofistik33_x64_d4.dll");
			GuiMain.messageCentre.append("Re/Loading <libifcoremdd.dll>" + "\n");
			System.loadLibrary("libifcoremdd");
			GuiMain.messageCentre.append("Re/Loading <libmmd.dll>" + "\n");
			System.loadLibrary("libmmd");
			GuiMain.messageCentre.append("Re/Loading <cdb_w33_x64d.dll>" + "\n");
			System.loadLibrary("cdb_w33_x64d");
			GuiMain.messageCentre.append("Re/Loading <ext_cdb_interface.dll>" + "\n");
			System.loadLibrary("ext_cdb_interface"); // Load native library at runtime, name is fixed
		}
		catch(SecurityException se)
		{
			throw new ExceptionThrower("Fatal error whilst opening the C++ based dll(s) : ",new Throwable(se));
		}
		catch(UnsatisfiedLinkError ule)
		{
			throw new ExceptionThrower("Fatal error whilst opening the C++ based dll(s) : ",new Throwable(ule));
		}

		GuiMain.messageCentre.append("Successfully re/loaded all C++ based CDB dlls !" + "\n");
	}
	//________________________________________________________________________

	
	// NATIVE METHODS INTERFACES
	public native void loadCdb(final String cdbName); //+all relevant data
	public native void closeCdb();
	public native int  getCdbStatus();
	//logger from the cpp dll
	public native String getMsgLoggerFromCppDll();
	public native int getSects();
	public native int getBeams();
	public native int getNodes();
	public native int getGroups();
	public native int getStructLines();
	public native int getLoadCases();
	public native int getMembers();
	public native int getMaterials();

}
