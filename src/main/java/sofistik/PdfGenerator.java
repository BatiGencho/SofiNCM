package sofistik;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import sofistik.AnalysisMemberEC3Analysis;
import javax.swing.JOptionPane;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import gui.GuiAnalysisParams;
import gui.GuiAnalysisParams.eBucklingType;
import gui.GuiAnalysisParams.eSwayType;
 
public class PdfGenerator {
 
	
	private final static String pathToPdfLogo = System.getProperty("user.dir") + "\\src\\main\\resources\\images\\sofLogo.png";
	private static String pdfTitleAndPath = null;
	
	//document
	private static Document document;
	private static Image image;
	private OutputStream file;
	
	//tables common
	private static PdfPTable sysDateTable;
	private static PdfPTable normDateTable;
	private static PdfPTable materialXDateTable;
	private static PdfPTable sectionXDateTable;
	private static PdfPTable selectedLoadCasesDateTable;
	private static PdfPTable selectedMaterialsverviewDateTable;
	private static PdfPTable selectedCrossSectionsOverviewDateTable;
	private static PdfPTable memberTopologyOverviewDateTable;
	private static PdfPTable memberForcesOverviewDateTable;
	private static PdfPTable memberNodesOverviewDateTable;
	
	//tables nachweise
	private static PdfPTable memberNachweiseDateTable;
	
	private static BaseFont bFontTitles;
	private static Font fontTitles;
	
	static {
		pdfTitleAndPath = null;
	}
	
