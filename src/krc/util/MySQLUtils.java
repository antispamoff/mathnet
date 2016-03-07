package krc.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLUtils {
	public static Connection getMySQLConnection() {
		Connection mySQLconn = null;
		try {
	        Class.forName("com.mysql.jdbc.Driver").newInstance();
			mySQLconn = DriverManager.getConnection("jdbc:mysql://localhost/mathnet?useSSL=false&user=root&password=root");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mySQLconn;
	}
}
