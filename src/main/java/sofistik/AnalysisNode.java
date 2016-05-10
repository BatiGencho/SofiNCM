package sofistik;

import java.util.HashMap;

public class AnalysisNode {

	//at package level
	int m_nr;
	int m_inr;
	int m_kfix;
	int m_ncod;
	public float[] xyz;
	
	HashMap<Integer,AnalysisForcesAndDisplElementNode> hmLoadCaseResults;
	
	AnalysisNode(int nNr) {

		this.m_nr = nNr;
		this.m_inr = -1;
		this.m_kfix = -1;
		this.m_ncod = -1;
		this.xyz = new float[1]; //needed because of JNI
		this.hmLoadCaseResults = new HashMap<Integer,AnalysisForcesAndDisplElementNode>();
	}
	
	//native method to fill a Node with a given data, node must only have id
	native void getDataForNode();
	native AnalysisForcesAndDisplElementNode getResultsForNode(int iLc);

}
