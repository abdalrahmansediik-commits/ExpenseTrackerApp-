package com.example.expensetracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.AppDatabase
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.data.model.TransactionEntity
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardSummary(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val categoryDistribution: Map<ExpenseCategory, Double> = emptyMap()
)

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository

    init {
        val dao = AppDatabase.getDatabase(application).transactionDao
        repository = ExpenseRepository(dao)
    }

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val summary: StateFlow<DashboardSummary> = transactions.map { list ->
        var income = 0.0
        var expense = 0.0
        val categoryExpenses = mutableMapOf<ExpenseCategory, Double>()

        for (item in list) {
            if (item.type == TransactionType.INCOME) {
                income += item.amount
            } else {
                expense += item.amount
                categoryExpenses[item.category] =
                    categoryExpenses.getOrDefault(item.category, 0.0) + item.amount
            }
        }

        DashboardSummary(
            totalBalance = income - expense,
            totalIncome = income,
            totalExpenses = expense,
            categoryDistribution = categoryExpenses
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardSummary()
    )

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: ExpenseCategory,
        timestamp: Long
    ) {
        viewModelScope.launch {
            repository.insert(
                TransactionEntity(
                    title = title.trim(),
                    amount = amount,
                    type = type,
                    category = category,
                    timestamp = timestamp
                )
            )
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }
}
