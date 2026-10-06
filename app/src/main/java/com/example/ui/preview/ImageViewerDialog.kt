package com.example.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.AppLanguage
import com.example.util.FileFormatters
import java.io.File

@Composable
fun ImageViewerDialog(
  file: File,
  language: AppLanguage,
  onShare: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.92f))
        .padding(16.dp)
    ) {
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.TopCenter)
          .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("image_viewer_close_button")) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = file.name,
              style = MaterialTheme.typography.titleMedium,
              color = Color.White
            )
            Text(
              text = FileFormatters.formatFileSize(file.length()),
              style = MaterialTheme.typography.bodySmall,
              color = Color.LightGray
            )
          }
        }

        IconButton(onClick = onShare, modifier = Modifier.testTag("image_viewer_share_button")) {
          Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
        }
      }

      // Image Display
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(vertical = 70.dp),
        contentAlignment = Alignment.Center
      ) {
        AsyncImage(
          model = file,
          contentDescription = file.name,
          modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp)),
          contentScale = ContentScale.Fit
        )
      }
    }
  }
}
