package sofistik;

//own packages
import sofistik.AnalysisMemberSubElement;

public class AnalysisMember implements Comparable<AnalysisMember>
{
	//locals at package level as other classes from package sofistik need to use them
	int id_intern;
	int id_Cdb;
	int nGroup;
	int nSubBeams;
	double mlength;
	//member title
	String m_strLineTitle;
	//overview of load cases for the member
	int [] mLoadCases;
	int [] mLoadCasesIntOrder;
	String [] mLoadCasesTitle;
	String [] mLoadCasesTheoryOrder;
	
	public AnalysisMemberSubElement[] arrSubElements; //maybe better to use a HashMap??? for quick finding
	
	//native methods for accessing data, needs a consecutive Cdb Index
	public native void initMemberData(int iCdbIndex); // init  datas
	private native AnalysisMemberSubElement[] fillSubElemsWithData(); //any sort of data, provided we have already alloc the subelems data structures

	public void initSubElementsData()
	{
		//now let the native fill the arrSubElements with data
		this.arrSubElements = fillSubElemsWithData();
	}
	
	public String getTitleForActiveLoadCase(int lcIndex)
	{
		// mLoadCases and mLoadCasesTitle will be always initialised to something even if the JNI fails
		for (int j=0 ; j< this.mLoadCases.length; j++)
		{
			if (this.mLoadCases[j]==lcIndex) return this.mLoadCasesTitle[j]; //title of this lc
		}
		return null; //no title for requested lcIndex
	}
	
	public String getLoadCaseTheoryForActiveLoadCase(int lcIndex)
	{
		for (int j=0 ; j< this.mLoadCases.length; j++)
		{
			if (this.mLoadCases[j]==lcIndex) return this.mLoadCasesTheoryOrder[j]; //theory+description of this lc
		}
		return null; //no title for requested lcIndex
	}
	
	public int getLoadCaseIntOrderForActiveLoadCase(int lcIndex)
	{
		for (int j=0 ; j< this.mLoadCasesIntOrder.length; j++)
		{
			if (this.mLoadCases[j]==lcIndex) return this.mLoadCasesIntOrder[j]; //int I,II or III of this lc
		}
		return -1; //no order for requested lcIndex
	}

	//standard constructor
	public AnalysisMember()
	{
		this.id_intern = -1;
		this.id_Cdb = -1;
		this.nGroup = -1;
		this.nSubBeams = -1;
		this.m_strLineTitle = new String("");  //need to init to smthg because of JNI
		this.mLoadCases = new int[1]; 		  //need to init to smthg because of JNI
		this.mLoadCasesIntOrder = new int[1]; 		  //need to init to smthg because of JNI
		this.mLoadCasesTitle = new String[1]; //need to init to smthg because of JNI
		this.mLoadCasesTheoryOrder = new String[1];  //need to init to smthg because of JNI	
		this.mlength = 0.0;
	}
	
	//getters
	public int getIdIntern() {
		return this.id_intern;
	}
	
	public int getIdCdb() {
		return this.id_Cdb;
	}
	
	public int getIdGroup() {
		return this.nGroup;
	}
	
	public int getnSubElems() {
		return this.nSubBeams;
	}
	
	public String getStrLineTitle() {
		return this.m_strLineTitle;
	}
	
	public double getStrLineLength() {
		return this.mlength;
	}

	@Override
	public int compareTo(AnalysisMember o) {
		if (this.hashCode()>=o.hashCode()) {
			return 1;
		}
		else if (this.hashCode()==o.hashCode()) {
			return -1;
		}
		else {
			return 0;			
		}
	}
	
	@Override
	public String toString() {

		return new String("intern ID : " + this.id_intern + "cdb ID : " + this.id_Cdb);
	}
	
	@Override
	public int hashCode() {
		return this.id_intern;
	}
	
	public boolean equals(AnalysisMember o) {
		if (this.hashCode()==o.hashCode()) return true;
		return false;
	}
	
}
