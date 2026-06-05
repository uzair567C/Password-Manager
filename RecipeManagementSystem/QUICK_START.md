# Recipe Manager - Professional Edition
## Quick Start Guide

### 🚀 Getting Started (5 minutes)

#### Prerequisites
- Java 26 or later
- PostgreSQL database
- Maven 3.6+

#### Step 1: Set Environment Variables
```bash
# Linux/Mac
export DB_PASSWORD=your_password
export DB_USER=postgres
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=recipe_management

# Windows (Command Prompt)
set DB_PASSWORD=your_password
set DB_USER=postgres
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=recipe_management

# Windows (PowerShell)
$env:DB_PASSWORD="your_password"
$env:DB_USER="postgres"
$env:DB_HOST="localhost"
$env:DB_PORT="5432"
$env:DB_NAME="recipe_management"
```

#### Step 2: Build the Project
```bash
cd RecipeManagementSystem
mvn clean compile
```

#### Step 3: Run the Application
```bash
mvn exec:java -Dexec.mainClass="com.recipe.Main"
```

---

### 🎯 Features Overview

**Dashboard**
- View total recipes and statistics
- Track cooking history
- See top-rated and most-cooked recipes
- Weekly meal plan overview

**My Recipes**
- Create and edit recipes
- Organize by categories
- Add ingredients and nutrition info
- Upload recipe photos
- Rate and review your recipes

**Meal Planner**
- Plan meals for the week
- Drag-and-drop recipe assignment
- Generate grocery lists
- Export lists to PDF

**Search & Discover**
- Search recipes by name/category
- Filter by difficulty level
- Sort by rating, cook time, cost
- Discover popular recipes

**Nutrition Tracking**
- Track daily calorie intake
- Monitor macronutrients (protein, carbs, fat)
- View weekly nutrition trends
- Set personal nutrition goals

**My Profile**
- Update personal information
- Change password
- Theme preference (Dark/Light)
- Account settings

---

### 🎨 UI Features

**Professional Design:**
- Modern flat design with FlatLaf
- Smooth dark/light theme toggle
- Responsive layouts
- Intuitive navigation

**Performance:**
- Background loading with progress indicators
- Smooth animations
- Optimized database queries
- Connection pooling with HikariCP

**Security:**
- BCrypt password hashing
- Session management
- SQL injection prevention
- Credentials in environment variables

---

### 🐛 Troubleshooting

**Issue: "Cannot connect to database"**
```
Solution:
1. Verify PostgreSQL is running: sudo systemctl status postgresql
2. Check credentials: psql -U postgres -h localhost
3. Verify DB_PASSWORD is set correctly
4. Check database exists: psql -U postgres -c "\\l" | grep recipe_management
```

**Issue: "Java version mismatch"**
```
Solution:
1. Check Java version: java -version
2. Update to Java 26+: https://www.oracle.com/java/technologies/downloads/
3. Update JAVA_HOME: export JAVA_HOME=/path/to/java26
```

**Issue: "Image files not loading"**
```
Solution:
1. Create images directory: mkdir -p images/recipes
2. Ensure write permissions: chmod 755 images/recipes
3. Check image paths in database
```

**Issue: "Application is slow"**
```
Solution:
1. Increase JVM heap: java -Xmx2g -jar app.jar
2. Check database indexes: SELECT * FROM pg_indexes WHERE tablename='recipes';
3. Monitor CPU/Memory: top or Activity Monitor
```

---

### 📚 Database Schema

**Tables:**
- `users` - User accounts
- `recipes` - Recipe details
- `ingredients` - Ingredient definitions
- `recipe_ingredients` - Recipe-ingredient mappings
- `nutrition` - Nutrition information
- `ratings` - User ratings
- `meal_plans` - Weekly meal plans
- `user_meals` - Assigned meals

---

### 🔧 Development Commands

```bash
# Build only
mvn clean compile

# Build and test
mvn clean test

# Create JAR package
mvn clean package

# Run with specific main class
mvn exec:java -Dexec.mainClass="com.recipe.Main"

# Clean build artifacts
mvn clean

# Run with Maven plugin
mvn -Dorg.slf4j.simpleLogger.defaultLogLevel=info exec:java
```

---

### 📁 Project Structure

```
RecipeManagementSystem/
├── src/
│   └── main/
│       ├── java/com/recipe/
│       │   ├── Main.java                 # Application entry point
│       │   ├── auth/                     # Authentication classes
│       │   ├── dao/                      # Database access objects
│       │   ├── database/                 # Database connection
│       │   ├── gui/                      # GUI components
│       │   │   ├── MainWindow.java       # Main application window
│       │   │   ├── ThemeManager.java     # Theme management
│       │   │   ├── auth/                 # Authentication screens
│       │   │   ├── components/           # Reusable UI components
│       │   │   ├── recipe/               # Recipe screens
│       │   │   ├── mealplan/             # Meal planning screens
│       │   │   └── search/               # Search/dashboard screens
│       │   ├── models/                   # Data models
│       │   ├── services/                 # Business logic
│       │   ├── utils/                    # Utility classes
│       │   └── exceptions/               # Custom exceptions
│       └── resources/
│           ├── META-INF/
│           │   └── MANIFEST.MF
│           └── properties/               # Configuration files
├── database/
│   └── schema.sql                        # Database schema
├── images/
│   └── recipes/                          # Recipe images
├── pom.xml                               # Maven configuration
└── IMPROVEMENTS_AND_DEPLOYMENT.md        # This document
```

---

### 💡 Best Practices

**When Creating Recipes:**
1. Use descriptive titles (e.g., "Lemon Garlic Pasta" not "Pasta")
2. Add accurate prep/cook times
3. Include all ingredients with quantities
4. Add nutritional information
5. Upload high-quality photos
6. Use consistent difficulty levels

**When Planning Meals:**
1. Plan for the full week
2. Balance nutrition across days
3. Include variety of cuisines
4. Consider ingredient availability
5. Review grocery list before shopping

**When Searching:**
1. Use specific keywords
2. Filter by difficulty for your skill level
3. Sort by rating to find popular recipes
4. Check ingredient availability
5. Review cook time vs your schedule

---

### 🆘 Getting Help

**Common Questions:**
- Q: How do I add a recipe?
  A: Click "My Recipes" → "Add New Recipe" → Fill in details

- Q: How do I share recipes with others?
  A: Currently recipes are personal. Consider using "Search & Discover" to find others' recipes

- Q: Can I export my recipes?
  A: You can generate grocery lists and meal plans. Recipe export coming soon.

- Q: How do I backup my data?
  A: Backup the PostgreSQL database using pg_dump

---

### 📞 Support Resources

- **Documentation:** See IMPROVEMENTS_AND_DEPLOYMENT.md
- **Issue Reporting:** Check logs in target/logs/
- **Database Issues:** Verify PostgreSQL connection and credentials
- **Performance Issues:** Check system resources and database indexes

---

**Version:** 1.0 - Professional Edition
**Last Updated:** June 5, 2026
**Status:** Production Ready ✅
