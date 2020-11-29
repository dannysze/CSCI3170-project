package roles;
import java.util.*;
import java.io.*;
import java.sql.*;
import database.*;

public class Administrator {

  public static void service() throws Exception {

    Scanner keyboard = new Scanner(System.in);

    services: while (true) {
      System.out.println("Administrator, what would you like to do?");
      System.out.println("1. Create tables");
      System.out.println("2. Delete tables");
      System.out.println("3. Load data");
      System.out.println("4. Check data");
      System.out.println("5. Go back");
      System.out.println("Please enter [1-5]");
      
      int input = 0;
      try {
        input = keyboard.nextInt();
      } catch (Exception e) {
        System.out.println("Invalid input! Please try again.");
      } finally {
        switch (input) {
          case 1: 
            createTables();
            break;
          case 2: 
            deleteTables();
            break;
          case 3: 
            loadData();
            break;
          case 4: 
            checkData();
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
  }
  
	private static void createTables() throws Exception {

		Connection con = LoadServer.connect();
		try {
			PreparedStatement create = con.prepareStatement("CREATE TABLE IF NOT EXISTS Drivers(ID integer PRIMARY KEY, Name varchar(30) not null, Vehicle_ID varchar(6) not null, Driving_years integer);");
			create.executeUpdate();
      create.close();
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
        insert.close();
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		} finally {
			System.out.println("Drivers tables are created");
		}
  }
  
  private static void deleteTables() throws Exception {

  }

  private static void loadData() throws Exception {

  }

  private static void checkData() throws Exception {

  }
}
