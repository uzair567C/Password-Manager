# 🍳 Recipe Management System

[![Java Version](https://shields.io)](https://oracle.com)
[![Build Tool](https://shields.io)](https://apache.org)
[![Database](https://shields.io)](https://postgresql.org)
[![UI Framework](https://shields.io)](https://oracle.com)

A rich, modern Java Swing desktop application designed for comprehensive recipe tracking, menu creation, automated grocery generation, and nutritional metric breakdown. Equipped with a dynamic dashboard, clean tiered interface components, custom event logging, and full offline persistence.

---

## 🚀 Key Features

* **🔐 Custom Authentication Engine** – Secured credential storage via strict validation parameters (`PasswordValidator`), session tokens, and local cache controls (`SessionManager`).
* **📊 Analytics Dashboard** – High-level insight engines detailing macro counters, most cooked list totals, user target goals, and automated item aggregations.
* **📅 Week Menu Planner** – Full 7-day visual calendar grid mapping meals directly to calorie target allowances with real-time feedback thresholds.
* **🛒 Smart Grocery Generator** – Translates scheduled weekly recipe quantities into clean, aggregated physical checklists using intelligent unit normalization engines.
* **🏷️ Complex Filtering System** – On-the-fly dashboard search queries processing text syntax, difficulty rankings, step times, and structural category indices simultaneously.
* **🎨 Modern UI Component Suite** – Custom UI components, featuring dynamic toast alerts (`ToastNotifier`), customizable vector feedback layers (`StarRatingWidget`), and dynamic system theme switching.

---

## 🛠️ System Architecture

The application implements a clean **Model-View-Controller / Data Access Object (DAO)** multi-tiered architecture layout:

```text
com.recipe.
├── auth       # Session security layers and validation engines
├── dao        # High-performance persistence interfaces (User, Recipe, Rating, MealPlan)
├── database   # Connection pooling and basic schema drivers
├── exceptions # Strongly typed custom platform handling models
├── gui        # Hierarchical Swing layout modules (Components, MealPlan, Recipe, Auth)
├── models     # Core structural POJOs and transactional state mappings
├── services   # Business rules, optimization logic, and filter calculations
└── utils      # Functional toolchains (PDF Exporting, Scaling, Unit Conversion)
```

---

## 🗄️ Database Design

The relational mapping strategy is engineered for speed, clean constraints, and absolute referential integrity. 

### Core Schema (`database/schema.sql`) Overview:
* **`users`**: Manages credential tables, theme configuration variables, and core physical calorie limits.
* **`recipes`**: Handles full tracking arrays, metrics, time metrics, and relational user properties.
* **`ingredients`**: Absolute inventory dictionary holding base pricing, scaling units, and explicit core macronutrients (Protein, Carbs, Fat, Calories).
* **`recipe_ingredients`**: High-performance intersection grid preserving specialized metrics and processing scaling equations accurately.
* **`meal_plans` / `ratings` / `favourites`**: Independent transactional tracking arrays building relational tables for cross-module functionality.

---

## 📦 Prerequisites & Installation

### Requirements
* **Java Development Kit (JDK):** Version 17 or higher
* **Apache Maven:** Version 3.8+
* **Database Engine:** PostgreSQL or similar RDBMS compatible with the compiled SQL schema.

### 1. Database Provisioning
Run the provided migration layout using your choice of client tool (e.g., DBeaver):
```bash
psql -U your_user -d your_database -f database/schema.sql
```

### 2. Configuration Settings
Update the global runtime configurations inside the properties array directory (`src/main/resources/properties/`):
```properties
# database.properties
db.url=jdbc:postgresql://localhost:5432/recipe_db
db.username=your_username
db.password=your_password
```

### 3. Compilation & Build Execution
Package the project executable with Maven clean operations:
```bash
mvn clean package
```

### 4. Running the Application
Execute the compiled target archive to start the main UI screen window loop:
```bash
java -jar target/RecipeManagementSystem-1.0-SNAPSHOT.jar
```

---

## 📄 Documentation Manifest

Refer to these Markdown guides located inside the repository root for advanced structural specifications:
* 📘 `QUICK_START.md` – Absolute baseline user onboarding manual.