package dao;

import model.Credential;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the credentials table.
 *
 * Responsibilities (Module 2):
 *   - Add / edit / delete credentials
 *   - Retrieve all credentials for a user
 *   - Search by site name or account username
 *   - Filter by priority (High / Medium / Low)
 *   - Toggle favourite flag
 *
 * Every method uses PreparedStatement to prevent SQL injection.
 * Every method returns a clear success/failure signal or result list.
 */
public class CredentialDAO {

    // ─── Shared DB connection ─────────────────────────────────────────────────

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // CREATE
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Inserts a new credential row and sets the generated id back on the object.
     *
     * @param cred  Credential to save (id field is ignored / overwritten)
     * @return      true if the row was inserted successfully
     */
    public boolean addCredential(Credential cred) {
        String sql = """
            INSERT INTO credentials
                (user_id, site, account_username, password, priority, favourite, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = getConn().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt   (1, cred.getUserId());
            ps.setString(2, cred.getSite().trim());
            ps.setString(3, cred.getAccountUsername().trim());
            ps.setString(4, cred.getPassword());
            ps.setString(5, validatePriority(cred.getPriority()));
            ps.setInt   (6, cred.isFavourite() ? 1 : 0);
            ps.setString(7, cred.getCreatedAtFormatted());

            int affected = ps.executeUpdate();

            // Write the auto-generated id back into the object
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        cred.setId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("[CredentialDAO.addCredential] " + e.getMessage());
            return false;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // READ
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Returns ALL credentials belonging to a specific user,
     * ordered: favourites first, then by priority (High → Medium → Low),
     * then alphabetically by site.
     */
    public List<Credential> getAllCredentials(int userId) {
        String sql = """
            SELECT * FROM credentials
            WHERE user_id = ?
            ORDER BY
                favourite DESC,
                CASE priority
                    WHEN 'High'   THEN 1
                    WHEN 'Medium' THEN 2
                    WHEN 'Low'    THEN 3
                    ELSE 4
                END,
                site ASC
        """;

        List<Credential> list = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[CredentialDAO.getAllCredentials] " + e.getMessage());
        }

        return list;
    }

    /**
     * Returns a single credential by its primary key.
     * Returns null if not found or if it does not belong to the given user.
     */
    public Credential getCredentialById(int id, int userId) {
        String sql = "SELECT * FROM credentials WHERE id = ? AND user_id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[CredentialDAO.getCredentialById] " + e.getMessage());
        }

        return null;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SEARCH
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Case-insensitive search across site name AND account username.
     * Supports partial matches (e.g. "goo" matches "google.com").
     *
     * @param userId  Owner of the credentials
     * @param query   Search term
     */
    public List<Credential> searchCredentials(int userId, String query) {
        String sql = """
            SELECT * FROM credentials
            WHERE user_id = ?
              AND (LOWER(site)             LIKE LOWER(?)
                OR LOWER(account_username) LIKE LOWER(?))
            ORDER BY site ASC
        """;

        List<Credential> list = new ArrayList<>();
        String pattern = "%" + query.trim() + "%";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt   (1, userId);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CredentialDAO.searchCredentials] " + e.getMessage());
        }

        return list;
    }

    /**
     * Convenience overload — search only by site name.
     */
    public List<Credential> searchBySite(int userId, String site) {
        String sql = """
            SELECT * FROM credentials
            WHERE user_id = ?
              AND LOWER(site) LIKE LOWER(?)
            ORDER BY site ASC
        """;

        List<Credential> list = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt   (1, userId);
            ps.setString(2, "%" + site.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CredentialDAO.searchBySite] " + e.getMessage());
        }

        return list;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // FILTER
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Returns credentials filtered by priority level.
     *
     * @param priority  "High" | "Medium" | "Low"
     */
    public List<Credential> filterByPriority(int userId, String priority) {
        String sql = """
            SELECT * FROM credentials
            WHERE user_id = ?
              AND priority = ?
            ORDER BY site ASC
        """;

        List<Credential> list = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt   (1, userId);
            ps.setString(2, validatePriority(priority));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CredentialDAO.filterByPriority] " + e.getMessage());
        }

        return list;
    }

    /**
     * Returns only the credentials the user has marked as favourite.
     */
    public List<Credential> getFavourites(int userId) {
        String sql = """
            SELECT * FROM credentials
            WHERE user_id = ?
              AND favourite = 1
            ORDER BY site ASC
        """;

        List<Credential> list = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CredentialDAO.getFavourites] " + e.getMessage());
        }

        return list;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // UPDATE
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Updates all editable fields of an existing credential.
     * The user_id and created_at are never changed.
     *
     * @return true if exactly one row was updated
     */
    public boolean updateCredential(Credential cred) {
        String sql = """
            UPDATE credentials
            SET site             = ?,
                account_username = ?,
                password         = ?,
                priority         = ?,
                favourite        = ?
            WHERE id = ?
              AND user_id = ?
        """;

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, cred.getSite().trim());
            ps.setString(2, cred.getAccountUsername().trim());
            ps.setString(3, cred.getPassword());
            ps.setString(4, validatePriority(cred.getPriority()));
            ps.setInt   (5, cred.isFavourite() ? 1 : 0);
            ps.setInt   (6, cred.getId());
            ps.setInt   (7, cred.getUserId());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("[CredentialDAO.updateCredential] " + e.getMessage());
            return false;
        }
    }

    /**
     * Flips the favourite flag for a single credential.
     *
     * @return the NEW favourite state, or null on failure
     */
    public Boolean toggleFavourite(int id, int userId) {
        Credential cred = getCredentialById(id, userId);
        if (cred == null) return null;

        boolean newState = !cred.isFavourite();
        String sql = "UPDATE credentials SET favourite = ? WHERE id = ? AND user_id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, newState ? 1 : 0);
            ps.setInt(2, id);
            ps.setInt(3, userId);

            if (ps.executeUpdate() == 1) return newState;

        } catch (SQLException e) {
            System.err.println("[CredentialDAO.toggleFavourite] " + e.getMessage());
        }

        return null;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // DELETE
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Deletes a single credential.
     * The user_id check ensures a user cannot delete another user's data.
     *
     * @return true if the row was deleted
     */
    public boolean deleteCredential(int id, int userId) {
        String sql = "DELETE FROM credentials WHERE id = ? AND user_id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("[CredentialDAO.deleteCredential] " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes ALL credentials belonging to a user.
     * Intended for account deletion flows.
     *
     * @return number of rows deleted
     */
    public int deleteAllCredentials(int userId) {
        String sql = "DELETE FROM credentials WHERE user_id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[CredentialDAO.deleteAllCredentials] " + e.getMessage());
            return 0;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // STATS (bonus — useful for the dashboard)
    // ═════════════════════════════════════════════════════════════════════════

    /** Total number of saved credentials for a user. */
    public int countCredentials(int userId) {
        String sql = "SELECT COUNT(*) FROM credentials WHERE user_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[CredentialDAO.countCredentials] " + e.getMessage());
        }
        return 0;
    }

    /** Number of credentials per priority level — returns int[]{high, medium, low} */
    public int[] countByPriority(int userId) {
        String sql = """
            SELECT priority, COUNT(*) AS cnt
            FROM credentials
            WHERE user_id = ?
            GROUP BY priority
        """;

        int[] counts = {0, 0, 0}; // [High, Medium, Low]

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int c = rs.getInt("cnt");
                    switch (rs.getString("priority")) {
                        case "High"   -> counts[0] = c;
                        case "Medium" -> counts[1] = c;
                        case "Low"    -> counts[2] = c;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[CredentialDAO.countByPriority] " + e.getMessage());
        }

        return counts;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Maps a ResultSet row → Credential object.
     * Centralised here so every query uses identical mapping logic.
     */
    private Credential mapRow(ResultSet rs) throws SQLException {
        String rawDate = rs.getString("created_at");
        LocalDateTime dt = rawDate != null
                ? LocalDateTime.parse(rawDate,
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : LocalDateTime.now();

        return new Credential(
            rs.getInt    ("id"),
            rs.getInt    ("user_id"),
            rs.getString ("site"),
            rs.getString ("account_username"),
            rs.getString ("password"),
            rs.getString ("priority"),
            rs.getInt    ("favourite") == 1,
            dt
        );
    }

    /**
     * Guards against invalid priority strings entering the database.
     * Falls back to "Medium" for any unrecognized value.
     */
    private String validatePriority(String priority) {
        if (priority == null) return "Medium";
        return switch (priority.trim()) {
            case "High", "Low" -> priority.trim();
            default            -> "Medium";
        };
    }
}