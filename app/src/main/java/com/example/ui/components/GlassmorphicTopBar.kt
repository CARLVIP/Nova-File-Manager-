package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.model.AppLanguage
import com.example.data.model.NavigationTab
import com.example.data.model.SortOption
import com.example.data.model.ViewMode
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.VioletNeon

@Composable
fun GlassmorphicTopBar(
  currentTab: NavigationTab,
  language: AppLanguage,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  viewMode: ViewMode,
  onToggleViewMode: () -> Unit,
  sortOption: SortOption,
  sortAscending: Boolean,
  onSortChange: (SortOption) -> Unit,
  onToggleLanguage: () -> Unit,
  onRefresh: () -> Unit,
  isMultiSelect: Boolean,
  selectedCount: Int,
  onClearSelection: () -> Unit,
  onSelectAll: () -> Unit,
  onCopySelected: () -> Unit,
  onCutSelected: () -> Unit,
  onDeleteSelected: () -> Unit
) {
  var isSearchExpanded by remember { mutableStateOf(false) }
  var showSortMenu by remember { mutableStateOf(false) }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .statusBarsPadding(),
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
    tonalElevation = 6.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      if (isMultiSelect) {
        // Multi-Select Action Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onClearSelection,
              modifier = Modifier.testTag("clear_selection_button")
            ) {
              Icon(Icons.Default.Close, contentDescription = "Clear Selection", tint = CyanNeon)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (language == AppLanguage.FA) "$selectedCount مورد انتخاب شد" else "$selectedCount selected",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onSelectAll, modifier = Modifier.testTag("select_all_button")) {
              Icon(Icons.Default.SelectAll, contentDescription = "Select All", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onCopySelected, modifier = Modifier.testTag("copy_selected_button")) {
              Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onCutSelected, modifier = Modifier.testTag("cut_selected_button")) {
              Icon(Icons.Default.ContentCut, contentDescription = "Cut", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDeleteSelected, modifier = Modifier.testTag("delete_selected_button")) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
          }
        }
      } else {
        // Normal Top Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Brand Title with subtle glowing indicator
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  Brush.linearGradient(
                    colors = listOf(CyanNeon, VioletNeon)
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.FolderSpecial,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = if (language == AppLanguage.FA) "نووا فایلز" else "Nova Files",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = if (language == AppLanguage.FA) currentTab.titleFa else currentTab.titleEn,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = CyanNeon
              )
            }
          }

          // Action Icons
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Search toggle
            IconButton(
              onClick = { isSearchExpanded = !isSearchExpanded },
              modifier = Modifier.testTag("search_toggle_button")
            ) {
              Icon(
                if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurface
              )
            }

            // View Mode Toggle (Grid/List)
            if (currentTab == NavigationTab.FILES || currentTab == NavigationTab.STARRED) {
              IconButton(
                onClick = onToggleViewMode,
                modifier = Modifier.testTag("view_mode_toggle_button")
              ) {
                Icon(
                  if (viewMode == ViewMode.GRID) Icons.Default.ViewList else Icons.Default.GridView,
                  contentDescription = "Toggle View",
                  tint = MaterialTheme.colorScheme.onSurface
                )
              }
            }

            // Sort Menu Button
            if (currentTab == NavigationTab.FILES) {
              Box {
                IconButton(
                  onClick = { showSortMenu = true },
                  modifier = Modifier.testTag("sort_button")
                ) {
                  Icon(
                    Icons.Default.Sort,
                    contentDescription = "Sort",
                    tint = MaterialTheme.colorScheme.onSurface
                  )
                }

                DropdownMenu(
                  expanded = showSortMenu,
                  onDismissRequest = { showSortMenu = false },
                  modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                  SortOption.values().forEach { option ->
                    val isSelected = sortOption == option
                    DropdownMenuItem(
                      text = {
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Text(
                            text = if (language == AppLanguage.FA) option.titleFa else option.titleEn,
                            color = if (isSelected) CyanNeon else MaterialTheme.colorScheme.onSurface
                          )
                          if (isSelected) {
                            Icon(
                              if (sortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                              contentDescription = null,
                              tint = CyanNeon,
                              modifier = Modifier.size(16.dp)
                            )
                          }
                        }
                      },
                      onClick = {
                        onSortChange(option)
                        showSortMenu = false
                      }
                    )
                  }
                }
              }
            }

            // Language Switcher Badge
            Surface(
              onClick = onToggleLanguage,
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .height(32.dp)
                .padding(start = 4.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .testTag("language_toggle_button")
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
              ) {
                Text(
                  text = if (language == AppLanguage.FA) "فا | EN" else "EN | فا",
                  style = MaterialTheme.typography.labelSmall,
                  color = CyanNeon
                )
              }
            }
          }
        }

        // Expandable Search Bar
        AnimatedVisibility(
          visible = isSearchExpanded,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
              .testTag("search_text_input"),
            placeholder = {
              Text(
                if (language == AppLanguage.FA) "جستجو در بین فایل‌ها و پوشه‌ها..." else "Search files and folders...",
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            },
            singleLine = true,
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = null, tint = CyanNeon)
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurface)
                }
              }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = CyanNeon,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline,
              focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
              unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            )
          )
        }
      }
    }
  }
}
