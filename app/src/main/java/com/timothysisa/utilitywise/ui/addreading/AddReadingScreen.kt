package com.timothysisa.utilitywise.ui.addreading

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.timothysisa.utilitywise.R
import com.timothysisa.utilitywise.data.UtilityRepository
import com.timothysisa.utilitywise.model.Reading
import com.timothysisa.utilitywise.model.UtilityType
import com.timothysisa.utilitywise.ui.icon
import com.timothysisa.utilitywise.ui.theme.Dimens
import com.timothysisa.utilitywise.ui.theme.InkMute
import com.timothysisa.utilitywise.ui.theme.UtilityWiseTheme
import com.timothysisa.utilitywise.validation.ReadingError
import com.timothysisa.utilitywise.validation.ReadingValidationResult
import com.timothysisa.utilitywise.validation.ReadingValidator
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val METER_DIGIT_COUNT = 7
private const val SHORT_DATE_PATTERN = "d MMM"
private const val FULL_DATE_PATTERN = "d MMM yyyy"

private val DEFAULT_TYPE = UtilityType.ELECTRICITY

/**
 * Holds the reading form's state and connects it to [UtilityRepository]. Pass the reading
 * to change as [readingToEdit], or null to add a new one. [onSave] receives the finished
 * reading; storing it is left to the caller.
 */
