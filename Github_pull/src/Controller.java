
public class Controller {

	/**
	 * @param args
	 */
	static DatabaseAccessGithub dag = new DatabaseAccessGithub();
	static IOFormatter io=new IOFormatter();
	static BatchProcess bp= new BatchProcess();
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		float startTime = 0;
		float endTime = 0;
		io.inputConString();
		System.out.println("");
		startTime = System.nanoTime();
		
		System.out.println("Connecting to Database...");
		boolean isGithub= dag.openConnection(io.getDBN(), io.getMysqlUserName(), io.getMysqlPass());
		if(isGithub)
		{
			System.out.println("Connected..");
			System.out.println("");
			
			if(io.getDBN().equalsIgnoreCase("github"))
			{
				io.batchInput();
				io.inputData();
			}	
			bp.getOwnerList(io.getDirectoryPath());
			dag.generateDCN(bp.getOwnerList(io.getDirectoryPath()), io.getStartDate(), io.getEndDate());
			io.writeFile(dag.getFileContent(), io.getDirectoryPath()+"network.net");
			endTime = System.nanoTime();
			System.out.println("Complete Execution");
			System.out.println("Total Time Elapsed: " + (((endTime - startTime)/1000000000)/60) + " minutes");
		}
		else
		{
			System.out.println("Wrong Connection String/Username/Password!");
					
		}

	}

}
