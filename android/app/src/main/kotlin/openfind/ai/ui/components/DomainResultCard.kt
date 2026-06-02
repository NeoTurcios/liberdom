package openfind.ai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import openfind.ai.domain.model.DomainResult
import openfind.ai.ui.theme.AvailableBg
import openfind.ai.ui.theme.CloudflareGray
import openfind.ai.ui.theme.CloudflareOrange
import openfind.ai.ui.theme.DarkSurfaceVariant
import openfind.ai.ui.theme.NeonGreen
import openfind.ai.ui.theme.StatusAvailable
import openfind.ai.ui.theme.StatusTaken
import openfind.ai.ui.theme.StatusUnknown
import openfind.ai.ui.theme.TakenBg
import openfind.ai.ui.theme.TextPrimary
import openfind.ai.ui.theme.TextSecondary
import openfind.ai.ui.theme.White

@Composable
fun DomainResultCard(
    result: DomainResult,
    isSaved: Boolean,
    onSave: () -> Unit,
    onPdf: () -> Unit,
    onShare: () -> Unit,
    onCopy: () -> Unit,
    isAiEnabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val statusColor = when (result.status) {
        "available" -> StatusAvailable
        "taken" -> StatusTaken
        else -> StatusUnknown
    }

    val statusBg = when (result.status) {
        "available" -> AvailableBg
        "taken" -> TakenBg
        else -> Color(0x1AEAB308)
    }

    val statusLabel = when (result.status) {
        "available" -> "Available"
        "taken" -> "Taken"
        else -> "Unknown"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = result.domain.uppercase(),
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = statusLabel,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            if (isAiEnabled && result.brandScore != null) {
                Spacer(Modifier.height(8.dp))
                AIScoreBadge(score = result.brandScore)
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = result.detail,
                color = TextSecondary,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Technical Details",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = if (expanded) "\u25B2" else "\u25BC",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    if (!result.ip.isNullOrEmpty()) {
                        DetailRow("Server IP:", result.ip)
                    }
                    if (!result.registrar.isNullOrEmpty()) {
                        DetailRow("Registrar:", result.registrar)
                    }
                    if (!result.creationDate.isNullOrEmpty()) {
                        DetailRow("Creation Date:", result.creationDate)
                    }
                    DetailRow("Detection Method:", result.method)

                    if (result.sslActive) {
                        DetailRow("SSL Status:", "Active", StatusAvailable)
                        if (!result.sslIssuer.isNullOrEmpty()) {
                            DetailRow("SSL Issuer:", result.sslIssuer)
                        }
                    }

                    if (result.cloudflare != "none") {
                        val cfColor = if (result.cloudflare == "orange") CloudflareOrange else CloudflareGray
                        val cfLabel = if (result.cloudflare == "orange") "Orange Cloud (Proxy active)" else "Gray Cloud (DNS only)"
                        DetailRow("Cloudflare:", cfLabel, cfColor)
                    }

                    if (result.nsServers.isNotEmpty()) {
                        DetailRow("Name Servers:", result.nsServers.take(3).joinToString(", "))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = onSave) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Save",
                        tint = if (isSaved) NeonGreen else TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = onPdf) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                IconButton(onClick = onCopy) {
                    Icon(
                        imageVector = Icons.Default.CopyAll,
                        contentDescription = "Copy",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, valueColor: Color = TextSecondary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
