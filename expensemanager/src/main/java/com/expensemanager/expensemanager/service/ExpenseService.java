package com.expensemanager.expensemanager.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.expensemanager.expensemanager.model.Expense;
import com.expensemanager.expensemanager.repository.ExpenseRepository;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<Expense> getExpensesForToday() {
        return expenseRepository.findByDate(LocalDate.now());
    }

    public Expense addExpense(String name, double amount) {
        Expense expense = new Expense(name, amount, LocalDate.now());
        return expenseRepository.save(expense);
    }

    public Optional<Expense> deleteExpense(String expenseId) {
        Optional<Expense> expense = expenseRepository.findById(expenseId);
        expense.ifPresent(expenseRepository::delete);
        return expense;
    }

    public Optional<Expense> editExpense(String expenseId, String name, double amount) {
        Optional<Expense> existingExpense = expenseRepository.findById(expenseId);
        if (existingExpense.isPresent()) {
            Expense expense = existingExpense.get();
            expense.setName(name);
            expense.setAmount(amount);
            return Optional.of(expenseRepository.save(expense));
        }
        return Optional.empty();
    }
}