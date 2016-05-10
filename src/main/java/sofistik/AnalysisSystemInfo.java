package sofistik;

public class AnalysisSystemInfo {

	//at package level
	int m_iprob;
	int m_iachs;
	int m_nknot;
	int m_mknot;
	int m_igdiv;
	int m_igres;
	String m_text;
	
	AnalysisSystemInfo() {
		
		this.m_iprob = -1;
		this.m_iachs = -1;
		this.m_nknot = -1;
		this.m_mknot = -1;
		this.m_igres = -1;
		this.m_igres = -1;
		this.m_text = new String(); //needed because of JNI
	}
	
	//native method to fill with a given data, node must only have id
	native void getDataForSystemInfo();

}