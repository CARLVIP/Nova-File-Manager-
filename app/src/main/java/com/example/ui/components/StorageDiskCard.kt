package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.StorageStats
import com.example.ui.theme.*
import com.example.util.FileFormatters

@Composable
fun StorageDiskCard(
  stats: StorageStats?,
  language: AppLanguage,
  onCleanClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (stats == null) return

  val usedPercentage = (stats.usedBytes.toFloat() / stats.totalBytes.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)
  val animatedPercentage by animateFloatAsState(targetValue = usedPercentage, label = "storage_bar")

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("storage_disk_card"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    ),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = Brush.horizontalGradient(listOf(CyanNeon.copy(alpha = 0.3f), VioletNeon.copy(alpha = 0.3f)))
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Header: Storage title & Quick Clean action
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Storage,
              contentDescription = null,
              tint = CyanNeon,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = if (language == AppLanguage.FA) "حافظه دستگاه" else "Device Storage",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = if (language == AppLanguage.FA)
                "${FileFormatters.formatFileSize(stats.usedBytes)} از ${FileFormatters.formatFileSize(stats.totalBytes)} مصرف شده"
              else
                "${FileFormatters.formatFileSize(stats.usedBytes)} used of ${FileFormatters.formatFileSize(stats.totalBytes)}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Percentage Badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
          border = null
        ) {
          Text(
            text = "${(usedPercentage * 100).toInt()}%",
            style = MaterialTheme.typography.labelLarge,
            color = CyanNeon,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Multi-segment storage bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(12.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(MaterialTheme.colorScheme.surfaceContainer)
      ) {
        Row(modifier = Modifier.fillMaxSize()) {
          stats.categoryStats.forEach { stat ->
            if (stat.percentage > 0f) {
              Box(
                modifier = Modifier
                  .weight(stat.percentage.coerceAtLeast(0.01f))
                  .fillMaxHeight()
                  .background(FileFormatters.getCategoryColor(stat.category))
              )
            }
          }
          val freeWeight = (1f - usedPercentage).coerceAtLeast(0.01f)
          Box(
            modifier = Modifier
              .weight(freeWeight)
              .fillMaxHeight()
              .background(Color.Transparent)
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Category breakdown labels (horizontal wrap)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        val topStats = stats.categoryStats.filter { it.bytes > 0 }.take(4)
        if (topStats.isNotEmpty()) {
          topStats.forEach { item ->
            Column(horizontalAlignment = Alignment.Start) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(FileFormatters.getCategoryColor(item.category))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (language == AppLanguage.FA) item.category.titleFa else item.category.titleEn,
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Text(
                text = FileFormatters.formatFileSize(item.bytes),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 14.dp, top = 2.dp)
              )
            }
          }
        } else {
          Text(
            text = if (language == AppLanguage.FA) "آمار فایل‌ها به‌روز است" else "Files up to date",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Quick clean button
      OutlinedButton(
        onClick = onCleanClick,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("storage_card_clean_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = CyanNeon
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
          brush = Brush.horizontalGradient(listOf(CyanNeon, VioletNeon))
        )
      ) {
        Icon(
          Icons.Default.CleaningServices,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (language == AppLanguage.FA) "آنالیز و پاک‌سازی هوشمند حافظه" else "Smart Storage Cleaner"
        )
      }
    }
  }
}
