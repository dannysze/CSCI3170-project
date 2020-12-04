import java.util.*;
import java.sql.*;

public class Manager {
  
  public static void service(Scanner keyboard) throws Exception {

    services: while (true) {
      System.out.println("Manager, what would you like to do?");
      System.out.println("1. Find trips");
      System.out.println("2. Go back");
      System.out.println("Please enter [1-2]");
      
      int input = 0;
      try {
        input = keyboard.nextInt();
      } catch (Exception e) {
        keyboard.next();
      } finally {
        switch (input) {
          case 1: 
            findTrips(keyboard);
            break;
          case 2: 
            break services;
          default: 
            System.out.println("[ERROR] Invalid input.");
            break;
        }
      }
    }
  }

  private static void findTrips(Scanner keyboard) throws Exception {
    int min = 0;
    int max = 0;
    while (true) {
      System.out.println("Please enter the minimum travelling distance.");
      try {
        min = keyboard.nextInt();
      } catch (Exception e) {
        System.out.println("[ERROR] Invalid input.");
        keyboard.next();
        continue;
      }

      if (min < 0) { // Upper bound missing
        System.out.println("[ERROR] Invalid input.");
      } else break;
    }
    
    while (true) {
      System.out.println("Please enter the maximum travelling distance.");
      try {
        max = keyboard.nextInt();
      } catch (Exception e) {
        System.out.println("[ERROR] Invalid input.");
        keyboard.next();
        continue;
      }

      if (max <= 0 || max < min) { // Upper bound missing
        System.out.println("[ERROR] Invalid input.");
      } else break;
    }
    
    Connection con = LoadServer.connect();

    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT TEMP.TID, D.Dname, P.Pname, TEMP.Start_location, TEMP.Destination, TEMP.Duration FROM (SELECT T.TID, T.Start_location, T.Destination, T.Fee AS Duration, T.DID, T.PID, ABS(TS2.Location_x-TS1.Location_x)+ABS(TS2.Location_y-TS1.Location_y) AS Distance FROM Trip T, Taxi_Stop TS1, Taxi_Stop TS2 WHERE T.Start_location = TS1.Tname AND T.Destination = TS2.Tname) AS TEMP, Driver D, Passenger P WHERE TEMP.PID = P.PID AND TEMP.DID = D.DID AND Distance >= "+min+" AND Distance <= "+max+";");
      System.out.println("trip id, driver name, passenger name, start location, destination, duration");
      while (rs.next()) {
        System.out.println(rs.getInt("TID")+", "+rs.getString("Dname")+", "+rs.getString("Pname")+", "+rs.getString("Start_location")+", "+rs.getString("Destination")+", "+rs.getInt("Duration"));
      }
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
    
    con.close();
  }
}
