package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import javax.swing.*;
import javax.swing.filechooser.*;


//own packages
import sofistik.*;
import exception.*;

public class GuiMain extends JFrame 
{
	
	//public interfaces
	private static final long serialVersionUID = 1L;
	//all paths are relative to /bin
	public static final String sofLogoPath = "sofLogo.png";
	public static final String desktopIconPath = "desktopLogo.png";
	public static final String clearLogIconPath = "clearConsole.png";
	public static final String saveConsoleLogPath = "saveConsoleLog.png";
	public static final String exitPath = "exit.png";
	public static final String loadCdbPath = "loadCdb.png";
	public static final String goToAnalysisPath = "analyse.png";
	public static final String progName = "SofiNCM";
	public static final String progVer = "v1.0";
	public static double fScaleFactorMainWin = 0.9;
	//communication
	public static JNI_READ_STATUS jniStat;
	public static CDB_READ_STATUS cdbStat;
	public static StringBuilder messageCentre;
	
	//library interface
	public static LibraryLoader libInterface;
	public enum JNI_READ_STATUS
	{
		JNI_VOID, JNI_OPEN, JNI_FAILED, JNI_CLOSED
	};
	
	public enum CDB_READ_STATUS
	{
		CDB_VOID, CDB_OPEN, CDB_FAILED, CDB_CLOSED
	};
	
	
	//privates
	private URL imgURL;
	private Container guiMainContainer;
	private JPanel tfPanelNorth;
	private ImageIcon iconLogo;
	private Icon iconClearLog;
	private Icon iconSaveConsoleLog;	
	private Icon iconExit;
	private Icon iconLoadCdb;
	private Icon iconGoToAnalysis;
	private JLabel lblClearConsoleLog;
	private JLabel lblSaveConsoleLog;
	private JLabel lblExit;
	private JLabel lblLoadCdb;
	private JLabel lblGoToAnalysis;
	
	private JPanel tfPanelCentre;
	private JButton loadCdbButton;
	JButton preprocessAnalysisButton;
	JTextArea consoleTArea;
	private JPanel JPanelButtons;
	private JScrollPane tAreaScrollPane;
	
	private JPanel tfPanelSouth;
	private JButton exitButton;
	private JButton saveLogButton;
	private JButton clearConsoleLog;
	
	private StringBuilder consoleWelcomeMsg;
	private String pathToCdbFile;
	
	private JFileChooser fileCh;
	private int screenWidth;
	private int screenHeight;

    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;
	
    static String helpInformation;
	
