package com.expensemanager.expensemanager.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.expensemanager.expensemanager.model.Expense;
import com.expensemanager.expensemanager.service.ExpenseService;

@Component
public class ExpenseCLI implements CommandLineRunner {

    @Autowired
    private ExpenseService expenseService;

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run(String... args) {
        while (true) {
            System.out.println("\nExpense Manager");
            System.out.println("1. View Expenses");
            System.out.println("2. Add Expense");
            System.out.println("3. Delete Expense");
            System.out.println("4. Edit Expense");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> viewExpenses();
                case 2 -> addExpense();
                case 3 -> deleteExpense();
                case 4 -> editExpense();
                case 5 -> System.exit(0);
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void viewExpenses() {
        // Fetch and display distinct dates
        List<LocalDate> distinctDates = expenseService.getDistinctDates();

        // Sort the dates in chronological order (oldest to latest)
        distinctDates.sort(LocalDate::compareTo);

        System.out.println("\n--- Available Dates ---");
        for (int i = 0; i < distinctDates.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, distinctDates.get(i));
        }

        // Get user input to select a date
        LocalDate selectedDate = getUserSelectedDate(distinctDates, false);
        if (selectedDate == null) {
            System.out.println("Operation cancelled.");
            return;
        }

        // Fetch and display expenses for the selected date
        List<Expense> expenses = expenseService.getExpensesByDate(selectedDate);
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        System.out.printf("\n--- Expenses for %s ---\n", selectedDate);
        expenses.forEach(expense -> System.out.printf("Name: %s, Amount: %.2f, Date: %s%n",
                expense.getName(), expense.getAmount(), expense.getDate()));
        System.out.printf("Total Amount: %.2f\n", total);
    }

    private LocalDate getUserSelectedDate(List<LocalDate> dates, boolean allowNewDate) {
        while (true) { // Loop until a valid choice is made
            if (allowNewDate) {
                System.out.print("Select a date by entering its index (0 to create a new date, or -1 to cancel): ");
            } else {
                System.out.print("Select a date by entering its index (-1 to cancel): ");
            }
    
            int choice = scanner.nextInt();
    
            if (choice == -1) {
                return null; // User cancels
            }
    
            if (allowNewDate && choice == 0) {
                // User wants to create a new date
                System.out.print("Enter new date (YYYY-MM-DD): ");
                String dateInput = scanner.next();
                try {
                    return LocalDate.parse(dateInput);
                } catch (Exception e) {
                    System.out.println("Invalid date format. Please try again.");
                }
            } else if (choice > 0 && choice <= dates.size()) {
                return dates.get(choice - 1); // Valid index selected
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    

    private void addExpense() {
        // Fetch and display distinct dates
        List<LocalDate> distinctDates = expenseService.getDistinctDates();

        // Sort the dates in chronological order (oldest to latest)
        distinctDates.sort(LocalDate::compareTo);

        System.out.println("\n--- Available Dates ---");
        for (int i = 0; i < distinctDates.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, distinctDates.get(i));
        }

        // Get user input to select a date or create a new date
        LocalDate selectedDate = getUserSelectedDate(distinctDates, true);
        if (selectedDate == null) {
            System.out.println("Operation cancelled.");
            return;
        }

        boolean addingExpenses = true;

        while(addingExpenses){
            // Fetch and display expenses for the selected date
            List<Expense> expenses = expenseService.getExpensesByDate(selectedDate);
            double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

            System.out.printf("\n--- Expenses for %s ---\n", selectedDate);
            expenses.forEach(expense -> System.out.printf("Name: %s, Amount: %.2f, Date: %s%n",
                    expense.getName(), expense.getAmount(), expense.getDate()));
            System.out.printf("Total: %.2f\n", total);

             // Add new expense
            scanner.nextLine(); // Consume newline
            System.out.print("Enter expense name (or enter -1 to stop): ");
            String name = scanner.nextLine();
            if (name.equals("-1")) {
                addingExpenses = false;
                System.out.println("Finished adding expenses for " + selectedDate);
                break;
            }

            System.out.print("Enter amount(RM): ");
            double amount;
            try{
                amount = scanner.nextDouble();
            } catch (Exception e) {
                System.out.println("Invalid amount. Please try again.");
                scanner.nextLine(); // Clear invalid input
                continue;   
            }
            Expense expense = expenseService.addExpense(name, amount, selectedDate);
            System.out.printf("Added expense: %s, Amount: %.2f, Date: %s%n", expense.getName(), expense.getAmount(), selectedDate);
        }
    }

    private void deleteExpense() {
        // Fetch and display distinct dates
        List<LocalDate> distinctDates = expenseService.getDistinctDates();

        // Sort the dates in chronological order (oldest to latest)
        distinctDates.sort(LocalDate::compareTo);

        System.out.println("\n--- Available Dates ---");
        for (int i = 0; i < distinctDates.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, distinctDates.get(i));
        }
        
        // Get user input to select a date
        LocalDate selectedDate = getUserSelectedDate(distinctDates, false);
        if (selectedDate == null) {
            System.out.println("Operation cancelled.");
            return;
        }

        // Fetch and display expenses for the selected date
        List<Expense> expenses = expenseService.getExpensesByDate(selectedDate);
        System.out.printf("\n--- Expenses for %s ---\n", selectedDate);
        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);
            System.out.printf("%d. Name: %s, Amount: %.2f, Date: %s%n", i + 1, expense.getName(), expense.getAmount(), expense.getDate());
        }

        // Get user input to select an expense to delete
        System.out.print("Enter the index of the expense to delete (0 to delete all, -1 to quit): ");
        int choice = scanner.nextInt();

        if (choice == -1) {
            System.out.println("Operation cancelled.");
        } else if (choice == 0) {
            // Confirm before deleting all expenses and the date
            System.out.print("Are you sure you want to delete ALL expenses and the date? (y/n): ");
            scanner.nextLine(); // Consume newline
            String confirmation = scanner.nextLine().trim().toLowerCase();
            if (confirmation.equals("y")) {
                expenseService.deleteAllExpensesByDate(selectedDate);
                expenseService.deleteDate(selectedDate); // Remove the date
                System.out.println("All expenses and the date " + selectedDate + " have been deleted.");
            } else {
                System.out.println("Operation cancelled. No expenses or date were deleted.");
            }
        } else if (choice > 0 && choice <= expenses.size()) {
            // Delete a single expense
            Expense expenseToDelete = expenses.get(choice - 1);
            Optional<Expense> deletedExpense = expenseService.deleteExpense(expenseToDelete.getId());
            deletedExpense.ifPresentOrElse(
                    expense -> System.out.printf("Deleted expense: %s%n", expense.getName()),
                    () -> System.out.println("Expense not found."));
        } else {
            System.out.println("Invalid choice. No expense deleted.");
        }
    }

