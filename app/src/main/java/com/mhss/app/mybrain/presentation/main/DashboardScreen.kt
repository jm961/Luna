package com.mhss.app.mybrain.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mhss.app.ui.R
import com.mhss.app.presentation.CalendarDashboardWidget
import com.mhss.app.presentation.MoodCircularBar
import com.mhss.app.presentation.TasksDashboardWidget
import com.mhss.app.ui.components.common.ModernCard
import com.mhss.app.ui.navigation.Screen
import com.mhss.app.ui.theme.PrimaryColor
import com.mhss.app.ui.theme.SecondaryColor
import com.mhss.app.ui.theme.TertiaryColor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel
import java.time.LocalTime
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: MainViewModel = koinViewModel()
) {
    val greeting = remember {
        when (LocalTime.now().hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }
    
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LaunchedEffect(true) { viewModel.onDashboardEvent(DashboardEvent.InitAll) }
        LazyColumn(
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = paddingValues.calculateBottomPadding() + 80.dp,
                start = 20.dp,
                end = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Modern Hero Section with Greeting
            item {
                HeroSection(
                    greeting = greeting,
                    tasksCount = viewModel.uiState.dashBoardTasks.size,
                    eventsCount = viewModel.uiState.dashBoardEvents.values.sumOf { it.size },
                    diaryEntriesCount = viewModel.uiState.dashBoardEntries.size
                )
            }
            
            // Quick Stats Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.ic_check,
                        label = "Tasks",
                        value = viewModel.uiState.dashBoardTasks.size.toString(),
                        color = PrimaryColor,
                        onClick = { navController.navigate(Screen.TasksScreen()) }
                    )
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.ic_calendar,
                        label = "Events",
                        value = viewModel.uiState.dashBoardEvents.values.sumOf { it.size }.toString(),
                        color = SecondaryColor,
                        onClick = { navController.navigate(Screen.CalendarScreen) }
                    )
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.ic_description,
                        label = "Entries",
                        value = viewModel.uiState.dashBoardEntries.size.toString(),
                        color = TertiaryColor,
                        onClick = { navController.navigate(Screen.DiaryScreen) }
                    )
                }
            }
            
            // Calendar Widget
            item {
                ModernSectionHeader(title = "Upcoming Events")
                Spacer(modifier = Modifier.height(8.dp))
                CalendarDashboardWidget(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 400.dp),
                    events = viewModel.uiState.dashBoardEvents,
                    onClick = {
                        navController.navigate(Screen.CalendarScreen)
                    },
                    onPermission = {
                        viewModel.onDashboardEvent(DashboardEvent.ReadPermissionChanged(it))
                    },
                    onAddEventClicked = {
                        navController.navigate(Screen.CalendarEventDetailsScreen())
                    },
                    onEventClicked = {
                        navController.navigate(
                            Screen.CalendarEventDetailsScreen(
                                Base64.encode(Json.encodeToString(it).toByteArray())
                            )
                        )
                    }
                )
            }
            
            // Tasks Widget
            item {
                ModernSectionHeader(title = "Today's Tasks")
                Spacer(modifier = Modifier.height(8.dp))
                TasksDashboardWidget(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 400.dp),
                    tasks = viewModel.uiState.dashBoardTasks,
                    onCheck = { task, completed ->
                        viewModel.onDashboardEvent(DashboardEvent.CompleteTask(task, completed))
                    },
                    onTaskClick = {
                        navController.navigate(Screen.TaskDetailScreen(it.id))
                    },
                    onAddClick = {
                        navController.navigate(Screen.TasksScreen(addTask = true))
                    },
                    onClick = {
                        navController.navigate(Screen.TasksScreen())
                    }
                )
            }
            
            // Analytics Row
            item {
                ModernSectionHeader(title = "Your Progress")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        MoodCircularBar(
                            entries = viewModel.uiState.dashBoardEntries,
                            showPercentage = false,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                navController.navigate(Screen.DiaryChartScreen)
                            }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        TasksSummaryCard(
                            modifier = Modifier.fillMaxWidth(),
                            tasks = viewModel.uiState.summaryTasks
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroSection(
    greeting: String,
    tasksCount: Int,
    eventsCount: Int,
    diaryEntriesCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        PrimaryColor.copy(alpha = 0.9f),
                        SecondaryColor.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.displaySmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Welcome back to MyBrain",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HeroStat(label = "Tasks", value = tasksCount)
            HeroStat(label = "Events", value = eventsCount)
            HeroStat(label = "Entries", value = diaryEntriesCount)
        }
    }
}

@Composable
private fun HeroStat(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun QuickStatCard(
    modifier: Modifier = Modifier,
    iconRes: Int,
    label: String,
    value: String,
    color: Color,
    onClick: () -> Unit
) {
    ModernCard(
        modifier = modifier.aspectRatio(1f),
        onClick = onClick,
        elevation = 6.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ModernSectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PrimaryColor, SecondaryColor)
                    )
                )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}