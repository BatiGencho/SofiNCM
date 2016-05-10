package sofistik;

public class AnalysisMaterial implements Comparable<AnalysisMaterial>  //Steel,Beton,Reinf
{

	int m_id;
	int m_type;
	int m_class;
	String m_title;
	
	//conc+steel
	float m_emod;
	float m_mue;
	float m_gmod;
	float m_kmod;
	float m_gam;
	float m_rho;
	float m_alfa;
	float m_e90;
	float m_mue90;
	float m_alf;
	float m_bet;	
	float m_scm;
	
	//steel
	float m_fy;
	float m_ft;
	float m_eps;	
	float m_rel1;	
	float m_rel2;
	float m_r;
	float m_k1;
	float m_eh;
	float m_fe;
	float m_epse;
	float m_fdyn;
	float m_fyc;
	float m_ftc;
	float m_tmax;
	float m_bc;
	
	//concrete
	float m_fc;
	float m_fck;	
	float m_ftm;	
	float m_ftl;
	float m_ftk;
	float m_ec;	
	float m_et;
	float m_muer;
	float m_fcm;
	float m_rdcl;
	float m_fcr;
	float m_ecr;
	float m_fbd;
	float m_ftd;	
	float m_feqr;
	float m_feqt;	
	float m_fcfat;
	
	//conc + steel
	float m_scms;
	float m_scmu;	
	float m_scmc;

	
	AnalysisMaterial(int id) {

		this.m_id = id;
		this.m_type = -999;
		this.m_class = -999;
		this.m_title = null;

		this.m_emod = -999; //steel + concrete
		this.m_mue = -999; //steel + concrete
		this.m_gmod = -999; //steel + concrete
		this.m_kmod = -999; //steel + concrete
		this.m_gam = -999; //steel + concrete
		this.m_rho = -999; //steel + concrete
		this.m_alfa = -999; //steel + concrete
		this.m_e90 = -999; //steel + concrete
		this.m_mue90 = -999; //steel + concrete
		this.m_alf = -999; //steel + concrete
		this.m_bet = -999; //steel + concrete
		this.m_scm = -999; //steel + concrete
		
		this.m_fy = -999; //steel
		this.m_ft = -999; //steel		
		this.m_fy = -999; //steel
		this.m_ft = -999; //steel
		this.m_eps = -999; //steel
		this.m_rel1 = -999; //steel
		this.m_rel2 = -999; //steel
		this.m_r = -999; //steel
		this.m_k1 = -999; //steel
		this.m_eh = -999; //steel
		this.m_fe = -999; //steel
		this.m_epse = -999; //steel
		this.m_fdyn = -999; //steel
		this.m_fyc = -999; //steel
		this.m_ftc = -999; //steel
		this.m_tmax = -999; //steel
		this.m_bc = -999; //steel

		this.m_fc = -999; //concrete
		this.m_fck = -999;//concrete
		this.m_ftm = -999;//concrete
		this.m_ftl = -999;//concrete
		this.m_ftk = -999;//concrete
		this.m_ec = -999;//concrete
		this.m_et = -999;//concrete
		this.m_muer = -999;//concrete
		this.m_fcm = -999;//concrete
		this.m_rdcl = -999;//concrete
		this.m_fcr = -999;//concrete
		this.m_ecr = -999;//concrete
		this.m_fbd = -999;//concrete
		this.m_ftd = -999;//concrete
		this.m_feqr = -999;//concrete
		this.m_feqt = -999;//concrete
		this.m_fcfat = -999;//concrete
		
		this.m_scms = -999;//steel + concrete
		this.m_scmu = -999;//steel + concrete
		this.m_scmc = -999;//steel + concrete	
	}
	
	@Override
	public int compareTo(AnalysisMaterial o) {
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

		return new String("Mat ID : " + this.m_id + " Mat Type : " + this.m_type + " Mat Class : " + this.m_class + " Mat Title : " + this.m_title);
	}
	
	@Override
	public int hashCode() {
		return this.m_id;
	}
	
	public boolean equals(AnalysisMaterial o) {
		if (this.hashCode()==o.hashCode()) return true;
		return false;
	}
	
	
	//native methods
	native void getDataForMaterial();
}
