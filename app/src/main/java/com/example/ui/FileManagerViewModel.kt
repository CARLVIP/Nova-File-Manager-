package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.FileManagerRepository
import com.example.util.FileFormatters
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class FileManagerViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = FileManagerRepository(application.applicationContext)

  private val _currentDirectory = MutableStateFlow<File>(repository.rootDirectory)
  val currentDirectory: StateFlow<File> = _currentDirectory.asStateFlow()

  private val _breadcrumbs = MutableStateFlow<List<File>>(listOf(repository.rootDirectory))
  val breadcrumbs: StateFlow<List<File>> = _breadcrumbs.asStateFlow()

  private val _items = MutableStateFlow<List<FileItem>>(emptyList())
  val items: StateFlow<List<FileItem>> = _items.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _categoryFilter = MutableStateFlow<FileCategory?>(null)
  val categoryFilter: StateFlow<FileCategory?> = _categoryFilter.asStateFlow()

  private val _sortOption = MutableStateFlow(SortOption.NAME)
  val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

  private val _sortAscending = MutableStateFlow(true)
  val sortAscending: StateFlow<Boolean> = _sortAscending.asStateFlow()

  private val _viewMode = MutableStateFlow(ViewMode.GRID)
  val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

  private val _selectedItems = MutableStateFlow<Set<File>>(emptySet())
  val selectedItems: StateFlow<Set<File>> = _selectedItems.asStateFlow()

  val isMultiSelectMode: StateFlow<Boolean> = _selectedItems.map { it.isNotEmpty() }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  private val _currentTab = MutableStateFlow(NavigationTab.HOME)
  val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

  private val _clipboard = MutableStateFlow<ClipboardState?>(null)
  val clipboard: StateFlow<ClipboardState?> = _clipboard.asStateFlow()

  private val _storageStats = MutableStateFlow<StorageStats?>(null)
  val storageStats: StateFlow<StorageStats?> = _storageStats.asStateFlow()

  private val _activePreview = MutableStateFlow<ActivePreview?>(null)
  val activePreview: StateFlow<ActivePreview?> = _activePreview.asStateFlow()

  private val _activeDialog = MutableStateFlow<ActiveDialog?>(null)
  val activeDialog: StateFlow<ActiveDialog?> = _activeDialog.asStateFlow()

  private val _appLanguage = MutableStateFlow(AppLanguage.FA) // Default Persian as requested, toggleable
  val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

  private val _starredItems = MutableStateFlow<List<FileItem>>(emptyList())
  val starredItems: StateFlow<List<FileItem>> = _starredItems.asStateFlow()

  private val _trashItems = MutableStateFlow<List<FileItem>>(emptyList())
  val trashItems: StateFlow<List<FileItem>> = _trashItems.asStateFlow()

  private val _recentItems = MutableStateFlow<List<FileItem>>(emptyList())
  val recentItems: StateFlow<List<FileItem>> = _recentItems.asStateFlow()

  private val _largeFiles = MutableStateFlow<List<FileItem>>(emptyList())
  val largeFiles: StateFlow<List<FileItem>> = _largeFiles.asStateFlow()

  private val _emptyFolders = MutableStateFlow<List<FileItem>>(emptyList())
  val emptyFolders: StateFlow<List<FileItem>> = _emptyFolders.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  init {
    viewModelScope.launch {
      repository.initializeSampleFilesIfEmpty()
      refreshAll()
    }
  }

  fun refreshAll() {
    viewModelScope.launch {
      _isLoading.value = true
      loadDirectoryFiles()
      loadStorageStats()
      loadStarredFiles()
      loadTrashFiles()
      loadRecentFiles()
      loadCleanerData()
      _isLoading.value = false
    }
  }

  private suspend fun loadDirectoryFiles() {
    val dir = _currentDirectory.value
    _items.value = repository.listFiles(
      directory = dir,
      searchQuery = _searchQuery.value,
      categoryFilter = _categoryFilter.value,
      sortOption = _sortOption.value,
      sortAscending = _sortAscending.value
    )
    updateBreadcrumbs(dir)
  }

  private fun updateBreadcrumbs(dir: File) {
    val list = mutableListOf<File>()
    var curr: File? = dir
    val root = repository.rootDirectory
    while (curr != null && curr.absolutePath.startsWith(root.absolutePath)) {
      list.add(0, curr)
      if (curr.absolutePath == root.absolutePath) break
      curr = curr.parentFile
    }
    if (list.isEmpty()) list.add(root)
    _breadcrumbs.value = list
  }

  private suspend fun loadStorageStats() {
    _storageStats.value = repository.calculateStorageStats()
  }

  private suspend fun loadStarredFiles() {
    _starredItems.value = repository.getStarredFiles()
  }

  private suspend fun loadTrashFiles() {
    _trashItems.value = repository.getTrashFiles()
  }

  private suspend fun loadRecentFiles() {
    _recentItems.value = repository.getRecentFiles()
  }

  private suspend fun loadCleanerData() {
    _largeFiles.value = repository.findLargeFiles()
    _emptyFolders.value = repository.findEmptyFolders()
  }

  fun navigateTo(dir: File) {
    if (dir.isDirectory && dir.exists()) {
      _currentDirectory.value = dir
      _selectedItems.value = emptySet()
      viewModelScope.launch {
        loadDirectoryFiles()
      }
    }
  }

  fun navigateUp(): Boolean {
    val current = _currentDirectory.value
    val root = repository.rootDirectory
    if (current.absolutePath != root.absolutePath && current.parentFile != null && current.parentFile!!.exists()) {
      navigateTo(current.parentFile!!)
      return true
    }
    return false
  }

  fun selectTab(tab: NavigationTab) {
    _currentTab.value = tab
    _selectedItems.value = emptySet()
    refreshAll()
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
    viewModelScope.launch {
      loadDirectoryFiles()
    }
  }

  fun setCategoryFilter(category: FileCategory?) {
    _categoryFilter.value = category
    viewModelScope.launch {
      loadDirectoryFiles()
    }
  }

  fun setSort(option: SortOption) {
    if (_sortOption.value == option) {
      _sortAscending.value = !_sortAscending.value
    } else {
      _sortOption.value = option
      _sortAscending.value = true
    }
    viewModelScope.launch {
      loadDirectoryFiles()
    }
  }

  fun toggleViewMode() {
    _viewMode.value = if (_viewMode.value == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID
  }

  fun toggleLanguage() {
    _appLanguage.value = if (_appLanguage.value == AppLanguage.FA) AppLanguage.EN else AppLanguage.FA
  }

  fun toggleSelection(file: File) {
    val current = _selectedItems.value.toMutableSet()
    if (current.contains(file)) {
      current.remove(file)
    } else {
      current.add(file)
    }
    _selectedItems.value = current
  }

  fun selectAll() {
    val allFiles = _items.value.map { it.file }.toSet()
    _selectedItems.value = allFiles
  }

  fun clearSelection() {
    _selectedItems.value = emptySet()
  }

  fun copySelected() {
    val files = _selectedItems.value.toList()
    if (files.isNotEmpty()) {
      _clipboard.value = ClipboardState(files, ClipboardOperation.COPY)
      _selectedItems.value = emptySet()
      showSnackbar(if (_appLanguage.value == AppLanguage.FA) "${files.size} مورد کپی شد" else "${files.size} items copied")
    }
  }

  fun cutSelected() {
    val files = _selectedItems.value.toList()
    if (files.isNotEmpty()) {
      _clipboard.value = ClipboardState(files, ClipboardOperation.CUT)
      _selectedItems.value = emptySet()
      showSnackbar(if (_appLanguage.value == AppLanguage.FA) "${files.size} مورد برای انتقال انتخاب شد" else "${files.size} items ready to move")
    }
  }

  fun pasteClipboard() {
    val clip = _clipboard.value ?: return
    val targetDir = _currentDirectory.value
    viewModelScope.launch {
      var count = 0
      for (file in clip.sourceFiles) {
        val result = if (clip.operation == ClipboardOperation.COPY) {
          repository.copyFileOrFolder(file, targetDir)
        } else {
          repository.moveFileOrFolder(file, targetDir)
        }
        if (result.isSuccess) count++
      }
      if (clip.operation == ClipboardOperation.CUT) {
        _clipboard.value = null
      }
      showSnackbar(
        if (_appLanguage.value == AppLanguage.FA) "$count مورد جای‌گذاری شد" else "$count items pasted successfully"
      )
      refreshAll()
    }
  }

  fun clearClipboard() {
    _clipboard.value = null
  }

  fun createFolder(name: String) {
    viewModelScope.launch {
      val res = repository.createFolder(_currentDirectory.value, name)
      if (res.isSuccess) {
        showSnackbar(if (_appLanguage.value == AppLanguage.FA) "پوشه جدید ساخته شد" else "Folder created")
        refreshAll()
      } else {
        showSnackbar(res.exceptionOrNull()?.message ?: "Error creating folder")
      }
      dismissDialog()
    }
  }

  fun createFile(name: String, content: String = "") {
    viewModelScope.launch {
      val res = repository.createFile(_currentDirectory.value, name, content)
      if (res.isSuccess) {
        showSnackbar(if (_appLanguage.value == AppLanguage.FA) "فایل جدید ایجاد شد" else "File created")
        refreshAll()
      } else {
        showSnackbar(res.exceptionOrNull()?.message ?: "Error creating file")
      }
      dismissDialog()
    }
  }

  fun renameFile(file: File, newName: String) {
    viewModelScope.launch {
      val res = repository.rename(file, newName)
      if (res.isSuccess) {
        showSnackbar(if (_appLanguage.value == AppLanguage.FA) "تغییر نام با موفقیت انجام شد" else "Renamed successfully")
        refreshAll()
      } else {
        showSnackbar(res.exceptionOrNull()?.message ?: "Error renaming")
      }
      dismissDialog()
    }
  }

  fun deleteItems(items: List<FileItem>, permanent: Boolean) {
    viewModelScope.launch {
      var successCount = 0
      for (item in items) {
        val res = if (permanent) {
          repository.permanentDelete(item.file)
        } else {
          repository.deleteToTrash(item.file)
        }
        if (res.isSuccess) successCount++
      }
      _selectedItems.value = emptySet()
      showSnackbar(
        if (_appLanguage.value == AppLanguage.FA) "$successCount مورد حذف شد" else "$successCount items deleted"
      )
      refreshAll()
      dismissDialog()
    }
  }

  fun restoreFromTrash(file: File) {
    viewModelScope.launch {
      val res = repository.restoreFromTrash(file)
      if (res.isSuccess) {
        showSnackbar(if (_appLanguage.value == AppLanguage.FA) "فایل بازیابی شد" else "File restored")
        refreshAll()
      } else {
        showSnackbar(res.exceptionOrNull()?.message ?: "Error restoring")
      }
    }
  }

  fun permanentDeleteTrash(file: File) {
    viewModelScope.launch {
      val res = repository.permanentDelete(file)
      if (res.isSuccess) {
        showSnackbar(if (_appLanguage.value == AppLanguage.FA) "فایل برای همیشه حذف شد" else "File permanently deleted")
        refreshAll()
      }
    }
  }

  fun emptyTrash() {
    viewModelScope.launch {
      val res = repository.emptyTrash()
      if (res.isSuccess) {
        showSnackbar(if (_appLanguage.value == AppLanguage.FA) "سطل زباله خالی شد" else "Trash emptied")
        refreshAll()
      }
      dismissDialog()
    }
  }

  fun toggleStar(file: File) {
    viewModelScope.launch {
      val starred = repository.toggleStar(file)
      showSnackbar(
        if (starred) {
          if (_appLanguage.value == AppLanguage.FA) "به نشان‌شده‌ها اضافه شد" else "Added to starred"
        } else {
          if (_appLanguage.value == AppLanguage.FA) "از نشان‌شده‌ها حذف شد" else "Removed from starred"
        }
      )
      loadDirectoryFiles()
      loadStarredFiles()
    }
  }

  fun compressSelectedToZip(zipName: String) {
    val selected = _selectedItems.value.toList()
    if (selected.isEmpty()) return
    viewModelScope.launch {
      val res = repository.zipFiles(selected, zipName, _currentDirectory.value)
      if (res.isSuccess) {
        showSnackbar(if (_appLanguage.value == AppLanguage.FA) "فایل فشرده ایجاد شد" else "ZIP Archive created")
        _selectedItems.value = emptySet()
        refreshAll()
      } else {
        showSnackbar(res.exceptionOrNull()?.message ?: "Error compressing")
      }
      dismissDialog()
    }
  }

  fun extractZip(file: File) {
    viewModelScope.launch {
      val res = repository.unzipFile(file, _currentDirectory.value)
      if (res.isSuccess) {
        showSnackbar(if (_appLanguage.value == AppLanguage.FA) "فایل استخراج شد" else "Archive extracted")
        refreshAll()
      } else {
        showSnackbar(res.exceptionOrNull()?.message ?: "Error extracting")
      }
    }
  }

  fun openFile(item: FileItem) {
    if (item.isDirectory) {
      navigateTo(item.file)
      return
    }

    if (FileFormatters.isTextEditable(item.file)) {
      try {
        val content = item.file.readText()
        _activePreview.value = ActivePreview.TextPreview(item.file, content, isEditable = true)
      } catch (e: Exception) {
        showSnackbar("Could not read text file: ${e.message}")
      }
    } else if (FileFormatters.isImage(item.file)) {
      _activePreview.value = ActivePreview.ImagePreview(item.file)
    } else if (FileFormatters.isAudio(item.file)) {
      _activePreview.value = ActivePreview.AudioPreview(item.file, item.name, item.sizeBytes)
    } else if (FileFormatters.isArchive(item.file)) {
      extractZip(item.file)
    } else {
      showDialog(ActiveDialog.Details(item))
    }
  }

  fun saveTextFile(file: File, newContent: String) {
    viewModelScope.launch {
      try {
        file.writeText(newContent)
        showSnackbar(if (_appLanguage.value == AppLanguage.FA) "تغییرات ذخیره شد" else "Changes saved")
        refreshAll()
        dismissPreview()
      } catch (e: Exception) {
        showSnackbar("Failed to save: ${e.message}")
      }
    }
  }

  fun importExternalFile(fileName: String, inputStream: InputStream) {
    viewModelScope.launch {
      try {
        val dest = File(_currentDirectory.value, fileName)
        dest.outputStream().use { out ->
          inputStream.copyTo(out)
        }
        showSnackbar(if (_appLanguage.value == AppLanguage.FA) "فایل وارد شد" else "File imported")
        refreshAll()
      } catch (e: Exception) {
        showSnackbar("Failed to import file: ${e.message}")
      }
    }
  }

  fun cleanStorageCache() {
    viewModelScope.launch {
      val bytesCleaned = repository.cleanCache()
      showSnackbar(
        if (_appLanguage.value == AppLanguage.FA)
          "حافظه موقت پاک‌سازی شد (${FileFormatters.formatFileSize(bytesCleaned)})"
        else
          "Cache cleaned (${FileFormatters.formatFileSize(bytesCleaned)})"
      )
      refreshAll()
    }
  }

  fun showDialog(dialog: ActiveDialog) {
    _activeDialog.value = dialog
  }

  fun dismissDialog() {
    _activeDialog.value = null
  }

  fun dismissPreview() {
    _activePreview.value = null
  }

  fun showSnackbar(message: String) {
    _snackbarMessage.value = message
  }

  fun clearSnackbar() {
    _snackbarMessage.value = null
  }
}