	///////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////
	//static constructor
	static
	{
		//check the licence date
		GuiLicenceChecker.checkLicenceDate();
		

		GuiMain.messageCentre = new StringBuilder();
		GuiMain.libInterface = null;
		GuiMain.jniStat = JNI_READ_STATUS.JNI_VOID;
		GuiMain.cdbStat = CDB_READ_STATUS.CDB_VOID;
		
		GuiMain.helpInformation = "Nominal Curvature Method SofiNCM v.1.0\n"
								+ "Author : Evgeni Pirianov, 2016, München\n"
								+ "\n"
								+ "Copyright protection applies as to the author of the software.\n"
								+ "\n"
								+ "Program technical requirements.\n"
								+ "\n"
								+" 1. Only 3D structural members of type beams are allowed. Pipes, cables, trusses or rods and 2D structures are not allowed\n"
								+" 2. Only beams running along structural lines defined in SofiMshc are allowed\n"
								+" 3. Only reinforced concrete members (of various cross-sections) are allowed, other materials would yield an error such as timber or masonry\n"
								+" 4. Only prismatic members are allowed, tapered beams are not allowed as the standard design procedures by EN 1992 do not deal with such\n" 
								+" 5. All static/dynamic load cases/combinations (mechanical, temperature, imperfection, prestress, impulse, wind etc.) and MAXIMA are fully supported\n"
								+" 6. All I,II and III order analysis are allowed\n"
								+" 7. Construction stages and none-effective areas in the cross-sections shall be ignored, only total and final sections are relevant\n"
								+" 8. If a member has hinges or nodal springs, hinge reactions and displacements are ignored as the latter are not covered by EN 1992\n"
								+" 9. Nominal stiffness, standard methods have not been yet implemented\n"
								+" 10.Imperial Units are not supported. Only SI units shall be currently supported\n"
								+" 11.The analysis shall use the minimum member reinforcement of rang 0 defined in AQUA and ignore higher rangs\n"
								+" 12.Members with so called intermediate supports are not allowed as the standard design procedures by EC2 do not deal with such\n"
								+" 13.Normal force N,ed is smoothened along the member and thus assumed to be always constant\n"
								;
	}
	
	
	//normal GUI constructor
	public GuiMain()
	{
		//get the screen dimensions
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();  // Get the screen dimension
		this.screenWidth = dim.width;
		this.screenHeight = dim.height;
		
		//configure main screen JFrame window
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //close libs
	    this.setSize((int)(fScaleFactorMainWin*screenWidth), (int)(fScaleFactorMainWin*screenHeight));  // or pack() the components
	    this.setLocationRelativeTo(null);  // center the application window
	    //this.pack();
	    this.setVisible(true);             // show it
	    this.setTitle(progName.concat(" ").concat(progVer) + " Preprocessor");
		this.setPreferredSize(new Dimension((int)(fScaleFactorMainWin*screenWidth), (int)(fScaleFactorMainWin*screenHeight)));
		try {
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			//UIManager.put("swing.boldMetal", Boolean.FALSE);
			UIManager.setLookAndFeel((LookAndFeel) Class.forName("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel").newInstance());
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error opening Look and Feel Layout ", "GUI Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			//e.printStackTrace();
		}
		
	    //get the main container layout
		guiMainContainer = this.getContentPane();
		guiMainContainer.setLayout(new BorderLayout());
		fileCh = new JFileChooser();
		guiMainContainer.add(fileCh);
		
		//create the 3 main gui panels
		this.createPanelNorth();
		this.createPanelCentre();
		this.createPanelSouth();
		this.createMenusBar();
		
		//assemble panels in JFrame
	    guiMainContainer.add(tfPanelNorth,BorderLayout.NORTH);
	    guiMainContainer.add(tfPanelCentre,BorderLayout.CENTER);
	    guiMainContainer.add(tfPanelSouth,BorderLayout.SOUTH);
	    
	    //add a desktop logo image
	    ImageIcon desktopIcon = null;
	  	this.imgURL = getClass().getClassLoader().getResource(GuiMain.desktopIconPath);
	  	if (this.imgURL != null) {
	  		desktopIcon = new ImageIcon(this.imgURL);
	  		this.setIconImage(desktopIcon.getImage());
	  	} 
	    
	    //add a windows listener
	    this.addWindowListener(new WindowAdapter()
	    {
	    	@Override
	    	public void windowClosing(WindowEvent e) 
	    	{
	  			int answer = JOptionPane.showConfirmDialog(null, "Exit program ? ", "Confirm Dialog", JOptionPane.YES_NO_CANCEL_OPTION);
	  			if  (answer == JOptionPane.YES_OPTION)
	  			{
	  				if (libInterface!=null) libInterface.closeCdb(); //free LIb!
	  				consoleTArea.append("Successfully closed CDB dlls!" + "\n");
		    		System.exit(0);
	  			}
	  			else
	  			{
	  				GuiMain.this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	  			}
	    	}
	    });
	    
	}
	
	
	private void createMenusBar()
	{
	  	//--------------------------------------------------------
	  	//                   MAIN DIALOG MENUS                        
	  	//--------------------------------------------------------
		this.menuBar = new JMenuBar();
		
		this.menu = new JMenu("File");
		this.menuBar.add(menu); //ToDo
		
		this.menu = new JMenu("Help");
		this.menuItem = new JMenuItem("About");
		this.menu.add(menuItem);
		this.menuBar.add(menu);
		
		this.setJMenuBar(menuBar); //add to main Frame
		
	    this.menuItem.addActionListener(new ActionListener() {
	          @Override
	          public void actionPerformed(ActionEvent e) {
	        	  if (e.getSource()==menuItem)
	        	  {
	        		  JOptionPane.showMessageDialog(null , GuiMain.helpInformation , "Information", JOptionPane.PLAIN_MESSAGE);
	        	  }
	          }
	       });
	}

