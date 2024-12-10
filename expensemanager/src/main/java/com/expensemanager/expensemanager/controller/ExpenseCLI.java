package com.expensemanager.expensemanager.controller;


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
        List<Expense> expenses = expenseService.getExpensesForToday();
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        System.out.println("\n--- Today's Expenses ---");
        expenses.forEach(expense -> System.out.printf("ID: %s, Name: %s, Amount: %.2f, Date: %s%n",
                expense.getId(), expense.getName(), expense.getAmount(), expense.getDate()));
        System.out.printf("Total: %.2f\n", total);
    }

    private void addExpense() {
        scanner.nextLine();  // Consume newline
        System.out.print("Enter expense name: ");
        String name = scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        Expense expense = expenseService.addExpense(name, amount);
        System.out.printf("Added expense: %s, Amount: %.2f%n", expense.getName(), expense.getAmount());
    }

    private void deleteExpense() {
        System.out.print("Enter expense ID to delete: ");
        String expenseId = scanner.next();
        Optional<Expense> deletedExpense = expenseService.deleteExpense(expenseId);
        deletedExpense.ifPresentOrElse(
                expense -> System.out.printf("Deleted expense: %s%n", expense.getName()),
                () -> System.out.println("Expense not found.")
        );
    }

    private void editExpense() {
        System.out.print("Enter expense ID to edit: ");
        String expenseId = scanner.next();
        scanner.nextLine();  // Consume newline
        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new amount: ");
        double amount = scanner.nextDouble();
        Optional<Expense> editedExpense = expenseService.editExpense(expenseId, name, amount);
        editedExpense.ifPresentOrElse(
                expense -> System.out.printf("Updated expense: %s, Amount: %.2f%n", expense.getName(), expense.getAmount()),
                () -> System.out.println("Expense not found.")
        );
    }
}
