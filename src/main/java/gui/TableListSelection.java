package gui;

import javax.swing.*;
import javax.swing.event.*;

import sofistik.AnalysisMember;
import sofistik.AnalysisMembersCollection;
import sofistik.AnalysisOptions;
import sofistik.PdfGenerator;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
 
public class TableListSelection extends JPanel
{

	private static final long serialVersionUID = 1L;
	private JTextArea output;
    private JList<String> list; 
    private JTable table;
    private String newline = "\n";
    private ListSelectionModel listSelectionModel;
    private JButton btnConfirm;
    private JPanel pnlBtnConfirm;
    
    static String[] columnNames;
    static String[][] tableData;
    static int selectedStructuralLine;
    static TreeSet<Integer> tableListUserSelectedLoadCases; //contains linear and non-linear
    
    static TableListSelection demo;
    static JFrame frame;
    private static GuiAnalysis pGuiAnalysis;
    
    private static int orderOfLoadCasesRequested = 1; 
    private static int iClickCounter = 0;
    
    static {
    	pGuiAnalysis = null;
    }
    
    @SuppressWarnings("rawtypes")
	public TableListSelection() 
    {
        super(new BorderLayout());
 
        table = new JTable(tableData, columnNames);
        listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
        table.setSelectionModel(listSelectionModel);
        JScrollPane tablePane = new JScrollPane(table);
        
     
        //Build control area (use default FlowLayout).
        JPanel controlPane = new JPanel();
        String[] modes = { "Single Selection",
                           "Single Interval Selection",
                           "Multiple Interval Selection" };
 
        @SuppressWarnings("unchecked")
		final JComboBox comboBox = new JComboBox(modes);
        comboBox.setSelectedIndex(2); //default : Multiple Interval Selection
        comboBox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String newMode = (String)comboBox.getSelectedItem();
                if (newMode.equals("Single Selection"))
                {
                    listSelectionModel.setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION);
                }
                else if (newMode.equals("Single Interval Selection"))
                {
                    listSelectionModel.setSelectionMode(
                    ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                } 
                else
                {
                    listSelectionModel.setSelectionMode(
                    ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                }
                output.append("----------" + "Mode: " + newMode + "----------" + newline);
            }
        });
        controlPane.add(new JLabel("Selection mode:"));
        controlPane.add(comboBox);
 
        //Build output area.
        output = new JTextArea(1, 10);
        output.setEditable(false);
        JScrollPane outputPane = new JScrollPane(output,
                         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
 
        //Do the layout.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        add(splitPane, BorderLayout.CENTER);
 
        JPanel topHalf = new JPanel();
        topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.LINE_AXIS));
        JPanel listContainer = new JPanel(new GridLayout(1,1));
        JPanel tableContainer = new JPanel(new GridLayout(1,1));
        tableContainer.setBorder(BorderFactory.createTitledBorder("Load Cases for Structural line " + selectedStructuralLine));
        tableContainer.add(tablePane);
        tablePane.setPreferredSize(new Dimension(840, 260)); //(420, 130));
        topHalf.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
        topHalf.add(listContainer);
        topHalf.add(tableContainer);
 
        topHalf.setMinimumSize(new Dimension(500, 100));  //(250, 50));
        topHalf.setPreferredSize(new Dimension(400, 220)); //200, 110));
        //--->
        splitPane.add(topHalf);
 
        
        btnConfirm = new JButton("OK");
        btnConfirm.setEnabled(true);
        btnConfirm.setVerticalTextPosition(SwingConstants.CENTER);
        btnConfirm.setHorizontalTextPosition(SwingConstants.CENTER);
        btnConfirm.setPreferredSize(new Dimension(50,30));
        btnConfirm.addActionListener(new ActionListener()
	  	{
	  		@Override
	  		public void actionPerformed(ActionEvent e) {
	  		
	  			
	  			if (TableListSelection.tableListUserSelectedLoadCases != null)
				{
					if (!TableListSelection.tableListUserSelectedLoadCases.isEmpty() && AnalysisOptions.selectedStrLine!=-1 && AnalysisOptions.selectedGroup!=-1)
					{
						//copy over the table selector load cases AnalysisOptions.selectedLoadCases
						if (orderOfLoadCasesRequested == 1)
						{
							if (AnalysisOptions.selectedLinearLoadCases == null) AnalysisOptions.selectedLinearLoadCases = new TreeSet<Integer>();
							AnalysisOptions.selectedLinearLoadCases.clear();
							if (TableListSelection.tableListUserSelectedLoadCases != null) AnalysisOptions.selectedLinearLoadCases.addAll(TableListSelection.tableListUserSelectedLoadCases);
							
							//print selected linear load cases to console
							if (  AnalysisOptions.selectedLinearLoadCases != null)
							{

								if (AnalysisOptions.selectedLinearLoadCases.size() > 0)
								{
									pGuiAnalysis.analysisOutputArea.append("\nSelected Linear Load Cases : \n");
									for (Integer lc : AnalysisOptions.selectedLinearLoadCases)
									{
										for (int k = 0; k < tableData.length; k++)
										{
											if (Integer.parseInt(tableData[k][0]) == lc)
											{
												pGuiAnalysis.analysisOutputArea.append(tableData[k][0] + " , " + tableData[k][1] + " , " + tableData[k][2]+"\n");
											}
										}
									}


									//set graphic and analyse button active
									pGuiAnalysis.btnAnalyseMember.setEnabled(true);
									pGuiAnalysis.btnAnalysisParams.setEnabled(true);
									pGuiAnalysis.btnShowGraphic.setEnabled(true);
									pGuiAnalysis.btnDisplayMemberAnalysisData.setEnabled(true);


									pGuiAnalysis.setEnabled(true); //set the main gui back on to active
									pGuiAnalysis.setVisible(true);
									pGuiAnalysis.setAutoRequestFocus(true);
									frame.setVisible(false);
								}
								else
								{
									String msg = new String("Please beware that no linear load were selected");
									JOptionPane.showMessageDialog(null, msg, "Load Case Selector", JOptionPane.YES_OPTION);
									pGuiAnalysis.analysisOutputArea.append("\nNo selected Linear Load Cases!\n");
								}

							}
							
							
						}
						
						if (orderOfLoadCasesRequested == 2)
						{
							if (AnalysisOptions.selectedNoneLinearLoadCases == null) AnalysisOptions.selectedNoneLinearLoadCases = new TreeSet<Integer>();
							AnalysisOptions.selectedNoneLinearLoadCases.clear();
							if (TableListSelection.tableListUserSelectedLoadCases != null) AnalysisOptions.selectedNoneLinearLoadCases.addAll(TableListSelection.tableListUserSelectedLoadCases);
							
							//print selected linear load cases to console
							if (  AnalysisOptions.selectedNoneLinearLoadCases != null)
							{
								if (AnalysisOptions.selectedNoneLinearLoadCases.size() > 0)
								{
									pGuiAnalysis.analysisOutputArea.append("\nSelected None-Linear Load Cases : \n");
									for (Integer lc : AnalysisOptions.selectedNoneLinearLoadCases)
									{
										for (int k = 0; k < tableData.length; k++)
										{
											if (Integer.parseInt(tableData[k][0]) == lc)
											{
												pGuiAnalysis.analysisOutputArea.append(tableData[k][0] + " , " + tableData[k][1] + " , " + tableData[k][2]+"\n");
											}
										}
									}
								}
								else
								{
									pGuiAnalysis.analysisOutputArea.append("\nNo selected None-Linear Load Cases!\n");
								}
								
								//set graphic and analyse button active
								pGuiAnalysis.btnAnalyseMember.setEnabled(true);
								pGuiAnalysis.btnAnalysisParams.setEnabled(true);
								pGuiAnalysis.btnShowGraphic.setEnabled(true);
								pGuiAnalysis.btnDisplayMemberAnalysisData.setEnabled(true);


								pGuiAnalysis.setEnabled(true); //set the main gui back on to active
								pGuiAnalysis.setVisible(true);
								pGuiAnalysis.setAutoRequestFocus(true);
								frame.setVisible(false);
								
							}
						}
						
					}
				}
	  			
	  			
	  			else
	  			{
	  				if (orderOfLoadCasesRequested == 1)
	  				{

	  				}
	  			}
	  			
	  			
	  			
	  			
	  			
	  			
	  			
	  			
	  		}
	  	}
	  	);

        pnlBtnConfirm = new JPanel();
        pnlBtnConfirm.add(btnConfirm);
        
        JPanel bottomHalf = new JPanel(new BorderLayout());
        bottomHalf.add(controlPane, BorderLayout.PAGE_START);
        bottomHalf.add(outputPane, BorderLayout.CENTER);
        bottomHalf.add(pnlBtnConfirm, BorderLayout.PAGE_END);
        //XXX: next line needed if bottomHalf is a scroll pane:
        bottomHalf.setMinimumSize(new Dimension(800, 100));   //Dimension(400, 50));
        bottomHalf.setPreferredSize(new Dimension(900, 220)); //(450, 110));
        //--->
        splitPane.add(bottomHalf);
    }
 

    static void bringUpLoadCaseSelector(final GuiAnalysis pGui , final int selectedStrLine, final String[] arrStructLineLoadCases, int iOrder) 
    {
    	
    	if (arrStructLineLoadCases == null || arrStructLineLoadCases.length < 1 || selectedStrLine <= 0) return;
    	
    	pGuiAnalysis = pGui;
    	pGuiAnalysis.setEnabled(false);
    	
    	orderOfLoadCasesRequested = iOrder;
    	iClickCounter = 0;
    	
    	selectedStructuralLine = selectedStrLine;
    	AnalysisMember currentSelectedMember = AnalysisMembersCollection.membersCollection.get(selectedStructuralLine);
    	if (currentSelectedMember !=  null)
    	{		
	        columnNames = new String[3];
	    	columnNames[0] = "LC Number";
	    	columnNames[1] = "LC Title";
	    	columnNames[2] = "LC Description";
	        tableData = new String[arrStructLineLoadCases.length-1][3]; //because of select which has the index 0
	        for (int i = 0; i <= arrStructLineLoadCases.length-1; i++)
	        {
	        	if (i==0) continue;
	        	tableData[i-1][0] = arrStructLineLoadCases[i];
	        	String iLcTitle = null;
	        	String iLcDescription = null;
	        	try
	        	{
	        		iLcTitle = currentSelectedMember.getTitleForActiveLoadCase(Integer.parseInt(arrStructLineLoadCases[i]));
	        		iLcDescription = currentSelectedMember.getLoadCaseTheoryForActiveLoadCase(Integer.parseInt(arrStructLineLoadCases[i]));
	        	}
	        	catch (NumberFormatException nfe)
	        	{
	        		pGuiAnalysis.analysisOutputArea.append("Error : cannot parse load case " + arrStructLineLoadCases[i] + "\n");
	        		return;
	        	}
	        	if (iLcTitle != null) tableData[i-1][1] = iLcTitle;
	        	if (iLcDescription != null) tableData[i-1][2] = iLcDescription;
	        }
    	}
    	else
    	{
    		pGuiAnalysis.analysisOutputArea.append("Error : cannot assemble the load cases for structural line " + selectedStructuralLine + "\n");
    		return;
    	}

        //Create and set up the window.
        frame = new JFrame("Load Case Selector");
 
        //Create and set up the content pane.
        demo = new TableListSelection();
        demo.setOpaque(true);
        frame.setContentPane(demo);
        
        frame.addWindowListener(new WindowAdapter()
	    {
	    	@Override
	    	public void windowClosing(WindowEvent e) 
	    	{
	    		pGuiAnalysis.setEnabled(true);
	    		pGuiAnalysis.setVisible(true);
	    		pGuiAnalysis.setAutoRequestFocus(true);
	    		pGuiAnalysis.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	    	}
	    });
               
        //Display the window.
        frame.pack();
        
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point pCentreOfScreen = ge.getCenterPoint();
        pCentreOfScreen.translate(-frame.getWidth()/2, -frame.getHeight()/2);
        frame.setLocation(pCentreOfScreen);
        
	    //add the desktop logo icon
	    ImageIcon desktopImage = null;
	  	URL imgURL = demo.getClass().getClassLoader().getResource(GuiMain.desktopIconPath);
	  	if (imgURL != null) 
	  	{
	  		desktopImage = new ImageIcon(imgURL);
	  		frame.setIconImage(desktopImage.getImage());
	  	}
        
        frame.setVisible(true);
        

    }
 
    class SharedListSelectionHandler implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        { 
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            iClickCounter++;
 
            if (lsm.isSelectionEmpty())
            {
                output.append("no load cases selected\n");
                //pGuiAnalysis.analysisOutputArea.append("no load cases selected\n");
            	if (tableListUserSelectedLoadCases == null)  tableListUserSelectedLoadCases = new TreeSet<Integer>();
            	tableListUserSelectedLoadCases.clear();
            } 
            else 
            {
            	output.append("Selected load cases : \n");
            	//pGuiAnalysis.analysisOutputArea.append("Selected Load Cases : \n");
            	
                //clear the selected lc every time!
            	if (tableListUserSelectedLoadCases == null)  tableListUserSelectedLoadCases = new TreeSet<Integer>();
            	tableListUserSelectedLoadCases.clear();
            	// Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) 
                {
                    if (lsm.isSelectedIndex(i))
                    {
                    	//print the selected load index to console
                    	output.append(" " + tableData[i][0] + " , " + tableData[i][1] + " , " + tableData[i][2]+"\n");
                    	//pGuiAnalysis.analysisOutputArea.append(" " + tableData[i][0] + " , " + tableData[i][1] + " , " + tableData[i][2]+"\n");
                        //add to the static tree set
                        tableListUserSelectedLoadCases.add(Integer.parseInt(tableData[i][0]));
                    }
                }
                iClickCounter = 0;
            }
            output.append(newline);
            output.setCaretPosition(output.getDocument().getLength());
        }
    }
}