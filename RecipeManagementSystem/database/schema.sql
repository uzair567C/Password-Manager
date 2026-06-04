-- =====================================================
-- RECIPE MANAGEMENT SYSTEM - COMPLETE DATABASE
-- Run this entire script in DBeaver
-- =====================================================

-- =====================================================
-- DROP ALL EXISTING TABLES (Clean slate)
-- =====================================================
DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS favourites CASCADE;
DROP TABLE IF EXISTS ratings CASCADE;
DROP TABLE IF EXISTS meal_plans CASCADE;
DROP TABLE IF EXISTS recipe_ingredients CASCADE;
DROP TABLE IF EXISTS nutrition CASCADE;
DROP TABLE IF EXISTS ingredients CASCADE;
DROP TABLE IF EXISTS recipes CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;

-- =====================================================
-- CREATE USERS TABLE
-- =====================================================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    calorie_goal INT DEFAULT 2000,
    total_recipes INT DEFAULT 0,
    total_cook_count INT DEFAULT 0,
    theme_preference VARCHAR(20) DEFAULT 'light',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- =====================================================
-- CREATE CATEGORIES TABLE
-- =====================================================
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(30) NOT NULL,
    icon VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CREATE RECIPES TABLE
-- =====================================================
CREATE TABLE recipes (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    prep_time INT NOT NULL DEFAULT 0,
    cook_time INT NOT NULL DEFAULT 0,
    servings INT NOT NULL DEFAULT 4,
    difficulty VARCHAR(20) CHECK (difficulty IN ('Easy', 'Medium', 'Hard')),
    category_id INT REFERENCES categories(id),
    photo_path VARCHAR(500),
    cost DECIMAL(10,2) DEFAULT 0.00,
    cook_count INT DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    is_draft BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CREATE INGREDIENTS TABLE
-- =====================================================
CREATE TABLE ingredients (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    unit VARCHAR(30) NOT NULL,
    unit_price DECIMAL(10,2) DEFAULT 0.00,
    calories_per_unit DECIMAL(10,2) DEFAULT 0.00,
    protein_per_unit DECIMAL(10,2) DEFAULT 0.00,
    carbs_per_unit DECIMAL(10,2) DEFAULT 0.00,
    fat_per_unit DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CREATE RECIPE_INGREDIENTS TABLE (Junction)
-- =====================================================
CREATE TABLE recipe_ingredients (
    recipe_id INT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    ingredient_id INT NOT NULL REFERENCES ingredients(id),
    quantity DECIMAL(10,2) NOT NULL DEFAULT 0,
    unit VARCHAR(30) NOT NULL,
    notes TEXT,
    PRIMARY KEY (recipe_id, ingredient_id)
);

-- =====================================================
-- CREATE NUTRITION TABLE
-- =====================================================
CREATE TABLE nutrition (
    recipe_id INT PRIMARY KEY REFERENCES recipes(id) ON DELETE CASCADE,
    calories INT NOT NULL DEFAULT 0,
    protein DECIMAL(10,2) DEFAULT 0.00,
    carbs DECIMAL(10,2) DEFAULT 0.00,
    fat DECIMAL(10,2) DEFAULT 0.00,
    fiber DECIMAL(10,2) DEFAULT 0.00,
    sugar DECIMAL(10,2) DEFAULT 0.00,
    sodium DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CREATE MEAL_PLANS TABLE
-- =====================================================
CREATE TABLE meal_plans (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipe_id INT NOT NULL REFERENCES recipes(id),
    day_of_week INT NOT NULL,
    meal_type VARCHAR(20) NOT NULL,
    week_start DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, day_of_week, meal_type, week_start)
);

-- =====================================================
-- CREATE RATINGS TABLE
-- =====================================================
CREATE TABLE ratings (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipe_id INT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    stars INT NOT NULL CHECK (stars >= 1 AND stars <= 5),
    review_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, recipe_id)
);

-- =====================================================
-- CREATE FAVOURITES TABLE
-- =====================================================
CREATE TABLE favourites (
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipe_id INT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, recipe_id)
);

