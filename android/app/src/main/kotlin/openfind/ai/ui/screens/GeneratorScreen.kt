package openfind.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import openfind.ai.ui.components.AIScoreBadge
import openfind.ai.ui.components.DynamicIslandHeader
import openfind.ai.ui.theme.DarkBackground
import openfind.ai.ui.theme.DarkSurfaceVariant
import openfind.ai.ui.theme.GreenEnd
import openfind.ai.ui.theme.GreenStart
import openfind.ai.ui.theme.NeonGreen
import openfind.ai.ui.theme.Purple
import openfind.ai.ui.theme.StatusAvailable
import openfind.ai.ui.theme.StatusTaken
import openfind.ai.ui.theme.TextPrimary
import openfind.ai.ui.theme.TextSecondary
import openfind.ai.ui.theme.White
import openfind.ai.viewmodel.GeneratorViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeneratorScreen(
    viewModel: GeneratorViewModel = koinViewModel(),
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.state.collectAsState()

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
                isScanning = state.isChecking,
                scanningDomain = "",
                currentLanguage = "EN",
                onToggleLanguage = {},
                onSettingsClick = onNavigateToSettings,
                onTitleClick = {}
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Creative Generator",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Enter a keyword to generate unique brand concepts.",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = state.keyword,
                onValueChange = { viewModel.onKeywordChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "e.g. tech, cloud, smart, medita",
                        color = TextSecondary
                    )
                },
                singleLine = true,
                textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(color = White),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.3f),
                    focusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.3f)
                )
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Extensions to check:",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.availableTlds.forEach { tld ->
                    FilterChip(
                        selected = tld in state.selectedTlds,
                        onClick = { viewModel.onToggleTld(tld) },
                        label = { Text(".$tld", fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Purple.copy(alpha = 0.3f),
                            selectedLabelColor = Purple,
                            containerColor = DarkSurfaceVariant,
                            labelColor = TextSecondary
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { viewModel.onGenerate() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = GreenStart),
                shape = RoundedCornerShape(14.dp),
                enabled = !state.isGenerating && state.keyword.length >= 2
            ) {
                if (state.isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = DarkBackground,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Generate Names",
                        color = DarkBackground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.generatedItems, key = { it.domain }) { item ->
                    GeneratorItemCard(
                        item = item,
                        onCheck = { viewModel.onCheckDomain(item.domain) },
                        isAiEnabled = false
                    )
                }
            }
        }
    }
}

@Composable
private fun GeneratorItemCard(
    item: openfind.ai.domain.model.GeneratorItem,
    onCheck: () -> Unit,
    isAiEnabled: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.domain,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            if (item.status != null) {
                val color = if (item.status == "available") StatusAvailable else StatusTaken
                val label = if (item.status == "available") "Available" else "Taken"
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = label,
                        color = color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(
                                color.copy(alpha = 0.15f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                    if (isAiEnabled && item.brandScore != null) {
                        Spacer(Modifier.height(4.dp))
                        AIScoreBadge(score = item.brandScore)
                    }
                }
            } else {
                Button(
                    onClick = onCheck,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenStart),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Check",
                        color = DarkBackground,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
