import java.util.*;
import java.io.*;
import java.sql.*;

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
						searchRequest();
						break;
					case 2: 
						takeRequest();
						break;
					case 3: 
						finishTrip();
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

	}

	private static void takeRequest(Scanner keyboard) throws Exception {

	}

	private static void finishTrip(Scanner keyboard) throws Exception {
		
	}
}
