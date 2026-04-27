package com.au.ahmad.service;

import java.util.List;

import com.au.ahmad.dao.CredentialDAO;
import com.au.ahmad.model.Credential;

/* 
Credential Service
+-----------------+------------------------------------------+------------------------------------------------------+
|function         | input                                    | output                                               |
+-----------------+------------------------------------------+------------------------------------------------------+
|addCredential    | int, String, String, String, String      | ServiceResult (success/failure message)              |
|getAllCredentials| int                                      | List<Credential>                                     |
|search           | int, String                              | List<Credential> (matching site/account)             |
|filterByPriority | int, String                              | List<Credential> (matching priority)                 |
|updateCredential | int, int, String, String, String, String | ServiceResult                                        |
|deleteCredential | int, int                                 | ServiceResult                                        |
+-----------------+------------------------------------------+------------------------------------------------------+
*/

public class CredentialService {

    private final CredentialDAO credentialDAO;

    public CredentialService() {
        this.credentialDAO = new CredentialDAO();
    }

    public CredentialService(CredentialDAO credentialDAO) {
        this.credentialDAO = credentialDAO;
    }

    public ServiceResult addCredential(int userId, String site,
                                       String accountUsername, String password,
                                       String priority) {
        if (isBlank(site))            return ServiceResult.fail("Site name cannot be empty.");
        if (isBlank(accountUsername)) return ServiceResult.fail("Username cannot be empty.");
        if (isBlank(password))        return ServiceResult.fail("Password cannot be empty.");
        if (site.length() > 200)      return ServiceResult.fail("Site name is too long (max 200 chars).");

        Credential cred = new Credential(userId, site.trim(),
                                         accountUsername.trim(), password, priority);
        boolean ok = credentialDAO.addCredential(cred);

        return ok
            ? ServiceResult.ok("Credential saved successfully.")
            : ServiceResult.fail("Failed to save credential. Please try again.");
    }

    public List<Credential> getAllCredentials(int userId) {
        return credentialDAO.getAllCredentials(userId);
    }

    public ServiceResult updateCredential(int credId, int userId,
                                          String site, String accountUsername,
                                          String password, String priority) {
        if (isBlank(site))            return ServiceResult.fail("Site name cannot be empty.");
        if (isBlank(accountUsername)) return ServiceResult.fail("Username cannot be empty.");
        if (isBlank(password))        return ServiceResult.fail("Password cannot be empty.");

        Credential existing = credentialDAO.getCredentialById(credId, userId);
        if (existing == null)
            return ServiceResult.fail("Credential not found or access denied.");

        existing.setSite(site.trim());
        existing.setAccountUsername(accountUsername.trim());
        existing.setPassword(password);
        existing.setPriority(priority);

        boolean ok = credentialDAO.updateCredential(existing);
        return ok
            ? ServiceResult.ok("Credential updated successfully.")
            : ServiceResult.fail("Update failed. Please try again.");
    }

    public ServiceResult deleteCredential(int credId, int userId) {
        Credential existing = credentialDAO.getCredentialById(credId, userId);
        if (existing == null)
            return ServiceResult.fail("Credential not found or access denied.");

        boolean ok = credentialDAO.deleteCredential(credId, userId);
        return ok
            ? ServiceResult.ok("Credential deleted.")
            : ServiceResult.fail("Delete failed. Please try again.");
    }

    public List<Credential> search(int userId, String query) {
        if (isBlank(query)) return credentialDAO.getAllCredentials(userId);
        return credentialDAO.searchCredentials(userId, query);
    }

    public List<Credential> filterByPriority(int userId, String priority) {
        if (priority == null || priority.equalsIgnoreCase("All"))
            return credentialDAO.getAllCredentials(userId);
        return credentialDAO.filterByPriority(userId, priority);
    }

    public List<Credential> getFavourites(int userId) {
        return credentialDAO.getFavourites(userId);
    }

    public ServiceResult toggleFavourite(int credId, int userId) {
        Boolean newState = credentialDAO.toggleFavourite(credId, userId);
        if (newState == null)
            return ServiceResult.fail("Could not update favourite status.");
        String msg = newState ? "Added to favourites." : "Removed from favourites.";
        return ServiceResult.ok(msg);
    }

    public int getTotalCredentials(int userId) {
        return credentialDAO.countCredentials(userId);
    }

    public int[] getPriorityCounts(int userId) {
        return credentialDAO.countByPriority(userId);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static class ServiceResult {
        private final boolean success;
        private final String  message;

        private ServiceResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public static ServiceResult ok(String message)   { return new ServiceResult(true,  message); }
        public static ServiceResult fail(String message) { return new ServiceResult(false, message); }

        public boolean isSuccess()  { return success; }
        public String  getMessage() { return message; }

        @Override
        public String toString() {
            return (success ? "[OK] " : "[FAIL] ") + message;
        }
    }
}