package gui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import sofistik.AnalysisMember;
import sofistik.AnalysisMembersCollection;
import sofistik.AnalysisOptions;



public class GuiAnalysisParams extends JFrame
{
	
	
    public static enum eBucklingLengthOption
    {
    	RELATIVE, ABSOLUTE;
    }
    public static enum eBucklingType
    {
    	SINGLE_COMPRESSION_BENDING, FRAME_COMPRESSION_BENDING;
    }
    public static enum eNationalAnnex
    {
    	DIN_EN_1992_NA, BS_EN_1992_NA;
    }
    public static enum eSwayType
    {
    	SWAY, NONESWAY;
    }
    public static final String[] implementedNationalAnnexes = { "DIN EN 1992-1-1:2013_NA" , "BS EN 1992-1-1:2004_NA" };
    public static final String[] implementedBucklingTypes = { "Single Compression+Bending" , "Frame Compression+Bending" };
    public static final String[] implementedSwayTypes = { "Sway Member" , "None-sway member" };
	
    //**********
	private static final long serialVersionUID = 1L;
    private JTextField tfCreepFactor;
    private JLabel lCreepFactor;
    private JLabel lCreepFactorUnit;
    private JPanel pCreepFactor;  //1
    public static double creepFactor;
    
    //-- a radio button chooser
	private JRadioButton rbRelativeBucklLength;
	private JRadioButton rbAbsoluteBucklLength;
	private JPanel pnlBucklLength;
	private JPanel pnlBucklLengthRadioButtons;
	private ButtonGroup btnGroupBucklLength;
    
    private JTextField tfBucklLenLyRel;
    private JLabel lBuckLenLyRel;
    private JLabel lBuckLenLyUnitRel;
    private JPanel pBucklLenLyRel;   //2
    
    private JTextField tfBucklLenLzRel;
    private JLabel lBuckLenLzRel;
    private JLabel lBuckLenLzUnitRel;
    private JPanel pBucklLenLzRel;   //3

    private JTextField tfBucklLenLyAbs;
    private JLabel lBuckLenLyAbs;
    private JLabel lBuckLenLyUnitAbs;
    private JPanel pBucklLenLyAbs;   //2
    
    private JTextField tfBucklLenLzAbs;
    private JLabel lBuckLenLzAbs;
    private JLabel lBuckLenLzUnitAbs;
    private JPanel pBucklLenLzAbs;   //3
    
    public static double buckLenLyAbs;
    public static double buckLenLzAbs;
    public static double buckLenLyRel;
    public static double buckLenLzRel;
    
    static double memberTrueLength;
    public static boolean userModifiedBucklLength;
    
	private JComboBox<String> cbNationalAnnex;
	private JPanel jpanelNationalAnnex;
	
	private JComboBox<String> cbBucklingType;
	private JPanel jpanelMemberType;
	
	private JComboBox<String> cbSwayType;
	private JPanel jpanelSwayType;
	
	private JPanel jpanelMemberTypeAndNationalAnnexAndSwayType;
    
    public static int selectedBucklingLengthOption;
    public static int selectedBucklingType;
    public static int selectedNationalAnnex;
    public static int selectedSwayType;
    
    private JTextField tfImperfectionY;
    private JLabel lImperfectionY;
    private JLabel lImperfectionYUnit;
    private JPanel pImperfectionY;
    public static double imperfectionY;
    
    private JTextField tfImperfectionZ;
    private JLabel lImperfectionZ;
    private JLabel lImperfectionZUnit;
    private JPanel pImperfectionZ; //4
    public static double imperfectionZ;
    
    private JButton btnOk;
    private JButton btnCancel;
    private JPanel  pBtnOk;
    private JPanel  pBtnCancel;
    
    private JPanel  pSouth;
    private JPanel  jpanelAnalParams;
	private ImageIcon desktopImage;
	
	private ComboBoxActionListener cbActionListener;
	private ComboBoxItemListener cbItemListener;
    
