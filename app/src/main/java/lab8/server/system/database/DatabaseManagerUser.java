package lab8.server.system.database;

import lab8.shared.io.connection.UserCredentials;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManagerUser {
    private static final String CHECK_PASSWORD_USER = "SELECT password FROM users WHERE name = ?";
    private static final String ADD_USER = "INSERT INTO users (user_id, name, password) VALUES (default, ?, ?)";
    private static final String FIND_UID_BY_NAME = "SELECT user_id FROM users WHERE name = ?";

    private static final Logger logger = Logger.getLogger(DatabaseManagerUser.class.getName());

    private static DatabaseManagerUser instance;

    private DatabaseManagerUser() {
    }

    public static synchronized DatabaseManagerUser getInstance() {
        return (instance == null) ? instance = new DatabaseManagerUser() : instance;
    }

    public boolean checkPassword(UserCredentials userCredentials) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {

            try (PreparedStatement stmt = DatabaseManager.getInstance().
                    prepareStatement(CHECK_PASSWORD_USER, conn, userCredentials.username())
            ) {
                ResultSet res = stmt.executeQuery();

                if (res.next())
                    return res.getString("password").equals(userCredentials.passwordHash());

                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Check password error:" + e.getMessage());
            return false;
        }
    }

    public long getUserId(String name) {
        try {
            try (Connection conn = DatabaseManager.getInstance().getConnection()) {
                try (PreparedStatement stmt = DatabaseManager.getInstance().
                        prepareStatement(FIND_UID_BY_NAME, conn, name)
                ) {
                    ResultSet res = stmt.executeQuery();
                    if (res.next())
                        return res.getLong("user_id");

                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, "Get user by id error:" + e.getMessage());
            return -1;
        }
    }

    public boolean addUser(UserCredentials userCredentials) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            try (PreparedStatement insertStmt = DatabaseManager.getInstance().
                    prepareStatement(ADD_USER, conn, userCredentials.username(),
                            userCredentials.passwordHash())
            ) {
                insertStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Connection error:" + e.getMessage());
            return false;
        }

    }
}
