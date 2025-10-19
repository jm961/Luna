package com.mhss.app.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mhss.app.ui.R
import com.mhss.app.domain.model.CalendarEvent
import com.mhss.app.util.permissions.Permission
import com.mhss.app.util.permissions.rememberPermissionState

@Composable
fun CalendarDashboardWidget(
    modifier: Modifier = Modifier,
    events: Map<String, List<CalendarEvent>>,
    onPermission: (Boolean) -> Unit = {},
    onClick: () -> Unit = {},
    onAddEventClicked: () -> Unit = {},
    onEventClicked: (CalendarEvent) -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        val readCalendarPermissionState = rememberPermissionState(
            Permission.READ_CALENDAR
        )
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_add),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(R.string.calendar),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                IconButton(
                    onClick = onAddEventClicked,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painterResource(R.drawable.ic_add),
                        stringResource(R.string.add_event),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (readCalendarPermissionState.isGranted) {
                    if (events.isEmpty()) {
                        item {
                            LaunchedEffect(true) { onPermission(true) }
                            Text(
                                text = stringResource(R.string.no_events),
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        events.forEach { (day, events) ->
                            item {
                                LaunchedEffect(true) { onPermission(true) }
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(
                                        text = day,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    events.forEach { event ->
                                        CalendarEventSmallItem(event = event, onClick = {
                                            onEventClicked(event)
                                        })
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        LaunchedEffect(true) { onPermission(false) }
                        NoReadCalendarPermissionMessage(
                            shouldShowRationale = false,
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
    }
}