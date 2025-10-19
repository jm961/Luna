package com.mhss.app.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mhss.app.ui.R
import com.mhss.app.domain.model.Calendar
import com.mhss.app.ui.components.common.MyBrainAppBar
import com.mhss.app.util.permissions.Permission
import com.mhss.app.ui.navigation.Screen
import com.mhss.app.util.date.monthName
import com.mhss.app.util.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun CalendarScreen(
    navController: NavHostController,
    viewModel: CalendarViewModel = koinViewModel()
) {
    val state = viewModel.uiState
    val lazyListState = rememberLazyListState()
    var settingsVisible by remember { mutableStateOf(false) }
    val readCalendarPermissionState = rememberPermissionState(
        permission = Permission.READ_CALENDAR
    )
    val month by remember(state.events) {
        derivedStateOf {
            state.events.values.elementAt(lazyListState.firstVisibleItemIndex)
                .first().start.monthName()
        }
    }
    val scope = rememberCoroutineScope()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (readCalendarPermissionState.isGranted) FloatingActionButton(
                onClick = {
                    navController.navigate(
                        Screen.CalendarEventDetailsScreen(
                            null
                        )
                    )
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
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (readCalendarPermissionState.isGranted) {
                LaunchedEffect(true) {
                    viewModel.onEvent(
                        CalendarViewModelEvent
                            .ReadPermissionChanged(readCalendarPermissionState.isGranted)
                    )
                }
                // Modern Header with Month Selector
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.calendar),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.events.isNotEmpty()) MonthDropDownMenu(
                            selectedMonth = month,
                            months = state.months,
                            onMonthSelected = { selected ->
                                scope.launch {
                                    lazyListState.scrollToItem(
                                        state.events.values.indexOfFirst {
                                            it.first().start.monthName() == selected
                                        }
                                    )
                                }
                            }
                        )
                        IconButton(onClick = { settingsVisible = !settingsVisible }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.ic_settings_sliders),
                                contentDescription = stringResource(R.string.include_calendars)
                            )
                        }
                    }
                }
                AnimatedVisibility(visible = settingsVisible) {
                    CalendarSettingsSection(
                        calendars = state.calendars, onCalendarClicked = {
                            viewModel.onEvent(CalendarViewModelEvent.IncludeCalendar(it))
                        }
                    )
                }
                LazyColumn(
                    state = lazyListState,
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    state.events.forEach { (day, events) ->
                        item {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Text(
                                    text = day.substring(0, day.indexOf(",")),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                events.forEach { event ->
                                    CalendarEventItem(event = event, onClick = {
                                        navController.navigate(
                                            Screen.CalendarEventDetailsScreen(
                                                Base64.encode(Json.encodeToString(event).toByteArray())
                                            )
                                        )
                                    })
                                }
                            }
                        }
                    }
                }
            } else {
                NoReadCalendarPermissionMessage(
                    shouldShowRationale = readCalendarPermissionState.shouldShowRationale,
                    onOpenSettings = {
                        readCalendarPermissionState.openAppSettings()
                    },
                    onRequest = {
                        readCalendarPermissionState.launchRequest()
                    }
                )
            }
        }
    }

}

@Composable
fun NoReadCalendarPermissionMessage(
    shouldShowRationale: Boolean,
    onOpenSettings: () -> Unit,
    onRequest: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_read_calendar_permission_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        if (shouldShowRationale) {
            TextButton(onClick = onOpenSettings) {
                Text(text = stringResource(R.string.go_to_settings))
            }

        } else {
            TextButton(onClick = { onRequest() }) {
                Text(text = stringResource(R.string.grant_permission))
            }
        }
    }
}

@Composable
fun MonthDropDownMenu(
    selectedMonth: String,
    months: List<String>,
    onMonthSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        Modifier.clickable { expanded = true }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AnimatedContent(targetState = selectedMonth, label = "") { month ->
                Text(
                    text = month,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            months.forEach {
                DropdownMenuItem(
                    onClick = {
                        onMonthSelected(it)
                        expanded = false
                    },
                    text = {
                        Text(text = it)
                    })
            }
        }
    }
}

@Composable
fun CalendarSettingsSection(
    calendars: Map<String, List<Calendar>>,
    onCalendarClicked: (Calendar) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.include_calendars),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        calendars.keys.forEach { calendar ->
            var expanded by remember { mutableStateOf(false) }
            Box(
                Modifier
                    .clickable { expanded = true }
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = calendar,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    calendars[calendar]?.forEach { subCalendar ->
                        DropdownMenuItem(
                            onClick = {
                                onCalendarClicked(subCalendar)
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = subCalendar.included,
                                        onCheckedChange = { onCalendarClicked(subCalendar) },
                                        colors = CheckboxDefaults.colors(
                                            uncheckedColor = Color(subCalendar.color),
                                            checkedColor = Color(subCalendar.color)
                                        )
                                    )
                                    Text(
                                        text = subCalendar.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            })
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}