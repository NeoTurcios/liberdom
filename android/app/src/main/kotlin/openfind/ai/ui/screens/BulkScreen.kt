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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import openfind.ai.ui.components.DomainResultCard
import openfind.ai.ui.components.DynamicIslandHeader
import openfind.ai.ui.theme.DarkBackground
import openfind.ai.ui.theme.DarkSurfaceVariant
import openfind.ai.ui.theme.GreenEnd
import openfind.ai.ui.theme.GreenStart
import openfind.ai.ui.theme.NeonGreen
import openfind.ai.ui.theme.StatusAvailable
import openfind.ai.ui.theme.StatusTaken
import openfind.ai.ui.theme.TextPrimary
import openfind.ai.ui.theme.TextSecondary
import openfind.ai.ui.theme.White
import openfind.ai.viewmodel.BulkViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun BulkScreen(
    viewModel: BulkViewModel = koinViewModel(),
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(state.results.size) {
        if (state.results.isNotEmpty()) {
            listState.animateScrollToItem(state.results.size - 1)
        }
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
                isScanning = state.isRunning,
                scanningDomain = "",
                currentLanguage = "EN",
                onToggleLanguage = {},
                onSettingsClick = onNavigateToSettings,
                onTitleClick = {}
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Bulk Domain Check",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Check multiple domains, write one per line.",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            androidx.compose.material3.OutlinedTextField(
                value = state.bulkInput,
                onValueChange = { viewModel.onBulkInputChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                placeholder = {
                    Text(
                        text = "domain.com\nexample.org\nmybrand.io",
                        color = TextSecondary
                    )
                },
                textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                    color = White,
                    fontSize = 14.sp
                ),
                shape = RoundedCornerShape(14.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                    focusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.3f)
                )
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.onBulkCheck() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = GreenStart),
                shape = RoundedCornerShape(14.dp),
                enabled = !state.isRunning && state.bulkInput.isNotBlank()
            ) {
                if (state.isRunning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = DarkBackground,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Scan Bulk Domains",
                        color = DarkBackground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (state.isRunning || state.stats.total > 0) {
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceVariant)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Checked", "${state.stats.checked}/${state.stats.total}", TextSecondary)
                    StatItem("Free", "${state.stats.free}", StatusAvailable)
                    StatItem("Taken", "${state.stats.taken}", StatusTaken)
                }

                if (state.isRunning) {
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NeonGreen,
                        trackColor = DarkSurfaceVariant
                    )
                }
            }

            if (state.results.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.onClearResults() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceVariant
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Clear Results", color = TextSecondary, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.results, key = { it.domain }) { result ->
                    DomainResultCard(
                        result = result,
                        isSaved = false,
                        onSave = { viewModel.onToggleSave(result) },
                        onPdf = { viewModel.onExportPdf(result) },
                        onShare = { viewModel.onShare(result) },
                        onCopy = { viewModel.onCopy(result) },
                        isAiEnabled = false
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
