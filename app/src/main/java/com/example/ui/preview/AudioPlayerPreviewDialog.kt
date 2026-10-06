package com.example.ui.preview

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.VioletNeon
import com.example.util.FileFormatters
import java.io.File

@Composable
fun AudioPlayerPreviewDialog(
  file: File,
  fileName: String,
  sizeBytes: Long,
  language: AppLanguage,
  onDismiss: () -> Unit
) {
  var isPlaying by remember { mutableStateOf(true) }
  var progress by remember { mutableFloatStateOf(0.35f) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 10.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (language == AppLanguage.FA) "پخش‌کننده صوتی" else "Audio Player",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("audio_player_close")) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Audio Icon Disc with Glow
        Box(
          modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(CyanNeon, VioletNeon))),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            Icons.Default.MusicNote,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(44.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = fileName,
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1
        )
        Text(
          text = FileFormatters.formatFileSize(sizeBytes),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Animated Sound Wave bars
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(36.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          repeat(14) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "wave_$index")
            val heightScale by infiniteTransition.animateFloat(
              initialValue = 0.2f,
              targetValue = if (isPlaying) 1f else 0.2f,
              animationSpec = infiniteRepeatable(
                animation = tween(400 + (index * 70) % 500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
              ),
              label = "height_$index"
            )

            Box(
              modifier = Modifier
                .width(4.dp)
                .fillMaxHeight(fraction = heightScale)
                .clip(RoundedCornerShape(2.dp))
                .background(if (index % 2 == 0) CyanNeon else VioletNeon)
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Progress Bar
        Slider(
          value = progress,
          onValueChange = { progress = it },
          colors = SliderDefaults.colors(
            thumbColor = CyanNeon,
            activeTrackColor = CyanNeon
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("01:24", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text("03:45", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Play / Pause Button
        IconButton(
          onClick = { isPlaying = !isPlaying },
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(CyanNeon)
            .testTag("audio_player_toggle_play")
        ) {
          Icon(
            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = Color.Black,
            modifier = Modifier.size(30.dp)
          )
        }
      }
    }
  }
}
