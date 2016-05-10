package exception;

import java.util.Date;


//TO BE USED AS A CHECKED EXCEPTION (CHECKED EXC)

public class ExceptionThrower extends Exception
{
	//4 constructors must necess redefine virtual funcs
    private static final long serialVersionUID = 1L;
	private String msg_where = null;
	private String msg_what = null;
	private int lineNumber = 0;
	private Throwable cause = null;
	
	public ExceptionThrower()
	{ 	
		super();
		
		Date solverDate = new Date();
		String time = ("Runtime : " +solverDate.toString() + " => ");
		
		this.msg_where = time + "An exception has occured during parsing";
	};
	public ExceptionThrower(String message)
	{ 
		super(message);
		
		Date solverDate = new Date();
		String time = ("Runtime : " +solverDate.toString() + " => ");
		
		this.msg_where = time + "An exception has occured";
		this.msg_what =  "Exception message: " + super.getMessage();
	};
	public ExceptionThrower(String message, Throwable cause)
	{ 
		super(message, cause);
		
		Date solverDate = new Date();
		String time = ("Runtime : " +solverDate.toString() + " => ");
		
		this.msg_where = time + "An exception has occured";
		this.msg_what =  "Exception message: " + super.getMessage();
		this.cause = super.getCause();
	};
	public ExceptionThrower(Throwable cause)
	{ 
		super(cause);
		
		Date solverDate = new Date();
		String time = ("Runtime : " +solverDate.toString() + " => ");
		
		this.msg_where = time + "An exception has occured";
		this.cause = super.getCause();
	};
	public ExceptionThrower(String message, String filename, int iLine, Throwable cause)
	{
		super(message,cause);

		Date solverDate = new Date();
		String time = ("Runtime : " +solverDate.toString() + " => ");
		
		Object exc_cline = iLine;
		this.msg_where = time + "An exception has occured in filename " + filename;
		this.msg_what =  "Exception message: " + super.getMessage() + " in line number : " + exc_cline.toString();
		this.cause = super.getCause();
		this.lineNumber = iLine;
	}
	
	public String getWhereProblemStr()
	{
		if (this.msg_where != null)
		{
			return (this.msg_where);
		}
		else
		{
			return "no filename available";
		}
	}
	
	public int getWhichLineInt()
	{
		return this.lineNumber;
	}
	
	public String getWhichLineStr()
	{
		if (this.lineNumber != 0)
		{
			Object exc_cline = lineNumber;
			return "Exception in line number: " + exc_cline.toString();
		}
		else
		{
			return "no line available";
		}
	}
	
	@Override
	public String getMessage()
	{
		if (this.msg_what != null)
		{
			return this.msg_what;
		}
		else
		{
			return "no message available";
		}
	}
	
	@Override
	public Throwable getCause()
	{
		return this.cause;
	}
	
	public String getCauseStr()
	{
		if (this.cause != null)
		{
			return ("Cause : "+ this.cause.toString());
		}
		else
		{
			return "no cause available";
		}
	}
	
	public String getCauseStrMsg()
	{
		if (this.cause != null)
		{
			return ("Cause message: "+ this.cause.getMessage());
		}
		else
		{
			return "no cause available";
		}
	}
	
	
	
//	public static void main(String... args) throws Exception
//	{
//		try
//		{
//			throw new ExceptionThrower("incorrect argument y","binarycode.txt",3,new Throwable(new IOException())); //could use string too as arg in Throwable
//			//throw new FileParseException("incorrect argument y",new Throwable(new IOException()));
//			//throw new FileParseException("incorrect argument y");
//			//throw new FileParseException(new Throwable(new IOException()));
//		}
//		catch(ExceptionThrower fpe)
//		{
//			//fpe.printStackTrace();
//			System.out.println(fpe.getWhereProblemStr());
//			System.out.println(fpe.getMessage());
//			System.out.println(fpe.getCauseStrMsg());
//			System.out.println(fpe.getWhichLineStr());
//			
//			throw new Exception(fpe);  // throwing exception further down the drain...
//		}
//		catch(Exception someException) // catch above as wrapped
//		{
//			someException.printStackTrace();
//		}
//		
//	
//	}


}
