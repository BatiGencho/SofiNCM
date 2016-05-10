package sofistik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import gui.*;
import gui.GuiAnalysisParams.eBucklingLengthOption;
import gui.GuiAnalysisParams.eBucklingType;
import gui.GuiAnalysisParams.eNationalAnnex;
import gui.GuiAnalysisParams.eSwayType;

public class AnalysisMemberEC3Analysis {

	public static AnalysisNorm currentNorm; // we only have one norm, use the native getDataForNorm to fill it with data
	public static HashMap<Integer,AnalysisMaterial> arrMats;
	public static HashMap<Integer,AnalysisCSection> arrSects;
	public static HashMap<Integer,AnalysisNode> arrNodes;
	public static HashMap<Integer,AnalysisMemberEC3MemberCase> hmMembers;
	public static AnalysisSystemInfo sysInfo;
	public static double maximumMyedI;
	public static double maximumMzedI;
	
	public static GuiAnalysis pGuiAnalysis = null;
	
	public enum AnalysisState {
		ANALYSIS_VOID, ANALYSIS_STARTED, ANALYSIS_READY,ANALYSIS_FAILED;
	}
	public static AnalysisState analysisState;
	
	AnalysisMemberEC3Analysis() {
		
		AnalysisMemberEC3Analysis.maximumMyedI = 0.0;
		AnalysisMemberEC3Analysis.maximumMzedI = 0.0;
		AnalysisMemberEC3Analysis.arrMats = null;
		AnalysisMemberEC3Analysis.arrSects = null;
		AnalysisMemberEC3Analysis.arrNodes = null;
		AnalysisMemberEC3Analysis.sysInfo = null;
		AnalysisMemberEC3Analysis.currentNorm = null;
		AnalysisMemberEC3Analysis.analysisState = AnalysisState.ANALYSIS_VOID;
		AnalysisMemberEC3Analysis.hmMembers = null;
	}
	
	
	public static void doPrepareAdditionalDesignDataAndAnalyse(final GuiAnalysis pGuiAnalysis)
	{
		//get a pointer to the main gui in order to use the console

		if (pGuiAnalysis == null) return;
		AnalysisMemberEC3Analysis.pGuiAnalysis = pGuiAnalysis;	
		
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//check the requested analysis method at this stage of the implementation
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		boolean isAnalysisMethodSupported = true;
		switch (AnalysisOptions.selectedAnalysisOption)
		{
			case SIMPLIFIED_METHOD : { isAnalysisMethodSupported = false; break; }
			case NOMINAL_STIFFNESS_METHOD : { isAnalysisMethodSupported = false; break; }
			case GENERAL_METHOD : { isAnalysisMethodSupported = false; break; }
			case NOMINAL_CURVATURE_METHOD : { isAnalysisMethodSupported = true; break; }
			default: { isAnalysisMethodSupported = false; break; }
		}
		if (!isAnalysisMethodSupported)
		{
			JOptionPane.showMessageDialog(null, "Selected Anylysis Method is not yet implemented", "Analysis error", JOptionPane.WARNING_MESSAGE);
			AnalysisMemberEC3Analysis.pGuiAnalysis.analysisOutputArea.append("Error : Selected Anylysis Method is not yet implemented\n");
			AnalysisMemberEC3Analysis.analysisState = AnalysisState.ANALYSIS_FAILED;
			return;
		}

		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//get the norm first using natives...
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		AnalysisMemberEC3Analysis.currentNorm = new AnalysisNorm();
		if (AnalysisMemberEC3Analysis.currentNorm != null)  AnalysisMemberEC3Analysis.currentNorm.getDataForNorm();
	
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//get the structural system then using natives...
		AnalysisMemberEC3Analysis.sysInfo = new AnalysisSystemInfo();
		if (sysInfo != null) sysInfo.getDataForSystemInfo();
		
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//get structural line based design member to be analyzed
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		AnalysisMember member = AnalysisMembersCollection.membersCollection.get(AnalysisOptions.selectedStrLine);
		if (member==null)
		{
			JOptionPane.showMessageDialog(null, "Selected member on structural line could not be found", "Analysis error", JOptionPane.ERROR_MESSAGE);
			AnalysisMemberEC3Analysis.pGuiAnalysis.analysisOutputArea.append("Error : Selected member on structural line could not be found");
			AnalysisMemberEC3Analysis.analysisState = AnalysisState.ANALYSIS_FAILED;
			return;
		}
		
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------		
		//loop over all subelements for str line & collect unique materials and cross - sections
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		AnalysisMemberEC3Analysis.arrMats = new HashMap<Integer,AnalysisMaterial>();
		AnalysisMemberEC3Analysis.arrSects = new HashMap<Integer,AnalysisCSection>();
		AnalysisMemberEC3Analysis.arrNodes = new HashMap<Integer,AnalysisNode>();
		
		boolean isTopNodeFixed = false;
		boolean isBottomNodeFixed = false;
		
		for (AnalysisMemberSubElement arrSubElem : member.arrSubElements)
		{
			//get first sub-beam node using natives
			AnalysisNode n1 = new AnalysisNode(arrSubElem.nodeId1);
			n1.getDataForNode(); //get data about the node support conditions
			if (n1.m_kfix == 0) isTopNodeFixed = true;
			AnalysisMemberEC3Analysis.arrNodes.put(arrSubElem.nodeId1,n1);
			
			//get second sub-beam node using natives
			AnalysisNode n2 = new AnalysisNode(arrSubElem.nodeId2);
			n2.getDataForNode();  //get data about the node support conditions
			if (n2.m_kfix == 0) isBottomNodeFixed = true;
			AnalysisMemberEC3Analysis.arrNodes.put(arrSubElem.nodeId2,n2);
			
			//get sub-beam start & end cross-sections
			AnalysisMemberEC3Analysis.arrSects.put(arrSubElem.sectId1,new AnalysisCSection(arrSubElem.sectId1));
			AnalysisMemberEC3Analysis.arrSects.put(arrSubElem.sectId2,new AnalysisCSection(arrSubElem.sectId2));
			
			//get sub-beam all cross-sectional materials
			for (int mid = 0 ; mid<arrSubElem.mats.length; mid++)
			{
				AnalysisMemberEC3Analysis.arrMats.put(arrSubElem.mats[mid],new AnalysisMaterial(arrSubElem.mats[mid]));
			}
		}
		
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------		
		// 1. fill Mats details from Native for current beam materials
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		int nConcreteMaterials = 0;
		int nSteelMaterials=0;
		int nOtherMaterials=0;
		for (Map.Entry<Integer,AnalysisMaterial> iMat : AnalysisMemberEC3Analysis.arrMats.entrySet())
		{
			iMat.getValue().getDataForMaterial(); //get data from native
			int iMatType = (int)iMat.getValue().m_type/1000;
			switch (iMatType)
			{
				case 1: nConcreteMaterials++; break;
				case 2: nSteelMaterials++; break;
				default:nOtherMaterials++; break;
			}
		}
		if (nConcreteMaterials==0)
		{
			JOptionPane.showMessageDialog(null, "No concrete material was found to be present in the member", "Analysis error", JOptionPane.ERROR_MESSAGE);
			pGuiAnalysis.analysisOutputArea.append("Error : No concrete material was found to be present in member " + AnalysisOptions.selectedStrLine + "\n");
			AnalysisMemberEC3Analysis.analysisState = AnalysisState.ANALYSIS_FAILED;
			return;
		}
		if (nSteelMaterials==0)
		{
			pGuiAnalysis.analysisOutputArea.append("Warning : no steel material (reinforcement) was found to be present in member " + AnalysisOptions.selectedStrLine + "\n");
		}
		if (nOtherMaterials!=0)
		{
			JOptionPane.showMessageDialog(null, "Timber or brick materials present in member. The latter are not analyzable", "Analysis error", JOptionPane.ERROR_MESSAGE);
			pGuiAnalysis.analysisOutputArea.append("Error : Timber or brick materials present in member " + AnalysisOptions.selectedStrLine + ". The latter are not analyzable!\n");
			AnalysisMemberEC3Analysis.analysisState = AnalysisState.ANALYSIS_FAILED;
			return;
		}
		
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		// 2. fill cs details from Native for current beam cross-sections
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		float Aconst = -1.0f;
		boolean isTapered = false;
		double Asfyd = 0.0;
		double Acfcd = 0.0;
		double A_uncracked = 0.0;
		double Iyy_uncracked = 0.0;
		double Izz_uncracked = 0.0;
		int iSection = 0;
		double EIyy = 0.0;
		double EIzz = 0.0;
		double fck = 0.0;
		double fydToEs = 0.0;
		int    iSteels = 0;
		double Dy = 0.0;
		double Dz = 0.0;
		
		for (Map.Entry<Integer,AnalysisCSection> iSect : AnalysisMemberEC3Analysis.arrSects.entrySet()) // SO FAR THE ASSUMPTION IS THAT WE HAVE ONLY ONE CONCRETE THAT WE CONSIDER
		{
			//init section data and reinforcements
			iSect.getValue().getDataForSection();
			iSect.getValue().initSectionReinf();
			
			//get the uncracked sect props = same for each beam if element is prismatic, so first section is enough
			if (iSection == 0)
			{
				A_uncracked = iSect.getValue().m_a;
				Iyy_uncracked = iSect.getValue().m_iy;
				Izz_uncracked = iSect.getValue().m_iz;
				Dy = Math.abs(iSect.getValue().m_ymax - iSect.getValue().m_ymin);
				Dz = Math.abs(iSect.getValue().m_zmax - iSect.getValue().m_zmin);
				
				if (AnalysisMemberEC3Analysis.arrMats.get(iSect.getValue().m_mno) != null) //count over the materials in the cross-section
				{
					if (AnalysisMemberEC3Analysis.arrMats.get(iSect.getValue().m_mno).m_type / 1000 == 1) //only concrete
					{
						if (AnalysisMemberEC3Analysis.arrMats.get(iSect.getValue().m_mno).m_scm != 0.0)
						{
							// Dauerstandsbeiwert: 	αcc = 	0.85 ; Betondruckfestigkeit: 	fcd = αcc fck / γc ==> = fc from CDB, but we choose to do it like this :
							Acfcd = iSect.getValue().m_a * ( (AnalysisMemberEC3Analysis.arrMats.get(iSect.getValue().m_mno).m_fck) / (AnalysisMemberEC3Analysis.arrMats.get(iSect.getValue().m_mno).m_scm) );
							EIyy = Iyy_uncracked * iSect.getValue().m_em;
							EIzz = Izz_uncracked * iSect.getValue().m_em;
							fck = AnalysisMemberEC3Analysis.arrMats.get(iSect.getValue().m_mno).m_fck;
						}
					}
				}
				
				
			}
			
			//quickly check that all reinforcement bars are of rang 0, if not yield warning
			for (AnalysisCSectionReinforcement r : iSect.getValue().getReinfBars())
			{
				//check for rang > 0
				if (r.m_ir > 0) {
					pGuiAnalysis.analysisOutputArea.append("Warning : reinforcement bar of rang " + r.m_ir + " > 0 in" + //
							" section " + iSect.getValue().id + " present! The latter will be ignored and only the minimum reinforcement of rang 0 will be used!\n");
				}
				else if (iSection == 0) //do only for the first section
				{		
					if (AnalysisMemberEC3Analysis.arrMats.get(r.m_mno) != null)
					{
						if (AnalysisMemberEC3Analysis.arrMats.get(r.m_mno).m_type / 1000 == 2) //only steels
						{
							
							if (AnalysisMemberEC3Analysis.arrMats.get(r.m_mno).m_scm != 0)
							{
								Asfyd += r.m_as * (AnalysisMemberEC3Analysis.arrMats.get(r.m_mno).m_fy / AnalysisMemberEC3Analysis.arrMats.get(r.m_mno).m_scm);
								fydToEs += (AnalysisMemberEC3Analysis.arrMats.get(r.m_mno).m_fy / AnalysisMemberEC3Analysis.arrMats.get(r.m_mno).m_scm) / AnalysisMemberEC3Analysis.arrMats.get(r.m_mno).m_emod;
								iSteels++;
							}
						}
					}
				}
			}
			//check for tapered beam
			if (Aconst < -1.0f) 
			{
				Aconst =  iSect.getValue().m_a;
			}
			else if (Aconst > 0.0f && isTapered == false)
			{
				if (Math.abs(Aconst - iSect.getValue().m_a)>1.0E-3) isTapered = true;
			}
			if (iSect.getValue().m_neffs > 0) pGuiAnalysis.analysisOutputArea.append("Warning : none-effective areas in member " + //
						AnalysisOptions.selectedStrLine + ", section " + iSect.getValue().id + " present! The latter will be ignored!\n");
			if (iSect.getValue().m_cs > 0) pGuiAnalysis.analysisOutputArea.append("Warning : construction stages in member " + //
						AnalysisOptions.selectedStrLine + ", section " + iSect.getValue().id + " present! The latter will be ignored and only the final section will be used!\n");
			
			iSection++; //move to next section
		}
		if (isTapered) //error for tapered beams
		{
			pGuiAnalysis.analysisOutputArea.append("Error : Analysis Member " + AnalysisOptions.selectedStrLine + " is tapered and no analysis method is currently available for that!\n");
			AnalysisMemberEC3Analysis.analysisState = AnalysisState.ANALYSIS_FAILED;
			return;
		}
		if (Acfcd <= 1.0E-8 ) //error for zero concrete area
		{
			pGuiAnalysis.analysisOutputArea.append("Error : Analysis Member " + AnalysisOptions.selectedStrLine + " has zero Ac.fyd which is incorrect, please check!\n");
			AnalysisMemberEC3Analysis.analysisState = AnalysisState.ANALYSIS_FAILED;
			return;
		}
		
		//calculate a mean value for the reinforcement ratios fy to Es.
		fydToEs = fydToEs / iSteels;
		
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------		
		//3. get displacements and forces for each selected load case (nodes + beam elements)
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		HashMap<Integer,Boolean> hmLoadCaseIsCompressionMember = new HashMap<Integer,Boolean>();
		HashMap<Integer,Double> hmLoadCaseN01ed = new HashMap<Integer,Double>();
		HashMap<Integer,Double> hmLoadCaseN02ed = new HashMap<Integer,Double>();
		HashMap<Integer,Double> hmLoadCaseM01yed = new HashMap<Integer,Double>();
		HashMap<Integer,Double> hmLoadCaseM01zed = new HashMap<Integer,Double>();
		HashMap<Integer,Double> hmLoadCaseM02yed = new HashMap<Integer,Double>();
		HashMap<Integer,Double> hmLoadCaseM02zed = new HashMap<Integer,Double>();
		
		HashMap<Integer,Double> hmLoadCaseTheta1yed = new HashMap<Integer,Double>();
		HashMap<Integer,Double> hmLoadCaseTheta1zed = new HashMap<Integer,Double>();
		
		HashMap<Integer,Double> hmLoadCaseTheta2yed = new HashMap<Integer,Double>();
		HashMap<Integer,Double> hmLoadCaseTheta2zed = new HashMap<Integer,Double>();
		
		
		for ( Integer iLc : AnalysisOptions.selectedLinearLoadCases) //loop over the customer selected lcs tree set
		{
			
			//-----------------------------------------------------------------------------------------------------------
			//loop over the collected analysis member sub-element nodes
			// get their displacements and forces
			for (Map.Entry<Integer, AnalysisNode > eachSubElementNode : AnalysisMemberEC3Analysis.arrNodes.entrySet())
			{
				//for each node extract the node displacements and resultant forces for the iLc
				AnalysisForcesAndDisplElementNode x = eachSubElementNode.getValue().getResultsForNode(iLc);
				if (x != null) eachSubElementNode.getValue().hmLoadCaseResults.put(iLc, x);
			}
			
			//-----------------------------------------------------------------------------------------------------------
			//get the resulted internal forces for each beam subelement of the structural line
			boolean isCompressiveLoadCase = true;
			int iElement = 0;
			for (AnalysisMemberSubElement arrSubElem : member.arrSubElements)
			{
				//======= subelement section begin
				AnalysisForcesAndDisplElementBeam xA = arrSubElem.getInternalResultsForMemberSubElement(iLc , -1);
				if (xA != null && !checkForZeroForcesAndDispl(xA))
				{
					ArrayList<AnalysisForcesAndDisplElementBeam> lcLocs;
					
					if (arrSubElem.hmLoadCaseResults.containsKey(iLc))  //if the lc is already there
					{
						lcLocs = arrSubElem.hmLoadCaseResults.get(iLc);
					}
					else 												//load case not there
					{
						lcLocs = new ArrayList<AnalysisForcesAndDisplElementBeam>();
					}
					lcLocs.add(xA);
					arrSubElem.hmLoadCaseResults.put(iLc, lcLocs);
					isCompressiveLoadCase = isCompressiveLoadCase & isCompressionMember(xA);
					if (iElement == 0) 
					{
						//forces
						hmLoadCaseN01ed.put(iLc,(double)xA.m_n);
						hmLoadCaseM01yed.put(iLc, (double)xA.m_my);
						hmLoadCaseM01zed.put(iLc, (double)xA.m_mz);
						
						//displacements
						hmLoadCaseTheta1yed.put(iLc, (double)xA.m_phiy);
						hmLoadCaseTheta1zed.put(iLc, (double)xA.m_phiz);
					}
				}
				else 		// ...check if there are any forces and displacements at all, if not=> error
				{
					pGuiAnalysis.analysisOutputArea.append("Error : Analysis Member : " + AnalysisOptions.selectedStrLine + " , subelement : " + arrSubElem.iSubElemCdbIndex + //
							" under load case " + iLc + " has no forces or displacements at all. Therefore is the entire member none-analyzable!\n");
					AnalysisMemberEC3Analysis.analysisState = AnalysisState.ANALYSIS_FAILED;
					return;
				}
				
				//=========== subelement section end
				AnalysisForcesAndDisplElementBeam xE = arrSubElem.getInternalResultsForMemberSubElement(iLc , +1);
				if (xE != null && !checkForZeroForcesAndDispl(xE))
				{
					ArrayList<AnalysisForcesAndDisplElementBeam> lcLocs;
					
					if (arrSubElem.hmLoadCaseResults.containsKey(iLc))  //if the lc is already there
					{
						lcLocs = arrSubElem.hmLoadCaseResults.get(iLc);
					}
					else 												//load case not there
					{
						lcLocs = new ArrayList<AnalysisForcesAndDisplElementBeam>();
					}
					lcLocs.add(xE);
					arrSubElem.hmLoadCaseResults.put(iLc, lcLocs);
					isCompressiveLoadCase = isCompressiveLoadCase & isCompressionMember(xE);
					if (iElement == member.arrSubElements.length - 1) 
					{
						//forces
						hmLoadCaseN02ed.put(iLc,(double)xE.m_n);
						hmLoadCaseM02yed.put(iLc, (double)xE.m_my);
						hmLoadCaseM02zed.put(iLc, (double)xE.m_mz);
						
						//displacements
						hmLoadCaseTheta2yed.put(iLc, (double)xE.m_phiy);
						hmLoadCaseTheta2zed.put(iLc, (double)xE.m_phiz);
					}
				}
				else 		// ...check if there are any forces and displacements at all, if not=> error
				{
					pGuiAnalysis.analysisOutputArea.append("Error : Analysis Member : " + AnalysisOptions.selectedStrLine + " , subelement : " + arrSubElem.iSubElemCdbIndex + //
							" under load case " + iLc + " has no forces or displacements at all. Therefore is the entire member none-analyzable!\n");
					AnalysisMemberEC3Analysis.analysisState = AnalysisState.ANALYSIS_FAILED;
					return;
				}
				
				iElement++;
			}
			//add a bool whether the load case has only N in member or not (bending load case)
			if (hmLoadCaseIsCompressionMember != null) hmLoadCaseIsCompressionMember.put(iLc, isCompressiveLoadCase);
		}
		
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------	
		//4. analyse each load combination selected
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    	AnalysisMember currentSelectedMember = AnalysisMembersCollection.membersCollection.get(AnalysisOptions.selectedStrLine);
    	
    	for ( Integer iLc : AnalysisOptions.selectedLinearLoadCases)
    	{
    		pGuiAnalysis.analysisOutputArea.append("=====================================\n");
    		pGuiAnalysis.analysisOutputArea.append(" LINEAR LOAD CASE " +  iLc + "       \n");
    		pGuiAnalysis.analysisOutputArea.append("=====================================\n");

			//construct a new EC3 container for Nachweis
			AnalysisMemberEC3MemberCase memberNachweis = null;
    		if (GuiAnalysisParams.selectedBucklingType == eBucklingType.SINGLE_COMPRESSION_BENDING.ordinal())
    		{
    			memberNachweis = new AnalysisMemberEC3SingleMemberCase();
    		}
    		else if (GuiAnalysisParams.selectedBucklingType == eBucklingType.FRAME_COMPRESSION_BENDING.ordinal())
    		{
    			memberNachweis = new AnalysisMemberEC3FrameMemberCase();
    		}
    		memberNachweis.setLoadCaseNumber(iLc);

    		//quickly check the member loading -- pure compression or compr+bending
    		Boolean bisCompressiveLoadCase = hmLoadCaseIsCompressionMember.get(iLc);
    		if (bisCompressiveLoadCase != null) 
    		{
    			if (bisCompressiveLoadCase.booleanValue())
    			{
    				pGuiAnalysis.analysisOutputArea.append("Member " + AnalysisOptions.selectedStrLine +" is in pure compression\n"); //can check only 1 dimension
    				memberNachweis.setIsCompressiveOnly(true);
    			}
    			else
    			{
    				pGuiAnalysis.analysisOutputArea.append("Member " + AnalysisOptions.selectedStrLine + " is in compressive-bending-torsional state\n"); //must check 2 dimension
    				memberNachweis.setIsCompressiveOnly(false);
    			}
    		}

    		//check if the current load case is a III order as we need a I/II ? order one here
    		if (currentSelectedMember !=  null)
    		{	
    			int iOrderForThisLoadCase = currentSelectedMember.getLoadCaseIntOrderForActiveLoadCase(iLc);
    			if (  iOrderForThisLoadCase != 1 && iOrderForThisLoadCase != 2) {
    				pGuiAnalysis.analysisOutputArea.append("Warning : load case " + //
    						iLc + " is not a I or II order load case for member " + AnalysisOptions.selectedStrLine + " which is insufficient for check!\n");
    			}
    		}


    		//get the buckling lengths
    		double bLy = 0.0;
    		double bLz = 0.0;

    		if (GuiAnalysisParams.selectedBucklingType == eBucklingType.SINGLE_COMPRESSION_BENDING.ordinal())
    		{
    			if (GuiAnalysisParams.userModifiedBucklLength) //USER SET BUCKLING LENGHTS
    			{
    				if (GuiAnalysisParams.selectedBucklingLengthOption == eBucklingLengthOption.ABSOLUTE.ordinal())
    				{
    					bLy = GuiAnalysisParams.buckLenLyAbs;
    					bLz = GuiAnalysisParams.buckLenLzAbs;
    				}
    				else if (GuiAnalysisParams.selectedBucklingLengthOption == eBucklingLengthOption.RELATIVE.ordinal())
    				{
    					bLy = (GuiAnalysisParams.buckLenLyRel)/100.0 * currentSelectedMember.mlength;;
    					bLz = (GuiAnalysisParams.buckLenLzRel)/100.0 * currentSelectedMember.mlength;
    				}
					memberNachweis.setBuckLenY(bLy);
					memberNachweis.setBuckLenZ(bLz);
    				pGuiAnalysis.analysisOutputArea.append("Warning : Buckling length L,y for member " + AnalysisOptions.selectedStrLine + " defined by user !\n");
    				pGuiAnalysis.analysisOutputArea.append("Warning : Buckling length L,z for member " + AnalysisOptions.selectedStrLine + " defined by user !\n");
    			}
    			else //AUTOMATICALLY COMPUTED BUCKLING LENGHTS, USE L IN MOST CASES UNLESS FULLY FIXED
    			{
    				bLy = currentSelectedMember.mlength;
    				bLz = currentSelectedMember.mlength;

    				//set the buckling lengths  in y and z acc. to the original member length, MODIFY FOR KNOWN SUPPORT CONDITIONS!!!!
    				if (isTopNodeFixed && isBottomNodeFixed) 
    				{
    					memberNachweis.setBuckLenY(bLy/2.0);
    					memberNachweis.setBuckLenZ(bLz/2.0);
    					pGuiAnalysis.analysisOutputArea.append("Warning : Buckling length L,y for member " + AnalysisOptions.selectedStrLine + " was automatically //"
    							+ "corrected due to support conditions !\n");
    					pGuiAnalysis.analysisOutputArea.append("Warning : Buckling length L,z for member " + AnalysisOptions.selectedStrLine + " was automatically //"
    							+ "corrected due to support conditions !\n");
    					// OTHERRRRRRRRRRR SUPPORTSSSSSS ?
    				}
    				else //USE ORIGINAL MEMBER LENGTH
    				{
    					memberNachweis.setBuckLenY(bLy);
    					memberNachweis.setBuckLenZ(bLz);
    					pGuiAnalysis.analysisOutputArea.append("Warning : Buckling length L,y for member " + AnalysisOptions.selectedStrLine + " set to original member length!\n");
    					pGuiAnalysis.analysisOutputArea.append("Warning : Buckling length L,z for member " + AnalysisOptions.selectedStrLine + " set to original member length!\n");
    				}
    			}

    		}
    		else if (GuiAnalysisParams.selectedBucklingType == eBucklingType.FRAME_COMPRESSION_BENDING.ordinal())
    		{
    			
    			double L = currentSelectedMember.mlength; //is always > 0
				double EIyL = EIyy / L;  //is always >= 0
				double EIzL = EIzz / L;  //is always >= 0
    			
    			//--------------rotations---------------
    			
    			double theta1y = 0.0; double theta2y = 0.0;
    			double theta1z = 0.0; double theta2z = 0.0;
    			
				if (hmLoadCaseTheta1yed.get(iLc) != null)
				{
					theta1y = hmLoadCaseTheta1yed.get(iLc);
				}
				if (hmLoadCaseTheta1zed.get(iLc) != null)
				{
					theta1z = hmLoadCaseTheta1zed.get(iLc);
				}
				if (hmLoadCaseTheta2yed.get(iLc) != null)
				{
					theta2y = hmLoadCaseTheta2yed.get(iLc);
				}
				if (hmLoadCaseTheta2zed.get(iLc) != null)
				{
					theta2z = hmLoadCaseTheta2zed.get(iLc);
				}
				
				//--------------forces--------------
				
    			double M1y = 0.0; double M2y = 0.0;
    			double M1z = 0.0; double M2z = 0.0;
				
				if (hmLoadCaseM01yed.get(iLc) != null)
				{
					M1y = hmLoadCaseM01yed.get(iLc);
				}
				if (hmLoadCaseM02yed.get(iLc) != null)
				{
					M2y = hmLoadCaseM02yed.get(iLc);
				}				
				if (hmLoadCaseM01zed.get(iLc) != null)
				{
					M1z = hmLoadCaseM01zed.get(iLc);
				}
				if (hmLoadCaseM02zed.get(iLc) != null)
				{
					M2z = hmLoadCaseM02zed.get(iLc);
				}

				//------------stiffnesses---------------
    			double k1y = 0.0;
    			double k1z = 0.0;
    			double k2y = 0.0;
    			double k2z = 0.0;
				
				//-------------- TRY TO CAPTURE SPECIAL CASES ---------------//
				
				//fully fixed top and bottom || nearly fixed  ^ very small rotations, use 0.1 as recommended
				if (isTopNodeFixed || (Math.abs(theta1y) < 1.0E-8))
				{
					k1y = 0.1;
					pGuiAnalysis.analysisOutputArea.append("Warning : k1y stiffness internally corrected to 0.1 due to fixed/nearly rigid support !\n");
				}
				
				if (isTopNodeFixed || (Math.abs(theta1z) < 1.0E-8))
				{
					k1z = 0.1;
					pGuiAnalysis.analysisOutputArea.append("Warning : k1z stiffness internally corrected to 0.1 due to fixed/nearly rigid support !\n");
				}

				if (isBottomNodeFixed || (Math.abs(theta2y) < 1.0E-8))
				{
					k2y = 0.1;
					pGuiAnalysis.analysisOutputArea.append("Warning : k2y stiffness internally corrected to 0.1 due to fixed/nearly rigid support !\n");
				}
				
				if (isBottomNodeFixed || (Math.abs(theta2z) < 1.0E-8))
				{
					k2z = 0.1;
					pGuiAnalysis.analysisOutputArea.append("Warning : k2z stiffness internally corrected to 0.1 due to fixed/nearly rigid support !\n");
				}
				
				if ((Math.abs(M1y) < 1.0E-8))  //very small bending moments , probably pin or similar in y-y
				{
					k1y = Double.MAX_VALUE;
					pGuiAnalysis.analysisOutputArea.append("Warning : k1y stiffness internally corrected due to negligible moment M1y !\n");
				}
				
				if ((Math.abs(M1z) < 1.0E-8))  //very small bending moments , probably pin or similar in z-z
				{
					k1z = Double.MAX_VALUE;
					pGuiAnalysis.analysisOutputArea.append("Warning : k1z stiffness internally corrected due to negligible moment M1z !\n");
				}
				
				if ((Math.abs(M2y) < 1.0E-8))  //very small bending moments , probably pin or similar in y-y
				{
					k2y = Double.MAX_VALUE;
					pGuiAnalysis.analysisOutputArea.append("Warning : k2y stiffness internally corrected due to negligible moment M2y !\n");
				}
				
				if ((Math.abs(M2z) < 1.0E-8))  //very small bending moments , probably pin or similar in z-z
				{
					k2z = Double.MAX_VALUE;
					pGuiAnalysis.analysisOutputArea.append("Warning : k2z stiffness internally corrected due to negligible moment M2z !\n");
				}
				
				
				// now differentiate between sway and non-sway
    			if (GuiAnalysisParams.selectedSwayType == eSwayType.NONESWAY.ordinal()) //aussteifende Bauteile 5.15
    			{
    				//-----------------------------------
    				//buckl. length lo,y
    				//-----------------------------------
    				double expressionYK1Y;
    				double expressionYK2Y;
    				
    				if (k1y == 0.1 )
    				{
    					expressionYK1Y = ( k1y / (0.45 + k1y) );
    				} 
    				else if (k1y == Double.MAX_VALUE)
    				{
    					expressionYK1Y = 1.0; //in the limit k->+inf
    				}
    				else
    				{
        				k1y = (theta1y / M1y) * EIyL;
    					expressionYK1Y = ( k1y / (0.45 + k1y) );
    				}
    				((AnalysisMemberEC3FrameMemberCase) memberNachweis).setk1y(k1y);

    				
    				
    				if (k2y == 0.1 )
    				{
    					expressionYK2Y = ( k2y / (0.45 + k2y) );
    				} 
    				else if (k2y == Double.MAX_VALUE)
    				{
    					expressionYK2Y = 1.0; //in the limit k->+inf
    				}
    				else
    				{
    					k2y = (theta2y / M2y) * EIyL;
    					expressionYK2Y = ( k2y / (0.45 + k2y) );
    				}
    				((AnalysisMemberEC3FrameMemberCase) memberNachweis).setk2y(k2y);
    				
    				double expressionY =  (1.0 +  expressionYK1Y) * (1.0 + expressionYK2Y);
    				double loy = 0.5 * L * Math.sqrt(expressionY);
    				memberNachweis.setBuckLenY(loy);
        			pGuiAnalysis.analysisOutputArea.append("Warning : Buckling length L,y for member " + AnalysisOptions.selectedStrLine + " calculated internally !\n");

    				
    				//-----------------------------------
    				//buckl. length lo,z
    				//-----------------------------------
       				double expressioZK1Z;
    				double expressionZK2Z;
    				
    				if (k1z == 0.1 )
    				{
    					expressioZK1Z = ( k1z / (0.45 + k1z) );
    				} 
    				else if (k1z == Double.MAX_VALUE)
    				{
    					expressioZK1Z = 1.0; //in the limit k->+inf
    				}
    				else
    				{
        				k1z = (theta1z / M1z) * EIzL;
        				expressioZK1Z = ( k1z / (0.45 + k1z) );
    				}
    				((AnalysisMemberEC3FrameMemberCase) memberNachweis).setk1z(k1z);

    				
    				
    				if (k2z == 0.1 )
    				{
    					expressionZK2Z = ( k2z / (0.45 + k2z) );
    				} 
    				else if (k2z == Double.MAX_VALUE)
    				{
    					expressionZK2Z = 1.0; //in the limit k->+inf
    				}
    				else
    				{
    					k2z = (theta2z / M2z) * EIzL;
    					expressionZK2Z = ( k2z / (0.45 + k2z) );
    				}
    				((AnalysisMemberEC3FrameMemberCase) memberNachweis).setk2z(k2z);
    				
    				double expressionZ =  (1.0 +  expressioZK1Z) * (1.0 + expressionZK2Z);
    				double loz = 0.5 * L * Math.sqrt(expressionZ);
    				memberNachweis.setBuckLenZ(loz);
        			pGuiAnalysis.analysisOutputArea.append("Warning : Buckling length L,z for member " + AnalysisOptions.selectedStrLine + " calculated internally !\n");

    			}
    			else if (GuiAnalysisParams.selectedSwayType == eSwayType.SWAY.ordinal()) //nicht aussteifende Bauteile 5.16
    			{
    				//-----------------------------------
    				//buckl. length lo,y
    				//-----------------------------------
    				double expressionYK1Y = 0.0;
    				double expressionYK2Y = 0.0;
    				
    				if (k1y == 0.1 )
    				{
    					expressionYK1Y  = ( k1y / (1.0 + k1y) );
    				} 
    				else if (k1y == Double.MAX_VALUE)
    				{
    					expressionYK1Y  = 1.0; //in the limit k->+inf
    				}
    				else
    				{
        				k1y = (theta1y / M1y) * EIyL;
    					expressionYK1Y  = ( k1y / (1.0 + k1y) );
    				}
    				((AnalysisMemberEC3FrameMemberCase) memberNachweis).setk1y(k1y);

    				
    				if (k2y == 0.1 )
    				{
    					expressionYK2Y = ( k2y / (1.0 + k2y) );
    				} 
    				else if (k2y == Double.MAX_VALUE)
    				{
    					expressionYK2Y = 1.0; //in the limit k->+inf
    				}
    				else
    				{
    					k2y = (theta2y / M2y) * EIyL;
    					expressionYK2Y = ( k2y / (1.0 + k2y) );
    				}
    				((AnalysisMemberEC3FrameMemberCase) memberNachweis).setk2y(k2y);
    				
    				
    				double expressionYKY_ = 0.0;
    				if (k1y == 0.1 && k2y == 0.1)
    				{
    					expressionYKY_  = 10.0 * ( ( k1y * k2y ) / (k1y + k2y) );
    				} 
    				else if (k1y == Double.MAX_VALUE && k2y == Double.MAX_VALUE)
    				{
    					expressionYKY_  = 1.0; //in the limit k->+inf ???
    				}
    				else if (k1y == Double.MAX_VALUE)
    				{
    					expressionYKY_  = ( 1.0 / (1.0 + (1.0 / k2y)) );
    				}
    				else if (k2y == Double.MAX_VALUE)
    				{
    					expressionYKY_  = ( 1.0 / (1.0 + (1.0 / k1y)) );
    				}
    				
    				double expressionY = Math.max( Math.sqrt(1.0 + expressionYKY_), (1.0 +  expressionYK1Y) * (1.0 + expressionYK2Y) );
    				double loy = L * expressionY;
    				memberNachweis.setBuckLenY(loy);
        			pGuiAnalysis.analysisOutputArea.append("Warning : Buckling length L,y for member " + AnalysisOptions.selectedStrLine + " calculated internally !\n");
        			
    				//-----------------------------------
    				//buckl. length lo,z
    				//-----------------------------------
       				double expressioZK1Z = 0.0;
    				double expressionZK2Z = 0.0;
    				
    				if (k1z == 0.1 )
    				{
    					expressioZK1Z = ( k1z / (1.0 + k1z) );
    				} 
    				else if (k1z == Double.MAX_VALUE)
    				{
    					expressioZK1Z = 1.0; //in the limit k->+inf
    				}
    				else
    				{
        				k1z = (theta1z / M1z) * EIzL;
        				expressioZK1Z = ( k1z / (1.0 + k1z) );
    				}
    				((AnalysisMemberEC3FrameMemberCase) memberNachweis).setk1z(k1z);

    				
    				
    				if (k2z == 0.1 )
    				{
    					expressionZK2Z = ( k2z / (1.0 + k2z) );
    				} 
    				else if (k2z == Double.MAX_VALUE)
    				{
    					expressionZK2Z = 1.0; //in the limit k->+inf
    				}
    				else
    				{
    					k2z = (theta2z / M2z) * EIzL;
    					expressionZK2Z = ( k2z / (1.0 + k2z) );
    				}
    				((AnalysisMemberEC3FrameMemberCase) memberNachweis).setk2z(k2z);
    				
    				
    				double expressionZKZ_ = 0.0;
    				if (k1z == 0.1 && k2z == 0.1)
    				{
    					expressionZKZ_  = 10.0 * ( ( k1z * k2z ) / (k1z + k2z) );
    				} 
    				else if (k1z == Double.MAX_VALUE && k2z == Double.MAX_VALUE)
    				{
    					expressionZKZ_  = 1.0; //in the limit k->+inf ???
    				}
    				else if (k1z == Double.MAX_VALUE)
    				{
    					expressionZKZ_  = ( 1.0 / (1.0 + (1.0 / k2z)) );
    				}
    				else if (k2z == Double.MAX_VALUE)
    				{
    					expressionZKZ_  = ( 1.0 / (1.0 + (1.0 / k1z)) );
    				}
    								
    				double expressionZ = Math.max( Math.sqrt(1.0 + expressionZKZ_), (1.0 +  expressioZK1Z) * (1.0 + expressionZK2Z) );
    				double loz = L * expressionZ;
    				memberNachweis.setBuckLenZ(loz);
        			pGuiAnalysis.analysisOutputArea.append("Warning : Buckling length L,z for member " + AnalysisOptions.selectedStrLine + " calculated internally !\n");

    			}
    		}


    		//set forces and moments
    		if (hmLoadCaseN01ed.get(iLc) != null && hmLoadCaseN02ed.get(iLc) != null)
    		{
    			memberNachweis.setNedI(0.5 * (hmLoadCaseN01ed.get(iLc).doubleValue() + hmLoadCaseN02ed.get(iLc).doubleValue()) ); //make N constant
    		}
    		if (Math.abs(memberNachweis.getNedI()) < 1.0E-3 ) //N,ed < 1N ????
    		{
    			pGuiAnalysis.analysisOutputArea.append("Warning : First Order Normal Force N,ed = " + memberNachweis.getNedI() + " [kN] " + //
    					" in Load Case " +  iLc + " is improper for slenderness estimation. Please check that!\n");
    		}

    		memberNachweis.setMoy1I(hmLoadCaseM01yed.get(iLc).doubleValue());
    		memberNachweis.setMoz1I(hmLoadCaseM01zed.get(iLc).doubleValue());
    		memberNachweis.setMoy2I(hmLoadCaseM02yed.get(iLc).doubleValue());
    		memberNachweis.setMoz2I(hmLoadCaseM02zed.get(iLc).doubleValue());

    		//set uncracked cross-sectional params
    		memberNachweis.setDy(Dy);
    		memberNachweis.setDz(Dz);
    		memberNachweis.setfck(fck);
    		memberNachweis.setfydToEs(fydToEs);
    		memberNachweis.setAsfyd(Asfyd);
    		memberNachweis.setAcfcd(Acfcd);
    		memberNachweis.setArea_uncracked(A_uncracked);
    		memberNachweis.setIyy_uncracked(Iyy_uncracked);
    		memberNachweis.setIzz_uncracked(Izz_uncracked);

    		//PARAMETER A
    		double A = 1.0 / (1.0 + 0.2 * GuiAnalysisParams.creepFactor); //if not set creepFactor by user, this will yield  A=0.7
    		memberNachweis.setfi_eff(GuiAnalysisParams.creepFactor);
    		memberNachweis.setA(A);
    		double w = Asfyd / Acfcd; //we already made sure Acfd > 0 above!
    		memberNachweis.setw(w);

    		//PARAMETER B
    		double B = 0.0;
    		if (w > 0.0) 
    		{
    			B = Math.sqrt(1.0 + 2.0*w);
    		}
    		else
    		{
    			B = 1.1;
    		}
    		memberNachweis.setB(B);

    		//PARAMETER N
    		double n = memberNachweis.getNedI() / Acfcd; //we already made sure Acfd > 0 above!
    		memberNachweis.setn(n);

    		//PARAMETER rMY & rMZ & Cy and Cz
    		double rmY = 0.0;
    		if (Math.max(Math.abs(memberNachweis.getMoy1I()), Math.abs(memberNachweis.getMoy2I())) > 1.0E-5)
    		{
    			rmY = Math.min(Math.abs(memberNachweis.getMoy1I()), Math.abs(memberNachweis.getMoy2I())) / Math.max(Math.abs(memberNachweis.getMoy1I()), Math.abs(memberNachweis.getMoy2I()));
    			if ((Math.abs(rmY)>1.0E-8) && !(memberNachweis.getMoy1I() > 0.0 && memberNachweis.getMoy2I() > 0.0))  //only if rmY is not near 0, check for signs
    			{
    				rmY *= -1;
    			}
    		}
    		else  //both are very small numbers => rmy = 0
    		{
    			rmY = 0.0;
    		}

    		double rmZ = 0.0;
    		if (Math.max(Math.abs(memberNachweis.getMoz1I()), Math.abs(memberNachweis.getMoz2I())) > 1.0E-5)
    		{
    			rmZ = Math.min(Math.abs(memberNachweis.getMoz1I()), Math.abs(memberNachweis.getMoz2I())) / Math.max(Math.abs(memberNachweis.getMoz1I()), Math.abs(memberNachweis.getMoz2I()));
    			if ((Math.abs(rmZ)>1.0E-8) && !(memberNachweis.getMoz1I() > 0.0 && memberNachweis.getMoz2I() > 0.0)) //only if rmZ is not near 0, check for signs
    			{
    				rmY *= -1;
    			}
    		}
    		else  //both are very small numbers => rmz = 0
    		{
    			rmZ = 0.0;
    		}


    		memberNachweis.setrmY(rmY);
    		memberNachweis.setrmZ(rmZ);

    		double Cy = 1.7 - rmY;
    		if (Cy < 0) Cy = 0.7;
    		double Cz = 1.7 - rmZ;
    		if (Cz < 0) Cz = 0.7;
    		memberNachweis.setSwayType(GuiAnalysisParams.selectedSwayType);
    		if (GuiAnalysisParams.selectedSwayType == eSwayType.SWAY.ordinal()) //extra clause for sway members
    		{
    			pGuiAnalysis.analysisOutputArea.append("Factors C,y and C,z corrected for sway members!\n");
    			Cy = 0.7; Cz = 0.7;
    		}
    		memberNachweis.setCy(Cy);
    		memberNachweis.setCz(Cz);

    		//PARAMETER LAMBDA LIM Y,Z
    		double lambdaLimY = 0.0;
    		if (n > +1.0E-5)
    		{
    			lambdaLimY = (20.0 * A * B * Cy) / Math.sqrt(n);
    		}
    		memberNachweis.setLambdaLimy(lambdaLimY);

    		double lambdaLimZ = 0.0;
    		if (n > +1.0E-5)
    		{
    			lambdaLimZ = (20.0 * A * B * Cz) / Math.sqrt(n);
    		}
    		memberNachweis.setLambdaLimz(lambdaLimZ);


    		//get the limiting buckling lengths from national annex, MUST HAVE CALCULATED n = Ned/Acfcd
    		if (GuiAnalysisParams.selectedNationalAnnex == eNationalAnnex.DIN_EN_1992_NA.ordinal())
    		{
    			if (Math.abs(n) >= 0.41) //λlim = 25 für |n| ≥ 0,41 		(5.13.aDE)
    			{
    				memberNachweis.setLambdaLimy(25.0);
    				memberNachweis.setLambdaLimz(25.0);
    			}
    			else 					//λlim = 16 / √n für |n| < 0,41 (5.13.bDE)
    			{
    				if (n > +1.0E-5)
    				{
    					memberNachweis.setLambdaLimy(16.0/Math.sqrt(n));
    					memberNachweis.setLambdaLimz(16.0/Math.sqrt(n));
    				}
    				else
    				{
    					memberNachweis.setLambdaLimy(16.0); //???
    					memberNachweis.setLambdaLimz(16.0); //???							
    				}
    			}
    			pGuiAnalysis.analysisOutputArea.append("Warning : limiting buckling slenderness was modified as per DIN EN 1992 NA!\n");
    		}
    		else if (GuiAnalysisParams.selectedNationalAnnex == eNationalAnnex.BS_EN_1992_NA.ordinal())
    		{
    			//λlim = 20.A.B.C/sqrt(n) using the recommended values for A,B,C
    			// nothing to do here, i.e. no modificaiton of LambdaLimy or LambdaLimz
    		}

    		
    		/////////////////////// COMPUTE MAX. SECTIONAL MOMENT ///////////////////////////////
    		
			//-------------------------
			//FIRST ORDER MOMENTS M0eY = 0.6.Moy2_ + 0.4.Moy1_ >= 0.4.Moy2_
			double M0eY = 0.0;	

			// |Moy2_| > |Moy1_|
			double Moy1_ = 0.0;
			double Moy2_ = 0.0;
			if (Math.abs(memberNachweis.getMoy1I()) > Math.abs(memberNachweis.getMoy2I()))
			{
				Moy2_ = Math.abs(memberNachweis.getMoy1I()) * Math.signum(memberNachweis.getMoy1I()); //max
				Moy1_ = Math.abs(memberNachweis.getMoy2I()) * Math.signum(memberNachweis.getMoy2I()); //min
			}
			else
			{
				Moy1_ = Math.abs(memberNachweis.getMoy1I()) * Math.signum(memberNachweis.getMoy1I()); //min
				Moy2_ = Math.abs(memberNachweis.getMoy2I()) * Math.signum(memberNachweis.getMoy2I()); //max
			}
			
			
			if (Math.signum(memberNachweis.getMoy1I())*Math.signum(memberNachweis.getMoy2I()) > 0.0) //if both same sign, use abs
			{
				M0eY = 0.6 * Math.abs(Moy2_) +  0.4 * Math.abs(Moy1_);
				M0eY  = Math.max(M0eY , 0.4 * Math.abs(Moy2_));
			}
			else //consider signs
			{
				M0eY = 0.6 * Moy2_ +  0.4 * Moy1_;
				M0eY  = Math.max(M0eY , 0.4 * Moy2_);
			}
			
			//-------------------------
			//FIRST ORDER MOMENTS M0eZ = 0.6.Moz2_ + 0.4.Moz1_ >= 0.4.Moz2_
			double M0eZ = 0.0;
			
			
			// |Moz2_| > |Moz1_|
			double Moz1_ = 0.0;
			double Moz2_ = 0.0;
			if (Math.abs(memberNachweis.getMoz1I()) > Math.abs(memberNachweis.getMoz2I()))
			{
				Moz2_ = Math.abs(memberNachweis.getMoz1I()) * Math.signum(memberNachweis.getMoz1I()); //max
				Moz1_ = Math.abs(memberNachweis.getMoz2I()) * Math.signum(memberNachweis.getMoz2I()); //min
			}
			else
			{
				Moz1_ = Math.abs(memberNachweis.getMoz1I()) * Math.signum(memberNachweis.getMoz1I()); //min
				Moz2_ = Math.abs(memberNachweis.getMoz2I()) * Math.signum(memberNachweis.getMoz2I()); //max
			}
			
			
			if (Math.signum(memberNachweis.getMoz1I())*Math.signum(memberNachweis.getMoz2I()) > 0.0) //if both same sign, use abs
			{
				M0eZ = 0.6 * Math.abs(Moz2_) +  0.4 * Math.abs(Moz1_);
				M0eZ  = Math.max(M0eZ , 0.4 * Math.abs(Moz2_));
			}
			else //consider signs
			{
				M0eZ = 0.6 * Moz2_ +  0.4 * Moz1_;
				M0eZ  = Math.max(M0eZ , 0.4 * Moz2_);
			}

			//set total moment to this values which is true only for 1st order with no II order effects
			memberNachweis.setMedY(M0eY);
			memberNachweis.setMedY(M0eZ);
			
			//collect max values
			AnalysisMemberEC3Analysis.maximumMyedI = Math.max(AnalysisMemberEC3Analysis.maximumMyedI,Math.abs(memberNachweis.getMedY()));
			AnalysisMemberEC3Analysis.maximumMzedI = Math.max(AnalysisMemberEC3Analysis.maximumMzedI,Math.abs(memberNachweis.getMedZ()));

    		/////////////////////// COMPUTE BUCKLING SLENDERNESSES //////////////////////////////

    		double iY = Math.sqrt(memberNachweis.getIyy_uncracked() / memberNachweis.getArea_uncracked());
    		memberNachweis.setLambdaY(memberNachweis.getBuckLenY() / iY);

    		double iZ = Math.sqrt(memberNachweis.getIzz_uncracked() / memberNachweis.getArea_uncracked());
    		memberNachweis.setLambdaZ(memberNachweis.getBuckLenZ() / iZ);		


    		if ( (memberNachweis.getLambdaLimy() > memberNachweis.getLambdaY()) && (memberNachweis.getLambdaLimz() > memberNachweis.getLambdaZ()) ) // NO II ORDER EFFECTS
    		{
    			memberNachweis.setRequiresNCMAnalysis(false);
    			pGuiAnalysis.analysisOutputArea.append("STABILITY CHECK OK FOR I ORDER !\nII ORDER EFFECTS SHALL NOT BE INCLUDED!\n");
    			pGuiAnalysis.analysisOutputArea.append("\n");
        		//add to hash map
        		if (AnalysisMemberEC3Analysis.hmMembers == null) AnalysisMemberEC3Analysis.hmMembers = new HashMap<Integer,AnalysisMemberEC3MemberCase>();
        		AnalysisMemberEC3Analysis.hmMembers.put(iLc, memberNachweis);
    		}
    		else // NEED TO CONSIDER II ORDER EFFECTS
    		{
    			memberNachweis.setRequiresNCMAnalysis(true);
    			pGuiAnalysis.analysisOutputArea.append("STABILITY CHECK VIOLATED FOR I ORDER!\nII ORDER EFFECTS WILL BE INCLUDED!\n");
    			pGuiAnalysis.analysisOutputArea.append("\n");
        		//add to hash map
        		if (AnalysisMemberEC3Analysis.hmMembers == null) AnalysisMemberEC3Analysis.hmMembers = new HashMap<Integer,AnalysisMemberEC3MemberCase>();
        		AnalysisMemberEC3Analysis.hmMembers.put(iLc, memberNachweis);
    			
    			//analyse II order effects here!
    			AnalysisMemberEC3Analysis.analyseIIOrderEffects(iLc);
    		}
    		
    	}
    	
		
		
		//NOW COMPARE THE ALREADY OBTAINED ENVELOPE ON THE BASIS OF I ORDER WITH EACH II LOAD CASE
    	if (AnalysisOptions.selectedNoneLinearLoadCases != null)
    	{
    		if (AnalysisOptions.selectedNoneLinearLoadCases.isEmpty()) pGuiAnalysis.analysisOutputArea.append("No II order load cases selected to be compared!\n");
    		
    		for ( Integer iLcII : AnalysisOptions.selectedNoneLinearLoadCases)
    		{
    			AnalysisMemberEC3Analysis.compareIIOrderLcToMaxEnvelope(iLcII);
    		}
    	}
		
		
		//if all is ok, proceed....
		AnalysisMemberEC3Analysis.analysisState = AnalysisState.ANALYSIS_READY;
	}
	
	
	private static void analyseIIOrderEffects(int iLc)
	{
		
		AnalysisMemberEC3MemberCase EC3Nachweis = AnalysisMemberEC3Analysis.hmMembers.get(iLc);
		
		if (EC3Nachweis.getRequiresNCMAnalysis())
		{
			
			double nu = 1 + EC3Nachweis.getw();
			double n = EC3Nachweis.getn();
			double nbal = 0.4; //balance point
			pGuiAnalysis.analysisOutputArea.append("Balance point of n,bal = 0.4 used!\n");

			double cY = Math.pow(Math.PI,2.0); //corresponds to a sinus form for the curvature. If r= const, use 8; true ONLY if PRISMATIC SECTION
			double cZ = cY;
			
			if (Math.abs(EC3Nachweis.getMoy1I()-EC3Nachweis.getMoy2I()) < 1.0E-5  &&   (Math.signum(EC3Nachweis.getMoy1I())*Math.signum(EC3Nachweis.getMoy2I())) > 0.0)
			{
				pGuiAnalysis.analysisOutputArea.append("Curvature factor c about y-y axis corrected!\n");
				cY = 8.0; //const curvature
			}
			
			if (Math.abs(EC3Nachweis.getMoz1I()-EC3Nachweis.getMoz2I()) < 1.0E-5  &&   (Math.signum(EC3Nachweis.getMoz1I())*Math.signum(EC3Nachweis.getMoz2I())) > 0.0)
			{
				pGuiAnalysis.analysisOutputArea.append("Curvature factor c about z-z axis corrected!\n");
				cZ = 8.0; //const curvature
			}

			double Kr  = 0.0 ;
			if (Math.abs(nu - nbal) > 1.0E-6) Kr = ( nu - n ) / ( nu - nbal );

			double betaY = 0.35 + EC3Nachweis.getfck()/200.0 - EC3Nachweis.getLambdaY() / 150.0;
			if (betaY < 1.0) betaY = 1.0;

			double betaZ = 0.35 + EC3Nachweis.getfck()/200.0 - EC3Nachweis.getLambdaZ() / 150.0;
			if (betaZ < 1.0) betaZ = 1.0;

			double KfiY = 1.0 + betaY * GuiAnalysisParams.creepFactor;

			double KfiZ = 1.0 + betaZ * GuiAnalysisParams.creepFactor;
			
			double eps_yd = EC3Nachweis.getfydToEs();

			double r0Y_inv = eps_yd / 0.45 * EC3Nachweis.getDy(); // BEWARE: special condition for asymmetric reinf. section
			double r0Z_inv = eps_yd / 0.45 * EC3Nachweis.getDz();

			double rY_inv = Kr * KfiY * r0Y_inv;
			double rZ_inv = Kr * KfiZ * r0Z_inv;
			
			double e2Y = rY_inv * Math.pow(EC3Nachweis.getBuckLenY(),2.0) / cY;
			double e2Z = rZ_inv * Math.pow(EC3Nachweis.getBuckLenZ(),2.0) / cZ;
			
			//ADDITIONAL IMPERFECTION MOMENTS = Ned*e,i,y and z
			double M0ImpY = EC3Nachweis.getAcfcd() * EC3Nachweis.getn() * GuiAnalysisParams.imperfectionY * 1E-3; //mm to m
			double M0ImpZ = EC3Nachweis.getAcfcd() * EC3Nachweis.getn() * GuiAnalysisParams.imperfectionZ * 1E-3; //mm to m
			
			// SECOND ORDER MOMENTE = Ned.e2,y and z
			double M2Y = EC3Nachweis.getAcfcd() * EC3Nachweis.getn() * e2Y; //second order curvature effect about y-y
			double M2Z = EC3Nachweis.getAcfcd() * EC3Nachweis.getn() * e2Z; //second order curvature effect about z-z
			
			//add to first order effects also the ones from second order and imperfections
			double MedY = EC3Nachweis.getMedY() + M0ImpY + M2Y;
			double MedZ = EC3Nachweis.getMedZ() + M0ImpZ + M2Z;
			
			EC3Nachweis.setMedY(MedY);
			EC3Nachweis.setMedY(MedZ);
			
			
			//collect max values
			AnalysisMemberEC3Analysis.maximumMyedI = Math.max(AnalysisMemberEC3Analysis.maximumMyedI,Math.abs(EC3Nachweis.getMedY()));
			AnalysisMemberEC3Analysis.maximumMzedI = Math.max(AnalysisMemberEC3Analysis.maximumMzedI,Math.abs(EC3Nachweis.getMedZ()));
			
			
			//add back to hashMap with the extended params
			AnalysisMemberEC3Analysis.hmMembers.put(iLc, EC3Nachweis);

		}
		
	}
	
