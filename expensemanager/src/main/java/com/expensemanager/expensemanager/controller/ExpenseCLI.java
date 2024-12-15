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
            System.out.println("6. View Expenses 2");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> viewExpenses();
                case 2 -> addExpense();
                case 3 -> deleteExpense();
                case 4 -> editExpense();
                case 5 -> System.exit(0);
                case 6 -> viewExpenses2();
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void viewExpenses() {
        List<Expense> expenses = expenseService.getExpensesForToday();
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        System.out.println("\n--- Today's Expenses ---");
        expenses.forEach(expense -> System.out.printf("ID: %s, Name: %s, Amount: %.2f, Date: %s%n",
                expense.getId(), expense.getName(), expense.getAmount(), expense.getDate()));
        System.out.printf("Total: %.2f\n", total);
    }

    private void viewExpenses2() {
        // Fetch and display distinct dates
        List<LocalDate> distinctDates = expenseService.getDistinctDates();
        System.out.println("\n--- Available Dates ---");
        for (int i = 0; i < distinctDates.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, distinctDates.get(i));
        }

        // Get user input to select a date
        LocalDate selectedDate = getUserSelectedDate(distinctDates);

        // Fetch and display expenses for the selected date
        List<Expense> expenses = expenseService.getExpensesByDate(selectedDate);
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        System.out.printf("\n--- Expenses for %s ---\n", selectedDate);
        expenses.forEach(expense -> System.out.printf("ID: %s, Name: %s, Amount: %.2f, Date: %s%n",
                expense.getId(), expense.getName(), expense.getAmount(), expense.getDate()));
        System.out.printf("Total: %.2f\n", total);
    }

    private LocalDate getUserSelectedDate(List<LocalDate> dates) {
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Select a date by entering its number or enter 0 to create a new date: ");
        int choice = scanner.nextInt();

        if (choice == 0) {
            System.out.print("Enter new date (YYYY-MM-DD): ");
            String dateInput = scanner.next();
            return LocalDate.parse(dateInput);
        } else if (choice > 0 && choice <= dates.size()) {
            return dates.get(choice - 1);
        } else {
            System.out.println("Invalid choice. Defaulting to today's date.");
            return LocalDate.now();
        }
    }

    private void addExpense() {
        // Fetch and display distinct dates
        List<LocalDate> distinctDates = expenseService.getDistinctDates();
        System.out.println("\n--- Available Dates ---");
        for (int i = 0; i < distinctDates.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, distinctDates.get(i));
        }

        // Get user input to select a date or create a new date
        LocalDate selectedDate = getUserSelectedDate(distinctDates);

        // Fetch and display expenses for the selected date
        List<Expense> expenses = expenseService.getExpensesByDate(selectedDate);
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        System.out.printf("\n--- Expenses for %s ---\n", selectedDate);
        expenses.forEach(expense -> System.out.printf("ID: %s, Name: %s, Amount: %.2f, Date: %s%n",
                expense.getId(), expense.getName(), expense.getAmount(), expense.getDate()));
        System.out.printf("Total: %.2f\n", total);

        // Add new expense
        scanner.nextLine(); // Consume newline
        System.out.print("Enter expense name: ");
        String name = scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        Expense expense = expenseService.addExpense(name, amount, selectedDate);
        System.out.printf("Added expense: %s, Amount: %.2f, Date: %s%n", expense.getName(), expense.getAmount(), selectedDate);
    }

    private void deleteExpense() {
        // Fetch and display distinct dates
        List<LocalDate> distinctDates = expenseService.getDistinctDates();
        System.out.println("\n--- Available Dates ---");
        for (int i = 0; i < distinctDates.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, distinctDates.get(i));
        }

        // Get user input to select a date
        LocalDate selectedDate = getUserSelectedDate(distinctDates);

        // Fetch and display expenses for the selected date
        List<Expense> expenses = expenseService.getExpensesByDate(selectedDate);
        System.out.printf("\n--- Expenses for %s ---\n", selectedDate);
        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);
            System.out.printf("%d. ID: %s, Name: %s, Amount: %.2f, Date: %s%n", i + 1, expense.getId(), expense.getName(), expense.getAmount(), expense.getDate());
        }

        // Get user input to select an expense to delete
        System.out.print("Select an expense to delete by entering its number: ");
        int choice = scanner.nextInt() - 1;

        if (choice >= 0 && choice < expenses.size()) {
            Expense expenseToDelete = expenses.get(choice);
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
        System.out.println("\n--- Available Dates ---");
        for (int i = 0; i < distinctDates.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, distinctDates.get(i));
        }

        // Get user input to select a date
        LocalDate selectedDate = getUserSelectedDate(distinctDates);

        // Fetch and display expenses for the selected date
        List<Expense> expenses = expenseService.getExpensesByDate(selectedDate);
        System.out.printf("\n--- Expenses for %s ---\n", selectedDate);
        for (int i = 0; i < expenses.size(); i++) {
            Expense expense = expenses.get(i);
            System.out.printf("%d. ID: %s, Name: %s, Amount: %.2f, Date: %s%n", i + 1, expense.getId(), expense.getName(), expense.getAmount(), expense.getDate());
        }

        // Get user input to select an expense to edit
        System.out.print("Select an expense to edit by entering its number: ");
        int choice = scanner.nextInt() - 1;

        if (choice >= 0 && choice < expenses.size()) {
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
