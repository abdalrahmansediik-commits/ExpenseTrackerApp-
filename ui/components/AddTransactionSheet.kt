package com.example.expensetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.ExpenseCategory
import com.example.expensetracker.data.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    onDismiss: () -> Unit,
    onSave: (String, Double, TransactionType, ExpenseCategory, Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.FOOD) }
    var dateEpoch by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var isTitleError by remember { mutableStateOf(false) }
    var isAmountError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateEpoch)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateEpoch = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Add Transaction",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Income / Expense Type Toggle
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = selectedType == TransactionType.EXPENSE,
                    onClick = {
                        selectedType = TransactionType.EXPENSE
                        selectedCategory = ExpenseCategory.FOOD
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Expense")
                }
                SegmentedButton(
                    selected = selectedType == TransactionType.INCOME,
                    onClick = {
                        selectedType = TransactionType.INCOME
                        selectedCategory = ExpenseCategory.SALARY
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Income")
                }
            }

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotBlank()) isTitleError = false
                },
                label = { Text("Title") },
                isError = isTitleError,
                supportingText = { if (isTitleError) Text("Title is required") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Amount Field
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    if (it.toDoubleOrNull() != null && it.toDouble() > 0) isAmountError = false
                },
                label = { Text("Amount ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = isAmountError,
                supportingText = { if (isAmountError) Text("Enter a valid positive number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Category Selection Dropdown
            ExposedDropdownMenuBox(
                expanded = isCategoryDropdownExpanded,
                onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isCategoryDropdownExpanded,
                    onDismissRequest = { isCategoryDropdownExpanded = false }
                ) {
                    ExpenseCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.displayName) },
                            leadingIcon = { Icon(category.getIcon(), contentDescription = null) },
                            onClick = {
                                selectedCategory = category
                                isCategoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Date Picker Selection Field
            OutlinedTextField(
                value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dateEpoch)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            // Save Button
            Button(
                onClick = {
                    val validAmount = amountText.toDoubleOrNull()
                    if (title.isBlank()) isTitleError = true
                    if (validAmount == null || validAmount <= 0) isAmountError = true

                    if (title.isNotBlank() && validAmount != null && validAmount > 0) {
                        onSave(title, validAmount, selectedType, selectedCategory, dateEpoch)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Transaction", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
