package sofistik;

import gui.GuiAnalysisParams;

public class AnalysisMemberEC3MemberCase {
	
	int 	loadCaseNumber;
	boolean isCompressiveOnly; //only for info, shows if N only
	boolean requiresNCMAnalysis;
	double bLy;
	double bLz;
	double A;
	double B;
	double Cy;
	double Cz;
	double lambdaLimy; 	//lambda lim y-y acc. to national annex
	double lambdaLimz; 	//lambda z-z acc. to national annex
	double lambdaY; 	//lambda in y-y
	double lambdaZ; 	//lambda in z-z
	double Ned_Iorder;
	double Moy1_Iorder;
	double Moy2_Iorder;
	double Moz1_Iorder;
	double Moz2_Iorder;
	double Acfcd;
	double Asfyd;
	double fi_eff; 		//creep factor
	double Iyy_uncracked;
	double Izz_uncracked;
	double Area_uncracked;
	double n;
	double rmY;
	double rmZ;
	double w;
	int    swayType;
	double fydToEs;
	double fck;
	double Dy;
	double Dz;
	double MedY;
	double MedZ;
	
	AnalysisMemberEC3MemberCase()
	{
		this.loadCaseNumber = -999;
		this.isCompressiveOnly = true;
		this.requiresNCMAnalysis  = false;
		this.bLy = -999.0;
		this.bLz = -999.0;
		this.A = 0.7;
		this.B = 1.1;
		this.Cy = 0.7;
		this.Cz = 0.7;
		this.lambdaLimy = -999.0;
		this.lambdaLimz = -999.0;
		this.lambdaY = -999.0;
		this.lambdaZ = -999.0;
		this.Ned_Iorder  = -999.0; //only one for the whole member
		this.Moy1_Iorder = -999.0;
		this.Moy2_Iorder = -999.0;
		this.Moz1_Iorder = -999.0;
		this.Moz2_Iorder = -999.0;
		this.Acfcd = -999.0;
		this.Asfyd = -999.0;
		this.fi_eff = -999.0;
		this.Iyy_uncracked = -999.0;
		this.Izz_uncracked = -999.0;
		this.Area_uncracked = -999.0;
		this.n = 0.0;
		this.rmY = 0.0;
		this.rmZ = 0.0;
		this.w = 0.0;
		this.swayType = 0;
		this.fydToEs = -999.0;
		this.fck = -999.0;
		this.Dy = -999.0;
		this.Dz = -999.0;
		this.MedY = -999;
		this.MedZ = -999;
	}
	
	//******************************************************
	
	public void setMedY(double MedY)
	{
		this.MedY = MedY;
	}
	
	public double getMedY()
	{
		return this.MedY;
	}
	
	public void setMedZ(double MedZ)
	{
		this.MedZ = MedZ;
	}
	
	public double getMedZ()
	{
		return this.MedZ;
	}
	
	//******************************************************
	
	public void setDy(double Dy)
	{
		this.Dy = Dy;
	}
	
	public double getDy()
	{
		return this.Dy;
	}
	
	//******************************************************
	
	public void setDz(double Dz)
	{
		this.Dz = Dz;
	}
	
	public double getDz()
	{
		return this.Dz;
	}
	
	//******************************************************
	
	public void setfck(double fck)
	{
		this.fck = fck;
	}
	
	public double getfck()
	{
		return this.fck;
	}
	
	//******************************************************
	
	public void setfydToEs(double fydToEs)
	{
		this.fydToEs = fydToEs;
	}
	
	public double getfydToEs()
	{
		return this.fydToEs;
	}

	//******************************************************
	
	public void setLoadCaseNumber(int loadCaseNumber)
	{
		this.loadCaseNumber = loadCaseNumber;
	}
	
	public int getLoadCaseNumber()
	{
		return this.loadCaseNumber;
	}

	
	//******************************************************
	
	public void setIsCompressiveOnly(boolean isCompressiveOnly)
	{
		this.isCompressiveOnly = isCompressiveOnly;
	}
	
	public boolean getIsCompressiveOnly()
	{
		return this.isCompressiveOnly;
	}
	
	
	//******************************************************
	
	public void setBuckLenY(double bLy)
	{
		this.bLy = bLy;
	}
	
	public double getBuckLenY()
	{
		return this.bLy;
	}	
	
	
	//******************************************************
	
	public void setBuckLenZ(double bLz)
	{
		this.bLz = bLz;
	}
	
	public double getBuckLenZ()
	{
		return this.bLz;
	}	
	
	//******************************************************
	
	public void setA(double A)
	{
		this.A = A;
	}
	
	public double getA()
	{
		return this.A;
	}		

	//******************************************************
	
	public void setB(double B)
	{
		this.B = B;
	}
	
	public double getB()
	{
		return this.B;
	}	
	
	//******************************************************
	
	public void setCy(double Cy)
	{
		this.Cy = Cy;
	}
	
