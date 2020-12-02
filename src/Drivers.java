import java.util.*;
import java.io.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;  

public class Drivers {
  
  public static void service(Scanner keyboard) throws Exception {
		
		services: while (true) {
			System.out.println("Driver, what would you like to do?");
			System.out.println("1. Search requests");
			System.out.println("2. Take a request");
			System.out.println("3. Finish a trip");
			System.out.println("4. Go back");
			System.out.println("Please enter [1-4]");
			
			int input = 0;
			try {
				input = keyboard.nextInt();
			} catch (Exception e) {
				keyboard.next();
			} finally {
				switch (input) {
					case 1: 
						searchRequest(keyboard);
						break;
					case 2: 
						takeRequest(keyboard);
						break;
					case 3: 
						finishTrip(keyboard);
						break;
					case 4: 
						break services;
					default: 
						System.out.println("Invalid input! Please try again.");
						break;
				}
			}
		}
	}
	
	private static void searchRequest(Scanner keyboard) throws Exception {
		keyboard.nextLine();
    Connection con = LoadServer.connect();
    Statement stmt = null;
    ResultSet rs = null;
    String[] questions = {
    "Please enter your ID.",
    "Please enter the coordinates of your location.",
    "Please enter the maximum distance from you to the passenger.",
    };
    int id, max_dist, coord_x, coord_y;
	id = max_dist = coord_x = coord_y = 0;


      try {
        stmt = con.createStatement();
        int i = 0;
        while(i < 3){
          String sc, sql;
          int input;
          boolean complete = false;
          while(!complete){
            sc = "";
            input = -1;
            System.out.println(questions[i]);
            try{
              sc = keyboard.nextLine().strip();
              if(i==1)
                if(!sc.matches("-?\\d+ -?\\d+")){
                  System.out.println("[ERROR] Invalid input.");
                  continue;
				}
			  if((i==0)||(i==2)){
				if(!sc.matches("\\d+")){
					System.out.println("[ERROR] Invalid input.");
                  	continue;
				}
				else input = Integer.parseInt(sc);;
			  }
              
              switch(i) {
                case 0:
                  id = input;
                  sql = "SELECT * FROM Drivers WHERE DID = %d;";
                  sql = String.format(sql, id);
                  rs = stmt.executeQuery(sql);
                  if(!rs.isBeforeFirst())
                  {
                    System.out.println("[ERROR] ID not found.");
                    continue;
                  }else complete = true;
                  break;
				case 1:
				  String[] coord = sc.split("\\s+");
				  coord_x = Integer.parseInt(coord[0]);
				  coord_y = Integer.parseInt(coord[1]);
                  complete = true;
                  break;
                case 2:
                  max_dist = input;
                  complete = true;
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
          if (i==3){
			rs = stmt.executeQuery("SELECT R.RID as 'request ID', P.Pname as 'passenger name', R.Passengers as 'num of passengers', R.Start_location as 'start location', R.Destination as 'destination' FROM Requests R, Passengers P, Taxi_stops T, Drivers D, Vehicles V WHERE (R.Taken ='n') AND (P.PID = R.PID) AND (UPPER(V.Model) LIKE CONCAT('%', UPPER(R.Model), '%')) AND (V.Seats >= R.Passengers) AND (V.VID = D.VID) AND (D.DID = "+id+") AND (D.Driving_years >= R.Driving_years) AND (T.Tname = R.Start_location) AND ((ABS(T.Location_x - "+coord_x+") + ABS(T.Location_y - "+coord_y+")) <= "+max_dist+");");
            if(!rs.isBeforeFirst()){
              System.out.println("No suitable request found");
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
 
	private static void takeRequest(Scanner keyboard) throws Exception {
		Connection con = LoadServer.connect();
		System.out.println("Please enter your ID.");
		int did = -1;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			while (true) {
				try {
					did = keyboard.nextInt();
				} catch (Exception e) {
					keyboard.next();
					System.out.println("[ERROR] Invalid input.");
					continue;
				}
				
				rs = stmt.executeQuery("SELECT * FROM Drivers WHERE DID = "+did+";");
				if (!rs.isBeforeFirst()) {
					System.out.println("[ERROR] Invalid input.");
					continue;
				} else {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {}
						rs = null;
					}

					rs = stmt.executeQuery("SELECT * FROM Trips WHERE DID = "+did+" AND ISNULL(End_time);");
					if (!rs.isBeforeFirst()) break;
					else {
						System.out.println("[ERROR] Driver is in an unfinished trip.");
						return;
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
		}

		try {
			rs = stmt.executeQuery("SELECT * FROM Requests R, Drivers D, Vehicles V, Passengers P WHERE P.PID = R.PID AND R.Taken = 'n' AND D.DID = "+did+" AND D.VID = V.VID AND D.Driving_years >= R.Driving_years AND V.Seats >= R.Passengers AND UPPER(V.Model) LIKE CONCAT(\"%\", UPPER(R.Model), \"%\");");
			if (!rs.isBeforeFirst()) {
				System.out.println("[ERROR] No suitable Requests.");
				return;
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

		System.out.println("Please enter the request ID.");
		int rid = -1;
		try {
			stmt = con.createStatement();
			while (true) {
				try {
					rid = keyboard.nextInt();
				} catch (Exception e) {
					keyboard.next();
					System.out.println("[ERROR] Invalid input.");
					continue;
				}
				rs = stmt.executeQuery("SELECT * FROM Requests R, Drivers D, Vehicles V, Passengers P WHERE P.PID = R.PID AND R.RID = "+rid+" AND R.Taken = 'n' AND D.DID = "+did+" AND D.VID = V.VID AND D.Driving_years >= R.Driving_years AND V.Seats >= R.Passengers AND UPPER(V.Model) LIKE CONCAT(\"%\", UPPER(R.Model), \"%\");");
				if (!rs.isBeforeFirst()) {
					System.out.println("[ERROR] Invalid input.");
					continue;
				} else {
					rs.next();
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
					LocalDateTime now = LocalDateTime.now();
					try (PreparedStatement insert = con.prepareStatement("INSERT INTO Trips (DID, PID, Start_time, Start_location, Destination, Fee) VALUES ("+did+", "+rs.getInt("PID")+", '"+dtf.format(now)+"', '"+rs.getString("Start_location")+"', '"+rs.getString("Destination")+"', 0);")) {
						insert.executeUpdate();
						insert.close();
						System.out.println("Trip ID, Passenger name, Start");
						System.out.println(rs.getInt("RID")+", "+rs.getString("Pname")+", "+dtf.format(now));
						
					} try (PreparedStatement delete = con.prepareStatement("DELETE FROM Requests WHERE RID = "+rs.getInt("RID")+";")) {
						delete.executeUpdate();
						delete.close();
					} catch (SQLException e) {
						System.out.println("SQLException: " + e.getMessage());
						System.out.println("SQLState: " + e.getSQLState());
						System.out.println("VendorError: " + e.getErrorCode());
					} 
					break;
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

	}

	private static void finishTrip(Scanner keyboard) throws Exception {
		Connection con = LoadServer.connect();
	
		System.out.println("Please enter your ID.");
		int did = -1;
		char c = ' ';
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Trips WHERE ISNULL(End_time);");
			if (!rs.isBeforeFirst()) {
				System.out.println("[ERROR] There are no unfinished trip.");  // OR invalid DID
				return;
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
		ResultSet trip = null;
		try {
			stmt = con.createStatement();
			while (true) {
				try {
					did = keyboard.nextInt();
				} catch (Exception e) {
					keyboard.next();
					System.out.println("[ERROR] Invalid input.");
					continue;
				}
				
				rs = stmt.executeQuery("SELECT * FROM Trips WHERE DID = "+did+" AND ISNULL(End_time);");
				if (!rs.isBeforeFirst()) {
					System.out.println("[ERROR] Driver is not in an unfinished trip.");  // OR invalid DID
					continue;
				} else {
					break;
				} 
			}	

			System.out.println("Do you wish to finish the trip? [y/n]");
			loop: while (true) {
				try {
					c = keyboard.next().charAt(0);
				} catch (Exception e) {
					System.out.println("[ERROR] Invalid input.");
					continue;
				}
				switch(Character.toLowerCase(c)) {
					case 'y': 
						rs.next();
						PreparedStatement update = con.prepareStatement("UPDATE Trips SET End_time = CURRENT_TIMESTAMP(), Fee = TIMESTAMPDIFF(minute, Start_time, End_time) WHERE DID = "+rs.getInt("DID")+";");
						update.executeUpdate();
						System.out.println("Trip ID, Passenger name, Start, End, Fee");
						trip = stmt.executeQuery("SELECT T.TID, P.Pname, T.Start_time, T.End_time, T.Fee FROM Trips T, Passengers P WHERE T.PID = P.PID AND T.TID = "+rs.getInt("TID")+";");
						// trip.next();
						while (trip.next()) {
							System.out.println(trip.getInt("TID")+", "+trip.getString("Pname")+", "+trip.getString("Start_time")+", "+trip.getString("End_time")+", "+trip.getInt("Fee"));
						}
						break loop;
						case 'n': 
						break loop;
					default:
						System.out.println("[ERROR] Invalid input.");
						break;
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
	}
}
