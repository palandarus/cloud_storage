package DataBase;

import org.springframework.stereotype.Component;

import java.sql.*;


@Component
public class DBHelper {



    private Connection connection;
    private static DBHelper ourInstance = new DBHelper();

    public static DBHelper getInstance() {
        return ourInstance;
    }

    private DBHelper() {
    }

    public Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:serverDB.db");
            System.out.println("Successfully connect to DB");
        } catch (ClassNotFoundException e) {
            System.out.println("DataBase " + e);
        } catch (SQLException e) {
            System.out.println("DataBase " + e);
        }

        return connection;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void disconnectDb() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("DataBase " + e);
        }
    }
}
