package openfind.ai.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import openfind.ai.data.local.entity.HistoryEntity
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
import openfind.ai.viewmodel.LibraryViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = koinViewModel(),
    onNavigateToSearch: (String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val lang = LocalLanguage.current
    val settingsRepository: SettingsRepository = koinInject()
    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(
                    text = Translations.string("library_confirm_title", lang),
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (state.tabMode == "saved")
                        Translations.string("library_confirm_msg_saved", lang)
                    else
                        Translations.string("library_confirm_msg_history", lang),
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onClear(state.tabMode)
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusTaken)
                ) {
                    Text(Translations.string("library_confirm_btn", lang), color = White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showClearDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(Translations.string("library_cancel_btn", lang), color = TextSecondary)
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
                text = Translations.string("library_title", lang),
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (lang == "es") "${state.itemCount} dominios" else "${state.itemCount} domains",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                if (state.itemCount > 0) {
                    Button(
                        onClick = { showClearDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusTaken.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = StatusTaken,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(Translations.string("library_btn_clear", lang), color = StatusTaken, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            TabRow(
                selectedTabIndex = state.tabIndex,
                containerColor = DarkBackground,
                contentColor = NeonGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[state.tabIndex]),
                        color = NeonGreen
                    )
                }
            ) {
                Tab(
                    selected = state.tabIndex == 0,
                    onClick = { viewModel.onTabChange(0) },
                    text = {
                        Text(
                            text = Translations.string("library_tab_saved", lang),
                            color = if (state.tabIndex == 0) NeonGreen else TextSecondary,
                            fontWeight = if (state.tabIndex == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = state.tabIndex == 1,
                    onClick = { viewModel.onTabChange(1) },
                    text = {
                        Text(
                            text = Translations.string("library_tab_history", lang),
                            color = if (state.tabIndex == 1) NeonGreen else TextSecondary,
                            fontWeight = if (state.tabIndex == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(color = NeonGreen)
                }
            } else if (state.savedItems.isEmpty() && state.historyItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = Translations.string("library_empty", lang),
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
                    if (state.tabMode == "saved") {
                        items(state.savedItems, key = { it.domain }) { item ->
                            SwipeableLibraryItem(
                                domain = item.domain,
                                status = item.status,
                                detail = item.detail,
                                method = item.method,
                                ip = item.ip,
                                registrar = item.registrar,
                                creationDate = item.creationDate,
                                timestamp = item.date,
                                onDelete = { viewModel.onDelete("saved", item.domain) },
                                onRecheck = { onNavigateToSearch(item.domain) },
                                onPdf = { viewModel.onExportPdf(item) },
                                onShare = { viewModel.onShare(item) }
                            )
                        }
                    } else {
                        items(state.historyItems, key = { it.id }) { item ->
                            SwipeableHistoryItem(
                                item = item,
                                onDelete = { viewModel.onDelete("history", item.domain) },
                                onRecheck = { onNavigateToSearch(item.domain) },
                                onPdf = { viewModel.onExportPdf(item) },
                                onShare = { viewModel.onShare(item) }
                            )
                        }
                    }
                    item { Spacer(Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableLibraryItem(
    domain: String,
    status: String,
    detail: String,
    method: String,
    ip: String?,
    registrar: String?,
    creationDate: String?,
    timestamp: Long,
    onDelete: () -> Unit,
    onRecheck: () -> Unit,
    onPdf: () -> Unit,
    onShare: () -> Unit
) {
    val lang = LocalLanguage.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
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
                    Text(
                        text = domain.uppercase(),
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
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
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                val detailText = when (status) {
                    "available" -> Translations.string("detail_available", lang)
                    "taken" -> Translations.string("detail_taken", lang)
                    else -> Translations.string("detail_unknown", lang)
                }

                Text(
                    text = detailText,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(timestamp)),
                        color = TextSecondary.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onPdf, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.PictureAsPdf, "PDF", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Share, "Share", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onRecheck, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Search, "Recheck", tint = NeonGreen, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableHistoryItem(
    item: HistoryEntity,
    onDelete: () -> Unit,
    onRecheck: () -> Unit,
    onPdf: () -> Unit,
    onShare: () -> Unit
) {
    SwipeableLibraryItem(
        domain = item.domain,
        status = item.status,
        detail = item.detail,
        method = item.method,
        ip = item.ip,
        registrar = item.registrar,
        creationDate = item.creationDate,
        timestamp = item.date,
        onDelete = onDelete,
        onRecheck = onRecheck,
        onPdf = onPdf,
        onShare = onShare
    )
}
