package openfind.ai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val OpenFindColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = DarkBackground,
    secondary = Purple,
    onSecondary = White,
    background = DarkBackground,
    onBackground = White,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = StatusTaken,
    onError = White
)

private val OpenFindShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun OpenFindTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OpenFindColorScheme,
        typography = OpenFindTypography,
        shapes = OpenFindShapes,
        content = content
    )
}