	private void createPanelNorth()
	{
	  	//--------------------------------------------------------
	  	//                   PANEL NORTH                          
	  	//--------------------------------------------------------
	  	//set the image Icon and image JLabel
	  	this.iconLogo = null;
	  	this.imgURL = getClass().getClassLoader().getResource(GuiMain.sofLogoPath);
	  	if (this.imgURL != null) {
	  		this.iconLogo = new ImageIcon(this.imgURL);
	  	} else {
	  		JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + GuiMain.sofLogoPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
	  		System.exit(1);
	  	}
	  	JLabel logoLabel = new JLabel(iconLogo, SwingConstants.CENTER);
	  	logoLabel.setSize(this.iconLogo.getIconWidth(), this.iconLogo.getIconWidth());

	  	//add the Jlabel in a JPanel
	  	tfPanelNorth = new JPanel(new GridLayout(1,0));
	  	//tfPanel.setPreferredSize(new Dimension(this.logoIcon.getIconWidth(), this.logoIcon.getIconWidth()));
	  	tfPanelNorth.setBorder(BorderFactory.createTitledBorder(""));
	  	tfPanelNorth.add(logoLabel);
		
	}
	
	private void createPanelCentre()
	{
		//--------------------------------------------------------
	  	//                   PANEL CENTRE                          
	  	//--------------------------------------------------------

	    ActionListener bListener = new ActionListener()
	    {
	    	@Override
	  		public void actionPerformed(ActionEvent e) 
	    	{
	  			
	  			if (fileCh==null) fileCh = new JFileChooser();
	  			
	  			if (e.getSource() == loadCdbButton)
	  			{
	  				
		  			 //if lib already opened, close first and reopen below
		  			if (GuiMain.jniStat == JNI_READ_STATUS.JNI_OPEN)
		  			{
		  				if (GuiMain.libInterface!=null && GuiMain.cdbStat == CDB_READ_STATUS.CDB_OPEN) 
		  				{
		  					GuiMain.libInterface.closeCdb();
		  					GuiMain.libInterface = null;
		  					GuiMain.jniStat = JNI_READ_STATUS.JNI_CLOSED;
		  				}
		  			}
		  			
		  			// now try to re/open it
		  			GuiMain.jniStat = JNI_READ_STATUS.JNI_OPEN;
	  				try
	  				{
	  					GuiMain.messageCentre.replace(0, messageCentre.length(), "");
	  					GuiMain.libInterface = new LibraryLoader(); //open the native library
	  				}
	  				catch (ExceptionThrower ext)
	  				{
	  					GuiMain.jniStat = JNI_READ_STATUS.JNI_FAILED;
	  					preprocessAnalysisButton.setEnabled(false);
	  					consoleTArea.append(GuiMain.messageCentre.toString());
	  					JOptionPane.showMessageDialog(null, "Could not load the JNI native library" + ext.getCauseStr(), "JNI native error", JOptionPane.ERROR_MESSAGE);
	  					consoleTArea.append("Could not load the JNI native library, could be missing ?" + ext.getCauseStr()+ "\n");
	  				}
	  				
	  				
	  				if (jniStat == JNI_READ_STATUS.JNI_OPEN)
	  				{
	  					consoleTArea.append(GuiMain.messageCentre.toString());
	  					
	  					int returnVal = fileCh.showOpenDialog(GuiMain.this);
	  					
	  		            if (returnVal == JFileChooser.APPROVE_OPTION) 
	  		            {
	  		            	//int rVal = fileCh.showSaveDialog(null);
	  		                File file = fileCh.getSelectedFile();
	  		                
	  		                consoleTArea.append("Opening: " + file.getName() + " on " + file.getAbsolutePath() + "\n");
	  		                String fileExt = GuiUtils.getExtension(file);
	  		                
	  		                if (fileExt==null || !file.isFile() || !fileExt.equals(GuiUtils.cdb))
	  		                {
	  		                	cdbStat = CDB_READ_STATUS.CDB_FAILED;
	  		                	consoleTArea.append("Illegal file format chosen [extension is not .cdb]" + "\n");
	  		                	JOptionPane.showMessageDialog(null, "Illegal file format chosen [extension is not .cdb]", "GUI error", JOptionPane.ERROR_MESSAGE);
	  		                	preprocessAnalysisButton.setEnabled(false);
	  		                }
	  		                else
	  		                {
	  		                	cdbStat = CDB_READ_STATUS.CDB_OPEN;
	  		                	pathToCdbFile = new String(file.getAbsolutePath());
	  		                	
	  		                	//run the opening in a new thread
	  			  				GuiProgressBar.doLoadCdbInProgressBar(pathToCdbFile,GuiMain.this);
	  			  				
	  			  				// ***************** USE IF PROBLEMS WITH THREADING *******************//
	  		                	//libInterface.loadCdb(pathToCdbFile); //ORIGINAL
	  		                	//print to log loaded data overview
//	  		                	consoleTArea.append("LOADED DATABASE TOPOLOGY : \n");
//	  		                	
//	  		                	consoleTArea.append("Structural Groups loaded : " +  libInterface.getGroups() + "\n");
//	  		                	consoleTArea.append("Structural Lines loaded : " + libInterface.getStructLines() + "\n");
//	  		                	consoleTArea.append("Structural Nodes loaded : " + libInterface.getNodes() + "\n");
//	  		                	consoleTArea.append("Structural Beam FE loaded : " + libInterface.getBeams() + "\n");
//	  		                	consoleTArea.append("Structural Sections loaded : " + libInterface.getSects() + "\n");
//	  		                	consoleTArea.append("Load Cases loaded : " + libInterface.getLoadCases() + "\n");
//	  		                	
//	  		                	consoleTArea.append("==> Generated Analysis Members = " + libInterface.getMembers() + "\n");
//	  		                	//catch....
//	  		                	preprocessAnalysisButton.setEnabled(true);
	  			  				// ***************** USE IF PROBLEMS WITH THREADING *******************//
	  		                }

	  		            }
	  		            
	  		            if (jniStat == JNI_READ_STATUS.JNI_OPEN && cdbStat == CDB_READ_STATUS.CDB_OPEN && fileCh.getSelectedFile()!=null) {
	  		            fileCh.setSelectedFile(fileCh.getSelectedFile()); 
	  		            }
	  		            else {
	  		            	fileCh.setSelectedFile(null);
	  		            }
	  		            
	  		          	consoleTArea.setCaretPosition(consoleTArea.getDocument().getLength());
	  				}
	  			}
	  			
	  			if (e.getSource()==preprocessAnalysisButton) 
	  			{
	  				//to be implemented, must be also visible
	  				if (jniStat == JNI_READ_STATUS.JNI_OPEN && cdbStat==CDB_READ_STATUS.CDB_OPEN)
	  				{
	  					//set the static analysis opts to default values
	  					GuiAnalysisParams.setAllGuiAnalysisParamsToDefault();					
	  					
	  					//start the gui analysis and disable the main gui
	  					consoleTArea.append("Entering the Analysis Mode... " + "\n");
	  					GuiMain.this.setEnabled(false);
	  					new GuiAnalysis(GuiMain.this);
	  				}
	  				else
	  				{
	  					JOptionPane.showMessageDialog(null, "Either JNI libs or the cdb are faulty. Please reload them.", "GUI error", JOptionPane.ERROR_MESSAGE);
  						consoleTArea.append("Either JNI libs or the cdb are faulty. Please reload them. " + "\n");
	  				}
	  			}
	    	}
	    };

	  	
	  	if (GuiMain.jniStat == JNI_READ_STATUS.JNI_VOID && GuiMain.cdbStat == CDB_READ_STATUS.CDB_VOID)
	  	{//build and display only once upon setting the gui for the first time
	  	 //only once per program start // /throw EXCEPTIONS catch?
	  		this.initConsoleWelcomeMsg();
	  	}
	  	
		
	  	
	  	this.consoleTArea = new JTextArea(this.consoleWelcomeMsg.toString(),25,100);
	  	this.consoleTArea.setFont(new Font("Serif", Font.BOLD, 15));
	  	this.consoleTArea.setLineWrap(true);       // wrap line
	  	this.consoleTArea.setWrapStyleWord(true);  // wrap line at word boundary
	  	//this.consoleTArea.setMargin(new Insets(5,5,5,5));
	  	this.consoleTArea.setBackground(Color.BLACK); // light blue
	  	this.consoleTArea.setForeground(Color.YELLOW); // light blue
	  	this.consoleTArea.setEditable(false);
	  	//this.consoleTArea.setBorder(BorderFactory.createTitledBorder(""));
	  	this.tAreaScrollPane = new JScrollPane(consoleTArea); // Wrap the JTextArea inside a JScrollPane
	  	this.tAreaScrollPane.setBorder(BorderFactory.createTitledBorder("Console Output"));
	  	this.tAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	  	this.tAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	  	
	  	
	  	this.imgURL = getClass().getClassLoader().getResource(loadCdbPath);
	  	if (this.imgURL != null) {
	  		this.iconLoadCdb = new ImageIcon(this.imgURL);
	  	} else {
	  		JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + exitPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
	  		System.exit(1);
	  	}
	  	this.lblLoadCdb = new JLabel("Load Cdb", this.iconLoadCdb, SwingConstants.CENTER);
	  	
	  	this.loadCdbButton = new JButton();
	  	this.loadCdbButton.add(this.lblLoadCdb);
	  	this.loadCdbButton.setToolTipText("Load the selected CDB");
	  	this.loadCdbButton.setEnabled(true);
	  	this.loadCdbButton.setVerticalTextPosition(SwingConstants.CENTER);
	  	this.loadCdbButton.setHorizontalTextPosition(SwingConstants.CENTER);
	  	this.loadCdbButton.addActionListener(bListener);
	  	JPanel JpanelLoadCdbButton = new JPanel(new FlowLayout());
	  	JpanelLoadCdbButton.add(this.loadCdbButton);
	  	
	  	
	  	this.imgURL = getClass().getClassLoader().getResource(goToAnalysisPath);
	  	if (this.imgURL != null) {
	  		this.iconGoToAnalysis = new ImageIcon(this.imgURL);
	  	} else {
	  		JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + goToAnalysisPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
	  		System.exit(1);
	  	}
	  	this.lblGoToAnalysis = new JLabel("To Analysis", this.iconGoToAnalysis, SwingConstants.CENTER);
  	
	  	this.preprocessAnalysisButton = new JButton();
	  	this.preprocessAnalysisButton.add(this.lblGoToAnalysis);
	  	this.preprocessAnalysisButton.setToolTipText("Go to Analysis");
	  	this.preprocessAnalysisButton.setEnabled(false);
	  	this.preprocessAnalysisButton.setVerticalTextPosition(SwingConstants.CENTER);
	  	this.preprocessAnalysisButton.setHorizontalTextPosition(SwingConstants.CENTER);
	  	this.preprocessAnalysisButton.addActionListener(bListener);
	  	JPanel JpanelpreprocessAnalysisButton = new JPanel(new FlowLayout());
	  	JpanelpreprocessAnalysisButton.add(this.preprocessAnalysisButton);
	  	
	  	this.JPanelButtons = new JPanel(new BorderLayout());
	    this.JPanelButtons.setBorder(BorderFactory.createTitledBorder("Navigator"));
	  	this.JPanelButtons.add(JpanelLoadCdbButton,BorderLayout.NORTH);
	  	this.JPanelButtons.add(JpanelpreprocessAnalysisButton,BorderLayout.CENTER);
	    
	  	this.tfPanelCentre = new JPanel(new BorderLayout());
	  	this.tfPanelCentre.add(this.tAreaScrollPane,BorderLayout.CENTER);
	  	this.tfPanelCentre.add(JPanelButtons,BorderLayout.EAST);
	}
	
