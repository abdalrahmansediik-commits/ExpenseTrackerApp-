package com.example.expensetracker.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME, EXPENSE
}

enum class ExpenseCategory(val displayName: String, val color: Color) {
    FOOD("Food & Dining", Color(0xFFF59E0B)),
    RENT("Rent & Housing", Color(0xFF3B82F6)),
    TRANSPORT("Transport", Color(0xFF10B981)),
    ENTERTAINMENT("Entertainment", Color(0xFFEC4899)),
    UTILITIES("Utilities & Bills", Color(0xFF8B5CF6)),
    SALARY("Salary & Wage", Color(0xFF059669)),
    INVESTMENT("Investment", Color(0xFF06B6D4)),
    OTHER("Other", Color(0xFF6B7280));

    fun getIcon(): ImageVector = when (this) {
        FOOD -> Icons.Default.Restaurant
        RENT -> Icons.Default.Home
        TRANSPORT -> Icons.Default.DirectionsCar
        ENTERTAINMENT -> Icons.Default.Movie
        UTILITIES -> Icons.Default.FlashOn
        SALARY -> Icons.Default.AccountBalanceWallet
        INVESTMENT -> Icons.AutoMirrored.Filled.TrendingUp
        OTHER -> Icons.Default.Category
    }
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: ExpenseCategory,
    val timestamp: Long = System.currentTimeMillis()
)