	public double getCy()
	{
		return this.Cy;
	}
	
	public void setCz(double Cz)
	{
		this.Cz = Cz;
	}
	
	public double getCz()
	{
		return this.Cz;
	}
	
	//******************************************************
	
	public void setLambdaLimy(double lambdaLimy)
	{
		this.lambdaLimy = lambdaLimy;
	}
	
	public double getLambdaLimy()
	{
		return this.lambdaLimy;
	}
	
	//******************************************************
	
	public void setLambdaLimz(double lambdaLimz)
	{
		this.lambdaLimz = lambdaLimz;
	}
	
	public double getLambdaLimz()
	{
		return this.lambdaLimz;
	}
	
	//******************************************************
	
	public void setLambdaY(double lambdaY)
	{
		this.lambdaY = lambdaY;
	}
	
	public double getLambdaY()
	{
		return this.lambdaY;
	}
	
	//******************************************************
	
	public void setLambdaZ(double lambdaZ)
	{
		this.lambdaZ = lambdaZ;
	}
	
	public double getLambdaZ()
	{
		return this.lambdaZ;
	}
		
	//******************************************************
	
	public void setNedI(double Ned_Iorder)
	{
		this.Ned_Iorder = Ned_Iorder;
	}
	
	public double getNedI()
	{
		return this.Ned_Iorder;
	}
	
	//******************************************************
	
	public void setMoy1I(double Moy1_Iorder)
	{
		this.Moy1_Iorder = Moy1_Iorder;
	}
	
	public double getMoy1I()
	{
		return this.Moy1_Iorder;
	}
	
	//******************************************************
	
	public void setMoy2I(double Moy2_Iorder)
	{
		this.Moy2_Iorder = Moy2_Iorder;
	}
	
	public double getMoy2I()
	{
		return this.Moy2_Iorder;
	}
	
	//******************************************************
	
	public void setMoz1I(double Moz1_Iorder)
	{
		this.Moz1_Iorder = Moz1_Iorder;
	}
	
	public double getMoz1I()
	{
		return this.Moz1_Iorder;
	}
	
	//******************************************************
	
	public void setMoz2I(double Moz2_Iorder)
	{
		this.Moz2_Iorder = Moz2_Iorder;
	}
	
	public double getMoz2I()
	{
		return this.Moz2_Iorder;
	}
	
	//******************************************************
	
	public void setAcfcd(double Acfcd)
	{
		this.Acfcd = Acfcd;
	}
	
	public double getAcfcd()
	{
		return this.Acfcd;
	}
	
	//******************************************************
	
	public void setAsfyd(double Asfyd)
	{
		this.Asfyd = Asfyd;
	}
	
	public double getAsfyd()
	{
		return this.Asfyd;
	}
	
	//******************************************************
	
	public void setfi_eff(double fi_eff)
	{
		this.fi_eff = fi_eff;
	}
	
	public double getfi_eff()
	{
		return this.fi_eff;
	}
	
	//******************************************************
	
	public void setIyy_uncracked(double Iyy_uncracked)
	{
		this.Iyy_uncracked = Iyy_uncracked;
	}
	
	public double getIyy_uncracked()
	{
		return this.Iyy_uncracked;
	}
	
	//******************************************************
	
	public void setIzz_uncracked(double Izz_uncracked)
	{
		this.Izz_uncracked = Izz_uncracked;
	}
	
	public double getIzz_uncracked()
	{
		return this.Izz_uncracked;
	}
	
	//******************************************************
	
	public void setArea_uncracked(double Area_uncracked)
	{
		this.Area_uncracked = Area_uncracked;
	}
	
	public double getArea_uncracked()
	{
		return this.Area_uncracked;
	}
	
	//******************************************************
	
	public void setn(double n)
	{
		this.n = n;
	}
	
	public double getn()
	{
		return this.n;
	}

	//******************************************************
	
	public void setrmY(double rmY)
	{
		this.rmY = rmY;
	}
	
	public double getrmY()
	{
		return this.rmY;
	}
	
	//******************************************************
	
	public void setrmZ(double rmZ)
	{
		this.rmZ = rmZ;
	}
	
	public double getrmZ()
	{
		return this.rmZ;
	}
	
	//******************************************************
	
	public void setw(double w)
	{
		this.w = w;
	}
	
	public double getw()
	{
		return this.w;
	}

	//******************************************************
	
	public String isCompressiveOnly() {

		if (isCompressiveOnly) return "true";
		return "false";
	}

	//******************************************************
	public void setSwayType(int swayType) {

		this.swayType = swayType;
	}
	
	public String getSwayType(int swayType) {

		return GuiAnalysisParams.implementedSwayTypes[swayType];
	}
	
	//******************************************************
	
	
	public void setRequiresNCMAnalysis(boolean x) {

		this.requiresNCMAnalysis = x;
	}
	
	public boolean getRequiresNCMAnalysis() {

		return this.requiresNCMAnalysis;
	}

}
