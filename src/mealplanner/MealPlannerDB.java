package mealplanner;

import java.sql.*;
import java.util.ArrayList;

public class MealPlannerDB {
    private Connection connection;
    private Statement statement;

    public MealPlannerDB() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql:meals_db", "postgres", "1111");
            connection.setAutoCommit(true);
            statement = connection.createStatement();
            createTables();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createTables() {
        try {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS meals(category VARCHAR(64), meal VARCHAR(64), meal_id INT);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ingredients(ingredient VARCHAR(64), ingredient_id INT, meal_id INT);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS plan(day VARCHAR(64), category VARCHAR(64), meal VARCHAR(64));");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void addPlan(String day, String category, String meal) {
        try {
            statement.executeUpdate(String.format("INSERT INTO plan(day, category, meal) VALUES ('%s', '%s', '%s');", day, category, meal));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void showPlan() {
        try {
            for (String day : MealPlanner.days) {
                System.out.println();
                System.out.println(day);
                for (String category : MealPlanner.categories) {
                    ResultSet rs = statement.executeQuery(String.format("SELECT meal FROM plan WHERE day = '%s' AND category = '%s';", day, category));
                    if (rs.next()) {
                        System.out.printf("%s: %s\n", category, rs.getString("meal"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected ArrayList<Integer> getListMealIdFromPlan() {
        ArrayList<Integer> list = new ArrayList<>();
        try {
            Statement statement1 = connection.createStatement();
            ResultSet rs = statement1.executeQuery("SELECT meal FROM plan;");
            while (rs.next()) {
                list.add(getMealIdByName(rs.getString("meal")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    protected void addMeal(String category, String meal, int meal_id) {
        try {
            statement.executeUpdate(String.format("INSERT INTO meals(category, meal, meal_id) VALUES ('%s', '%s', %d);", category, meal, meal_id));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void addIngredients(String ingredient, int ingredient_id, int meal_id) {
        try {
            statement.executeUpdate(String.format("INSERT INTO ingredients(ingredient, ingredient_id, meal_id) VALUES ('%s', %d, %d);", ingredient, ingredient_id, meal_id));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void showMeals(String category) {
        try {
            Statement statement2 = connection.createStatement();
            ResultSet rsMeal = statement.executeQuery(String.format("SELECT meal, meal_id FROM meals WHERE category = '%s' ", category));
            while(rsMeal.next()) {
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

    protected ArrayList<String> listOfMeals(String category) {
        ArrayList<String> list = new ArrayList<>();
        try {
            ResultSet resultSet = statement.executeQuery(String.format("SELECT meal FROM meals WHERE category = '%s' ORDER BY meal;", category));
            while(resultSet.next()) {
                list.add(resultSet.getString("meal"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    protected boolean isEmpty(String query) {
        try {
            ResultSet checkIsEmpty = statement.executeQuery(String.format("SELECT COUNT(*) FROM meals %s", query));
            checkIsEmpty.next();
            int numberOfRow = checkIsEmpty.getInt(1);
            return numberOfRow == 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected int getLastMealId() {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM meals ORDER BY meal_id DESC LIMIT 1;");
            if (resultSet.next()) {
                return resultSet.getInt("meal_id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    protected int getMealIdByName(String meal) {
        try {
            ResultSet resultSet = statement.executeQuery(String.format("SELECT meal_id FROM meals WHERE meal = '%s';", meal));
            if (resultSet.next()) {
                return resultSet.getInt("meal_id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    protected ArrayList<String> getListOfIngredientsByMealId(ArrayList<Integer> meals_id) {
        ArrayList<String> ingredients = new ArrayList<>();
        try {
            for (int meal_id : meals_id) {
                ResultSet rs = statement.executeQuery(String.format("SELECT * FROM ingredients WHERE meal_id = %d", meal_id));
                while (rs.next()) {
                    ingredients.add(rs.getString("ingredient"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ingredients;
    }
}