    private void editExpense() {
        // Fetch and display distinct dates
        List<LocalDate> distinctDates = expenseService.getDistinctDates();

        // Sort the dates in chronological order (oldest to latest)
        distinctDates.sort(LocalDate::compareTo);

        System.out.println("\n--- Available Dates ---");
        for (int i = 0; i < distinctDates.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, distinctDates.get(i));
        }
        // System.out.println("0. Create a new date");
        // System.out.println("-1. Cancel");

        // Get user input to select a date
        LocalDate selectedDate = getUserSelectedDate(distinctDates, false);
        if (selectedDate == null) {
            System.out.println("Operation cancelled.");
            return;
        }

        // Fetch and display expenses for the selected date
        List<Expense> expenses = expenseService.getExpensesByDate(selectedDate);
        System.out.printf("\n--- Expenses for %s ---\n", selectedDate);
        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);
            System.out.printf("%d. Name: %s, Amount: %.2f, Date: %s%n", i + 1, expense.getName(), expense.getAmount(), expense.getDate());
        }

        // Get user input to select an expense to edit
        System.out.print("Enter the index of the expense to edit (-1 to cancel): ");
        int choice = scanner.nextInt() - 1;

        if (choice == -2) {
            System.out.println("Operation cancelled.");
        } else if (choice >= 0 && choice < expenses.size()) {
            Expense expenseToEdit = expenses.get(choice);
            scanner.nextLine(); // Consume newline
            System.out.print("Enter new name: ");
            String name = scanner.nextLine();
            System.out.print("Enter new amount: ");
            double amount = scanner.nextDouble();
            Optional<Expense> editedExpense = expenseService.editExpense(expenseToEdit.getId(), name, amount);
            editedExpense.ifPresentOrElse(
                    expense -> System.out.printf("Updated expense: %s, Amount: %.2f%n", expense.getName(),
                            expense.getAmount()),
                    () -> System.out.println("Expense not found."));
        } else {
            System.out.println("Invalid choice. No expense edited.");
        }
    }
}