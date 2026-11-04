package com.timothysisa.utilitywise.ui.bills

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.timothysisa.utilitywise.R
import com.timothysisa.utilitywise.model.UtilityType
import com.timothysisa.utilitywise.ui.theme.Dimens
import com.timothysisa.utilitywise.validation.BillFieldError
import com.timothysisa.utilitywise.validation.BillValidationResult
import com.timothysisa.utilitywise.validation.BillValidator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Collects the data for a new bill and returns valid parsed values through [onSave].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBillSheet(
    onSave: (UtilityType, BillValidationResult.Valid) -> Unit,
    onDismiss: () -> Unit,
) {
    var type by remember { mutableStateOf(UtilityType.ELECTRICITY) }
    var amountText by remember { mutableStateOf("") }
    var periodLabel by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<LocalDate?>(null) }
    var saveAttempted by remember { mutableStateOf(value = false) }
    val validation = BillValidator.validate(amountText, periodLabel, dueDate)
    val errors = validation as? BillValidationResult.Invalid

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = Dimens.ScreenPadding,
                    end = Dimens.ScreenPadding,
                    bottom = Dimens.ScreenPadding,
                ),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge)
        ) {
            Text(text = stringResource(R.string.bills_add_sheet_title), style = MaterialTheme.typography.headlineSmall)
            UtilityPicker(selectedType = type) { type = it }
            BillTextField(
                label = stringResource(R.string.bills_amount),
                value = amountText,
                errorMessage = errors?.amountError?.takeIf { saveAttempted }?.message(),
                onValueChange = { amountText = it },
            )
            BillTextField(
                label = stringResource(R.string.bills_period),
                value = periodLabel,
                errorMessage = errors?.periodError?.takeIf { saveAttempted }?.message(),
                onValueChange = { periodLabel = it }
            )
            BillDueDateField(
                dueDate = dueDate,
                errorMessage = errors?.dueDateError?.takeIf { saveAttempted }?.message(),
                onDateSelect = { dueDate = it }
            )
            Button(
                onClick = {
                    when (validation) {
                        is BillValidationResult.Valid -> onSave(type, validation)
                        is BillValidationResult.Invalid -> saveAttempted = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ButtonHeight)
            ) {
                Text(text = stringResource(R.string.bills_save))
            }
        }
    }
}

@Composable
private fun UtilityPicker(selectedType: UtilityType, onTypeSelect: (UtilityType) -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.bills_choose_utility),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.padding(top = Dimens.SpacingSmall),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
        ) {
            UtilityType.entries.forEach { type ->
                Surface(
                    onClick = { onTypeSelect(type) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(Dimens.CornerRadiusSmall),
                    color = if (type == selectedType) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = stringResource(type.displayNameRes),
                        modifier = Modifier.padding(Dimens.SpacingMedium),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (type == selectedType) {
                            MaterialTheme.colorScheme.onSecondary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BillTextField(
    label: String,
    value: String,
    errorMessage: String?,
    onValueChange: (String) -> Unit
) {
    Column {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(modifier = Modifier.padding(Dimens.CardPaddingHorizontal)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    singleLine = true
                )
            }
        }
        errorMessage?.let { BillFieldErrorText(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BillDueDateField(
    dueDate: LocalDate?,
    errorMessage: String?,
    onDateSelect: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern(stringResource(R.string.bills_full_date_pattern))
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(modifier = Modifier.padding(Dimens.CardPaddingHorizontal)) {
                Text(
                    text = stringResource(R.string.bills_due_date),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dueDate?.format(dateFormatter).orEmpty(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        errorMessage?.let { BillFieldErrorText(it) }
    }
    if (showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = dueDate?.toUtcMillis())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { onDateSelect(it.toUtcLocalDate()) }
                        showDatePicker = false
                    }
                ) {
                    Text(text = stringResource(R.string.add_reading_date_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = stringResource(R.string.add_reading_date_cancel))
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun BillFieldErrorText(message: String) {
    Text(
        text = message,
        modifier = Modifier.padding(start = Dimens.SpacingSmall, top = Dimens.SpacingTiny),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
private fun BillFieldError.message(): String = when (this) {
    BillFieldError.REQUIRED -> stringResource(R.string.error_required)
    BillFieldError.NOT_A_NUMBER -> stringResource(R.string.error_not_a_number)
    BillFieldError.NOT_POSITIVE -> stringResource(R.string.error_not_positive)
    BillFieldError.TOO_MANY_DECIMAL_PLACES -> stringResource(R.string.error_too_many_decimal_places)
}

private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toUtcLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
