package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.FileCategory
import com.example.data.model.FileItem
import com.example.ui.theme.*
import com.example.util.FileFormatters

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileItemListCard(
  item: FileItem,
  isSelected: Boolean,
  isSelectionMode: Boolean,
  language: AppLanguage,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  onToggleStar: () -> Unit,
  onOpen: () -> Unit,
  onCopy: () -> Unit,
  onCut: () -> Unit,
  onRename: () -> Unit,
  onDelete: () -> Unit,
  onDetails: () -> Unit,
  onShare: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showMenu by remember { mutableStateOf(false) }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .combinedClickable(
        onClick = onClick,
        onLongClick = onLongClick
      )
      .testTag("file_item_${item.name}"),
    shape = RoundedCornerShape(16.dp),
    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    tonalElevation = if (isSelected) 4.dp else 1.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (isSelectionMode) {
        Checkbox(
          checked = isSelected,
          onCheckedChange = { onClick() },
          colors = CheckboxDefaults.colors(
            checkedColor = CyanNeon,
            checkmarkColor = Color.Black
          ),
          modifier = Modifier.padding(end = 8.dp)
        )
      }

      // Icon Box
      CategoryIconBox(item = item, size = 44.dp)

      Spacer(modifier = Modifier.width(12.dp))

      // File Details
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = item.name,
          style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = if (item.isDirectory) "${item.childCount} ${if (language == AppLanguage.FA) "آیتم" else "items"}"
            else FileFormatters.formatFileSize(item.sizeBytes),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = " • ",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = FileFormatters.formatDate(item.lastModified),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Star Toggle Button
      IconButton(
        onClick = onToggleStar,
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          if (item.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
          contentDescription = "Star",
          tint = if (item.isStarred) AmberNeon else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.size(18.dp)
        )
      }

      // Overflow Menu
      Box {
        IconButton(
          onClick = { showMenu = true },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            Icons.Default.MoreVert,
            contentDescription = "More",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }

        FileActionDropdownMenu(
          expanded = showMenu,
          onDismiss = { showMenu = false },
          item = item,
          language = language,
          onOpen = onOpen,
          onCopy = onCopy,
          onCut = onCut,
          onRename = onRename,
          onDelete = onDelete,
          onDetails = onDetails,
          onShare = onShare
        )
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileItemGridCard(
  item: FileItem,
  isSelected: Boolean,
  isSelectionMode: Boolean,
  language: AppLanguage,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  onToggleStar: () -> Unit,
  onOpen: () -> Unit,
  onCopy: () -> Unit,
  onCut: () -> Unit,
  onRename: () -> Unit,
  onDelete: () -> Unit,
  onDetails: () -> Unit,
  onShare: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showMenu by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .combinedClickable(
        onClick = onClick,
        onLongClick = onLongClick
      )
      .testTag("file_item_grid_${item.name}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
      else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ),
    border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyanNeon))
    else null
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      // Top row in Grid: Selection checkbox or Star + Menu
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (isSelectionMode) {
          Checkbox(
            checked = isSelected,
            onCheckedChange = { onClick() },
            colors = CheckboxDefaults.colors(checkedColor = CyanNeon, checkmarkColor = Color.Black),
            modifier = Modifier.size(24.dp)
          )
        } else {
          IconButton(
            onClick = onToggleStar,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              if (item.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
              contentDescription = "Star",
              tint = if (item.isStarred) AmberNeon else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Box {
          IconButton(
            onClick = { showMenu = true },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              Icons.Default.MoreVert,
              contentDescription = "More",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
          }

          FileActionDropdownMenu(
            expanded = showMenu,
            onDismiss = { showMenu = false },
            item = item,
            language = language,
            onOpen = onOpen,
            onCopy = onCopy,
            onCut = onCut,
            onRename = onRename,
            onDelete = onDelete,
            onDetails = onDetails,
            onShare = onShare
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Centered Icon Box
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        CategoryIconBox(item = item, size = 52.dp)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // File Name
      Text(
        text = item.name,
        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(2.dp))

      // Size / Children
      Text(
        text = if (item.isDirectory) "${item.childCount} ${if (language == AppLanguage.FA) "مورد" else "items"}"
        else FileFormatters.formatFileSize(item.sizeBytes),
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
fun CategoryIconBox(item: FileItem, size: androidx.compose.ui.unit.Dp) {
  val icon = getFileIcon(item)
  val categoryColor = if (item.isDirectory) CyanNeon else FileFormatters.getCategoryColor(item.category)

  Box(
    modifier = Modifier
      .size(size)
      .clip(RoundedCornerShape(12.dp))
      .background(categoryColor.copy(alpha = 0.15f)),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      icon,
      contentDescription = null,
      tint = categoryColor,
      modifier = Modifier.size(size * 0.55f)
    )
  }
}

fun getFileIcon(item: FileItem): ImageVector {
  if (item.isDirectory) return Icons.Default.Folder
  return when (item.category) {
    FileCategory.IMAGES -> Icons.Default.Image
    FileCategory.VIDEOS -> Icons.Default.Movie
    FileCategory.AUDIO -> Icons.Default.MusicNote
    FileCategory.DOCUMENTS -> Icons.Default.Description
    FileCategory.ARCHIVES -> Icons.Default.Archive
    FileCategory.CODE -> Icons.Default.Code
    FileCategory.OTHER, FileCategory.ALL -> Icons.Default.InsertDriveFile
  }
}

@Composable
fun FileActionDropdownMenu(
  expanded: Boolean,
  onDismiss: () -> Unit,
  item: FileItem,
  language: AppLanguage,
  onOpen: () -> Unit,
  onCopy: () -> Unit,
  onCut: () -> Unit,
  onRename: () -> Unit,
  onDelete: () -> Unit,
  onDetails: () -> Unit,
  onShare: () -> Unit
) {
  DropdownMenu(
    expanded = expanded,
    onDismissRequest = onDismiss,
    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
  ) {
    DropdownMenuItem(
      text = { Text(if (language == AppLanguage.FA) "باز کردن" else "Open") },
      leadingIcon = { Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp)) },
      onClick = { onDismiss(); onOpen() }
    )
    DropdownMenuItem(
      text = { Text(if (language == AppLanguage.FA) "کپی" else "Copy") },
      leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp)) },
      onClick = { onDismiss(); onCopy() }
    )
    DropdownMenuItem(
      text = { Text(if (language == AppLanguage.FA) "انتقال (برش)" else "Cut / Move") },
      leadingIcon = { Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(18.dp)) },
      onClick = { onDismiss(); onCut() }
    )
    DropdownMenuItem(
      text = { Text(if (language == AppLanguage.FA) "تغییر نام" else "Rename") },
      leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp)) },
      onClick = { onDismiss(); onRename() }
    )
    if (!item.isDirectory) {
      DropdownMenuItem(
        text = { Text(if (language == AppLanguage.FA) "اشتراک‌گذاری" else "Share") },
        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp)) },
        onClick = { onDismiss(); onShare() }
      )
    }
    DropdownMenuItem(
      text = { Text(if (language == AppLanguage.FA) "مشخصات" else "Details") },
      leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(18.dp)) },
      onClick = { onDismiss(); onDetails() }
    )
    Divider(modifier = Modifier.padding(vertical = 4.dp))
    DropdownMenuItem(
      text = {
        Text(
          if (language == AppLanguage.FA) "حذف (سطل زباله)" else "Move to Trash",
          color = MaterialTheme.colorScheme.error
        )
      },
      leadingIcon = {
        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
      },
      onClick = { onDismiss(); onDelete() }
    )
  }
}