-- =====================================================
-- CREATE AUDIT_LOG TABLE
-- =====================================================
CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id INT,
    details JSONB,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- CREATE INDEXES
-- =====================================================
CREATE INDEX idx_recipes_user_id ON recipes(user_id);
CREATE INDEX idx_recipes_title ON recipes(title);
CREATE INDEX idx_recipes_difficulty ON recipes(difficulty);
CREATE INDEX idx_recipes_created_at ON recipes(created_at);
CREATE INDEX idx_meal_plans_user_week ON meal_plans(user_id, week_start);
CREATE INDEX idx_ratings_recipe_id ON ratings(recipe_id);
CREATE INDEX idx_ratings_user_id ON ratings(user_id);
CREATE INDEX idx_favourites_user_id ON favourites(user_id);
CREATE INDEX idx_favourites_recipe_id ON favourites(recipe_id);
CREATE INDEX idx_audit_log_user_id ON audit_log(user_id);
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at);
CREATE INDEX idx_ingredients_name ON ingredients(name);

-- =====================================================
-- INSERT CATEGORIES
-- =====================================================
INSERT INTO categories (name, type) VALUES
('Italian', 'cuisine'),
('Chinese', 'cuisine'),
('Mexican', 'cuisine'),
('Indian', 'cuisine'),
('Japanese', 'cuisine'),
('French', 'cuisine'),
('Thai', 'cuisine'),
('Breakfast', 'meal_type'),
('Lunch', 'meal_type'),
('Dinner', 'meal_type'),
('Snack', 'meal_type'),
('Dessert', 'meal_type'),
('Vegetarian', 'diet'),
('Vegan', 'diet'),
('Gluten-Free', 'diet'),
('Keto', 'diet'),
('Easy', 'difficulty'),
('Medium', 'difficulty'),
('Hard', 'difficulty');

-- =====================================================
-- INSERT USERS (Password for all is 'password123')
-- =====================================================
-- User: asad (id = 1)
INSERT INTO users (username, email, password_hash, full_name, calorie_goal) 
VALUES ('asad', 'asad@recipeapp.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MrI6eQKqXg6YxZxXxXxXxXxXxXxX', 'Asad User', 2000);

-- User: testuser (id = 2)
INSERT INTO users (username, email, password_hash, full_name, calorie_goal) 
VALUES ('testuser', 'test@recipeapp.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MrI6eQKqXg6YxZxXxXxXxXxXxXxX', 'Test User', 2000);

-- =====================================================
-- INSERT INGREDIENTS
-- =====================================================
INSERT INTO ingredients (name, unit, unit_price, calories_per_unit, protein_per_unit, carbs_per_unit, fat_per_unit) VALUES
('Flour', 'g', 0.005, 3.64, 0.10, 0.76, 0.01),
('Sugar', 'g', 0.008, 3.87, 0, 1.00, 0),
('Salt', 'g', 0.002, 0, 0, 0, 0),
('Butter', 'g', 0.015, 7.17, 0.01, 0, 0.81),
('Milk', 'ml', 0.012, 0.42, 0.03, 0.05, 0.02),
('Egg', 'piece', 0.35, 72, 6, 0.5, 5),
('Chicken Breast', 'g', 0.025, 1.65, 0.31, 0, 0.036),
('Rice', 'g', 0.008, 1.30, 0.027, 0.28, 0.003),
('Tomato', 'piece', 0.50, 22, 1, 5, 0.2),
('Onion', 'piece', 0.40, 44, 1, 10, 0.1),
('Garlic', 'clove', 0.10, 4, 0.2, 1, 0),
('Olive Oil', 'tbsp', 0.25, 119, 0, 0, 13.5),
('Pasta', 'g', 0.015, 3.5, 0.12, 0.72, 0.02),
('Cheese', 'g', 0.025, 4.0, 0.25, 0.03, 0.33),
('Beef', 'g', 0.035, 2.5, 0.26, 0, 0.17),
('Potato', 'piece', 0.60, 163, 4.3, 37, 0.2),
('Carrot', 'piece', 0.30, 25, 0.6, 6, 0.1),
('Broccoli', 'g', 0.012, 0.34, 0.03, 0.07, 0.004),
('Bread', 'slice', 0.20, 80, 3, 15, 1),
('Yogurt', 'g', 0.018, 0.61, 0.10, 0.04, 0.03),
('Lemon', 'piece', 0.40, 17, 0.6, 5.4, 0.2),
('Cumin', 'tsp', 0.15, 8, 0.4, 0.9, 0.5),
('Soy Sauce', 'tbsp', 0.12, 10, 1, 1, 0),
('Ginger', 'g', 0.08, 5, 0.1, 1.1, 0.1),
('Bell Pepper', 'piece', 0.80, 31, 1, 6, 0.3),
('Mushroom', 'g', 0.018, 22, 3, 3, 0.3),
('Spinach', 'g', 0.014, 23, 2.9, 3.6, 0.4),
('Honey', 'tbsp', 0.35, 64, 0, 17, 0),
('Water', 'ml', 0, 0, 0, 0, 0);

