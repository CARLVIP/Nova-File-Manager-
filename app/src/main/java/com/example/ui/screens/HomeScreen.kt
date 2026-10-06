package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.FileItemListCard
import com.example.ui.components.StorageDiskCard
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.VioletNeon
import com.example.util.FileFormatters

@Composable
fun HomeScreen(
  storageStats: StorageStats?,
  recentFiles: List<FileItem>,
  language: AppLanguage,
  onCategoryClick: (FileCategory) -> Unit,
  onCleanStorageClick: () -> Unit,
  onOpenFile: (FileItem) -> Unit,
  onToggleStar: (FileItem) -> Unit,
  onFileAction: (item: FileItem, action: String) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp)
  ) {
    // 1. Storage Visualizer Card
    item {
      StorageDiskCard(
        stats = storageStats,
        language = language,
        onCleanClick = onCleanStorageClick
      )
    }

    // 2. Categories Title
    item {
      Text(
        text = if (language == AppLanguage.FA) "دسته‌بندی فایل‌ها" else "Categories",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    // 3. Category Grid Cards
    item {
      val categories = listOf(
        CategoryItemData(FileCategory.IMAGES, if (language == AppLanguage.FA) "تصاویر" else "Images", Icons.Default.Image, CyanNeon),
        CategoryItemData(FileCategory.VIDEOS, if (language == AppLanguage.FA) "ویدیوها" else "Videos", Icons.Default.Movie, Color(0xFFEC4899)),
        CategoryItemData(FileCategory.AUDIO, if (language == AppLanguage.FA) "موسیقی" else "Audio", Icons.Default.MusicNote, VioletNeon),
        CategoryItemData(FileCategory.DOCUMENTS, if (language == AppLanguage.FA) "اسناد" else "Documents", Icons.Default.Description, Color(0xFFF59E0B)),
        CategoryItemData(FileCategory.ARCHIVES, if (language == AppLanguage.FA) "فشرده" else "Archives", Icons.Default.Archive, Color(0xFF10B981)),
        CategoryItemData(FileCategory.CODE, if (language == AppLanguage.FA) "اسکریپت و کد" else "Code", Icons.Default.Code, Color(0xFF60A5FA))
      )

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for (i in categories.indices step 2) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            val cat1 = categories[i]
            val stat1 = storageStats?.categoryStats?.find { it.category == cat1.category }
            CategoryCard(
              category = cat1,
              count = stat1?.count ?: 0,
              sizeBytes = stat1?.bytes ?: 0L,
              language = language,
              onClick = { onCategoryClick(cat1.category) },
              modifier = Modifier.weight(1f)
            )

            if (i + 1 < categories.size) {
              val cat2 = categories[i + 1]
              val stat2 = storageStats?.categoryStats?.find { it.category == cat2.category }
              CategoryCard(
                category = cat2,
                count = stat2?.count ?: 0,
                sizeBytes = stat2?.bytes ?: 0L,
                language = language,
                onClick = { onCategoryClick(cat2.category) },
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }
    }

    // 4. Recent Files Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (language == AppLanguage.FA) "فایل‌های اخیر" else "Recent Files",
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${recentFiles.size} ${if (language == AppLanguage.FA) "مورد" else "items"}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    if (recentFiles.isEmpty()) {
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        ) {
          Box(
            modifier = Modifier.padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (language == AppLanguage.FA) "هنوز فایلی ایجاد نشده است" else "No recent files yet",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    } else {
      items(recentFiles, key = { it.path }) { item ->
        FileItemListCard(
          item = item,
          isSelected = false,
          isSelectionMode = false,
          language = language,
          onClick = { onOpenFile(item) },
          onLongClick = { onOpenFile(item) },
          onToggleStar = { onToggleStar(item) },
          onOpen = { onOpenFile(item) },
          onCopy = { onFileAction(item, "copy") },
          onCut = { onFileAction(item, "cut") },
          onRename = { onFileAction(item, "rename") },
          onDelete = { onFileAction(item, "delete") },
          onDetails = { onFileAction(item, "details") },
          onShare = { onFileAction(item, "share") }
        )
      }
    }
  }
}

private data class CategoryItemData(
  val category: FileCategory,
  val label: String,
  val icon: ImageVector,
  val color: Color
)

@Composable
private fun CategoryCard(
  category: CategoryItemData,
  count: Int,
  sizeBytes: Long,
  language: AppLanguage,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(18.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    tonalElevation = 2.dp,
    modifier = modifier.testTag("category_card_${category.category.name.lowercase()}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(category.color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          category.icon,
          contentDescription = category.label,
          tint = category.color,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Column {
        Text(
          text = category.label,
          style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = if (count > 0) "$count ${if (language == AppLanguage.FA) "فایل" else "files"}"
          else if (language == AppLanguage.FA) "خالی" else "Empty",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
