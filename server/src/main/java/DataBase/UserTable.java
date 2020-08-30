package DataBase;

import java.sql.*;


public class UserTable  implements SQLConstants{
    private Connection connection;
    private PreparedStatement statement;

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + " (" +
            USER_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            LOGIN_COLUMN + " TEXT, " +
            PASSWORD_COLUMN + " INTEGER);";

    public UserTable() {
        this.connection = DBHelper.getInstance().getConnection();
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try {
            statement = connection.prepareStatement(CREATE_TABLE);
            statement.execute();
        } catch (SQLException e) {
            System.out.println("UserTable" + e);
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("UserTable" + e);
            }
        }
    }

    public void createNewUser(User user) {
        try {
            statement = connection.prepareStatement("INSERT INTO " + USER_TABLE_NAME + "(" +
                    LOGIN_COLUMN + ", " +
                    PASSWORD_COLUMN + ") " +
                    "VALUES (?,?);");
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("UserTable" + e);
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("UserTable" + e);

            }
        }
    }

    public boolean isUserExists(String login) {
        try {
            statement = connection.prepareStatement("SELECT " + USER_ID + " FROM " +
                    USER_TABLE_NAME + " WHERE (" +
                    LOGIN_COLUMN + " = '" + login + "' );");
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet == null || resultSet.isClosed() || !resultSet.next()) return false;
            else return true;
        } catch (SQLException e) {
            System.out.println("UserTable" + e);
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("UserTable" + e);
            }
        }
        return false;
    }

    public boolean checkAuth(String login, String password) {
        try {
            statement = connection.prepareStatement("SELECT " + PASSWORD_COLUMN + " FROM " +
                    USER_TABLE_NAME + " WHERE (" +
                    LOGIN_COLUMN + " = '" + login + "' );");
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet != null && !resultSet.isClosed()) {
                String resultPass = resultSet.getString(1);
                if (resultPass.equals(password)) return true;
            } else return false;
        } catch (SQLException e) {
            System.out.println("UserTable" + e);
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("UserTable" + e);
            }
        }
        return false;
    }

       public int getUserId(String login) {
        try {
            statement = connection.prepareStatement("SELECT " + USER_ID_COLUMN + " FROM " +
                    USER_TABLE_NAME + " WHERE (" +
                    LOGIN_COLUMN + " = '" + login + "' );");
            statement.execute();
            ResultSet resultSet = statement.getResultSet();

            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.out.println("UserTable" + e);
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("UserTable" + e);
            }
        }
        return -1;
    }

}
