package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.FileItem
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.RoseDanger
import com.example.util.FileFormatters

@Composable
fun TrashScreen(
  trashItems: List<FileItem>,
  language: AppLanguage,
  onRestoreFile: (FileItem) -> Unit,
  onPermanentDelete: (FileItem) -> Unit,
  onEmptyTrash: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    // Top Bar with Empty Trash button
    if (trashItems.isNotEmpty()) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (language == AppLanguage.FA) "${trashItems.size} فایل در سطل زباله" else "${trashItems.size} items in Trash",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(
          onClick = onEmptyTrash,
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          modifier = Modifier.testTag("empty_trash_button")
        ) {
          Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(if (language == AppLanguage.FA) "خالی کردن" else "Empty Trash", style = MaterialTheme.typography.labelSmall)
        }
      }
    }

    if (trashItems.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 90.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Icon(
            Icons.Default.DeleteSweep,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
          )
          Text(
            text = if (language == AppLanguage.FA) "سطل زباله خالی است" else "Trash is empty",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = if (language == AppLanguage.FA) "فایل‌های حذف‌شده برای بازیابی به اینجا منتقل می‌شوند" else "Deleted files appear here before permanent deletion",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(trashItems, key = { it.path }) { item ->
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth().testTag("trash_item_${item.name}")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = item.name,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 1
                )
                Text(
                  text = FileFormatters.formatFileSize(item.sizeBytes),
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                // Restore button
                IconButton(
                  onClick = { onRestoreFile(item) },
                  modifier = Modifier.size(36.dp).testTag("restore_button_${item.name}")
                ) {
                  Icon(Icons.Default.Restore, contentDescription = "Restore", tint = CyanNeon)
                }

                // Delete permanently button
                IconButton(
                  onClick = { onPermanentDelete(item) },
                  modifier = Modifier.size(36.dp).testTag("delete_permanent_button_${item.name}")
                ) {
                  Icon(Icons.Default.DeleteForever, contentDescription = "Delete Forever", tint = RoseDanger)
                }
              }
            }
          }
        }
      }
    }
  }
}