    public void generatePdf(final String customerPdfPath) {
 
        try {
 
        	  PdfGenerator.pdfTitleAndPath = new String(customerPdfPath);

              this.file = new FileOutputStream(new File(PdfGenerator.pdfTitleAndPath));
              PdfGenerator.document = new Document();
	          //Document document = new Document(new Rectangle(14400, 14400));
	          PdfWriter writer = PdfWriter.getInstance(document, this.file);
	          writer.setPdfVersion(PdfWriter.VERSION_1_6);
 
	          //Inserting Sofistik Image into the PDF page
	          PdfGenerator.image = Image.getInstance (pathToPdfLogo);
	          PdfGenerator.image.scaleAbsolute(350f, 80f);//image width,height
	          

	          //Inserting List in PDF
	

	          //Text formating in PDF
//	          Chunk chunk=new Chunk("Welecome To Java4s Programming Blog...");
//	          chunk.setUnderline(+1f,-2f);//1st co-ordinate is for line width,2nd is space between
//	          Chunk chunk1=new Chunk("Php4s.com");
//	          chunk1.setUnderline(+4f,-8f);
//	          chunk1.setBackground(new BaseColor (17, 46, 193));      

	          //bFontTitles = BaseFont.createFont(FontFactory.HELVETICA,"CP1253", BaseFont.EMBEDDED);
	          bFontTitles = BaseFont.createFont(FontFactory.HELVETICA,BaseFont.CP1257, BaseFont.EMBEDDED);
	          fontTitles = new Font(bFontTitles, 12, Font.BOLD);

	          
	          //construct the entire pdf document
	          //-----------opening
	          PdfGenerator.document.open();//PDF document opened........			       

	          //-----------page 1 --- x NORM AND SYSTEM INFO (for both BASIC / DETAILED)
	          PdfGenerator.document.add(image);
	          PdfGenerator.document.add(Chunk.NEWLINE);

	          //document.add(Chunk.NEWLINE);   //Something like in HTML :-)
	          //document.add(chunk);
	          //document.add(chunk1);

	          //
	          PdfGenerator.document.add(new Paragraph("Analysis Report"));
	          //
	          //
	          PdfGenerator.document.add(new Paragraph("Document Generated On - " + new Date().toString()));
	          //
	          //
	          //print the norm table
	          PdfGenerator.printPdfTable_NormData();
	          //
	          //
	          //print the system data table
	          PdfGenerator.printPdfTable_SystemData();
	          //

	          
	          //---------------page 2 --- x MATERIALS
	          //document.newPage();
	          //document.add(image);
	          
	          //print the materials overview table
	          if (AnalysisOptions.selectedOutputLevel == AnalysisOutputLevel.BASIC) {
	        	  PdfGenerator.printPdfTable_addMaterialsOverviewTable();
	          }

	          //print a detailed overview of each material
	          if (AnalysisOptions.selectedOutputLevel == AnalysisOutputLevel.DETAILED) {
	        	  for (Entry<Integer, AnalysisMaterial> iMat : AnalysisMemberEC3Analysis.arrMats.entrySet())
	        	  {
	        		  PdfGenerator.printPdfTable_MaterialX(iMat.getValue());
	        	  }
	          }
	          
	          //---------------page x --- SECTIONS
	          //document.newPage();
	          //document.add(image);
	          
	          //print the sections overview table
	          if (AnalysisOptions.selectedOutputLevel == AnalysisOutputLevel.BASIC) {
	        	  PdfGenerator.printPdfTable_addSectionsOverviewTable();
	          }
	          //print a detailed overview of each cross-section
	          if (AnalysisOptions.selectedOutputLevel == AnalysisOutputLevel.DETAILED) {
	        	  for (Entry<Integer, AnalysisCSection> iSect : AnalysisMemberEC3Analysis.arrSects.entrySet())
	        	  {
	        		  PdfGenerator.printPdfTable_CSectionX(iSect.getValue());
	        	  }
	          }
	          
	          //---------------page x --- MEMBER / SUBELEMENTS TOPOLOGY
	          //table for topology of the beam (Similar to the output in console)
	          PdfGenerator.printPdfTable_addMemberTopologyOverviewTable();
	          
	          document.add(Chunk.NEWLINE);
	          
	          //---------------page x --- LOAD CASES
	          //print an overview of the load cases (inside BASIC / DETAILED differentiation)
	          PdfGenerator.printPdfTable_addSelectedLoadCasesOverviewTable();
	          
	          document.add(Chunk.NEWLINE);
	          
	          //table for member forces and displacements for nodes and subelements for each load case
	          PdfGenerator.printPdfTable_addSelectedLoadCasesMemberForcesOverviewTable();
	          
	          document.add(Chunk.NEWLINE);
	          
	          
	          PdfGenerator.printPdfTable_addSelectedLoadCasesNodalForcesOverviewTable();
	          
	          document.add(Chunk.NEWLINE);
	          
	          //---------------page x --- EC3 LIMITING SLENDERNESS TABLE FOR EACH SELECTED LC
	          
	          PdfGenerator.printPdfTable_addLoadCaseNachweisLimitingSlenderness();
	          
	          

        } catch (Exception e) {
				JOptionPane.showMessageDialog(null, "An error occured while generating pdf output ", "Pdf Generator error", JOptionPane.ERROR_MESSAGE);
        }
        finally {
        	
	          //----closing
	          if (document != null) document.close();
	          if (this.file != null)
				try {
					this.file.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "An error occured while generating pdf output ", "Pdf Generator error", JOptionPane.ERROR_MESSAGE);
				}
        }
    }
   
    public static void printPdfTable_addMemberTopologyOverviewTable() throws DocumentException
    {
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
        //---------------------------  member topology    ------------------------
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
    	
     	PdfGenerator.memberTopologyOverviewDateTable = new PdfPTable(8);
    	PdfPCell cell = null;
    	cell = new PdfPCell (new Paragraph (" ANALYSIS MEMBER " + AnalysisOptions.selectedStrLine + " TOPOLOGY OVERVIEW ",fontTitles));
        cell.setColspan(8);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10.0f);
        cell.setBackgroundColor (new BaseColor (140, 221, 8));
    	
        PdfGenerator.memberTopologyOverviewDateTable.addCell(cell);
        PdfGenerator.memberTopologyOverviewDateTable.setSpacingBefore(15.0f);       // Space Before table starts, like margin-top in CSS
        PdfGenerator.memberTopologyOverviewDateTable.setSpacingAfter(15.0f);        // Space After table starts, like margin-Bottom in CSS
        
        PdfPCell cellMemberId = new PdfPCell (new Paragraph ("Member Id"));
        cellMemberId.setColspan(1);
        cellMemberId.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellMemberId.setVerticalAlignment(Element.ALIGN_CENTER);
        cellMemberId.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellMemberId);
        
        PdfPCell cellSubElementGroupId = new PdfPCell (new Paragraph ("Group Id"));
        cellSubElementGroupId.setColspan(1);
        cellSubElementGroupId.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellSubElementGroupId.setVerticalAlignment(Element.ALIGN_CENTER);
        cellSubElementGroupId.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementGroupId);
        
        PdfPCell cellSubElementId = new PdfPCell (new Paragraph ("Beam Id"));
        cellSubElementId.setColspan(1);
        cellSubElementId.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellSubElementId.setVerticalAlignment(Element.ALIGN_CENTER);
        cellSubElementId.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementId);
        
        PdfPCell cellSubElementNodes = new PdfPCell (new Paragraph ("Nodes B-E"));
        cellSubElementNodes.setColspan(1);
        cellSubElementNodes.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellSubElementNodes.setVerticalAlignment(Element.ALIGN_CENTER);
        cellSubElementNodes.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementNodes);
        
        PdfPCell cellSubElementSections = new PdfPCell (new Paragraph ("Sections B-E"));
        cellSubElementSections.setColspan(1);
        cellSubElementSections.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellSubElementSections.setVerticalAlignment(Element.ALIGN_CENTER);
        cellSubElementSections.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementSections);
        
        PdfPCell cellSubElementMaterials = new PdfPCell (new Paragraph ("Materials"));
        cellSubElementMaterials.setColspan(1);
        cellSubElementMaterials.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellSubElementMaterials.setVerticalAlignment(Element.ALIGN_CENTER);
        cellSubElementMaterials.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementMaterials);
        
        PdfPCell cellSubElementLength = new PdfPCell (new Paragraph ("Length [m]"));
        cellSubElementLength.setColspan(1);
        cellSubElementLength.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellSubElementLength.setVerticalAlignment(Element.ALIGN_CENTER);
        cellSubElementLength.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementLength);
        
        PdfPCell cellSubElementLoadCases = new PdfPCell (new Paragraph ("Load Cases"));
        cellSubElementLoadCases.setColspan(1);
        cellSubElementLoadCases.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellSubElementLoadCases.setVerticalAlignment(Element.ALIGN_CENTER);
        cellSubElementLoadCases.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementLoadCases);
        

		//get member design data
		AnalysisMember member = AnalysisMembersCollection.membersCollection.get(AnalysisOptions.selectedStrLine);
		
		
		//MEMBER ID
        PdfPCell cellMemberIdValue = new PdfPCell (new Paragraph (AnalysisOptions.selectedStrLine + " "  + " [ " + member.getStrLineTitle().trim() + " ]"));
        cellMemberIdValue.setColspan(1);
        cellMemberIdValue.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellMemberIdValue.setVerticalAlignment(Element.ALIGN_CENTER);
        cellMemberIdValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellMemberIdValue);
		
		int iSubElemsCounter = 0; double totalL = 0.0;
		for (AnalysisMemberSubElement arrSubElem : member.arrSubElements)
		{
			totalL += arrSubElem.dL;
			
			//EACH NEXT MEMBER ID [WHITESPACE]
			if (iSubElemsCounter > 0) //for each new add subelement add an empty cell under member id
			{
				PdfGenerator.memberTopologyOverviewDateTable.addCell(" ");
			}
		
			//MEMBER-SUBELEMENT GROUP
	        PdfPCell cellSubElementGroupValue = new PdfPCell (new Paragraph (Integer.toString(AnalysisOptions.selectedGroup)));
	        cellSubElementGroupValue.setColspan(1);
	        cellSubElementGroupValue.setHorizontalAlignment (Element.ALIGN_CENTER);
	        cellSubElementGroupValue.setVerticalAlignment(Element.ALIGN_CENTER);
	        cellSubElementGroupValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
	        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementGroupValue);
			
			//MEMBER-SUBELEMENT ID
	        PdfPCell cellSubElementIdValue = new PdfPCell (new Paragraph (Integer.toString(arrSubElem.iSubElemCdbIndex)));
	        cellSubElementIdValue.setColspan(1);
	        cellSubElementIdValue.setHorizontalAlignment (Element.ALIGN_CENTER);
	        cellSubElementIdValue.setVerticalAlignment(Element.ALIGN_CENTER);
	        cellSubElementIdValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
	        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementIdValue);
			
			//MEMBER-SUBELEMENT NODES
	        PdfPCell cellSubElementNodesValues = new PdfPCell (new Paragraph (arrSubElem.nodeId1 + " - " + arrSubElem.nodeId2));
	        cellSubElementNodesValues.setColspan(1);
	        cellSubElementNodesValues.setHorizontalAlignment (Element.ALIGN_CENTER);
	        cellSubElementNodesValues.setVerticalAlignment(Element.ALIGN_CENTER);
	        cellSubElementNodesValues.setBackgroundColor (new BaseColor (255, 255, 255));   //white
	        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementNodesValues);
			
			//MEMBER-SUBELEMENT SECTIONS
	        PdfPCell cellSubElementSectionsValues = new PdfPCell (new Paragraph (arrSubElem.sectId1 + " - " + arrSubElem.sectId2));
	        cellSubElementSectionsValues.setColspan(1);
	        cellSubElementSectionsValues.setHorizontalAlignment (Element.ALIGN_CENTER);
	        cellSubElementSectionsValues.setVerticalAlignment(Element.ALIGN_CENTER);
	        cellSubElementSectionsValues.setBackgroundColor (new BaseColor (255, 255, 255));   //white
	        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementSectionsValues);
			
			//MEMBER-SUBELEMENT MATS
	        StringBuilder strBMats = new StringBuilder();
			if (arrSubElem.mats != null && arrSubElem.mats.length != 0) //print all lcs for the sub-element with titles
			{
				for (int k=0 ; k < arrSubElem.mats.length; k++)
				{
					if (k==arrSubElem.mats.length-1) {
						strBMats.append(arrSubElem.mats[k]);
					}
					else {
						strBMats.append(arrSubElem.mats[k] + " , ");
					}
				}
		        PdfPCell cellSubElementMaterialsValues = new PdfPCell (new Paragraph (strBMats.toString()));
		        cellSubElementMaterialsValues.setColspan(1);
		        cellSubElementMaterialsValues.setHorizontalAlignment (Element.ALIGN_CENTER);
		        cellSubElementMaterialsValues.setVerticalAlignment(Element.ALIGN_CENTER);
		        cellSubElementMaterialsValues.setBackgroundColor (new BaseColor (255, 255, 255));   //white
		        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementMaterialsValues);
			}
			else
			{
				PdfGenerator.memberTopologyOverviewDateTable.addCell("[ none ]");
			}
			
			//MEMBER-SUBELEMENT LENGTH
	        PdfPCell cellSubElementLengthValue = new PdfPCell (new Paragraph (Double.toString(arrSubElem.dL)));
	        cellSubElementLengthValue.setColspan(1);
	        cellSubElementLengthValue.setHorizontalAlignment (Element.ALIGN_CENTER);
	        cellSubElementLengthValue.setVerticalAlignment(Element.ALIGN_CENTER);
	        cellSubElementLengthValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
	        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementLengthValue);
			
			//MEMBER-SUBELEMENT LOAD CASES
	        StringBuilder strBLcs = new StringBuilder();
			if (arrSubElem.lcs != null && arrSubElem.lcs.length != 0) //print all lcs for the sub-element with titles
			{
				for (int k=0 ; k < arrSubElem.lcs.length; k++)
				{
					if (k==arrSubElem.lcs.length-1) {
						strBLcs.append(arrSubElem.lcs[k]);
					}
					else {
						strBLcs.append(arrSubElem.lcs[k] + " , ");
					}	
				}
		        PdfPCell cellSubElementLoadCasesValues = new PdfPCell (new Paragraph (strBLcs.toString()));
		        cellSubElementLoadCasesValues.setColspan(1);
		        cellSubElementLoadCasesValues.setHorizontalAlignment (Element.ALIGN_CENTER);
		        cellSubElementLoadCasesValues.setVerticalAlignment(Element.ALIGN_CENTER);
		        cellSubElementLoadCasesValues.setBackgroundColor (new BaseColor (255, 255, 255));   //white
		        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellSubElementLoadCasesValues);
			}
			else
			{
				PdfGenerator.memberTopologyOverviewDateTable.addCell("[ none ] ");
			}
			
			//next sub beam
			iSubElemsCounter++;
		}

		//provide some brief summary
    	PdfPCell cellTotalL = null;
		cellTotalL = new PdfPCell (new Paragraph (" ANALYSIS MEMBER TOTAL LENGTH : " + totalL + " [m]"));
		cellTotalL.setColspan(8);
        cellTotalL.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellTotalL.setVerticalAlignment(Element.ALIGN_CENTER);
        cellTotalL.setPadding(10.0f);
        cellTotalL.setBackgroundColor (new BaseColor (234,173,234));
        PdfGenerator.memberTopologyOverviewDateTable.addCell(cellTotalL);
        
    	PdfPCell cellTotalMemberSubElements = null;
    	cellTotalMemberSubElements = new PdfPCell (new Paragraph (" ANALYSIS MEMBER SUBELEMENTS : " + member.getnSubElems()));
    	cellTotalMemberSubElements.setColspan(8);
		cellTotalMemberSubElements.setHorizontalAlignment (Element.ALIGN_CENTER);
		cellTotalMemberSubElements.setVerticalAlignment(Element.ALIGN_CENTER);
		cellTotalMemberSubElements.setPadding(10.0f);
		cellTotalMemberSubElements.setBackgroundColor (new BaseColor (234,173,234));
		PdfGenerator.memberTopologyOverviewDateTable.addCell(cellTotalMemberSubElements);
		
    	PdfPCell cellAllSelectedLoadCases = null;
    	cellAllSelectedLoadCases = new PdfPCell (new Paragraph (" SELECTED FOR ANALYSIS LOAD CASES : " + AnalysisOptions.selectedLinearLoadCases.toString()));
    	cellAllSelectedLoadCases.setColspan(8);
    	cellAllSelectedLoadCases.setHorizontalAlignment (Element.ALIGN_CENTER);
    	cellAllSelectedLoadCases.setVerticalAlignment(Element.ALIGN_CENTER);
    	cellAllSelectedLoadCases.setPadding(10.0f);
    	cellAllSelectedLoadCases.setBackgroundColor (new BaseColor (234,173,234));
    	PdfGenerator.memberTopologyOverviewDateTable.addCell(cellAllSelectedLoadCases);
        
        //add table to document
        PdfGenerator.document.add(PdfGenerator.memberTopologyOverviewDateTable);
    }
    
    
    
    
    public static void printPdfTable_addSelectedLoadCasesNodalForcesOverviewTable() throws DocumentException
    {
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
        //---------------------------  nodal forces    ---------------------------
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
    	

		for ( Integer iLc : AnalysisOptions.selectedLinearLoadCases) //loop over the customer selected lcs tree set
		{
    	
			PdfGenerator.memberNodesOverviewDateTable = null;

			PdfGenerator.memberNodesOverviewDateTable = new PdfPTable(4);
			PdfPCell cell = null;
			cell = new PdfPCell (new Paragraph (" MEMBER " + AnalysisOptions.selectedStrLine + " NODAL RESULTS UNDER LOAD CASE " + iLc.intValue()));
			cell.setColspan(4);
			cell.setHorizontalAlignment (Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			//cell.setPadding(10.0f);
			cell.setBackgroundColor (new BaseColor (140, 221, 8));

			PdfGenerator.memberNodesOverviewDateTable.addCell(cell);
			//PdfGenerator.memberForcesOverviewDateTable.setSpacingBefore(15.0f);
			//PdfGenerator.memberForcesOverviewDateTable.setSpacingAfter(15.0f);

			PdfPCell cellNodeId = new PdfPCell (new Paragraph ("Node Id")); 		//----1
			cellNodeId.setColspan(1);
			cellNodeId.setHorizontalAlignment (Element.ALIGN_CENTER);
			cellNodeId.setVerticalAlignment(Element.ALIGN_CENTER);
			cellNodeId.setBackgroundColor (new BaseColor (235, 199, 158));
			PdfGenerator.memberNodesOverviewDateTable.addCell(cellNodeId);

			PdfPCell cellSupportsId = new PdfPCell (new Paragraph ("Supports")); 	//----2
			cellSupportsId.setColspan(1);
			cellSupportsId.setHorizontalAlignment (Element.ALIGN_CENTER);
			cellSupportsId.setVerticalAlignment(Element.ALIGN_CENTER);
			cellSupportsId.setBackgroundColor (new BaseColor (235, 199, 158));
			PdfGenerator.memberNodesOverviewDateTable.addCell(cellSupportsId);

			PdfPCell cellForces = new PdfPCell (new Paragraph ("Forces"));  		//----3
			cellForces.setColspan(1);
			cellForces.setHorizontalAlignment (Element.ALIGN_CENTER);
			cellForces.setVerticalAlignment(Element.ALIGN_CENTER);
			cellForces.setBackgroundColor (new BaseColor (235, 199, 158));
			PdfGenerator.memberNodesOverviewDateTable.addCell(cellForces);

			PdfPCell cellDisplacements = new PdfPCell (new Paragraph ("Displacements"));  //----4
			cellDisplacements.setColspan(1);
			cellDisplacements.setHorizontalAlignment (Element.ALIGN_CENTER);
			cellDisplacements.setVerticalAlignment(Element.ALIGN_CENTER);
			cellDisplacements.setBackgroundColor (new BaseColor (235, 199, 158));
			PdfGenerator.memberNodesOverviewDateTable.addCell(cellDisplacements);


			for (Integer nodeIndex : AnalysisMemberEC3Analysis.arrNodes.keySet()) //all nodes that are present in the selected member
			{
				//loop over all nodes and extract the ones that are in the current load case
				AnalysisNode n = AnalysisMemberEC3Analysis.arrNodes.get(nodeIndex);
				AnalysisForcesAndDisplElementNode res = n.hmLoadCaseResults.get(iLc);
				

				if (res !=null) //node is in the current iLc
				{

						//loop over the forces
						for (int k = 0; k < 7; k++)
						{
							//NODE ID (FIRST TOP LEFT CELL)
							PdfPCell cellNodeIdValue = new PdfPCell (new Paragraph (Integer.toString(n.m_nr))); //"Node Id"
							cellNodeIdValue.setColspan(1);
							cellNodeIdValue.setHorizontalAlignment (Element.ALIGN_CENTER);
							cellNodeIdValue.setVerticalAlignment(Element.ALIGN_CENTER);
							cellNodeIdValue.setBackgroundColor (new BaseColor (255, 255, 255));
							PdfGenerator.memberNodesOverviewDateTable.addCell(cellNodeIdValue);
							
							//NODE SUPPORTS
							PdfPCell cellNodeSupports;
							if (decryptSupportType(n.m_kfix) == null || decryptSupportType(n.m_kfix) == "") {	
								cellNodeSupports = new PdfPCell (new Paragraph (Integer.toString(n.m_kfix))); //"Supports"
							}
							else
							{
								cellNodeSupports = new PdfPCell (new Paragraph (decryptSupportType(n.m_kfix))); //"Supports"
							}
							cellNodeSupports.setColspan(1);
							cellNodeSupports.setHorizontalAlignment (Element.ALIGN_CENTER);
							cellNodeSupports.setVerticalAlignment(Element.ALIGN_CENTER);
							cellNodeSupports.setBackgroundColor (new BaseColor (255, 255, 255));
							PdfGenerator.memberNodesOverviewDateTable.addCell(cellNodeSupports);

							//NODAL FORCE
							switch (k)
							{
								case 0:
								{
									PdfPCell cellN = new PdfPCell (new Paragraph ("Nx = " + Float.toString(res.m_px) + decryptUnits(1151)));
									cellN.setColspan(1);
									cellN.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellN.setVerticalAlignment(Element.ALIGN_CENTER);
									cellN.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellN);
									break;
								}

								case 1:
								{
									PdfPCell cellNy = new PdfPCell (new Paragraph ("Ny = " + Float.toString(res.m_py) + decryptUnits(1151)));
									cellNy.setColspan(1);
									cellNy.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellNy.setVerticalAlignment(Element.ALIGN_CENTER);
									cellNy.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellNy);
									break;
								}
	
								case 2:
								{
									PdfPCell cellNz = new PdfPCell (new Paragraph ("Nz = " + Float.toString(res.m_pz) + decryptUnits(1151)));
									cellNz.setColspan(1);
									cellNz.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellNz.setVerticalAlignment(Element.ALIGN_CENTER);
									cellNz.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellNz);
									break;
								}
								
								case 3:
								{
									PdfPCell cellMx = new PdfPCell (new Paragraph ("Mx = " + Float.toString(res.m_mx) + decryptUnits(1152)));
									cellMx.setColspan(1);
									cellMx.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellMx.setVerticalAlignment(Element.ALIGN_CENTER);
									cellMx.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellMx);
									break;
								}
	
								case 4:
								{
									PdfPCell cellMy = new PdfPCell (new Paragraph ("My = " + Float.toString(res.m_my) + decryptUnits(1152)));
									cellMy.setColspan(1);
									cellMy.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellMy.setVerticalAlignment(Element.ALIGN_CENTER);
									cellMy.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellMy);
									break;
								}
	
								case 5:
								{
									PdfPCell cellMz = new PdfPCell (new Paragraph ("Mz = " + Float.toString(res.m_mz) + decryptUnits(1152)));
									cellMz.setColspan(1);
									cellMz.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellMz.setVerticalAlignment(Element.ALIGN_CENTER);
									cellMz.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellMz);
									break;
								}
								
								case 6:
								{
									PdfPCell cellMb = new PdfPCell (new Paragraph ("Mb = " + Float.toString(res.m_mb) + decryptUnits(1105)));
									cellMb.setColspan(1);
									cellMb.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellMb.setVerticalAlignment(Element.ALIGN_CENTER);
									cellMb.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellMb);
									break;
								}

							}
							
							//NODAL DISPLACEMENTS
							switch (k)
							{
								case 0:
								{
									PdfPCell cellUx = new PdfPCell (new Paragraph ("Ux = " + Float.toString(res.m_ux) + decryptUnits(1003)));
									cellUx.setColspan(1);
									cellUx.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellUx.setVerticalAlignment(Element.ALIGN_CENTER);
									cellUx.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellUx);
									break;
								}

								case 1:
								{
									PdfPCell cellUx = new PdfPCell (new Paragraph ("Uy = " + Float.toString(res.m_uy) +  decryptUnits(1003)));
									cellUx.setColspan(1);
									cellUx.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellUx.setVerticalAlignment(Element.ALIGN_CENTER);
									cellUx.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellUx);
									break;
								}
	
								case 2:
								{
									PdfPCell cellUz = new PdfPCell (new Paragraph ("Uz = " + Float.toString(res.m_uz) + decryptUnits(1003)));
									cellUz.setColspan(1);
									cellUz.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellUz.setVerticalAlignment(Element.ALIGN_CENTER);
									cellUz.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellUz);
									break;
								}
								
								case 3:
								{
									PdfPCell cellPhiX = new PdfPCell (new Paragraph ("Phix = " + Float.toString(res.m_urx)  + decryptUnits(1004)));
									cellPhiX.setColspan(1);
									cellPhiX.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellPhiX.setVerticalAlignment(Element.ALIGN_CENTER);
									cellPhiX.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellPhiX);
									break;
								}
	
								case 4:
								{
									PdfPCell cellPhiY = new PdfPCell (new Paragraph ("Phiy = " + Float.toString(res.m_ury) + decryptUnits(1004)));
									cellPhiY.setColspan(1);
									cellPhiY.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellPhiY.setVerticalAlignment(Element.ALIGN_CENTER);
									cellPhiY.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellPhiY);
									break;
								}
	
								case 5:
								{
									PdfPCell cellPhiZ = new PdfPCell (new Paragraph ("Phiz = " + Float.toString(res.m_urz) + decryptUnits(1004)));
									cellPhiZ.setColspan(1);
									cellPhiZ.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellPhiZ.setVerticalAlignment(Element.ALIGN_CENTER);
									cellPhiZ.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellPhiZ);
									break;
								}
								
								case 6:
								{
									PdfPCell cellPhiW = new PdfPCell (new Paragraph ("Phiw = " + Float.toString(res.m_urb) + decryptUnits(1105)));
									cellPhiW.setColspan(1);
									cellPhiW.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellPhiW.setVerticalAlignment(Element.ALIGN_CENTER);
									cellPhiW.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberNodesOverviewDateTable.addCell(cellPhiW);
									break;
								}

							}
							
							
						}


				}
				
			}

			//add lc table to document & move to next load case for the same structural line
			PdfGenerator.document.add(PdfGenerator.memberNodesOverviewDateTable);
			PdfGenerator.document.add(Chunk.NEWLINE);

		} //load cases loop
    	
    	
    }
    
    public static void printPdfTable_addSelectedLoadCasesMemberForcesOverviewTable() throws DocumentException
    {
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
        //---------------------------  member forces    --------------------------
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
    	
		for ( Integer iLc : AnalysisOptions.selectedLinearLoadCases) //loop over the customer selected lcs tree set
		{
    	
			PdfGenerator.memberTopologyOverviewDateTable = null;

			PdfGenerator.memberForcesOverviewDateTable = new PdfPTable(5);
			PdfPCell cell = null;
			cell = new PdfPCell (new Paragraph (" MEMBER " + AnalysisOptions.selectedStrLine + " UNDER LOAD CASE " + iLc.intValue()));
			cell.setColspan(5);
			cell.setHorizontalAlignment (Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_CENTER);
			//cell.setPadding(10.0f);
			cell.setBackgroundColor (new BaseColor (140, 221, 8));

			PdfGenerator.memberForcesOverviewDateTable.addCell(cell);
			//PdfGenerator.memberForcesOverviewDateTable.setSpacingBefore(15.0f);
			//PdfGenerator.memberForcesOverviewDateTable.setSpacingAfter(15.0f);

			PdfPCell cellMemberId = new PdfPCell (new Paragraph ("Member Id")); 		//----1
			cellMemberId.setColspan(1);
			cellMemberId.setHorizontalAlignment (Element.ALIGN_CENTER);
			cellMemberId.setVerticalAlignment(Element.ALIGN_CENTER);
			cellMemberId.setBackgroundColor (new BaseColor (235, 199, 158));
			PdfGenerator.memberForcesOverviewDateTable.addCell(cellMemberId);

			PdfPCell cellSubElementId = new PdfPCell (new Paragraph ("Beam Id")); //----2
			cellSubElementId.setColspan(1);
			cellSubElementId.setHorizontalAlignment (Element.ALIGN_CENTER);
			cellSubElementId.setVerticalAlignment(Element.ALIGN_CENTER);
			cellSubElementId.setBackgroundColor (new BaseColor (235, 199, 158));
			PdfGenerator.memberForcesOverviewDateTable.addCell(cellSubElementId);

			PdfPCell cellSubElementPositionX = new PdfPCell (new Paragraph ("Position X")); //----3
			cellSubElementPositionX.setColspan(1);
			cellSubElementPositionX.setHorizontalAlignment (Element.ALIGN_CENTER);
			cellSubElementPositionX.setVerticalAlignment(Element.ALIGN_CENTER);
			cellSubElementPositionX.setBackgroundColor (new BaseColor (235, 199, 158));
			PdfGenerator.memberForcesOverviewDateTable.addCell(cellSubElementPositionX);

			PdfPCell cellSubElementForces = new PdfPCell (new Paragraph ("Forces"));  		//----4
			cellSubElementForces.setColspan(1);
			cellSubElementForces.setHorizontalAlignment (Element.ALIGN_CENTER);
			cellSubElementForces.setVerticalAlignment(Element.ALIGN_CENTER);
			cellSubElementForces.setBackgroundColor (new BaseColor (235, 199, 158));
			PdfGenerator.memberForcesOverviewDateTable.addCell(cellSubElementForces);

			PdfPCell cellSubElementDisplacements = new PdfPCell (new Paragraph ("Displacements"));  //----5
			cellSubElementDisplacements.setColspan(1);
			cellSubElementDisplacements.setHorizontalAlignment (Element.ALIGN_CENTER);
			cellSubElementDisplacements.setVerticalAlignment(Element.ALIGN_CENTER);
			cellSubElementDisplacements.setBackgroundColor (new BaseColor (235, 199, 158));
			PdfGenerator.memberForcesOverviewDateTable.addCell(cellSubElementDisplacements);


			//get member design data
			AnalysisMember member = AnalysisMembersCollection.membersCollection.get(AnalysisOptions.selectedStrLine);


			for (AnalysisMemberSubElement arrSubElem : member.arrSubElements)
			{
				
				//get data for the load combination
				ArrayList<AnalysisForcesAndDisplElementBeam> lcData = arrSubElem.hmLoadCaseResults.get(iLc.intValue());
				if (lcData !=null)
				{
					//get data for positions x=0 and x=L of the subelement
					for (AnalysisForcesAndDisplElementBeam pX : lcData)
					{

						//loop over the forces
						for (int k = 0; k < 7; k++)
						{
							//MEMBER ID (FIRST TOP LEFT CELL)
							PdfPCell cellMemberIdValue = new PdfPCell (new Paragraph (AnalysisOptions.selectedStrLine + " [ "  + member.getStrLineTitle().trim() + " ]"));
							cellMemberIdValue.setColspan(1);
							cellMemberIdValue.setHorizontalAlignment (Element.ALIGN_CENTER);
							cellMemberIdValue.setVerticalAlignment(Element.ALIGN_CENTER);
							cellMemberIdValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
							PdfGenerator.memberForcesOverviewDateTable.addCell(cellMemberIdValue);
							
							//MEMBER-SUBELEMENT ID
							PdfPCell cellSubElementIdValue = new PdfPCell (new Paragraph (Integer.toString(arrSubElem.iSubElemCdbIndex)));
							cellSubElementIdValue.setColspan(1);
							cellSubElementIdValue.setHorizontalAlignment (Element.ALIGN_CENTER);
							cellSubElementIdValue.setVerticalAlignment(Element.ALIGN_CENTER);
							cellSubElementIdValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
							PdfGenerator.memberForcesOverviewDateTable.addCell(cellSubElementIdValue);

							//SUBELEMENT POSITION X
							PdfPCell cellX = new PdfPCell (new Paragraph ("x = " + Float.toString(pX.m_x) + decryptUnits(1001)));
							cellX.setColspan(1);
							cellX.setHorizontalAlignment (Element.ALIGN_CENTER);
							cellX.setVerticalAlignment(Element.ALIGN_CENTER);
							cellX.setBackgroundColor (new BaseColor (255, 255, 255));   //white
							PdfGenerator.memberForcesOverviewDateTable.addCell(cellX);

							//SUBELEMENT FORCE
							switch (k)
							{
								case 0:
								{
									PdfPCell cellN = new PdfPCell (new Paragraph ("N = " + Float.toString(pX.m_n) + decryptUnits(1101)));
									cellN.setColspan(1);
									cellN.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellN.setVerticalAlignment(Element.ALIGN_CENTER);
									cellN.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellN);
									break;
								}

								case 1:
								{
									PdfPCell cellVy = new PdfPCell (new Paragraph ("Vy = " + Float.toString(pX.m_vy) + decryptUnits(1102)));
									cellVy.setColspan(1);
									cellVy.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellVy.setVerticalAlignment(Element.ALIGN_CENTER);
									cellVy.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellVy);
									break;
								}
	
								case 2:
								{
									PdfPCell cellVz = new PdfPCell (new Paragraph ("Vz = " + Float.toString(pX.m_vz) + decryptUnits(1102)));
									cellVz.setColspan(1);
									cellVz.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellVz.setVerticalAlignment(Element.ALIGN_CENTER);
									cellVz.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellVz);
									break;
								}
								
								case 3:
								{
									PdfPCell cellMx = new PdfPCell (new Paragraph ("Mx = " + Float.toString(pX.m_mt) + decryptUnits(1103)));
									cellMx.setColspan(1);
									cellMx.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellMx.setVerticalAlignment(Element.ALIGN_CENTER);
									cellMx.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellMx);
									break;
								}
	
								case 4:
								{
									PdfPCell cellMy = new PdfPCell (new Paragraph ("My = " + Float.toString(pX.m_my) + decryptUnits(1104)));
									cellMy.setColspan(1);
									cellMy.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellMy.setVerticalAlignment(Element.ALIGN_CENTER);
									cellMy.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellMy);
									break;
								}
	
								case 5:
								{
									PdfPCell cellMz = new PdfPCell (new Paragraph ("Mz = " + Float.toString(pX.m_mz) + decryptUnits(1104)));
									cellMz.setColspan(1);
									cellMz.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellMz.setVerticalAlignment(Element.ALIGN_CENTER);
									cellMz.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellMz);
									break;
								}
								
								case 6:
								{
									PdfPCell cellMb = new PdfPCell (new Paragraph ("Mb = " + Float.toString(pX.m_mb) + decryptUnits(1105)));
									cellMb.setColspan(1);
									cellMb.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellMb.setVerticalAlignment(Element.ALIGN_CENTER);
									cellMb.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellMb);
									break;
								}

							}
							
							//SUBELEMENT DISPLACEMENTS
							switch (k)
							{
								case 0:
								{
									PdfPCell cellUx = new PdfPCell (new Paragraph ("Ux = " + Float.toString(pX.m_ux) + decryptUnits(1003)));
									cellUx.setColspan(1);
									cellUx.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellUx.setVerticalAlignment(Element.ALIGN_CENTER);
									cellUx.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellUx);
									break;
								}

								case 1:
								{
									PdfPCell cellUx = new PdfPCell (new Paragraph ("Uy = " + Float.toString(pX.m_uy) +  decryptUnits(1003)));
									cellUx.setColspan(1);
									cellUx.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellUx.setVerticalAlignment(Element.ALIGN_CENTER);
									cellUx.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellUx);
									break;
								}
	
								case 2:
								{
									PdfPCell cellUz = new PdfPCell (new Paragraph ("Uz = " + Float.toString(pX.m_uz) + decryptUnits(1003)));
									cellUz.setColspan(1);
									cellUz.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellUz.setVerticalAlignment(Element.ALIGN_CENTER);
									cellUz.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellUz);
									break;
								}
								
								case 3:
								{
									PdfPCell cellPhiX = new PdfPCell (new Paragraph ("Phix = " + Float.toString(pX.m_phix)  + decryptUnits(1004)));
									cellPhiX.setColspan(1);
									cellPhiX.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellPhiX.setVerticalAlignment(Element.ALIGN_CENTER);
									cellPhiX.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellPhiX);
									break;
								}
	
								case 4:
								{
									PdfPCell cellPhiY = new PdfPCell (new Paragraph ("Phiy = " + Float.toString(pX.m_phiy) + decryptUnits(1004)));
									cellPhiY.setColspan(1);
									cellPhiY.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellPhiY.setVerticalAlignment(Element.ALIGN_CENTER);
									cellPhiY.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellPhiY);
									break;
								}
	
								case 5:
								{
									PdfPCell cellPhiZ = new PdfPCell (new Paragraph ("Phiz = " + Float.toString(pX.m_phiz) + decryptUnits(1004)));
									cellPhiZ.setColspan(1);
									cellPhiZ.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellPhiZ.setVerticalAlignment(Element.ALIGN_CENTER);
									cellPhiZ.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellPhiZ);
									break;
								}
								
								case 6:
								{
									PdfPCell cellPhiW = new PdfPCell (new Paragraph ("Phiw = " + Float.toString(pX.m_phiw) + decryptUnits(1005)));
									cellPhiW.setColspan(1);
									cellPhiW.setHorizontalAlignment (Element.ALIGN_CENTER);
									cellPhiW.setVerticalAlignment(Element.ALIGN_CENTER);
									cellPhiW.setBackgroundColor (new BaseColor (255, 255, 255));   //white
									PdfGenerator.memberForcesOverviewDateTable.addCell(cellPhiW);
									break;
								}

							}
							
							
						}



					}

				}
				
			}

			//add lc table to document & move to next load case for the same structural line
			PdfGenerator.document.add(PdfGenerator.memberForcesOverviewDateTable);
			PdfGenerator.document.add(Chunk.NEWLINE);

		} //load cases loop
    }
    

    public static void printPdfTable_addSelectedLoadCasesOverviewTable() throws DocumentException
    {
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
        //---------------------------  list of loadcases     ---------------------
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------        
    	
    	
    	PdfGenerator.selectedLoadCasesDateTable =new PdfPTable(3);
    	PdfPCell cell = null;
    	
       	if (AnalysisOptions.selectedOutputLevel == AnalysisOutputLevel.DETAILED)
    	{
       		cell = new PdfPCell (new Paragraph (" ALL LOAD CASES FOR MEMBER " + AnalysisOptions.selectedStrLine));
    	}
    	else
    	{
    		 cell = new PdfPCell (new Paragraph (" USER SELECTED LOAD CASES FOR MEMBER "  + AnalysisOptions.selectedStrLine));
    	}
        cell.setColspan(3);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10.0f);
        cell.setBackgroundColor (new BaseColor (140, 221, 8));					               

        PdfGenerator.selectedLoadCasesDateTable.addCell(cell);
        PdfGenerator.selectedLoadCasesDateTable.setSpacingBefore(15.0f);       // Space Before table starts, like margin-top in CSS
        PdfGenerator.selectedLoadCasesDateTable.setSpacingAfter(15.0f);        // Space After table starts, like margin-Bottom in CSS
        
        PdfPCell cellLoadCaseNumber = new PdfPCell (new Paragraph ("LoadCase Number"));
        cellLoadCaseNumber.setColspan(1);
        cellLoadCaseNumber.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellLoadCaseNumber.setVerticalAlignment(Element.ALIGN_CENTER);
        cellLoadCaseNumber.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.selectedLoadCasesDateTable.addCell(cellLoadCaseNumber);
        
        PdfPCell cellLoadCaseTitle = new PdfPCell (new Paragraph ("LoadCase Title"));
        cellLoadCaseTitle.setColspan(1);
        cellLoadCaseTitle.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellLoadCaseTitle.setVerticalAlignment(Element.ALIGN_CENTER);
        cellLoadCaseTitle.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.selectedLoadCasesDateTable.addCell(cellLoadCaseTitle);
        
        PdfPCell cellLoadCaseType = new PdfPCell (new Paragraph ("LoadCase Type"));
        cellLoadCaseType.setColspan(1);
        cellLoadCaseType.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellLoadCaseType.setVerticalAlignment(Element.ALIGN_CENTER);
        cellLoadCaseType.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.selectedLoadCasesDateTable.addCell(cellLoadCaseType);
        
        //for detailed ouput print the all member relevant load cases and mark the selected ones
        //for normal output, print only the selected ones
        AnalysisMember currentSelectedMember = AnalysisMembersCollection.membersCollection.get(AnalysisOptions.selectedStrLine);
        if (currentSelectedMember !=  null)
        {
        	//for basic output, just print the selected load cases
    		if (AnalysisOptions.selectedOutputLevel == AnalysisOutputLevel.BASIC)
    		{
    			for ( Integer selectedLc : AnalysisOptions.selectedLinearLoadCases) //loop over the customer selected lcs tree set
    			{
    				PdfGenerator.selectedLoadCasesDateTable.addCell(Integer.toString(selectedLc));
    				PdfGenerator.selectedLoadCasesDateTable.addCell(currentSelectedMember.getTitleForActiveLoadCase(selectedLc));
    				PdfGenerator.selectedLoadCasesDateTable.addCell(currentSelectedMember.getLoadCaseTheoryForActiveLoadCase(selectedLc));
    			}
    		}
    		else if (AnalysisOptions.selectedOutputLevel == AnalysisOutputLevel.DETAILED)
    		{
            	for (int i = 0; i < currentSelectedMember.mLoadCases.length; i++)
            	{
            		if (AnalysisOptions.selectedLinearLoadCases.contains(currentSelectedMember.mLoadCases[i])) //check if the load case has been selected, if so, colour it
            		{          			
            			PdfPCell cellX = new PdfPCell(new Paragraph(Integer.toString(currentSelectedMember.mLoadCases[i])));
            			cellX.setBackgroundColor (new BaseColor (200, 221, 8));
            			PdfGenerator.selectedLoadCasesDateTable.addCell(cellX);
            			
            			cellX = new PdfPCell(new Paragraph(currentSelectedMember.mLoadCasesTitle[i].trim()));
            			cellX.setBackgroundColor (new BaseColor (200, 221, 8));
            			PdfGenerator.selectedLoadCasesDateTable.addCell(cellX);
            			
            			cellX = new PdfPCell(new Paragraph(currentSelectedMember.mLoadCasesTheoryOrder[i].trim()));
            			cellX.setBackgroundColor (new BaseColor (200, 221, 8));
            			PdfGenerator.selectedLoadCasesDateTable.addCell(cellX);
            		}
            		else //not a selected load case, just print it
            		{
            			PdfGenerator.selectedLoadCasesDateTable.addCell(Integer.toString(currentSelectedMember.mLoadCases[i]));
            			PdfGenerator.selectedLoadCasesDateTable.addCell(currentSelectedMember.mLoadCasesTitle[i].trim());
            			PdfGenerator.selectedLoadCasesDateTable.addCell(currentSelectedMember.mLoadCasesTheoryOrder[i].trim());
            		}
            	}
    		}
        }
        
        //add table to document
        PdfGenerator.document.add(PdfGenerator.selectedLoadCasesDateTable);
    
    }
    
    public static void printPdfTable_addSectionsOverviewTable() throws DocumentException
    {
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
        //---------------------------  list of sections     ----------------------
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------ 
    	
    	PdfGenerator.selectedCrossSectionsOverviewDateTable = new PdfPTable(4);
    	PdfPCell cell = null;
    	cell = new PdfPCell (new Paragraph (" CROSS-SECTIONS IN MEMBER OVERVIEW "));
        cell.setColspan(4);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10.0f);
        cell.setBackgroundColor (new BaseColor (140, 221, 8));
    	
        PdfGenerator.selectedCrossSectionsOverviewDateTable.addCell(cell);
        PdfGenerator.selectedCrossSectionsOverviewDateTable.setSpacingBefore(15.0f);       // Space Before table starts, like margin-top in CSS
        PdfGenerator.selectedCrossSectionsOverviewDateTable.setSpacingAfter(15.0f);        // Space After table starts, like margin-Bottom in CSS
        
        PdfPCell cellSectionId = new PdfPCell (new Paragraph ("Section ID"));
        cellSectionId.setColspan(1);
        cellSectionId.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellSectionId.setVerticalAlignment(Element.ALIGN_CENTER);
        cellSectionId.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.selectedCrossSectionsOverviewDateTable.addCell(cellSectionId);
        
        PdfPCell cellRefMatId = new PdfPCell (new Paragraph ("Reference Material No."));
        cellRefMatId.setColspan(1);
        cellRefMatId.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellRefMatId.setVerticalAlignment(Element.ALIGN_CENTER);
        cellRefMatId.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.selectedCrossSectionsOverviewDateTable.addCell(cellRefMatId);
        
        PdfPCell cellRefReinfMat = new PdfPCell (new Paragraph ("Reference Reinf. Material No."));
        cellRefReinfMat.setColspan(1);
        cellRefReinfMat.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellRefReinfMat.setVerticalAlignment(Element.ALIGN_CENTER);
        cellRefReinfMat.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.selectedCrossSectionsOverviewDateTable.addCell(cellRefReinfMat);
        
        PdfPCell cellSectTitle = new PdfPCell (new Paragraph ("Section Title"));
        cellSectTitle.setColspan(1);
        cellSectTitle.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellSectTitle.setVerticalAlignment(Element.ALIGN_CENTER);
        cellSectTitle.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.selectedCrossSectionsOverviewDateTable.addCell(cellSectTitle);

        
        for (Entry<Integer, AnalysisCSection> iSect : AnalysisMemberEC3Analysis.arrSects.entrySet())
        {    	  
        	PdfGenerator.selectedCrossSectionsOverviewDateTable.addCell(Integer.toString(iSect.getKey().intValue()));
        	PdfGenerator.selectedCrossSectionsOverviewDateTable.addCell(Integer.toString(iSect.getValue().m_mno));
        	PdfGenerator.selectedCrossSectionsOverviewDateTable.addCell(Integer.toString(iSect.getValue().m_mrf));
        	PdfGenerator.selectedCrossSectionsOverviewDateTable.addCell("[ " + iSect.getValue().m_text + " ]");
        }

        //add table to document
        PdfGenerator.document.add(PdfGenerator.selectedCrossSectionsOverviewDateTable);
    	
    }
    
    public static void printPdfTable_addMaterialsOverviewTable() throws DocumentException
    {
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
        //---------------------------  list of materials     ---------------------
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------ 
    	
    	PdfGenerator.selectedMaterialsverviewDateTable = new PdfPTable(3);
    	PdfPCell cell = null;
    	cell = new PdfPCell (new Paragraph (" MATERIALS IN MEMBER OVERVIEW "));
        cell.setColspan(3);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10.0f);
        cell.setBackgroundColor (new BaseColor (140, 221, 8));
    	
        PdfGenerator.selectedMaterialsverviewDateTable.addCell(cell);
        PdfGenerator.selectedMaterialsverviewDateTable.setSpacingBefore(15.0f);       // Space Before table starts, like margin-top in CSS
        PdfGenerator.selectedMaterialsverviewDateTable.setSpacingAfter(15.0f);        // Space After table starts, like margin-Bottom in CSS
    	
        
        PdfPCell cellMaterialId = new PdfPCell (new Paragraph ("Material ID"));
        cellMaterialId.setColspan(1);
        cellMaterialId.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellMaterialId.setVerticalAlignment(Element.ALIGN_CENTER);
        cellMaterialId.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.selectedMaterialsverviewDateTable.addCell(cellMaterialId);
        
        PdfPCell cellMaterialType = new PdfPCell (new Paragraph ("Material Type"));
        cellMaterialType.setColspan(1);
        cellMaterialType.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellMaterialType.setVerticalAlignment(Element.ALIGN_CENTER);
        cellMaterialType.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.selectedMaterialsverviewDateTable.addCell(cellMaterialType);
        
        PdfPCell cellMaterialDesignation = new PdfPCell (new Paragraph ("Designation"));
        cellMaterialDesignation.setColspan(1);
        cellMaterialDesignation.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellMaterialDesignation.setVerticalAlignment(Element.ALIGN_CENTER);
        cellMaterialDesignation.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.selectedMaterialsverviewDateTable.addCell(cellMaterialDesignation);
        
        
        for (Entry<Integer, AnalysisMaterial> iMat : AnalysisMemberEC3Analysis.arrMats.entrySet())
        {    	  
        	PdfGenerator.selectedMaterialsverviewDateTable.addCell(Integer.toString(iMat.getKey().intValue()));
            
        	//now decide what kind of material...
            int mType = iMat.getValue().m_type/1000;
            switch (mType)
            {
            	case 1: //concrete
            	{
            		PdfGenerator.selectedMaterialsverviewDateTable.addCell("Concrete");
            		break;
            	}
            	case 2: //steel
            	{
            		PdfGenerator.selectedMaterialsverviewDateTable.addCell("Steel");
            		break;
            	}
            	case 3: //timber
            	{
            		PdfGenerator.selectedMaterialsverviewDateTable.addCell("Timber");
            		break;
            	}
            	default: //all others
            	{
            		PdfGenerator.selectedMaterialsverviewDateTable.addCell("Other unknown");
            		break;
            	}
            }
        	PdfGenerator.selectedMaterialsverviewDateTable.addCell(iMat.getValue().m_title);
        }

        //add table to document
        PdfGenerator.document.add(PdfGenerator.selectedMaterialsverviewDateTable);
    }
    
    public static void printPdfTable_addLoadCaseNachweisLimitingSlenderness() throws DocumentException
    {
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
        //-----------  table of calculated limit slenderness values     ----------
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
    	
    	for ( Integer iLc : AnalysisOptions.selectedLinearLoadCases)
    	{

				AnalysisMemberEC3MemberCase memberNachweis = AnalysisMemberEC3Analysis.hmMembers.get(iLc);
    			
    			if (memberNachweis != null)
    			{


    				PdfGenerator.memberNachweiseDateTable = new PdfPTable(3);
    				PdfPCell cell = null;
    				cell = new PdfPCell (new Paragraph ("LOAD CASE " + iLc + " (I order) LIMITING SLENDERNESS FOR MEMBER " + AnalysisOptions.selectedStrLine));
    				cell.setColspan(3);
    				cell.setHorizontalAlignment (Element.ALIGN_CENTER);
    				cell.setVerticalAlignment(Element.ALIGN_CENTER);
    				cell.setPadding(10.0f);
    				cell.setBackgroundColor (new BaseColor (140, 221, 8));

    				PdfGenerator.memberNachweiseDateTable.addCell(cell);
    				PdfGenerator.memberNachweiseDateTable.setSpacingBefore(15.0f);       // Space Before table starts, like margin-top in CSS
    				PdfGenerator.memberNachweiseDateTable.setSpacingAfter(15.0f);        // Space After table starts, like margin-Bottom in CSS


    				///////////////////////////////// GENERAL OPTIONS ///////////////////////////
    				
    				if (memberNachweis instanceof AnalysisMemberEC3SingleMemberCase)
    				{
    					PdfPCell cellBucklingType = null;
    					cellBucklingType = new PdfPCell (new Paragraph ("Buckling Type Member : Single Compression Member"));
    					cellBucklingType.setColspan(3);
    					cellBucklingType.setHorizontalAlignment (Element.ALIGN_CENTER);
    					cellBucklingType.setVerticalAlignment(Element.ALIGN_CENTER);
    					cellBucklingType.setPadding(10.0f);
    					cellBucklingType.setBackgroundColor (new BaseColor (77,77,255)); //dark blue
    					PdfGenerator.memberNachweiseDateTable.addCell(cellBucklingType);

    				}
    				else
    				{
    					PdfPCell cellBucklingType = null;
    					cellBucklingType = new PdfPCell (new Paragraph ("Buckling Type Member : Frame Compression Member"));
    					cellBucklingType.setColspan(3);
    					cellBucklingType.setHorizontalAlignment (Element.ALIGN_CENTER);
    					cellBucklingType.setVerticalAlignment(Element.ALIGN_CENTER);
    					cellBucklingType.setPadding(10.0f);
    					cellBucklingType.setBackgroundColor (new BaseColor (77,77,255)); //dark blue
    					PdfGenerator.memberNachweiseDateTable.addCell(cellBucklingType);
    				}

    				PdfPCell cellCompressionOnly = null;
    				cellCompressionOnly = new PdfPCell (new Paragraph ("Member in pure compression : " + memberNachweis.isCompressiveOnly()));
    				cellCompressionOnly.setColspan(3);
    				cellCompressionOnly.setHorizontalAlignment (Element.ALIGN_CENTER);
    				cellCompressionOnly.setVerticalAlignment(Element.ALIGN_CENTER);
    				cellCompressionOnly.setPadding(10.0f);
    				cellCompressionOnly.setBackgroundColor (new BaseColor (77,77,255)); //dark blue
    				PdfGenerator.memberNachweiseDateTable.addCell(cellCompressionOnly);

    				PdfPCell cellSwayType = null;
    				cellSwayType = new PdfPCell (new Paragraph ("Member sway type : " + memberNachweis.getSwayType(GuiAnalysisParams.selectedSwayType)));
    				cellSwayType.setColspan(3);
    				cellSwayType.setHorizontalAlignment (Element.ALIGN_CENTER);
    				cellSwayType.setVerticalAlignment(Element.ALIGN_CENTER);
    				cellSwayType.setPadding(10.0f);
    				cellSwayType.setBackgroundColor (new BaseColor (77,77,255)); //dark blue
    				PdfGenerator.memberNachweiseDateTable.addCell(cellSwayType);

    				///////////////////////////////// COLUMNS ///////////////////////////////////

    				PdfPCell cellParameter = new PdfPCell (new Paragraph ("Parameter"));
    				cellParameter.setColspan(1);
    				cellParameter.setHorizontalAlignment (Element.ALIGN_CENTER);
    				cellParameter.setVerticalAlignment(Element.ALIGN_CENTER);
    				cellParameter.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
    				PdfGenerator.memberNachweiseDateTable.addCell(cellParameter);

    				PdfPCell cellFormula = new PdfPCell (new Paragraph ("Formula"));
    				cellFormula.setColspan(1);
    				cellFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    				cellFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    				cellFormula.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
    				PdfGenerator.memberNachweiseDateTable.addCell(cellFormula);

    				PdfPCell cellValue = new PdfPCell (new Paragraph ("Value"));
    				cellValue.setColspan(1);
    				cellValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    				cellValue.setVerticalAlignment(Element.ALIGN_CENTER);
    				cellValue.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
    				PdfGenerator.memberNachweiseDateTable.addCell(cellValue);
    				
    				/////////////////////////////////// AREA UNCRACKED ///////////////////////////////////////////
    				
    		        PdfPCell cellUncrackedArea = new PdfPCell (new Paragraph ("Area,unckracked"));
    		        cellUncrackedArea.setColspan(1);
    		        cellUncrackedArea.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedArea.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedArea.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedArea);
    				
    		        PdfPCell cellUncrackedAreaFormula = new PdfPCell (new Paragraph (" - "));
    		        cellUncrackedAreaFormula.setColspan(1);
    		        cellUncrackedAreaFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedAreaFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedAreaFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedAreaFormula);
    		        
    		        
    		        PdfPCell cellUncrackedAreaValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getArea_uncracked()) + " [m2]" ));
    		        cellUncrackedAreaValue.setColspan(1);
    		        cellUncrackedAreaValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedAreaValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedAreaValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedAreaValue);
    				
    		        ///////////////////////////////////// INERTIA Y UNCRACKED ///////////////////////////////////////////

    		        PdfPCell cellUncrackedIyy = new PdfPCell (new Paragraph ("Iyy,unckracked"));
    		        cellUncrackedIyy.setColspan(1);
    		        cellUncrackedIyy.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedIyy.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedIyy.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedIyy);
    				
    		        PdfPCell cellUncrackedIyyFormula = new PdfPCell (new Paragraph (" - "));
    		        cellUncrackedIyyFormula.setColspan(1);
    		        cellUncrackedIyyFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedIyyFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedIyyFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedIyyFormula);
    		        
    		        
    		        PdfPCell cellUncrackedIyyValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getIyy_uncracked()) + " [m4]"));
    		        cellUncrackedIyyValue.setColspan(1);
    		        cellUncrackedIyyValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedIyyValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedIyyValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedIyyValue);
    		        
    		        ///////////////////////////////////// INERTIA Z UNCRACKED ///////////////////////////////////////////

    		        PdfPCell cellUncrackedIzz = new PdfPCell (new Paragraph ("Izz,unckracked"));
    		        cellUncrackedIzz.setColspan(1);
    		        cellUncrackedIzz.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedIzz.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedIyy.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedIzz);
    				
    		        PdfPCell cellUncrackedIzzFormula = new PdfPCell (new Paragraph (" - "));
    		        cellUncrackedIzzFormula.setColspan(1);
    		        cellUncrackedIzzFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedIzzFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedIzzFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedIzzFormula);
    		        
    		        
    		        PdfPCell cellUncrackedIzzValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getIzz_uncracked()) + " [m4]"));
    		        cellUncrackedIzzValue.setColspan(1);
    		        cellUncrackedIzzValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedIzzValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedIzzValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedIzzValue);
    				
    		        ///////////////////////////////////// INERTIA Ac.fcd UNCRACKED ///////////////////////////////////////////

    		        PdfPCell cellUncrackedAcfcd = new PdfPCell (new Paragraph ("Ac.fcd,unckracked"));
    		        cellUncrackedAcfcd.setColspan(1);
    		        cellUncrackedAcfcd.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedAcfcd.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedAcfcd.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedAcfcd);
    				
    		        PdfPCell cellUncrackedAcfcdFormula = new PdfPCell (new Paragraph (" - "));
    		        cellUncrackedAcfcdFormula.setColspan(1);
    		        cellUncrackedAcfcdFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedAcfcdFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedAcfcdFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedAcfcdFormula);
    		        
    		        
    		        PdfPCell cellUncrackedAcfcdValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getAcfcd()) + " [kN]"));
    		        cellUncrackedAcfcdValue.setColspan(1);
    		        cellUncrackedAcfcdValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedAcfcdValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedAcfcdValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedAcfcdValue);
    		        
    		        ///////////////////////////////////// INERTIA As.fyd UNCRACKED ///////////////////////////////////////////

    		        PdfPCell cellUncrackedAsfyd = new PdfPCell (new Paragraph ("As.fyd"));
    		        cellUncrackedAsfyd.setColspan(1);
    		        cellUncrackedAsfyd.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedAsfyd.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedAsfyd.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedAsfyd);
    				
    		        PdfPCell cellUncrackedAsfydFormula = new PdfPCell (new Paragraph (" - "));
    		        cellUncrackedAsfydFormula.setColspan(1);
    		        cellUncrackedAsfydFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedAsfydFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedAsfydFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedAsfydFormula);
    		        
    		        
    		        PdfPCell cellUncrackedAsfydValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getAsfyd()) + " [kN]"));
    		        cellUncrackedAsfydValue.setColspan(1);
    		        cellUncrackedAsfydValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellUncrackedAsfydValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellUncrackedAsfydValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellUncrackedAsfydValue);
    		        
    		        ///////////////////////////////////// CREEP FACTOR ///////////////////////////////////////////

    		        PdfPCell cellEffectiveCreepFactor = new PdfPCell (new Paragraph ("Effective Creep Factor fi,eff"));
    		        cellEffectiveCreepFactor.setColspan(1);
    		        cellEffectiveCreepFactor.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellEffectiveCreepFactor.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellEffectiveCreepFactor.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellEffectiveCreepFactor);
    				
    		        PdfPCell cellEffectiveCreepFactorFormula = new PdfPCell (new Paragraph (" - "));
    		        cellEffectiveCreepFactorFormula.setColspan(1);
    		        cellEffectiveCreepFactorFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellEffectiveCreepFactorFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellEffectiveCreepFactorFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellEffectiveCreepFactorFormula);
    		        
    		        
    		        PdfPCell cellEffectiveCreepFactorValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getfi_eff()) + " [-]"));
    		        cellEffectiveCreepFactorValue.setColspan(1);
    		        cellEffectiveCreepFactorValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellEffectiveCreepFactorValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellEffectiveCreepFactorValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellEffectiveCreepFactorValue);
    		        
    		        ///////////////////////////////////// N,ed CONST. ///////////////////////////////////////////

    		        PdfPCell cellNed = new PdfPCell (new Paragraph ("N,ed (I order)"));
    		        cellNed.setColspan(1);
    		        cellNed.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellNed.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellNed.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellNed);
    				
    		        PdfPCell cellNedFormula = new PdfPCell (new Paragraph (" - "));
    		        cellNedFormula.setColspan(1);
    		        cellNedFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellNedFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellNedFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellNedFormula);
    		        
    		        
    		        PdfPCell cellNedValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getNedI()) + " [kN]"));
    		        cellNedValue.setColspan(1);
    		        cellNedValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellNedValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellNedValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellNedValue);
    		        
    		        ///////////////////////////////////// Moy1_Iorder CONST. ///////////////////////////////////////////

    		        PdfPCell cellMoy1I = new PdfPCell (new Paragraph ("M,oy1 (I order)"));
    		        cellMoy1I.setColspan(1);
    		        cellMoy1I.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellMoy1I.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellMoy1I.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellMoy1I);
    				
    		        PdfPCell cellMoy1IFormula = new PdfPCell (new Paragraph (" - "));
    		        cellMoy1IFormula.setColspan(1);
    		        cellMoy1IFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellMoy1IFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellMoy1IFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellMoy1IFormula);
    		        
    		        
    		        PdfPCell cellMoy1IValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getMoy1I()) + " [kNm]"));
    		        cellMoy1IValue.setColspan(1);
    		        cellMoy1IValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellMoy1IValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellMoy1IValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellMoy1IValue);
    		        
    		        ///////////////////////////////////// Moz1_Iorder CONST. ///////////////////////////////////////////

    		        PdfPCell cellMoz1I = new PdfPCell (new Paragraph ("M,oz1 (I order)"));
    		        cellMoz1I.setColspan(1);
    		        cellMoz1I.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellMoz1I.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellMoz1I.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellMoz1I);
    				
    		        PdfPCell cellMoz1IFormula = new PdfPCell (new Paragraph (" - "));
    		        cellMoz1IFormula.setColspan(1);
    		        cellMoz1IFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellMoz1IFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellMoz1IFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellMoz1IFormula);
    		        
    		        
    		        PdfPCell cellMoz1IValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getMoz1I()) + " [kNm]"));
    		        cellMoz1IValue.setColspan(1);
    		        cellMoz1IValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellMoz1IValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellMoz1IValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellMoz1IValue);
    		        
    		        ///////////////////////////////////// Moy2_Iorder CONST. ///////////////////////////////////////////

    		        PdfPCell cellMoy2I = new PdfPCell (new Paragraph ("M,oy2 (I order)"));
    		        cellMoy2I.setColspan(1);
    		        cellMoy2I.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellMoy2I.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellMoy2I.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellMoy2I);
    				
    		        PdfPCell cellMoy2IFormula = new PdfPCell (new Paragraph (" - "));
    		        cellMoy2IFormula.setColspan(1);
    		        cellMoy2IFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellMoy2IFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellMoy2IFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellMoy2IFormula);
    		        
    		        
    		        PdfPCell cellMoy2IValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getMoy2I()) + " [kNm]"));
    		        cellMoy2IValue.setColspan(1);
    		        cellMoy2IValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellMoy2IValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellMoy2IValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellMoy2IValue);
    		        
    		        ///////////////////////////////////// Moz2_Iorder CONST. ///////////////////////////////////////////

    		        PdfPCell cellMoz2I = new PdfPCell (new Paragraph ("M,oz2 (I order)"));
    		        cellMoz2I.setColspan(1);
    		        cellMoz2I.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellMoz2I.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellMoz2I.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellMoz2I);
    				
    		        PdfPCell cellMoz2IFormula = new PdfPCell (new Paragraph (" - "));
    		        cellMoz2IFormula.setColspan(1);
    		        cellMoz2IFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellMoz2IFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellMoz2IFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellMoz2IFormula);
    		        
    		        
    		        PdfPCell cellMoz2IValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getMoz2I()) + " [kNm]"));
    		        cellMoz2IValue.setColspan(1);
    		        cellMoz2IValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellMoz2IValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellMoz2IValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellMoz2IValue);
    		        
    		        
    				if (memberNachweis instanceof AnalysisMemberEC3FrameMemberCase)
    				{
    		        
        		        ///////////////////////////////////// k1,y CONST. ///////////////////////////////////////////

        		        PdfPCell cellk1Y = new PdfPCell (new Paragraph ("k1,y"));
        		        cellk1Y.setColspan(1);
        		        cellk1Y.setHorizontalAlignment (Element.ALIGN_CENTER);
        		        cellk1Y.setVerticalAlignment(Element.ALIGN_CENTER);
        		        cellk1Y.setBackgroundColor (new BaseColor (255, 255, 255));   //white
        		        PdfGenerator.memberNachweiseDateTable.addCell(cellk1Y);
        				
        		        PdfPCell cellk1YFormula = new PdfPCell (new Paragraph (" (theta1y/M1y).(EIyy/L) "));
        		        cellk1YFormula.setColspan(1);
        		        cellk1YFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
        		        cellk1YFormula.setVerticalAlignment(Element.ALIGN_CENTER);
        		        cellk1YFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
        		        PdfGenerator.memberNachweiseDateTable.addCell(cellk1YFormula);
        		        
        		        PdfPCell cellk1YValue = new PdfPCell (new Paragraph (Double.toString(((AnalysisMemberEC3FrameMemberCase) memberNachweis).getk1y()) + " [rad / kNm.kNm]"));
        		        cellk1YValue.setColspan(1);
        		        cellk1YValue.setHorizontalAlignment (Element.ALIGN_CENTER);
        		        cellk1YValue.setVerticalAlignment(Element.ALIGN_CENTER);
        		        cellk1YValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
        		        PdfGenerator.memberNachweiseDateTable.addCell(cellk1YValue);
        		        
        		        ///////////////////////////////////// k1,z CONST. ///////////////////////////////////////////

        		        PdfPCell cellk1Z = new PdfPCell (new Paragraph ("k1,z"));
        		        cellk1Z.setColspan(1);
        		        cellk1Z.setHorizontalAlignment (Element.ALIGN_CENTER);
        		        cellk1Z.setVerticalAlignment(Element.ALIGN_CENTER);
        		        cellk1Z.setBackgroundColor (new BaseColor (255, 255, 255));   //white
        		        PdfGenerator.memberNachweiseDateTable.addCell(cellk1Z);
        				
        		        PdfPCell cellk1ZFormula = new PdfPCell (new Paragraph (" (theta1z/M1z).(EIzz/L) "));
        		        cellk1ZFormula.setColspan(1);
        		        cellk1ZFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
        		        cellk1ZFormula.setVerticalAlignment(Element.ALIGN_CENTER);
        		        cellk1ZFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
        		        PdfGenerator.memberNachweiseDateTable.addCell(cellk1ZFormula);
        		        
        		        PdfPCell cellk1ZValue = new PdfPCell (new Paragraph (Double.toString(((AnalysisMemberEC3FrameMemberCase) memberNachweis).getk1z()) + " [rad / kNm.kNm]"));
        		        cellk1ZValue.setColspan(1);
        		        cellk1ZValue.setHorizontalAlignment (Element.ALIGN_CENTER);
        		        cellk1ZValue.setVerticalAlignment(Element.ALIGN_CENTER);
        		        cellk1ZValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
        		        PdfGenerator.memberNachweiseDateTable.addCell(cellk1ZValue);
    					
        		        ///////////////////////////////////// k2,y CONST. ///////////////////////////////////////////

        		        PdfPCell cellk2Y = new PdfPCell (new Paragraph ("k2,y"));
        		        cellk2Y.setColspan(1);
        		        cellk2Y.setHorizontalAlignment (Element.ALIGN_CENTER);
        		        cellk2Y.setVerticalAlignment(Element.ALIGN_CENTER);
        		        cellk2Y.setBackgroundColor (new BaseColor (255, 255, 255));   //white
        		        PdfGenerator.memberNachweiseDateTable.addCell(cellk1Y);
        				
        		        PdfPCell cellk2YFormula = new PdfPCell (new Paragraph (" (theta2y/M2y).(EIyy/L) "));
        		        cellk2YFormula.setColspan(1);
        		        cellk2YFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
        		        cellk2YFormula.setVerticalAlignment(Element.ALIGN_CENTER);
        		        cellk2YFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
        		        PdfGenerator.memberNachweiseDateTable.addCell(cellk2YFormula);
        		        
        		        PdfPCell cellk2YValue = new PdfPCell (new Paragraph (Double.toString(((AnalysisMemberEC3FrameMemberCase) memberNachweis).getk2y()) + " [rad / kNm.kNm]"));
        		        cellk2YValue.setColspan(1);
        		        cellk2YValue.setHorizontalAlignment (Element.ALIGN_CENTER);
        		        cellk2YValue.setVerticalAlignment(Element.ALIGN_CENTER);
        		        cellk2YValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
        		        PdfGenerator.memberNachweiseDateTable.addCell(cellk2YValue);   					
    					
        		        ///////////////////////////////////// k2,z CONST. ///////////////////////////////////////////

        		        PdfPCell cellk2Z = new PdfPCell (new Paragraph ("k2,z"));
        		        cellk2Z.setColspan(1);
        		        cellk2Z.setHorizontalAlignment (Element.ALIGN_CENTER);
        		        cellk2Z.setVerticalAlignment(Element.ALIGN_CENTER);
        		        cellk2Z.setBackgroundColor (new BaseColor (255, 255, 255));   //white
        		        PdfGenerator.memberNachweiseDateTable.addCell(cellk2Z);
        				
        		        PdfPCell cellk2ZFormula = new PdfPCell (new Paragraph (" (theta2z/M2z).(EIzz/L) "));
        		        cellk2ZFormula.setColspan(1);
        		        cellk2ZFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
        		        cellk2ZFormula.setVerticalAlignment(Element.ALIGN_CENTER);
        		        cellk2ZFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
        		        PdfGenerator.memberNachweiseDateTable.addCell(cellk2ZFormula);
        		        
        		        PdfPCell cellk2ZValue = new PdfPCell (new Paragraph (Double.toString(((AnalysisMemberEC3FrameMemberCase) memberNachweis).getk2z()) + " [rad / kNm.kNm]"));
        		        cellk2ZValue.setColspan(1);
        		        cellk2ZValue.setHorizontalAlignment (Element.ALIGN_CENTER);
        		        cellk2ZValue.setVerticalAlignment(Element.ALIGN_CENTER);
        		        cellk2ZValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
        		        PdfGenerator.memberNachweiseDateTable.addCell(cellk2ZValue);
    				}
    				
    		        ///////////////////////////////////// Lo,y CONST. ///////////////////////////////////////////

    		        PdfPCell cellLoy = new PdfPCell (new Paragraph ("Lo,y (buckl)"));
    		        cellLoy.setColspan(1);
    		        cellLoy.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellLoy.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellLoy.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellLoy);
    				
    		        PdfPCell cellLoyFormula = new PdfPCell (new Paragraph (" - "));
    		        cellLoyFormula.setColspan(1);
    		        cellLoyFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellLoyFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellLoyFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellLoyFormula);
    		        
    		        
    		        PdfPCell cellLoyValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getBuckLenY()) + " [m]"));
    		        cellLoyValue.setColspan(1);
    		        cellLoyValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellLoyValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellLoyValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellLoyValue);
    		        
    		        ///////////////////////////////////// Lo,z CONST. ///////////////////////////////////////////

    		        PdfPCell cellLoz = new PdfPCell (new Paragraph ("Lo,z (buckl)"));
    		        cellLoz.setColspan(1);
    		        cellLoz.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellLoz.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellLoz.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellLoz);
    				
    		        PdfPCell cellLozFormula = new PdfPCell (new Paragraph (" - "));
    		        cellLozFormula.setColspan(1);
    		        cellLozFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellLozFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellLozFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellLozFormula);
    		        
    		        
    		        PdfPCell cellLozValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getBuckLenZ()) + " [m]"));
    		        cellLozValue.setColspan(1);
    		        cellLozValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellLozValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellLozValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellLozValue);
    		        
    		        ///////////////////////////////////// rm,y CONST. ///////////////////////////////////////////

    		        PdfPCell cellrmY = new PdfPCell (new Paragraph ("rm,y"));
    		        cellrmY.setColspan(1);
    		        cellrmY.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellrmY.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellrmY.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellrmY);
    				
    		        PdfPCell cellrmYFormula = new PdfPCell (new Paragraph (" |Mo1,y / Mo2,y| "));
    		        cellrmYFormula.setColspan(1);
    		        cellrmYFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellrmYFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellrmYFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellrmYFormula);
    		        
    		        
    		        PdfPCell cellrmYValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getrmY()) + " [-]"));
    		        cellrmYValue.setColspan(1);
    		        cellrmYValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellrmYValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellrmYValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellrmYValue);
    		        
    		        ///////////////////////////////////// rm,z CONST. ///////////////////////////////////////////

    		        PdfPCell cellrmZ = new PdfPCell (new Paragraph ("rm,z"));
    		        cellrmZ.setColspan(1);
    		        cellrmZ.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellrmZ.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellrmZ.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellrmZ);
    				
    		        PdfPCell cellrmZFormula = new PdfPCell (new Paragraph (" |Mo1,z / Mo2,z| "));
    		        cellrmZFormula.setColspan(1);
    		        cellrmZFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellrmZFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellrmZFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellrmZFormula);
    		        
    		        PdfPCell cellrmZValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getrmZ()) + " [-]"));
    		        cellrmZValue.setColspan(1);
    		        cellrmZValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellrmZValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellrmZValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellrmZValue);
    		        
    		        
    		        ///////////////////////////////////// ω CONST. ///////////////////////////////////////////

    		        PdfPCell cellω = new PdfPCell (new Paragraph ("w"));
    		        cellω.setColspan(1);
    		        cellω.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellω.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellω.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellω);
    				
    		        PdfPCell cellωFormula = new PdfPCell (new Paragraph (" As.fyd / Ac.fcd "));
    		        cellωFormula.setColspan(1);
    		        cellωFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellωFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellωFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellωFormula);
    		        
    		        PdfPCell cellωValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getw()) + " [-]"));
    		        cellωValue.setColspan(1);
    		        cellωValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellωValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellωValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellωValue);
    		        
    		        ///////////////////////////////////// n CONST. ///////////////////////////////////////////

    		        PdfPCell celln = new PdfPCell (new Paragraph ("n"));
    		        celln.setColspan(1);
    		        celln.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        celln.setVerticalAlignment(Element.ALIGN_CENTER);
    		        celln.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(celln);
    				
    		        PdfPCell cellnFormula = new PdfPCell (new Paragraph (" Ned / Ac.fcd "));
    		        cellnFormula.setColspan(1);
    		        cellnFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellnFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellnFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellnFormula);
    		        
    		        PdfPCell cellnValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getn()) + " [-]"));
    		        cellnValue.setColspan(1);
    		        cellnValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellnValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellnValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellnValue);
    		        
    		        ///////////////////////////////////// A CONST. ///////////////////////////////////////////

    		        PdfPCell cellA = new PdfPCell (new Paragraph ("A"));
    		        cellA.setColspan(1);
    		        cellA.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellA.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellA.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellA);
    				
    		        PdfPCell cellAFormula = new PdfPCell (new Paragraph (" 1 / (1 + 0.2.fi,eff) "));
    		        cellAFormula.setColspan(1);
    		        cellAFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellAFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellAFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellAFormula);
    		        
    		        PdfPCell cellAValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getA()) + " [-]"));
    		        cellAValue.setColspan(1);
    		        cellAValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellAValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellAValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellAValue);
    		        
    		        ///////////////////////////////////// B CONST. ///////////////////////////////////////////

    		        PdfPCell cellB = new PdfPCell (new Paragraph ("B"));
    		        cellB.setColspan(1);
    		        cellB.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellB.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellB.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellB);
    				
    		        PdfPCell cellBFormula = new PdfPCell (new Paragraph (" √(1 + 2.w) "));
    		        cellBFormula.setColspan(1);
    		        cellBFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellBFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellBFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellBFormula);
    		        
    		        PdfPCell cellBValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getB()) + " [-]"));
    		        cellBValue.setColspan(1);
    		        cellBValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellBValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellBValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellBValue);
    		        
    		        ///////////////////////////////////// CY CONST. ///////////////////////////////////////////

    		        PdfPCell cellCY = new PdfPCell (new Paragraph ("Cy"));
    		        cellCY.setColspan(1);
    		        cellCY.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellCY.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellCY.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellCY);
    				
    		        PdfPCell cellCYFormula = new PdfPCell (new Paragraph (" 1.7 - rm,y "));
    		        cellCYFormula.setColspan(1);
    		        cellCYFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellCYFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellCYFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellCYFormula);
    		        
    		        PdfPCell cellCYValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getCy()) + " [-]"));
    		        cellCYValue.setColspan(1);
    		        cellCYValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellCYValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellCYValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellCYValue);
    		        
    		        ///////////////////////////////////// CZ CONST. ///////////////////////////////////////////

    		        PdfPCell cellCZ = new PdfPCell (new Paragraph ("Cz"));
    		        cellCZ.setColspan(1);
    		        cellCZ.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellCZ.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellCZ.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellCZ);
    				
    		        PdfPCell cellCZFormula = new PdfPCell (new Paragraph (" 1.7 - rm,z "));
    		        cellCZFormula.setColspan(1);
    		        cellCZFormula.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellCZFormula.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellCZFormula.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellCZFormula);
    		        
    		        PdfPCell cellCZValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getCz()) + " [-]"));
    		        cellCZValue.setColspan(1);
    		        cellCZValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellCZValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellCZValue.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellCZValue);
    		        
    		        ///////////////////////////////////// λy CONST. ///////////////////////////////////////////

    		        PdfPCell cellλy = new PdfPCell (new Paragraph (" = > lambda,y =  "));
    		        cellλy.setColspan(2);
    		        cellλy.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellλy.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellλy.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellλy);
    				
    		        PdfPCell cellλyValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getLambdaY()) + " [-]"));
    		        cellλyValue.setColspan(1);
    		        cellλyValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellλyValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellλyValue.setBackgroundColor (new BaseColor (255, 225, 255));   //reddish
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellλyValue);
    		        
    		        ///////////////////////////////////// λz CONST. ///////////////////////////////////////////

    		        PdfPCell cellλz = new PdfPCell (new Paragraph (" = > lambda,z =  "));
    		        cellλz.setColspan(2);
    		        cellλz.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellλz.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellλz.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellλz);
    				
    		        PdfPCell cellλzValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getLambdaZ()) + " [-]"));
    		        cellλzValue.setColspan(1);
    		        cellλzValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellλzValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellλzValue.setBackgroundColor (new BaseColor (255, 225, 255));   //reddish
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellλzValue);
    		        
    		        
    		        ///////////////////////////////////// λlimy CONST. ///////////////////////////////////////////

    		        PdfPCell cellλlimy = new PdfPCell (new Paragraph (" = > lambda,lim,y = 20.A.B.Cy / sqrt(n) =  "));
    		        cellλlimy.setColspan(2);
    		        cellλlimy.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellλlimy.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellλlimy.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellλlimy);
    				
    		        PdfPCell cellλlimyValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getLambdaLimy()) + " [-]"));
    		        cellλlimyValue.setColspan(1);
    		        cellλlimyValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellλlimyValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellλlimyValue.setBackgroundColor (new BaseColor (255, 225, 255));   //reddish
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellλlimyValue);
    		        
    		        ///////////////////////////////////// λlimz CONST. ///////////////////////////////////////////

    		        PdfPCell cellλlimz = new PdfPCell (new Paragraph (" = > lambda,lim,z = 20.A.B.Cz / sqrt(n) =  "));
    		        cellλlimz.setColspan(2);
    		        cellλlimz.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellλlimz.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellλlimz.setBackgroundColor (new BaseColor (255, 255, 255));   //white
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellλlimz);
    				
    		        PdfPCell cellλlimzValue = new PdfPCell (new Paragraph (Double.toString(memberNachweis.getLambdaLimz()) + " [-]"));
    		        cellλlimzValue.setColspan(1);
    		        cellλlimzValue.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        cellλlimzValue.setVerticalAlignment(Element.ALIGN_CENTER);
    		        cellλlimzValue.setBackgroundColor (new BaseColor (255, 225, 255));   //reddish
    		        PdfGenerator.memberNachweiseDateTable.addCell(cellλlimzValue);
    			
    		        

    		        ///////////////////////////////////// PROCEED OR NOT ???  ///////////////////////////////////////////

    		        if (memberNachweis.getRequiresNCMAnalysis())
    		        {
    		        	PdfPCell cellProceed = new PdfPCell (new Paragraph (" = > Requires a II order effects check!  "));
    		        	cellProceed.setColspan(3);
    		        	cellProceed.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        	cellProceed.setVerticalAlignment(Element.ALIGN_CENTER);
    		        	cellProceed.setBackgroundColor (new BaseColor (255, 64, 64));   //red
    		        	PdfGenerator.memberNachweiseDateTable.addCell(cellProceed);
    		        }
    		        else
    		        {
    		        	PdfPCell cellProceed = new PdfPCell (new Paragraph (" = > Second order effects can be neglected!  "));
    		        	cellProceed.setColspan(3);
    		        	cellProceed.setHorizontalAlignment (Element.ALIGN_CENTER);
    		        	cellProceed.setVerticalAlignment(Element.ALIGN_CENTER);
    		        	cellProceed.setBackgroundColor (new BaseColor (0, 255, 127));   // light green
    		        	PdfGenerator.memberNachweiseDateTable.addCell(cellProceed);
    		        }
    		         
    		        
    		        
    			}
    			
        		//add table to document
        		PdfGenerator.document.add(PdfGenerator.memberNachweiseDateTable);

    	} //loop over the selected load cases
    }
    
    
    public static void printPdfTable_MaterialX(AnalysisMaterial mat) throws DocumentException
    {
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
        //---------------------------   materials     ----------------------------
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------  
    	PdfGenerator.materialXDateTable =new PdfPTable(3);
    	
    	//now decide what kind of material...
        int mType = mat.m_type/1000;
        String whatMatType = new String();
        switch (mType)
        {
        	case 1: //concrete
        	{
        		whatMatType = "Concrete";
        		break;
        	}
        	case 2: //steel
        	{
        		whatMatType = "Steel";
        		break;
        	}
        	case 3: //timber
        	{
        		whatMatType = "Timber";
        		break;
        	}
        	default: //all others
        	{
        		whatMatType = "Other unknown";
        		break;
        	}
        }
        
        PdfPCell cell = new PdfPCell (new Paragraph (" Material ID : " + mat.m_id + " | Type : " + whatMatType + " | Title : [ " + mat.m_title + " ]"));
        cell.setColspan(3);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10.0f);
        cell.setBackgroundColor (new BaseColor (140, 221, 8));					               

        PdfGenerator.materialXDateTable.addCell(cell);
        PdfGenerator.materialXDateTable.setSpacingBefore(15.0f);       // Space Before table starts, like margin-top in CSS
        PdfGenerator.materialXDateTable.setSpacingAfter(15.0f);        // Space After table starts, like margin-Bottom in CSS
        
        
        PdfPCell cellProperty = new PdfPCell (new Paragraph ("Property"));
        cellProperty.setColspan(1);
        cellProperty.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellProperty.setVerticalAlignment(Element.ALIGN_CENTER);
        cellProperty.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.materialXDateTable.addCell(cellProperty);
        
        PdfPCell cellValue = new PdfPCell (new Paragraph ("Value"));
        cellValue.setColspan(1);
        cellValue.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellValue.setVerticalAlignment(Element.ALIGN_CENTER);
        cellValue.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.materialXDateTable.addCell(cellValue);
        
        PdfPCell cellSIUnit = new PdfPCell (new Paragraph ("SI Unit"));
        cellSIUnit.setColspan(1);
        cellSIUnit.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellSIUnit.setVerticalAlignment(Element.ALIGN_CENTER);
        cellSIUnit.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.materialXDateTable.addCell(cellSIUnit);
        
        switch (mType)
        {
        	case 1: //concrete
        	{
        		
                //------------------------------------------------------------------------	          
                PdfGenerator.materialXDateTable.addCell("Elasticity Modulus EMOD");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_emod));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1090));
                //------------------------------------------------------------------------	          
                PdfGenerator.materialXDateTable.addCell("Poisson's ratio MUE");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_mue));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1090));
                //------------------------------------------------------------------------	          
                PdfGenerator.materialXDateTable.addCell("Shear modulus GMOD");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_gmod));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1090));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Compression modulus KMOD");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_kmod));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1090));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Dead weight GAM");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_gam));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1091));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Density RHO");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_rho));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(159));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Temperature Elongation coefficient ALFA");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_alfa));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(107));   
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Elasticity modulus perpendicular E90");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_e90));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1090));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Poissons ratio perpendicular MUE 90");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_mue90));
                PdfGenerator.materialXDateTable.addCell("[-]");       		
                //------------------------------------------------------------------------	        