@Composable
fun AddReadingRoute(
    readingToEdit: Reading?,
    onSave: (Reading) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now() }
    var form by rememberSaveable(stateSaver = ReadingFormState.Saver) {
        mutableStateOf(
            readingToEdit?.let(ReadingFormState::forReading)
                ?: ReadingFormState.forNewReading(
                    type = DEFAULT_TYPE,
                    today = today,
                    latestTariff = UtilityRepository.latestTariff(DEFAULT_TYPE)
                )
        )
    }
    val previousReading = previousReadingFor(form, readingToEdit?.id, today)
    val validation = validationFor(form, previousReading, readingToEdit?.id, today)

    AddReadingScreen(
        form = form,
        isEditing = readingToEdit != null,
        previousReading = previousReading,
        validation = validation,
        today = today,
        onTypeSelect = { type ->
            form = form.withType(type, UtilityRepository.latestTariff(type))
        },
        onMeterTextChange = { text -> form = form.copy(meterText = text) },
        onMeterFieldLeave = { form = form.copy(meterFieldLeft = true) },
        onDateSelect = { date -> form = form.copy(date = date) },
        onTariffTextChange = { text -> form = form.copy(tariffText = text) },
        onTariffFieldLeave = { form = form.copy(tariffFieldLeft = true) },
        onSaveClick = {
            when (validation) {
                is ReadingValidationResult.Valid -> onSave(
                    Reading(
                        id = readingToEdit?.id ?: UtilityRepository.nextReadingId(),
                        type = form.type,
                        value = validation.meterValue,
                        date = validation.date,
                        tariff = validation.tariff,
                        photoUri = readingToEdit?.photoUri
                    )
                )
                is ReadingValidationResult.Invalid -> form = form.copy(saveAttempted = true)
            }
        },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

/**
 * The reading form. [previousReading] is the reading this one is measured from, or null
 * for the first reading of a meter. Errors in [validation] are only shown once [form] says
 * the user has left that field or tried to save.
 */
@Composable
fun AddReadingScreen(
    form: ReadingFormState,
    isEditing: Boolean,
    previousReading: Reading?,
    validation: ReadingValidationResult,
    today: LocalDate,
    onTypeSelect: (UtilityType) -> Unit,
    onMeterTextChange: (String) -> Unit,
    onMeterFieldLeave: () -> Unit,
    onDateSelect: (LocalDate) -> Unit,
    onTariffTextChange: (String) -> Unit,
    onTariffFieldLeave: () -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val errors = validation as? ReadingValidationResult.Invalid
    val validReading = validation as? ReadingValidationResult.Valid
    val unitsAdded = if (validReading != null && previousReading != null) {
        validReading.meterValue - previousReading.value
    } else {
        null
    }
    val unitName = form.type.unitRes?.let { stringResource(it) }.orEmpty()

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge)
            ) {
                AddReadingTopBar(isEditing = isEditing, onBackClick = onBackClick)
                UtilityTypePicker(selectedType = form.type, onTypeSelect = onTypeSelect)
                MeterCard(
                    meterText = form.meterText,
                    unitName = unitName,
                    errorMessage = errors?.meterError?.takeIf { form.showMeterError }?.message(),
                    previousReading = previousReading,
                    unitsAdded = unitsAdded,
                    onMeterTextChange = onMeterTextChange,
                    onMeterFieldLeave = onMeterFieldLeave
                )
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)) {
                    DateField(
                        date = form.date,
                        today = today,
                        errorMessage = errors?.dateError?.takeIf { form.showDateError }?.message(),
                        onDateSelect = onDateSelect,
                        modifier = Modifier.weight(1f)
                    )
                    TariffField(
                        tariffText = form.tariffText,
                        unitName = unitName,
                        errorMessage = errors?.tariffError?.takeIf { form.showTariffError }?.message(),
                        onTariffTextChange = onTariffTextChange,
                        onTariffFieldLeave = onTariffFieldLeave,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (validReading != null && unitsAdded != null) {
                    CostPreview(units = unitsAdded, tariff = validReading.tariff, unitName = unitName)
                }
            }
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.SpacingLarge)
                    .height(Dimens.ButtonHeight)
            ) {
                Text(text = stringResource(R.string.add_reading_save), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun AddReadingTopBar(isEditing: Boolean, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(top = Dimens.SpacingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = onBackClick,
            modifier = Modifier.size(Dimens.IconButtonSize),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.add_reading_back)
                )
            }
        }
        Spacer(modifier = Modifier.width(Dimens.SpacingLarge))
        Text(
            text = stringResource(
                if (isEditing) R.string.add_reading_title_edit else R.string.add_reading_title_new
            ),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
private fun UtilityTypePicker(selectedType: UtilityType, onTypeSelect: (UtilityType) -> Unit) {
    Row(
        modifier = Modifier.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
    ) {
        // Only metered utilities take readings, so broadband never appears here.
        UtilityType.entries.filter { it.hasMeter }.forEach { type ->
            val selected = type == selectedType
            Surface(
                selected = selected,
                onClick = { onTypeSelect(type) },
                modifier = Modifier
                    .weight(1f)
                    .height(Dimens.ButtonHeight)
                    .semantics { role = Role.RadioButton },
                shape = CircleShape,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = type.icon,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.IconSizeSmall)
                    )
                    Spacer(modifier = Modifier.width(Dimens.SpacingSmall))
                    Text(text = stringResource(type.displayNameRes), style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}

@Composable
private fun MeterCard(
    meterText: String,
    unitName: String,
    errorMessage: String?,
    previousReading: Reading?,
    unitsAdded: Double?,
    onMeterTextChange: (String) -> Unit,
    onMeterFieldLeave: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(Dimens.HeroPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.add_reading_meter_question),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = unitName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column {
                MeterInput(
                    meterText = meterText,
                    unitName = unitName,
                    errorMessage = errorMessage,
                    onMeterTextChange = onMeterTextChange,
                    onMeterFieldLeave = onMeterFieldLeave
                )
                errorMessage?.let { FieldError(message = it) }
            }
            LastReadingSummary(previousReading = previousReading, unitsAdded = unitsAdded, unitName = unitName)
        }
    }
}

@Composable
private fun MeterInput(
    meterText: String,
    unitName: String,
    errorMessage: String?,
    onMeterTextChange: (String) -> Unit,
    onMeterFieldLeave: () -> Unit
) {
    val description = stringResource(R.string.add_reading_meter_description, unitName)
    var hasBeenFocused by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    BasicTextField(
        value = meterText,
        onValueChange = { text -> onMeterTextChange(text.filter(Char::isDigit).take(METER_DIGIT_COUNT)) },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (hasBeenFocused && !focusState.isFocused) onMeterFieldLeave()
                if (focusState.isFocused) hasBeenFocused = true
                isFocused = focusState.isFocused
            }
            .semantics {
                contentDescription = description
                if (errorMessage != null) error(errorMessage)
            },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box {
                // The boxes only draw the digits; TalkBack reads the text field itself.
                MeterDigits(
                    meterText = meterText,
                    isFocused = isFocused,
                    modifier = Modifier.clearAndSetSemantics {}
                )
                // The real text field stays laid out under the boxes so typing, the
                // keyboard and accessibility all keep working, but it is never drawn.
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer { alpha = 0f }
                ) {
                    innerTextField()
                }
            }
        }
    )
}

