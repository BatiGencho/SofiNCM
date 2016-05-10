package sofistik;

public class AnalysisCSection  implements Comparable<AnalysisCSection>
{
	
	int id;    //id of the section
	int m_mno; //ref.mat number
	int m_mrf; //ref.mat reinf. number
	
	//section - properties for the gross section based on the ref. material
	float m_a;
	float m_ay;
	float m_az;
	float m_it;
	float m_iy;
	float m_iz;
	float m_iyz;
	float m_ys;
	float m_zs;
	float m_ysc;
	float m_zsc;
	float m_em;
	float m_gm;
	float m_gam;
	float m_ymin;
	float m_ymax;
	float m_zmin;
	float m_zmax;
	String m_text;
	
	int m_neffs;     //none eff. parts
	int m_cs;        //const. stages
	int m_cuts;  	 //section cuts
	int m_spt;   	 //str. points
	int m_reinfBars; //reinf bars

	
	//holds the reinf bars
	public AnalysisCSectionReinforcement[] arrSectReinfBars;
	
	AnalysisCSection(int id) {
		this.id = id;
		this.m_a = -1f;
		this.m_ay = -1f;
		this.m_az = -1f;
		this.m_it = -1f;
		this.m_iy = -1f;
		this.m_iz = -1f;
		this.m_iyz = -1f;
		this.m_ys = -1f;
		this.m_zs = -1f;
		this.m_ysc = -1f;
		this.m_zsc = -1f;
		this.m_em = -1f;
		this.m_gm = -1f;
		this.m_gam = -1f;
		this.m_ymin = -1f;
		this.m_ymax = -1f;
		this.m_zmin = -1f;
		this.m_zmax = -1f;
		this.m_text = new String("");
	}
	
	public void initSectionReinf()
	{
		//now let the native fill the reinforcemnts array
		this.arrSectReinfBars = getReinfBars();
	}
	
	@Override
	public int compareTo(AnalysisCSection o) {
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

		return new String("Section ID: " + this.id + " Mat Nr: " + this.m_mno + " Mat Reinf: " + this.m_mrf + " Sect Title [ " + this.m_text + " ] "  );
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
	
	public boolean equals(AnalysisCSection o) {
		if (this.hashCode()==o.hashCode()) return true;
		return false;
	}
	
	//native methods
	native void getDataForSection();
	native AnalysisCSectionReinforcement[] getReinfBars();

}
