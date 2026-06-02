package openfind.ai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import openfind.ai.data.local.entity.AiModelInfo

private val Slate900 = Color(0xFF0F172A)
private val Slate800 = Color(0xFF1E293B)
private val Slate700 = Color(0xFF334155)
private val Slate500 = Color(0xFF64748B)
private val Slate400 = Color(0xFF94A3B8)
private val Slate300 = Color(0xFFCBD5E1)
private val Emerald500 = Color(0xFF10B981)
private val Emerald400 = Color(0xFF34D399)
private val Amber500 = Color(0xFFF59E0B)
private val Amber600 = Color(0xFFD97706)
private val Red400 = Color(0xFFF87171)
private val Red500 = Color(0xFFEF4444)
private val Violet500 = Color(0xFF8B5CF6)
private val Violet600 = Color(0xFF7C3AED)
private val Cyan500 = Color(0xFF06B6D4)

@Composable
fun AiSettingsSection(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modelInfo: AiModelInfo,
    onRequestEnable: () -> Unit
) {
    var showEnableDialog by remember { mutableStateOf(false) }
    var showDisableDialog by remember { mutableStateOf(false) }
    var isEnabling by remember { mutableStateOf(false) }
    var enableSuccess by remember { mutableStateOf<Boolean?>(null) }
    var enableProgress by remember { mutableFloatStateOf(0f) }
    var enableAttempted by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val switchTrackColor by animateColorAsState(
        targetValue = if (isEnabled && modelInfo.isLoaded) Emerald500 else Slate700,
        label = "trackColor"
    )

    val accentColor = if (modelInfo.recommended) Emerald500
    else if (modelInfo.isCompatible) Amber500
    else Red400

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "Local AI Engine",
            subtitle = "On-device brand evaluation powered by TensorFlow Lite"
        )

        // ── Enable/Disable Card ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate800),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Brand Evaluation",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (modelInfo.isLoaded) "Model loaded and active"
                        else if (isEnabled) "Attempting to load..."
                        else "Disabled — tap to enable",
                        color = Slate400,
                        fontSize = 12.sp
                    )
                }
                if (isEnabling) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Violet500,
                        strokeWidth = 2.dp,
                        strokeCap = StrokeCap.Round
                    )
                } else {
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { toggled ->
                            if (toggled) {
                                showEnableDialog = true
                            } else {
                                showDisableDialog = true
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Emerald500,
                            uncheckedThumbColor = Slate300,
                            uncheckedTrackColor = Slate700
                        )
                    )
                }
            }

            if (isEnabling) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Loading model...",
                            color = Slate300,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${(enableProgress * 100).toInt()}%",
                            color = Violet500,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Slate700)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(enableProgress)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Violet500)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = enableAttempted && enableSuccess != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (enableSuccess == true) Icons.Default.CheckCircle
                        else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (enableSuccess == true) Emerald400 else Amber500,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (enableSuccess == true) "AI engine loaded successfully."
                        else "Could not load model. Check available RAM.",
                        color = if (enableSuccess == true) Emerald400 else Amber500,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // ── Device Info Card ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate800),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Device Status",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Slate700)
                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(
                    icon = Icons.Default.Storage,
                    iconTint = Cyan500,
                    label = "Device RAM",
                    value = "%.1f GB".format(modelInfo.deviceRamGb)
                )
                Spacer(modifier = Modifier.height(10.dp))

                InfoRow(
                    icon = Icons.Default.Memory,
                    iconTint = Violet500,
                    label = "Model Size",
                    value = "%.0f MB".format(modelInfo.modelSizeMb)
                )
                Spacer(modifier = Modifier.height(10.dp))

                InfoRow(
                    icon = Icons.Default.Info,
                    iconTint = if (modelInfo.isCompatible) Emerald400 else Red400,
                    label = "Compatibility",
                    value = if (modelInfo.isCompatible) "Compatible" else "Insufficient RAM"
                )
            }
        }

        // ── Compatibility Status Card ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (modelInfo.recommended) Color(0xFF064E3B)
                else if (modelInfo.isCompatible) Color(0xFF78350F)
                else Color(0xFF7F1D1D)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = if (modelInfo.recommended) Icons.Default.CheckCircle
                    else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (modelInfo.recommended) Emerald400
                    else if (modelInfo.isCompatible) Amber500
                    else Red400,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = when {
                            modelInfo.recommended -> "Optimal Device Detected"
                            modelInfo.isCompatible -> "Compatible — AI available"
                            else -> "AI Not Supported"
                        },
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when {
                            modelInfo.recommended ->
                                "Your device has enough RAM for optimal AI performance. 4-thread inference with NNAPI acceleration available."
                            modelInfo.isCompatible ->
                                "AI can run with 2 threads. Performance may be slightly reduced but fully functional."
                            else ->
                                "Your device has less than 2.5GB free RAM. AI is disabled to preserve system stability. Heuristic evaluation still works."
                        },
                        color = Slate300.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // ── Memory Usage Warning ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate800),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Slate400,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "About Local AI",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "OpenFind AI runs a lightweight TensorFlow Lite model entirely on-device. " +
                                "No data is ever sent to external servers. The model evaluates brand quality based on " +
                                "phonetics, memorability, length, and TLD fitness. " +
                                "When AI is disabled, a fast heuristic engine provides instant fallback evaluations. " +
                                "The AI module is completely optional — ideal for low-end devices.",
                        color = Slate400,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }

    // ── Enable Confirmation Dialog ──
    if (showEnableDialog) {
        AlertDialog(
            onDismissRequest = { showEnableDialog = false },
            containerColor = Slate900,
            title = {
                Text(
                    text = "Enable Local AI?",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "This will load a ~${"%.0f".format(modelInfo.modelSizeMb)}MB TensorFlow Lite model into device memory.",
                        color = Slate300,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            InfoRow(
                                icon = Icons.Default.Storage,
                                iconTint = Cyan500,
                                label = "Required RAM",
                                value = "≥ 2.5 GB free"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            InfoRow(
                                icon = Icons.Default.Memory,
                                iconTint = Violet500,
                                label = "Model Size",
                                value = "%.0f MB".format(modelInfo.modelSizeMb)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            InfoRow(
                                icon = Icons.Default.Info,
                                iconTint = if (modelInfo.isCompatible) Emerald400 else Red400,
                                label = "Device RAM",
                                value = "%.1f GB total".format(modelInfo.deviceRamGb)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            InfoRow(
                                icon = if (modelInfo.isCompatible) Icons.Default.CheckCircle
                                else Icons.Default.Warning,
                                iconTint = if (modelInfo.isCompatible) Emerald400 else Amber500,
                                label = "Status",
                                value = if (modelInfo.isCompatible) "Ready" else "Insufficient memory"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (!modelInfo.isCompatible) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Amber500,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Your device may not have enough free RAM. Heuristic evaluation remains available.",
                                color = Amber500,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEnableDialog = false
                        isEnabling = true
                        enableSuccess = null
                        enableAttempted = false
                        enableProgress = 0f

                        scope.launch {
                            delay(500)
                            enableProgress = 0.4f
                            delay(500)
                            enableProgress = 0.8f
                            delay(400)

                            try {
                                onRequestEnable()
                                delay(300)
                                enableProgress = 1f
                                delay(200)
                                enableSuccess = true
                                onToggle(true)
                            } catch (_: Exception) {
                                enableSuccess = false
                                onToggle(false)
                            }

                            enableAttempted = true
                            delay(1000)
                            isEnabling = false
                            enableAttempted = false
                            enableSuccess = null
                        }
                    },
                    enabled = !isEnabling,
                    colors = ButtonDefaults.buttonColors(containerColor = Violet600),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (modelInfo.isCompatible) "Enable AI" else "Try Anyway",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEnableDialog = false }
                ) {
                    Text(
                        text = "Cancel",
                        color = Slate400
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp
        )
    }

    // ── Disable Confirmation Dialog ──
    if (showDisableDialog) {
        AlertDialog(
            onDismissRequest = { showDisableDialog = false },
            containerColor = Slate900,
            title = {
                Text(
                    text = "Disable Local AI?",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Turning off AI will unload the model from memory and switch to the lightweight heuristic engine.",
                        color = Slate300,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Heuristic evaluation is instant and always available. You can re-enable AI at any time.",
                        color = Slate400,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDisableDialog = false
                        onToggle(false)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Red500),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Disable AI",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDisableDialog = false }
                ) {
                    Text(
                        text = "Keep Enabled",
                        color = Slate400
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 8.dp
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            color = Slate400,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = Slate400,
                fontSize = 13.sp
            )
        }
        Text(
            text = value,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
