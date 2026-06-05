# 🗃️ Recipe & Password Manager Workspace

[![Java Version](https://shields.io)](https://oracle.com)
[![Build Tool](https://shields.io)](https://apache.org)
[![Database](https://shields.io)](https://postgresql.org)

Welcome to the unified development workspace. This repository hosts two independent desktop applications designed to manage user credentials securely and organize culinary data efficiently.

---

## 📂 Repository Structure

The root directory acts as a workspace housing two primary standalone sub-projects:

```text
Recipe_Manager/
├── Password_Manager/          # Java desktop credentials manager tool
│   ├── src/main/java/         # Authentication dashboards & encryption generators
│   └── pom.xml                # Dependency tree for encryption layers
├── RecipeManagementSystem/    # Advanced culinary & meal planning platform
│   ├── src/main/java/         # MVC architecture, DAO layers, UI components
│   ├── database/schema.sql    # Relational database setup migrations
│   └── pom.xml                # Dependency tree for application UI & utilities
└── password_manager.db        # Local persistence engine file for the Password tool
```

---

## 🛡️ 1. Password Manager Application

Located inside the `Password_Manager/` directory, this tool provides local security vaults to generate and store sensitive user records.

### Core Modules
* **🔑 Password Generator** (`passwordgenrator.java`): Automated algorithm tool designed to output cryptographically strong strings based on custom parameter criteria.
* **🗄️ Credential Service & DAO** (`CredentialDAO.java`): Clean abstract interface driving CRUD queries over the local SQLite `password_manager.db` runtime file.
* **🖥️ Split Desktop View** (`loginPage.java` & `dashboard.java`): Modern GUI dashboard views detailing access entries and categorical metadata logs.

### Compilation
```bash
cd Password_Manager
mvn clean package
```

---

## 🍳 2. Recipe Management System

Located inside the `RecipeManagementSystem/` directory, this application implements a complete multi-tier desktop platform layout utilizing Swing/AWT and PostgreSQL.

### Key Features
* **🔐 Secure Auth Boundary** – Active session tracking hooks via strict character parameters (`PasswordValidator`) and token validation layers (`SessionManager`).
* **📊 Live Metric Charts** – Real-time computation modules tracking macro targets, pricing variations, and kitchen usage tallies.
* **📅 Week Calendar Planner** – Complete 7-day grid tracking user menus against strict caloric goal caps.
* **🛒 Grocery Checklist** – Compiles multiple planned lists into a combined grocery list with unit standardizing engines (`UnitConverter`).

### Setup and Migration
1. Set up your target database tables using your management console tool of choice:
   ```bash
   psql -U username -d database_name -f RecipeManagementSystem/database/schema.sql
   ```
2. Update connection keys located inside the properties bundle directory (`RecipeManagementSystem/src/main/resources/properties/database.properties`).
3. Compile and execute:
   ```bash
   cd RecipeManagementSystem
   mvn clean package
   java -jar target/RecipeManagementSystem-1.0-SNAPSHOT.jar
   ```

---

## 🛠️ Global Development Checklist

* **IDE Setup**: If processing with **VS Code**, the root context parameters are automatically configured via the tracking configuration object inside `RecipeManagementSystem/.vscode/settings.json`.
* **Exclusions**: Binary assets (`.jpg`, `.png`), `.class` targets, and internal runtime files are ignored via `.gitignore` tracking paths.

