import java.sql.*;
import java.util.*;

public class Main {
	public static void main(String[] args) throws Exception {
		
		// Connection conn = LoadServer.connect();
		// conn.close();
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
	}
}