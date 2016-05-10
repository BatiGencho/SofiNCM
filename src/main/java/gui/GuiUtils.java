package gui;

import java.awt.Color;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
 

public class GuiUtils {
	
    public final static String cdb = "cdb";
    public final static String pdf = "pdf";

    public static String getExtension(final File f) {
    	
    	if (f == null) return null;
    	
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
 
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    public static String getFileNameWithoutExtension(final File f) {
        String fname = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        fname = s.substring(0, i);
        return fname;
    }
 
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = GuiUtils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

}