package com.mhss.app.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mhss.app.domain.model.Calendar
import com.mhss.app.domain.model.CalendarEvent
import com.mhss.app.ui.R
import com.mhss.app.ui.navigation.Screen
import com.mhss.app.util.date.formatEventStartEnd
import com.mhss.app.util.permissions.Permission
import com.mhss.app.util.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun SamsungCalendarScreen(
    navController: NavHostController,
    viewModel: CalendarViewModel = koinViewModel()
) {
    val state = viewModel.uiState
    var selectedDate by remember { mutableStateOf(java.util.Calendar.getInstance()) }
    var showMonthView by remember { mutableStateOf(true) }
    var currentMonth by remember { mutableStateOf(java.util.Calendar.getInstance()) }
    var settingsVisible by remember { mutableStateOf(false) }
    
    val readCalendarPermissionState = rememberPermissionState(
        permission = Permission.READ_CALENDAR
    )

    LaunchedEffect(readCalendarPermissionState.isGranted) {
        if (readCalendarPermissionState.isGranted) {
            viewModel.onEvent(
                CalendarViewModelEvent.ReadPermissionChanged(true)
            )
        }
    }

    val selectedDateEvents = remember(state.events, selectedDate) {
        val dateString = formatDateForKey(selectedDate)
        state.events.entries.find { it.key.contains(dateString) }?.value ?: emptyList()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (readCalendarPermissionState.isGranted) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.CalendarEventDetailsScreen(null))
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        modifier = Modifier.size(25.dp),
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = stringResource(R.string.add_event),
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        if (readCalendarPermissionState.isGranted) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Top Bar with Month/Year and Actions
                SamsungCalendarTopBar(
                    currentMonth = currentMonth,
                    showMonthView = showMonthView,
                    onMonthViewToggle = { showMonthView = !showMonthView },
                    onSettingsClick = { settingsVisible = !settingsVisible },
                    onTodayClick = {
                        selectedDate = java.util.Calendar.getInstance()
                        currentMonth = java.util.Calendar.getInstance()
                    }
                )

                // Settings Section
                AnimatedVisibility(
                    visible = settingsVisible,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    CalendarSettingsSection(
                        calendars = state.calendars,
                        onCalendarClicked = {
                            viewModel.onEvent(CalendarViewModelEvent.IncludeCalendar(it))
                        }
                    )
                }

                // Month View
                AnimatedVisibility(
                    visible = showMonthView,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    MonthCalendarView(
                        currentMonth = currentMonth,
                        selectedDate = selectedDate,
                        events = state.events,
                        onDateSelected = { selectedDate = it },
                        onMonthChanged = { currentMonth = it }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Week/Day Selector
                WeekDaySelector(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    events = state.events
                )

                // Events List
                EventsList(
                    selectedDate = selectedDate,
                    events = selectedDateEvents,
                    onEventClick = { event ->
                        navController.navigate(
                            Screen.CalendarEventDetailsScreen(
                                Base64.encode(Json.encodeToString(event).toByteArray())
                            )
                        )
                    }
                )
            }
        } else {
            NoReadCalendarPermissionMessage(
                shouldShowRationale = readCalendarPermissionState.shouldShowRationale,
                onOpenSettings = { readCalendarPermissionState.openAppSettings() },
                onRequest = { readCalendarPermissionState.launchRequest() }
            )
        }
    }
}

