package com.mhss.app.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mhss.app.ui.R
import com.mhss.app.domain.model.Mood
import com.mhss.app.domain.model.DiaryEntry

@Composable
fun MoodCircularBar(
    modifier: Modifier = Modifier,
    entries: List<DiaryEntry>,
    strokeWidth: Float = 40f,
    showPercentage: Boolean = true,
    onClick: () -> Unit = {}
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
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val mostFrequentMood by remember(entries) {
                derivedStateOf {
                    if (entries.isEmpty()) Mood.OKAY
                    else {
                        // if multiple ones with the same frequency, return the most positive one
                        val entriesGrouped = entries.groupBy { it.mood }
                        val max = entriesGrouped.maxOf { it.value.size }
                        entriesGrouped
                            .filter { it.value.size == max }
                            .maxByOrNull { it.key.value }?.key ?: Mood.OKAY
                    }
                }
            }
            val moods by remember(entries) {
                derivedStateOf {
                    entries.toPercentages()
                }
            }
            Text(
                text = stringResource(R.string.mood_summary),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (entries.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    var currentAngle = remember { 90f }
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        for ((mood, percentage) in moods) {
                            drawArc(
                                color = mood.color,
                                startAngle = currentAngle,
                                sweepAngle = percentage * 360f,
                                useCenter = false,
                                size = Size(size.width, size.width),
                                style = Stroke(strokeWidth)
                            )
                            currentAngle += percentage * 360f
                        }
                    }
                    if (showPercentage) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            for ((mood, percentage) in moods) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(mood.iconRes),
                                        contentDescription = stringResource(mood.titleRes),
                                        tint = mood.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = stringResource(
                                            R.string.percent,
                                            (percentage * 100).toInt()
                                        ),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    } else {
                        Icon(
                            painter = painterResource(mostFrequentMood.iconRes),
                            contentDescription = stringResource(mostFrequentMood.titleRes),
                            tint = mostFrequentMood.color,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.your_mood_was))
                        append(" ")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = mostFrequentMood.color
                            )
                        ) {
                            append(stringResource(mostFrequentMood.titleRes))
                        }
                        append(" ")
                        append(stringResource(R.string.most_of_the_time))
                    },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_data_yet),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

fun List<DiaryEntry>.toPercentages(): Map<Mood, Float> {
    return this
        .sortedBy { it.mood.value }
        .groupingBy { it.mood }
        .eachCount()
        .mapValues { it.value / this.size.toFloat() }
}

@Composable
@Preview
fun MoodCircularBarPreview() {
    MoodCircularBar(
        entries = listOf(
            DiaryEntry(
                id = 1,
                mood = Mood.AWESOME
            ),
            DiaryEntry(
                id = 1,
                mood = Mood.AWESOME
            ),
            DiaryEntry(
                id = 2,
                mood = Mood.GOOD,
            ),
            DiaryEntry(
                id = 3,
                mood = Mood.OKAY,
            ),
            DiaryEntry(
                id = 3,
                mood = Mood.OKAY,
            ),
            DiaryEntry(
                id = 4,
                mood = Mood.BAD,
            ),
            DiaryEntry(
                id = 5,
                mood = Mood.TERRIBLE,
            ),
            DiaryEntry(
                id = 5,
                mood = Mood.TERRIBLE,
            )
        )
    )
}