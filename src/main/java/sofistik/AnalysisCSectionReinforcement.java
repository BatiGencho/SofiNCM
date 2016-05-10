package sofistik;

public class AnalysisCSectionReinforcement
{
	
	int m_id;    					 /*        Identification 200 */ //==CONST.
	int m_mno;					 	 /*        Material number */
	int m_ir; 					     /*        Layer number */

	
	float   m_y;                     /* [1011] y-ordinate */
	float   m_z;                     /* [1011] z-ordinate */
	float   m_as;                    /* [1020] Reinforcement area */
	float   m_asma;                  /* [1020] max Reinforcement area */
	float   m_rng;                   /*        Layer number */
	float   m_d;                     /* [1023] Diameter */
	float   m_aref;                  /* [1012] Reference area */
	float   m_a;                     /* [1011] distance of bars */
	
	String m_tnr; 					 /*        Designation */
	
	public AnalysisCSectionReinforcement()
	{
		this.m_id=200;
		this.m_mno=-1;
		this.m_ir=-1;
		this.m_tnr=new String("");
		
		this.m_y=-1f;
		this.m_z = -1f;
		this.m_as = -1f;
		this.m_asma = -1f;
		this.m_rng = -1f;
		this.m_d = -1f;
		this.m_aref = -1f;
		this.m_aref = -1f;		
		this.m_a = -1f;
	}

}