-- =====================================================
-- INSERT RECIPES (All belong to user_id = 1 which is 'asad')
-- =====================================================
INSERT INTO recipes (user_id, title, description, prep_time, cook_time, servings, difficulty, cost, cook_count, average_rating) VALUES
(1, 'Spaghetti Carbonara', 'Classic Italian pasta dish with eggs, cheese, and pancetta. Creamy and delicious!', 10, 20, 4, 'Medium', 12.50, 25, 4.8),
(1, 'Chicken Tikka Masala', 'Creamy Indian curry with grilled chicken pieces. Perfect with rice or naan.', 20, 30, 4, 'Medium', 15.50, 18, 4.7),
(1, 'Vegetable Fried Rice', 'Quick and easy vegetable fried rice. Perfect for a weeknight dinner!', 10, 15, 4, 'Easy', 8.00, 32, 4.3),
(1, 'Grilled Cheese Sandwich', 'Classic grilled cheese sandwich with buttery bread and melted cheese.', 5, 10, 2, 'Easy', 4.50, 48, 4.5),
(1, 'Fluffy Pancakes', 'Light and fluffy pancakes for breakfast. Serve with maple syrup and fresh berries.', 10, 15, 4, 'Easy', 5.00, 35, 4.7),
(1, 'Pizza Margherita', 'Simple and delicious Neapolitan pizza with fresh tomatoes, mozzarella, and basil.', 30, 15, 2, 'Hard', 12.00, 12, 4.9),
(1, 'Butter Chicken', 'Rich and creamy Indian butter chicken curry. A family favorite!', 25, 35, 6, 'Medium', 18.00, 22, 4.8),
(1, 'Caesar Salad', 'Classic Caesar salad with crispy romaine, croutons, and Parmesan cheese.', 15, 0, 4, 'Easy', 7.50, 28, 4.4),
(1, 'Beef Stir Fry', 'Quick and healthy beef stir fry with vegetables in a savory sauce.', 15, 10, 4, 'Medium', 14.00, 15, 4.6),
(1, 'Chocolate Cake', 'Rich and moist chocolate cake with chocolate frosting. Perfect for celebrations!', 20, 35, 8, 'Hard', 10.00, 20, 4.9),
(1, 'Classic Omelette', 'Fluffy French-style omelette with cheese and herbs.', 5, 5, 1, 'Easy', 3.00, 55, 4.5),
(1, 'Chicken Noodle Soup', 'Comforting homemade chicken noodle soup with vegetables.', 15, 45, 6, 'Easy', 12.00, 20, 4.7);

-- =====================================================
-- INSERT NUTRITION DATA
-- =====================================================
INSERT INTO nutrition (recipe_id, calories, protein, carbs, fat) VALUES
(1, 850, 35, 90, 35),
(2, 650, 42, 35, 28),
(3, 450, 10, 65, 15),
(4, 450, 15, 35, 28),
(5, 380, 10, 55, 12),
(6, 850, 25, 110, 30),
(7, 750, 38, 45, 45),
(8, 350, 12, 20, 25),
(9, 500, 32, 25, 30),
(10, 550, 8, 80, 25),
(11, 250, 18, 2, 19),
(12, 380, 28, 35, 12);

