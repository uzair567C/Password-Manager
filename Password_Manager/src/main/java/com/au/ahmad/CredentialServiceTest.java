package com.au.ahmad;

import java.util.List;

import com.au.ahmad.model.Credential;
import com.au.ahmad.service.CredentialService;
import com.au.ahmad.service.CredentialService.ServiceResult;

/*
Credential Service Tests
+-------------------+------------------------------------------+------------------------------------------------------+
|function           | input                                    | output                                               |
+-------------------+------------------------------------------+------------------------------------------------------+
|addCredential      | int, String, String, String, String      | ServiceResult (success/failure message)              |
|getAllCredentials  | int                                      | List<Credential>                                     |
|search             | int, String                              | List<Credential> (matching site/account)             |
|filterByPriority   | int, String                              | List<Credential> (matching priority)                 |
|updateCredential   | int, int, String, String, String, String | ServiceResult (success/failure message)              |
|toggleFavourite    | int, int                                 | ServiceResult (new favourite status)                 |
|getTotalCredentials| int                                      | int (total count)                                    |
|getPriorityCounts  | int                                      | int[] (counts for High, Medium, Low)                 |
|deleteCredential   | int, int                                 | ServiceResult (success/failure message)              |
+-------------------+------------------------------------------+------------------------------------------------------+

*/

public class CredentialServiceTest {
    

    private static final int TEST_USER_ID = 1;
    private static final CredentialService service = new CredentialService();

    public static void main(String[] args) {
        System.out.println("══════════════════════════════════════════");
        System.out.println("   Module 2 — CredentialService Tests     ");
        System.out.println("══════════════════════════════════════════\n");

        testAdd();
        testGetAll();
        testSearch();
        testFilterByPriority();
        testUpdate();
        testFavouriteToggle();
        testStats();
        testDelete();
        testValidation();
    }

    static void testAdd() {
        header("ADD CREDENTIALS");
        print(service.addCredential(TEST_USER_ID, "google.com",  "example@gmail.com", "G00gle#2024",  "High"));
        print(service.addCredential(TEST_USER_ID, "github.com",  "example_dev",       "GitH@b$ecure", "High"));
        print(service.addCredential(TEST_USER_ID, "netflix.com", "example@gmail.com", "N3tflix!Pass", "Medium"));
        print(service.addCredential(TEST_USER_ID, "reddit.com",  "example_redditor",  "R3dditPass",   "Low"));
    }

    static void testGetAll() {
        header("GET ALL");
        service.getAllCredentials(TEST_USER_ID)
               .forEach(c -> System.out.printf("  [%d] %-15s | %-25s | %s%n",
                       c.getId(), c.getSite(), c.getAccountUsername(), c.getPriority()));
    }

    static void testSearch() {
        header("SEARCH 'git'");
        service.search(TEST_USER_ID, "git")
               .forEach(c -> System.out.println("  Found: " + c.getSite()));
    }

    static void testFilterByPriority() {
        header("FILTER High");
        service.filterByPriority(TEST_USER_ID, "High")
               .forEach(c -> System.out.println("  " + c.getSite()));
    }

    static void testUpdate() {
        header("UPDATE");
        List<Credential> list = service.getAllCredentials(TEST_USER_ID);
        if (list.isEmpty()) return;
        Credential first = list.get(0);
        print(service.updateCredential(first.getId(), TEST_USER_ID,
              first.getSite(), first.getAccountUsername(), "N3wP@ss!", "High"));
    }

    static void testFavouriteToggle() {
        header("TOGGLE FAVOURITE");
        List<Credential> list = service.getAllCredentials(TEST_USER_ID);
        if (list.isEmpty()) return;
        print(service.toggleFavourite(list.get(0).getId(), TEST_USER_ID));
    }

    static void testStats() {
        header("STATS");
        System.out.println("  Total: " + service.getTotalCredentials(TEST_USER_ID));
        int[] c = service.getPriorityCounts(TEST_USER_ID);
        System.out.printf("  High=%d  Medium=%d  Low=%d%n", c[0], c[1], c[2]);
    }

    static void testDelete() {
        header("DELETE");
        List<Credential> list = service.getAllCredentials(TEST_USER_ID);
        if (list.isEmpty()) return;
        Credential last = list.get(list.size() - 1);
        print(service.deleteCredential(last.getId(), TEST_USER_ID));
    }

    static void testValidation() {
        header("VALIDATION (all should FAIL)");
        print(service.addCredential(TEST_USER_ID, "",      "user", "pass", "High"));
        print(service.addCredential(TEST_USER_ID, "x.com", "",     "pass", "High"));
        print(service.addCredential(TEST_USER_ID, "x.com", "user", "",     "High"));
    }

    static void header(String t) {
        System.out.println("\n── " + t + " " + "─".repeat(Math.max(0, 42 - t.length())));
    }

    static void print(ServiceResult r) { System.out.println("  " + r); }
}