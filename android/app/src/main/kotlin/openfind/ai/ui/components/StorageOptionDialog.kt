package openfind.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import openfind.ai.ui.theme.DarkBackground
import openfind.ai.ui.theme.DarkSurfaceVariant
import openfind.ai.ui.theme.NeonGreen
import openfind.ai.ui.theme.Purple
import openfind.ai.ui.theme.TextPrimary
import openfind.ai.ui.theme.TextSecondary
import openfind.ai.ui.theme.White

@Composable
fun StorageOptionDialog(
    onDismiss: () -> Unit,
    onOptionA: () -> Unit,
    onOptionB: () -> Unit
) {
    var selected by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(DarkBackground)
                .border(2.dp, DarkSurfaceVariant, RoundedCornerShape(28.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚠️",
                fontSize = 28.sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Memory limit reached",
                color = White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "The saved list is full (Limit: 10 items). What would you like to do?",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            OptionCard(
                label = "A",
                title = "Option A: Empty the entire list",
                subtitle = "Wipe out all saved domains to start fresh with fully clean storage.",
                isSelected = selected == "A",
                onClick = { selected = "A" }
            )

            Spacer(Modifier.height(12.dp))

            OptionCard(
                label = "B",
                title = "Option B: Dynamic Auto-Cleanup",
                subtitle = "Automatically delete the oldest saved items when new ones are added.",
                isSelected = selected == "B",
                onClick = { selected = "B" }
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    border = androidx.compose.foundation.BorderStroke(2.dp, TextSecondary.copy(alpha = 0.3f))
                ) {
                    Text("Cancel", color = TextSecondary)
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = {
                        when (selected) {
                            "A" -> {
                                onOptionA()
                                onDismiss()
                            }
                            "B" -> {
                                onOptionB()
                                onDismiss()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selected != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected != null) Purple else TextSecondary.copy(alpha = 0.3f)
                    )
                ) {
                    Text("Confirm", color = White)
                }
            }
        }
    }
}

@Composable
private fun OptionCard(
    label: String,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Purple else TextSecondary.copy(alpha = 0.15f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Purple.copy(alpha = 0.15f) else DarkSurfaceVariant)
            .border(if (isSelected) 2.dp else 1.5.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(18.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isSelected) Purple else TextSecondary.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
