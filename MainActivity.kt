package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.ui.components.AddTransactionSheet
import com.example.expensetracker.ui.screens.AnalyticsScreen
import com.example.expensetracker.ui.screens.DashboardScreen
import com.example.expensetracker.ui.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                ExpenseTrackerApp()
            }
        }
    }
}

sealed class Screen(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Dashboard : Screen("Home", Icons.Default.Home)
    data object Analytics : Screen("Analytics", Icons.Default.Analytics)
}

@Composable
fun ExpenseTrackerApp(
    viewModel: ExpenseViewModel = viewModel()
) {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
    var showAddSheet by remember { mutableStateOf(false) }

    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedScreen == Screen.Dashboard,
                    onClick = { selectedScreen = Screen.Dashboard },
                    icon = { Icon(Screen.Dashboard.icon, contentDescription = Screen.Dashboard.title) },
                    label = { Text(Screen.Dashboard.title) }
                )
                NavigationBarItem(
                    selected = selectedScreen == Screen.Analytics,
                    onClick = { selectedScreen = Screen.Analytics },
                    icon = { Icon(Screen.Analytics.icon, contentDescription = Screen.Analytics.title) },
                    label = { Text(Screen.Analytics.title) }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { innerPadding ->
        when (selectedScreen) {
            Screen.Dashboard -> {
                DashboardScreen(
                    summary = summary,
                    transactions = transactions,
                    onDeleteTransaction = { viewModel.deleteTransaction(it) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            Screen.Analytics -> {
                AnalyticsScreen(
                    categoryDistribution = summary.categoryDistribution,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        if (showAddSheet) {
            AddTransactionSheet(
                onDismiss = { showAddSheet = false },
                onSave = { title, amount, type, category, timestamp ->
                    viewModel.addTransaction(title, amount, type, category, timestamp)
                    showAddSheet = false
                }
            )
        }
    }
}
