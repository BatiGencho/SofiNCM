package sofistik;

import java.util.ArrayList;
import java.util.HashMap;

public class AnalysisMemberSubElement {
	
	//at package level
	public int iSubElemCdbIndex;
	public int nodeId1;
	public int nodeId2;
	public int sectId1;
	public int sectId2;
	public double dL;
	public int[] lcs;
	public int[] mats;
	
	HashMap<Integer,ArrayList<AnalysisForcesAndDisplElementBeam>> hmLoadCaseResults;
	
	public AnalysisMemberSubElement()
	{
		this.iSubElemCdbIndex=-1;
		this.nodeId1=-1;
		this.nodeId2=-1;
		this.sectId1=-1;
		this.sectId2=-1;
		this.dL = -1;
		//some basic init (needed because JNI needs to access it later)
		this.lcs = new int[1]; //all lcs acting on the sub element
		this.mats = new int[1]; //all mats within the subelement	
		this.hmLoadCaseResults = new HashMap<Integer,ArrayList<AnalysisForcesAndDisplElementBeam>>();
	}
	
	//natives
	native AnalysisForcesAndDisplElementBeam getInternalResultsForMemberSubElement(int iLc , int iPos);
}
