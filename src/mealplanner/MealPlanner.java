package mealplanner;

import java.util.*;

public class MealPlanner {
    private final Scanner scanner = new Scanner(System.in);
    private boolean flag = true;
    private final MealPlannerDB db = new MealPlannerDB();
    private int meal_id = 0;
    public void start() {
        while(flag) {
            System.out.println("What would you like to do (add, show, exit)?");
            String action = scanner.nextLine();
            switch (action) {
                case ("add") -> add();
                case ("show") -> show();
                case ("exit") -> flag = false;
            }
        }
        System.out.println("Bye!");
    }

    private void add() {
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        String category = scanner.nextLine();
        while(!category.equalsIgnoreCase("breakfast")
                && !category.equalsIgnoreCase("lunch")
                && !category.equalsIgnoreCase("dinner")) {
            System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
            category = scanner.nextLine();
        }
        System.out.println("Input the meal's name:");
        String meal = scanner.nextLine();
        while(availableInput(new String[]{meal})) {
            meal = scanner.nextLine();
        }
        meal_id++;
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
        if (db.isEmpty()) {
            System.out.println("No meals saved. Add a meal first.");
            return;
        }
       db.showMeals();
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

}