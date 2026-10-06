package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.data.model.*
import com.example.ui.FileManagerViewModel
import com.example.ui.components.GlassmorphicTopBar
import com.example.ui.components.SpeedDialFab
import com.example.ui.components.StylishBottomBar
import com.example.ui.dialogs.FileOperationDialogs
import com.example.ui.preview.AudioPlayerPreviewDialog
import com.example.ui.preview.ImageViewerDialog
import com.example.ui.preview.TextEditorPreviewDialog
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import java.io.File

class MainActivity : ComponentActivity() {

  private val viewModel: FileManagerViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme(darkTheme = true) {
        val context = LocalContext.current

        // State collection
        val currentTab by viewModel.currentTab.collectAsState()
        val language by viewModel.appLanguage.collectAsState()
        val currentDir by viewModel.currentDirectory.collectAsState()
        val breadcrumbs by viewModel.breadcrumbs.collectAsState()
        val items by viewModel.items.collectAsState()
        val selectedItems by viewModel.selectedItems.collectAsState()
        val isMultiSelect by viewModel.isMultiSelectMode.collectAsState()
        val searchQuery by viewModel.searchQuery.collectAsState()
        val categoryFilter by viewModel.categoryFilter.collectAsState()
        val viewMode by viewModel.viewMode.collectAsState()
        val sortOption by viewModel.sortOption.collectAsState()
        val sortAscending by viewModel.sortAscending.collectAsState()
        val clipboard by viewModel.clipboard.collectAsState()
        val storageStats by viewModel.storageStats.collectAsState()
        val recentItems by viewModel.recentItems.collectAsState()
        val starredItems by viewModel.starredItems.collectAsState()
        val trashItems by viewModel.trashItems.collectAsState()
        val largeFiles by viewModel.largeFiles.collectAsState()
        val emptyFolders by viewModel.emptyFolders.collectAsState()
        val activePreview by viewModel.activePreview.collectAsState()
        val activeDialog by viewModel.activeDialog.collectAsState()
        val snackbarMessage by viewModel.snackbarMessage.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        // SAF File Importer
        val filePickerLauncher = rememberLauncherForActivityResult(
          contract = ActivityResultContracts.OpenMultipleDocuments()
        ) { uris: List<Uri> ->
          uris.forEach { uri ->
            try {
              val contentResolver = context.contentResolver
              var fileName = "imported_${System.currentTimeMillis()}"
              val cursor = contentResolver.query(uri, null, null, null, null)
              cursor?.use {
                if (it.moveToFirst()) {
                  val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                  if (nameIndex >= 0) {
                    fileName = it.getString(nameIndex)
                  }
                }
              }
              contentResolver.openInputStream(uri)?.use { stream ->
                viewModel.importExternalFile(fileName, stream)
              }
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }
        }

        // Share File Helper
        fun shareFile(file: File) {
          try {
            val uri = FileProvider.getUriForFile(
              context,
              "${context.packageName}.fileprovider",
              file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
              type = "*/*"
              putExtra(Intent.EXTRA_STREAM, uri)
              addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share ${file.name}"))
          } catch (e: Exception) {
            viewModel.showSnackbar("Cannot share file: ${e.message}")
          }
        }

        // BackHandler: Handle folder hierarchy and tabs
        BackHandler(enabled = true) {
          if (isMultiSelect) {
            viewModel.clearSelection()
          } else if (currentTab == NavigationTab.FILES && viewModel.navigateUp()) {
            // Handled folder up
          } else if (currentTab != NavigationTab.HOME) {
            viewModel.selectTab(NavigationTab.HOME)
          } else {
            finish()
          }
        }

        // Snackbar trigger
        LaunchedEffect(snackbarMessage) {
          snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
          }
        }

        // Persian RTL / English LTR layout direction
        val layoutDirection = if (language == AppLanguage.FA) LayoutDirection.Rtl else LayoutDirection.Ltr

        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
          Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
              GlassmorphicTopBar(
                currentTab = currentTab,
                language = language,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                viewMode = viewMode,
                onToggleViewMode = { viewModel.toggleViewMode() },
                sortOption = sortOption,
                sortAscending = sortAscending,
                onSortChange = { viewModel.setSort(it) },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onRefresh = { viewModel.refreshAll() },
                isMultiSelect = isMultiSelect,
                selectedCount = selectedItems.size,
                onClearSelection = { viewModel.clearSelection() },
                onSelectAll = { viewModel.selectAll() },
                onCopySelected = { viewModel.copySelected() },
                onCutSelected = { viewModel.cutSelected() },
                onDeleteSelected = {
                  val itemsToDelete = items.filter { selectedItems.contains(it.file) }
                  viewModel.showDialog(ActiveDialog.DeleteConfirmation(itemsToDelete, permanent = false))
                }
              )
            },
            bottomBar = {
              StylishBottomBar(
                currentTab = currentTab,
                onSelectTab = { viewModel.selectTab(it) },
                language = language,
                clipboard = clipboard,
                onPasteClipboard = { viewModel.pasteClipboard() },
                onClearClipboard = { viewModel.clearClipboard() }
              )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
            ) {
              // Main Tab Content
              when (currentTab) {
                NavigationTab.HOME -> {
                  HomeScreen(
                    storageStats = storageStats,
                    recentFiles = recentItems,
                    language = language,
                    onCategoryClick = { cat ->
                      viewModel.setCategoryFilter(cat)
                      viewModel.selectTab(NavigationTab.FILES)
                    },
                    onCleanStorageClick = { viewModel.selectTab(NavigationTab.CLEANER) },
                    onOpenFile = { viewModel.openFile(it) },
                    onToggleStar = { viewModel.toggleStar(it.file) },
                    onFileAction = { item, action ->
                      when (action) {
                        "copy" -> {
                          viewModel.toggleSelection(item.file)
                          viewModel.copySelected()
                        }
                        "cut" -> {
                          viewModel.toggleSelection(item.file)
                          viewModel.cutSelected()
                        }
                        "rename" -> viewModel.showDialog(ActiveDialog.Rename(item.file))
                        "delete" -> viewModel.showDialog(ActiveDialog.DeleteConfirmation(listOf(item), permanent = false))
                        "details" -> viewModel.showDialog(ActiveDialog.Details(item))
                        "share" -> shareFile(item.file)
                      }
                    }
                  )
                }

                NavigationTab.FILES -> {
                  FilesExplorerScreen(
                    currentDirectory = currentDir,
                    breadcrumbs = breadcrumbs,
                    items = items,
                    selectedItems = selectedItems,
                    isSelectionMode = isMultiSelect,
                    categoryFilter = categoryFilter,
                    viewMode = viewMode,
                    language = language,
                    onNavigateTo = { viewModel.navigateTo(it) },
                    onCategoryFilterChange = { viewModel.setCategoryFilter(it) },
                    onFileClick = { item ->
                      if (isMultiSelect) {
                        viewModel.toggleSelection(item.file)
                      } else {
                        viewModel.openFile(item)
                      }
                    },
                    onFileLongClick = { item ->
                      viewModel.toggleSelection(item.file)
                    },
                    onToggleStar = { viewModel.toggleStar(it.file) },
                    onFileAction = { item, action ->
                      when (action) {
                        "copy" -> {
                          viewModel.toggleSelection(item.file)
                          viewModel.copySelected()
                        }
                        "cut" -> {
                          viewModel.toggleSelection(item.file)
                          viewModel.cutSelected()
                        }
                        "rename" -> viewModel.showDialog(ActiveDialog.Rename(item.file))
                        "delete" -> viewModel.showDialog(ActiveDialog.DeleteConfirmation(listOf(item), permanent = false))
                        "details" -> viewModel.showDialog(ActiveDialog.Details(item))
                        "share" -> shareFile(item.file)
                      }
                    },
                    onCreateFolderClick = { viewModel.showDialog(ActiveDialog.NewFolder(currentDir)) }
                  )
                }

                NavigationTab.CLEANER -> {
                  StorageCleanerScreen(
                    storageStats = storageStats,
                    largeFiles = largeFiles,
                    emptyFolders = emptyFolders,
                    language = language,
                    onCleanCache = { viewModel.cleanStorageCache() },
                    onDeleteFile = { viewModel.showDialog(ActiveDialog.DeleteConfirmation(listOf(it), permanent = false)) },
                    onDeleteFolder = { viewModel.showDialog(ActiveDialog.DeleteConfirmation(listOf(it), permanent = false)) }
                  )
                }

                NavigationTab.STARRED -> {
                  StarredScreen(
                    items = starredItems,
                    viewMode = viewMode,
                    language = language,
                    onOpenFile = { viewModel.openFile(it) },
                    onToggleStar = { viewModel.toggleStar(it.file) },
                    onFileAction = { item, action ->
                      when (action) {
                        "copy" -> {
                          viewModel.toggleSelection(item.file)
                          viewModel.copySelected()
                        }
                        "cut" -> {
                          viewModel.toggleSelection(item.file)
                          viewModel.cutSelected()
                        }
                        "rename" -> viewModel.showDialog(ActiveDialog.Rename(item.file))
                        "delete" -> viewModel.showDialog(ActiveDialog.DeleteConfirmation(listOf(item), permanent = false))
                        "details" -> viewModel.showDialog(ActiveDialog.Details(item))
                        "share" -> shareFile(item.file)
                      }
                    }
                  )
                }

                NavigationTab.TRASH -> {
                  TrashScreen(
                    trashItems = trashItems,
                    language = language,
                    onRestoreFile = { viewModel.restoreFromTrash(it.file) },
                    onPermanentDelete = { viewModel.permanentDeleteTrash(it.file) },
                    onEmptyTrash = { viewModel.showDialog(ActiveDialog.EmptyTrashConfirmation) }
                  )
                }
              }

              // Speed Dial FAB ("منو شیکو خفن") - visible on Files, Home and Starred tabs
              if (currentTab == NavigationTab.FILES || currentTab == NavigationTab.HOME) {
                SpeedDialFab(
                  language = language,
                  isSelectionActive = selectedItems.isNotEmpty(),
                  onNewFolder = { viewModel.showDialog(ActiveDialog.NewFolder(currentDir)) },
                  onNewFile = { viewModel.showDialog(ActiveDialog.NewFile(currentDir)) },
                  onImportFile = { filePickerLauncher.launch(arrayOf("*/*")) },
                  onCompressSelected = {
                    val selectedList = items.filter { selectedItems.contains(it.file) }
                    viewModel.showDialog(ActiveDialog.CompressToZip(selectedList))
                  },
                  onQuickClean = { viewModel.cleanStorageCache() },
                  modifier = Modifier.fillMaxSize()
                )
              }

              // File Dialogs
              FileOperationDialogs(
                activeDialog = activeDialog,
                language = language,
                onDismiss = { viewModel.dismissDialog() },
                onCreateFolder = { viewModel.createFolder(it) },
                onCreateFile = { name, content -> viewModel.createFile(name, content) },
                onRename = { viewModel.renameFile((activeDialog as ActiveDialog.Rename).file, it) },
                onDeleteConfirm = { permanent ->
                  val targets = (activeDialog as ActiveDialog.DeleteConfirmation).items
                  viewModel.deleteItems(targets, permanent)
                },
                onCompressConfirm = { zipName -> viewModel.compressSelectedToZip(zipName) },
                onEmptyTrashConfirm = { viewModel.emptyTrash() }
              )

              // Active Previews
              when (val preview = activePreview) {
                is ActivePreview.TextPreview -> {
                  TextEditorPreviewDialog(
                    file = preview.file,
                    initialContent = preview.content,
                    language = language,
                    onSave = { viewModel.saveTextFile(preview.file, it) },
                    onDismiss = { viewModel.dismissPreview() }
                  )
                }
                is ActivePreview.ImagePreview -> {
                  ImageViewerDialog(
                    file = preview.file,
                    language = language,
                    onShare = { shareFile(preview.file) },
                    onDismiss = { viewModel.dismissPreview() }
                  )
                }
                is ActivePreview.AudioPreview -> {
                  AudioPlayerPreviewDialog(
                    file = preview.file,
                    fileName = preview.fileName,
                    sizeBytes = preview.sizeBytes,
                    language = language,
                    onDismiss = { viewModel.dismissPreview() }
                  )
                }
                null -> Unit
              }
            }
          }
        }
      }
    }
  }
}
