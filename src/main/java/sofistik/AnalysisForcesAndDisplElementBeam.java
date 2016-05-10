package sofistik;

public class AnalysisForcesAndDisplElementBeam
{

	//class being used by a beam to store forces and displacements
	
	
//************* CDB DESCRIPTION **************** //
//	float   m_x;                     /* [1001] max. beam length */
//	float   m_n;                     /* [1101] normal force */
//	float   m_vy;                    /* [1102] y-shear force */
//	float   m_vz;                    /* [1102] z-shear force */
//	float   m_mt;                    /* [1103] torsional moment */
//	float   m_my;                    /* [1104] bending moment My */
//	float   m_mz;                    /* [1104] bending moment Mz */
//	float   m_mb;                    /* [1105] warping moment Mb */
//	float   m_mt2;                   /* [1103] 2nd torsionalmom. */
//	float   m_ux;                    /* [1003] diplacem. local x */
//	float   m_uy;                    /* [1003] diplacem. local y */
//	float   m_uz;                    /* [1003] diplacem. local z */
//	float   m_phix;                  /* [1004] rotation local x */
//	float   m_phiy;                  /* [1004] rotation local y */
//	float   m_phiz;                  /* [1004] rotation local z */
//	float   m_phiw;                  /* [1005] twisting */
//	float   m_mt3;                   /* [1103] 3rd torsionalmom */
//	float   m_pa;                    /* [1095] axial bedding */
//	float   m_pt;                    /* [1095] transverse bedding */
//	float   m_pty;                   /* [1095] local y component of transverse bedding */
//	float   m_ptz;                   /* [1095] local z component of transverse bedding */
	
	
	float m_x;
	float m_n;
	float m_vy;
	float m_vz;
	
	float m_mt;
	float m_my;
	float m_mz;
	float m_mb;
	float m_mt2;
	
	float m_ux;
	float m_uy;
	float m_uz;
	
	float m_phix;
	float m_phiy;
	float m_phiz;
	float m_phiw;
	
	float m_pa;
	float m_pt;
	float m_pty;
	float m_ptz;	
	
	AnalysisForcesAndDisplElementBeam()
	{
		this.m_x = 0f;
		this.m_n = 0f;
		this.m_vy = 0f;
		this.m_vz = 0f;
		this.m_mt = 0f;
		this.m_my = 0f;
		this.m_mz = 0f;
		this.m_mb = 0f;
		this.m_mt2 = 0f;
		this.m_ux = 0f;
		this.m_uy = 0f;
		this.m_uz = 0f;
		this.m_phix = 0f;
		this.m_phiy = 0f;
		this.m_phiz = 0f;
		this.m_phiw = 0f;
		this.m_pa = 0f;
		this.m_pt = 0f;
		this.m_pty = 0f;
		this.m_ptz = 0f;
	}
		
}
