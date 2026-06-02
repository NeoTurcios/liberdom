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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.navigation.NavController
import openfind.ai.ui.components.DomainResultCard
import openfind.ai.ui.components.DynamicIslandHeader
import openfind.ai.ui.components.SearchBar
import openfind.ai.ui.theme.DarkBackground
import openfind.ai.ui.theme.GreenEnd
import openfind.ai.ui.theme.GreenStart
import openfind.ai.ui.theme.NeonGreen
import openfind.ai.ui.theme.Purple
import openfind.ai.ui.theme.TextPrimary
import openfind.ai.ui.theme.TextSecondary
import openfind.ai.ui.theme.White
import openfind.ai.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    navController: NavController? = null,
    viewModel: SearchViewModel = koinViewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(navController) {
        val prefillDomain = navController
            ?.currentBackStackEntry
            ?.savedStateHandle
            ?.get<String>("prefill_domain")
        if (!prefillDomain.isNullOrBlank()) {
            viewModel.onDomainInputChange(prefillDomain)
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("prefill_domain")
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DynamicIslandHeader(
                isScanning = state.isLoading,
                scanningDomain = state.domainInput,
                currentLanguage = state.language,
                onToggleLanguage = { viewModel.onToggleLanguage() },
                onSettingsClick = onNavigateToSettings,
                onTitleClick = onNavigateToAbout
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Find your next domain",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Low-level direct and hybrid DNS/WHOIS search without intermediaries or premium fees.",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            SearchBar(
                value = state.domainInput,
                onValueChange = { viewModel.onDomainInputChange(it) },
                onSearch = { viewModel.onSearch() },
                placeholder = "Search domain..."
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Audit",
                        tint = Purple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Advanced Audit",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Switch(
                    checked = state.isAuditEnabled,
                    onCheckedChange = { viewModel.onToggleAudit(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = Purple,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = TextSecondary.copy(alpha = 0.2f)
                    )
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { viewModel.onSearch() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GreenStart, GreenEnd)
                        )
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenStart
                ),
                shape = RoundedCornerShape(14.dp),
                enabled = !state.isLoading && state.domainInput.isNotBlank()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = DarkBackground,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Check Domain",
                        color = DarkBackground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            state.error?.let { error ->
                Text(
                    text = error,
                    color = openfind.ai.ui.theme.StatusTaken,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
            }

            state.result?.let { result ->
                DomainResultCard(
                    result = result,
                    isSaved = state.isResultSaved,
                    onSave = { viewModel.onSave() },
                    onPdf = { viewModel.onExportPdf() },
                    onShare = { viewModel.onShare() },
                    onCopy = { viewModel.onCopy() },
                    isAiEnabled = state.isAiEnabled
                )
                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Designed with love and open source\nDeveloped by neopunto.com\nContact: hola@neopunto.com | Non-Commercial License \u00A9 2026",
                color = TextSecondary.copy(alpha = 0.5f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAbout() },
                lineHeight = 16.sp
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}
