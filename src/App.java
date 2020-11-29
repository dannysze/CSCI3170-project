import java.sql.*;
import java.util.*;

public class App {
	public static void main(String[] args) throws Exception {
		
		connect();

		Scanner keyboard = new Scanner(System.in);

		System.out.println("Welcome! Who are you?");
		System.out.println("1. An administrator");
		System.out.println("2. A passenger");
		System.out.println("3. A driver");
		System.out.println("4. A manager");
		System.out.println("5. None of the above");
		System.out.println("Please enter [1-4]");
		
		int input;
		try {
			input = keyboard.nextInt();
		} catch (Exception e) {
			System.out.println("Invalid input! Please try again.");
		} finally {
			switch (input) {
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
			}
		}
		
	}

	public static Connection connect() {
		String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group13";
		String dbUsername = "Group13";
		String dbPassword = "3170group13";

		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
			System.out.println("Connected");
		} catch (ClassNotFoundException e) {
			System.out.println("[ERROR]: Java MySQL Driver not found!!");
			System.exit(1);
		} catch (SQLException e) {
			System.out.println(e);
		}
		return null;
	}
}
