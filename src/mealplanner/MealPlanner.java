package mealplanner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MealPlanner {
    private final Scanner scanner = new Scanner(System.in);
    private boolean flag = true;
    private final MealPlannerDB db = new MealPlannerDB();
    protected static final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    protected static final String[] categories = {"Breakfast", "Lunch", "Dinner"};

    public void start() {
        while(flag) {
            System.out.println("What would you like to do (add, show, plan, save, exit)?");
            String action = scanner.nextLine();
            switch (action) {
                case ("add") -> add();
                case ("show") -> show();
                case ("plan") -> plan();
                case ("save") -> save();
                case ("exit") -> flag = false;
            }
        }
        System.out.println("Bye!");
    }

    private void add() {
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        String category = inputCategory();
        System.out.println("Input the meal's name:");
        String meal = scanner.nextLine();
        while(availableInput(new String[]{meal})) {
            meal = scanner.nextLine();
        }
        int meal_id = db.getLastMealId() + 1;
        db.addMeal(category, meal, meal_id);
        System.out.println("Input the ingredients:");
        String[] ingredients = scanner.nextLine().split(",");
        while(availableInput(ingredients)) {
            ingredients = scanner.nextLine().split(",");
        }
        for (int i = 0; i < ingredients.length; i++) {
            db.addIngredients(ingredients[i], i, meal_id);
        }
        System.out.println("The meal has been added!");
    }

    private void show() {
        if (db.isEmpty(";")) {
            System.out.println("No meals saved. Add a meal first.");
            return;
        }
        System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
        String category = inputCategory();
        if (db.isEmpty(String.format("WHERE category = '%s'", category))) {
            System.out.println("No meals found.");
            return;
        }
        System.out.println("Category: " + category);
        db.showMeals(category);
    }

    private void plan() {
        for (String day : days) {
            System.out.println(day);
            for (String category : categories) {
                ArrayList<String> listOfMeals = db.listOfMeals(category.toLowerCase());
                for (String meal : listOfMeals) {
                    System.out.println(meal);
                }
                System.out.printf("Choose the %s for %s from the list above:\n", category, day);
                String meal = scanner.nextLine();
                while(!listOfMeals.contains(meal)) {
                    System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                    meal = scanner.nextLine();
                }
                db.addPlan(day, category, meal);
            }
            System.out.printf("Yeah! We planned the meals for %s.", day);
            System.out.println();
        }
        db.showPlan();
    }

    private void save() {
        ArrayList<Integer> meals_id = db.getListMealIdFromPlan();
        if (meals_id.isEmpty()) {
            System.out.println("Unable to save. Plan your meals first.");
            return;
        }
        ArrayList<String> ingredients = db.getListOfIngredientsByMealId(meals_id);
        System.out.println("Input a filename:");
        String filename = scanner.nextLine();
        // count each ingredient
        Map<String, Integer> ingredientCount = new HashMap<>();
        for (String ingredient : ingredients) {
            if (ingredientCount.containsKey(ingredient)) {
                int count = ingredientCount.get(ingredient);
                ingredientCount.put(ingredient, count + 1);
            } else {
                ingredientCount.put(ingredient, 1);
            }
        }
        // write in the file
        try (FileWriter writer = new FileWriter(filename)) {
            for (Map.Entry<String, Integer> entry : ingredientCount.entrySet()) {
                if (entry.getValue() > 1) {
                    writer.write(entry.getKey() + " x" + entry.getValue() + "\n");
                } else {
                    writer.write(entry.getKey() + "\n");
                }
            }
            System.out.println("Saved!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean availableInput(String[] ingredients){
        for (String str : ingredients) {
            str = str.replaceAll("\\s","");
            if(!str.matches("[a-zA-Z]+")) {
                System.out.println("Wrong format. Use letters only!");
                return true;
            }
        }
        return false;
    }

    private String inputCategory() {
        String category = scanner.nextLine();
        while(!category.equalsIgnoreCase("breakfast")
                && !category.equalsIgnoreCase("lunch")
                && !category.equalsIgnoreCase("dinner")) {
            System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            category = scanner.nextLine();
        }
        return category;
    }
}