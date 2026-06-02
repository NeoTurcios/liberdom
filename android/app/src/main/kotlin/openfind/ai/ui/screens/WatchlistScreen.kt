package openfind.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import openfind.ai.data.local.entity.WatchlistEntity
import openfind.ai.data.repository.SettingsRepository
import openfind.ai.ui.components.DynamicIslandHeader
import openfind.ai.ui.theme.DarkBackground
import openfind.ai.ui.theme.DarkSurface
import openfind.ai.ui.theme.DarkSurfaceVariant
import openfind.ai.ui.theme.NeonGreen
import openfind.ai.ui.theme.StatusAvailable
import openfind.ai.ui.theme.StatusTaken
import openfind.ai.ui.theme.StatusUnknown
import openfind.ai.ui.theme.TextPrimary
import openfind.ai.ui.theme.TextSecondary
import openfind.ai.ui.theme.White
import openfind.ai.ui.utils.LocalLanguage
import openfind.ai.ui.utils.Translations
import openfind.ai.viewmodel.WatchlistViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel = koinViewModel(),
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val lang = LocalLanguage.current
    val settingsRepository: SettingsRepository = koinInject()
    var showAddDialog by remember { mutableStateOf(false) }

    val availableIntervals = listOf(6, 12, 24, 48)

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = Translations.string("watchlist_add_domain", lang),
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.newDomainInput,
                        onValueChange = { viewModel.onNewDomainInputChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(Translations.string("watchlist_placeholder_domain", lang), color = TextSecondary)
                        },
                        singleLine = true,
                        textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(color = White),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.newLabelInput,
                        onValueChange = { viewModel.onNewLabelInputChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(if (lang == "es") "Etiqueta (opcional)" else "Label (optional)", color = TextSecondary)
                        },
                        singleLine = true,
                        textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(color = White),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = Translations.string("watchlist_interval", lang),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(8.dp))

                    availableIntervals.forEach { hours ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.selectedInterval == hours,
                                onClick = { viewModel.onSelectInterval(hours) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = NeonGreen,
                                    unselectedColor = TextSecondary
                                )
                            )
                            Text(
                                text = "${hours}h",
                                color = TextPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onAdd()
                        showAddDialog = false
                    },
                    enabled = state.newDomainInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    Text(Translations.string("watchlist_add_btn", lang), color = DarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showAddDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(Translations.string("watchlist_cancel_btn", lang), color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            DynamicIslandHeader(
                isScanning = false,
                scanningDomain = "",
                currentLanguage = lang,
                onToggleLanguage = {
                    val newLang = if (lang == "es") "en" else "es"
                    settingsRepository.setLanguage(newLang)
                },
                onSettingsClick = onNavigateToSettings,
                onTitleClick = {}
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = Translations.string("watchlist_title", lang),
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            if (state.watchlist.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = Translations.string("watchlist_empty", lang),
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.watchlist, key = { it.domain }) { item ->
                        WatchlistItemCard(
                            item = item,
                            onRemove = { viewModel.onRemove(item.domain) },
                            onToggleNotify = { viewModel.onToggleNotify(item.domain) },
                            onChangeInterval = { hours ->
                                viewModel.onChangeInterval(item.domain, hours)
                            }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = NeonGreen,
            contentColor = DarkBackground,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchlistItemCard(
    item: WatchlistEntity,
    onRemove: () -> Unit,
    onToggleNotify: () -> Unit,
    onChangeInterval: (Int) -> Unit
) {
    val lang = LocalLanguage.current
    val intervals = listOf(6, 12, 24, 48)
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRemove()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(StatusTaken.copy(alpha = 0.3f))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Default.Delete, "Delete", tint = StatusTaken)
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.domain,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.label.isNotEmpty()) {
                            Text(
                                text = item.label,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onToggleNotify,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (item.notifyEnabled)
                                Icons.Default.NotificationsActive
                            else
                                Icons.Default.NotificationsOff,
                            contentDescription = "Toggle Notifications",
                            tint = if (item.notifyEnabled) NeonGreen else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                item.lastStatus?.let { status ->
                    val statusColor = when (status) {
                        "available" -> StatusAvailable
                        "taken" -> StatusTaken
                        else -> StatusUnknown
                    }
                    val statusLabel = when (status) {
                        "available" -> Translations.string("status_available", lang)
                        "taken" -> Translations.string("status_taken", lang)
                        else -> Translations.string("status_unknown", lang)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (lang == "es") "Último Estado:" else "Last Status:", color = TextSecondary, fontSize = 11.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = statusLabel,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                item.lastChecked?.let { timestamp ->
                    Spacer(Modifier.height(4.dp))
                    val lastCheckedLabel = if (lang == "es") "Última comprobación" else "Last checked"
                    Text(
                        text = "$lastCheckedLabel: ${java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(timestamp))}",
                        color = TextSecondary.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    intervals.forEach { hours ->
                        val isSelected = item.intervalHours == hours
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) NeonGreen.copy(alpha = 0.15f)
                                    else DarkBackground
                                )
                                .then(
                                    Modifier.clickable { onChangeInterval(hours) }
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${hours}h",
                                color = if (isSelected) NeonGreen else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
