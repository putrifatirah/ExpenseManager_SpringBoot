package com.expensemanager.expensemanager.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.expensemanager.expensemanager.model.Expense;

public interface ExpenseRepository extends MongoRepository<Expense, String> {
    List<Expense> findByDate(LocalDate date);

    @Aggregation(pipeline = {
            "{ $group: { _id: '$date' } }"
    })
    List<Date> findDistinctDates();
}