    //pointer to the GuiAnalysis JFrame
    private GuiAnalysis pGuiAnalysis;
    
    
    
    private class ComboBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
        	
        	if (evt.getSource() == cbBucklingType)
        	{
        		GuiAnalysisParams.selectedBucklingType = cbBucklingType.getSelectedIndex();
        		
        		if (GuiAnalysisParams.selectedBucklingType == eBucklingType.FRAME_COMPRESSION_BENDING.ordinal())
        		{
        			tfBucklLenLyRel.setEnabled(false);
        			tfBucklLenLzRel.setEnabled(false);
        			tfBucklLenLyAbs.setEnabled(false);
        			tfBucklLenLzAbs.setEnabled(false);
        		}
        		else
        		{
					if (selectedBucklingLengthOption == eBucklingLengthOption.ABSOLUTE.ordinal())
					{
						tfBucklLenLyAbs.setEnabled(true);
						tfBucklLenLzAbs.setEnabled(true);
						tfBucklLenLyRel.setEnabled(false);
						tfBucklLenLzRel.setEnabled(false);
					}
					else
					{
						tfBucklLenLyAbs.setEnabled(false);
						tfBucklLenLzAbs.setEnabled(false);
						tfBucklLenLyRel.setEnabled(true);
						tfBucklLenLzRel.setEnabled(true);
					}
        			
        		}
        	}
        	
	   		 if (evt.getSource() == cbNationalAnnex)
	   		 {
	   			GuiAnalysisParams.selectedNationalAnnex = cbNationalAnnex.getSelectedIndex();
	   		 }
	   		 
