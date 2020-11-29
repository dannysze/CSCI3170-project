package roles;
import java.util.*;
import java.io.*;
import java.sql.*;

public class Administrator {
  
	public static void createTables() throws Exception {

		Connection con = App.connect();
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
}