//                PdfGenerator.materialXDateTable.addCell("Meridian Angle ALF");
//                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_alf));
//                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(5)); 
//                //------------------------------------------------------------------------	        
//                PdfGenerator.materialXDateTable.addCell("Descent Angle BET");
//                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_bet));
//                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(5));              
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Material safety SCM");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_scm));
                PdfGenerator.materialXDateTable.addCell("[-]");               
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Effective strength FC");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_fc));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));               
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Nominal strength FCK");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_fck));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));      		
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Mean Tension Strength FTM");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_ftm));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Min Tension Strength FTL");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_ftl));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Max Tension Strength FTK");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_ftk));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));
                //------------------------------------------------------------------------	        
//                PdfGenerator.materialXDateTable.addCell("tensile failure energy ET");
//                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_et));
//                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(112));  
//                //------------------------------------------------------------------------	        
//                PdfGenerator.materialXDateTable.addCell("friction in crack  MUER");
//                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_muer));
//                PdfGenerator.materialXDateTable.addCell("[-]");  
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("mean value of strength FCM");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_fcm));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));           
                //------------------------------------------------------------------------	        
//                PdfGenerator.materialXDateTable.addCell("weight class RDCL");
//                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_rdcl));
//                PdfGenerator.materialXDateTable.addCell("[-]");                                
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Calc Mean Strength FCR");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_fcr));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));                
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Calc. Mean Elasticity ECR");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_ecr));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1090));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Bond Strength FBD");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_fbd));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Design Tensile Strength  FTD");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_ftd));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Equ. Tensile Strength_I FEQR");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_feqr));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Equ. Tensile Strength_II FEQT");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_feqt));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Fatigue Strength FCFAT");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_fcfat));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Serv. Safety Factor SCMS");
                PdfGenerator.materialXDateTable.addCell(Float.toString(Math.abs(mat.m_scms)));
                PdfGenerator.materialXDateTable.addCell("[-]"); 
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Ulti. Safety Factor SCMU");
                PdfGenerator.materialXDateTable.addCell(Float.toString(Math.abs(mat.m_scmu)));
                PdfGenerator.materialXDateTable.addCell("[-]"); 
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Calc. Safety Factor SCMC");
                PdfGenerator.materialXDateTable.addCell(Float.toString(Math.abs(mat.m_scmc)));
                PdfGenerator.materialXDateTable.addCell("[-]"); 
                
        		break;
        	}
        	case 2: //steel
        	{
        		
                //------------------------------------------------------------------------	          
                PdfGenerator.materialXDateTable.addCell("Elasticity Modulus EMOD");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_emod));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1090));
                //------------------------------------------------------------------------	          
                PdfGenerator.materialXDateTable.addCell("Poissons ratio MUE");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_mue));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1090));
                //------------------------------------------------------------------------	          
                PdfGenerator.materialXDateTable.addCell("Shear modulus GMOD");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_gmod));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1090));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Compression modulus KMOD");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_kmod));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1090));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Dead weight GAM");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_gam));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1091));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Density RHO");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_rho));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(159));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Temperature Elongation coefficient ALFA");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_alfa));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(107));   
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Elasticity modulus perpendicular E90");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_e90));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1090));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Poissons ratio perpendicular MUE 90");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_mue90));
                PdfGenerator.materialXDateTable.addCell("[-]");   		
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Meridian Angle ALF");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_alf));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(5)); 
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Descent Angle BET");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_bet));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(5));              
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Material safety SCM");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_scm));
                PdfGenerator.materialXDateTable.addCell("[-]");
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Yield stress FY");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_fy));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Tensile strength FT");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_ft));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));                         
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("limit strain EPS");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_eps));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(9)); 
                //------------------------------------------------------------------------	        
