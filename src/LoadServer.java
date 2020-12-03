import java.sql.*;

public class LoadServer { // Get Connection Obj 
  
	public static Connection connect() {
		String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/group13";
		String dbUsername = "Group13";
		String dbPassword = "3170group13";

		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
			// System.out.println("Connected");
		} catch (ClassNotFoundException e) {
			System.out.println("[ERROR]: Java MySQL Driver not found!!");
			System.exit(1);
		} catch (SQLException e) {
			System.out.println(e);
		}
		return con;
	}
}
