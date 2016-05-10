package sofistik;


import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;


//own packages
import gui.GuiMain;


public class AnalysisMembersCollection {

	public static HashMap<Integer,AnalysisMember> membersCollection;
	
	public AnalysisMembersCollection(int nMembers) //set the inital capacity
	{
		AnalysisMembersCollection.membersCollection = new HashMap<Integer,AnalysisMember>(nMembers);
	}
	
	public static void addMemberToSet(final AnalysisMember mem)
	{
		if (mem==null || membersCollection==null) return; 
		membersCollection.put(mem.id_Cdb,mem);
	}
	
	//returns a set of all str lines, groups and load cases
	public static String[] getDataToStringArray(int option)
	{
		// options 
		// 1 : getIdIntern
		// 2 : getidGroup
		// 3 : getIdLoadCase
		if (membersCollection==null) return null;
		if (!(option == 1 || option == 2 || option == 3)) return null;
		

		LinkedHashSet<String> sArrList = new LinkedHashSet<String>();
		sArrList.add("select");
		
		for (Map.Entry<Integer,AnalysisMember> item : membersCollection.entrySet())
		{
			switch(option)
			{
				case 1 : sArrList.add(Integer.toString(item.getValue().getIdIntern()));
					break;
				case 2 : sArrList.add(Integer.toString(item.getValue().getIdGroup()));
					break;
				case 3 : 
					for (int i = 0; i < item.getValue().arrSubElements.length; i++)
					{
						for (int j=0; j< item.getValue().arrSubElements[i].lcs.length; j++)
						{
							sArrList.add(Integer.toString(item.getValue().arrSubElements[i].lcs[j]));
						}
					}
					break;
			}
		}
		String[] sArr = (String[]) sArrList.toArray(new String[sArrList.size()]);
		return sArr;
	}
	
	//return a list of groups corresponding to a given str. line
	public static String[] getGroupsForAStructuralLine(int iStrLine)
	{
		if (membersCollection==null) return null;
		AnalysisMember x = membersCollection.get((Integer)iStrLine);
		if (x==null) return null;
		
		LinkedHashSet<String> sGrpsList = new LinkedHashSet<String>();
		sGrpsList.add("select");
		
		if (x!= null) {
			sGrpsList.add(Integer.toString(x.getIdGroup()));
		}
		String[] sArr = (String[]) sGrpsList.toArray(new String[sGrpsList.size()]);
		return sArr;
	}
	
	 //return a list of load cases corresponding to a given str. line
	public static String[] getLinearLoadCasesForAStructuralLine(int iStrLine)
	{
		if (membersCollection==null) return null;
		AnalysisMember x = membersCollection.get((Integer)iStrLine);
		if (x==null) return null;
		
		LinkedHashSet<String> uniqueLcs = new LinkedHashSet<String>(); //sorting to be in the nat order of Integer
		uniqueLcs.add("select");
		
		for (AnalysisMemberSubElement subElem : x.arrSubElements)
		{
			for (int s = 0; s < subElem.lcs.length; s++)
			{
				if (x.mLoadCases != null)
				{
					for (int l = 0; l < x.mLoadCases.length; l++)
					{
						if (x.mLoadCases[l] == subElem.lcs[s] &&  x.mLoadCasesIntOrder[l]==1)
						{
							uniqueLcs.add(Integer.toString(subElem.lcs[s]));
						}
					}
				}
			}
		}

		String[] sArr = (String[]) uniqueLcs.toArray(new String[uniqueLcs.size()]);
		return sArr;
	}
	
	
	public static String[] getNonLinearLoadCasesForAStructuralLine(int iStrLine)
	{
		if (membersCollection==null) return null;
		AnalysisMember x = membersCollection.get((Integer)iStrLine);
		if (x==null) return null;
		
		LinkedHashSet<String> uniqueLcs = new LinkedHashSet<String>(); //sorting to be in the nat order of Integer
		uniqueLcs.add("select");
		
		for (AnalysisMemberSubElement subElem : x.arrSubElements)
		{
			for (int s = 0; s < subElem.lcs.length; s++)
			{
				if (x.mLoadCases != null)
				{
					for (int l = 0; l < x.mLoadCases.length; l++)
					{
						if (x.mLoadCases[l] == subElem.lcs[s] &&  x.mLoadCasesIntOrder[l]!=1)
						{
							uniqueLcs.add(Integer.toString(subElem.lcs[s]));
						}
					}
				}
			}
		}

		String[] sArr = (String[]) uniqueLcs.toArray(new String[uniqueLcs.size()]);
		return sArr;
	}
	
	
	// fills in the membersCollection member data with AnalysisMembers
	static public void createBeamSet()
	{
		if (membersCollection==null) return;
		
		int nMems = GuiMain.libInterface.getMembers();
		
		for (int i = 1; i <= nMems ; i++) { //loop over the internal cdb indexes
			
			AnalysisMember mem = new AnalysisMember();
			mem.initMemberData(i);
			mem.initSubElementsData();
			if (mem.nSubBeams == 0 || mem.nGroup == -1) continue; //not real analysis members, do not add
			membersCollection.put(mem.id_intern,mem);
		}
	}
}
