package webservices;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class DBUpdateUtil {
	private static final String SERVER_ACC = "jdbc:mysql://192.168.56.103/sweng";
	private static final String DB_USER = "user";
	private static final String USER_PW = "password";

	public static void main(String[] args)
		throws SQLException
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if(args.length < 2)
		{
			System.out.println("Error: Too few parameters");
			System.out.println("Usage: <type> <file>");
			System.out.println("	Where <type> is one of <user>, <product> or <location>");
			System.out.println("	and <file> specifies the input-file");
			System.exit(1);
		}

		switch (args[0])
		{
			case "user": 	addUsers(args[1]); 		break;
			case "product": addProducts(args[1]);	break;
			case "location":addLocations(args[1]);	break;
			default:		System.err.println("Invalid type: " + args[0]);
							System.exit(1);	break;
		}
	}

	private static void addUsers(String fn)
		throws SQLException
	{
		Connection con = DriverManager.getConnection(SERVER_ACC, DB_USER, USER_PW);
		PreparedStatement s = con.prepareStatement("INSERT INTO user (name) VALUES (?)");

		try(BufferedReader br = new BufferedReader(new FileReader(fn)))
		{
			String str;

			while((str = br.readLine()) != null)
			{
				s.setString(1, str);
				s.execute();
			}
		}catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		con.close();
	}

	private static void addProducts(String fn)
		throws SQLException
	{
		Connection con = DriverManager.getConnection(SERVER_ACC, DB_USER, USER_PW);
		PreparedStatement s = con.prepareStatement("INSERT INTO product (name, description, iconFile) VALUES (?, ?, ?)");

		try(BufferedReader br = new BufferedReader(new FileReader(fn)))
		{
			String str;

			while((str = br.readLine()) != null)
			{
				int idxEndProdName = str.indexOf('\"', 1);
				int idxEndDescription = str.indexOf('\"', idxEndProdName + 3);

				String prodName = str.substring(1, idxEndProdName);
				String description = str.substring(idxEndProdName + 3, idxEndDescription);
				String iconStr = str.substring(idxEndDescription + 2);

				s.setString(1, prodName);
				s.setString(2, description);
				s.setString(3, iconStr);
				s.execute();

				// TODO upload images manually
			}
		}catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		con.close();
	}

	private static void addLocations(String fn)
		throws SQLException
	{
		Connection con = DriverManager.getConnection(SERVER_ACC, DB_USER, USER_PW);
		PreparedStatement s = con.prepareStatement("INSERT INTO location (name, xcoord, ycoord) VALUES (?, ?, ?)");

		try(BufferedReader br = new BufferedReader(new FileReader(fn)))
		{
			String str;

			while((str = br.readLine()) != null)
			{
				int idxEndProdName = str.indexOf('\"', 1);
				String prodName = str.substring(1, idxEndProdName);

				String[] location = str.substring(idxEndProdName + 2).split(" ");

				s.setString(1, prodName);
				s.setInt(2, Integer.parseInt(location[0]));
				s.setInt(3, Integer.parseInt(location[1]));
				s.execute();
			}
		}catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		con.close();
	}
}