	   		 if (evt.getSource() == cbSwayType)
	   		 {
	   			GuiAnalysisParams.selectedSwayType = cbSwayType.getSelectedIndex();
	   		 }
        }
    }

    private class ComboBoxItemListener implements ItemListener 
    {
        @Override
        public void itemStateChanged(ItemEvent evt)
        {
        	if (evt.getStateChange() == ItemEvent.SELECTED) 
        	{

        		if (evt.getSource() == cbBucklingType)
        		{

        		}  
        	
        		
        		if (evt.getSource() == cbNationalAnnex)
        		{

        		}
        		
           		if (evt.getSource() == cbSwayType)
        		{

        		} 
        		
        		
        	}
        }
    }
    
    static void setAllGuiAnalysisParamsToDefault()
    {
    	//set the static analysis opts to defaults
    	GuiAnalysisParams.creepFactor = 2.143; //A = 1/(1+0.2*fi,eff) , if A unknown use 0.7 => fi,eff = 2.143
    	GuiAnalysisParams.memberTrueLength = -1.0;
    	GuiAnalysisParams.buckLenLyRel = -1.0;
    	GuiAnalysisParams.buckLenLzRel = -1.0;
    	GuiAnalysisParams.buckLenLyAbs = -1.0;
    	GuiAnalysisParams.buckLenLzAbs = -1.0;
    	GuiAnalysisParams.imperfectionY = 10.0; //10 mm?
    	GuiAnalysisParams.imperfectionZ = 10.0; //10 mm?
    	GuiAnalysisParams.userModifiedBucklLength = false;
    	GuiAnalysisParams.selectedBucklingLengthOption = eBucklingLengthOption.ABSOLUTE.ordinal();
    	GuiAnalysisParams.selectedBucklingType = eBucklingType.SINGLE_COMPRESSION_BENDING.ordinal();
    	GuiAnalysisParams.selectedNationalAnnex = eNationalAnnex.DIN_EN_1992_NA.ordinal();
    }
    
    GuiAnalysisParams(final GuiAnalysis pGA )
    {
    	//set the pointer to the analysis gui
    	this.pGuiAnalysis = pGA;
    	
    	//get the member length
        AnalysisMember currentSelectedMember = AnalysisMembersCollection.membersCollection.get(AnalysisOptions.selectedStrLine);
        if (currentSelectedMember !=  null && GuiAnalysisParams.userModifiedBucklLength == false)
        {
            GuiAnalysisParams.buckLenLyAbs = currentSelectedMember.getStrLineLength();
            GuiAnalysisParams.buckLenLzAbs = currentSelectedMember.getStrLineLength();
            GuiAnalysisParams.buckLenLyRel = 100.0;
            GuiAnalysisParams.buckLenLzRel = 100.0;
            GuiAnalysisParams.memberTrueLength = currentSelectedMember.getStrLineLength();
        }
  
    	this.setTitle("Analysis Parameters Dialog");
    	this.setSize(new Dimension((int)(0.4*GuiAnalysis.screenWidth), (int)(0.6*GuiAnalysis.screenHeight)));
    	this.setPreferredSize(new Dimension((int)(0.4*GuiAnalysis.screenWidth), (int)(0.60*GuiAnalysis.screenHeight)));
    	this.setLocationRelativeTo(null);  // center the application window
    	this.setVisible(true);
    	this.setEnabled(true);
    	
    	this.tfCreepFactor = new JTextField(20);
    	this.tfCreepFactor.setEditable(true);
    	//this.tfCreepFactor.setUI(new HintTextFieldUI("Recommended range : 0.1 - 0.7"));
    	this.tfCreepFactor.setText(Double.toString(GuiAnalysisParams.creepFactor));
    	this.lCreepFactor = new JLabel("Effective Creep Factor φ,eff = ");
    	this.lCreepFactor.setToolTipText("Creep factor is to be obtained acc. to Eq. 5.19 DIN EN 1992-1-1");
    	this.lCreepFactor.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lCreepFactor.setOpaque(true);
    	this.lCreepFactorUnit = new JLabel("[ - ]");
    	this.lCreepFactorUnit.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lCreepFactorUnit.setOpaque(true);
    	this.pCreepFactor = new JPanel(new FlowLayout());
    	this.pCreepFactor.add(this.lCreepFactor);
    	this.pCreepFactor.add(this.tfCreepFactor);
    	this.pCreepFactor.add(this.lCreepFactorUnit);

    	this.tfBucklLenLyAbs = new JTextField(20);
    	this.tfBucklLenLyAbs.setEditable(true);
    	//this.tfBucklLenLx.setUI(new HintTextFieldUI("Recommended range : 1L - 2L"));
    	this.tfBucklLenLyAbs.setText(Double.toString(GuiAnalysisParams.buckLenLyAbs));
    	this.lBuckLenLyAbs = new JLabel("Buckling Length lo,y = ");
    	this.lBuckLenLyAbs.setToolTipText("Buckling length lo,y is recommended to be obtained acc. to Eq. 5.16 DIN EN 1992-1-1");
    	this.lBuckLenLyAbs.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lBuckLenLyAbs.setOpaque(true);
    	this.lBuckLenLyUnitAbs = new JLabel(" [ m ]");
    	this.lBuckLenLyUnitAbs.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lBuckLenLyUnitAbs.setOpaque(true);
    	this.pBucklLenLyAbs = new JPanel(new FlowLayout());
    	this.pBucklLenLyAbs.add(this.lBuckLenLyAbs);
    	this.pBucklLenLyAbs.add(this.tfBucklLenLyAbs);
    	this.pBucklLenLyAbs.add(this.lBuckLenLyUnitAbs);
    	
    	this.tfBucklLenLzAbs = new JTextField(20);
    	this.tfBucklLenLzAbs.setEditable(true);
    	//this.tfBucklLenLy.setUI(new HintTextFieldUI("Recommended range : 1L - 2L"));
    	this.tfBucklLenLzAbs.setText(Double.toString(GuiAnalysisParams.buckLenLzAbs));
    	this.lBuckLenLzAbs = new JLabel("Buckling Length lo,z = ");
    	this.lBuckLenLzAbs.setToolTipText("Buckling length lo,z is recommended to be obtained acc. to Eq. 5.16 DIN EN 1992-1-1");
    	this.lBuckLenLzAbs.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lBuckLenLzAbs.setOpaque(true);
    	this.lBuckLenLzUnitAbs = new JLabel("[ m ]");
    	this.lBuckLenLzUnitAbs.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lBuckLenLzUnitAbs.setOpaque(true);
    	this.pBucklLenLzAbs = new JPanel(new FlowLayout());
    	this.pBucklLenLzAbs.add(this.lBuckLenLzAbs);
    	this.pBucklLenLzAbs.add(this.tfBucklLenLzAbs);
    	this.pBucklLenLzAbs.add(this.lBuckLenLzUnitAbs);
    	
    	
    	this.tfBucklLenLyRel = new JTextField(20);
    	this.tfBucklLenLyRel.setEditable(true);
    	this.tfBucklLenLyRel.setEnabled(false);
    	//this.tfBucklLenLx.setUI(new HintTextFieldUI("Recommended range : 1L - 2L"));
    	this.tfBucklLenLyRel.setText(Double.toString(GuiAnalysisParams.buckLenLyRel));
    	this.lBuckLenLyRel = new JLabel("Buckling Length lo,y = ");
    	this.lBuckLenLyRel.setToolTipText("Buckling length lo,y is recommended to be obtained acc. to Eq. 5.16 DIN EN 1992-1-1");
    	this.lBuckLenLyRel.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lBuckLenLyRel.setOpaque(true);
    	this.lBuckLenLyUnitRel = new JLabel(" [ % ] lo");
    	this.lBuckLenLyUnitRel.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lBuckLenLyUnitRel.setOpaque(true);
    	this.pBucklLenLyRel = new JPanel(new FlowLayout());
    	this.pBucklLenLyRel.add(this.lBuckLenLyRel);
    	this.pBucklLenLyRel.add(this.tfBucklLenLyRel);
    	this.pBucklLenLyRel.add(this.lBuckLenLyUnitRel);
    	
    	this.tfBucklLenLzRel = new JTextField(20);
    	this.tfBucklLenLzRel.setEditable(true);
    	this.tfBucklLenLzRel.setEnabled(false);
    	//this.tfBucklLenLx.setUI(new HintTextFieldUI("Recommended range : 1L - 2L"));
    	this.tfBucklLenLzRel.setText(Double.toString(GuiAnalysisParams.buckLenLzRel));
    	this.lBuckLenLzRel = new JLabel("Buckling Length lo,z = ");
    	this.lBuckLenLzRel.setToolTipText("Buckling length lo,z is recommended to be obtained acc. to Eq. 5.16 DIN EN 1992-1-1");
    	this.lBuckLenLzRel.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lBuckLenLzRel.setOpaque(true);
    	this.lBuckLenLzUnitRel = new JLabel(" [ % ] lo");
    	this.lBuckLenLzUnitRel.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lBuckLenLzUnitRel.setOpaque(true);
    	this.pBucklLenLzRel = new JPanel(new FlowLayout());
    	this.pBucklLenLzRel.add(this.lBuckLenLzRel);
    	this.pBucklLenLzRel.add(this.tfBucklLenLzRel);
    	this.pBucklLenLzRel.add(this.lBuckLenLzUnitRel);


    	this.tfImperfectionY = new JTextField(20);
    	this.tfImperfectionY.setEditable(true);
    	//this.tfImperfection.setUI(new HintTextFieldUI("Recommended range : L/100 - L/200"));
    	this.tfImperfectionY.setText(Double.toString(GuiAnalysisParams.imperfectionY));
    	this.lImperfectionY = new JLabel("Imperfection e,i,y = ");
    	this.lImperfectionY.setToolTipText("The imperfection is recommended to be obtained as L/200 for general purposes");
    	this.lImperfectionY.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lImperfectionY.setOpaque(true);
    	this.lImperfectionYUnit = new JLabel("[ mm ]");
    	this.lImperfectionYUnit.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lImperfectionYUnit.setOpaque(true);
    	this.pImperfectionY = new JPanel(new FlowLayout());
    	this.pImperfectionY.add(this.lImperfectionY);
    	this.pImperfectionY.add(this.tfImperfectionY);
    	this.pImperfectionY.add(this.lImperfectionYUnit);
    	
    	this.tfImperfectionZ = new JTextField(20);
    	this.tfImperfectionZ.setEditable(true);
    	//this.tfImperfection.setUI(new HintTextFieldUI("Recommended range : L/100 - L/200"));
    	this.tfImperfectionZ.setText(Double.toString(GuiAnalysisParams.imperfectionZ));
    	this.lImperfectionZ = new JLabel("Imperfection e,i,z = ");
    	this.lImperfectionZ.setToolTipText("The imperfection is recommended to be obtained as L/200 for general purposes");
    	this.lImperfectionZ.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lImperfectionZ.setOpaque(true);
    	this.lImperfectionZUnit = new JLabel("[ mm ]");
    	this.lImperfectionZUnit.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
    	this.lImperfectionZUnit.setOpaque(true);
    	this.pImperfectionZ = new JPanel(new FlowLayout());
    	this.pImperfectionZ.add(this.lImperfectionZ);
    	this.pImperfectionZ.add(this.tfImperfectionZ);
    	this.pImperfectionZ.add(this.lImperfectionZUnit);
    	
    	this.btnOk = new JButton();
		this.btnOk = new JButton("Confirm");
		this.btnOk.setEnabled(true);
		this.btnOk.setVerticalAlignment(SwingConstants.CENTER);
		this.btnOk.setHorizontalAlignment(SwingConstants.CENTER);
		this.btnOk.setVerticalTextPosition(SwingConstants.CENTER);
		this.btnOk.setHorizontalTextPosition(SwingConstants.CENTER);
		this.btnOk.setPreferredSize(new Dimension(GuiAnalysis.screenWidth/8,GuiAnalysis.screenHeight/20));
		this.btnOk.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				boolean isInputCorrect = false;
				
				try
				{
					isInputCorrect = true;
					String sBuckLenLyRel = null;
					String sBuckLenLzRel = null;
					String sBuckLenLyAbs = null;
					String sBuckLenLzAbs = null;
					
					if (selectedBucklingLengthOption == eBucklingLengthOption.ABSOLUTE.ordinal())
					{
						sBuckLenLyAbs = tfBucklLenLyAbs.getText();
						sBuckLenLzAbs = tfBucklLenLzAbs.getText();
						if (sBuckLenLyAbs!=null) GuiAnalysisParams.buckLenLyAbs = Double.parseDouble(sBuckLenLyAbs);
						if (sBuckLenLzAbs!=null) GuiAnalysisParams.buckLenLzAbs = Double.parseDouble(sBuckLenLzAbs);
						GuiAnalysisParams.buckLenLyRel = (double)(GuiAnalysisParams.buckLenLyAbs / GuiAnalysisParams.memberTrueLength);
						GuiAnalysisParams.buckLenLzRel = (double)(GuiAnalysisParams.buckLenLzAbs / GuiAnalysisParams.memberTrueLength);						
					}
					else
					{
						sBuckLenLyRel = tfBucklLenLyRel.getText();
						sBuckLenLzRel = tfBucklLenLzRel.getText();
						if (sBuckLenLyRel!=null) GuiAnalysisParams.buckLenLyRel = Double.parseDouble(sBuckLenLyRel);
						if (sBuckLenLzRel!=null) GuiAnalysisParams.buckLenLzRel = Double.parseDouble(sBuckLenLzRel);
						GuiAnalysisParams.buckLenLyAbs = (double)(((GuiAnalysisParams.buckLenLyRel)/100.0) * GuiAnalysisParams.memberTrueLength);
						GuiAnalysisParams.buckLenLzAbs = (double)(((GuiAnalysisParams.buckLenLzRel)/100.0) * GuiAnalysisParams.memberTrueLength);
					}

					tfBucklLenLyRel.setText(Double.toString(GuiAnalysisParams.buckLenLyRel));
					tfBucklLenLzRel.setText(Double.toString(GuiAnalysisParams.buckLenLzRel));
					tfBucklLenLyAbs.setText(Double.toString(GuiAnalysisParams.buckLenLyAbs));
					tfBucklLenLzAbs.setText(Double.toString(GuiAnalysisParams.buckLenLzAbs));
					
					String sCreepFactor = tfCreepFactor.getText();
					String sImperfectionY = tfImperfectionY.getText();
					String sImperfectionZ = tfImperfectionZ.getText();
					if (sCreepFactor!=null) GuiAnalysisParams.creepFactor = Double.parseDouble(sCreepFactor);
					if (sImperfectionY!=null) GuiAnalysisParams.imperfectionY = Double.parseDouble(sImperfectionY);
					if (sImperfectionZ!=null) GuiAnalysisParams.imperfectionZ = Double.parseDouble(sImperfectionZ);
				}
				catch(NullPointerException npe)
				{
					isInputCorrect = false;
					JOptionPane.showMessageDialog(null, "Cannot parse double value", "Analysis Error", JOptionPane.ERROR_MESSAGE);
				}
				catch(NumberFormatException nfe)
				{
					isInputCorrect = false;
					JOptionPane.showMessageDialog(null, "Cannot parse double value", "Analysis Error", JOptionPane.ERROR_MESSAGE);
				}

				if (isInputCorrect)
				{
					//check for negative or null values
					if (GuiAnalysisParams.buckLenLyRel <= 0.0 || GuiAnalysisParams.buckLenLzRel <= 0.0 || //
							GuiAnalysisParams.buckLenLyAbs <= 0.0 || GuiAnalysisParams.buckLenLzAbs <= 0.0 || //
							GuiAnalysisParams.creepFactor < 0.0 || GuiAnalysisParams.imperfectionY < 0.0 || GuiAnalysisParams.imperfectionZ < 0.0)
					{
						JOptionPane.showMessageDialog(null, "Negative or zero entries are not allowed", "Analysis error", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						GuiAnalysisParams.userModifiedBucklLength = true;
						pGuiAnalysis.setEnabled(true); //set the main gui back on to active
						GuiAnalysisParams.this.setVisible(false);
					}
				}
			}
		});
    	this.pBtnOk = new JPanel(new FlowLayout());
    	this.pBtnOk.add(this.btnOk);
    	
    	

    	this.btnCancel = new JButton();
		this.btnCancel = new JButton("Cancel");
		this.btnCancel.setEnabled(true);
		this.btnCancel.setVerticalAlignment(SwingConstants.CENTER);
		this.btnCancel.setHorizontalAlignment(SwingConstants.CENTER);
		this.btnCancel.setVerticalTextPosition(SwingConstants.CENTER);
		this.btnCancel.setHorizontalTextPosition(SwingConstants.CENTER);
		this.btnCancel.setPreferredSize(new Dimension(GuiAnalysis.screenWidth/8,GuiAnalysis.screenHeight/20));
    	this.pBtnCancel = new JPanel(new FlowLayout());
    	this.pBtnCancel.add(this.btnCancel);
    	this.btnCancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				GuiAnalysisParams.this.setVisible(false);
				pGuiAnalysis.setEnabled(true);
				pGuiAnalysis.setVisible(true);
				pGuiAnalysis.setAutoRequestFocus(true);
			}
			
		});
    	
    	this.pSouth = new JPanel(new FlowLayout());
    	this.pSouth.add(pBtnOk);
    	this.pSouth.add(pBtnCancel);
    	
		this.jpanelAnalParams = new JPanel(new GridLayout(0,1));
		this.jpanelAnalParams.setBorder(BorderFactory.createTitledBorder("General Parameters"));
		this.jpanelAnalParams.add(pCreepFactor);
		this.jpanelAnalParams.add(pImperfectionY);
		this.jpanelAnalParams.add(pImperfectionZ);

		this.rbRelativeBucklLength = new JRadioButton("Relative to Member Length");
		this.rbRelativeBucklLength.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	        	 
	        	 if (GuiAnalysisParams.selectedBucklingType == eBucklingType.FRAME_COMPRESSION_BENDING.ordinal())
	        	 {
	        		 tfBucklLenLyRel.setEnabled(false);
	        		 tfBucklLenLzRel.setEnabled(false);
	        		 tfBucklLenLyAbs.setEnabled(false);
	        		 tfBucklLenLzAbs.setEnabled(false);
	        	 }
	        	 else
	        	 {
	        		 tfBucklLenLyRel.setEnabled(true);
	        		 tfBucklLenLzRel.setEnabled(true);
	        		 tfBucklLenLyAbs.setEnabled(false);
	        		 tfBucklLenLzAbs.setEnabled(false);
	        		 selectedBucklingLengthOption = eBucklingLengthOption.RELATIVE.ordinal();
	        	 }
	         }
	      });
		this.rbAbsoluteBucklLength = new JRadioButton("Absolute to Member Length",true);
		this.rbAbsoluteBucklLength.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	        	 
	        	 if (GuiAnalysisParams.selectedBucklingType == eBucklingType.FRAME_COMPRESSION_BENDING.ordinal())
	        	 {
	        		 tfBucklLenLyRel.setEnabled(false);
	        		 tfBucklLenLzRel.setEnabled(false);
	        		 tfBucklLenLyAbs.setEnabled(false);
	        		 tfBucklLenLzAbs.setEnabled(false);
	        	 }
	        	 else
	        	 {
	        		 tfBucklLenLyRel.setEnabled(false);
	        		 tfBucklLenLzRel.setEnabled(false);
	        		 tfBucklLenLyAbs.setEnabled(true);
	        		 tfBucklLenLzAbs.setEnabled(true);
	        		 selectedBucklingLengthOption = eBucklingLengthOption.ABSOLUTE.ordinal();
	        	 }
	         }
	      });
		this.btnGroupBucklLength = new ButtonGroup();
		this.btnGroupBucklLength.add(this.rbRelativeBucklLength);
		this.btnGroupBucklLength.add(this.rbAbsoluteBucklLength);
		this.pnlBucklLengthRadioButtons = new JPanel(new FlowLayout());
		this.pnlBucklLengthRadioButtons.add(this.rbRelativeBucklLength);
		this.pnlBucklLengthRadioButtons.add(this.rbAbsoluteBucklLength);
		
		JPanel pnlBucklLengthFieldsLeft = new JPanel(new GridLayout(0,1));
		pnlBucklLengthFieldsLeft.add(this.rbRelativeBucklLength);
		pnlBucklLengthFieldsLeft.add(this.pBucklLenLyRel);
		pnlBucklLengthFieldsLeft.add(this.pBucklLenLzRel);
		
		JPanel pnlBucklLengthFieldsRight = new JPanel(new GridLayout(0,1));
		pnlBucklLengthFieldsRight.add(this.rbAbsoluteBucklLength);
		pnlBucklLengthFieldsRight.add(this.pBucklLenLyAbs);
		pnlBucklLengthFieldsRight.add(this.pBucklLenLzAbs);
		
		this.pnlBucklLength = new JPanel(new GridLayout(1,0)); //one row X X X and multiple columns equally spaced
		this.pnlBucklLength.setBorder(BorderFactory.createTitledBorder("Buckling Parameters"));
		this.pnlBucklLength.add(pnlBucklLengthFieldsLeft);
		this.pnlBucklLength.add(pnlBucklLengthFieldsRight);
		
	    this.cbActionListener = new ComboBoxActionListener();
	    this.cbItemListener = new ComboBoxItemListener();
		
		
		this.cbBucklingType = new JComboBox<String>(GuiAnalysisParams.implementedBucklingTypes);	
		this.cbBucklingType.setOpaque(true);
		this.cbBucklingType.addActionListener(this.cbActionListener);
		this.cbBucklingType.addItemListener(this.cbItemListener);
		this.cbBucklingType.setSelectedItem(GuiAnalysisParams.implementedBucklingTypes[GuiAnalysisParams.selectedBucklingType]);
		this.jpanelMemberType = new JPanel(new BorderLayout());
		this.jpanelMemberType.setBorder(BorderFactory.createTitledBorder("Member Buckling Type"));
		this.jpanelMemberType.add(cbBucklingType,BorderLayout.CENTER);
		
		this.cbNationalAnnex = new JComboBox<String>(GuiAnalysisParams.implementedNationalAnnexes);
		this.cbNationalAnnex.setOpaque(true);
		this.cbNationalAnnex.addActionListener(this.cbActionListener);
		this.cbNationalAnnex.addItemListener(this.cbItemListener);
		this.cbNationalAnnex.setSelectedItem(GuiAnalysisParams.implementedNationalAnnexes[GuiAnalysisParams.selectedNationalAnnex]);
		this.jpanelNationalAnnex = new JPanel(new BorderLayout());
		this.jpanelNationalAnnex.setBorder(BorderFactory.createTitledBorder("Buckling Limits acc. National Annex"));
		this.jpanelNationalAnnex.add(cbNationalAnnex,BorderLayout.CENTER);
		
		this.cbSwayType = new JComboBox<String>(GuiAnalysisParams.implementedSwayTypes);
		this.cbSwayType.setOpaque(true);
		this.cbSwayType.addActionListener(this.cbActionListener);
		this.cbSwayType.addItemListener(this.cbItemListener);
		this.cbSwayType.setSelectedItem(GuiAnalysisParams.implementedSwayTypes[GuiAnalysisParams.selectedSwayType]);
		this.jpanelSwayType = new JPanel(new BorderLayout());
		this.jpanelSwayType.setBorder(BorderFactory.createTitledBorder("Member Sway Type"));
		this.jpanelSwayType.add(cbSwayType,BorderLayout.CENTER);

		//---
		this.jpanelMemberTypeAndNationalAnnexAndSwayType = new JPanel(new GridLayout(0,1));
		this.jpanelMemberTypeAndNationalAnnexAndSwayType.add(this.jpanelMemberType);
		this.jpanelMemberTypeAndNationalAnnexAndSwayType.add(this.jpanelNationalAnnex);
		this.jpanelMemberTypeAndNationalAnnexAndSwayType.add(this.jpanelSwayType);
		
		this.pnlBucklLength.add(this.jpanelMemberTypeAndNationalAnnexAndSwayType);
		
		
		this.add(this.jpanelAnalParams,BorderLayout.NORTH);
		this.add(this.pnlBucklLength,BorderLayout.CENTER);
		this.add(this.pSouth,BorderLayout.SOUTH);
		
	    //add the desktop logo icon
	    this.desktopImage = null;
	  	URL imgURL = getClass().getClassLoader().getResource(GuiMain.desktopIconPath);
	  	if (imgURL != null) {
	  		this.desktopImage = new ImageIcon(imgURL);
	  		this.setIconImage(this.desktopImage.getImage());
	  	}
	  	else
	  	{
			JOptionPane.showMessageDialog(null, "Fatal Error opening image file " + GuiMain.desktopIconPath, "GUI Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
	  	}

    	//Display the window.
    	this.pack();

    	//upon closing return back to GuiAnalysis
		this.addWindowListener(new WindowAdapter()
		{
	         @Override
	         public void windowClosing(WindowEvent evt) {
		    	pGuiAnalysis.setEnabled(true); //set the main gui back on to active
	         }
		}
		);
    }  
}