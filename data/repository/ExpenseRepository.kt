package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.TransactionDao
import com.example.expensetracker.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: TransactionDao) {
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()

    suspend fun insert(transaction: TransactionEntity) = dao.insertTransaction(transaction)

    suspend fun delete(transaction: TransactionEntity) = dao.deleteTransaction(transaction)
}
