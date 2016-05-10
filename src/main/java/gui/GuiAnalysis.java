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
import java.util.TreeSet;
import javax.swing.*;

import gui.TableListSelection;

//own packages
import sofistik.*;
import sofistik.AnalysisMemberEC3Analysis.AnalysisState;



public class GuiAnalysis extends JFrame
{
	
	private static final long serialVersionUID = 1L;
	private Container guiAnalysisMainContainer;
	static int screenWidth;
	static int screenHeight;
	private final GuiMain pGuiMain;
	public static final String generateOutputPdfPath = "outputToPdf.png";
	public static final String loadCasesPath = "loadCases.png";
	public static final String analysisParamsPath = "analysisParams.png";
	public static final String printMemberTopologyPath = "printTopology.png";
	public static final String analysePath = "analyse.png";
	public static final String saveConsoleLogPath = "saveConsoleLog.png";
	public static final String clearConsoleLogPath = "clearConsole.png";
	public static final String backToPreprocessorPath = "backToPreprocessor.png";
	public static final String showGraphicPath = "showGraphic.png";
	
	private URL imgURL;
	private ImageIcon desktopImage;
	private Icon iconGenerateOutputPdf;
	private JLabel lblGenerateOutputPdf;
	private Icon iconLoadCases;
	private JLabel lblLoadCases;
	private JLabel lblLoadCasesII;
	private Icon iconAnalysisParams;
	private JLabel lblAnalysisParams;
	private Icon iconPrintMemberTopology;
	private JLabel lblPrintMemberTopology;
	private Icon iconAnalyse;
	private JLabel lblAnalyse;
	private Icon iconSaveConsoleLog;
	private JLabel lblSaveConsoleLog;
	private Icon iconClearConsoleLog;
	private JLabel lblClearConsoleLog;
	private Icon iconBackToPreprocessor;
	private JLabel lblBackToPreprocessor;
	private Icon iconShowGraphic;
	private JLabel lblShowGraphic;
	
	//------PANEL NORTH-------
	private JPanel pnlNorth;
	//struct line
	private JComboBox<String> cbStrLines;
	private JLabel lStrLines;

	//group combo box
	private JComboBox<String> cbGroups;
	private JLabel lGroups;
	private JButton btnLoadCases;
	private JButton btnLoadCasesII;
	private ComboBoxActionListener cbActionListener;
	private ComboBoxItemListener cbItemListener;

	//------PANEL WEST-------
	private JRadioButton rbSimplMethod;
	private JRadioButton rbNomStiffMethod;
	private JRadioButton rbNomCurvMethod;
	private JRadioButton rbGeneralMethod;
	private JPanel pnlWest;//to hold all components
	private ButtonGroup btnGroupAnalysisOptions;
	private ButtonGroup btnGroupLevelOfOutput;
	private JRadioButton rbBasicOutput;
	private JRadioButton rbDetailedOutput;
	private JPanel pnlWestOutput;
	private JPanel pnlWestMethod;
	
	//------PANEL EAST-------
	JButton btnDisplayMemberAnalysisData;
	JButton btnShowGraphic;
	JButton btnAnalyseMember;
	JButton btnAnalysisParams;
	private JPanel  pnlEast;
	
	//------PANEL SOUTH-------
	private JButton btnGeneratePdfWithChecks;
	private JButton btnBackToMainGui;
	private JButton btnSaveConsoleLog;
	private JButton btnClearConsole;
	private JFileChooser fileCh;
	private JPanel pnlSouth;
	
	//------PANEL CENTRE-------
	public JTextArea analysisOutputArea;
	private JScrollPane analysisAreaScrollPane;
	private JPanel pnlCentre;
	private JPanel pnlVisualisation;
	private GuiColumnVisualisation columnVisualiser;
	private GuiSectionVisualisation sectionVisualiser;
	static final int COLUMN_VISUALISATION_WIDTH  = 200;
	static final int COLUMN_VISUALISATION_HEIGHT = 400;
	static final int SECTION_VISUALISATION_WIDTH  = 200;
	static final int SECTION_VISUALISATION_HEIGHT = 200;
	
	//------MENUS---------
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;
	
