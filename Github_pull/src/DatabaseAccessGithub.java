import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class DatabaseAccessGithub {

	public Connection con;
	public ResultSet rs, rs2;
	public Statement s;
	public Statement s2;
	
	private NetworkBuilder nb= new NetworkBuilder();
	
	private String fileContent;
	private String fileName;
	private String dbName;
	
	private int num;
	
	public DatabaseAccessGithub () {

		fileContent ="";
		fileName="";
		
	}
	public String getFileContent()
	{
		return fileContent;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public String getDBName()
	{
		return dbName;
	}
	
	
	public boolean openConnection(String databaseName, String mysqlUser, String password) throws Exception
	{
		dbName = databaseName;
		Class.forName("com.mysql.jdbc.Driver"); //load mysql driver
		try
		{
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + databaseName + "?user=" + mysqlUser + "&password=" + password); //set-up connection with database
			s = con.createStatement(); //Statements to issue sql queries
			s2 = con.createStatement();
		} catch (SQLException e) 
		{
			con.close();
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public String[]  getMinMax() throws Exception {
		 
		System.out.println("Calculating Min and Max date From The database");
		String[] list=null;
		//System.out.println("connection object"+s);
		rs= s.executeQuery("select min(created_at),max(created_at) from pull_request_comments");
		boolean status;
		status=rs.next();
		if(status)
		{
		 list=new String[2];
		 list[0]=rs.getString(1);
		 list[1]=rs.getString(2);
		}
		return list;
	}	
		
	public void generateDCN(String projectList, String startDate, String endDate) throws SQLException {
		
		ArrayList<String> developers=new ArrayList<String>();
		ArrayList<String> developers2= new ArrayList<String>();
		ArrayList<String> developers3=new ArrayList<String>();
		
		ArrayList<Integer> edges= new ArrayList<Integer>();
		int num=0;
		String projectList1=projectList.replaceAll("'", "");
		
		String [] projectListExcel=projectList1.split(",");
		
		ArrayList<String> excelList=new ArrayList<String>(Arrays.asList(projectListExcel)); 
		
		System.out.println("");
		System.out.println("Calculating the Total Number of Distinct developers....");
		String qry;
/*		if(projectList!=null)
		{
			qry="select distinct a.owner_id from projects a,users b " +
				"where a.owner_id=b.id and a.id in ("+projectList+") and  " +
						"" +
						"" +
						
		}*/
		System.out.println("");
		System.out.println("Calculating the Total Number of Distinct Developers...");
		qry="select count(distinct(a.user_id)) 'who'" +
				"from pull_request_comments a, commits b, projects c " +
				"where a.commit_id = b.id " +
				"and b.project_id = c.id " +
				"and c.id in ("+projectList+") " +
				"and a.created_at between '"+startDate+"' and '"+endDate+"' ;"; 

		rs = s.executeQuery(qry);
		System.out.println(qry);
		
		while(rs.next())
		{
			num = rs.getInt("who");
		}
		
		System.out.println("Retrieving the Developer's E-Mail Addresses...");
		
		qry=	"select distinct(d.login) 'who' " +
				"from pull_request_comments a, commits b, projects c, users d " +
				"where a.commit_id = b.id " +
				"and b.project_id = c.id " +
				"and a.user_id = d.id " +
				"and c.id in ("+projectList+") " +
				"and a.created_at between '"+startDate+"' and '"+endDate+"' " +
				"order by who; "; 
		rs = s.executeQuery(qry);
		System.out.println(qry);
		
		while(rs.next())
		{
			developers.add(rs.getString("who"));
		}
		
		System.out.println("Building the Developer Communication Network...");
		
		qry=	"select a.user1, count(distinct(b.pid2)), b.user2 from" +
				"(select a.pull_request_id 'pid1', b.login 'user1', a.commit_id 'cid1', a.created_at from pull_request_comments a, users b where a.user_id = b.id) a," +
				"(select a.pull_request_id 'pid2', b.login 'user2', a.commit_id 'cid2', a.created_at from pull_request_comments a, users b where a.user_id = b.id) b," +
				"commits c, projects d " +
				"where user1 <> user2 " +
				"and a.pid1 = b.pid2 " +
				"and a.cid1 = c.id " +
				"and c.project_id = d.id " +
				"and d.id in ("+projectList+") " +
				"and a.created_at between '"+startDate+"' and '"+endDate+"' " +
				"group by a.user1, b.user2 " +
				"order by a.user1; "			;
		rs = s.executeQuery(qry);
		System.out.println(qry);
		
		while(rs.next())
		{
			developers2.add(rs.getString("a.user1"));
			developers3.add(rs.getString("b.user2"));
			edges.add((rs.getInt("count(distinct(b.pid2))")));
		}
		
		//System.out.println(developers + "\n" + developers2 + "\n" + developers3 + "\n" + edges + "\n" );
		
		fileContent = nb.networkBuilder(developers, developers2, developers3, edges, num);
	
	}
	

}