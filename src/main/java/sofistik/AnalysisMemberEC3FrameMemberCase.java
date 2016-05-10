package sofistik;

public class AnalysisMemberEC3FrameMemberCase extends AnalysisMemberEC3MemberCase
{
	double k1y;
	double k2y;
	
	double k1z;
	double k2z;
	
	AnalysisMemberEC3FrameMemberCase()
	{
		super();
		this.k1y = -999.0;
		this.k2y = -999.0;
		
		this.k1z = -999.0;
		this.k2z = -999.0;
	}
	
	//**********************************
	
	public void setk1y(double k1y)
	{
		this.k1y = k1y;
	}
	
	public double getk1y()
	{
		return this.k1y;
	}
	
	//***********************************
	
	public void setk2y(double k2y)
	{
		this.k2y = k2y;
	}
	
	public double getk2y()
	{
		return this.k2y;
	}
	
	//***********************************
	
	public void setk1z(double k1z)
	{
		this.k1z = k1z;
	}
	
	public double getk1z()
	{
		return this.k1z;
	}
	
	//***********************************
	
	public void setk2z(double k2z)
	{
		this.k2z = k2z;
	}
	
	public double getk2z()
	{
		return this.k2z;
	}

}