-- =====================================================
-- INSERT RECIPE INGREDIENTS
-- =====================================================
-- Recipe 1: Spaghetti Carbonara
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
(1, (SELECT id FROM ingredients WHERE name = 'Pasta'), 400, 'g'),
(1, (SELECT id FROM ingredients WHERE name = 'Egg'), 2, 'piece'),
(1, (SELECT id FROM ingredients WHERE name = 'Cheese'), 50, 'g'),
(1, (SELECT id FROM ingredients WHERE name = 'Garlic'), 2, 'clove');

-- Recipe 2: Chicken Tikka Masala
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
(2, (SELECT id FROM ingredients WHERE name = 'Chicken Breast'), 500, 'g'),
(2, (SELECT id FROM ingredients WHERE name = 'Onion'), 1, 'piece'),
(2, (SELECT id FROM ingredients WHERE name = 'Garlic'), 3, 'clove'),
(2, (SELECT id FROM ingredients WHERE name = 'Tomato'), 2, 'piece'),
(2, (SELECT id FROM ingredients WHERE name = 'Yogurt'), 150, 'g');

-- Recipe 3: Vegetable Fried Rice
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
(3, (SELECT id FROM ingredients WHERE name = 'Rice'), 300, 'g'),
(3, (SELECT id FROM ingredients WHERE name = 'Onion'), 1, 'piece'),
(3, (SELECT id FROM ingredients WHERE name = 'Carrot'), 1, 'piece'),
(3, (SELECT id FROM ingredients WHERE name = 'Egg'), 2, 'piece');

-- Recipe 4: Grilled Cheese Sandwich
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
(4, (SELECT id FROM ingredients WHERE name = 'Bread'), 4, 'slice'),
(4, (SELECT id FROM ingredients WHERE name = 'Cheese'), 60, 'g'),
(4, (SELECT id FROM ingredients WHERE name = 'Butter'), 20, 'g');

-- Recipe 5: Fluffy Pancakes
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
(5, (SELECT id FROM ingredients WHERE name = 'Flour'), 200, 'g'),
(5, (SELECT id FROM ingredients WHERE name = 'Milk'), 250, 'ml'),
(5, (SELECT id FROM ingredients WHERE name = 'Egg'), 1, 'piece'),
(5, (SELECT id FROM ingredients WHERE name = 'Butter'), 30, 'g'),
(5, (SELECT id FROM ingredients WHERE name = 'Sugar'), 30, 'g');

-- Recipe 6: Pizza Margherita
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
(6, (SELECT id FROM ingredients WHERE name = 'Flour'), 250, 'g'),
(6, (SELECT id FROM ingredients WHERE name = 'Tomato'), 3, 'piece'),
(6, (SELECT id FROM ingredients WHERE name = 'Cheese'), 120, 'g'),
(6, (SELECT id FROM ingredients WHERE name = 'Olive Oil'), 2, 'tbsp');

-- Recipe 7: Butter Chicken
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
(7, (SELECT id FROM ingredients WHERE name = 'Chicken Breast'), 600, 'g'),
(7, (SELECT id FROM ingredients WHERE name = 'Butter'), 50, 'g'),
(7, (SELECT id FROM ingredients WHERE name = 'Onion'), 1, 'piece'),
(7, (SELECT id FROM ingredients WHERE name = 'Garlic'), 4, 'clove'),
(7, (SELECT id FROM ingredients WHERE name = 'Tomato'), 3, 'piece');

-- Recipe 8: Caesar Salad
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
(8, (SELECT id FROM ingredients WHERE name = 'Lemon'), 1, 'piece'),
(8, (SELECT id FROM ingredients WHERE name = 'Cheese'), 30, 'g'),
(8, (SELECT id FROM ingredients WHERE name = 'Garlic'), 1, 'clove'),
(8, (SELECT id FROM ingredients WHERE name = 'Olive Oil'), 3, 'tbsp');