	public GuiAnalysis(GuiMain gm)
	{
		//init the gui frame
		this.guiAnalysisPrepareOverviewData();
		
		//get the screen dimensions and setup main frame
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();  // Get the screen dimension
		GuiAnalysis.screenWidth = dim.width;
		GuiAnalysis.screenHeight = dim.height;
		pGuiMain = gm;

		this.addWindowListener(new WindowAdapter()
		{
	         @Override
	         public void windowClosing(WindowEvent evt) {
	        	 
	        	 int opt = JOptionPane.showConfirmDialog(null, "Leaving the analysis part will reset all current options and settings", "GUI Message", JOptionPane.OK_CANCEL_OPTION);
	        	 if (opt == JOptionPane.OK_OPTION) {
	        		 pGuiMain.setEnabled(true); //set the main gui back on to active
	        		 pGuiMain.setVisible(true);
	        		 pGuiMain.setAutoRequestFocus(true);
	        	 }
	        	 else if (opt == JOptionPane.CANCEL_OPTION)
	        	 {	 
	        		 GuiAnalysis.this.setVisible(true);
	        	 }
	         }
		}
		);
		
		this.setSize(new Dimension((int)(GuiMain.fScaleFactorMainWin*screenWidth), (int)(GuiMain.fScaleFactorMainWin*screenHeight)));
		this.setLocationRelativeTo(null);  // center the application window
		this.setVisible(true);             // show it
		this.setTitle(GuiMain.progName.concat(" ").concat(GuiMain.progVer) + " Analysis");
		this.setPreferredSize(new Dimension((int)(GuiMain.fScaleFactorMainWin*screenWidth), (int)(GuiMain.fScaleFactorMainWin*screenHeight)));
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
		this.guiAnalysisMainContainer = this.getContentPane();
		this.guiAnalysisMainContainer.setLayout(new BorderLayout());
		this.fileCh = new JFileChooser();
		this.guiAnalysisMainContainer.add(fileCh);
		
		//set up the north panel (str lines, load cases and groups)
		this.setUpPanelNorth();
		
		//set up the west panel (analysis options)
		this.setUpPanelWest();
		
		//set up the south panel (cancel, save, generate pdf)
		this.setUpPanelSouth();
		
		//set up the east panel (display member data, analyse member)
		this.setUpPanelEast();
		
		//set up the analysis output area and the small element graphic
		this.setUpPanelCentre();
		
		//assemble panels in JFrame
		this.add(pnlNorth,BorderLayout.NORTH);
		this.add(pnlWest,BorderLayout.WEST);
	    this.add(pnlSouth,BorderLayout.SOUTH);
	    this.add(pnlEast,BorderLayout.EAST);
	    this.add(pnlCentre,BorderLayout.CENTER);
	    //add the menus bar
	    this.createMenusBar();
	    
	    //add the desktop logo icon
	    this.desktopImage = null;
	  	URL imgURL = getClass().getClassLoader().getResource(GuiMain.desktopIconPath);
	  	if (imgURL != null) {
	  		this.desktopImage = new ImageIcon(imgURL);
	  		this.setIconImage(this.desktopImage.getImage());
	  	}

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
	        		  JOptionPane.showMessageDialog(null, GuiMain.helpInformation, "Information", JOptionPane.PLAIN_MESSAGE);
	        	  }
	          }
	       });
	}
	
	private void guiAnalysisPrepareOverviewData()
	{
		//construct the members set and allocate capacity
		AnalysisMembersCollection membersSet = new AnalysisMembersCollection(GuiMain.libInterface.getMembers());
	    //init the beam set elements
	    AnalysisMembersCollection.createBeamSet();
	    
	    //the first time the gui is constructed, ensure clearing the static select options
	    AnalysisOptions.selectedAnalysisOption = AnalysisMethods.NOMINAL_CURVATURE_METHOD; //default one
	    AnalysisOptions.selectedOutputLevel = AnalysisOutputLevel.DETAILED; //default
	    AnalysisOptions.strLinesStringArray = AnalysisMembersCollection.getDataToStringArray(1);
	    AnalysisOptions.selectedGroup = -1;
	    AnalysisOptions.selectedStrLine = -1;
	    AnalysisOptions.selectedLinearLoadCases = new TreeSet<Integer>();
	    AnalysisOptions.selectedNoneLinearLoadCases = new TreeSet<Integer>();
	    
	    //action listeners for combo boxes
	    this.cbActionListener = new ComboBoxActionListener();
	    this.cbItemListener = new ComboBoxItemListener();
	}
	
    private class ComboBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
        	
        	if (evt.getSource() == cbStrLines && cbStrLines.getSelectedItem().equals("select"))
        	{
        		String[] def = new String[]{"select"};
        		
        		cbStrLines.setModel(new DefaultComboBoxModel<String>((String[]) AnalysisOptions.strLinesStringArray));
        		
        		cbGroups.setModel(new DefaultComboBoxModel<String>(def));
        		
        		AnalysisOptions.selectedGroup = -1;
        		AnalysisOptions.selectedStrLine = -1; 
        		btnAnalyseMember.setEnabled(false);
        		btnAnalysisParams.setEnabled(false);
        		btnDisplayMemberAnalysisData.setEnabled(false);
        	}
        	
	   		 if (evt.getSource() == cbGroups && !cbGroups.getSelectedItem().equals("select"))
	   		 {
	   			 AnalysisOptions.selectedGroup = Integer.parseInt((String)cbGroups.getSelectedItem());
	   			 if (!AnalysisOptions.selectedLinearLoadCases.isEmpty() && AnalysisOptions.selectedStrLine!=-1 && AnalysisOptions.selectedGroup!=-1)
	   			 {
	   				 btnAnalyseMember.setEnabled(true);
	   				 btnAnalysisParams.setEnabled(true);
	   				 btnDisplayMemberAnalysisData.setEnabled(true);
	   			 }
	   			 else
	   			 {
	   				btnAnalyseMember.setEnabled(false);
	   				btnAnalysisParams.setEnabled(false);
	   				btnDisplayMemberAnalysisData.setEnabled(false);
	   			 }
	   		 }
	   		
	   		//clear the table of selected load cases
     		if (AnalysisOptions.selectedLinearLoadCases != null) AnalysisOptions.selectedLinearLoadCases.clear();
     		if (AnalysisOptions.selectedNoneLinearLoadCases != null) AnalysisOptions.selectedNoneLinearLoadCases.clear();
     		if (TableListSelection.tableListUserSelectedLoadCases != null) TableListSelection.tableListUserSelectedLoadCases.clear();

        }
    }

    private class ComboBoxItemListener implements ItemListener 
    {
        @Override
        public void itemStateChanged(ItemEvent evt)
        {
        	if (evt.getStateChange() == ItemEvent.SELECTED) 
        	{

        		if (evt.getSource() == cbStrLines && !cbStrLines.getSelectedItem().equals("select"))
        		{
        			cbStrLines.setSelectedItem(cbStrLines.getSelectedItem());
        			AnalysisOptions.selectedStrLine = Integer.parseInt((String)cbStrLines.getSelectedItem());

        			//clear the table of selected load cases
        			AnalysisOptions.selectedLinearLoadCases.clear();
        			AnalysisOptions.selectedNoneLinearLoadCases.clear();
        			if (TableListSelection.tableListUserSelectedLoadCases != null) TableListSelection.tableListUserSelectedLoadCases.clear();

        			String[] grps = AnalysisMembersCollection.getGroupsForAStructuralLine(AnalysisOptions.selectedStrLine);
        			if (grps!=null)
        			{
        				cbGroups.setModel(new DefaultComboBoxModel<String>(grps));
        				cbGroups.setSelectedItem(cbGroups.getSelectedItem());
        			}
        		}    		 
        	}
        }
    }
    

	private void setUpPanelNorth()
	{
		//init the north panel
		this.pnlNorth = new JPanel(new FlowLayout());
		this.pnlNorth.setBorder(BorderFactory.createTitledBorder(""));
		
		
		/////////////////////////////////////////////////////////////////////////
		
		//structural lines
		
		String[] arrSListStrLines = new String[]{"select"};
		this.cbStrLines = new JComboBox<String>(arrSListStrLines);
		this.lStrLines = new JLabel("Structural Line(s)");
		this.lStrLines.setOpaque(true);
		this.pnlNorth.add(this.lStrLines);
		this.pnlNorth.add(this.cbStrLines);
		this.cbStrLines.setBackground(new Color(231, 240, 248));
		this.cbStrLines.setForeground(Color.BLACK);
		this.cbStrLines.addActionListener(this.cbActionListener);
		this.cbStrLines.addItemListener(this.cbItemListener);

		/////////////////////////////////////////////////////////////////////////

		//groups
		
		//groups (find only the group corresponding to the chosen str line
		String[] arrSGroups = new String[]{"select"};
		this.cbGroups = new JComboBox<String>(arrSGroups);
		this.lGroups = new JLabel("Group(s)");
		this.lGroups.setOpaque(true);
		this.pnlNorth.add(this.lGroups);
		this.pnlNorth.add(this.cbGroups);
		this.cbGroups.setBackground(new Color(231, 240, 248));
		this.cbGroups.setForeground(Color.BLACK);
		this.cbGroups.addActionListener(this.cbActionListener);
		this.cbGroups.addActionListener(this.cbActionListener);
		this.cbGroups.addItemListener(this.cbItemListener);

		/////////////////////////////////////////////////////////////////////////
	    
		//load cases I
		
		this.imgURL = getClass().getClassLoader().getResource(GuiAnalysis.loadCasesPath);
		if (this.imgURL != null) {
			this.iconLoadCases = new ImageIcon(this.imgURL);
		} else {
			JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + GuiAnalysis.loadCasesPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		this.lblLoadCases = new JLabel("Loadcases I", this.iconLoadCases, SwingConstants.CENTER);
		this.btnLoadCases = new JButton();
		this.btnLoadCases.add(this.lblLoadCases);
		this.pnlNorth.add(this.btnLoadCases);
		this.btnLoadCases.addItemListener(this.cbItemListener);
		this.btnLoadCases.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{	
				if (AnalysisOptions.selectedStrLine != -1 && AnalysisOptions.selectedGroup != -1 && AnalysisOptions.selectedStrLine > 0 && AnalysisOptions.selectedGroup >= 0)
				{

					//get for that structural line the lcs
					String[] linearLcsForSelectedStructuralLine = AnalysisMembersCollection.getLinearLoadCasesForAStructuralLine(AnalysisOptions.selectedStrLine);
					
					if (linearLcsForSelectedStructuralLine != null)
					{
						if (TableListSelection.tableListUserSelectedLoadCases != null)  TableListSelection.tableListUserSelectedLoadCases.clear();
						TableListSelection.bringUpLoadCaseSelector(GuiAnalysis.this, AnalysisOptions.selectedStrLine, linearLcsForSelectedStructuralLine,1);
					}
					else
					{
						analysisOutputArea.append("Error : No linear load cases associated with the selected structural line!\n");
					}
				}
				else
				{
					analysisOutputArea.append("Error : Structural Line and Group must be selected for the load cases to be generated and enabled!\n");
				}
			}
		});
		
		//load cases II/III
		
		this.lblLoadCasesII = new JLabel("Loadcases II / III", this.iconLoadCases, SwingConstants.CENTER);
		this.btnLoadCasesII = new JButton();
		this.btnLoadCasesII.add(this.lblLoadCasesII);
		this.pnlNorth.add(this.btnLoadCasesII);
		this.btnLoadCasesII.addItemListener(this.cbItemListener);
		this.btnLoadCasesII.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (AnalysisOptions.selectedStrLine != -1 && AnalysisOptions.selectedGroup != -1 && AnalysisOptions.selectedStrLine > 0 && AnalysisOptions.selectedGroup >= 0)
				{

					//get for that structural line the lcs (II or III order)
					String[] nonLinearlcsForSelectedStructuralLine = AnalysisMembersCollection.getNonLinearLoadCasesForAStructuralLine(AnalysisOptions.selectedStrLine);
					
					if (nonLinearlcsForSelectedStructuralLine != null)
					{
						if (TableListSelection.tableListUserSelectedLoadCases != null)  TableListSelection.tableListUserSelectedLoadCases.clear();
						TableListSelection.bringUpLoadCaseSelector(GuiAnalysis.this, AnalysisOptions.selectedStrLine, nonLinearlcsForSelectedStructuralLine,2);
					}
					else
					{
						analysisOutputArea.append("Error : No nonlinear load cases associated with the selected structural line!\n");
					}
				}
				else
				{
					analysisOutputArea.append("Error : Structural Line and Group must be selected for the load cases to be generated and enabled!\n");
				}
				
			}
		});
	}
	
	private void setUpPanelWest()
	{
		
	    // Create radio buttons for setting vertical alignment of the JLabel
		this.rbSimplMethod = new JRadioButton("Simplified Method");
		this.rbNomStiffMethod = new JRadioButton("Nominal Stiffness Method");
		this.rbNomCurvMethod = new JRadioButton("Nominal Curvature Method", true); //default
		this.rbGeneralMethod = new JRadioButton("General Method");

		// Put the radio buttons into a ButtonGroup to ensure exclusive selection
		this.btnGroupAnalysisOptions = new ButtonGroup();
		this.btnGroupAnalysisOptions.add(this.rbSimplMethod);
		this.btnGroupAnalysisOptions.add(this.rbNomStiffMethod);
		this.btnGroupAnalysisOptions.add(this.rbNomCurvMethod);
		this.btnGroupAnalysisOptions.add(this.rbGeneralMethod);
		
		//add the analysis output radio buttons and create a group
		this.rbBasicOutput = new JRadioButton("Basic Output");
		this.rbDetailedOutput = new JRadioButton("Detailed Output", true);
		this.btnGroupLevelOfOutput = new ButtonGroup();
		this.btnGroupLevelOfOutput.add(this.rbBasicOutput);
		this.btnGroupLevelOfOutput.add(this.rbDetailedOutput);
		this.rbBasicOutput.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	        	 AnalysisOptions.selectedOutputLevel = AnalysisOutputLevel.BASIC;
	         }
	      });
		this.rbDetailedOutput.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	        	 AnalysisOptions.selectedOutputLevel = AnalysisOutputLevel.DETAILED;
	         }
	      });
		
		// Set up a JPanel to hold all radio buttons
		this.pnlWestMethod = new JPanel(new GridLayout(0,1)); // single column
		this.pnlWestMethod.add(this.rbSimplMethod);
		this.pnlWestMethod.add(this.rbNomStiffMethod);
		this.pnlWestMethod.add(this.rbNomCurvMethod);
		this.pnlWestMethod.add(this.rbGeneralMethod);
		this.pnlWestMethod.setBorder(BorderFactory.createTitledBorder("Calculation Method"));
		
		this.pnlWestOutput = new JPanel(new GridLayout(0,1)); //single column
		this.pnlWestOutput.add(this.rbBasicOutput);
		this.pnlWestOutput.add(this.rbDetailedOutput);
		this.pnlWestOutput.setBorder(BorderFactory.createTitledBorder("Output Level"));
		
		this.pnlWest = new JPanel(new BorderLayout());
		this.pnlWest.add(this.pnlWestMethod,BorderLayout.CENTER);
		this.pnlWest.add(this.pnlWestOutput,BorderLayout.SOUTH);
		
		
		
		// Radio buttons also fire ActionEvent
		this.rbSimplMethod.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	        	 AnalysisOptions.selectedAnalysisOption = AnalysisMethods.SIMPLIFIED_METHOD;
	         }
	      });
		this.rbNomStiffMethod.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	        	 AnalysisOptions.selectedAnalysisOption = AnalysisMethods.NOMINAL_STIFFNESS_METHOD;
	         }
	      });
		this.rbNomCurvMethod.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	        	 AnalysisOptions.selectedAnalysisOption = AnalysisMethods.NOMINAL_CURVATURE_METHOD;
	         }
	      });
		this.rbGeneralMethod.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	        	 AnalysisOptions.selectedAnalysisOption = AnalysisMethods.GENERAL_METHOD;
	         }
	      });
	}
	
	private void setUpPanelEast()
	{

		this.imgURL = getClass().getClassLoader().getResource(GuiAnalysis.printMemberTopologyPath);
		if (this.imgURL != null) {
			this.iconPrintMemberTopology = new ImageIcon(this.imgURL);
		} else {
			JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + GuiAnalysis.printMemberTopologyPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		this.lblPrintMemberTopology = new JLabel("Print Member Topology", this.iconPrintMemberTopology, SwingConstants.CENTER);


		JPanel jp1 = new JPanel(new FlowLayout());
		this.btnDisplayMemberAnalysisData = new JButton();
		this.btnDisplayMemberAnalysisData.add(this.lblPrintMemberTopology);
		this.btnDisplayMemberAnalysisData.setEnabled(false);
		this.btnDisplayMemberAnalysisData.setVerticalAlignment(SwingConstants.CENTER);
		this.btnDisplayMemberAnalysisData.setHorizontalAlignment(SwingConstants.CENTER);
		this.btnDisplayMemberAnalysisData.setVerticalTextPosition(SwingConstants.CENTER);
		this.btnDisplayMemberAnalysisData.setHorizontalTextPosition(SwingConstants.CENTER);
		this.btnDisplayMemberAnalysisData.setPreferredSize(new Dimension(screenWidth/8,screenHeight/20));
		this.btnDisplayMemberAnalysisData.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String memberAnalysisInfo = getGeneratedMemberInfo();
				if (AnalysisMembersCollection.membersCollection.size()==0) analysisOutputArea.append("Error : No analysis members present\n");
				if (memberAnalysisInfo==null) analysisOutputArea.append("Error : Structural Line, Group and Linear Loadcase(s) must be all selected! If no linear load cases are listed for the structural line, there are no load cases associated with it at all\n");
				if (memberAnalysisInfo!=null) analysisOutputArea.append(memberAnalysisInfo);	
			}
		});
		jp1.add(btnDisplayMemberAnalysisData);
		
		
		
		this.imgURL = getClass().getClassLoader().getResource(GuiAnalysis.showGraphicPath);
		if (this.imgURL != null) {
			this.iconShowGraphic = new ImageIcon(this.imgURL);
		} else {
			JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + GuiAnalysis.showGraphicPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		this.lblShowGraphic = new JLabel("Show/Update Graphic", this.iconShowGraphic, SwingConstants.CENTER);

		JPanel jp2 = new JPanel(new FlowLayout());
		this.btnShowGraphic = new JButton();
		this.btnShowGraphic.add(this.lblShowGraphic);
		this.btnShowGraphic.setEnabled(false);
		this.btnShowGraphic.setVerticalAlignment(SwingConstants.CENTER);
		this.btnShowGraphic.setHorizontalAlignment(SwingConstants.CENTER);
		this.btnShowGraphic.setVerticalTextPosition(SwingConstants.CENTER);
		this.btnShowGraphic.setHorizontalTextPosition(SwingConstants.CENTER);
		this.btnShowGraphic.setPreferredSize(new Dimension(screenWidth/7,screenHeight/20));
		this.btnShowGraphic.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				if (AnalysisOptions.selectedStrLine != -1 && AnalysisOptions.selectedGroup != -1) // || AnalysisOptions.selectedLoadCases.isEmpty()
				{
					//columnVisualiser = new GuiColumnVisualisation();
					//columnVisualiser.setEnabled(true);
					columnVisualiser.repaint();
					//requestFocus();
					
					//sectionVisualiser.setEnabled(true);
					sectionVisualiser.repaint();
					//requestFocus();
				}
			}
		});
		jp2.add(btnShowGraphic);


		JPanel jp3 = new JPanel(new BorderLayout(10,10));
		
		this.imgURL = getClass().getClassLoader().getResource(GuiAnalysis.analysePath);
		if (this.imgURL != null) {
			this.iconAnalyse = new ImageIcon(this.imgURL);
		} else {
			JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + GuiAnalysis.analysePath, "GUI Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		this.lblAnalyse = new JLabel("ANALYSE", this.iconAnalyse, SwingConstants.CENTER);
		
		this.btnAnalyseMember = new JButton();
		this.btnAnalyseMember.add(this.lblAnalyse);
		this.btnAnalyseMember.setEnabled(false);
		this.btnAnalyseMember.setVerticalAlignment(SwingConstants.CENTER);
		this.btnAnalyseMember.setHorizontalAlignment(SwingConstants.CENTER);
		this.btnAnalyseMember.setVerticalTextPosition(SwingConstants.CENTER);
		this.btnAnalyseMember.setHorizontalTextPosition(SwingConstants.CENTER);
		this.btnAnalyseMember.setPreferredSize(new Dimension(screenWidth/20,screenHeight/20));
		this.btnAnalyseMember.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {

				
				analysisOutputArea.append("\nANALYSIS STARTED ...!\n");
				long startAnalysisTime = System.currentTimeMillis();
				AnalysisMemberEC3Analysis.analysisState = AnalysisState.ANALYSIS_STARTED; //set analysis to started
				AnalysisMemberEC3Analysis.doPrepareAdditionalDesignDataAndAnalyse(GuiAnalysis.this);
				
				//...postprocess , evtl PDF ???
				if (AnalysisMemberEC3Analysis.analysisState == AnalysisMemberEC3Analysis.AnalysisState.ANALYSIS_READY)
				{
					long endAnalysisTIme = System.currentTimeMillis();
					analysisOutputArea.append("ANALYSIS FINISHED!\n");
					analysisOutputArea.append("Time taken : " +  (endAnalysisTIme - startAnalysisTime) +"[ms]\n");
					btnGeneratePdfWithChecks.setEnabled(true);
				}
				else
				{
					analysisOutputArea.append("Analysis did not run due to errors!\n");
					btnGeneratePdfWithChecks.setEnabled(false);
				}
			}
		});
		
		
		
		this.imgURL = getClass().getClassLoader().getResource(GuiAnalysis.analysisParamsPath);
		if (this.imgURL != null) {
			this.iconAnalysisParams = new ImageIcon(this.imgURL);
		} else {
			JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + GuiAnalysis.analysisParamsPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		this.lblAnalysisParams = new JLabel("Analysis Parameters", this.iconAnalysisParams, SwingConstants.CENTER);
		
		this.btnAnalysisParams = new JButton();
		this.btnAnalysisParams.add(this.lblAnalysisParams);
		this.btnAnalysisParams.setEnabled(false);
		this.btnAnalysisParams.setVerticalAlignment(SwingConstants.CENTER);
		this.btnAnalysisParams.setHorizontalAlignment(SwingConstants.CENTER);
		this.btnAnalysisParams.setVerticalTextPosition(SwingConstants.CENTER);
		this.btnAnalysisParams.setHorizontalTextPosition(SwingConstants.CENTER);
		this.btnAnalysisParams.setPreferredSize(new Dimension(screenWidth/10,screenHeight/20));
		this.btnAnalysisParams.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
		    	GuiAnalysis.this.setEnabled(false);
		    	new GuiAnalysisParams(GuiAnalysis.this);
			}
		});
		jp3.add(btnAnalysisParams,BorderLayout.NORTH);
		jp3.add(btnAnalyseMember,BorderLayout.CENTER);
		
		
		this.pnlEast = new JPanel(new BorderLayout());
		this.pnlEast.setBorder(BorderFactory.createTitledBorder("Controls"));
		this.pnlEast.add(jp1,BorderLayout.NORTH);
		this.pnlEast.add(jp2,BorderLayout.CENTER);
		this.pnlEast.add(jp3,BorderLayout.SOUTH);

	}
	
	private String getGeneratedMemberInfo()
	{
		///check here only for linear load cases!
		if (AnalysisOptions.selectedStrLine == -1 || AnalysisOptions.selectedGroup == -1 || AnalysisOptions.selectedLinearLoadCases.isEmpty()) return null;
		
		StringBuilder info = new  StringBuilder();
		
		//get member design data
		AnalysisMember member = AnalysisMembersCollection.membersCollection.get(AnalysisOptions.selectedStrLine);
		
		
		info.append("=====================================\n");
		info.append(" PREPARING MEMBER ANALYSIS OVERVIEW  \n");
		info.append("=====================================\n");
		info.append(" Selected Structural Line = " + AnalysisOptions.selectedStrLine +  " : " + member.getStrLineTitle().trim() + "\n");
		info.append(" Selected Structural Group = " + AnalysisOptions.selectedGroup +  "\n");		
		if (AnalysisOptions.selectedLinearLoadCases != null  && !AnalysisOptions.selectedLinearLoadCases.isEmpty())
		{
			info.append(" Selected Linear LoadCases = " + AnalysisOptions.selectedLinearLoadCases.toString() +  "\n");
		}
		if (AnalysisOptions.selectedNoneLinearLoadCases != null && !AnalysisOptions.selectedNoneLinearLoadCases.isEmpty())
		{
			info.append(" Selected None-Linear LoadCases = " + AnalysisOptions.selectedNoneLinearLoadCases.toString() +  "\n");
		}
		info.append(" Selected Analysis Method = " + AnalysisOptions.selectedAnalysisOption +  "\n");
		
		double memLength = 0;
		
		for (AnalysisMemberSubElement arrSubElem : member.arrSubElements)
		{
			info.append("-------------------------------------\n");
			info.append(" Sub-Beam Index = " + arrSubElem.iSubElemCdbIndex + "\n");
			info.append(" Sub-Beam Start Node = " + arrSubElem.nodeId1 + "\n");
			info.append(" Sub-Beam End Node = " + arrSubElem.nodeId2 + "\n");;
			info.append(" Sub-Beam Start Section = " + arrSubElem.sectId1 + "\n");
			info.append(" Sub-Beam End Section = " + arrSubElem.sectId2 + "\n");
			info.append(" Sub-Beam Segment Length = " + arrSubElem.dL + " [m]\n");
			
			memLength += arrSubElem.dL;
			
			if (arrSubElem.lcs != null) //print all lcs for the sub-element with titles
			{
				for (int k=0 ; k < arrSubElem.lcs.length; k++)
				{
					if (k==0) info.append(" Load Cases = \n");
					//lc number
					info.append("...." + arrSubElem.lcs[k] + "  ");
					//lc title
					if (member.getTitleForActiveLoadCase(arrSubElem.lcs[k])!=null) {
						info.append("[" + member.getTitleForActiveLoadCase(arrSubElem.lcs[k]) + "]\n");
					}
					else {
						info.append("[ ]\n");
					}
				}
			}
			else
			{
				info.append("no load cases present => please check your model! \n");
			}
			
			if (arrSubElem.mats != null)  //print all materials for the sub-element
			{
				for (int k=0 ; k < arrSubElem.mats.length; k++)
				{
					if (k==0) info.append(" Sub-Beam Materials = ");
					//material number
					if (k==arrSubElem.mats.length-1) {
						info.append(arrSubElem.mats[k]);
					}
					else {
						info.append(arrSubElem.mats[k] + " , ");
					}
					if (k==arrSubElem.mats.length-1) info.append("\n");
				}
			}
			else
			{
				info.append("no materials present => please check your model! \n");
			}
		}
		//print the total length and number of elements
		info.append("-------------------------------------\n");
		info.append(" Total number of subelements = " + member.getnSubElems() + "\n");
		info.append(" Total length of member = " + memLength + " [m]\n");

		//get the member structure from the membersSet
		return info.toString();
	}
	
	private void setUpPanelSouth()
	{

		this.imgURL = getClass().getClassLoader().getResource(GuiAnalysis.generateOutputPdfPath);
		if (this.imgURL != null) {
			this.iconGenerateOutputPdf = new ImageIcon(this.imgURL);
		} else {
			JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + GuiAnalysis.generateOutputPdfPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		this.lblGenerateOutputPdf = new JLabel("Generate Output PDF", this.iconGenerateOutputPdf, SwingConstants.CENTER);
		
		this.btnGeneratePdfWithChecks = new JButton();
		this.btnGeneratePdfWithChecks.add(this.lblGenerateOutputPdf);
		this.btnGeneratePdfWithChecks.setEnabled(false);
		this.btnGeneratePdfWithChecks.setVerticalTextPosition(SwingConstants.CENTER);
		this.btnGeneratePdfWithChecks.setHorizontalTextPosition(SwingConstants.CENTER);
		this.btnGeneratePdfWithChecks.setPreferredSize(new Dimension(screenWidth/8,screenHeight/20));
		this.btnGeneratePdfWithChecks.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (AnalysisMemberEC3Analysis.analysisState == AnalysisMemberEC3Analysis.AnalysisState.ANALYSIS_READY)
				{
					///////////////////////////////////////////
					
		  			if (fileCh==null) fileCh = new JFileChooser();
		  			int returnVal = fileCh.showOpenDialog(GuiAnalysis.this);

		  			if (returnVal == JFileChooser.APPROVE_OPTION) 
		  			{
		  				// ask user for path and name to his Pdf
		  				File file = fileCh.getSelectedFile();
		  				
  		                String fileExt = GuiUtils.getExtension(file); //check file extension
  		                if (fileExt==null || !fileExt.equals(GuiUtils.pdf)) //!file.isFile() || 
  		                {
  		                	JOptionPane.showMessageDialog(null, "Output file must have a [.pdf] extension!", "Pdf Generator error", JOptionPane.ERROR_MESSAGE);
  		                }
  		                else
  		                {
		  					//log message
		  					analysisOutputArea.append("Saving the pdf file as : " + file.getName() + " to " + file.getPath() + "\n");

		  					//generate the pdf with data from the AnalysisEC3 class
		  					new PdfGenerator().generatePdf(file.getPath());
						
		  					//check if the pdf was generated ?
		  					File pdfFile = new File(file.getPath());
		  					if (pdfFile.exists()) {
		  						analysisOutputArea.append("Output Pdf generated to :" + pdfFile.getAbsolutePath() + "\n");
		  					}
		  					else
		  					{
		  						analysisOutputArea.append("Output Pdf was unable to generate to :" + pdfFile.getAbsolutePath() + "\n");
		  					}
		  					//if generated, attempt to open the pdf for the user
		  					if (pdfFile.exists())
		  					{
		  						if (Desktop.isDesktopSupported())
		  						{
		  							try {
		  								Desktop.getDesktop().open(pdfFile);
		  							} catch (IOException e1) {
		  								JOptionPane.showMessageDialog(null, "An error occured whilst opening the generated pdf analysis otput", "Pdf Generator error", JOptionPane.ERROR_MESSAGE);
		  							}
		  						} else {
		  							JOptionPane.showMessageDialog(null, "AWT is not supported on the platform. Open the pdf manually", "Pdf Generator error", JOptionPane.ERROR_MESSAGE);
		  						}
		  					}
  		                }
		  			}
		  			fileCh.setSelectedFile(null);
		  			analysisOutputArea.setCaretPosition(analysisOutputArea.getDocument().getLength());

					/////////////////////////////////////////////
				}
			}
		});
		
		
		this.imgURL = getClass().getClassLoader().getResource(GuiAnalysis.backToPreprocessorPath);
		if (this.imgURL != null) {
			this.iconBackToPreprocessor = new ImageIcon(this.imgURL);
		} else {
			JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + GuiAnalysis.backToPreprocessorPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		this.lblBackToPreprocessor = new JLabel("Back to Preprocessor", this.iconBackToPreprocessor, SwingConstants.CENTER);

		this.btnBackToMainGui = new JButton();
		this.btnBackToMainGui.add(this.lblBackToPreprocessor);
		this.btnBackToMainGui.setEnabled(true);
		this.btnBackToMainGui.setVerticalTextPosition(SwingConstants.CENTER);
		this.btnBackToMainGui.setHorizontalTextPosition(SwingConstants.CENTER);
		this.btnBackToMainGui.setPreferredSize(new Dimension(screenWidth/8,screenHeight/20));		
		this.btnBackToMainGui.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//JOptionPane.showMessageDialog(null, "Leaving the analysis part will reset all current options and settings", "GUI Message", JOptionPane.WARNING_MESSAGE);
				int opt = JOptionPane.showConfirmDialog(null, "Leaving the analysis part will reset all current options and settings", "GUI Message", JOptionPane.OK_CANCEL_OPTION);
				if (opt == JOptionPane.OK_OPTION) {
					pGuiMain.setEnabled(true);
					pGuiMain.setVisible(true);
					pGuiMain.setAutoRequestFocus(true);
					GuiAnalysis.this.setVisible(false);
				}
			}
		});
		
		//set saveLog button
		this.imgURL = getClass().getClassLoader().getResource(GuiAnalysis.saveConsoleLogPath);
		if (this.imgURL != null) {
			this.iconSaveConsoleLog = new ImageIcon(this.imgURL);
		} else {
			JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + saveConsoleLogPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		this.lblSaveConsoleLog = new JLabel("Save Console Log", this.iconSaveConsoleLog, SwingConstants.CENTER);

	  	this.btnSaveConsoleLog = new JButton();
	  	this.btnSaveConsoleLog.add(this.lblSaveConsoleLog);
	  	this.btnSaveConsoleLog.setPreferredSize(new Dimension(screenWidth/10,screenHeight/20));
	  	this.btnSaveConsoleLog.setToolTipText("Save the console Log file");
	  	this.btnSaveConsoleLog.setEnabled(true);
	  	this.btnSaveConsoleLog.setVerticalTextPosition(SwingConstants.CENTER);
	  	this.btnSaveConsoleLog.setHorizontalTextPosition(SwingConstants.CENTER);
	  	this.btnSaveConsoleLog.addActionListener(new ActionListener()
	  	{
	  		@Override
	  		public void actionPerformed(ActionEvent e)
	  		{

	  			if (fileCh==null) fileCh = new JFileChooser();
	  			int returnVal = fileCh.showOpenDialog(GuiAnalysis.this);

	  			if (returnVal == JFileChooser.APPROVE_OPTION) 
	  			{
	  				File file = fileCh.getSelectedFile();
	  				analysisOutputArea.append("Saving the console log as : " + file.getName() + " to " + file.getPath() + "\n");
	  				
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
	  						prtStream.append(analysisOutputArea.getText());
	  					} 
	  					catch (FileNotFoundException | UnsupportedEncodingException e1)
	  					{
	  						isOk=false;
	  						JOptionPane.showMessageDialog(null, "Cannot save log to current file " + file.getName() +
	  								" on path " +  file.getPath() , "GUI error", JOptionPane.ERROR_MESSAGE);
	  						analysisOutputArea.append("Saving the console log failed : " + file.getName() + " to " + file.getPath() + "\n");
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
	  							analysisOutputArea.append("Failed closing the write streams " + "\n");
	  						}
	  						if (isOk) analysisOutputArea.append("Console successfully saved under : " + file.getName() + " on " + file.getPath() + "\n");
	  					}
	  				}
	  			//}
	  			fileCh.setSelectedFile(null);
	  			analysisOutputArea.setCaretPosition(analysisOutputArea.getDocument().getLength());
	  			/////////////////////////
	  		}
	  	});
	  	
	  	
		//set saveLog button
	  	this.imgURL = getClass().getClassLoader().getResource(GuiAnalysis.clearConsoleLogPath);
	  	if (this.imgURL != null) {
	  		this.iconClearConsoleLog = new ImageIcon(this.imgURL);
	  	} else {
	  		JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + GuiAnalysis.clearConsoleLogPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
	  		System.exit(1);
	  	}
	  	this.lblClearConsoleLog = new JLabel("Clear Console Log", this.iconClearConsoleLog, SwingConstants.CENTER);

	  	this.btnClearConsole = new JButton();
	  	this.btnClearConsole.add(this.lblClearConsoleLog);
	  	this.btnClearConsole.setPreferredSize(new Dimension(screenWidth/10,screenHeight/20));
	  	this.btnClearConsole.setToolTipText("Cler the Console Log file");
	  	this.btnClearConsole.setEnabled(true);
	  	this.btnClearConsole.setVerticalTextPosition(SwingConstants.CENTER);
	  	this.btnClearConsole.setHorizontalTextPosition(SwingConstants.CENTER);
	  	this.btnClearConsole.addActionListener(new ActionListener()
	  	{
	  		@Override
	  		public void actionPerformed(ActionEvent e)
	  		{
	  			analysisOutputArea.setText("");
	  			analysisOutputArea.validate();
	  		}
  		});
		
	  	this.pnlSouth = new JPanel(new FlowLayout());
	  	this.pnlSouth.setBorder(BorderFactory.createTitledBorder(""));
	  	this.pnlSouth.add(this.btnGeneratePdfWithChecks);
	  	this.pnlSouth.add(this.btnSaveConsoleLog);
	  	this.pnlSouth.add(this.btnClearConsole);
	  	this.pnlSouth.add(this.btnBackToMainGui);
		
	}
	
	private void setUpPanelCentre()
	{
		this.analysisOutputArea = new JTextArea("",20,50);
		this.analysisOutputArea.setFont(new Font("Serif", Font.BOLD, 15));
		this.analysisOutputArea.setLineWrap(true);       // wrap line
		this.analysisOutputArea.setWrapStyleWord(true);  // wrap line at word boundary
		this.analysisOutputArea.setMargin(new Insets(5,5,5,5));
		this.analysisOutputArea.setBackground(Color.BLACK); // light blue
		this.analysisOutputArea.setForeground(Color.YELLOW); // light blue
		this.analysisOutputArea.setEditable(false);
		
		this.analysisAreaScrollPane = new JScrollPane(analysisOutputArea); // Wrap the JTextArea inside a JScrollPane
		this.analysisAreaScrollPane.setBorder(BorderFactory.createEtchedBorder());
		this.analysisAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.analysisAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.pnlCentre = new JPanel(new BorderLayout(5,5));
	  	this.pnlCentre.setBorder(BorderFactory.createTitledBorder("Member Analysis Output"));
	  	this.pnlCentre.add(analysisAreaScrollPane,BorderLayout.WEST);
	  	
	  	this.columnVisualiser = new GuiColumnVisualisation();
	  	//this.columnVisualiser.setPreferredSize(new Dimension(COLUMN_VISUALISATION_WIDTH, COLUMN_VISUALISATION_HEIGHT));
	  	this.columnVisualiser.setVisible(true);
	  	this.columnVisualiser.setBorder(BorderFactory.createTitledBorder("Column"));
	  	this.columnVisualiser.setAutoscrolls(getFocusTraversalKeysEnabled());
	  	
		this.sectionVisualiser = new GuiSectionVisualisation();
	  	//this.sectionVisualiser.setPreferredSize(new Dimension(SECTION_VISUALISATION_WIDTH, SECTION_VISUALISATION_HEIGHT));
	  	this.sectionVisualiser.setVisible(true);
	  	this.sectionVisualiser.setBorder(BorderFactory.createTitledBorder("Section"));
	  	this.sectionVisualiser.setAutoscrolls(getFocusTraversalKeysEnabled());
	  	
	  	//add section and column visualiser to panel for visualisation
	  	this.pnlVisualisation = new JPanel(new BorderLayout());
	  	this.pnlVisualisation.add(columnVisualiser,BorderLayout.NORTH);
	  	this.pnlVisualisation.add(sectionVisualiser,BorderLayout.CENTER);


	  	//add to the centre panel
	  	this.pnlCentre.add(this.pnlVisualisation,BorderLayout.CENTER);

	}
	
}
