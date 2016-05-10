package gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.JOptionPane;

public class GuiLicenceChecker {
	
	static void checkLicenceDate()
	{
		// Die after May 05, 2016
		Calendar expireDate = Calendar.getInstance();
		// May is 4 (y, m, d) as Jan is 0
		expireDate.set(2016, 29, 5);
		// Get current date and compare
		if (Calendar.getInstance().after(expireDate)) {
			
			String msg = new String("Your licence has expired! Please contact the developer : evgeni.pirianov@gmail.com");
			JOptionPane.showMessageDialog(null, msg, "Licence Checker", JOptionPane.YES_NO_CANCEL_OPTION);
			//
			System.exit(0);
		}
	}
}