@Composable
fun SamsungCalendarTopBar(
    currentMonth: java.util.Calendar,
    showMonthView: Boolean,
    onMonthViewToggle: () -> Unit,
    onSettingsClick: () -> Unit,
    onTodayClick: () -> Unit
) {
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(currentMonth.time)
    val year = currentMonth.get(java.util.Calendar.YEAR).toString()
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Month and Year
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onMonthViewToggle)
        ) {
            Text(
                text = monthName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = year,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Icon(
                imageVector = if (showMonthView) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle month view",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onTodayClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = "Today",
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings_sliders),
                    contentDescription = stringResource(R.string.include_calendars),
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun MonthCalendarView(
    currentMonth: java.util.Calendar,
    selectedDate: java.util.Calendar,
    events: Map<String, List<CalendarEvent>>,
    onDateSelected: (java.util.Calendar) -> Unit,
    onMonthChanged: (java.util.Calendar) -> Unit
) {
    var displayedMonth by remember(currentMonth) { 
        mutableStateOf(currentMonth.clone() as java.util.Calendar) 
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        // Month Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    displayedMonth = (displayedMonth.clone() as java.util.Calendar).apply {
                        add(java.util.Calendar.MONTH, -1)
                    }
                    onMonthChanged(displayedMonth)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous month",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            
            IconButton(
                onClick = {
                    displayedMonth = (displayedMonth.clone() as java.util.Calendar).apply {
                        add(java.util.Calendar.MONTH, 1)
                    }
                    onMonthChanged(displayedMonth)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next month",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Day Headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid
        val daysInMonth = getDaysInMonth(displayedMonth)
        daysInMonth.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { date ->
                    CalendarDayCell(
                        date = date,
                        isSelected = date?.isSameDay(selectedDate) == true,
                        isToday = date?.isSameDay(java.util.Calendar.getInstance()) == true,
                        hasEvents = date?.let { events.keys.any { key -> key.contains(formatDateForKey(it)) } } == true,
                        isCurrentMonth = date?.get(java.util.Calendar.MONTH) == displayedMonth.get(java.util.Calendar.MONTH),
                        onDateSelected = { date?.let { onDateSelected(it) } }
                    )
                }
                // Fill remaining cells in the week
                repeat(7 - week.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun RowScope.CalendarDayCell(
    date: java.util.Calendar?,
    isSelected: Boolean,
    isToday: Boolean,
    hasEvents: Boolean,
    isCurrentMonth: Boolean,
    onDateSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }
            )
            .clickable(enabled = date != null) { onDateSelected() },
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = date.get(java.util.Calendar.DAY_OF_MONTH).toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        !isCurrentMonth -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onBackground
                    },
                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                )
                if (hasEvents && !isSelected) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
fun WeekDaySelector(
    selectedDate: java.util.Calendar,
    onDateSelected: (java.util.Calendar) -> Unit,
    events: Map<String, List<CalendarEvent>>
) {
    val weekDates = remember(selectedDate) {
        getWeekDates(selectedDate)
    }
    
    val lazyListState = rememberLazyListState()
    val selectedIndex = weekDates.indexOfFirst { it.isSameDay(selectedDate) }
    
    LaunchedEffect(selectedIndex) {
        if (selectedIndex != -1) {
            lazyListState.animateScrollToItem(selectedIndex)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        LazyRow(
            state = lazyListState,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(weekDates) { date ->
                WeekDayItem(
                    date = date,
                    isSelected = date.isSameDay(selectedDate),
                    hasEvents = events.keys.any { it.contains(formatDateForKey(date)) },
                    onDateSelected = { onDateSelected(date) }
                )
            }
        }
    }
}

@Composable
fun WeekDayItem(
    date: java.util.Calendar,
    isSelected: Boolean,
    hasEvents: Boolean,
    onDateSelected: () -> Unit
) {
    val isToday = date.isSameDay(java.util.Calendar.getInstance())
    val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(date.time).uppercase()
    
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else Color.Transparent
            )
            .clickable(onClick = onDateSelected)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = dayOfWeek,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.get(java.util.Calendar.DAY_OF_MONTH).toString(),
                style = MaterialTheme.typography.titleLarge,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isToday -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onBackground
                },
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.SemiBold
            )
        }
        if (hasEvents) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.primary
                    )
            )
        } else {
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun EventsList(
    selectedDate: java.util.Calendar,
    events: List<CalendarEvent>,
    onEventClick: (CalendarEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // Date Header
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        Text(
            text = dateFormat.format(selectedDate.time),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        if (events.isEmpty()) {
            // No events message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_calendar),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "No events",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            // Events list
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(events) { event ->
                    SamsungEventCard(
                        event = event,
                        onClick = { onEventClick(event) }
                    )
                }
            }
        }
    }
}

@Composable
fun SamsungEventCard(
    event: CalendarEvent,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(event.color))
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Event details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = context.formatEventStartEnd(
                        start = event.start,
                        end = event.end,
                        allDayString = stringResource(R.string.all_day),
                        eventTimeAtRes = R.string.event_time_at,
                        eventTimeRes = R.string.event_time,
                        location = event.location,
                        allDay = event.allDay,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                event.location?.let { location ->
                    if (location.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Text(
                                text = location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper functions
fun getDaysInMonth(month: java.util.Calendar): List<java.util.Calendar?> {
    val calendar = month.clone() as java.util.Calendar
    calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
    
    val daysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1
    
    val days = mutableListOf<java.util.Calendar?>()
    
    // Add empty cells before the first day
    val prevMonth = (calendar.clone() as java.util.Calendar).apply {
        add(java.util.Calendar.MONTH, -1)
        set(java.util.Calendar.DAY_OF_MONTH, getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
    }
    for (i in firstDayOfWeek - 1 downTo 0) {
        val day = (prevMonth.clone() as java.util.Calendar).apply {
            add(java.util.Calendar.DAY_OF_MONTH, -(firstDayOfWeek - 1 - i))
        }
        days.add(day)
    }
    
    // Add all days in the month
    for (day in 1..daysInMonth) {
        val dayCalendar = calendar.clone() as java.util.Calendar
        dayCalendar.set(java.util.Calendar.DAY_OF_MONTH, day)
        days.add(dayCalendar)
    }
    
    // Add days from next month to complete the grid
    val remainingCells = (7 - (days.size % 7)) % 7
    val nextMonth = (calendar.clone() as java.util.Calendar).apply {
        add(java.util.Calendar.MONTH, 1)
        set(java.util.Calendar.DAY_OF_MONTH, 1)
    }
    for (i in 0 until remainingCells) {
        val day = (nextMonth.clone() as java.util.Calendar).apply {
            add(java.util.Calendar.DAY_OF_MONTH, i)
        }
        days.add(day)
    }
    
    return days
}

fun getWeekDates(selectedDate: java.util.Calendar): List<java.util.Calendar> {
    val calendar = selectedDate.clone() as java.util.Calendar
    val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
    calendar.add(java.util.Calendar.DAY_OF_MONTH, -(dayOfWeek - 1))
    
    return (0..6).map {
        val day = calendar.clone() as java.util.Calendar
        day.add(java.util.Calendar.DAY_OF_MONTH, it)
        day
    }
}

fun formatDateForKey(date: java.util.Calendar): String {
    val dayOfMonth = date.get(java.util.Calendar.DAY_OF_MONTH)
    val month = date.get(java.util.Calendar.MONTH) + 1
    val year = date.get(java.util.Calendar.YEAR)
    return "$dayOfMonth/$month/$year"
}

fun java.util.Calendar.isSameDay(other: java.util.Calendar): Boolean {
    return this.get(java.util.Calendar.YEAR) == other.get(java.util.Calendar.YEAR) &&
            this.get(java.util.Calendar.DAY_OF_YEAR) == other.get(java.util.Calendar.DAY_OF_YEAR)
}
