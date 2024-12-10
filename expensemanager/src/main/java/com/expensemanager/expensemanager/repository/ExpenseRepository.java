package com.expensemanager.expensemanager.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.expensemanager.expensemanager.model.Expense;

public interface ExpenseRepository extends MongoRepository<Expense, String> {
    List<Expense> findByDate(LocalDate date);
}