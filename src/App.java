import java.io.*;
import java.sql.*;
import java.util.*;

public class App {
	public static void main(String[] args) throws Exception {
		
		// Connection conn = connect();
		
		// Scanner keyboard = new Scanner(System.in);

		// System.out.println("Welcome! Who are you?");
		// System.out.println("1. An administrator");
		// System.out.println("2. A passenger");
		// System.out.println("3. A driver");
		// System.out.println("4. A manager");
		// System.out.println("5. None of the above");
		// System.out.println("Please enter [1-4]");
		
		// int input = 0;
		// try {
		// 	input = keyboard.nextInt();
		// } catch (Exception e) {
		// 	System.out.println("Invalid input! Please try again.");
		// } finally {
		// 	switch (input) {
		// 		case 1: break;
		// 		case 2: break;
		// 		case 3: break;
		// 		case 4: break;
		// 		case 5: break;
		// 		default: 	System.out.println("Invalid input! Please try again.");
		// 							break;
		// 	}
		// }
		

		// CreateTable();
		// post();
		createTables();
	}

	public static Connection connect() {
		String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group13";
		String dbUsername = "Group13";
		String dbPassword = "3170group13";

		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
			System.out.println("Connected");
		} catch (ClassNotFoundException e) {
			System.out.println("[ERROR]: Java MySQL Driver not found!!");
			System.exit(1);
		} catch (SQLException e) {
			System.out.println(e);
		}
		return con;
	}

	public static void CreateTable() throws Exception {
		try {
			Connection con = connect();
			PreparedStatement create = con.prepareStatement("CREATE TABLE IF NOT EXISTS S1(sid integer primary key, name varchar(30) not null, year integer, age integer);");
			create.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			System.out.println("Table created successfully!");
			
		}
	}

	public static void post() throws Exception {
		final int sid = 1;
		final String name = "ABC";
		
		try { 
			Connection con = connect();
			PreparedStatement posted = con.prepareStatement("INSERT INTO S1 VALUES(" + sid + ", '" + name + "', NULL, NULL);");

			posted.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			System.out.println("Date inserted successfully!");
		}
	}

	public static void createTables() throws Exception {

		Connection con = connect();
		try {
			PreparedStatement create = con.prepareStatement("CREATE TABLE IF NOT EXISTS Drivers(ID integer PRIMARY KEY, Name varchar(30) not null, Vehicle_ID varchar(6) not null, Driving_years integer);");
			create.executeUpdate();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			System.out.println("Table created successfully!");
		}

		String filename = "./test_data/test_data/drivers.csv";
		File file = new File(filename);
		try {
			Scanner inputStream = new Scanner(file);
			PreparedStatement insert = null;
			while (inputStream.hasNext()) {
				String data = inputStream.nextLine();
				String[] values = data.split(",");
				int id = Integer.parseInt(values[0]);
				int drivingYears = Integer.parseInt(values[3]);
				insert = con.prepareStatement("INSERT INTO Drivers VALUES ("+id+", '"+values[1]+"', '"+values[2]+"', "+drivingYears+")");
				insert.executeUpdate();
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			insert.close();
			System.out.println("Drivers tables are created");
		}

	}
}
