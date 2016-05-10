package sofistik;

import java.util.TreeSet;


public class AnalysisOptions //class to keep the data for a chosen analysis (str line, group, load cases and analysis type)
{
	//need them public as they are used in the package gui
	public static String[] strLinesStringArray;
	public static TreeSet<Integer> selectedLinearLoadCases;
	public static TreeSet<Integer> selectedNoneLinearLoadCases;
	public static int selectedGroup;
	public static int selectedStrLine;
	public static AnalysisMethods selectedAnalysisOption; //enum
	public static AnalysisOutputLevel selectedOutputLevel;
}