@Composable
private fun MeterDigits(meterText: String, isFocused: Boolean, modifier: Modifier = Modifier) {
    val paddingCount = METER_DIGIT_COUNT - meterText.length
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.MeterDigitSpacing)
    ) {
        meterText.padStart(METER_DIGIT_COUNT, '0').forEachIndexed { index, digit ->
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(Dimens.MeterDigitHeight),
                shape = RoundedCornerShape(Dimens.CornerRadiusExtraSmall),
                color = MaterialTheme.colorScheme.inverseSurface,
                border = if (isFocused) {
                    BorderStroke(Dimens.FocusBorderWidth, MaterialTheme.colorScheme.secondary)
                } else {
                    null
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = digit.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        // Leading zeros only fill the boxes, so they are dimmed like an old meter.
                        color = if (index < paddingCount) InkMute else MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun LastReadingSummary(previousReading: Reading?, unitsAdded: Double?, unitName: String) {
    val locale = currentLocale()
    val dateFormatter = remember(locale) { DateTimeFormatter.ofPattern(SHORT_DATE_PATTERN, locale) }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.SpacingLarge, vertical = Dimens.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (previousReading == null) {
                    stringResource(R.string.add_reading_first_reading)
                } else {
                    stringResource(
                        R.string.add_reading_last_time,
                        previousReading.value,
                        previousReading.date.format(dateFormatter)
                    )
                },
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall
            )
            if (unitsAdded != null) {
                Text(
                    text = stringResource(R.string.add_reading_units_added, unitsAdded, unitName),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(
    date: LocalDate?,
    today: LocalDate,
    errorMessage: String?,
    onDateSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val locale = currentLocale()
    val dateFormatter = remember(locale) { DateTimeFormatter.ofPattern(FULL_DATE_PATTERN, locale) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    Column(modifier = modifier) {
        Surface(
            onClick = { showDatePicker = true },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { if (errorMessage != null) error(errorMessage) },
            shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(Dimens.CardPaddingHorizontal)) {
                FieldLabel(text = stringResource(R.string.add_reading_date))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = date?.format(dateFormatter).orEmpty(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.IconSizeSmall),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        errorMessage?.let { FieldError(message = it) }
    }
    if (showDatePicker) {
        ReadingDatePickerDialog(
            initialDate = date ?: today,
            today = today,
            onDateConfirm = { selected ->
                onDateSelect(selected)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReadingDatePickerDialog(
    initialDate: LocalDate,
    today: LocalDate,
    onDateConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val todayMillis = today.toUtcMillis()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toUtcMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= todayMillis

            override fun isSelectableYear(year: Int): Boolean = year <= today.year
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { datePickerState.selectedDateMillis?.let { onDateConfirm(it.toUtcLocalDate()) } },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text(text = stringResource(R.string.add_reading_date_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.add_reading_date_cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun TariffField(
    tariffText: String,
    unitName: String,
    errorMessage: String?,
    onTariffTextChange: (String) -> Unit,
    onTariffFieldLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = stringResource(R.string.add_reading_price_per_unit, unitName)
    val focusManager = LocalFocusManager.current
    var hasBeenFocused by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(Dimens.CardPaddingHorizontal)) {
                FieldLabel(text = label, modifier = Modifier.clearAndSetSemantics {})
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.add_reading_currency_symbol),
                        modifier = Modifier.clearAndSetSemantics {},
                        style = MaterialTheme.typography.titleMedium
                    )
                    BasicTextField(
                        value = tariffText,
                        onValueChange = onTariffTextChange,
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { focusState ->
                                if (hasBeenFocused && !focusState.isFocused) onTariffFieldLeave()
                                if (focusState.isFocused) hasBeenFocused = true
                            }
                            .semantics {
                                contentDescription = label
                                if (errorMessage != null) error(errorMessage)
                            },
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
        }
        errorMessage?.let { FieldError(message = it) }
    }
}

@Composable
private fun CostPreview(units: Double, tariff: Double, unitName: String) {
    Column {
        Text(
            text = stringResource(R.string.add_reading_adds_about),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.money_amount, units * tariff),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = stringResource(
                    R.string.add_reading_cost_breakdown,
                    stringResource(R.string.units_with_unit, units, unitName),
                    stringResource(R.string.money_amount, tariff)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun FieldError(message: String) {
    Text(
        text = message,
        modifier = Modifier.padding(start = Dimens.SpacingSmall, top = Dimens.SpacingTiny),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
private fun ReadingError.message(): String = when (this) {
    ReadingError.Required -> stringResource(R.string.error_required)
    ReadingError.NotANumber -> stringResource(R.string.error_not_a_number)
    ReadingError.NotPositive -> stringResource(R.string.error_not_positive)
    ReadingError.InFuture -> stringResource(R.string.error_in_future)
    is ReadingError.BelowPrevious -> stringResource(R.string.error_below_previous, previousValue)
    is ReadingError.AboveNext -> stringResource(R.string.error_above_next, nextValue)
}

private fun previousReadingFor(form: ReadingFormState, editingId: Long?, today: LocalDate): Reading? =
    UtilityRepository.previousReading(form.type, form.date ?: today, excludingId = editingId)

private fun validationFor(
    form: ReadingFormState,
    previousReading: Reading?,
    editingId: Long?,
    today: LocalDate
): ReadingValidationResult = ReadingValidator.validate(
    meterText = form.meterText,
    date = form.date,
    tariffText = form.tariffText,
    previousValue = previousReading?.value,
    nextValue = UtilityRepository.nextReading(form.type, form.date ?: today, excludingId = editingId)?.value,
    today = today
)

// The date picker works in UTC midnight milliseconds, whatever the device's time zone.
private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toUtcLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

@Composable
private fun currentLocale(): Locale = LocalConfiguration.current.locales[0]

@Composable
private fun AddReadingScreenPreview(form: ReadingFormState, readingToEdit: Reading?) {
    val today = LocalDate.now()
    val previousReading = previousReadingFor(form, readingToEdit?.id, today)
    UtilityWiseTheme {
        AddReadingScreen(
            form = form,
            isEditing = readingToEdit != null,
            previousReading = previousReading,
            validation = validationFor(form, previousReading, readingToEdit?.id, today),
            today = today,
            onTypeSelect = {},
            onMeterTextChange = {},
            onMeterFieldLeave = {},
            onDateSelect = {},
            onTariffTextChange = {},
            onTariffFieldLeave = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}

@Preview
@Composable
private fun NewReadingPreview() {
    val form = ReadingFormState.forNewReading(
        type = DEFAULT_TYPE,
        today = LocalDate.now(),
        latestTariff = UtilityRepository.latestTariff(DEFAULT_TYPE)
    )
    AddReadingScreenPreview(form = form.copy(meterText = "24054"), readingToEdit = null)
}

@Preview
@Composable
private fun EditReadingPreview() {
    val reading = UtilityRepository.latestReadings(count = 1).first()
    AddReadingScreenPreview(form = ReadingFormState.forReading(reading), readingToEdit = reading)
}

@Preview
@Composable
private fun ReadingErrorsPreview() {
    val form = ReadingFormState.forNewReading(
        type = DEFAULT_TYPE,
        today = LocalDate.now(),
        latestTariff = null
    )
    AddReadingScreenPreview(form = form.copy(meterText = "23000", saveAttempted = true), readingToEdit = null)
}
