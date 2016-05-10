package sofistik;

public class AnalysisForcesAndDisplElementNode
{
	//class being used by node to store NODAL forces and displacements
	
	
//	float   m_ux;                    /* [1003] displacement X */
//	float   m_uy;                    /* [1003] displacement Y */
//	float   m_uz;                    /* [1003] displacement Z */
//	float   m_urx;                   /* [1004] rotation X */
//	float   m_ury;                   /* [1004] rotation Y */
//	float   m_urz;                   /* [1004] rotation Z */
//	float   m_urb;                   /* [1005] twisting */
//	float   m_px;                    /* [1151] nodal support X */
//	float   m_py;                    /* [1151] nodal support Y */
//	float   m_pz;                    /* [1151] nodal support Z */
//	float   m_mx;                    /* [1152] support moment X */
//	float   m_my;                    /* [1152] support moment Y */
//	float   m_mz;                    /* [1152] support moment Z */
//	float   m_mb;                    /* [1105] warping moment */
	
	//displ.
	float m_ux;
	float m_uy;
	float m_uz;
	//rotations
	float m_urx;
	float m_ury;
	float m_urz;
	float m_urb;
	//forces
	float m_nr;
	float m_px;
	float m_py;
	float m_pz;
	float m_mx;
	float m_my;
	float m_mz;
	float m_mb;
	
	AnalysisForcesAndDisplElementNode()
	{
		this.m_ux = 0f;
		this.m_uy = 0f;
		this.m_uz = 0f;
		
		this.m_urx = 0f;
		this.m_ury = 0f;
		this.m_urz = 0f;
		this.m_urb = 0f;
		
		this.m_nr = 0f;
		this.m_px = 0f;
		this.m_py = 0f;
		this.m_pz = 0f;
		this.m_mx = 0f;
		this.m_my = 0f;
		this.m_mz = 0f;
		this.m_mb = 0f;
	}

}
