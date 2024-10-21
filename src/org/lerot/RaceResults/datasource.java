package org.lerot.RaceResults;



import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class datasource
{

	Connection con;
	String dbname="";
	private String errorMessage;
	String path="";

	public datasource(String apath)
	{
		path= apath;
		
	}
	
	public Connection getConnection()
	{
		 connect();
		 return con;
	}
	
	
	public void disconnect()
	{
	 try
	{
		con.close();
	} catch (SQLException e)
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	public void connect()
	{
		String connectstring = "";
		if (checkFileCanRead(path))
		{
			try
			{
				Class.forName("org.sqlite.JDBC");
				File dbfile = new File(path);
				dbname = dbfile.getName();
				connectstring = "jdbc:sqlite:"	+ path;
				con = DriverManager.getConnection(connectstring);
				if(con!=null)
				{
				  //System.out.println("Can connect to " + connectstring);
					dbname = dbfile.getName();
					//System.out.println("opened :"+ dbname);
				}
				else
					System.out.println("Cannot connect to " + connectstring);
			} catch (Exception e)
			{
				errorMessage = e.getClass().getName() + ": " + e.getMessage();
				System.out.println("Cannot connect to " + connectstring);
				con = null;
			}
		} else
		{
			errorMessage = " cannot read " + path;
			con = null;
		}
	}

	public Map<String, String> checkmcdb()
	{
		HashMap<String, String> checks = new HashMap<String, String>();
		PreparedStatement preparedStatement = null;
		checks.put("dbname", dbname);
		try
		{ 
			connect();
			preparedStatement = con
					.prepareStatement(" select value as val  from parameters where key='Title'; ");
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next())
			{
				String title =  rs.getString("val");
				checks.put("Title", title);
			}
			preparedStatement.close();
			preparedStatement = con
					.prepareStatement(" select count(*) as nc from proup ");
			 rs = preparedStatement.executeQuery();
			while (rs.next())
			{
				String nc = "" + rs.getInt("nc");
				checks.put("No of Contacts", nc);
			}
			preparedStatement.close();
			preparedStatement = con
					.prepareStatement(" SELECT max(update_dt) as mud FROM attributevalues ");
			rs = preparedStatement.executeQuery();
			while (rs.next())
			{
				String nc = rs.getString("mud");
				checks.put("Latest Update", nc);
			}
			preparedStatement.close();
			checks.put("Valid", "yes");
		} catch (SQLException e)
		{
			errorMessage = e.getClass().getName() + ": " + e.getMessage();
			// System.err.println(errorMessage);
			checks.put("Valid", "no");
		}
		disconnect();
		return checks;
	}

	public static boolean checkFileCanRead(String filename)
	{
		File file = new File(filename);
		if (!file.exists()) return false;
		if (!file.canRead()) return false;
		try
		{
			FileReader fileReader = new FileReader(filename);
			fileReader.read();
			fileReader.close();
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public static void listFiles()
	{
		File f = new File("."); // current directory

		Vector<String> list = getdbs(f.getPath());
		for (String file : list)
		{
			System.out.println("     file:" + file);
		}
	}

	public static Vector<String> getdbs(String dotcontacts)
	{
		Vector<String> databases = new Vector<String>();
		File dotdir = new File(dotcontacts);
		File[] files = dotdir.listFiles();
		for (File file : files)
		{
			String name = file.getName();
			if (name.endsWith(".sqlite"))
			{
				databases.add(file.getPath());
				//System.out.println(file.getName());
			}
		}
		return databases;
	}

	
	
	
	
	public ArrayList<Map<String, String>> doQuery(String sqlstr)
	{
		ArrayList<Map<String, String>> Rowlist = new ArrayList<Map<String, String>>();
		ResultSetMetaData rsmd = null;
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			getConnection();
			stmt = con.createStatement();
			if (stmt == null)
				System.out.println(" Processing error " + sqlstr);
			rs = stmt.executeQuery(sqlstr);
			rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (rs.next())
			{
				Map<String, String> arow = new HashMap<String, String>();
				// The column count starts from 1
				for (int i = 1; i < columnCount + 1; i++)
				{
					String name = rsmd.getColumnName(i);
					String value = rs.getString(i);
					if (name != null) arow.put(name, value);
				}
				Rowlist.add(arow);
			}
			rs.close();
			stmt.close();
			disconnect();
			return Rowlist;
		} catch (SQLException e)
		{
			errorMessage = e.getClass().getName() + ": " + e.getMessage();
			System.err.println(errorMessage);
			return null;
		}

	}

	
}