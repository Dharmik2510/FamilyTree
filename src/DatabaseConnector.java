import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Class that will connect the program to database
public class DatabaseConnector
{
    // connection string
    private static final String CONNECTION_STRING = "jdbc:mysql://db.cs.dal.ca:3306/dsoni?serverTimezone=UTC&useSSL=false";
    // user name
    private static final String USER = "dsoni";
    // password
    private static final String PASSWORD = "B00867641";


    // getting connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);
    }
}

