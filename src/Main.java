import java.sql.*;
import java.util.*;

public class Main {
	public static void main(String[] args) throws Exception {
		
		Connection conn = LoadServer.connect();
		
		Scanner keyboard = new Scanner(System.in);

		services: while (true) {
			System.out.println("Welcome! Who are you?");
			System.out.println("1. An administrator");
			System.out.println("2. A passenger");
			System.out.println("3. A driver");
			System.out.println("4. A manager");
			System.out.println("5. None of the above");
			System.out.println("Please enter [1-4]");
			
			int input = 0;
			try {
				input = keyboard.nextInt();
			} catch (Exception e) {
				keyboard.next();
			} finally {
				switch (input) {
					case 1: 
						Administrator.service(keyboard);
						break;
					case 2: 
						Passenger.service(keyboard);
						break;
					case 3: 
						Drivers.service(keyboard);
						break;
					case 4: 
						Manager.service(keyboard);
						break;
					case 5: 
						break services;
					default: 	
						System.out.println("Invalid input! Please try again.");
						break;
				}
			}
		}
		
		keyboard.close();
		// CreateTable();
		// post();
		// createTables();
	}

	public static void CreateTable() throws Exception {
		try {
			Connection con = LoadServer.connect();
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
			Connection con = LoadServer.connect();
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
}