	private static void compareIIOrderLcToMaxEnvelope(int iLcII)
	{
		//to be implemented....
	
	}
	
	public static boolean checkForZeroForcesAndDispl(final AnalysisForcesAndDisplElementBeam X)
	{
		//tollerance on forces and rotations
		double eps = 1.0E-10;
		
		//check for totally unloaded member
		if (Math.abs(X.m_n) < eps && Math.abs(X.m_vy) < eps && Math.abs(X.m_vz) < eps && //
			Math.abs(X.m_mt2) < eps && Math.abs(X.m_my) < eps && Math.abs(X.m_mz) < eps && Math.abs(X.m_mt) < eps && Math.abs(X.m_mb) < eps )
		{
			return true;
		}
		return false;
	}
	
	public static boolean isCompressionMember(final AnalysisForcesAndDisplElementBeam X)
	{
		//tollerance on forces and moments
		double eps = 1.0E-10;
		
		//check for pure compression
		if (Math.abs(X.m_n) > eps && Math.abs(X.m_vy) < eps && Math.abs(X.m_vz) < eps && //
			Math.abs(X.m_mt2) < eps && Math.abs(X.m_my) < eps && Math.abs(X.m_mz) < eps && Math.abs(X.m_mt) < eps && Math.abs(X.m_mb) < eps )
		{
			return true;
		}
		return false;
	}
	
}
