import java.util.*;
import java.io.*;
import java.sql.*;

public class Passenger {

  public static void service(Scanner keyboard) throws Exception {

    services: while (true) {
      System.out.println("Passenger, what would you like to do?");
      System.out.println("1. Request a ride");
      System.out.println("2. Check trip records");
      System.out.println("3. Go back");
      System.out.println("Please enter [1-3]");
      
      int input = 0;
      try {
        input = keyboard.nextInt();
      } catch (Exception e) {
        keyboard.next();
      } finally {
        switch (input) {
          case 1: 
            requestRide(keyboard);
            break;
          case 2: 
            checkTripRecords(keyboard);
            break;
          case 3: 
            break services;
          default: 
            System.out.println("[ERROR] Invalid input.");
            break;
        }
      }
    }
  }

  private static void requestRide(Scanner keyboard) throws Exception {
    keyboard.nextLine();
    Connection con = LoadServer.connect();
    Statement stmt = null;
    ResultSet rs = null;
    String[] questions = {
    "Please enter your ID.",
    "Please enter the number of passengers.",
    "Please enter the start location.",
    "Please enter the destination.",
    "Please enter the model. (Press enter to skip)",
    "Please enter the minimum driving years of the driver. (Press enter to skip)"
    };
    int id, num_passenger, driving_years;
    id = num_passenger = driving_years = 0;
    String start, end, model;
    start = end = model = "";

      try {
        stmt = con.createStatement();
        int i = 0;
        while(i < 6){
          String sc, sql;
          int input;
          boolean complete = false;
          while(!complete){
            sc = "";
            input = -1;
            System.out.println(questions[i]);
            try{
              sc = keyboard.nextLine().strip();
              if(sc.matches("\\d+")){
                input = Integer.parseInt(sc);
                if((i==1)&&!(input>0)){
                  System.out.println("[ERROR] Invalid input.");
                  continue;
                }
              }
              else{
                if((i==0)||(i==1)||((i==5)&&(!sc.isEmpty())))
                {
                  System.out.println("[ERROR] Invalid input.");
                  continue;
                }
              }
              
              switch(i) {
                case 0:
                  id = input;
                  sql = "SELECT * FROM Passengers WHERE PID = %d;";
                  sql = String.format(sql, id);
                  rs = stmt.executeQuery(sql);
                  if(!rs.isBeforeFirst())
                  {
                    System.out.println("[ERROR] ID not found.");
                    continue;
                  }else complete = true;
                  break;
                case 1:
                  num_passenger = input;
                  sql = "SELECT * FROM Vehicles WHERE Seats >= %d;";
                  sql = String.format(sql, num_passenger);
                  rs = stmt.executeQuery(sql);
                  if(!rs.isBeforeFirst())
                  {
                    System.out.println("[ERROR] Invalid number of passengers.");
                    continue;
                  }else complete = true;
                  break;
                case 2:
                  start = sc;
                  sql = "SELECT * FROM Taxi_stops WHERE Tname = '%s';";
                  sql = String.format(sql, start);
                  rs = stmt.executeQuery(sql);
                  if(!rs.isBeforeFirst())
                  {
                    System.out.println("[ERROR] Start location not found.");
                    continue;
                  }else complete = true;
                  break;
                case 3:
                  end = sc;
                  if (end.equalsIgnoreCase(start)){
                    System.out.println("[ERROR] Destination and start location should be different.");
                    continue;
                  }
                  sql = "SELECT * FROM Taxi_stops WHERE Tname = '%s';";
                  sql = String.format(sql, end);
                  rs = stmt.executeQuery(sql);
                  if(!rs.isBeforeFirst())
                  {
                    System.out.println("[ERROR] Destination not found.");
                    continue;
                  }else complete = true;
                  break;
                case 4:
                  if(sc.isEmpty()){
                    model = "";
                    complete = true; 
                  }else{
                    model = sc;
                    sql = "SELECT * FROM Vehicles WHERE UPPER(Model) LIKE UPPER('%"+model+"%');";
                    rs = stmt.executeQuery(sql);
                    if(!rs.isBeforeFirst())
                    {
                      System.out.println("[ERROR] Model not found.");
                      continue;
                    }else complete = true;
                  }
                  break;
                case 5:
                  if(sc.isEmpty()){
                    driving_years = 0;
                    complete = true;
                  }else{
                    driving_years = input;
                    sql = "SELECT * FROM Drivers WHERE Driving_years >= %d;";
                    sql = String.format(sql, driving_years);
                    rs = stmt.executeQuery(sql);
                    if(!rs.isBeforeFirst())
                    {
                      System.out.println("[ERROR] Invalid number of driving years.");
                      continue;
                    }else complete = true;
                  }
                  break;
                default:
                  break;
              }
              
            } catch (SQLException e) {
              throw e;
            } catch(Exception e){
              keyboard.next();
              continue;
            } 
          }
          i++;     
          if (i==6){
            sql = "SELECT COUNT(*) as TOTAL FROM (SELECT * FROM Vehicles WHERE (UPPER(Model) LIKE UPPER('%"+model+"%')) AND (Seats >= "+num_passenger+")) as V, (SELECT * FROM Drivers WHERE Driving_years >= "+ driving_years+") as D WHERE V.VID = D.VID;";
            rs = stmt.executeQuery(sql);
            rs.next();
            if(rs.getInt("TOTAL") == 0)
            {
              System.out.println("Vehicles matching above criterias not found. Please adjust the criteria.");
              i = 1;
            }else{
              System.out.println("Your request is placed. " + rs.getInt("TOTAL") + " drivers are able to take the request.");
              sql = "INSERT INTO Requests VALUES (NULL, "+id+", '"+start+"', '"+end+"', '"+model+"', "+num_passenger+",'n',"+driving_years+");";
              stmt.executeUpdate(sql);
            }
          }
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

  private static void checkTripRecords(Scanner keyboard) throws Exception {
    keyboard.nextLine();
    Connection con = LoadServer.connect();
    Statement stmt = null;
    ResultSet rs = null;
    String[] questions = {
    "Please enter your ID.",
    "Please enter the start date.",
    "Please enter the end date.",
    "Please enter the destination.",
    };
    int id;
    id = 0;
    String start_date, end_date, end;
    start_date = end_date = end = "";

      try {
        stmt = con.createStatement();
        int i = 0;
        while(i < 4){
          String sc, sql;
          int input;
          boolean complete = false;
          while(!complete){
            sc = "";
            input = -1;
            System.out.println(questions[i]);
            try{
              sc = keyboard.nextLine().strip();
              if(((i==1)||(i==2)))
                if(!sc.matches("\\d{4}-(0[1-9]|1[012])-([012][0-9]|3[01])")){
                  System.out.println("[ERROR] Invalid input.");
                  continue;
                }
              if(sc.matches("\\d+"))
                  input = Integer.parseInt(sc);
              else{
                if(i==0)
                {
                  System.out.println("[ERROR] Invalid input.");
                  continue;
                }
              }
              
              switch(i) {
                case 0:
                  id = input;
                  sql = "SELECT * FROM Passengers WHERE PID = %d;";
                  sql = String.format(sql, id);
                  rs = stmt.executeQuery(sql);
                  if(!rs.isBeforeFirst())
                  {
                    System.out.println("[ERROR] ID not found.");
                    continue;
                  }else complete = true;
                  break;
                case 1:
                  start_date = sc;
                  complete = true;
                  break;
                case 2:
                  end_date = sc;
                  if(start_date.compareTo(end_date) > 0)
                  {
                    System.out.println("[ERROR] Start date should not be greater than end date.");
                    continue;
                  }
                  complete = true;
                  break;
                case 3:
                  end = sc;
                  sql = "SELECT * FROM Taxi_stops WHERE Tname = '%s';";
                  sql = String.format(sql, end);
                  rs = stmt.executeQuery(sql);
                  if(!rs.isBeforeFirst())
                  {
                    System.out.println("[ERROR] Destination not found.");
                    continue;
                  }else complete = true;
                  break;
                default:
                  break;
              }
              
            } catch (SQLException e) {
              throw e;
            } catch(Exception e){
              keyboard.next();
              continue;
            } 
          }
          i++;     
          if (i==4){
            rs = stmt.executeQuery("SELECT TID as 'Trip_id' , Dname as 'Driver Name', VID as 'Vehicle ID', Model as 'Vehicle Model', Start_time as 'Start', End_time as 'End', Fee as 'Fee', Start_location as 'Start Location', Destination from Trips natural join Drivers natural join Vehicles where (PID="+id+") AND (DATE(Start_time)>='"+start_date+"') AND (DATE(End_time)<='"+end_date+"') AND (Destination ='"+end+"');");
            if(!rs.isBeforeFirst()){
              System.out.println("No record found");
            }
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int j = 1; j <= columnsNumber; j++) {
                    System.out.print(rsmd.getColumnLabel(j));
                    if (j < columnsNumber) System.out.print(", ");

                }
                System.out.println();
                for (int j = 1; j <= columnsNumber; j++){
                  String columnValue = rs.getString(j);
                  System.out.print(columnValue);
                  if (j < columnsNumber) System.out.print(", ");
                }
                System.out.println();
            }
          
          }
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
