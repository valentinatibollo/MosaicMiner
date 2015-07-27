package it.fsm.mosaic.util;



import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;


public class DatabaseUtil {
	
	
	//private static String MOSAIC_CONNECTION = "java:comp/env/jdbc/cronicaDev";
	private static String MOSAIC_CONNECTION = "java:comp/env/jdbc/cronica";
	
	
	public static Connection getMosaicNewConnection(){
		return getConnection(MOSAIC_CONNECTION);
	}
	
	
	private static Connection getConnection(String type){
		Connection connection = null;

		try {
			  Context context = new InitialContext();
			  javax.sql.DataSource ds =(javax.sql.DataSource)context.lookup(type);
			   connection = ds.getConnection();
			}catch (Exception e) {
			  e.printStackTrace();
			}

		return connection;
	}
	
	public static void closeConnection(Connection conn){
		try{
			conn.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
}
