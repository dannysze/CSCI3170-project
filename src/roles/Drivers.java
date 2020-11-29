package roles;
import java.util.*;
import java.io.*;
import java.sql.*;
import database.*;

public class Drivers {
  
  public static void service() throws Exception {

		Scanner keyboard = new Scanner(System.in);
		
		services: while (true) {
			System.out.println("Administrator, what would you like to do?");
			System.out.println("1. Search requests");
			System.out.println("2. Take a request");
			System.out.println("3. Finish a trip");
			System.out.println("4. Go back");
			System.out.println("Please enter [1-4]");
			
			int input = 0;
			try {
				input = keyboard.nextInt();
			} catch (Exception e) {
				System.out.println("Invalid input! Please try again.");
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
		keyboard.close();
	}
	
	private static void searchRequest() throws Exception {

	}

	private static void takeRequest() throws Exception {

	}

	private static void finishTrip() throws Exception {

	}
}
