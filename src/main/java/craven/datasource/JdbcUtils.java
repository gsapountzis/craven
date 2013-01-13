package craven.datasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcUtils {

    private static final Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

    // -- Handle null for primitives

    private static <T> T wasNull(ResultSet rs, T value) throws SQLException {
        if (rs.wasNull()) {
            return null;
        }
        else {
            return value;
        }
    }

    public static Boolean getOptionalBoolean(ResultSet rs, String label) throws SQLException {
        boolean value = rs.getBoolean(label);
        return wasNull(rs, value);
    }

    public static Byte getOptionalByte(ResultSet rs, String label) throws SQLException {
        byte value = rs.getByte(label);
        return wasNull(rs, value);
    }

    public static Short getOptionalShort(ResultSet rs, String label) throws SQLException {
        short value = rs.getShort(label);
        return wasNull(rs, value);
    }

    public static Integer getOptionalInt(ResultSet rs, String label) throws SQLException {
        int value = rs.getInt(label);
        return wasNull(rs, value);
    }

    public static Long getOptionalLong(ResultSet rs, String label) throws SQLException {
        long value = rs.getLong(label);
        return wasNull(rs, value);
    }

    public static Float getOptionalFloat(ResultSet rs, String label) throws SQLException {
        float value = rs.getFloat(label);
        return wasNull(rs, value);
    }

    public static Double getOptionalDouble(ResultSet rs, String label) throws SQLException {
        double value = rs.getDouble(label);
        return wasNull(rs, value);
    }

    // -- Close resources

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException ex) {
                logger.debug("Could not close JDBC Connection", ex);
            }
            catch (Throwable ex) {
                // We don't trust the JDBC driver: It might throw RuntimeException or Error.
                logger.debug("Unexpected exception on closing JDBC Connection", ex);
            }
        }
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (SQLException ex) {
                logger.trace("Could not close JDBC Statement", ex);
            }
            catch (Throwable ex) {
                // We don't trust the JDBC driver: It might throw RuntimeException or Error.
                logger.trace("Unexpected exception on closing JDBC Statement", ex);
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            }
            catch (SQLException ex) {
                logger.trace("Could not close JDBC ResultSet", ex);
            }
            catch (Throwable ex) {
                // We don't trust the JDBC driver: It might throw RuntimeException or Error.
                logger.trace("Unexpected exception on closing JDBC ResultSet", ex);
            }
        }
    }

}
