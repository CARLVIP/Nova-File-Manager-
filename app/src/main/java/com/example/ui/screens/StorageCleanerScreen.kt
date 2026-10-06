package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.FileItem
import com.example.data.model.StorageStats
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.VioletNeon
import com.example.util.FileFormatters

@Composable
fun StorageCleanerScreen(
  storageStats: StorageStats?,
  largeFiles: List<FileItem>,
  emptyFolders: List<FileItem>,
  language: AppLanguage,
  onCleanCache: () -> Unit,
  onDeleteFile: (FileItem) -> Unit,
  onDeleteFolder: (FileItem) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // 1. Cleaner Header Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("cleaner_summary_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(Brush.linearGradient(listOf(CyanNeon, VioletNeon))),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.CleaningServices,
              contentDescription = null,
              tint = Color.Black,
              modifier = Modifier.size(32.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = if (language == AppLanguage.FA) "بهینه‌ساز و پاک‌کننده هوشمند" else "Smart Storage Cleaner",
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = if (language == AppLanguage.FA)
              "حذف حافظه موقت (کش)، فایل‌های حجیم و پوشه‌های خالی"
            else
              "Clean application cache, large media, and empty directories",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
          )

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = onCleanCache,
            modifier = Modifier.fillMaxWidth().testTag("clean_cache_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black)
          ) {
            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (language == AppLanguage.FA) "پاک‌سازی حافظه موقت (Cache)" else "Clean Temporary Cache")
          }
        }
      }
    }

    // 2. Large Files Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (language == AppLanguage.FA) "فایل‌های حجیم (> 100 KB)" else "Large Files (> 100 KB)",
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${largeFiles.size} ${if (language == AppLanguage.FA) "مورد" else "items"}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    if (largeFiles.isEmpty()) {
      item {
        CleanEmptyCard(
          text = if (language == AppLanguage.FA) "هیچ فایل حجیمی یافت نشد" else "No large files detected"
        )
      }
    } else {
      items(largeFiles, key = { it.path }) { item ->
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth().testTag("large_file_item_${item.name}")
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
                style = MaterialTheme.typography.bodySmall,
                color = CyanNeon
              )
            }

            IconButton(
              onClick = { onDeleteFile(item) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RoseDanger)
            }
          }
        }
      }
    }

    // 3. Empty Folders Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (language == AppLanguage.FA) "پوشه‌های خالی" else "Empty Folders",
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${emptyFolders.size} ${if (language == AppLanguage.FA) "مورد" else "items"}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    if (emptyFolders.isEmpty()) {
      item {
        CleanEmptyCard(
          text = if (language == AppLanguage.FA) "هیچ پوشه خالی وجود ندارد" else "No empty folders detected"
        )
      }
    } else {
      items(emptyFolders, key = { it.path }) { folder ->
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth().testTag("empty_folder_item_${folder.name}")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(Icons.Default.Folder, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = folder.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
              )
            }

            IconButton(
              onClick = { onDeleteFolder(folder) },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RoseDanger)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CleanEmptyCard(text: String) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    modifier = Modifier.fillMaxWidth()
  ) {
    Box(
      modifier = Modifier.padding(18.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
