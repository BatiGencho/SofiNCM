package sofistik;

public class AnalysisNorm {
	
	int m_id;
	int m_dc;
	int m_Country;
	int m_Code;
	int m_Year;
	int m_Class;
	String m_normText; 
	int m_Wind;
	int m_Snow;	
	int m_Seis;
	int m_Dummy;
	float m_Altitude;	
	float m_Version;	
	
	AnalysisNorm() { //constructor
		
		this.m_id = -1;
		this.m_dc = -1;
		this.m_Country = -1;
		this.m_Code = -1;
		this.m_Year = -1;
		this.m_Class = -1;
		this.m_normText = new String(""); 
		this.m_Wind = -1;
		this.m_Snow = -1;
		this.m_Seis = -1;
		this.m_Dummy = -1;
		this.m_Altitude = -1.0f;
		this.m_Version = -1.0f;
	}
	
	//native method
	native void getDataForNorm();
}
