package com.miagenda.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

data class CalendarGridInfo(
    val gridBounds: Rect,
    val cellSize: Float,
    val firstDayOffset: Int,
    val daysInMonth: Int,
    val yearMonth: YearMonth
)

@Composable
fun MonthlyCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    datesWithAppointments: Set<LocalDate>,
    modifier: Modifier = Modifier,
    onGridInfoChanged: ((CalendarGridInfo) -> Unit)? = null
) {
    val yearMonth = YearMonth.from(selectedDate)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % 7
    val today = LocalDate.now()

    Column(modifier = modifier.fillMaxWidth()) {
        MonthHeader(
            yearMonth = yearMonth,
            onPreviousMonth = { onMonthChange(yearMonth.minusMonths(1)) },
            onNextMonth = { onMonthChange(yearMonth.plusMonths(1)) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DayOfWeekHeader()

        Spacer(modifier = Modifier.height(4.dp))

        DaysGrid(
            daysInMonth = daysInMonth,
            firstDayOfWeek = firstDayOfWeek,
            yearMonth = yearMonth,
            selectedDate = selectedDate,
            today = today,
            datesWithAppointments = datesWithAppointments,
            onDateSelected = onDateSelected,
            onGridInfoChanged = onGridInfoChanged
        )
    }
}

@Composable
private fun MonthHeader(
    yearMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val monthName = yearMonth.month.getDisplayName(TextStyle.FULL, Locale("es"))
    val text = "$monthName ${yearMonth.year}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Mes anterior"
            )
        }

        Text(
            text = text.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Mes siguiente"
            )
        }
    }
}

@Composable
private fun DayOfWeekHeader() {
    val dayNames = listOf("D", "L", "M", "X", "J", "V", "S")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        dayNames.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DaysGrid(
    daysInMonth: Int,
    firstDayOfWeek: Int,
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    datesWithAppointments: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    onGridInfoChanged: ((CalendarGridInfo) -> Unit)?
) {
    var gridInfo by remember { mutableStateOf<CalendarGridInfo?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coords ->
                    val bounds = coords.boundsInWindow()
                    val cellSz = bounds.width / 7f
                    val info = CalendarGridInfo(
                        gridBounds = bounds,
                        cellSize = cellSz,
                        firstDayOffset = firstDayOfWeek,
                        daysInMonth = daysInMonth,
                        yearMonth = yearMonth
                    )
                    if (info != gridInfo) {
                        gridInfo = info
                        onGridInfoChanged?.invoke(info)
                    }
                }
        ) {
            var dayCounter = 1
            val totalCells = firstDayOfWeek + daysInMonth
            val rows = (totalCells + 6) / 7

            Column(modifier = Modifier.fillMaxWidth()) {
                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val isBlank = cellIndex < firstDayOfWeek || dayCounter > daysInMonth

                            if (isBlank) {
                                Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                            } else {
                                val date = yearMonth.atDay(dayCounter)
                                val isSelected = date == selectedDate
                                val isToday = date == today
                                val hasAppointment = date in datesWithAppointments

                                DayCell(
                                    day = dayCounter,
                                    isSelected = isSelected,
                                    isToday = isToday,
                                    hasAppointment = hasAppointment,
                                    onClick = { onDateSelected(date) },
                                    modifier = Modifier.weight(1f)
                                )
                                dayCounter++
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    hasAppointment: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    val dotColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (hasAppointment) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            } else {
                Spacer(modifier = Modifier.size(6.dp))
            }
        }
    }
}
