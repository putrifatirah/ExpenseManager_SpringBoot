package com.expensemanager.expensemanager.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<Expense> getExpensesByDate(LocalDate date) {
        return expenseRepository.findByDate(date);
    }

    public List<LocalDate> getDistinctDates() {
        List<Date> distinctDates = expenseRepository.findDistinctDates();
        return distinctDates.stream()
                .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .collect(Collectors.toList());
    }

    public List<Expense> getExpensesForDate(LocalDate date) {
        return expenseRepository.findByDate(date);
    }

    public Expense addExpense(String name, double amount, LocalDate date) {
        Expense expense = new Expense(name, amount, date);
        return expenseRepository.save(expense);
    }

    public Optional<Expense> deleteExpense(String expenseId) {
        Optional<Expense> expense = expenseRepository.findById(expenseId);
        expense.ifPresent(expenseRepository::delete);
        return expense;
    }

    //delete all expenses for a specific date
    public void deleteAllExpensesByDate(LocalDate date) {
        List<Expense> expenses = expenseRepository.findByDate(date);
        expenseRepository.deleteAll(expenses);
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

    public List<Expense> getExpensesByName(String name) {
        return expenseRepository.findByNameContainingIgnoreCase(name);
    }
    
}