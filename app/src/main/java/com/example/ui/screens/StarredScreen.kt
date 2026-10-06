package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.model.FileItem
import com.example.data.model.ViewMode
import com.example.ui.components.FileItemGridCard
import com.example.ui.components.FileItemListCard
import com.example.ui.theme.AmberNeon

@Composable
fun StarredScreen(
  items: List<FileItem>,
  viewMode: ViewMode,
  language: AppLanguage,
  onOpenFile: (FileItem) -> Unit,
  onToggleStar: (FileItem) -> Unit,
  onFileAction: (item: FileItem, action: String) -> Unit,
  modifier: Modifier = Modifier
) {
  if (items.isEmpty()) {
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
          Icons.Default.Star,
          contentDescription = null,
          tint = AmberNeon.copy(alpha = 0.5f),
          modifier = Modifier.size(64.dp)
        )
        Text(
          text = if (language == AppLanguage.FA) "هیچ فایل نشان‌شده‌ای وجود ندارد" else "No starred items yet",
          style = MaterialTheme.typography.titleMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = if (language == AppLanguage.FA) "روی آیکون ستاره در فایل‌ها ضربه بزنید تا به اینجا اضافه شوند" else "Tap the star icon on any file to add it here",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  } else {
    if (viewMode == ViewMode.GRID) {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(items, key = { it.path }) { item ->
          FileItemGridCard(
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
    } else {
      LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(items, key = { it.path }) { item ->
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
}
