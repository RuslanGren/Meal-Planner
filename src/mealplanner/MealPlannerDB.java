package mealplanner;

import java.sql.*;

public class MealPlannerDB {
    private Connection connection;
    public MealPlannerDB() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql:meals_db", "postgres", "1111");
            connection.setAutoCommit(true);
            createTables();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createTables() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS meals(category VARCHAR(64), meal VARCHAR(64), meal_id INTEGER);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ingredients(ingredient VARCHAR(1024), ingredient_id INT, meal_id INTEGER);");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addMeal(String category, String meal, int meal_id) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(String.format("INSERT INTO meals(category, meal, meal_id) VALUES ('%s', '%s', %d);", category, meal, meal_id));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addIngredients(String ingredient, int ingredient_id, int meal_id) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(String.format("INSERT INTO ingredients(ingredient, ingredient_id, meal_id) VALUES ('%s', %d, %d);", ingredient, ingredient_id, meal_id));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showMeals() {
        try {
            Statement statement = connection.createStatement();
            Statement statement2 = connection.createStatement();
            ResultSet rsMeal = statement.executeQuery("SELECT * FROM meals;");
            while(rsMeal.next()) {
                System.out.println("Category: " + rsMeal.getString("category"));
                System.out.println("Name: " + rsMeal.getString("meal"));
                System.out.println("Ingredients:");
                ResultSet rsIngredients = statement2.executeQuery(String.format("SELECT * FROM ingredients WHERE meal_id = %d;", rsMeal.getInt("meal_id")));
                while(rsIngredients.next()) {
                    System.out.println(rsIngredients.getString("ingredient").strip());
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isEmpty() {
        try {
            Statement statement = connection.createStatement();
            int numberOfRow;
            ResultSet checkIsEmpty = statement.executeQuery("SELECT COUNT(*) FROM meals");
            checkIsEmpty.next();
            numberOfRow = checkIsEmpty.getInt(1);
            return numberOfRow == 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}