//                PdfGenerator.materialXDateTable.addCell("Relaxation 0.55fpk REL1");
//                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_rel1));
//                PdfGenerator.materialXDateTable.addCell("[-]");
//                //------------------------------------------------------------------------	        
//                PdfGenerator.materialXDateTable.addCell("Relaxation_70 REL2");
//                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_rel2));
//                PdfGenerator.materialXDateTable.addCell("[-]");
//                //------------------------------------------------------------------------	        
//                PdfGenerator.materialXDateTable.addCell("Bond Coefficient R");
//                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_r));
//                PdfGenerator.materialXDateTable.addCell("[-]");                
//                //------------------------------------------------------------------------	        
//                PdfGenerator.materialXDateTable.addCell("Bond EC Bond Factor K1");
//                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_k1));
//                PdfGenerator.materialXDateTable.addCell("[-]");                    
//                //------------------------------------------------------------------------	        
//                PdfGenerator.materialXDateTable.addCell("Hardening Modulus EH");
//                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_eh));
//                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1090));
//                //------------------------------------------------------------------------	        
//                PdfGenerator.materialXDateTable.addCell("Proportional Stress FE");
//                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_fe));
//                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));
//                //------------------------------------------------------------------------	        
//                PdfGenerator.materialXDateTable.addCell("Plastic Strain EPSE");
//                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_epse));
//                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(9));
//                //------------------------------------------------------------------------	        
//                PdfGenerator.materialXDateTable.addCell("Dynamic Strength FDYN");
//                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_fdyn));
//                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Compr. Effective Strength FYC");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_fyc));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Compr. Nominal Strength FTC");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_ftc));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(1092));
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Max. Thickness TMAX");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_tmax));
                PdfGenerator.materialXDateTable.addCell(PdfGenerator.decryptUnits(16));                
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Buckling Class BC");
                PdfGenerator.materialXDateTable.addCell(Float.toString(mat.m_bc));
                PdfGenerator.materialXDateTable.addCell("[-]");
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Serv. Safety Factor SCMS");
                PdfGenerator.materialXDateTable.addCell(Float.toString(Math.abs(mat.m_scms)));
                PdfGenerator.materialXDateTable.addCell("[-]"); 
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Ulti. Safety Factor SCMU");
                PdfGenerator.materialXDateTable.addCell(Float.toString(Math.abs(mat.m_scmu)));
                PdfGenerator.materialXDateTable.addCell("[-]"); 
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("Calc. Safety Factor SCMC");
                PdfGenerator.materialXDateTable.addCell(Float.toString(Math.abs(mat.m_scmc)));
                PdfGenerator.materialXDateTable.addCell("[-]");
        		break;
        	}       	
        	case 3: //timber
        	{
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("TIMBER MATERIAL IS NOT ALLOWED");
                PdfGenerator.materialXDateTable.addCell(Float.toString(9999));
                PdfGenerator.materialXDateTable.addCell("[-]");
        		break;
        	}
        	default: //all others
        	{
                //------------------------------------------------------------------------	        
                PdfGenerator.materialXDateTable.addCell("OTHER MATERIALS ARE NOT ALLOWED");
                PdfGenerator.materialXDateTable.addCell(Float.toString(9999));
                PdfGenerator.materialXDateTable.addCell("[-]");
        		break;
        	}
        }
    
        //add table to document
        PdfGenerator.document.add(PdfGenerator.materialXDateTable);

    }
    
    public static void printPdfTable_CSectionX(AnalysisCSection sect) throws DocumentException
    {
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
        //------------------------   cross-section     ---------------------------
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------  
    	PdfGenerator.sectionXDateTable =new PdfPTable(3);

        PdfPCell cell = new PdfPCell (new Paragraph (" Section ID : " + sect.id + " | Title : [ " + sect.m_text + " ]"));

        cell.setColspan(3);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(10.0f);
        cell.setBackgroundColor (new BaseColor (140, 221, 8));					               

        PdfGenerator.sectionXDateTable.addCell(cell);
        PdfGenerator.sectionXDateTable.setSpacingBefore(15.0f);       // Space Before table starts, like margin-top in CSS
        PdfGenerator.sectionXDateTable.setSpacingAfter(15.0f);        // Space After table starts, like margin-Bottom in CSS
        
        
        PdfPCell cellProperty = new PdfPCell (new Paragraph ("Property"));
        cellProperty.setColspan(1);
        cellProperty.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellProperty.setVerticalAlignment(Element.ALIGN_CENTER);
        cellProperty.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.sectionXDateTable.addCell(cellProperty);
        
        PdfPCell cellValue = new PdfPCell (new Paragraph ("Value"));
        cellValue.setColspan(1);
        cellValue.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellValue.setVerticalAlignment(Element.ALIGN_CENTER);
        cellValue.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.sectionXDateTable.addCell(cellValue);
        
        PdfPCell cellSIUnit = new PdfPCell (new Paragraph ("SI Unit"));
        cellSIUnit.setColspan(1);
        cellSIUnit.setHorizontalAlignment (Element.ALIGN_CENTER);
        cellSIUnit.setVerticalAlignment(Element.ALIGN_CENTER);
        cellSIUnit.setBackgroundColor (new BaseColor (235, 199, 158));   //light orange
        PdfGenerator.sectionXDateTable.addCell(cellSIUnit);
        

        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Reference Mat. Id");
        PdfGenerator.sectionXDateTable.addCell(Integer.toString(sect.m_mno));
        PdfGenerator.sectionXDateTable.addCell("[-]");
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Reinforcement Mat. Id");
        PdfGenerator.sectionXDateTable.addCell(Integer.toString(sect.m_mrf));
        PdfGenerator.sectionXDateTable.addCell("[-]");
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Area (total)");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_a));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1012));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Shear Deformation Ay");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_ay));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1012));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Shear Deformation Az");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_az));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1012));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Torsional moment of inertia");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_it));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1014));        
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Moment of inertia y-y");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_iy));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1014));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Moment of inertia z-z");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_iz));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1014));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Moment of inertia y-z");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_iyz));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1014));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("y-Ordinate Center of gravity");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_ys));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1011));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("z-Ordinate Center of gravity");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_zs));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1011));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("y-Ordinate Shear Center");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_ysc));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1011));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("z-Ordinate Shear-Center");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_zsc));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1011));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Elasticity Modulus");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_em));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1090));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Shear modulus");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_gm));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1090));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Deadweight");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_gam));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1091));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Ordinate ymin");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_ymin));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1011));
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Ordinate zmin");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_zmin));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1011));                                        
        //------------------------------------------------------------------------	          
        PdfGenerator.sectionXDateTable.addCell("Ordinate ymax");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_ymax));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1011));       
        //------------------------------------------------------------------------
        PdfGenerator.sectionXDateTable.addCell("Ordinate zmax");
        PdfGenerator.sectionXDateTable.addCell(Float.toString(sect.m_zmax));
        PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1011));       
        //------------------------------------------------------------------------    

        if (AnalysisOptions.selectedOutputLevel == AnalysisOutputLevel.DETAILED)
        {
        	
            PdfGenerator.sectionXDateTable.addCell("Number of None-effective areas");
            PdfGenerator.sectionXDateTable.addCell(Integer.toString(sect.m_neffs));
            PdfGenerator.sectionXDateTable.addCell("[-]");
            
            PdfGenerator.sectionXDateTable.addCell("Number of Construction Stages");
            PdfGenerator.sectionXDateTable.addCell(Integer.toString(sect.m_cs));
            PdfGenerator.sectionXDateTable.addCell("[-]");
            
            PdfGenerator.sectionXDateTable.addCell("Number of Reinforcement Bars");
            PdfGenerator.sectionXDateTable.addCell(Integer.toString(sect.m_reinfBars));
            PdfGenerator.sectionXDateTable.addCell("[-]");
            
            PdfGenerator.sectionXDateTable.addCell("Number of Shear Cuts");
            PdfGenerator.sectionXDateTable.addCell(Integer.toString(sect.m_cuts));
            PdfGenerator.sectionXDateTable.addCell("[-]");
            
            PdfGenerator.sectionXDateTable.addCell("Number of Stress/Polygon Points");
            PdfGenerator.sectionXDateTable.addCell(Integer.toString(sect.m_spt));
            PdfGenerator.sectionXDateTable.addCell("[-]");
        	
            
    		if (sect.arrSectReinfBars.length > 0)
    		{
    	        PdfPCell reinfTitleCell = new PdfPCell (new Paragraph ("REINFORCEMENTS"));
    	        reinfTitleCell.setColspan(3);
    	        reinfTitleCell.setHorizontalAlignment (Element.ALIGN_CENTER);
    	        reinfTitleCell.setVerticalAlignment(Element.ALIGN_CENTER);
    	        reinfTitleCell.setPadding(10.0f);
    	        reinfTitleCell.setBackgroundColor (new BaseColor (77,77,255)); //dark blue 			
    			PdfGenerator.sectionXDateTable.addCell(reinfTitleCell);
    		}
            
        	int iReinfBars = 1; //counter on reinf. bars
        	for (AnalysisCSectionReinforcement r : sect.arrSectReinfBars)
        	{ 	
        		PdfPCell reinfSeparatorCell = new PdfPCell (new Paragraph ("Reinforcement Bar : " + iReinfBars + " Title : " + r.m_tnr));
        		reinfSeparatorCell.setColspan(3);
        		reinfSeparatorCell.setHorizontalAlignment (Element.ALIGN_CENTER);
        		reinfSeparatorCell.setVerticalAlignment(Element.ALIGN_CENTER);
        		reinfSeparatorCell.setPadding(10.0f);
        		reinfSeparatorCell.setBackgroundColor (new BaseColor (151,255,255)); //light blue
        		PdfGenerator.sectionXDateTable.addCell(reinfSeparatorCell);
        		
        		PdfGenerator.sectionXDateTable.addCell("Reinf. Material number");
        		PdfGenerator.sectionXDateTable.addCell(Integer.toString(r.m_mno));
        		PdfGenerator.sectionXDateTable.addCell("[-]");

        		PdfGenerator.sectionXDateTable.addCell("Layer number");
        		PdfGenerator.sectionXDateTable.addCell(Integer.toString(r.m_ir));
        		PdfGenerator.sectionXDateTable.addCell("[-]"); 

        		PdfGenerator.sectionXDateTable.addCell("y-ordinate");
        		PdfGenerator.sectionXDateTable.addCell(Float.toString(r.m_y));
        		PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1011));

        		PdfGenerator.sectionXDateTable.addCell("z-ordinate");
        		PdfGenerator.sectionXDateTable.addCell(Float.toString(r.m_z));
        		PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1011));

        		PdfGenerator.sectionXDateTable.addCell("Reinforcement Area");
        		PdfGenerator.sectionXDateTable.addCell(Float.toString(r.m_as));
        		PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1020));

        		PdfGenerator.sectionXDateTable.addCell("Max. Reinforcement Area");
        		PdfGenerator.sectionXDateTable.addCell(Float.toString(r.m_asma));
        		PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1020));

        		PdfGenerator.sectionXDateTable.addCell("Layer Number");
        		PdfGenerator.sectionXDateTable.addCell(Float.toString(r.m_rng));
        		PdfGenerator.sectionXDateTable.addCell("[-]");

        		PdfGenerator.sectionXDateTable.addCell("Diameter");
        		PdfGenerator.sectionXDateTable.addCell(Float.toString(r.m_d));
        		PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1023));

        		PdfGenerator.sectionXDateTable.addCell("Reference area");
        		PdfGenerator.sectionXDateTable.addCell(Float.toString(r.m_aref));
        		PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1012));

        		PdfGenerator.sectionXDateTable.addCell("Distance of Bars");
        		PdfGenerator.sectionXDateTable.addCell(Float.toString(r.m_a));
        		PdfGenerator.sectionXDateTable.addCell(PdfGenerator.decryptUnits(1011));

        		iReinfBars++;
        	}
        }
        
        
        //add table to document
        PdfGenerator.document.add(PdfGenerator.sectionXDateTable);
    }
        		
    
    public static void printPdfTable_NormData() throws DocumentException
    {
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
        //---------------------------Norm data table----------------------------
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------  
    	PdfGenerator.normDateTable =new PdfPTable(2);

        PdfPCell cell = new PdfPCell (new Paragraph ("Norm & Design Code Data"));

        cell.setColspan (3);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPadding (10.0f);
        cell.setBackgroundColor (new BaseColor (140, 221, 8));					               

        PdfGenerator.normDateTable.addCell(cell);
        PdfGenerator.normDateTable.setSpacingBefore(30.0f);       // Space Before table starts, like margin-top in CSS
        PdfGenerator.normDateTable.setSpacingAfter(30.0f);        // Space After table starts, like margin-Bottom in CSS
        

        PdfGenerator.normDateTable.addCell("Norm & Design Code");
        PdfGenerator.normDateTable.addCell(decryptNormType(AnalysisMemberEC3Analysis.currentNorm.m_dc));
        //------------------------------------------------------------------------	          
        PdfGenerator.normDateTable.addCell("Norm Country code");
        PdfGenerator.normDateTable.addCell(Integer.toString(AnalysisMemberEC3Analysis.currentNorm.m_Country));
        //------------------------------------------------------------------------	          
        PdfGenerator.normDateTable.addCell("Norm Code");
        PdfGenerator.normDateTable.addCell(Integer.toString(AnalysisMemberEC3Analysis.currentNorm.m_Code));
        //------------------------------------------------------------------------	          
        PdfGenerator.normDateTable.addCell("Norm Year");
        PdfGenerator.normDateTable.addCell(Integer.toString(AnalysisMemberEC3Analysis.currentNorm.m_Year));
        //------------------------------------------------------------------------	        
        PdfGenerator.normDateTable.addCell("Norm Class");
        PdfGenerator.normDateTable.addCell(Integer.toString(AnalysisMemberEC3Analysis.currentNorm.m_Class));
        //------------------------------------------------------------------------	        
        PdfGenerator.normDateTable.addCell("Seismic Class");
        PdfGenerator.normDateTable.addCell(Integer.toString(AnalysisMemberEC3Analysis.currentNorm.m_Seis));
        //------------------------------------------------------------------------	        
        PdfGenerator.normDateTable.addCell("Snow Class");
        PdfGenerator.normDateTable.addCell(Integer.toString(AnalysisMemberEC3Analysis.currentNorm.m_Snow));
        //------------------------------------------------------------------------	        
        PdfGenerator.normDateTable.addCell("Wind Class");
        PdfGenerator.normDateTable.addCell(Integer.toString(AnalysisMemberEC3Analysis.currentNorm.m_Wind));   
        //------------------------------------------------------------------------	        
        PdfGenerator.normDateTable.addCell("Norm Description");
        PdfGenerator.normDateTable.addCell(AnalysisMemberEC3Analysis.currentNorm.m_normText);     
        
        //add table to document
        PdfGenerator.document.add(PdfGenerator.normDateTable);

    }
    
    public static String decryptSupportType(int ist)
    {
    	StringBuilder result = new StringBuilder();
    	
    	if (ist == 0) {
    		result.append(" ALL FIXED ");
    		return result.toString();
    	}
    	

    	if ((ist & 1) == 1)
    	{
    		result.append(" PX ");
    	}
    	if ((ist & 2) == 2)
    	{
    		result.append(" PY ");
    	}
    	if ((ist & 4) == 4)
    	{
    		result.append(" PZ ");
    	}
    	if ((ist & 8) == 8)
    	{
    		result.append(" MX ");
    	}
    	if ((ist & 16) == 16)
    	{
    		result.append(" MY ");
    	}
    	if ((ist & 32) == 32)
    	{
    		result.append(" MZ ");
    	}
    	if ((ist & 64) == 64)
    	{
    		result.append(" MB ");
    	}
    	
    	return result.toString();
    }

    
    public static String decryptNormType(int idc)
    {
    	
    	String result = null;
    	
    	switch(idc)
    	{
	    	case 0 : 
	    	{ result = "EC/EU/Original Eurocode"; break; }
	    	case 1 : 
	    	{ result = "DIN/DE/49/Germany"; break; 	}
	    	case 2 : 
	    	{ result = "OEN/AT/43/Austria"; break; }
	    	case 3 : 
	    	{ result = "SIA/CH/41/Austria"; break; }
	    	case 4 : 
	    	{ result = "GB/GB/44/Great Britain"; break;	}
	    	case 5 : 
	    	{ result = "US/USA/01/United States"; break; }
	    	case 6 : 
	    	{ result = "JS/JP/81/Japan"; break; }
	    	case 7 : 
	    	{ result = "GB/CN/86/China"; break;	}
	    	case 8 : 
	    	{ result = "IS/IND/91/India"; break; }
	    	case 9 : 
	    	{ result = "RU/RUS/07/Russia"; break; }	    	
	    	case 10 : 
	    	{ result = "GR/GR/30/Hellas--Greece"; break; }		    	
	    	case 11 : 
	    	{ result = "NL/NL/31/Netherlands"; break; }		    	
	    	case 12 : 
	    	{ result = "BE/B/32/Belgium"; break; }		    		    	
	    	case 13 : 
	    	{ result = "FR/F/33/France"; break; }		    	
	    	case 14 : 
	    	{ result = "ES/E/34/Espagna"; break; }
	    	case 15 : 
	    	{ result = "DK/DK/45/Danmark"; break; }
	    	case 16 : 
	    	{ result = "SE/S/46/Sweden"; break; }
	    	case 17 : 
	    	{ result = "NO/N/47/Norway"; break; }
	    	case 18 : 
	    	{ result = "FI/FIN/358/Suomi/Finland"; break; }
	    	case 19 : 
	    	{ result = "IT/I/39/Italy"; break; }
	    	case 20 : 
	    	{ result = "PT/P/351/Portugal"; break; }
	    	case 21 : 
	    	{ result = "IE/IRL/353/Ireland"; break; }
	    	case 22 : 
	    	{ result = "IL/IL/972/Israel"; break; }
	    	case 23 : 
	    	{ result = "TR/TR/90/Turkey"; break; }
	    	case 30 : 
	    	{ result = "RO/RO/40/Romania"; break; }
	    	case 31 : 
	    	{ result = "SK/SK/42/Slovakia"; break; }
	    	case 32 : 
	    	{ result = "CZ/CZ/42/Czech Republic"; break; }
	    	case 33 : 
	    	{ result = "HU/H/36/Hungary"; break; }
	    	case 34 : 
	    	{ result = "PL/PL/48/Poland"; break; }	    	
	    	case 35 : 
	    	{ result = "AL/AL/355/Albania"; break; }	    	
	    	case 36 : 
	    	{ result = "BG/BG/359/Bulgaria"; break; }
	    	case 37 : 
	    	{ result = "LT/LT/370/Litauen"; break; }
	    	case 38 : 
	    	{ result = "LV/LV/371/Lettland"; break; }
	    	case 39 : 
	    	{ result = "EE/EST/372/Estland"; break; }
	    	case 40 : 
	    	{ result = "MD/MD/373/Moldawia"; break; }
	    	case 41 : 
	    	{ result = "BY/BY/375/Belarus"; break; }
	    	case 42 : 
	    	{ result = "UA/UA/380/Ukraine"; break; }
	    	case 43 : 
	    	{ result = "YU/YU/381/Jugoslavia"; break; }
	    	case 44 : 
	    	{ result = "HR/HR/385/Hrvatska"; break; }
	    	case 45 : 
	    	{ result = "SI/SLO/386/Slowenija"; break; }
	    	case 46 : 
	    	{ result = "BA/BIH/387/Bosnien-Herzegowina"; break; }
	    	case 47 : 
	    	{ result = "MK/MK/389/Mazedonia"; break; }
	    	case 50 : 
	    	{ result = "CA/CDN/2/Canada"; break; }
	    	case 51 : 
	    	{ result = "PE/PE/51/Peru"; break; }	    	
	    	case 52 : 
	    	{ result = "MX/MEX/52/Mexiko"; break; }	
	    	case 53 : 
	    	{ result = "CU/C/53/Cuba"; break; }		    	
	    	case 54 : 
	    	{ result = "AR/RA/54/Argentinia"; break; }		    	
	    	case 55 : 
	    	{ result = "BR/BR/55/Brasilia"; break; }		    	
	    	case 56 : 
	    	{ result = "CL/RCH/56/Chile"; break; }		    	
	    	case 57 : 
	    	{ result = "CO/CO/57/Kolumbia"; break; }		    	
	    	case 58 : 
	    	{ result = "VE/YV/58/Venezuela"; break; }		    	
	    	case 60 : 
	    	{ result = "MY/MAL/60/Malaysia"; break; }		    	
	    	case 61 : 
	    	{ result = "AU/AUS/61/Australia"; break; }		    	
	    	case 62 : 
	    	{ result = "ID/RI/62/Indonesien"; break; }		    	
	    	case 63 : 
	    	{ result = "PH/RP/63/Philippinen"; break; }		    	
	    	case 64 : 
	    	{ result = "NZ/NZ/64/New Zealand"; break; }		    	
	    	case 65 : 
	    	{ result = "SG/SGP/65/Singapur"; break; }		    	
	    	case 66 : 
	    	{ result = "TH/THA/66/Thailand"; break; }		    	
	    	case 67 : 
	    	{ result = "KR/ROK/82/Korea"; break; }		    	
	    	case 68 : 
	    	{ result = "VN/VN/84/Vietnam"; break; }		    	
	    	case 69 : 
	    	{ result = "TW/RCA/886/Taiwan"; break; }		    	
	    	case 70 : 
	    	{ result = "HK/HK/852/Hongkong"; break; }
	    	case 71 : 
	    	{ result = "PK/PK/92/Pakistan"; break; }		    	
	    	case 72 : 
	    	{ result = "IR/IR/98/Iran"; break; }		    	
	    	case 80 : 
	    	{ result = "ZA/ZA/27/South Africa"; break; }		    	
	    	case 81 : 
	    	{ result = "EG/ET/20/Egypt"; break; }		    	
	    	
	    	default : 
	    	{ result = "unknown/unimplemented code/norm"; break; }
    	}
    	
    	return result;
    }
   
    
    public static String decryptUnits(int unit)
    {
    	String result = null;
    	
    	switch(unit)
    	{
//EXPLICIT	    
	    	case 1001 : //1001 * GEO_LENGTH        Geometric length in the model       l (m)
	    	{
	    		result = "[m]"; break;
	    	}
	    	case 1002 : //1002   GEO_AREA          Geometric areas in the model        A (m2)
	    	{
	    		result = "[m2]"; break;
	    	}
	    	case 1003 : //1003 * GEO_DEFORMATION   Deformations                        u (mm)
	    	{
	    		result = "[m]"; break;
	    	}
	    	case 1004 : //1004 * GEO_ROTATION      Rotational deformations           phi (mrad)
	    	{
	    		result = "[rad]"; break;
	    	}
	    	case 1005 : //1005 * GEO_DISTORTION    Distortion (Verwindungen)           u (1/km)
	    	{
	    		result = "[1/m]"; break;
	    	}
	    	case 1006 : //1006 * GEO_ELEVATION     Geometric elevation                 H (m)
	    	{
	    		result = "[m]"; break;
	    	}
	    	case 1008 : //1008 * GEO_VOLUME        Geometric volumes in the model      V (m³)
	    	{
	    		result = "[m3]"; break;
	    	}
	    	case 1009 : //1009 * GEO_CURVATURE     Curvature in Geometry               k (1/km)
	    	{
	    		result = "[1/km]"; break;
	    	}
	    	case 1010 : // 1010 * GEO_THICKNESS     Thickness                           t (m)
	    	{
	    		result = "[m]"; break;
	    	}
	    	case 1011 : //1011 * SECT_LENGTH       Dimension of cross sections       b,h (mm)
	    	{
	    		result = "[m]"; break;
	    	}	
	    	case 1012 : //1012 * SECT_AREA         Area of cross sections              A (m2)
	    	{
	    		result = "[m2]"; break;
	    	}	    
	    	case 1013 : //1013   SECT_AREA_3       3rd order area of cross sections    W (m³)
	    	{
	    		result = "[m3]"; break;
	    	}	    	
	    	case 1014 : //1014 * SECT_AREA_4       4th order area of cross sections    I (m4)
	    	{
	    		result = "[m4]"; break;
	    	}	    	
	    	case 1015 : //1015   SECT_AREA_5       5th order area of cross sections    A (m5)
	    	{
	    		result = "[m5]"; break;
	    	}		    	
	    	case 1016 : //1016 * SECT_AREA_6       6th order area of cross sections   Cm (m6)
	    	{
	    		result = "[m6]"; break;
	    	}	
	    	case 1017 : //1017 * SECT_RESI_2       Resistance of sections            1/W (1/m2)
	    	{
	    		result = "[1/m2]"; break;
	    	}    	
	    	case 1018 : //1018 * SECT_RESI_3       Resistance of sections            1/W (1/m³)
	    	{
	    		result = "[1/m3]"; break;
	    	}
	    	case 1019 : //1019   SECT_AREA_LENG    Surface area per length             - (m2/m)
	    	{
	    		result = "[m2/m]"; break;
	    	}
	    	case 1020 : //1020 * REINF_AREA        Reinforcement area As              As (cm2)
	    	{
	    		result = "[m2]"; break;
	    	}
	    	case 1021 : //1021 * REINF_AREA_L      Reinforcement area As per length   As (cm2/m)
	    	{
	    		result = "[m2/m]"; break;
	    	}
	    	case 1022 : //1022   REINF_AREA_A      Reinforcement area As per area     As (cm2/m2)
	    	{
	    		result = "[m2/m2]"; break;
	    	}
	    	case 1023 : //1023 * REINF_DIAMETER    Diameter of Reinforcement           D (mm)
	    	{
	    		result = "[m]"; break;
	    	}    	
	    	case 1024 : //1024 * REINF_COVER       Cover resp. static distance         d (mm)
	    	{
	    		result = "[m]"; break;
	    	} 
	    	//1025 : obsoleted
	    	case 1026 : //1026 * REINF_CRACKW      Crackwidth for reinforcements       w (mm)
	    	{
	    		result = "[m]"; break;
	    	} 	    	
	    	case 1027 : //1027 * TENDON_AREA       Tendon or duct area                At (mm2)
	    	{
	    		result = "[m2]"; break;
	    	} 		    	
	    	case 1028 : //1028 * TENDON_FORCE      Tendon Force                        Z (kN)
	    	{
	    		result = "[kN]"; break;
	    	} 	    	
	    	case 1029 : //1029 * REINF_AREA_G      Generalized Reinforcement area     As (cm2 or cm2/m)
	    	{
	    		result = "[m2 or m2/m]"; break;
	    	} 	    	
	    	case 1030 : //1030   REINF_TOTAL       Total Reinforcement weight          G (kg)
	    	{
	    		result = "[kg]"; break;
	    	}
	    	case 1080 : //1080   PRESSURE          Pressure                            p (bar)
	    	{
	    		result = "[bar]"; break;
	    	}
	    	case 1081 : //1081   STRAIN            Strain                             ^e (o/oo)
	    	{
	    		result = "[o/oo]"; break;
	    	}
	    	case 1082 : //1082   STRAIN_R          Twist                              ^k (1/km)
	    	{
	    		result = "[1/km]"; break;
	    	}
	    	case 1088 : //1088 * SOIL_MODULE       Elastizity or Shear modulus         E (kN/m2)
	    	{
	    		result = "[kN/m2]"; break;
	    	}
	    	case 1089 : //1089 * SOIL_STRESS       Stress of soil materials            f (kN/m2)
	    	{
	    		result = "[kN/m2]"; break;
	    	}
	    	case 1090 : //1090 * MAT_MODULE        Elastizity or Shear modulus         E (N/mm2)
	    	{
	    		result = "[N/m2]"; break;
	    	}
	    	case 1091 : //1091 * MAT_WEIGHT        self weight                       gam (kN/m³)
	    	{
	    		result = "[kN/m3]"; break;
	    	}	    	
	    	case 1092 : //1092 * MAT_STRESS        Stress of materials                 f (N/mm2)
	    	{
	    		result = "[N/m2]"; break;
	    	}	    	
	    	case 1093 : //1093   MAT_ENERGY        Material deformation energy         - (kNm)
	    	{
	    		result = "[kNm]"; break;
	    	}
	    	case 1095 : //1095 * MAT_ELSUP_P1      Elastic support force/deformation   C (kN/m)
	    	{
	    		result = "[kN/m]"; break;
	    	}
	    	case 1096 : //1096 * MAT_ELSUP_P2      Elastic support force/deform/length C (kN/m2)
	    	{
	    		result = "[kN/m2]"; break;
	    	}
	    	case 1097 : //1097 * MAT_ELSUP_P3      Elastic support force/deform/area   C (kN/m³)
	    	{
	    		result = "[kN/m3]"; break;
	    	}
	    	case 1098 :// 1098 * MAT_ELSUP_M1      Elastic support moment/rotation     C (kNm/rad)
	    	{
	    		result = "[kNm/rad]"; break;
	    	}	  
	    	case 1099 ://1099 * MAT_ELSUP_M2      Elastic support moment/rotat/length C (kNm/rad/m)
	    	{
	    		result = "[kNm/rad/m]"; break;
	    	}  
	    	case 1100 ://1100 * MAT_ELSUP_M3      Elastic support moment/rotat/area   C (kNm/rad/m2)
	    	{
	    		result = "[kNm/rad/m2]"; break;
	    	}	    	
	    	case 1101 ://1101 * BEAM_NFORCE       Normal force in beam/truss/cable    N (kN)
	    	{
	    		result = "[kN]"; break;
	    	}	    		   
	    	case 1102 ://1102 * BEAM_SFORCE       Shear force in beams               Vy (kN)
	    	{
	    		result = "[kN]"; break;
	    	}  
	    	case 1103 ://1103 * BEAM_TORSION      Torsional moment in beams          Mt (kNm)
	    	{
	    		result = "[kNm]"; break;
	    	}  
	    	case 1104 ://1104 * BEAM_BENDING      Bending moment in beams            My (kNm)
	    	{
	    		result = "[kNm]"; break;
	    	}
	    	case 1105 ://1105 * BEAM_BIMOMENT     Warping bimoment in beams          Mb (kNm2)
	    	{
	    		result = "[kNm2]"; break;
	    	}  
	    	case 1111 ://1111 * PLATE_NFORCE      Membran forces in plates/coques  n-xx (kN/m)
	    	{
	    		result = "[kN/m]"; break;
	    	}  
	    	case 1112 ://1112 * PLATE_SFORCE      Shear forces in plates/coques     v-x (kN/m)
	    	{
	    		result = "[kN/m]"; break;
	    	}
	    	case 1113 ://1113 * PLATE_TORSION     Torsional moment plates/coques   m-xy (kNm/m)
	    	{
	    		result = "[kNm/m]"; break;
	    	}
	    	case 1114 : // 1114 * PLATE_BENDING     Bending moment in plates/coques  m-xx (kNm/m)
	    	{
	    		result = "[kNm/m]"; break;
	    	}   	
	    	case 1151 ://1151   SUPP_POINT        Supporting force in a point         P (kN)
	    	{
	    		result = "[kN]"; break;
	    	}  	  
	    	case 1152 : //1152   SUPP_MOMENT       Supporting moment in a point        M (kNm)
	    	{
	    		result = "[kNm]"; break;
	    	}
	    	case 1153 : //1153   SUPP_LINE         Supporting force per length         p (kN/m)
	    	{
	    		result = "[kN/m]"; break;
	    	}    	 
	    	case 1154 : //1154   SUPP_LMOMENT      Supporting moment per length        m (kNm/m)
	    	{
	    		result = "[kNm/m]"; break;
	    	}  	
	    	case 1155 : //1155   SUPP_AREA         Supporting force per area           p (kN/m2)
	    	{
	    		result = "[kN/m2]"; break;
	    	}	  
	    	case 1156 : //1156   SUPP_AMOMENT      Supporting moment per area          m (kNm/m2)
	    	{
	    		result = "[kNm/m2]"; break;
	    	}   	  
	    	case 1157 : //1157   MAT_ELSUP_M0      Elastic Support force/rotation      C (kN/rad)
	    	{
	    		result = "[kN/rad]"; break;
	    	} 
	    	case 1158 : //1158   MAT_ELSUP_MB1     Elastic Support warp/deformation    C (kNm2/m)
	    	{
	    		result = "[kNm2/m]"; break;
	    	}
	    	case 1159 : //1159   MAT_ELSUP_MB2     Elastic Support warp/twist          C (kNm³)
	    	{
	    		result = "[kNm3]"; break;
	    	}
	    	case 1160 : //1160 * VISCOSITY_P2      Damping force / area                D (kNsec/m³)
	    	{
	    		result = "[kNsec/m3]"; break;
	    	}
	    	case 1161 : //1161 * VISCOSITY_M2      Damping moment / area               D (kNsec/m)
	    	{
	    		result = "[kNsec/m]"; break;
	    	}
	    	case 1180 : //1180 * MASS_POINT        Point mass                          M (t)
	    	{
	    		result = "[kg]"; break;
	    	} 
	    	case 1181 : //1181 * MASS_LINE         massdistribution per length         m (t/m)
	    	{
	    		result = "[kg/m]"; break;
	    	}
	    	case 1182 : //1182 * INERTIA_POINT     Rotational mass                     I (t*m2)
	    	{
	    		result = "[kg*m2]"; break;
	    	} 
	    	case 1184 : //1184   INERTIA_DISTR     massdistribution per area           i (t/m2)
	    	{
	    		result = "[kg/m2]"; break;
	    	}  
	    	case 1190 : //1190 * LOAD_POINT        Single point load                   P (kN)
	    	{
	    		result = "[kN]"; break;
	    	} 
	    	case 1191 : //1191 * LOAD_LINE         Line load                           p (kN/m)
	    	{
	    		result = "[kN/m]"; break;
	    	}
	    	case 1192 : //1192 * LOAD_AREA         Surface load                        p (kN/m2)
	    	{
	    		result = "[kN/m2]"; break;
	    	}	  
	    	case 1193 : //1193 * LOAD_VOLUME       Volume load                         p (kN/m³)
	    	{
	    		result = "[kN/m3]"; break;
	    	}  
	    	case 1194 : //1194 * LOAD_MOMENT       Point moment                        M (kNm)
	    	{
	    		result = "[kNm]"; break;
	    	}
	    	case 1195 : //1195 * LOAD_LMOMENT      Line moment loading                 m (kNm/m)
	    	{
	    		result = "[kNm/m]"; break;
	    	}
	    	case 1196 : //1196 * LOAD_AMOMENT      Area moment loading                 m (kNm/m2)
	    	{
	    		result = "[kNm/m2]"; break;
	    	}

//IMPLICIT	    	
	    	case 1 : //1   -                    X 201    °K (for CFD)
	    	{
	    		result = "[°K]"; break;
	    	}
	    	case 2 : //2   0.001                 X 202    °C (for CFD)
	    	{
	    		result = "[°C]"; break;
	    	}	    	
	    	case 3 : //3   Rad                   203    gon
	    	{
	    		result = "[rad]"; break;
	    	}	    	
	    	case 4 : //4   mrad                    X 204    °F (for CFD)
	    	{
	    		result = "[rad]"; break;
	    	}	    	
	    	case 5 : //5   °                         205    °##'##.##"
	    	{
	    		result = "[°]"; break;
	    	}	    	
	    	case 6 : //6   °C                        206    °F  (Fahrenheit)
	    	{
	    		result = "[°C]"; break;
	    	}	
	    	case 7 : //7   °/m                       207    °F/in
	    	{
	    		result = "[°/m]"; break;
	    	}
	    	case 8 : //8   o/o                       208
	    	{
	    		result = "[o/o]"; break;
	    	}
	    	case 9 : //9   o/oo                      209 
	    	{
	    		result = "[o/oo]"; break;
	    	}
	    	case 10 : //10   1/mrad                  210    1/°F
	    	{
	    		result = "[1/rad]"; break;
	    	}	    	
//Length	    	
	    	case 11 : // 11   km                        211    mi
	    	{
	    		result = "[m]"; break;
	    	}
	    	case 12 : // 12   - (obsoleted)             212    yd
	    	{
	    		result = "[obsoleted]"; break;
	    	}
	    	case 13 : // 13   m                         213    ft
	    	{
	    		result = "[m]"; break;
	    	}
	    	case 14 : //           14   dm                        214    in
	    	{
	    		result = "[m]"; break;
	    	}	    	
	    	case 15 : //           15   cm                        215    
	    	{
	    		result = "[m]"; break;
	    	}	    	
	    	case 16 : //           16   mm                        216      
	    	{
	    		result = "[m]"; break;
	    	}



//Curvature      17   1/m                       217    °/ft
//           18   1/km                      218    1/ft
//           19   °/m (Angle/m)             219    1/mile
//          191   rad/m
//           20   1/mm                      220    1/in
//Areas          21   km2                       221    Square yard
//           22   m2                        222    Square foot
//           23   cm³                       223    Square inch
//           24   cm2                       224
//           25   mm2                       225
//           26   cm2/m                     226    Square inch/foot
//           27   cm2/m2                    227    Squ inch/squ ft
//           28   m2/m                      228    Square foot/ft
//           29   mm2/m                     229
//           30   mm2/m2                    230
//Volume         31   km³                       231    Cubic yard
//           32   m³                        232    Cubic foot
//           33   l                         233    gallon
//           34   mm³                       234    Cubic inch
//Fluids         35  (m³/sec)                   235
//           36  (l/sec)                    236    gallons/second
//           37  (l/min)                    237    gallons/minute
//           38  (l/h)                      238    gallons/hour  
//           39  (W)                        239
//           40                             240
//Inertia        41   cm4                       241
//           42   cm5                       242
//           43   mm4                       243    foot4
//           44   m4                        244    inch4
//           45   m5                        245    inch5
//           46   m6                        246    inch6
//           47   mm5                       247    foot5
//           48   mm6                       248    foot6
//           49   cm6                       249
//Mass           50   Gramm                     250    Ounzes
//           51   Kilogramm=Nsec2/m         251    Pound (lb)
//           52   Tonne=kNsec2/m            252    Long Ton
//           53   Tonne                     253    Short Ton
//           54   Kgm2=Nsec2m               254    ft2.lb
//           55   Tonnen*m2=kNsec2*m        255    ft2.Short Ton
//           56   Tonnen/m =kNsec2/m2       256    Short Ton/ft
//           57   Tonnen/m2=kNsec2/m³       257    Short Ton/ft2
//           58   kg/l=t/m³=g/cm³           258
//           59   kg/m³                     259    Pound/Kubikfoot
//          158   kg/m  (special factor for rho from gam in kN/m)
//          159   kg/m³ (special factor for rho from gam in kN/m³)
//Forces         60   Newton                    260    Pound  (lb)
//and Moments    61   MN                        261
//Stiffness      62   kN                        262    kip
//           63   MNm                       263    Inch.Kip
//           64   kNm                       264    Foot.Kip
//           65   MN/m                      265    lbf/foot
//           66   kN/m                      266    Kip/foot
//           67   MNm/m                     267    Inch*kip/foot
//           68   kNm/m                     268    foot*kip/foot
//          161-189 tiny forces see below
//Stress         69   Pa                        269    Inch*Kip/rad
//           70   kPa                       270    Foot*Kip/rad
//           71   MPa                       271    Kilolbf/inch2 (ksi)
//           72   kN/m2                     272    Pound/inch2   (psi)
//           73   kN/cm2                    273    pound/foot2
//           74   N/mm2 (=MPa)              274    kip/in2
//           75   MN/m2 (=MPa)              275    lbf/in2
//           76   bar                       276
//Weight         77   N/mm³                     277    Pound/inch3
//Bedding        78   kN/m³                     278    Pound/foot3
//           79                             279    KiloPound/foot3
//           80   MN/m³                     280
//           81   tm                        281    Inch*kip/rad/foot
//           82   tm2/m                     282    foot*kip/rad/foot
//Stiffness      83   kN*m2                     283    KiloPound*inch2
//Unit stress    84   1/m2                      284    1/in2
//           85   1/m³                      285    1/in3
//           86   1/cm2                     286
//           87   1/cm³                     287
//           88   kNm/m2                    288    kip*ft/ft2
//           89   Nmm/mm2                   289    
//Time           90   sec                       290    s                 (seconds)
//           91   min                       291    ####m##.##s       (minutes)
//           92   h (hours)                 292    ####h##m##.##s    (hours)
//           93   t (Tage)                  293    ####d##h##m##.##s (days)
//           94   a (Jahre)                 294                             
//          192   1/min 
//          193   1/h
//Speed          95   1/sec                     295    in/sec
//           96   m/sec                     296    ft/sec
//           97   km/h                      297    mi/h
//Accelerat.     98   1/sec2                    298    1/min2
//           99   m/sec2                    299    ft/sec2
//Viscosity     100   sec/cm2                   300    sec/inch2
//          101   kNsec/m                   301    kipsec/in
//          102   kNmsec                    302    kipinsec
//          103   1/msec                    303    kg/sec 
//          104   1/msec2                   304    kg/min
//          105   kNsec/m2                  305    kg/h
//          106   Nsec/m2                   306    kg/d

	    	case 107 : //Heat Flow     107   1/K                       307    kg/sec/m     (old 157)
	    	{
	    		result = "[1/K]"; break;
	    	}
//Mass Flow     108   1/kg                      308    kg/min/m
//          109   1/t                       309    kg/h/m
//Tiny forces   110  (Pa)                       310    kg/d/m
//          111 + Nm                        311    kg/sec/m2

	    	case 112 : //          112 + N/m                       312    kg/min/m2
	    	{
	    		result = "[N/m]"; break;
	    	}
//          113 + N/m2                      313    kg/h/m2
//          114 + N/m³                      314    kg/d/m2   
//          115 + Nm/m                      315    kg/sec/m³
//          116 + Nmm                       316    kg/min/m³
//          117 + N/mm                      317    kg/h/m³ 
//          118 + Nmm/m                     318    kg/d/m³
//          119 + mm/sec                    319    m³/sec
//          120 + mm/sec2                   320    m³/min
//Turbulence    121   m2/sec                    321    m³/h
//          122   m2/sec2                   322    m³/d
//          123   m2/sec3                   323    l/sec
//          124   Nmm2                      324    l/min
//          125   Nm2                       325    l/h
//          126   Nmm³                      326    l/d
//          127   Nm³                       327    m³/sec/m
//          128   J/K/kg                    328    m³/min/m
//          129   J/K/m³                    329    m³/h/m
//Heat          130   W                         330    m³/d/m
//Heat          131   kW                        331    l/sec/m
//          132   MW                        332    l/min/m
//          133   W/m                       333    l/h/m
//          134   kW/m                      334    l/d/m
//          135   MW/m                      335    m³/sec/m2
//          136   W/m2                      336    m³/min/m2
//          137   kW/m2                     337    m³/h/m2
//          138   MW/m2                     338    m³/d/m2
//          139   W/K/m2                    339    l/sec/m2
//          140   kW/K/m2                   340    l/min/m2
//          141   MW/K/m2                   341    l/h/m2
//          142   W/K/m                     342    l/d/m2
//          143   kW/K/m                    343    m³/sec/m³
//          144   MW/K/m                    344    m³/min/m³
//          145   W/m³                      345    m³/h/m³
//          146   kW/m³                     346    m³/d/m³
//          147   MW/m³                     347    l/sec/m³
//          148   Wsec                      348    l/min/m³
//          149   kWsec                     349    l/h/m³
//          150   Wh                        350    l/d/m³
//          151   kWh                     X 351   bar    (for a kg-m-sec)
//          152   MWh                     X 352   mbar   (for a kg-m-sec)
//          153   Km2/W                   X 353   Pa     (for a kg-m-sec)
//          154   kJ/m³
//          155   kJ/K/m³    
//          156         
//          157                           X 357   kg/msec
//          158   kg/m  (special factor)  X 358   kg/m³ (for a kg-m-sec)}
//          
	    	case 159 : //159   kg/m³ (special factor for rho from gam in kN/m³)
	    	{
	    		result = "[kg/m³]"; break;
	    	}
	    	
//          160   m³/m  (storage coefficient seepage)
//          161   Nmm/rad                 X 361   J/K/kg
//          162   Nm/rad                  X 362   J/K/m³
//          163   kNm/rad                 X 363   W
//          164   MNm/rad                 X 364   kW
//          165   Nmm/m/rad               X 365   MW
//          166   Nm/m/rad                X 366   W/m
//          167   kNm/m/rad               X 367   kW/m
//          168   MNm/m/rad               X 368   MW/m
//          169   kNm³                    X 369   W/m2
//          170   Nmm³                    X 370   kW/m2
//          171   kN/rad                  X 371   MW/m2
//          172   N/rad                   X 372   W/K/m2
//          173   kN/mrad                 X 373   kW/K/m2
//          174   N/mrad                  X 374   MW/K/m2
//          175   kNm2/rad                X 375   W/K/m
//          176   kNm2/mrad               X 376   kW/K/m
//          177   Nm2/rad                 X 377   MW/K/m
//          178   Nm2/mrad                X 378   W/m³
//          179   Nmm2/rad                X 379   kW/m³
//          180   Nmm2/mrad               X 380   MW/m³
//          181   Nsec/m2                 X 381   Wsec
//          182   Nsec/mm2                X 382   kWsec
//          183   Nsec/m³                 X 383   Wh
//          184   Nsec/mm³                X 384   kWh
//          185   kNsec/m³                X 385   MWh
//          186   Nsec/m                  X 386   Km2/W
//          187   Nsec/mm                 X 387   kJ/m³
//          188   Nm/m2                   X 388   kJ/K/m³
//          189   Nmm/m2                  X 389   Wsec/kg
//          190   Nmm/mm2
//          191   rad/m
//          192   1/min 
//          193   1/h
//          194   sec/m
//          195   sec2/m2
//          196   m/sec
//          197   m/min
//          198   m/h
//          199   m/d


	
	    	default : 
	    	{
	    		result = "unknown/unimplemented"; break;
	    	}
    	}
    	
    	return result;
    	
    }
    
    
    public static void printPdfTable_SystemData() throws DocumentException
    {
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
        //---------------------------System data table----------------------------
        //------------------------------------------------------------------------
        //------------------------------------------------------------------------
        //System data table
    	PdfGenerator.sysDateTable = new PdfPTable(2);

        PdfPCell cell = new PdfPCell (new Paragraph ("System Data"));

        cell.setColspan (3);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setPadding (10.0f);
        cell.setBackgroundColor (new BaseColor (140, 221, 8));					               

        PdfGenerator.sysDateTable.addCell(cell);
        PdfGenerator.sysDateTable.setSpacingBefore(30.0f);       // Space Before table starts, like margin-top in CSS
        PdfGenerator.sysDateTable.setSpacingAfter(30.0f);        // Space After table starts, like margin-Bottom in CSS
        

        PdfGenerator.sysDateTable.addCell("Group Divisor");
        PdfGenerator.sysDateTable.addCell(Integer.toString(AnalysisMemberEC3Analysis.sysInfo.m_igdiv));
        //------------------------------------------------------------------------	          
        PdfGenerator.sysDateTable.addCell("System Name");
        PdfGenerator.sysDateTable.addCell(AnalysisMemberEC3Analysis.sysInfo.m_text);
        //------------------------------------------------------------------------	          
        PdfGenerator.sysDateTable.addCell("Number of Nodes");
        PdfGenerator.sysDateTable.addCell(Integer.toString(AnalysisMemberEC3Analysis.sysInfo.m_nknot));
        //------------------------------------------------------------------------	          
        PdfGenerator.sysDateTable.addCell("Highest Node Number");
        PdfGenerator.sysDateTable.addCell(Integer.toString(AnalysisMemberEC3Analysis.sysInfo.m_mknot));
        //------------------------------------------------------------------------
        PdfGenerator.sysDateTable.addCell("Gravity Direction");
        switch (AnalysisMemberEC3Analysis.sysInfo.m_iachs)
        {
        	case 0:
        	{  sysDateTable.addCell("undefined");
        		break;
        	}
        	case -1:
        	{  sysDateTable.addCell("negative X-axis");
        		break;
        	}
        	case -2:
        	{  sysDateTable.addCell("negative Y-axis");
        		break;
        	}
        	case -3:
        	{  sysDateTable.addCell("negative Z-axis");
        		break;
        	}
        	case +1:
        	{  sysDateTable.addCell("positive X-axis");
        		break;
        	}
        	case +2:
        	{  sysDateTable.addCell("positive Y-axis");
        		break;
        	}
        	case +3:
        	{  sysDateTable.addCell("positive Z-axis");
        		break;
        	}
        	
        }
        //------------------------------------------------------------------------	          
        sysDateTable.addCell("System Type");
        switch (AnalysisMemberEC3Analysis.sysInfo.m_iprob)
        {
        	case 0:
        	{  sysDateTable.addCell("general 3D System");
        		break;
        	}
        	case 9:
        	{  sysDateTable.addCell("sectional 2D System");
        		break;
        	}
        	case 10:
        	{  sysDateTable.addCell("plane frame (FRAME)");
        		break;
        	}
        	case 11:
        	{  sysDateTable.addCell("plane stress 2D System");
        		break;
        	}
        	case 12:
        	{  sysDateTable.addCell("plane strain 2D System");
        		break;
        	}
        	case 13:
        	{  sysDateTable.addCell("axissymmetric 3D System");
        		break;
        	}
        	case 14:
        	{  sysDateTable.addCell("plane frame (FRAME) WKS");
        		break;
        	}
        	case 15:
        	{  sysDateTable.addCell("plane stress 2D System WKS");
        		break;
        	}
        	case 16:
        	{  sysDateTable.addCell("plane strain 2D System");
        		break;
        	}
        	case 17:
        	{  sysDateTable.addCell("axissymmetric 3D System");
        		break;
        	}
        	
        	case 18:
        	{  sysDateTable.addCell("sectional y-z System (positive face)");
        		break;
        	}
        	
        	case 19:
        	{  sysDateTable.addCell("sectional y-z System (negative face)");
        		break;
        	}
        	
        	case 20:
        	{  sysDateTable.addCell("general 2D System");
        		break;
        	}
        	
        	case 30:
        	{  sysDateTable.addCell("plane girder 2D System");
        		break;
        	}
        	
        	case 31:
        	{  sysDateTable.addCell("prestressed plane girder 2D System");
        		break;
        	}
        	
        	case 34:
        	{  sysDateTable.addCell("plane girder 2D System WKS");
        		break;
        	}
        	
        	case 35:
        	{  sysDateTable.addCell("prestressed plane girder 2D System WKS");
        		break;
        	}
        	
        }
        
        //add table to document
        PdfGenerator.document.add(sysDateTable);
    }
    
    

}