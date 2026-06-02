package openfind.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import openfind.ai.ui.theme.Purple
import openfind.ai.ui.theme.StatusAvailable
import openfind.ai.ui.theme.StatusUnknown
import openfind.ai.ui.theme.TextSecondary
import openfind.ai.ui.theme.White

@Composable
fun AIScoreBadge(
    score: Float,
    modifier: Modifier = Modifier
) {
    val scoreColor = when {
        score >= 9.0f -> StatusAvailable
        score >= 7.0f -> Purple
        score >= 5.0f -> StatusUnknown
        else -> TextSecondary
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(scoreColor.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "AI Score: ${"%.1f".format(score)}/10",
            color = scoreColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