-- Recipe 9: Beef Stir Fry
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
(9, (SELECT id FROM ingredients WHERE name = 'Beef'), 400, 'g'),
(9, (SELECT id FROM ingredients WHERE name = 'Bell Pepper'), 1, 'piece'),
(9, (SELECT id FROM ingredients WHERE name = 'Onion'), 1, 'piece'),
(9, (SELECT id FROM ingredients WHERE name = 'Soy Sauce'), 3, 'tbsp');

-- Recipe 10: Chocolate Cake
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
(10, (SELECT id FROM ingredients WHERE name = 'Flour'), 250, 'g'),
(10, (SELECT id FROM ingredients WHERE name = 'Sugar'), 200, 'g'),
(10, (SELECT id FROM ingredients WHERE name = 'Butter'), 150, 'g'),
(10, (SELECT id FROM ingredients WHERE name = 'Egg'), 3, 'piece'),
(10, (SELECT id FROM ingredients WHERE name = 'Milk'), 100, 'ml');

-- Recipe 11: Classic Omelette
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
(11, (SELECT id FROM ingredients WHERE name = 'Egg'), 3, 'piece'),
(11, (SELECT id FROM ingredients WHERE name = 'Butter'), 15, 'g'),
(11, (SELECT id FROM ingredients WHERE name = 'Cheese'), 30, 'g'),
(11, (SELECT id FROM ingredients WHERE name = 'Milk'), 30, 'ml');

-- Recipe 12: Chicken Noodle Soup
INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
(12, (SELECT id FROM ingredients WHERE name = 'Chicken Breast'), 400, 'g'),
(12, (SELECT id FROM ingredients WHERE name = 'Carrot'), 2, 'piece'),
(12, (SELECT id FROM ingredients WHERE name = 'Onion'), 1, 'piece'),
(12, (SELECT id FROM ingredients WHERE name = 'Garlic'), 3, 'clove'),
(12, (SELECT id FROM ingredients WHERE name = 'Pasta'), 100, 'g');

-- =====================================================
-- INSERT RATINGS
-- =====================================================
INSERT INTO ratings (user_id, recipe_id, stars, review_text) VALUES
(1, 1, 5, 'Absolutely delicious! Best carbonara ever!'),
(1, 2, 5, 'Great flavor, love the creaminess.'),
(1, 3, 4, 'Quick and easy, perfect for weeknights.'),
(1, 4, 5, 'My kids love this sandwich!'),
(1, 5, 5, 'Fluffiest pancakes ever!'),
(1, 6, 5, 'Restaurant quality pizza at home!'),
(1, 7, 5, 'Better than takeout!'),
(1, 8, 4, 'Fresh and delicious salad.'),
(1, 9, 4, 'Very flavorful stir fry.'),
(1, 10, 5, 'Best chocolate cake ever!'),
(1, 11, 4, 'Perfect breakfast omelette.'),
(1, 12, 5, 'So comforting, just like grandma''s.');

-- =====================================================
-- INSERT FAVOURITES
-- =====================================================
INSERT INTO favourites (user_id, recipe_id) VALUES
(1, 1),
(1, 2),
(1, 5),
(1, 7),
(1, 10);

-- =====================================================
-- CREATE UPDATE FUNCTION AND TRIGGERS
-- =====================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_recipes_updated_at ON recipes;
CREATE TRIGGER update_recipes_updated_at BEFORE UPDATE ON recipes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_nutrition_updated_at ON nutrition;
CREATE TRIGGER update_nutrition_updated_at BEFORE UPDATE ON nutrition
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- VERIFY INSTALLATION
-- =====================================================
SELECT 'DATABASE SETUP COMPLETE!' as status;
SELECT 'Total Users: ' || COUNT(*) FROM users;
SELECT 'Total Recipes: ' || COUNT(*) FROM recipes;
SELECT 'Total Ingredients: ' || COUNT(*) FROM ingredients;
SELECT 'Total Ratings: ' || COUNT(*) FROM ratings;