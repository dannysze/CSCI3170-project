import java.util.*;
import java.io.*;
import java.sql.*;

public class Administrator {

  public static void service(Scanner keyboard) throws Exception {

    // Scanner keyboard = new Scanner(System.in);

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
        keyboard.next();
      } finally {
        switch (input) {
          case 1: 
            createTables();
            break;
          case 2: 
            deleteTables();
            break;
          case 3: 
            loadData(keyboard);
            break;
          case 4: 
            checkData();
            break;
          case 5: 
            break services;
          default: 
            System.out.println("[ERROR] Invalid input.");
            break;
        }
      }
    }
  }
  
	private static void createTables() throws Exception {

    String[] createTables = { "CREATE TABLE IF NOT EXISTS Passenger(PID integer PRIMARY KEY, Pname varchar(30) NOT NULL)",
      "CREATE TABLE IF NOT EXISTS Vehicle(VID varchar(6) PRIMARY KEY, Model varchar(30) NOT NULL, Seats integer NOT NULL);",
      "CREATE TABLE IF NOT EXISTS Driver(DID integer PRIMARY KEY, Dname varchar(30) NOT NULL, VID varchar(6) NOT NULL, Driving_years integer, FOREIGN KEY (VID) REFERENCES Vehicle(VID));",
      "CREATE TABLE IF NOT EXISTS Taxi_Stop(Tname varchar(20) PRIMARY KEY, Location_x integer NOT NULL, Location_y integer NOT NULL);",
      "CREATE TABLE IF NOT EXISTS Trip(TID integer PRIMARY KEY AUTO_INCREMENT, DID integer NOT NULL, PID integer NOT NULL, Start_time datetime NOT NULL, End_time datetime, Start_location varchar(20) NOT NULL, Destination varchar(20) NOT NULL, Fee integer NOT NULL,  FOREIGN KEY (DID) REFERENCES Driver(DID), FOREIGN KEY (PID) REFERENCES Passenger(PID));",
      "CREATE TABLE IF NOT EXISTS Request(RID integer PRIMARY KEY AUTO_INCREMENT, PID integer NOT NULL, Start_location varchar(20) NOT NULL, Destination varchar(20) NOT NULL, Model varchar(30) NOT NULL, Passengers integer NOT NULL, Taken varchar(1) NOT NULL, Driving_years integer NOT NULL, FOREIGN KEY (PID) REFERENCES Passenger(PID));"
    };
    Connection con = LoadServer.connect();
    System.out.print("Processing...");
    for (int i = 0; i < createTables.length; i++) {
      try (PreparedStatement create = con.prepareStatement(createTables[i])) {
        create.executeUpdate();
      } catch (SQLException e) {
        System.out.println("SQLException: " + e.getMessage());
        System.out.println("SQLState: " + e.getSQLState());
        System.out.println("VendorError: " + e.getErrorCode());
      } finally {
      }
    }
    System.out.print("Done! Tables are created!\n");
    con.close();

  }
  
  private static void deleteTables() throws Exception {
    String[] deleteTables = { "Request", "Trip", "Driver", "Taxi_Stop", "Vehicle", "Passenger"};
    Connection con = LoadServer.connect();
    System.out.print("\rProcessing...");
    for (int i = 0; i < deleteTables.length; i++) {
      try (PreparedStatement delete = con.prepareStatement("DROP TABLE IF EXISTS " +deleteTables[i])) {
      delete.executeUpdate();
      } catch (SQLException e) {
        System.out.println("SQLException: " + e.getMessage());
        System.out.println("SQLState: " + e.getSQLState());
        System.out.println("VendorError: " + e.getErrorCode());
      } finally {
      }
    }
    System.out.print("Done! Tables are deleted!\n");
    con.close();
  }
  
  private static void loadData(Scanner keyboard) throws Exception {
    //if all data is sucessfully loaded, then this variable remains true.If not then false.
    boolean success = true;

    Connection con = LoadServer.connect();
    System.out.println("Please enter the folder path");
    String path = keyboard.next();
    
    System.out.print("Processing... ");
    String filename = path + "/vehicles.csv";
    File file = new File(filename);
    try {
      Scanner inputStream = new Scanner(file);
      while (inputStream.hasNext()) {
        String data = inputStream.nextLine();
        String[] values = data.split(",");
        int seats = Integer.parseInt(values[2]);
        try (PreparedStatement insert = con.prepareStatement("INSERT INTO Vehicle VALUES ('"+values[0]+"', '"+values[1]+"', "+seats+");")) {
          insert.executeUpdate();
          insert.close();
        } catch (SQLException e) {
          System.out.println("SQLException: " + e.getMessage());
          System.out.println("SQLState: " + e.getSQLState());
          System.out.println("VendorError: " + e.getErrorCode());

          success = false;
        } finally {
        }
      }
      inputStream.close();
    } catch (FileNotFoundException e) {
      System.out.println(e);
      success = false;
    }
    
    filename = path + "/drivers.csv";
     file = new File(filename);
    try {
      Scanner inputStream = new Scanner(file);
      while (inputStream.hasNext()) {
        String data = inputStream.nextLine();
        String[] values = data.split(",");
        int id = Integer.parseInt(values[0]);
        int drivingYears = Integer.parseInt(values[3]);
        try (PreparedStatement insert = con.prepareStatement("INSERT INTO Driver VALUES ("+id+", '"+values[1]+"', '"+values[2]+"', "+drivingYears+");")) {
          insert.executeUpdate();
          insert.close();
        } catch (SQLException e) {
          System.out.println("SQLException: " + e.getMessage());
          System.out.println("SQLState: " + e.getSQLState());
          System.out.println("VendorError: " + e.getErrorCode());

          success = false;
        } finally {
          // System.out.print("\rProcessing... ");
        }
      }
      inputStream.close();
    } catch (FileNotFoundException e) {
      System.out.println(e);
      success = false;
    }

    filename = path + "/passengers.csv";
    file = new File(filename);
    try {
      Scanner inputStream = new Scanner(file);
      while (inputStream.hasNext()) {
        String data = inputStream.nextLine();
        String[] values = data.split(",");
        int id = Integer.parseInt(values[0]);
        try (PreparedStatement insert = con.prepareStatement("INSERT INTO Passenger VALUES ("+id+", '"+values[1]+"');")) {
          insert.executeUpdate();
          insert.close();
        } catch (SQLException e) {
          System.out.println("SQLException: " + e.getMessage());
          System.out.println("SQLState: " + e.getSQLState());
          System.out.println("VendorError: " + e.getErrorCode());

          success = false;
        } finally {
          // System.out.print("\rProcessing... ");
        }
      }
      inputStream.close();
    } catch (FileNotFoundException e) {
      System.out.println(e);
      success = false;
    }

    filename = path + "/taxi_stops.csv";
    file = new File(filename);
    try {
      Scanner inputStream = new Scanner(file);
      while (inputStream.hasNext()) {
        String data = inputStream.nextLine();
        String[] values = data.split(",");
        int location_x = Integer.parseInt(values[1]);
        int location_y = Integer.parseInt(values[2]);
        try (PreparedStatement insert = con.prepareStatement("INSERT INTO Taxi_Stop VALUES ('"+values[0]+"', "+location_x+", "+location_y+");")) {
          insert.executeUpdate();
          insert.close();
        } catch (SQLException e) {
          System.out.println("SQLException: " + e.getMessage());
          System.out.println("SQLState: " + e.getSQLState());
          System.out.println("VendorError: " + e.getErrorCode());

          success = false;
        } finally {
          // System.out.print("\rProcessing... ");
        }
      }
      inputStream.close();
    } catch (FileNotFoundException e) {
      System.out.println(e);
      success = false;
    }

    filename = path + "/trips.csv";
    file = new File(filename);
    try {
      Scanner inputStream = new Scanner(file);
      while (inputStream.hasNext()) {
        String data = inputStream.nextLine();
        String[] values = data.split(",");
        int tid = Integer.parseInt(values[0]);
        int did = Integer.parseInt(values[1]);
        int pid = Integer.parseInt(values[2]);
        int fee = Integer.parseInt(values[7]);
        try (PreparedStatement insert = con.prepareStatement("INSERT INTO Trip VALUES ("+tid+", "+did+", "+pid+", '"+values[3]+"', '"+values[4]+"', '"+values[5]+"', '"+values[6]+"', "+fee+");")) {
          insert.executeUpdate();
          insert.close();
        } catch (SQLException e) {
          System.out.println("SQLException: " + e.getMessage());
          System.out.println("SQLState: " + e.getSQLState());
          System.out.println("VendorError: " + e.getErrorCode());

          success = false;
        } finally {
          // System.out.print("\rProcessing... ");
        }
      }
      inputStream.close();
    } catch (FileNotFoundException e) {
      System.out.println(e);
      success = false;
    }

    if(success)
      System.out.print("Data is loaded!\n");
  }

  private static void checkData() throws Exception {
    System.out.println("Numbers of record in each table: ");

    String[] checkTables = { "Vehicle", "Passenger", "Driver", "Trip", "Request", "Taxi_Stop"};
    Connection con = LoadServer.connect();
    Statement stmt = null;
    ResultSet rs = null;
    for (int i = 0; i < checkTables.length; i++) {
      try {
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT COUNT(*) AS TOTAL FROM " +checkTables[i]+";");
        rs.next();
        System.out.println(checkTables[i]+" "+rs.getInt("TOTAL"));
      } catch (SQLException e) {
        System.out.println("SQLException: " + e.getMessage());
        System.out.println("SQLState: " + e.getSQLState());
        System.out.println("VendorError: " + e.getErrorCode());
      } finally {
        if (rs != null) {
          try {
            rs.close();
          } catch (SQLException e) {}
          rs = null;
        }

        if (stmt != null) {
          try {
            stmt.close();
          } catch (SQLException e) {}
          stmt = null;
        }
      }
    }
    con.close();

  }
}
