import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;


public class BatchProcess {
	DatabaseAccessGithub dag = Controller.dag;
	public String getOwnerList(String s) throws Exception
	{
	
		String dirName = s;
		String projectList=null;
		CSVReader reader = new CSVReader(new FileReader(dirName+"project-ids.csv"), ',', '\"', 1);
		String [] nextLine;
		
		while ((nextLine = reader.readNext()) != null) 
		{
	       
			if(nextLine[0].isEmpty()||nextLine[0].trim().isEmpty()||nextLine[0]==null||nextLine[0].trim().equals("none"))
			{
			
				return null;
			}
			else if(projectList==null)
			{
			
				projectList=nextLine[0].trim()+",";
				//System.out.println("accountList="+accountList);					
			}
			else 
			{
				
				projectList=projectList+nextLine[0].trim()+",";
				//System.out.println("accountList="+accountList);
			}
				
		}
	   //System.out.println(accountList);
		if(projectList!=null)	
			return projectList.substring(0, projectList.length()-1);
		else
			return projectList;
}
}
