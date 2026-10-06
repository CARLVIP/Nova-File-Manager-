package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.example.ui.components.BreadcrumbBar
import com.example.ui.components.FileItemGridCard
import com.example.ui.components.FileItemListCard
import com.example.ui.theme.CyanNeon
import java.io.File

@Composable
fun FilesExplorerScreen(
  currentDirectory: File,
  breadcrumbs: List<File>,
  items: List<FileItem>,
  selectedItems: Set<File>,
  isSelectionMode: Boolean,
  categoryFilter: FileCategory?,
  viewMode: ViewMode,
  language: AppLanguage,
  onNavigateTo: (File) -> Unit,
  onCategoryFilterChange: (FileCategory?) -> Unit,
  onFileClick: (FileItem) -> Unit,
  onFileLongClick: (FileItem) -> Unit,
  onToggleStar: (FileItem) -> Unit,
  onFileAction: (item: FileItem, action: String) -> Unit,
  onCreateFolderClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier.fillMaxSize()
  ) {
    // 1. Breadcrumbs Bar
    BreadcrumbBar(
      breadcrumbs = breadcrumbs,
      onNavigateTo = onNavigateTo
    )

    // 2. Category Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      FilterChip(
        selected = categoryFilter == null,
        onClick = { onCategoryFilterChange(null) },
        label = { Text(if (language == AppLanguage.FA) "همه" else "All") },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = CyanNeon.copy(alpha = 0.2f),
          selectedLabelColor = CyanNeon
        ),
        shape = RoundedCornerShape(12.dp)
      )

      FileCategory.values().filter { it != FileCategory.ALL && it != FileCategory.OTHER }.forEach { cat ->
        val isSelected = categoryFilter == cat
        FilterChip(
          selected = isSelected,
          onClick = { onCategoryFilterChange(if (isSelected) null else cat) },
          label = { Text(if (language == AppLanguage.FA) cat.titleFa else cat.titleEn) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = CyanNeon.copy(alpha = 0.2f),
            selectedLabelColor = CyanNeon
          ),
          shape = RoundedCornerShape(12.dp)
        )
      }
    }

    // 3. File Listing or Empty State
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
            Icons.Default.FolderOpen,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
          )
          Text(
            text = if (language == AppLanguage.FA) "این پوشه خالی است" else "This folder is empty",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Button(
            onClick = onCreateFolderClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("empty_state_new_folder_button")
          ) {
            Icon(Icons.Default.CreateNewFolder, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (language == AppLanguage.FA) "ایجاد پوشه جدید" else "Create Folder")
          }
        }
      }
    } else {
      if (viewMode == ViewMode.GRID) {
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
          contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(items, key = { it.path }) { item ->
            FileItemGridCard(
              item = item,
              isSelected = selectedItems.contains(item.file),
              isSelectionMode = isSelectionMode,
              language = language,
              onClick = { onFileClick(item) },
              onLongClick = { onFileLongClick(item) },
              onToggleStar = { onToggleStar(item) },
              onOpen = { onFileClick(item) },
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
          modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
          contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(items, key = { it.path }) { item ->
            FileItemListCard(
              item = item,
              isSelected = selectedItems.contains(item.file),
              isSelectionMode = isSelectionMode,
              language = language,
              onClick = { onFileClick(item) },
              onLongClick = { onFileLongClick(item) },
              onToggleStar = { onToggleStar(item) },
              onOpen = { onFileClick(item) },
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
}