	private void createPanelSouth()
	{
	  	//--------------------------------------------------------
	  	//                   PANEL SOUTH                          
	  	//--------------------------------------------------------
		//set the Exit button
		
		this.imgURL = getClass().getClassLoader().getResource(exitPath);
		if (this.imgURL != null) {
			this.iconExit = new ImageIcon(this.imgURL);
		} else {
			JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + exitPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		this.lblExit = new JLabel("Exit", this.iconExit, SwingConstants.CENTER);

	  	exitButton = new JButton();
	  	exitButton.add(this.lblExit);
	  	exitButton.setPreferredSize(new Dimension(screenWidth/20,screenHeight/20));
	  	exitButton.setToolTipText("Exit program");
	  	exitButton.setEnabled(true);
	  	exitButton.setVerticalTextPosition(SwingConstants.CENTER);
	  	exitButton.setHorizontalTextPosition(SwingConstants.CENTER);
	  	exitButton.addActionListener(new ActionListener()
	  	{
	  		@Override
	  		public void actionPerformed(ActionEvent e) {
	  			//close libs?
	  			int answer = JOptionPane.showConfirmDialog(null, "Exit program ? ", "Confirm Dialog", JOptionPane.YES_NO_CANCEL_OPTION);
	  			if  (answer == JOptionPane.YES_OPTION)
	  			{
	  				if (libInterface!=null) libInterface.closeCdb();
	  				consoleTArea.append("Successfully closed CDB dlls!" + "\n");
	  				System.exit(0);
	  			}
	  			else
	  			{
	  				GuiMain.this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	  			}
	  		}
	  	}
	  	);
	  	
	  	
		//set saveLog button  	
	  	this.imgURL = getClass().getClassLoader().getResource(saveConsoleLogPath);
	  	if (this.imgURL != null) {
	  		this.iconSaveConsoleLog = new ImageIcon(this.imgURL);
	  	} else {
	  		JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + saveConsoleLogPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
	  		System.exit(1);
	  	}
	  	this.lblSaveConsoleLog = new JLabel("Save Console Log", this.iconSaveConsoleLog, SwingConstants.CENTER);
	  	
	  	this.saveLogButton = new JButton();
	  	this.saveLogButton.add(this.lblSaveConsoleLog);
	  	this.saveLogButton.setPreferredSize(new Dimension(screenWidth/10,screenHeight/20));
	  	this.saveLogButton.setToolTipText("Save the console Log file");
	  	this.saveLogButton.setEnabled(true);
	  	this.saveLogButton.setVerticalTextPosition(SwingConstants.CENTER);
	  	this.saveLogButton.setHorizontalTextPosition(SwingConstants.CENTER);
	  	this.saveLogButton.addActionListener(new ActionListener()
	  	{
	  		@Override
	  		public void actionPerformed(ActionEvent e)
	  		{

	  			if (fileCh==null) fileCh = new JFileChooser();
	  			int returnVal = fileCh.showOpenDialog(GuiMain.this);

	  			if (returnVal == JFileChooser.APPROVE_OPTION) 
	  			{
	  				File file = fileCh.getSelectedFile();
	  				consoleTArea.append("Saving the console log as : " + file.getName() + " to " + file.getPath() + "\n");
	  				
	  				//if (file.createNewFile())
	  				//{
	  					PrintStream prtStream = null;
	  					FileReader fileReader = null;
	  					BufferedReader bufferedReader = null;
	  					boolean isOk = true;
	  					try
	  					{
	  						prtStream = new PrintStream(file,"UTF-8");
	  						fileReader = new FileReader(file);
	  						bufferedReader = new BufferedReader(fileReader);
	  						prtStream.append(consoleTArea.getText());
	  					} 
	  					catch (FileNotFoundException | UnsupportedEncodingException e1)
	  					{
	  						isOk=false;
	  						JOptionPane.showMessageDialog(null, "Cannot save log to current file " + file.getName() +
	  								" on path " +  file.getPath() , "GUI error", JOptionPane.ERROR_MESSAGE);
	  						consoleTArea.append("Saving the console log failed : " + file.getName() + " to " + file.getPath() + "\n");
	  					}
	  					finally
	  					{
	  						try {
	  							prtStream.close();
	  							bufferedReader.close();
	  							fileReader.close();
	  						} catch (IOException e1) {
	  							isOk = false;
	  							JOptionPane.showMessageDialog(null, "Cannot close the writing streams ", "GUI error", JOptionPane.ERROR_MESSAGE);
	  							consoleTArea.append("Failed closing the write streams " + "\n");
	  						}
	  						if (isOk) consoleTArea.append("Console successfully saved under : " + file.getName() + " on " + file.getPath() + "\n");
	  					}
	  				}
	  			//}
	  			fileCh.setSelectedFile(null);
	  			consoleTArea.setCaretPosition(consoleTArea.getDocument().getLength());
	  			/////////////////////////
	  		}
	  	}
	  	);
	  	
	  	this.imgURL = getClass().getClassLoader().getResource(clearLogIconPath);
	  	if (this.imgURL != null) {
	  		this.iconClearLog = new ImageIcon(this.imgURL);
	  	} else {
	  		JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + clearLogIconPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
	  		System.exit(1);
	  	}
	  	this.lblClearConsoleLog = new JLabel("Clear Console Log", this.iconClearLog, SwingConstants.CENTER);
	  	
	  	this.clearConsoleLog = new JButton();
	  	this.clearConsoleLog.add(this.lblClearConsoleLog);
	  	this.clearConsoleLog.setPreferredSize(new Dimension(screenWidth/10,screenHeight/20));
	  	this.clearConsoleLog.setToolTipText("Cler the Console Log file");
	  	this.clearConsoleLog.setEnabled(true);
	  	this.clearConsoleLog.setVerticalTextPosition(SwingConstants.CENTER);
	  	this.clearConsoleLog.setHorizontalTextPosition(SwingConstants.CENTER);
	  	this.clearConsoleLog.addActionListener(new ActionListener()
	  	{
	  		@Override
	  		public void actionPerformed(ActionEvent e)
	  		{
	  			consoleTArea.setText("");
	  			consoleTArea.validate();
	  		}
  		});

	  	//add buttons the the south panel
	  	tfPanelSouth = new JPanel(new FlowLayout());
	  	tfPanelSouth.setBorder(BorderFactory.createTitledBorder(""));
	  	tfPanelSouth.add(exitButton);
	  	tfPanelSouth.add(saveLogButton);
	  	tfPanelSouth.add(clearConsoleLog);
	}
	
	
	
	private void initConsoleWelcomeMsg()
	{
		
		this.consoleWelcomeMsg = new StringBuilder();
		
		this.consoleWelcomeMsg.append( "Loading local system settings ...." + "\n");
		
		this.consoleWelcomeMsg.append( "OS name : " + System.getProperty("os.name") + "\n");
		this.consoleWelcomeMsg.append( "OS version : " + System.getProperty("os.version") + "\n");
		this.consoleWelcomeMsg.append( "OS arch : " + System.getProperty("os.arch") + "\n");
		this.consoleWelcomeMsg.append("Processor arch : " + System.getenv("PROCESSOR_ARCHITECTURE") + "\n");
		this.consoleWelcomeMsg.append("Processor type  : " + System.getenv("PROCESSOR_IDENTIFIER")  + "\n");
		if (System.getenv("PROCESSOR_ARCHITEW6432")!=null) this.consoleWelcomeMsg.append(" Processor type (32x64)  : " + System.getenv("PROCESSOR_ARCHITEW6432") + "\n");
		this.consoleWelcomeMsg.append("Physical cores : " + System.getenv("NUMBER_OF_PROCESSORS") + "\n");
		this.consoleWelcomeMsg.append("Java version : " +  System.getProperty("java.version") + "\n");
		/* Total number of processors or cores available to the JVM */
		this.consoleWelcomeMsg.append("JVM Available processors (cores): " + Runtime.getRuntime().availableProcessors() + "\n");
	    /* Total amount of free memory available to the JVM */
		this.consoleWelcomeMsg.append("JVM Free memory (bytes): " +  Runtime.getRuntime().freeMemory() + "\n");
		/* Total memory currently available to the JVM */
		this.consoleWelcomeMsg.append("Total memory available to JVM (bytes): " + Runtime.getRuntime().totalMemory() + "\n");
		/* Maximum amount of memory currently available to the JVM */
		this.consoleWelcomeMsg.append("Maximum memory available to JVM (bytes): " + Runtime.getRuntime().maxMemory() + "\n");
		this.consoleWelcomeMsg.append("Default program working directory:  " + System.getProperty("user.dir") + "\n");
		//print date and time
		this.consoleWelcomeMsg.append(new Date() + " " + Locale.getDefault() + "\n");
		
		this.consoleWelcomeMsg.append("System settings all loaded successfully!\n");
	}
	
	public static void main(String[] args) 
	{
	      SwingUtilities.invokeLater(new Runnable() 
	      {
	         @Override
	         public void run() {
	        	GuiMain guiMainThread = new GuiMain(); // construct the gui and get system params
	         }
	      });
	   }

}
